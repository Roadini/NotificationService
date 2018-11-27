package websocket.notification.util;

import org.codehaus.jackson.annotate.JsonProperty;

public class Data {
	@JsonProperty("Server")
	private String server;
	
	@JsonProperty("UserName")
	private String userName;
	
	@JsonProperty("Password")
	private String password;
	
	@JsonProperty("From")
	private String from;
	
	@JsonProperty("To")
	private String to;
	
	@JsonProperty("Text")
	private String text;

	public Data(String from,String to, String text) {
		super();
		this.from = from;
		this.to= to;
		this.text = text;
	}

	public Data() {
		super();
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFrom() {
		return this.from;
	}
	
	public String getTo() {
		return this.to;
	}

	public String getText() {
		return text;
	}
}
