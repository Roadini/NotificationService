package websocket.notification;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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


public class XmppWebSocket implements WebSocket.OnTextMessage,
MessageListener{
	protected Connection connection;
	protected XMPPConnection talk;
	private PubSubManager manager;
	private LeafNode ownernode;
	private ConfigureForm form;

	

	public void onOpen(Connection arg0) {
		this.connection = arg0;
		System.out.println("Open");
	}

	public void onClose(int arg0, String arg1) {
		talk.disconnect();
		
	}

	public void onMessage(String arg0) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Message recMsg = mapper.readValue(arg0, Message.class);
			
			if (recMsg.getType().equals("login")) {

				SmackConfiguration.setPacketReplyTimeout(5000);
				ConnectionConfiguration config = new ConnectionConfiguration(
						recMsg.getData().getServer(), 5222, "pubsub.myserverxmpp");
				config.setSASLAuthenticationEnabled(true);

				talk = new XMPPConnection(config);
				talk.connect();
				talk.login(recMsg.getData().getUserName(), recMsg.getData()
						.getPassword());
				manager= new PubSubManager(talk, "pubsub.myserverxmpp");
				
				try {
					ownernode = (LeafNode) manager.getNode(("node"+recMsg.getData().getUserName()));
					
					
					
					
					
					for (int i = 0; i < ownernode.getItems().size(); i++) {
						ArrayList<String> frompayload = parseXml(ownernode.getItems().get(i).toString());
						System.out.println(frompayload.get(1) + " publicou " +frompayload.get(0)+" no teu perfil");
						//System.out.println(ownernode.getItems().get(i));
					}
					//System.out.println(ownernode.getItems().get(1));
					//System.out.println(ownernode.getItems().size());
				} catch (XMPPException e) {
					/*configure node properties*/
					form = new ConfigureForm(FormType.submit);
					form.setPersistentItems(true);
					form.setMaxItems(50);
					form.setDeliverPayloads(true);
					form.setAccessModel(AccessModel.open);
					form.setPublishModel(PublishModel.open);
					
					ownernode = (LeafNode) manager.createNode("node"+recMsg.getData().getUserName(),form);
					ownernode.subscribe(recMsg.getData().getUserName()+"@myserverxmpp");
				}
				
				ownernode.addItemEventListener(new ItemEventCoordinator());
				System.out.println("PUMBAAA");
			
			} else if (recMsg.getType().equals("publish")) {
				
				recMsg.getData();
				LeafNode nodeToPublish =(LeafNode) manager.getNode("node"+recMsg.getData().getTo());
				String Xmltosend="<message><body>" + recMsg.getData().getText() +"</body><from>"+ talk.getAccountManager().getAccountAttribute("name") +"</from>"+ "</message>";
				
				SimplePayload payload = new SimplePayload("message", "pubusb:node"+recMsg.getData().getTo(),Xmltosend);  
				Item payloadItem = new PayloadItem(null, payload);
				nodeToPublish.publish(payloadItem);
				
				
				System.out.println("CONAAA");

			}

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		

	}

	/*@Override
	public void processMessage(Chat chat,
			org.jivesoftware.smack.packet.Message messgage) {
		if (messgage.getBody() == null) {
			return;
		}
		StringTokenizer st = new StringTokenizer(messgage.getFrom(), "/");
		Message sndMsg = new Message("publish", new Data(st.nextToken(), messgage.getBody(),""));

		ObjectMapper mapper = new ObjectMapper();
		try {
			connection.sendMessage(mapper.writeValueAsString(sndMsg));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}*/
	
class ItemEventCoordinator  implements ItemEventListener {
	public void handlePublishedItems(ItemPublishEvent items) {
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println("Notificação");
		System.out.println("Item count: " + items.getItems().size());
		
		ArrayList<String> frompayload = parseXml(items.getItems().get(0).toString());
		
		System.out.println(parseXml(items.getItems().get(0).toString()));
		System.out.println(frompayload.get(1) + " publicou " +frompayload.get(0)+" no teu perfil");
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

	public void processMessage(org.jivesoftware.smack.packet.Message message) {
		// TODO Auto-generated method stub
		
	}

	public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
		// TODO Auto-generated method stub
		
	}


}
