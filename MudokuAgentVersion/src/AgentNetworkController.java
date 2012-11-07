

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

public class AgentNetworkController {
	
	String host;
	int port;
	int numAgents;
	int typeAgents;
	
	Socket socket;
	
	Agent agent;
	Thread readerThread;
	
	public int clientId = -1;
	
    AgentGameController gameController;
    
    PrintStream writer;

	public AgentNetworkController(AgentGameController gameController_)
	{
		gameController = gameController_;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		socket.close();
	}
	
	public void MessageReceived(String message)
	{
		gameController.MessageReceived(message);
	}
	
	public void SendMessage(String message)
	{
		writer.println(message);
	}
	
	public int[][] getActualGrid()
	{
		return gameController.getActualGrid();
	}
	
	public void Connect(String host_, int port_, int numAgents_, int typeAgents_) throws UnknownHostException, IOException
	{
		host = host_;
		port = port_;
		numAgents = numAgents_;
		typeAgents = typeAgents_;
		
		//System.out.println("ClientNetworkController --> Connect, host: " + host);
		//System.out.println("ClientNetworkController --> Connect, port: " + port);
		//System.out.println("ClientNetworkController --> Connect, numAgents: " + numAgents);
		//System.out.println("ClientNetworkController --> Connect, typeAgents: " + typeAgents);
				
		for (int i=0; i<numAgents; i++)
		{
			socket = new Socket(host, port);
	        agent = new Agent(socket.getInputStream(), this, typeAgents, clientId);
	        clientId++;
	        
	        readerThread = new Thread(agent);        
	        readerThread.start();
	        
	        writer = new PrintStream(socket.getOutputStream());		//Aqui pot haver-hi errors.
		}
    }
	
}
