package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.ChatRoom;

public class ChatRoomList {

	volatile Map<Integer, ChatRoom> chatMap;				// 채팅창번호, 채팅창 저장
	volatile Map<Set<String>, Integer> userChatMap;	// 유저id 셋, 채팅창번호 저장 
	UserList userList;														// user들의 정보가 모인 객체
	String path;
	File dir;
	
	public ChatRoomList() {
	}
	public ChatRoomList(String path,UserList userList) {
		this.path = path+"chat/";
		this.dir = new File(this.path);
		this.userList = userList;
		initChatMap();
	}
	
	// chat 폴더에서 채팅방 정보 읽어드려 객체에 저장
	void initChatMap(){
		this.chatMap = new HashMap<Integer, ChatRoom>();
		this.userChatMap = new HashMap<Set<String>, Integer>();
		
		if(!this.dir.exists()) {
			// 폴더가 없을 때	
			this.dir.mkdirs();
		} else {
			// 폴더가 있을 때	
			File[] files = this.dir.listFiles();
			ObjectInputStream ois = null;
			ChatRoom chat=null;
			for(File file : files) {
				
				try {
					ois = new ObjectInputStream(
							new FileInputStream(file)
							);
					chat = (ChatRoom)ois.readObject();
					this.chatMap.put(chat.getChatId(), chat);
					this.userChatMap.put(chat.getIdSet(), chat.getChatId());
					System.out.println(chat);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				} catch(ClassNotFoundException cnfe) {
					cnfe.printStackTrace();
				} finally {
					try {
						ois.close();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				} // finally
			} // for
		} // else
	} // initChatMap
	
	// 새로운 채팅발 개설 로직
	boolean createChat(ChatRoom chat) {
		System.out.println("챗 생성 시도");
		if(isPossibleCreateChat(chat)) {
			int chatId =0;
			while(true) {
				chatId = (int)(Math.random()*2147483647);
				if(!chatMap.containsKey(chatId)) break;
			}
			chat.setChatId(chatId);
			addChat(chat);
			userList.linkUserToChat(chat);
			System.out.println("챗 생성 성공");
			return true;
		} else {
			System.out.println("챗 생성 실패");
			return false;
		}
	}
	
	// 채팅방 추가
	void addChat(ChatRoom chat){
		System.out.println("채팅방 추가");
		chatMap.put(chat.getChatId(), chat);
		userChatMap.put(chat.getIdSet(), chat.getChatId());
		
		File file = new File(path + chat.getChatId() + ".dat");
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(
						new FileOutputStream(file)
					);
			oos.writeObject(chat);
			oos.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	} // addChat
	
	// 채팅방 생성할 수 있는지
	boolean isPossibleCreateChat(ChatRoom chat) {
		if(this.userChatMap.containsKey(chat.getIdSet())) {
			return false;
		} else {
			return true;
		}
	}
	
	// user가 가진 채팅방 리스트 반환
	List<ChatRoom> getChatListByUser(User user){
		List<ChatRoom> chatList = new ArrayList<ChatRoom>();
		if(user.getChatList()!=null) {
			for(int chatId : user.getChatList()) {
				chatList.add(chatMap.get(chatId));
			}
		}
		return chatList;
	}
	
	// 채팅방번호에 해당하는 채팅반 반환
	ChatRoom getChat(int chatId) {
		return chatMap.get(chatId);
	}
	
	// 채팅방 정보 저장
	void save() {
		if(!chatMap.isEmpty()) {
			ObjectOutputStream os = null;
			for(ChatRoom chat : chatMap.values()) {
				try {
					os=new ObjectOutputStream(new FileOutputStream(this.path+chat.getChatId()+".dat"));
					os.writeObject(chat);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				} finally {
					try {
						os.close();
					}catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		}
	}
	
}// class
