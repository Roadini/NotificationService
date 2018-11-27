package websocket.notification;

import java.util.Properties;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;




/*const url = "ws://localhost:8040/websocket";*/

public class Main 
{
	public static void main(String[] args) throws Exception {
		new Main();
	}

	public Main() throws Exception {

		

		
		Server server = new Server(8040);

		//ResourceHandler rh = new ResourceHandler();
		/*rh.setResourceBase(this.getClass().getClassLoader().getResource("html")
				.toExternalForm());
*/
        
		XmppWebSocketServlet wss = new XmppWebSocketServlet();
		ServletHolder sh = new ServletHolder(wss);
		ServletContextHandler sch = new ServletContextHandler();
		sch.addServlet(sh, "/websocket/*");
		//HandlerList hl = new HandlerList();
		//hl.setHandlers(new Handler[] { rh, sch });
		//server.setHandler(hl);
		server.setHandler(sch);
		server.start();


		Handler handler = server.getHandler();
		if (handler instanceof WebAppContext) {
			System.out.println("found wac");
			WebAppContext rctx = (WebAppContext) handler;
			rctx.getSessionHandler().getSessionManager()
					.setMaxInactiveInterval(60);
		}

	}
}
