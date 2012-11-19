import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.io.*;

public class AgentNetworkController 
{
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
	
	public static int[][] getActualState()
	{
		return gameController.getActualState();
	}
	
	static int getSudokuSize()
	{
		return GameController.sudokuSize;
	}
	
	int getNumAgentsConnected()
	{
		return gameController.listPanel.getItemCount();
	}
	
	boolean getConflictExists()
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
	
	void createRandomCommunity(int numRandomAgents)
	{
		if (numRandomAgents >= 10)
		{
			int countAgents = numRandomAgents;
			int[] numAgentsToAdd = new int[10];
			
			for(int i=0; i<numAgentsToAdd.length; i++)
			{
				numAgentsToAdd[i] = 1;
				countAgents--;
			}
			
			/*Proportion of Agents:
			 * Contributors -> 50%
			 * Testers -> 30%
			 * Committers -> 20%
			 */
			
			int numContributors = (int) Math.round(countAgents*0.5);
			countAgents -= numContributors;
			int numTesters = (int) Math.round(countAgents*0.3);
			countAgents -= numTesters;
			//int numCommitters = (int) Math.round(countAgents*0.2);
			int numCommitters = countAgents;
			countAgents -= numCommitters;
			
			Random random = new Random(System.nanoTime());
			int index;
			int typeAgent = -1;
			
			for(int i=0; i<numContributors; i++)
			{
				index = random.nextInt(3);
				numAgentsToAdd[index]++;
			}
			for(int i=0; i<numTesters; i++)
			{
				index = random.nextInt(3) + 3;
				numAgentsToAdd[index]++;
			}	
			for(int i=0; i<numCommitters; i++)
			{
				index = random.nextInt(3) + 6;
				numAgentsToAdd[index]++;
			}
			
			
			for(int i=0; i<numAgentsToAdd.length; i++)
			{
				switch(i)
				{
					case 0:
						typeAgent = 0;
						break;
					case 1:
						typeAgent = 1;
						break;
					case 2:
						typeAgent = 2;
						break;
					case 3:
						typeAgent = 4;
						break;
					case 4:
						typeAgent = 5;
						break;
					case 5:
						typeAgent = 6;
						break;
					case 6:
						typeAgent = 8;
						break;
					case 7:
						typeAgent = 9;
						break;
					case 8:
						typeAgent = 10;
						break;
					case 9:
						typeAgent = 12;
						break;
				}
				
				Connect(numAgentsToAdd[i], typeAgent);
			}
		}
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
			case 4:
				stringAgentType = "Tester by Rows";
				break;
			case 5:
				stringAgentType = "Tester by Columns";
				break;
			case 6:
				stringAgentType = "Tester by Squares";
				break;
			case 8:
				stringAgentType = "Comitter by Rows";
				break;
			case 9:
				stringAgentType = "Comitter by Columns";
				break;
			case 10:
				stringAgentType = "Comitter by Squares";
				break;
			case 12:
				stringAgentType = "Project Leader";
				break;
		}

		for (int i=0; i<numAgents; i++)
        {
	        gameController.listPanel.add("Agent [" + agentId + "] --> " + stringAgentType);
	        
	        agent = new Agent(this, agentId, typeAgent);
	        agent.executarAgents(agentId, typeAgent);
	        
	        agentId++;
        }
    }

}
