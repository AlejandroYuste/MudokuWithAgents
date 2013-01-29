

/*************************************************************************
 *  Compilation:  javac EchoClient.java In.java Out.java
 *  Execution:    java EchoClient name host
 *
 *  Connects to host server on port 4444, sends text, and prints out
 *  whatever the server sends back.
 *
 *  
 *  % java EchoClient wayne localhost
 *  Connected to localhost on port 4444
 *  this is a test
 *  [wayne]: this is a test
 *  it works
 *  [wayne]: it works
 *  <Ctrl-d>                 
 *  Closing connection to localhost
 *
 *  Windows users: replace <Ctrl-d> with <Ctrl-z>
 *  
 *************************************************************************/

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class ClientNetworkController {
	
	String host;
	int port;
	Socket socket;
	
	ClientReader reader;
	Thread readerThread;
	PrintStream writer;
	
    public int clientId;
    public String userName;
    ClientGameController gameController;

	public ClientNetworkController(ClientGameController gameController_)
	{
		gameController = gameController_;
		clientId = -1;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		socket.close();
	}
	
	public void Connect(String host_, int port_, String userName_) throws UnknownHostException, IOException
	{
		host = host_;
		port = port_;
		userName = userName_;
		
		System.out.println("Connecting to server");
        socket = new Socket(host, port);
        
        reader = new ClientReader(socket.getInputStream(), this);
        readerThread = new Thread(reader);
        readerThread.start(); 
        
        writer = new PrintStream(socket.getOutputStream());
        
        SendMessage("connect#" + gameController.clientType + "," + userName);
	}
	
	public void SendMessage(String message)
	{
		System.out.println("Client " + clientId  + " : Sending message to server " + message);
		writer.println(message);
	}
	
	public void MessageReceived(String message)
	{
		gameController.MessageReceived(message);
	}
}
