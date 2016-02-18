package chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Client extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;//display message
	private JButton send = new JButton("SEND");
	private JPanel panel = new JPanel();
	private ObjectOutputStream output; //connected by stream£¬ from you to user
	private ObjectInputStream input; // get info from user
    private String message = "";
    private String IP;
    private Socket connection;
    
    //constructor
    public Client(String host){
    	super("Client Program!");
    	IP = host;
    	userText = new JTextField();
    	userText.setEditable(false);// before connected, cannot type anything
    	send.addActionListener(
        		new ActionListener(){
        			public void actionPerformed(ActionEvent event){
        				sendMessage(userText.getText());
        				userText.setText("");//set command back to empty
        				
        			}
        	    }   			
        	);  
    	
    	panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
    	panel.add(userText);
    	panel.add(send);
    	add(panel, BorderLayout.SOUTH);
    	chatWindow = new JTextArea();
    	add(new JScrollPane(chatWindow));
    	setSize(300,150);
    	setVisible(true);
    }
    
    //connect to server
    public void startRunning(){
    	try{
			//connection and start to chat
			connectToServer();
			setupStreams();// output and input stream
			whileChatting();// start chatting 
		}catch(EOFException eofException){
			showMessage("\n Client terminated connection!");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeCrap();
		}
    }
    
    //connect to server
    private void connectToServer() throws IOException{
    	showMessage("Attempting connectiong¡£¡£¡£ \n");
    	connection = new Socket(InetAddress.getByName(IP),6789);
    	showMessage("Connected to "+connection.getInetAddress().getHostName());
    }
    
    //set up output and input streams 
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
    	output.flush();
    	input = new ObjectInputStream(connection.getInputStream());
    	showMessage("\n Streams are set up! \n");
    }
    
    //during chatting
    private void whileChatting() throws IOException{
    	ableToType(true);
    	do{
    		//have a conversation
    		try{
    			message = (String) input.readObject();//read message from user
    			showMessage("\n" + message);
    		}catch(ClassNotFoundException classNotFoundException){
    			showMessage("\n Unkown Object!");//some wired message cannot display
    		}
    	}while(!message.equals("SERVER - END"));
    }
    
    //close streams and sockets after chatting
    private void closeCrap(){
    	showMessage("\n Closing connection... \n");
    	ableToType(false);
    	try{
    		output.close();
    		input.close();
    		connection.close();	
    	}catch(IOException ioException){
    		ioException.printStackTrace();
    	}
    }
    
    //send a message to server
    private void sendMessage(String message){
        try{
         	output.writeObject("CLIENT - " + message);
         	output.flush();
         	showMessage("\nCLIENT - " + message);
        }catch(IOException ioException){
            chatWindow.append("\n ERROR: CANT SEND MESSAGE");	
        }
    }
    
  //updates chatWindow
    private void showMessage(final String text){
    	SwingUtilities.invokeLater(
    		new Runnable(){
    			public void run(){
    				chatWindow.append(text);
    			}
    		}	   	     		
    	);
    }
    
    //type flip 
    private void ableToType(final boolean tof){
    	SwingUtilities.invokeLater(
        	new Runnable(){
        		public void run(){
        		    userText.setEditable(tof);
        		}
        	}	   	     		
        );
    	
    }
}
