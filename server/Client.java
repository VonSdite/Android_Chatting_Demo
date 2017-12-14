package server;

import java.io.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {
	Socket client;
	boolean done;
	JButton sent;
	JTextArea chatContent;
	JTextField sentence;
	DataInputStream in = null;
	DataOutputStream out = null;

	public Client() {
		buildGUI("聊天室----客户机端");
		try {
			client = new Socket("localhost", 8888);
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			done = false;
			String line = null;
			while (!done) {
				while ((line = in.readUTF()) != null) {
					chatContent.append("对方：" + line + "\n");
					if (line.equals("bte")) {
						String msg = "服务器发来结束通信命令！\n";
						msg += "系统将在您确认此对话框的8秒钟后关闭，\n";
						JOptionPane.showMessageDialog(this, msg);
						Thread.sleep(8000);
						done = true;
						break;
					}
				}
				in.close();
				out.close();
				System.exit(0);
			}
		} catch (Exception e) {
			chatContent.append("服务器已关闭。。。\n");
		}
	}

	public void buildGUI(String title) {
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
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent e) {
		String str = sentence.getText();
		if (str != null && !str.equals("")) {
			chatContent.append("本人：" + str + "\n");
			try {
				out.writeUTF(sentence.getText());
			} catch (Exception e3) {
				chatContent.append("服务器没有启动...\n");
			}
		} else {
			chatContent.append("聊天信息不能为空\n");
		}
		sentence.setText("");
	}

	public static void main(String[] args) {
		new Client();
	}
}