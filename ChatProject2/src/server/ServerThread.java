package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import data.ChatRoom;
import data.LoginData;
import data.Message;
import data.TransmitData;

public class ServerThread extends Thread {

	Socket socket;		// 소켓 저장 변수
	UserList userList;	// 유저에 관한 데이터 저장된 공유 객체
	User user;				// 해당 쓰레드를 사용하는 유저 저장 객체
	ChatRoomList chatRoomList;		// 채팅방 데이터들이 저장된 공유 객체
	ObjectInputStream ois = null;	
	ObjectOutputStream oos = null;
	Map<String, ObjectOutputStream> userOutputMap;	// 각 Thread 에서 다른 user(client)에 데이터를 송신하기 위함
	
	// 생성자
	public ServerThread() {
	}
	public ServerThread(Socket socket, UserList userList, ChatRoomList chatRoomList, Map<String, ObjectOutputStream> userOutputMap) {
		this.socket = socket;
		this.userList = userList;
		this.chatRoomList = chatRoomList;
		this.userOutputMap = userOutputMap;
	}


	@Override
	public void run() {
		//쓰레드로 유저와 양방향 통신
		System.out.println("서버 쓰레드 시작");
		
		LoginData loginData = null;
		
		try {
			ois = new ObjectInputStream(this.socket.getInputStream());
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			while(true) {
				// 로그인 데이터 수신
				loginData = (LoginData)((TransmitData)ois.readObject()).getData();
				
				System.out.println("오브젝트 받기 완료");
				System.out.println(loginData);
				
				user = new User(loginData.getId(),loginData.getPassword(),null);
				
				if(loginData.isLogin()) {
					//로그인 성공 시 break
					if(login())	break;
				} else {
					//회원가입 성공 시 break
					if(signUp()) break;
				}
			} // while
			
			// 로그인 후 진행 할 작업 들
			// 온라인 유저로 저장
			userOutputMap.put(user.getId(), oos);
			// 온라인 리스트 넘김
			transmitOnline();
			
			// 유저가가진 chatList 넘김
			transmitChatList();
			
			// 메세지 수신
			while(true) {
				// 데이타 수신 후 처리작업
				seqAfterRecieve((TransmitData)ois.readObject());
			}
			
		} catch(SocketException se) {
			// 소켓 닫힐 때 처리 작업
			close();
		} catch(EOFException eofe) {
			// 소켓 닫힐 때 처리 작업
			close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} finally {
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		} // finally
		
	} // run
	
	// 데이타 수신 후 처리 작업
	void seqAfterRecieve(TransmitData data) throws IOException {
		System.out.println(data);
		switch(data.getOption()) {
		case TransmitData.BOOLEAN :
			return;
		case TransmitData.LOGIN :
			return;
		case TransmitData.CHATLIST :
			return;
		case TransmitData.ONLINE :
			return;
		case TransmitData.MESSAGE :
			Message msg = (Message)data.getData();
			// 메세지 받아서 실행
			sendMsg(msg);
			return;
		case TransmitData.REQ_ADD_CHAT :
			// 채팅방 추가 요청
			System.out.println("채팅방 추가 요청");
			ChatRoom chat = (ChatRoom)data.getData();
			if(chatRoomList.createChat(chat)) {
				transmitChat(chat);
			}
			return;
		case TransmitData.REQ_CHATLIST :
			// 채팅리스트 보내주기
			System.out.println("채팅창 리스트 요청");
			transmitChatList();
			return;
		case TransmitData.REQ_ONLINE :
			// 온라인 유저 리스트 보내주기
			transmitOnline();
			return;
		default :
			return;
		}
	}
	
	boolean login(){
		if(this.userList.login(this.user)) {
			//로그인 성공
			user = userList.getUser(user.getId());
			sendData(TransmitData.BOOLEAN, true);
			return true;
		}
		else {
			// 로그인 실패
			sendData(TransmitData.BOOLEAN,false);
			return false;
		}
	}
	
	boolean signUp(){
		if(this.userList.signUp(this.user)) {
			// 회원가입 성공
			sendData(TransmitData.BOOLEAN, true);
			return true;
		} else {
			// 회원가입 실패
			sendData(TransmitData.BOOLEAN,false);
			return false;
		}
	}
	
	// 온라인 유저 전송
	void transmitOnline(){
		List<String> onlineList = new ArrayList<String>();
		onlineList.addAll(userOutputMap.keySet());
		System.out.println(onlineList);
		for(ObjectOutputStream os : userOutputMap.values()) {
			sendData(TransmitData.ONLINE, onlineList, os);
		}  // for
	}
	
	// 채팅리스트 전송
	void transmitChatList(){
		List<ChatRoom> chatList = chatRoomList.getChatListByUser(user);
		sendData(TransmitData.CHATLIST, chatList);
	}
	
	// 채팅방 전송
	void transmitChat(ChatRoom chat) throws IOException{
		for(String userId : chat.getIdSet()) {
			if(userOutputMap.containsKey(userId))
				sendData(TransmitData.REQ_ADD_CHAT, chat, userOutputMap.get(userId));
		}
	}
	
	// 메세지 전송
	void sendMsg(Message msg) throws IOException{
		ChatRoom chat = chatRoomList.getChat(msg.getChatId());
		chat.addMsg(msg);
		for(String userId : chat.getIdSet()) {
			if(userOutputMap.containsKey(userId))
				sendData(TransmitData.MESSAGE, msg, userOutputMap.get(userId));
		}
	}
	
	// 데이타 전송
	void sendData(Byte option, Object obj) {
		try {
			oos.writeObject(new TransmitData(option, obj));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	// 데이타 전송 오버로딩(현재 쓰레드사용 유저가 아닌 다른 유저에게 전송하기 위함)
	void sendData(Byte option, Object obj,ObjectOutputStream os) {
		try {
			os.writeObject(new TransmitData(option, obj));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	// 소켓 닫을 때 처리 구문
	void close() {
		System.out.println("소켓 닫힘");
		userOutputMap.remove(user.getId());
		chatRoomList.save();
		System.out.println(userOutputMap);
		transmitOnline();
		try {
			ois.close();
			oos.close();
			socket.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
} // class
