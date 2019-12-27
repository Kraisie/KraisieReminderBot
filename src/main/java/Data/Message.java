package Data;

public class Message {

	private String command;
	private int index;
	private String message;

	public Message() {
		this.command = "";
		this.index = -1;
		this.message = "";
	}

	public Message(String command, int index, String message) {
		this.command = command;
		this.index = index;
		this.message = message;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
