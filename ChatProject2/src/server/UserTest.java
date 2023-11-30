package server;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class UserTest {
	public static void main(String[] args) {
		
		ObjectInputStream ois = null;
		User user=null;
		try {
			ois = new ObjectInputStream(new FileInputStream("C:/chatProject/user/user1.dat"));
			user = (User)ois.readObject();
			System.out.println(user);
			System.out.println(user.getId()+","+user.getPassword()+","+user.isOnlineYN());
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				
				ois.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
	}
}
