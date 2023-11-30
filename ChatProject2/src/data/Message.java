package data;

import java.io.Serializable;

public class Message implements Serializable{

	public static final long serialVersionUID = 181224685112L;
	
	private String writer;
	private String message;
	private int chatId;
	
	public Message() {
	}
	public Message(String writer, String message, int chatId) {
		super();
		this.writer = writer;
		this.message = message;
		this.chatId = chatId;
	}
	
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getChatId() {
		return chatId;
	}
	public void setChatId(int chatId) {
		this.chatId = chatId;
	}
	
	public String toText() {
		return "["+writer+"] : "+message;
	}
	
	@Override
	public String toString() {
		return "Message [writer=" + writer + ", message=" + message + ", chatId=" + chatId + "]";
	}
	
	
}
