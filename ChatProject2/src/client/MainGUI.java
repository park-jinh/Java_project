package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import data.ChatRoom;
import data.TransmitData;

public class MainGUI extends JFrame {

	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	TransmitData tData;
	JList<String> jUserList;
	JList<String> jChatList;
	ChatRoom chat;
	List<ChatGUI> chatGUIList;
	String id;
	
	// 생성자
	public MainGUI() {
	}
	public MainGUI(Socket socket,ObjectOutputStream oos,ObjectInputStream ois,List<ChatGUI> chatGUIList,String id) {
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
		this.chatGUIList = chatGUIList;
		this.id = id;
		init();
	}
	
	
	void init() {
		this.setTitle("채팅 프로그램 ("+id+")");
		
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		
		ImageIcon userImg = new ImageIcon("img/user.png");
		ImageIcon chatImg = new ImageIcon("img/chat.png");
		JLabel label1 = new JLabel(userImg,JLabel.CENTER);
		label1.setBorder(new LineBorder(Color.blue));
		JLabel label2 = new JLabel(chatImg,JLabel.CENTER);
		JLabel label3 = new JLabel("",JLabel.CENTER);
		JLabel label4 = new JLabel("",JLabel.CENTER);
		JLabel label5 = new JLabel("",JLabel.CENTER);
		JLabel label6 = new JLabel("",JLabel.CENTER);
		JLabel label7 = new JLabel("",JLabel.CENTER);
		
		CardLayout card = new CardLayout();
		JPanel panel = new JPanel(card);
		
		JPanel userListPanel = new JPanel(new BorderLayout());
		JButton creatChatBtn = new JButton("채팅방 생성");
		userListPanel.add(creatChatBtn, BorderLayout.SOUTH);
		jUserList = new JList<String>();
		JScrollPane userList = new JScrollPane(jUserList);
		
		userListPanel.add(userList, BorderLayout.CENTER);
		
		JPanel chatListPanel = new JPanel(new BorderLayout());
		JButton openChatBtn = new JButton("채팅방 입장");
		chatListPanel.add(openChatBtn, BorderLayout.SOUTH);
		jChatList = new JList<String>();
		jChatList.setVisibleRowCount(10);
		jChatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane chatList = new JScrollPane(jChatList);
		chatListPanel.add(chatList, BorderLayout.CENTER);
		
		
		panel.add(userListPanel,"user");
		panel.add(chatListPanel,"chat");
		
		
		gridbagInsert(gridbag,label1,0,0,1,1,1,1);
		gridbagInsert(gridbag,label2,0,1,1,1,1,1);
		gridbagInsert(gridbag,label3,0,2,1,1,1,1);
		gridbagInsert(gridbag,label4,0,3,1,1,1,1);
		gridbagInsert(gridbag,label5,0,4,1,1,1,1);
		gridbagInsert(gridbag,label6,0,5,1,1,1,1);
		gridbagInsert(gridbag,label7,0,6,1,1,1,1);
		gridbagInsert(gridbag,panel,1,0,5,7,5,1);
		
		
		this.setSize(500,700);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			// 창 종료시
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					socket.close();
					oos.close();
					ois.close();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				} finally {
					System.exit(0);
				}
			}
		});
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
		label1.addMouseListener(new MouseAdapter() {
			// 유저 클릭 시
			@Override
			public void mousePressed(MouseEvent e) {
				label1.setBorder(new LineBorder(Color.blue));
				label2.setBorder(null);
				card.show(panel, "user");
			}
		});
		label2.addMouseListener(new MouseAdapter() {
			// 채팅방 클릭 시
			@Override
			public void mousePressed(MouseEvent e) {
				label2.setBorder(new LineBorder(Color.blue));
				label1.setBorder(null);
				card.show(panel, "chat");
			}
		});
		
		creatChatBtn.addActionListener(new  ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 채팅방 생성 버튼 클릭 시
				Set<String> idSet = new HashSet<String>();
				idSet.addAll(jUserList.getSelectedValuesList());
				chat = new ChatRoom(idSet, null, -1);
				System.out.println("채팅방 생성 요청"+chat);
				try {
					oos.writeObject(new TransmitData(TransmitData.REQ_ADD_CHAT,chat));
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		openChatBtn.addActionListener(new  ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 채팅방 입장 버튼 클릭 시
				int index = jChatList.getSelectedIndex();
//				chatGUIList.get(index).loadChatMsg();
				chatGUIList.get(index).setVisible(true);
			}
		});
	} // init
	
	// gridbag 레이아웃 설정하여 컴포넌트 넣기
	void gridbagInsert(GridBagLayout gridbag,Component c, int x, int y,int w,int h, int wx,int wy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridheight = h;
		gbc.gridwidth = w;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gridbag.setConstraints(c, gbc);
		this.add(c);
	} // gridbagInsert
	
	
}
