package websocket.notification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
//import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
//import org.jxmpp.jid.DomainBareJid;
//import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;

//import roadini.notifications.XmppWebSocket.ItemEventCoordinator;
import websocket.notification.util.Data;
import websocket.notification.util.Message;


public class XmppWebSocket implements WebSocket.OnTextMessage, MessageListener{

	       protected Connection connection;
	       protected XMPPConnection talk;
	       private PubSubManager manager;
	       private LeafNode ownernode;
	       private ConfigureForm form;
	       private String userToken;

	       public void onOpen(Connection arg0) {
		       this.connection = arg0;
		       System.out.println("Open v2");
	       }

	       public void onClose(int arg0, String arg1) {
		       talk.disconnect();
	       }

	       public void onMessage(String arg0) {
		       ObjectMapper mapper = new ObjectMapper();
		       try {

			       System.out.println("Pim");
			       URL url = new URL("http://auth_api:3000/auth/v1/getselfuser");
			       HttpURLConnection con = (HttpURLConnection) url.openConnection();
			       con.setRequestMethod("POST");

			       Message recMsg = mapper.readValue(arg0, Message.class);

			       if (recMsg.getType().equals("login")) {
				       System.out.println("PAM");

				       SmackConfiguration.setPacketReplyTimeout(5000);
				       ConnectionConfiguration config = new ConnectionConfiguration(
						       recMsg.getData().getServer(), 5222, "pubsub.myserverxmpp");
				       config.setSASLAuthenticationEnabled(true);

				       talk = new XMPPConnection(config);
				       talk.connect();
				       System.out.println("PUM");


				       // Get user information from token
				       this.userToken = recMsg.getData().getToken();

				       System.out.println(this.userToken);

				       System.out.println();

				       con.setRequestProperty("Cookie", "jwt="+this.userToken);

				       System.out.println(con.getResponseCode());

				       BufferedReader in = new BufferedReader(
						       new InputStreamReader(con.getInputStream()));
				       String inputLine;
				       StringBuffer content = new StringBuffer();
				       while ((inputLine = in.readLine()) != null) {
					       content.append(inputLine);
				       }
				       in.close();

				       System.out.println(content.toString());

				       JsonElement jelement = new JsonParser().parse(content.toString());
				       JsonObject  jobject = (JsonObject) jelement.getAsJsonArray().get(0);
				       String id = jobject.get("id").getAsString();
				       String pass = jobject.get("id").getAsString() + jobject.get("email").getAsString();


				       talk.login(id,pass);

				       manager= new PubSubManager(talk, "pubsub.myserverxmpp");

				       try {
					       ownernode = (LeafNode) manager.getNode(("node_p"+id));

					       for (int i = 0; i < ownernode.getItems().size(); i++) {
						       ArrayList<String> frompayload = parseXml(ownernode.getItems().get(i).toString());
						       System.out.println(frompayload.get(1) + " publicou " +frompayload.get(0)+" no teu perfil");
						       Message sndMsg = new Message("publish", new Data(frompayload.get(1),frompayload.get(0)));
						       try {
			                       connection.sendMessage(mapper.writeValueAsString(sndMsg));
			                       ownernode.deleteItem(ownernode.getItems().get(i).getId());
			                   } catch (IOException e) {
			                       e.printStackTrace();
			                   }
					       }

				       } catch (XMPPException e) {
					       /*configure node properties*/
					       form = new ConfigureForm(FormType.submit);
					       form.setPersistentItems(true);
					       form.setMaxItems(50);
					       form.setDeliverPayloads(true);
					       form.setAccessModel(AccessModel.open);
					       form.setPublishModel(PublishModel.open);

					       ownernode = (LeafNode) manager.createNode("node_p"+id,form);
					       ownernode.subscribe(id+"@myserverxmpp");
				       }

				       ownernode.addItemEventListener(new ItemEventCoordinator());

				       System.out.println("Login Successful");

			       } else if (recMsg.getType().equals("publish")) {

				       LeafNode nodeToPublish =(LeafNode) manager.getNode("node_p"+recMsg.getData().getTo());

				       String Xmltosend="<message><body>" + recMsg.getData().getText() +"</body><from>"+ talk.getAccountManager().getAccountAttribute("name") +"</from>"+ "</message>";

				       SimplePayload payload = new SimplePayload("message", "pubusb:node_p"+recMsg.getData().getTo(),Xmltosend);
				       Item payloadItem = new PayloadItem(null, payload);
				       nodeToPublish.publish(payloadItem);


				       System.out.println("Publish Successful");

			       }

		       } catch (XMPPException | IOException e) {
			       e.printStackTrace();
		       }

	       }

	       class ItemEventCoordinator  implements ItemEventListener {

		       public void handlePublishedItems(ItemPublishEvent items) {
			       ObjectMapper mapper = new ObjectMapper();


			       System.out.println("Notificação");
			       System.out.println("Item count: " + items.getItems().size());

			       ArrayList<String> frompayload = parseXml(items.getItems().get(0).toString());
                   Message sndMsg = new Message("publish", new Data(frompayload.get(1),frompayload.get(0)));

			       System.out.println(parseXml(items.getItems().get(0).toString()));
			       System.out.println(frompayload.get(1) + " publicou " +frompayload.get(0)+" no teu perfil");
                   try {
                       connection.sendMessage(mapper.writeValueAsString(sndMsg));
                       ownernode.deleteItem(ownernode.getItems().get(0).getId());
                   } catch (IOException e) {
                       e.printStackTrace();
                   } catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       }

	       }

	       public ArrayList<String> parseXml(String message) {

		       ArrayList<String> content = new ArrayList<String>();

		       String body = message.substring(message.indexOf("<body>")+6, message.indexOf("</body>"));
		       content.add(body);
		       String from = message.substring(message.indexOf("<from>")+6, message.indexOf("</from>"));
		       content.add(from);
		       return content;

	       }
	       public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
		       // TODO Auto-generated method stub

	       }


}
