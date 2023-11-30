package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatRoom implements Serializable {

	public static final long serialVersionUID = 8453124528L;
	
	private Set<String> idSet;
	private List<Message> mList;
	private int chatId;
	
	public ChatRoom() {
	}
	public ChatRoom(Set<String> idSet, List<Message> mList, int chatId) {
		super();
		this.idSet = idSet;
		this.mList = mList;
		this.chatId = chatId;
	}
	
	public Set<String> getIdSet() {
		return idSet;
	}
	public void setIdSet(Set<String> idSet) {
		this.idSet = idSet;
	}
	public List<Message> getmList() {
		return mList;
	}
	public void setmList(List<Message> mList) {
		this.mList = mList;
	}
	public int getChatId() {
		return chatId;
	}
	public void setChatId(int chatId) {
		this.chatId = chatId;
	}
	
	public String getName() {
		String name="";
		for(String temp : this.idSet) {
			name += temp+" ";
		}
		return name;
	}
	
	public void addMsg(Message msg) {
		if(this.mList == null)
			this.mList = new ArrayList<Message>();
		this.mList.add(msg);
	}
	
	@Override
	public String toString() {
		return "ChatRoom [idSet=" + idSet + ", mList=" + (mList==null?"null":mList.size()) + ", chatId=" + chatId + "]";
	}
	
	
}
