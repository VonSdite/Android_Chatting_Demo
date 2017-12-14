package server;

import java.io.*;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server extends JFrame implements ActionListener {
	ServerSocket server;// ���������׽���
	Socket theClient;// ��ͻ���ͨ�ŵ��׽���
	boolean done;// ͨ���Ƿ����
	JButton sent;// ���Ͱ�ť
	JTextArea chatContent;// ����������
	JTextField sentence;// ������Ϣ��
	DataInputStream in = null;// ���Կͻ��˵�������
	DataOutputStream out = null;// ���͵��ͻ��˵������

	public Server() {
		buildGUI("������----��������");
		try {
			server = new ServerSocket(8888);// �����������׽��ֶ���
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
						chatContent.append("�Է���" + line + "\n");
					}
					in.close();
					out.close();
					theClient.close();
				}
			} catch (Exception e1) {
				chatContent.append("�Է���" + theClient.getInetAddress() + "���Ѿ��뿪������\n");
			}

		}
	}

	public void buildGUI(String title)// ����ͼ�ν���
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
		sent = new JButton("����");
		bottomPanel.add(new JLabel("������Ϣ"));
		bottomPanel.add(sentence);
		bottomPanel.add(sent);
		container.add(bottomPanel, BorderLayout.SOUTH);
		sent.addActionListener(this);
		sentence.addActionListener(this);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter()// �����ڲ��ڼ������ڹرղ���
		{
			public void windowClosing(WindowEvent e) {
				try {
					out.writeUTF("bye");
				} catch (IOException e2) {
					System.out.println("�������˴��ڹرա�����");

				} finally {
					System.exit(0);
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		String str = sentence.getText();// ��ȡ������Ϣ������������
		if (str != null && !str.equals(""))// ����������ݲ�Ϊ�գ�������Ϣ
		{
			chatContent.append("���ˣ�" + str + "\n");
			try {
				out.writeUTF(str);
			} catch (Exception e3) {
				chatContent.append("�ͻ��˲�����...\n");
			}
		} else {
			chatContent.append("������Ϣ����Ϊ��\n");
		}
		sentence.setText("");// ���������Ϣ��������
	}

	public static void main(String[] args) {
		new Server();
	}
}