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
		buildGUI("������----�ͻ�����");
		try {
			client = new Socket("localhost", 8888);
			out = new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			done = false;
			String line = null;
			while (!done) {
				while ((line = in.readUTF()) != null) {
					chatContent.append("�Է���" + line + "\n");
					if (line.equals("bte")) {
						String msg = "��������������ͨ�����\n";
						msg += "ϵͳ������ȷ�ϴ˶Ի����8���Ӻ�رգ�\n";
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
			chatContent.append("�������ѹرա�����\n");
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
		sent = new JButton("����");
		bottomPanel.add(new JLabel("������Ϣ"));
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
			chatContent.append("���ˣ�" + str + "\n");
			try {
				out.writeUTF(sentence.getText());
			} catch (Exception e3) {
				chatContent.append("������û������...\n");
			}
		} else {
			chatContent.append("������Ϣ����Ϊ��\n");
		}
		sentence.setText("");
	}

	public static void main(String[] args) {
		new Client();
	}
}