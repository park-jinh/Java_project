package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import data.ChatRoom;
import data.LoginData;
import data.Message;
import data.TransmitData;

public class ServerLoginThread extends Thread {

	Socket socket;
	UserList userList;
	User user;
	TransmitData tData;
	ChatRoomList chatRoomList;
	ObjectInputStream ois = null;
	ObjectOutputStream oos = null;
	Map<String, ObjectOutputStream> userOutputMap;
	
	// 생성자
	public ServerLoginThread() {
	}
	public ServerLoginThread(Socket socket, UserList userList, ChatRoomList chatRoomList, Map<String, ObjectOutputStream> userOutputMap) {
		this.socket = socket;
		this.userList = userList;
		this.chatRoomList = chatRoomList;
		this.userOutputMap = userOutputMap;
	}


	@Override
	public void run() {
		//쓰레드로 실행 할 로그인 기능
		System.out.println("서버 로그인 쓰레드 시작");
		
		LoginData loginData = null;
		
		try {
			ois = new ObjectInputStream(this.socket.getInputStream());
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			while(true) {
				
				 loginData = (LoginData)((TransmitData)ois.readObject()).getData();
				
				System.out.println("오브젝트 받기 완료");
				System.out.println(loginData);
				
				user = new User(loginData.getId(),loginData.getPassword(),this.socket,null,true);
				
				
				if(loginData.isLogin()) {
					//로그인
					if(this.userList.login(this.user)) {
						//로그인 성공
						user = userList.getUser(user.getId());
						user.setOnlineYN(true);
						tData = new TransmitData(TransmitData.BOOLEAN,true);
						oos.writeObject(tData);
						break;
					}
					else {
						// 로그인 실패
						tData = new TransmitData(TransmitData.BOOLEAN,false);
						oos.writeObject(tData);
					}
				} else {
					//회원가입
					if(this.userList.signUp(this.user)) {
						// 회원가입 성공
						tData = new TransmitData(TransmitData.BOOLEAN,true);
						oos.writeObject(tData);
						break;
					} else {
						// 회원가입 실패
						tData = new TransmitData(TransmitData.BOOLEAN,false);
						oos.writeObject(tData);
					}
				}
			} // while
			// 로그인 후 진행 할 작업
			userOutputMap.put(user.getId(), oos);
			// 온라인 리스트 넘김
			transmitOnline();
			
			// 유저가가진 chatList 넘김
			transmitChatList();
			
			// 메세지 수신
			while(true) {
				seqAfterRecieve((TransmitData)ois.readObject());
			}
			// 메세지 송신
			
		}
		catch(SocketException se) {
			// 소켓 닫힐 때 처리 구문 추가 필
			System.out.println("소켓 닫힘");
			user.setOnlineYN(false);
			userOutputMap.remove(user.getId());
			chatRoomList.save();
			System.out.println(userOutputMap);
			transmitOnline();
		}catch(EOFException eofe) {
			// 소켓 닫힐 때 처리 구문 추가 필
			System.out.println("소켓 닫힘");
			user.setOnlineYN(false);
			userOutputMap.remove(user.getId());
			chatRoomList.save();
			System.out.println(userOutputMap);
			transmitOnline();
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} finally {
			try {
				ois.close();
				oos.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}// finally
		
	} // run
	
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
			// 채팅창 추가 요청
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
	
	// 온라인 유저 전송
	void transmitOnline(){
		List<String> onlineList = userList.getOnlineUserList();
		System.out.println(onlineList);
		tData = new TransmitData(TransmitData.ONLINE, onlineList);
		try {
			for(ObjectOutputStream os : userOutputMap.values()) {
					os.writeObject(tData);
			} 
		} catch(IOException ioe) {
		} // for
	}
	
	// 채팅리스트 전송
	void transmitChatList() throws IOException {
		List<ChatRoom> chatList = chatRoomList.getChatListByUser(user);
		oos.writeObject(new TransmitData(TransmitData.CHATLIST, chatList));
	}
	
	// 채팅방 전송
	void transmitChat(ChatRoom chat) throws IOException{
		for(String userId : chat.getIdSet()) {
			if(userOutputMap.containsKey(userId))
				userOutputMap.get(userId).writeObject(new TransmitData(TransmitData.REQ_ADD_CHAT, chat));
		}
	}
	
	void sendMsg(Message msg) throws IOException{
		ChatRoom chat = chatRoomList.getChat(msg.getChatId());
		chat.addMsg(msg);
		for(String userId : chat.getIdSet()) {
			if(userOutputMap.containsKey(userId))
				userOutputMap.get(userId).writeObject(new TransmitData(TransmitData.MESSAGE,msg));
		}
	}
	
} // class
