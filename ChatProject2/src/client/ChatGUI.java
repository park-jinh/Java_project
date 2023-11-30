package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import data.ChatRoom;
import data.Message;
import data.TransmitData;

public class ChatGUI extends JFrame{

	ChatRoom chatRoom;
	ObjectOutputStream oos = null;
	String id;
	JTextArea chatTextArea;
	
	public ChatGUI() {
	}
	public ChatGUI(ChatRoom chatRoom,ObjectOutputStream oos, String id) {
		this.chatRoom = chatRoom;
		this.oos = oos;
		this.id = id;
		init();
	}
	
	void init() {
		this.setTitle(chatRoom.getName()+" ("+id+")");
		
		GridBagLayout gridbag = new GridBagLayout();
		this.setLayout(gridbag);
		
		chatTextArea = new JTextArea("");
		chatTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatTextArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel messagePanel = new JPanel();
		JTextField messageText = new JTextField();
		JButton sendBtn = new JButton("전송");
		messageText.setFont(new Font("맑은 고딕",Font.PLAIN,16));
		messagePanel.setLayout(new BorderLayout());
		messagePanel.add(messageText,BorderLayout.CENTER);
		messagePanel.add(sendBtn,BorderLayout.EAST);
		
		
		gridbagInsert(gridbag, scrollPane, 0, 0, 1, 7, 1, 7);
		gridbagInsert(gridbag, messagePanel, 0, 8, 1, 1, 1, 1);

		this.setSize(400,600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(false);
		
		// 버튼 클릭 받을 때 작동할 내용
		sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMsg(messageText);
			}
		});
		// 텍스트 창에서 엔터키 눌렀을 때
		messageText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMsg(messageText);
			}
		});
		
	}
	
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
	}
	
	void loadChatMsg() {
		if(chatRoom.getmList() != null) {
			List<Message> msgList=chatRoom.getmList();
			int listSize = msgList.size();
			for(int i=0;i<listSize;i++) {
				addMsg(msgList.get(i));
			}
		}
	}
	
	void sendMsg(JTextField jtf) {
		// 메세지를 서버로 보내는 메소드
		String str = jtf.getText();
		Message msg = new Message(id, str, chatRoom.getChatId());
		try {
			oos.writeObject(new TransmitData(TransmitData.MESSAGE, msg));
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("메세지 전송"+msg);
		jtf.setText("");
	}
	
	void addMsg(Message msg) {
		chatRoom.addMsg(msg);
		if((msg.getWriter()).equals(id)) {
			chatTextArea.append("[나] - "+msg.getMessage());
			chatTextArea.append("\n");
		} else {
			chatTextArea.append(msg.toText());
			chatTextArea.append("\n");
		}
	}
	
	
}
