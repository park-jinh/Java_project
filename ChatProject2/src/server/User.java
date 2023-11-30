package server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{
	
	public static final long serialVersionUID = 18123541235112L;
	
	private String id;							// 유저 아이디
	private String password;				// 유저 비밀번호
	private List<Integer> chatList;		// 유저가 가진 채팅방 리스트
	
	public User() {
	}

	public User(String id, String password, List<Integer> chatList) {
		this.id = id;
		this.password = password;
		this.chatList = chatList;
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

	public List<Integer> getChatList() {
		return chatList;
	}

	public void setChatList(List<Integer> chatList) {
		this.chatList = chatList;
	}
	
	public void addChatId(int chatId) {
		if(this.chatList == null) {
			this.chatList = new ArrayList<Integer>();
		}
		this.chatList.add(chatId);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", password=" + password + ", chatList=" + chatList;
	}
	
	
}
