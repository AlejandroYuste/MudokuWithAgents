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
	protected void finalize() throws Throwable 
	{
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
	
	void createRandomCommunity(int numRandomAgents)
	{
		if (numRandomAgents >= 13)
		{
			int countAgents = numRandomAgents;
			int numContributors = 0, numReporters = 0, numTesters = 0, numCommitters = 0, numLeaders = 0;
			int val, index, typeAgent = -1;
			int[] numAgentsToAdd = new int[13];
			
			for(int i=0; i<numAgentsToAdd.length; i++)
			{
				numAgentsToAdd[i] = 1;
				countAgents--;
			}
			
			/*	numAgentsToAdd Index:
			 *  0 --> Contributor by Rows
			 *  1 --> Contributor by Columns
			 *  2 --> Contributor by Squares
			 *  3 --> Reporter by Rows
			 *  4 --> Reporter by Columns
			 *  5 --> Reporter by Squares
			 *  6 --> Tester by Rows
			 *  7 --> Tester by Columns
			 *  8 --> Tester by Squares
			 *  9 --> Committer by Rows
			 *  10 --> Committer by Columns
			 *  11 --> Committer by Squares
			 *  12 --> Leaders
			 */
			
			Random random = new Random(System.nanoTime());
		
			/*Proportion of Agents:		(Random Community)
			 * Contributors -> 45%
			 * BugReporterrs -> 15%
			 * Testers -> 15%
			 * Committers -> 20%
			 * Leaders -> 5%
			 */
			
			while (countAgents>0)
			{
				val = random.nextInt(99) + 1;
				
				if(val <= 45)
					numContributors++;
				else if (val <= 60)
					numReporters++;
				else if (val <= 75)
					numTesters++;
				else if (val <= 95)
					numCommitters++;
				else
					numLeaders++;
					
				countAgents--;
			}
						
			for(int i=0; i<numContributors; i++)
			{
				index = random.nextInt(3);
				numAgentsToAdd[index]++;
			}
			for(int i=0; i<numReporters; i++)
			{
				index = random.nextInt(3);
				numAgentsToAdd[index + 3]++;
			}	
			for(int i=0; i<numTesters; i++)
			{
				index = random.nextInt(3);
				numAgentsToAdd[index + 6]++;
			}	
			for(int i=0; i<numCommitters; i++)
			{
				index = random.nextInt(3);
				numAgentsToAdd[index + 9]++;
			}
			for(int i=0; i<numLeaders; i++)
			{
				numAgentsToAdd[12]++;
			}
			
			for(int i=0; i<numAgentsToAdd.length; i++)
			{
				typeAgent = getTypeAgent(i);
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
		
		stringAgentType = gameController.getLabelTypeAgent(typeAgent);

		for (int i=0; i<numAgents; i++)
        {
	        gameController.listPanel.add("Agent [" + agentId + "] --> " + stringAgentType);
	        
	        agent = new Agent(this, agentId, typeAgent);
	        agent.executarAgents(agentId, typeAgent);
	        
	        SendMessage("connect#" + agentId + "," + typeAgent);
	        
	        agentId++;
        }
    }
	
	int getCellState(int x, int y)
	{
		return gameController.getCellState(x, y);
	}
	
	void pauseExecution()
	{
		agent.pauseExecution();
	}
	
	void playExecution()
	{
		agent.playExecution();
	}
	
	int getTypeAgent(int indexType)
	{
		int typeAgent = -1;
		
		switch(indexType)
		{
			case 0:	
				typeAgent = 0;		//Contributor by Rows
				break;
			case 1:
				typeAgent = 1;		//Contributor by Columns
				break;
			case 2:
				typeAgent = 2;		//Contributor by Squares
				break;
			case 3:
				typeAgent = 4;		//Reporter by Rows
				break;
			case 4:
				typeAgent = 5;		//Reporter by Columns
				break;
			case 5:
				typeAgent = 6;		//Reporter by Squares
				break;
			case 6:
				typeAgent = 8;		//Tester by Rows
				break;
			case 7:
				typeAgent = 9;		//Tester by Columns
				break;
			case 8:
				typeAgent = 10;		//Tester by Squares
				break;
			case 9:
				typeAgent = 12;		//Committer by Rows
				break;
			case 10:
				typeAgent = 13;		//Committer by Columns
				break;
			case 11:
				typeAgent = 14;		//Committer by Squares
				break;
			case 12:
				typeAgent = 16;		//Leader
				break;
		}
		
		return typeAgent;
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
}
