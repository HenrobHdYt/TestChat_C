import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import javax.management.Notification;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class Client {
	
	JFrame clientFrame;
	JPanel clientPanel;
	JTextArea textAreaMessage;
	JTextField textField_ClientMessage;
	JButton sendButton;
	JTextField textField_UserName;
	JCheckBox noti;
	
	Socket client;
	PrintWriter writer;
	BufferedReader reader;
	
	public static void main(String[] args) {
		Client c = new Client();
		c.createGUI();
	}
	
	public void createGUI() {
		clientFrame = new JFrame("Lehmann Chat");
		clientFrame.setSize(800, 600);
		
		clientPanel = new JPanel();
		
		textAreaMessage = new JTextArea();
		textAreaMessage.setEditable(false);
		
		noti = new JCheckBox("Notifications");
		
		
		textField_ClientMessage = new JTextField(38);
		textField_ClientMessage.addKeyListener(new SendPressEnterListener());
		
		sendButton = new JButton("Senden");
		sendButton.addActionListener(new SendButtonListener());
		
		textField_UserName = new JTextField(10);
		
		//Srollbar
		JScrollPane scrollMessage = new JScrollPane(textAreaMessage);
		scrollMessage.setPreferredSize(new Dimension(700,500));
		scrollMessage.setMinimumSize(new Dimension(700, 500));
		scrollMessage.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollMessage.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		if(!connectToServer()) {
			//Nichtzw
		}
		
		Thread t = new Thread(new MessageFromServerListener());
		t.start();
		
		clientPanel.add(scrollMessage);
		clientPanel.add(noti);
		clientPanel.add(textField_UserName);
		clientPanel.add(textField_ClientMessage);
		clientPanel.add(sendButton);
		
		clientFrame.getContentPane().add(BorderLayout.CENTER,clientPanel);
		
		clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientFrame.setVisible(true);
		
	}
	
	public boolean connectToServer() {
		try {
			client = new Socket("192.168.178.89", 1231);
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream());
			appendToTextMessage("Netzwerkverbindung hergestellt");
			int Slive = 1;
			
			return false;
			
		} catch (Exception e) {
			appendToTextMessage("Verbindung konnte leider nicht hergestellt werden");
			e.printStackTrace();
			
			return false;
		}
	}
	
	public void sendMessageToServer() {
		writer.println(textField_UserName.getText() + ": " + textField_ClientMessage.getText());
		writer.flush();
		
		textField_ClientMessage.setText("");
		textField_ClientMessage.requestFocus();
	}
	
	public void appendToTextMessage(String message) {
		textAreaMessage.append(message + "\n");
		
	}
	
	public class SendPressEnterListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				sendMessageToServer();
			}
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			
			
		}
		
	}
	
	public class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			sendMessageToServer();
			
		}
		
	}
	
	public class MessageFromServerListener implements Runnable {

		@Override
		public void run() {
			String message;
			
			try { 
				while((message = reader.readLine()) !=null) {
					appendToTextMessage(message);
					TestNotifications Test = new TestNotifications();
					Test.TestNotifications();
				}
			} catch (Exception e) {
				appendToTextMessage("Nachricht konnte nicht gesendet werden");
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
}
