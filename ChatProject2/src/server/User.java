package server;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{
	
	public static final long serialVersionUID = 18123541235112L;
	
	private String id;
	private String password;
	private Socket socket;
	private List<Integer> chatList;
	boolean onlineYN;
	
	public User() {
	}

	public User(String id, String password, Socket socket, List<Integer> chatList, boolean onlineYN) {
		super();
		this.id = id;
		this.password = password;
		this.socket = socket;
		this.chatList = chatList;
		this.onlineYN = onlineYN;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public List<Integer> getChatList() {
		return chatList;
	}

	public void setChatList(List<Integer> chatList) {
		this.chatList = chatList;
	}

	public boolean isOnlineYN() {
		return onlineYN;
	}

	public void setOnlineYN(boolean onlineYN) {
		this.onlineYN = onlineYN;
	}
	
	public void addChatId(int chatId) {
		if(this.chatList == null) {
			this.chatList = new ArrayList<Integer>();
		}
		this.chatList.add(chatId);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", password=" + password + ", socket=" + socket + ", chatList=" + chatList
				+ ", onlineYN=" + onlineYN + "]";
	}
	
	
}
