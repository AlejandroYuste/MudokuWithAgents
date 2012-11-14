import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

public class AgentNetworkController {
	
	String host;
	int port;
	int numAgents;
	int typeAgent;
	
	static int conflictX;
	static int conflictY;
	
	Socket socket;
	
	Agent agent;
	AgentReader reader;
	Thread readerThread;
	
	private int agentId = 1;
	
    static AgentGameController gameController;
    
    static PrintStream writer;

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
	
	public static void SendMessage(String message)
	{
		writer.println(message);
	}
	
	public static int[][] getActualGrid()
	{
		return gameController.getActualGrid();
	}
	
	static int getSudokuSize()
	{
		return gameController.sudokuSize;
	}
	
	int getNumAgentsConnected()
	{
		return gameController.panelAgentsConnected.getItemCount();
	}
	
	public boolean getConflictExists()
	{
		return gameController.getConflictExists();
	}
	
	void setPositionConflic(int conflictX_, int conflictY_)
	{
		conflictX = conflictX_;
		conflictY = conflictY_;
	}
	
	static int getPositionYConflict()
	{
		return conflictY;
	}
	
	void stopExecuting(int agentId)
	{
		agent.stopExecuting(agentId);
	}
	
	int getRegion(int x, int y)
	{
		return gameController.getRegion(x, y);
	}
	
	static int getPositionXConflict()
	{
		return conflictX;
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
					
		String stringAgentType = new String();
		
		switch(typeAgent)
		{
			case 0:
				stringAgentType = "Contributor by Rows";
				break;
			case 1:
				stringAgentType = "Contributor by Columns";
				break;
			case 2:
				stringAgentType = "Contributor by Squares";
				break;
			case 3:
				stringAgentType = "Tester by Rows";
				break;
			case 4:
				stringAgentType = "Tester by Columns";
				break;
			case 5:
				stringAgentType = "Terse by Squares";
				break;
			case 6:
				stringAgentType = "Comitter by Rows";
				break;
			case 7:
				stringAgentType = "Comitter by Columns";
				break;
			case 8:
				stringAgentType = "Comitter by Squares";
				break;
			case 9:
				stringAgentType = "Project Manager";
				break;
		}

		for (int i=0; i<numAgents; i++)
        {
	        gameController.panelAgentsConnected.add("Agent [" + agentId + "] --> " + stringAgentType);
	        
	        agent = new Agent(this, agentId, typeAgent);
	        agent.executarAgents(agentId, typeAgent);
	        
	        agentId++;
        }
    }

}
