package chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Server extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;//display message
	private JButton send = new JButton("SEND");
	private JPanel panel = new JPanel();
	private ObjectOutputStream output; //connected by stream£¬ from you to user
	private ObjectInputStream input; // get info from user
    private ServerSocket server;
    private Socket connection;
    
    //constructor
    public Server(){
    	super("Aaron Instant Messager");//call the parent's constructor
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
    	add(panel,BorderLayout.SOUTH);
    	chatWindow = new JTextArea();
    	add(new JScrollPane(chatWindow));
    	
    	
    	setSize(300,150);
    	setVisible(true);
    }
    
    //set up and run the server
   
    public void startRunning(){
    	try{
    		server = new ServerSocket(6789, 100);//6789 is testing port, and 100 is the max num of chatting people
    		while(true){
    			try{
    				//connection and start to chat
    				waitForConnection();
    				setupStreams();// output and input stream
    				whileChatting();// start chatting 
    			}catch(EOFException eofException){
    				showMessage("\n Server ended the connection!");
    			}finally{
    				closeCrap();
    			}
    		}
    	}catch(IOException ioException){
    		ioException.printStackTrace();
    	}
    	
    }
    
    //wait for connection, the display connection information
    private void waitForConnection() throws IOException{
    	showMessage("Waiting for the connection... \n");
    	connection = server.accept(); // socket be created
    	showMessage( "Now connected to " + connection.getInetAddress().getHostName());
    	
    }
    
    // get stream to send and receive data
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
    	output.flush();
    	input = new ObjectInputStream(connection.getInputStream());
    	showMessage("\n Streams are set up! \n");
    }
    
    //during chatting
    private void whileChatting() throws IOException{
    	String message = "You are now connected! ";
    	sendMessage(message);
    	ableToType(true);
    	do{
    		//have a conversation
    		try{
    			message = (String) input.readObject();//read message from user
    			showMessage("\n" + message);
    		}catch(ClassNotFoundException classNotFoundException){
    			showMessage("\n idk wtf that user send!");//some wired message cannot display
    		}
    	}while(!message.equals("CLIENT - END"));
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
    
    //send a message to client
    private void sendMessage(String message){
        try{
         	output.writeObject("SERVER - " + message);
         	output.flush();
         	showMessage("\nSERVER - " + message);
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