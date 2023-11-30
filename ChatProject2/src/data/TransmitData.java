package data;

import java.io.Serializable;
import java.util.List;

public class TransmitData implements Serializable {

	public static final long serialVersionUID = 71552368142L;
	
	// 전송 데이타가 가진 데이터 지정
	public static final byte BOOLEAN = 0;				// boolean 데이타
	public static final byte LOGIN = 1;					// login 데이타
	public static final byte CHATLIST = 2;				// 채팅방리스트 데이타
	public static final byte ONLINE = 3;					// online유저 id 데이타
	public static final byte MESSAGE = 4;				// message 데이타
	public static final byte REQ_ADD_CHAT = 5;	// 채팅방 추가 요청, 채팅방 데이타
	public static final byte REQ_CHATLIST = 6;		// 채팅방 리스트 요청, 채팅방리스트 데이타
	public static final byte REQ_ONLINE = 7;			// 온라인유저 리스트 요청, 온라인유저리스트 데이타
	
	private byte option;
	private List<ChatRoom> chatList;
	private LoginData login;
	private Message msg;
	private ChatRoom chat;
	private List<String> onlineUsers;
	private boolean bool;
	
	// 생성자
	public TransmitData() {
	}
	public TransmitData(byte option) {
		this.option = option;
	}
	public TransmitData(byte option,Object obj) {
		super();
		this.option = option;
		setData(obj);
	}
	
	public byte getOption() {
		return option;
	}
	public void setOption(byte option) {
		this.option = option;
	}
	public List<ChatRoom> getChatList() {
		return chatList;
	}
	public void setChatList(List<ChatRoom> chatList) {
		this.chatList = chatList;
	}
	public LoginData getLogin() {
		return login;
	}
	public void setLogin(LoginData login) {
		this.login = login;
	}
	public Message getMsg() {
		return msg;
	}
	public void setMsg(Message msg) {
		this.msg = msg;
	}
	public List<String> getOnlineUsers() {
		return onlineUsers;
	}
	public void setOnlineUsers(List<String> onlineUsers) {
		this.onlineUsers = onlineUsers;
	}
	public boolean isBool() {
		return bool;
	}
	public void setBool(boolean bool) {
		this.bool = bool;
	}
	
	// 옵션에 해당하는 데이타 반환
	public Object getData() {
		switch(this.option) {
		case TransmitData.BOOLEAN :
			return this.bool;
		case TransmitData.LOGIN :
			return this.login;
		case TransmitData.CHATLIST :
			return this.chatList;
		case TransmitData.ONLINE :
			return this.onlineUsers;
		case TransmitData.MESSAGE :
			return this.msg;
		case TransmitData.REQ_ADD_CHAT :
			return this.chat;
		default :
			return null;
		}
	}
	
	// 옵션에 해당하는 데이타 세팅
	@SuppressWarnings("unchecked")
	public void setData(Object obj) {
		switch(this.option) {
		case TransmitData.BOOLEAN :
			this.bool = (Boolean)obj;
			return;
		case TransmitData.LOGIN :
			this.login = (LoginData)obj;
			return;
		case TransmitData.CHATLIST :
			this.chatList = (List<ChatRoom>)obj;
			return;
		case TransmitData.ONLINE :
			this.onlineUsers = (List<String>)obj;
			return;
		case TransmitData.MESSAGE :
			this.msg = (Message)obj;
			return;
		case TransmitData.REQ_ADD_CHAT :
			this.chat = (ChatRoom)obj;
			return;
		default :
			return;
		}
	}
	@Override
	public String toString() {
		return "TransmitData [option=" + option + ", chatList=" + chatList + ", login=" + login + ", msg=" + msg
				+ ", chat=" + chat + ", onlineUsers=" + onlineUsers + ", bool=" + bool + "]";
	}
	
	
	
}
