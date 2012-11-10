import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class AgentNetworkController {
	
	String host;
	int port;
	int numAgents;
	int typeAgent;
	
	ThreadsInformation threadInfo;
	List<ThreadsInformation> ThreadsAgent = new ArrayList<ThreadsInformation>();
	
	Socket socket;
	
	AgentReader reader;
	Agent agent;
	Thread agentThread;
	Thread readerThread;
	
	private int agentId = 1;
	
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
	
	@SuppressWarnings("deprecation")
	void stopExecuting(int agentId_)
	{
		for(int i=0; i<ThreadsAgent.size();i++)
		{
			threadInfo = (ThreadsInformation) ThreadsAgent.get(i);
			if(threadInfo.getAgentId() == agentId_)
			{
				Thread thrInf = threadInfo.getThread();
				thrInf.stop();
				ThreadsAgent.remove(threadInfo);
			}
		}
		System.out.println("Agent --> Acabant el Thread del Agent: " + agentId_);
	}
	
	int getSudokuSize()
	{
		return gameController.sudokuSize;
	}
	
	public void Connect(String host_, int port_) throws UnknownHostException, IOException
	{
		host = host_;
		port = port_;

		socket = new Socket(host, port);
		reader = new AgentReader(socket.getInputStream(), this);

        readerThread = new Thread(reader);        
        readerThread.start();		
		
		writer = new PrintStream(socket.getOutputStream());
    }
	
	public void Connect(int numAgents_, int typeAgents_)
	{
		numAgents = numAgents_;
		typeAgent = typeAgents_;
						

		for (int i=0; i<numAgents; i++)
        {
	        gameController.panelAgentsConnected.add("Agent " + agentId);
	        
	        agent = new Agent(this, agentId, typeAgent);
	        agentThread = new Thread(agent);        
	        agentThread.start();
	        
	        ThreadsAgent.add(new ThreadsInformation(agentThread, agentId));
	        
	        agentId++;
        }
    }
	
}
