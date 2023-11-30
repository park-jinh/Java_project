package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.ChatRoom;
import data.Message;
import data.TransmitData;

public class ClientMain extends Thread{
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	TransmitData tData;
	MainGUI mainGUI;
	List<String> userNameList;
	List<ChatRoom> chatRoomList;
	List<String> chatName;
	Map<Integer, ChatGUI> chatMap;
	List<ChatGUI> chatGUIList;
	String id;
	
	// 생성자
	public ClientMain(Socket socket, ObjectOutputStream oos, ObjectInputStream ois,String id) {
		super();
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
		this.id = id;
		this.chatGUIList = new ArrayList<ChatGUI>();
		this.chatMap = new HashMap<Integer, ChatGUI>();
		this.chatName = new ArrayList<String>();
		this.mainGUI = new MainGUI(socket,oos,ois,chatGUIList,id);
	}

	@Override
	public void run() {
		init();
	}
	
	void init() {
		try {
			while(true) {
				tData = (TransmitData)ois.readObject();
				seqAfterRead();
			}
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	} // init

	
	// 데이터 수신 후 처리 과정
	void seqAfterRead() {
		System.out.println("수신완료"+tData.getOption());
		System.out.println(tData);
		switch(tData.getOption()) {
		case TransmitData.BOOLEAN :
			return;
		case TransmitData.LOGIN :
			return;
		case TransmitData.CHATLIST :
			// 채팅창 리스트 업데이트 코드
			chatRoomList = (List<ChatRoom>)tData.getData();
			if(chatRoomList != null) {
				loadChatList();
			}
			return;
		case TransmitData.ONLINE :
			// 온라인 유저 업데이트 코드
			userNameList = (List<String>)tData.getData();
			System.out.println(userNameList);
			updateUserList();
			return;
		case TransmitData.MESSAGE :
			Message msg = (Message)tData.getData();
			// 메세지 받아서 실행
			chatMap.get(msg.getChatId()).addMsg(msg);
			return;
		case TransmitData.REQ_ADD_CHAT :
			// 채팅창 새로 만드는 요청
			ChatRoom chat = (ChatRoom)tData.getData();
			addChatRoom(chat);
			return;
		case TransmitData.REQ_CHATLIST :
			return;
		case TransmitData.REQ_ONLINE :
			return;
		default :
			return;
		}
	}
	
	// 유저리스트 업데이트하는 메소드
	void updateUserList() {
		mainGUI.jUserList.setListData(userNameList.toArray(new String[userNameList.size()]));
	}
	
	void loadChatList() {
		int listSize = chatRoomList.size();
		ChatGUI chatGUI = null;
		
		for(int i=0;i<listSize;i++) {
			chatName.add(chatRoomList.get(i).getName());
			chatGUI = new ChatGUI(chatRoomList.get(i),oos,id);
			this.chatGUIList.add(chatGUI);
			chatMap.put(chatRoomList.get(i).getChatId(), chatGUI);
		}
		updateChatList();
	}
	
	// 채팅방리스트 업데이트하는 메소드
	void updateChatList() {
		mainGUI.jChatList.setListData(chatName.toArray(new String[chatName.size()]));
	}
	
	// 채팅방 추가하는 메소드
	void addChatRoom(ChatRoom chat) {
		if(chatRoomList == null) {
			chatRoomList = new ArrayList<ChatRoom>();
			chatName = new ArrayList<String>();
		} 
		chatRoomList.add(chat);
		chatName.add(chat.getName());
		ChatGUI chatGUI = new ChatGUI(chat,oos,id);
		chatGUIList.add(chatGUI);
		chatMap.put(chat.getChatId(), chatGUI);
		updateChatList();
	}
	
} // class
