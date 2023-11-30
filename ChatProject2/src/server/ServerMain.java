package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerMain {

	
	public static void main(String[] args) {
		Map<String, ObjectOutputStream> userOutputMap = new HashMap<String, ObjectOutputStream>();

		String path = "c:/chatProject/";
		UserList userList = new UserList(path);
		ChatRoomList chatRoomList = new ChatRoomList(path,userList);
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		System.out.println("서버 시작");
		try {
			
			serverSocket = new ServerSocket(8888); //로그인 처리 소켓
			while(true){
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress()+" 님이 접속하였습니다.");
				new ServerLoginThread(socket, userList, chatRoomList, userOutputMap).start();
			}
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	} // main
	
	
	
} // 
