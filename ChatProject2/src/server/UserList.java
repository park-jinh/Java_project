package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.ChatRoom;

public class UserList {

	volatile Map<String,User> userMap;	// id, user 저장 맵
	String path;										// user 데이터 저장 폴더 주소
	File dir;												// user 데이터 저장 폴더
	
	// 생성자
	public UserList() {
	}
	public UserList(String path) {
		this.path = path+"user/";
		this.dir = new File(this.path);
		getMap();
	}

	// 폴더에서 user파일들을 읽어와 userMap에 저장
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
					user = new User(id,pw,list);
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
	
	// 회원가입
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
	
	// 유저 추가
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
	
	// 로그인 처리과정
	boolean login(User user) {
		System.out.println("로그인 시도");
		if(isExistUser(user) && isCorrectPW(user)) {
			// id 존재하고 비밀번호가 맞을 때(=로그인 성공)
			return true;
		} else {
			return false;
		}
	} // login
	
	// id가 존재하는 지 확인
	boolean isExistUser(User user) {
		return this.userMap.containsKey(user.getId());
	} // isExistUser
	
	// 비밀번호가 맞는지
	boolean isCorrectPW(User user) {
		String pw = getUser(user.getId()).getPassword();
		return (user.getPassword()).equals(pw);
	} // isCorrectPW

	// id에 해당하는 유저 반환
	User getUser(String id) {
		return userMap.get(id);
	} // getUser
	
	// 채팅창에 있는 유저 리스트와 유저가 가지고 있는 채팅방 리스트 연동
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
	} // linkUserToChat

} // class
