package server;

import java.io.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server extends JFrame implements ActionListener {
	ServerSocket server;// 服务器端套接字
	Socket theClient;// 与客户端通信的套接字
	boolean done;// 通信是否结束
	JButton sent;// 发送按钮
	JTextArea chatContent;// 聊天内容区
	JTextField sentence;// 聊天信息栏
	DataInputStream in = null;// 来自客户端的输入流
	DataOutputStream out = null;// 发送到客户端的输出流

	public Server() {
		buildGUI("聊天室----服务器端");
		try {
			server = new ServerSocket(8888);// 创建服务器套接字对象
		} catch (IOException e) {
			System.out.println(e);
		}

		while (true) {
			try {
				theClient = server.accept();
				out = new DataOutputStream(theClient.getOutputStream());
				in = new DataInputStream(theClient.getInputStream());
				done = true;
				String line = null;
				while (done) {
					while ((line = in.readUTF()) != null) {
						chatContent.append("对方：" + line + "\n");
					}
					in.close();
					out.close();
					theClient.close();
				}
			} catch (Exception e1) {
				chatContent.append("对方（" + theClient.getInetAddress() + "）已经离开聊天室\n");
			}

		}
	}

	public void buildGUI(String title)// 构造图形界面
	{
		this.setTitle(title);
		this.setSize(400, 300);
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		JScrollPane centerPane = new JScrollPane();
		chatContent = new JTextArea();
		centerPane.setViewportView(chatContent);
		container.add(centerPane, BorderLayout.CENTER);
		chatContent.setEditable(false);
		JPanel bottomPanel = new JPanel();
		sentence = new JTextField(20);
		sent = new JButton("发送");
		bottomPanel.add(new JLabel("聊天信息"));
		bottomPanel.add(sentence);
		bottomPanel.add(sent);
		container.add(bottomPanel, BorderLayout.SOUTH);
		sent.addActionListener(this);
		sentence.addActionListener(this);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter()// 匿名内部内监听窗口关闭操作
		{
			public void windowClosing(WindowEvent e) {
				try {
					out.writeUTF("bye");
				} catch (IOException e2) {
					System.out.println("服务器端窗口关闭。。。");

				} finally {
					System.exit(0);
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		String str = sentence.getText();// 获取聊天信息栏的聊天内容
		if (str != null && !str.equals(""))// 如果聊天内容不为空，则发送信息
		{
			chatContent.append("本人：" + str + "\n");
			try {
				out.writeUTF(str);
			} catch (Exception e3) {
				chatContent.append("客户端不存在...\n");
			}
		} else {
			chatContent.append("聊天信息不能为空\n");
		}
		sentence.setText("");// 清空聊天信息栏的内容
	}

	public static void main(String[] args) {
		new Server();
	}
}