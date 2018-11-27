package roadini.notifications.util;

import org.codehaus.jackson.annotate.JsonProperty;

public class Message {
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("Data")
	private Data data;
	
	public Message(String type, Data data) {
		super();
		this.type = type;
		this.data = data;
	}

	public Message(String type) {
		super();
		this.type = type;
	}

	public Message() {
		
	}

	public String getType() {
		return type;
	}

	public Data getData() {
		return data;
	}

}
