package websocket.notification.util;

import org.codehaus.jackson.annotate.JsonProperty;

public class Data {
	@JsonProperty("server")
	private String server;
	
	@JsonProperty("token")
	private String token;

	@JsonProperty("to")
	private int to;
	
	@JsonProperty("text")
	private String text;

	public Data() {
		super();
	}

	public Data(int to, String text) {
		super();
		this.to= to;
		this.text = text;
	}

	public Data(String token, String server){
		super();
		this.token = token;
		this.server = server;
	}

	public String getServer() {
		return this.server;
	}

	public String getToken() { return this.token; }

	public int getTo() {
		return this.to;
	}

	public String getText() {
		return text;
	}
}
