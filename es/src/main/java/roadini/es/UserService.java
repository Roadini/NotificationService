package roadini.es;

import java.io.IOException;
import java.io.StringReader;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class UserService {
	private String server;
	private int port;
	private String pubsubserver;
	private ConnectionConfiguration config;

	private XMPPConnection connection;
	private ConfigureForm form;
	private PubSubManager manager;
	private LeafNode myLeafNode;
	
public UserService(String server, int port, String pubsubserver) {
	this.server = server;
	this.port = port;
	this.pubsubserver = pubsubserver;
}
public void init() throws XMPPException{
	
	config = new ConnectionConfiguration(server,port);
	config.setSASLAuthenticationEnabled(true);
	connection = new XMPPConnection(config); 
	connection.connect();
	System.out.println("Connected");
	manager= new PubSubManager(connection, pubsubserver);

}

public void performLogin(String username, String password) throws XMPPException {
	if (connection!=null && connection.isConnected()) {
	    connection.login(username, password);
		System.out.println(username + " logged in with success");
	
}
}
public void performLogout() throws XMPPException {
	connection.disconnect();
}

public void getLeafNode(String LeafNodeId) throws XMPPException{
	myLeafNode =  (LeafNode) manager.getNode(LeafNodeId);
	System.out.println("getting..." + LeafNodeId);
}

public void createLeafNode(String LeafNodeId) throws XMPPException {
	form = new ConfigureForm(FormType.submit);
	form.setPersistentItems(true);
	form.setMaxItems(20);
	form.setDeliverPayloads(true);
	form.setAccessModel(AccessModel.open);
	form.setPublishModel(PublishModel.open);
	myLeafNode =  (LeafNode) manager.createNode(LeafNodeId,form);
	myLeafNode.addItemEventListener(new ItemEventCoordinator());
	System.out.println(LeafNodeId+" node created");
}
public void publishItem(String content) throws XMPPException {
	//System.out.println("Publishing Item");
	String from= connection.getAccountManager().getAccountAttribute("name");
	// SimplePayload payload = new SimplePayload("message", "pubsub:test:message", "<message xmlns='pubsub:test:message'><body>" + content + "</body><from>"+ "Luis Silva"+"</from></message>");
	SimplePayload payload = new SimplePayload("pubsub", server,"<message>" + content +"<from>"+ from +"</from>"+ "</message>" );  
	Item payloadItem = new PayloadItem(null, payload);
	myLeafNode.publish(payloadItem);
}

public void subscribeNode(String JID) throws XMPPException {
	myLeafNode.addItemEventListener(new ItemEventCoordinator());
	myLeafNode.subscribe(JID);
	System.out.println(JID + " subscribed to " + myLeafNode.getId());
}

class ItemEventCoordinator  implements ItemEventListener {
	@Override
	public void handlePublishedItems(ItemPublishEvent items) 
	{
		System.out.println("Notificação");
		/*System.out.println(items.getItems().toString());
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();

	        xpp.setInput( new StringReader ( "<message xmlns=\"http://jabber.org/protocol/pubsub\">fdgfdgdf<from>Luis Silva</from></message>"));
	        int eventType = xpp.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	 if(eventType == XmlPullParser.START_DOCUMENT) {
	                 //System.out.println("Start document");
	             } else if(eventType == XmlPullParser.END_DOCUMENT) {
	                 //System.out.println("End document");
	             } else if(eventType == XmlPullParser.START_TAG) {
	                 //System.out.println("Start tag "+xpp.getName());
	             } else if(eventType == XmlPullParser.END_TAG) {
	                 //System.out.println("End tag "+xpp.getName());
	             } else if(eventType == XmlPullParser.TEXT) {
	                 System.out.println("Text "+xpp.getText());
	             }
	             eventType = xpp.next();

	        }

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/	
		System.out.println("Item count: " + items.getItems().size());
		//System.out.println(items);
		System.out.println(items.getItems());
	}
}
}