package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.ChatRoom;
import data.TransmitData;

public class UserList {

	volatile Map<String,User> userMap;
	String path;
	File dir;
	
	// 생성자
	public UserList() {
	}
	public UserList(String path) {
		this.path = path+"user/";
		this.dir = new File(this.path);
		getMap();
	}


	void getMap(){
		this.userMap = new HashMap<String, User>();
		
		if(!this.dir.exists()) {
			// 폴더가 없을 때
			System.out.println("유저 폴더 없음");
			this.dir.mkdirs();
		} else {
			// 폴더가 있을 때
			System.out.println("유저 폴더 있음");
			File[] files = this.dir.listFiles();
			BufferedReader br = null;
			User user=null;
			String id="";
			String pw="";
			String readLine = "";
			List<Integer> list = null;
			for(File file : files) {
				
				try {
					br = new BufferedReader(new FileReader(file));
					id = br.readLine();
					pw = br.readLine();
					list =  new ArrayList<Integer>();
					while((readLine = br.readLine())!=null) {
						list.add(Integer.parseInt(readLine));
					}
					user = new User(id,pw,null,list,false);
					this.userMap.put(user.getId(), user);
					System.out.println(user);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				} finally {
					try {
						br.close();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				} // finally
			} // for
		}
		
	} // getMap
	
	
	boolean signUp(User user) {
		System.out.println("회원가입 시도");
		if(isExistUser(user)) {
			// 아이디가 존재하면
			return false;
		} else {
			addUser(user);
			return true;
		}
	} // signUp
	
	void addUser(User user) {
		this.userMap.put(user.getId(), user);
		
		File file = new File(this.path+user.getId()+".txt");
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(
						new FileWriter(file)
					);
			bw.write(user.getId());
			bw.newLine();
			bw.write(user.getPassword());

		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	} // addUser
	
	
	boolean login(User user) {
		System.out.println("로그인 시도");
		if(isExistUser(user) && isCorrectPW(user)) {
			getUser(user.getId()).setOnlineYN(true);
			return true;
		} else {
			return false;
		}
	} // login
	
	boolean isExistUser(User user) {
		return this.userMap.containsKey(user.getId());
	} // isExistUser
	
	boolean isCorrectPW(User user) {
		String pw = getUser(user.getId()).getPassword();
		return (user.getPassword()).equals(pw);
	} // isCorrectPW

	User getUser(String id) {
		return userMap.get(id);
	}
	
	
	// 온라인 유저 리스트 반환
	List<String> getOnlineUserList(){
		Object[] userList = this.userMap.values().toArray();
		User user = null;
		List<String> onlineList = new ArrayList<String>();
		int listSize = userList.length;
		
		for(int i=0;i<listSize;i++) {
			user = (User)userList[i];
			if(user.isOnlineYN())
				onlineList.add(user.getId());
		}
		
		return onlineList;
	}
	
	void linkUserToChat(ChatRoom chat) {
		Set<String> idSet = chat.getIdSet();
		int chatId = chat.getChatId();
		BufferedWriter bw = null;
		for(String id : idSet) {
			userMap.get(id).addChatId(chatId);
			try {
				bw = new BufferedWriter(new FileWriter(new File(path+id+".txt"),true));
				bw.newLine();
				bw.write(String.valueOf(chatId));
			} catch(IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					bw.close();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

} // class
