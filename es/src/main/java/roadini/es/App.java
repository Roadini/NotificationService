package roadini.es;
import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Spark;

public class App 
{
	
	public static void main(String[] args) {
		
		get("/hello", (request, response) -> {
			return "Hello";
		});
		
		//http://localhost:4567/createtopic
		post("/createtopic", (request, response) -> {
			response.type("application/json");
			UserService user= new UserService("127.0.0.1",5222, "pubsub.myserverxmpp");
			JsonParser parser= new JsonParser();
			JsonObject json = (JsonObject) parser.parse(request.body());
			String username = json.get("username").getAsString();
			String pw = json.get("pw").getAsString();
			user.init();
			user.performLogin(username, pw);
			user.createLeafNode("node"+username);
			user.performLogout();
			return "SUCCESS";
		});
		//http://localhost:4567/publish
		post("/publish", (request, response) -> {
			response.type("application/json");
			UserService user= new UserService("127.0.0.1",5222, "pubsub.myserverxmpp");
			JsonParser parser= new JsonParser();
			JsonObject json = (JsonObject) parser.parse(request.body());
			String username = json.get("username").getAsString();
			String pw = json.get("pw").getAsString();
			String TopicID = json.get("topicID").getAsString();
			String content = json.get("message").getAsString();
			user.init();
			user.performLogin(username, pw);
			user.getLeafNode("node"+TopicID);
			user.publishItem(content);
			user.performLogout();
		    return "SUCCESS";
		});
		//http://localhost:4567/subscribe
		post("/subscribe", (request, response) -> {
			response.type("application/json");
			UserService user= new UserService("127.0.0.1",5222, "pubsub.myserverxmpp");
			JsonParser parser= new JsonParser();
			JsonObject json = (JsonObject) parser.parse(request.body());
			String username = json.get("username").getAsString();
			String pw = json.get("pw").getAsString();
			String TopicID = json.get("topicID").getAsString();
			user.init();
			user.performLogin(username, pw);
			user.getLeafNode("node"+TopicID);
			user.subscribeNode(username+"@myserverxmpp");
		    return "SUCCESS";
		});
		
		
    }
}
