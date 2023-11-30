package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import data.ChatRoom;
import data.LoginData;
import data.Message;
import data.TransmitData;

public class Test1 {

	Socket socket;
	LoginData data;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	List<String> userNameList;
	List<ChatRoom> chatRoomList;
	
	public Test1() {
		test();
	}
	public Test1(Socket socket) {
		test();
		this.socket = socket;
	}
	void test() {
		String id = "";
		String pw = "";
		data= null;
		Scanner sc = new Scanner(System.in);
		TransmitData tData=null;
		boolean b = false;
		String readLine = "";
		
		try {
			socket = new Socket("127.0.0.1",8888);
			oos= new ObjectOutputStream(socket.getOutputStream());
			ois= new ObjectInputStream(socket.getInputStream());
			
			while(true) {
				System.out.print("로그인 하시겠습니까? (Y/N) : ");
				id=sc.next();
				if(id.equals("Y"))
					b = true;
				else
					b = false;
				System.out.print("아이디를 입력하시오 : ");
				id=sc.next();
				System.out.print("비밀번호를 입력하시오 : ");
				pw=sc.next();
				
				data = new LoginData(id, pw, b);
				tData = new TransmitData(TransmitData.LOGIN, data);
				oos.writeObject(tData);
				boolean readB = (Boolean)((TransmitData)ois.readObject()).getData();
				System.out.println(readB);
				if(readB)
					break;
			}
			
			int select = 0;
			boolean flag = true;
			while(flag) {
				
				System.out.println("실행 작업을 선택");
				System.out.println("[1] 채팅방 생성 [2] 채팅방 입장 [0] 종료");
				readLine = sc.nextLine();
				select = Integer.getInteger(readLine);
				switch(select) {
				case 0 : // 종료
					flag = false;
					break;
				case 1 : // 채팅방 생성
					oos.writeObject(new TransmitData(TransmitData.ONLINE));
					tData = (TransmitData)ois.readObject();
					seqAfterRead(tData);
					System.out.println("채팅할 유저를 고르세요");
					readLine = sc.nextLine();
					
					//채팅방 보여주는 코드
					break;
				case 2 : // 채팅방 입장
					oos.writeObject(new TransmitData(TransmitData.CHATLIST));
					tData = (TransmitData)ois.readObject();
					seqAfterRead(tData);
					System.out.println("입장할 채팅 방을 고르세요");
					readLine = sc.nextLine();
					select = Integer.getInteger(readLine);
					//채팅방 보여주는 코드
					break;
				default :
					flag = false;
					break;
				}
			}
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				oos.close();
				socket.close();
				sc.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
	}
	
	void seqAfterRead(TransmitData tData) {
		System.out.println("수신완료"+tData.getOption());
		switch(tData.getOption()) {
		case TransmitData.BOOLEAN :
			return;
		case TransmitData.LOGIN :
			return;
		case TransmitData.CHATLIST :
			// 채팅창 리스트 업데이트 코드
			System.out.println("채팅창 리스트");
			chatRoomList = (List<ChatRoom>)tData.getData();
			if(chatRoomList==null) {
				System.out.println("채팅방이 없습니다.");
			} else {
				int size = chatRoomList.size();
				for(int i=0;i<size;i++) {
					System.out.print("["+i+"] ");
					System.out.println(chatRoomList.get(i).getName());
				}
			}
			
			return;
		case TransmitData.ONLINE :
			// 온라인 유저 업데이트 코드
			System.out.println("온라인 유저 리스트");
			userNameList = (List<String>)tData.getData();
			if(userNameList==null) {
				System.out.println("온라인 유저가 없습니다.");
			} else {
				int size = userNameList.size();
				for(int i=0;i<size;i++) {
					System.out.print("["+i+"] ");
					System.out.println(userNameList.get(i));
				}
			}
			return;
		case TransmitData.MESSAGE :
			Message msg = (Message)tData.getData();
			// 메세지 받아서 실행
			
			return;
		case TransmitData.REQ_ADD_CHAT :
			return;
		case TransmitData.REQ_CHATLIST :
			return;
		case TransmitData.REQ_ONLINE :
			return;
		default :
			return;
		}
	}
	public static void main(String[] args) {
		new Test1();
	}
	
}
