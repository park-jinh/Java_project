package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerMain {

	
	public static void main(String[] args) {
		// 온라인 유저 id, oos 저장할 변수 
		Map<String, ObjectOutputStream> userOutputMap = new HashMap<String, ObjectOutputStream>();

		String path = "c:/chatProject/";
		UserList userList = new UserList(path);
		ChatRoomList chatRoomList = new ChatRoomList(path,userList);
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		System.out.println("서버 시작");
		try {
			
			serverSocket = new ServerSocket(8888);
			while(true){
				socket = serverSocket.accept();	// 소켓 연결 성공
				System.out.println(socket.getInetAddress()+" 님이 접속하였습니다.");
				new ServerThread(socket, userList, chatRoomList, userOutputMap).start();
				// 소켓 연결 후 이후 작업은 쓰레드에서 수행, 1Thread 1Socket 1user 구조
			}
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	} // main
	
	
	
} // 
