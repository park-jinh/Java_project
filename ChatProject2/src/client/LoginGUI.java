package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import data.LoginData;
import data.TransmitData;

public class LoginGUI extends JFrame {
	
	JLabel msgLabel;					// 정보 문자열 출력할 레이블
	JTextField idText;					// id 입력창
	JTextField pwText;					// pw 입력창
	LoginData data;					// 로그인 데이터
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	TransmitData tData;
	String id;
	
	public LoginGUI() {
	}
	public LoginGUI(Socket socket,ObjectOutputStream oos,ObjectInputStream ois) {
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
		init();
	}

	void init() {
		
		
		this.setTitle("로그인");
		this.setBounds(400,200,400,230);
		this.setLayout(null);
		this.setResizable(false);
		
		msgLabel = new JLabel("", JLabel.CENTER);
		msgLabel.setBounds(0, 10, 400, 30);
		msgLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		msgLabel.setForeground(Color.RED);
		this.add(msgLabel);
		
		
		JLabel idLabel = new JLabel("Id :", JLabel.RIGHT);
		idLabel.setBounds(10, 50, 100, 30);
		idLabel.setFont(new Font("Arial", Font.BOLD, 16));
		this.add(idLabel);
		
		idText = new JTextField("아이디 입력");
		idText.setBounds(120, 50, 250, 30);
		this.add(idText);
		
		JLabel passwordLabel = new JLabel("Password :", JLabel.RIGHT);
		passwordLabel.setBounds(10, 90, 100, 30);
		passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
		this.add(passwordLabel);
		
		pwText = new JTextField("비밀번호 입력");
		pwText.setBounds(120, 90, 250, 30);
		this.add(pwText);
		
		JButton signBtn = new JButton("회원가입");
		signBtn.setBounds(10, 150, 180, 40);
		signBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		this.add(signBtn);
		
		signBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 회원가입 버튼 클릭 이벤트
				signBtnClick();
			}
		});
		
		JButton okBtn = new JButton("로그인");
		okBtn.setBounds(200, 150, 180, 40);
		okBtn.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		this.add(okBtn);
		
		okBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 로그인 버튼 클릭 이벤트
				okBtnClick();
			}
		});
		
		idText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 아이디입력 창 클릭했을 시 이벤트
				if(("아이디 입력").equals(idText.getText()))
					idText.setText("");
			}
		});
		
		pwText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// 비밀번호 입력 창 활성화 되었을 시 이벤트
				if(("비밀번호 입력").equals(pwText.getText()))
					pwText.setText("");
			}
		});
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	} // init
	
	
	// 회원가입 버튼 클릭 시 작동할 메소드
	void signBtnClick() {
		if(send(false)) {
			//정상적으로 회원가입 되었을 떄
			msgLabel.setText("회원가입 성공");
			close();
		} else {
			// 실패했을 때
			msgLabel.setText("중복되는 아이디가 있습니다.");
		}
	} // signBtnClick
	
	// 로그인 버튼 클릭 시 작동할 메소드
	void okBtnClick() {
		if(send(true)) {
			//정상적으로 로그인 되었을 떄
			msgLabel.setText("로그인 성공");
			close();
		} else {
			// 실패했을 때
			msgLabel.setText("로그인에 실패했습니다. 다시확인하세요.");
		}
	} // okBtnClick
	
	// 로그인 정보 보내는 메소드
	boolean send(boolean flag) {
		msgLabel.setText("");
		data = new LoginData(this.idText.getText(),this.pwText.getText(), flag);
		tData = new TransmitData(TransmitData.LOGIN, data);
		boolean bool=false;
		
		try {
			oos.writeObject(tData);
			bool = (Boolean)((TransmitData)ois.readObject()).getData();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		
		return bool;
	} // send
	
	// 로그인 창이 끝날때 메소드
	void close() {
		id = this.idText.getText();
		new ClientMain(socket,oos,ois,id).start();
		this.dispose();
	}
	
	/*
	 *비밀번호를 SHA-256으로 해싱하는 메소드
	 *
	String pwHashing(String pw){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch(NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
		}
		
		pw += "jinhyeong"; // 더미 문자열 추가
		md.update(pw.getBytes());
		pw = new String(md.digest()); // 해싱된 문자열 저장
	
		return pw;
	}
	*/
	
	
	public static void main(String[] args) {
		Socket socket = null;
		ObjectInputStream oi = null;
		ObjectOutputStream oo= null;
		try {
		
			socket = new Socket("127.0.0.1",8888);
			oo = new ObjectOutputStream(socket.getOutputStream());
			oi = new ObjectInputStream(socket.getInputStream());
		} catch(IOException ioe) {
			
		}
		new LoginGUI(socket,oo,oi);
	}
	
}
