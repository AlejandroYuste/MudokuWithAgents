import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color;
import java.io.*;

public class AgentNetworkController {
	
	String host;
	int port;
	int numAgents;
	int typeAgent;
	
	int conflictX;
	int conflictY;
	
	ThreadsInformation threadInfo;
	List<ThreadsInformation> ThreadsAgent = new ArrayList<ThreadsInformation>();
	
	Socket socket;
	
	AgentReader reader;
	Agent agent;
	Thread agentThread;
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
	
	int getPositionYConflict()
	{
		return conflictY;
	}
	
	int getPositionXConflict()
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
						

		for (int i=0; i<numAgents; i++)
        {
			//gameController.panelAgentsConnected.add(Color.blue);
	        gameController.panelAgentsConnected.add("Agent " + agentId);
	        
	        agent = new Agent(this, agentId, typeAgent);
	        agentThread = new Thread(agent);        
	        agentThread.start();
	        
	        ThreadsAgent.add(new ThreadsInformation(agentThread, agentId));
	        
	        agentId++;
        }
    }
	
	//#######################################    Set Value    ############################################
	
	static synchronized void setValue(int agentId, int agentType)
	{
		System.out.println("Enterm a setValue, Agent: " + agentId);
		
		try {		
			Thread.sleep(1500);			//Donam teps a que s'actualitzi el grid
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		int[][] actualGrid = getActualGrid();
		int i, j, val;
		ArrayList<Integer> options = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
		
		int[] emptyPosition;
		ArrayList<int[]> emptyPositionList = new ArrayList<int[]>();
		
		Random random = new Random(System.nanoTime());
		
		switch(agentType)
		{
			case(0):		//Agent que nomes treballa per files				
				for(i=0;i<getSudokuSize();i++)
				{
					for(j=0;j<getSudokuSize();j++)
					{
						if(actualGrid[j][i] != -1)
							options.add(actualGrid[j][i]);
						else
						{
							emptyPosition = new int[2];
							emptyPosition[0] = j;
							emptyPosition[1] = i;
							emptyPositionList.add(emptyPosition);
						}
					}
					listOptions.add(options);
					options = new ArrayList<Integer>();
				}
			
				System.out.println("SetValue. listOptions: " + listOptions);
			
				emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
				i = emptyPosition[0];
				j = emptyPosition[1];
				
				options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(getSudokuSize() + 1);
				while(options.contains(val))
				{
					val = random.nextInt(getSudokuSize() + 1);
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
				
			case(1):		//Agent que nomes treballa per Columnes
							
				for(j=0;j<getSudokuSize();j++)
				{
					for(i=0;i<getSudokuSize();i++)
					{
						if(actualGrid[j][i] != -1)
							options.add(actualGrid[j][i]);
						else
						{
							emptyPosition = new int[2];
							emptyPosition[0] = j;
							emptyPosition[1] = i;
							emptyPositionList.add(emptyPosition);
						}
					}
					listOptions.add(options);
					options = new ArrayList<Integer>();
				}
			
			
				emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
				i = emptyPosition[0];
				j = emptyPosition[1];
				
				options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(getSudokuSize() + 1);
				while(options.contains(val))
				{
					val = random.nextInt(getSudokuSize() + 1);
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
				
			case(2):		////Agent que nomes treballa per Quadrats
							
				int sizeSquare = (int) Math.sqrt(getSudokuSize());
				
				for (int row=0; row<getSudokuSize();row+=sizeSquare)
				{
					for (int column=0; column<getSudokuSize();column+=sizeSquare)
					{
						for(i=0;i<sizeSquare;i++)
						{
							for(j=0;j<sizeSquare;j++)
							{
								if(actualGrid[i + row][j + column] != -1)
									options.add(actualGrid[i + row][j + column]);
								else
								{
									emptyPosition = new int[2];
									emptyPosition[0] = i + row;
									emptyPosition[1] = j + column;
									emptyPositionList.add(emptyPosition);
								}
							}
						}
						listOptions.add(options);
						options = new ArrayList<Integer>();
					}
				}
							
				emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
				i = emptyPosition[0];
				j = emptyPosition[1];
				
				options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(getSudokuSize() + 1);
				while(options.contains(val))
				{
					val = random.nextInt(getSudokuSize() + 1);
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
		}
		
		try {		
			Thread.sleep(1500);			//Donam teps a que s'actualitzi el grid
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//#######################################    Check Grid    ############################################
	
	static synchronized void checkGrid(int agentId, int agentType)
	{
		System.out.println("Enterm a CheckGrid, Agent: " + agentId);
		
		Random random = new Random(System.nanoTime());
		
		int[][] actualGrid = getActualGrid();
		int i, j, val;
		ArrayList<Integer> options = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
		
		int[] position;
		ArrayList<int[]> optionsPosition = new ArrayList<int[]>();
		ArrayList<ArrayList<int[]>> listOptionsPosition = new ArrayList<ArrayList<int[]>>();
		
		switch(agentType)
		{
			case(0):		//Agent que nomes treballa per files
				
				System.out.println("Enterm al cas per Files Agent: " + agentId);
				
				for(i=0;i<getSudokuSize();i++)
				{
					for(j=0;j<getSudokuSize();j++)
					{
						if(actualGrid[i][j] != -1)
						{
							options.add(actualGrid[i][j]);
							position = new int[2];
							position[0] = i;
							position[1] = j;
							optionsPosition.add(position);
						}
					}
					listOptions.add(options);
					listOptionsPosition.add(optionsPosition);
					optionsPosition = new ArrayList<int[]>();
					options = new ArrayList<Integer>();
				}
			
				System.out.println("Agent --> listOptions: " + listOptions);
				System.out.println("Agent --> listOptionsPosition: " + listOptionsPosition);
				/*options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(controller.getSudokuSize());
				while(options.contains(val))
				{
					val = random.nextInt(controller.getSudokuSize());
				}*/
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				//sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
				
			case(1):		//Agent que nomes treballa per Columnes
				
				System.out.println("Enterm al cas per Columnes");
			
				for(j=0;j<getSudokuSize();j++)
				{
					for(i=0;i<getSudokuSize();i++)
					{
						if(actualGrid[j][i] != -1)
							options.add(actualGrid[j][i]);
					}
					listOptions.add(options);
					options = new ArrayList<Integer>();
				}
				
				//options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(getSudokuSize());
				while(options.contains(val))
				{
					val = random.nextInt(getSudokuSize());
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				//sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
				
			case(2):		////Agent que nomes treballa per Quadrats
				
				System.out.println("Enterm al cas per Quadrats");
			
				int sizeSquare = (int) Math.sqrt(getSudokuSize());
				
				for (int row=0; row<getSudokuSize();row+=sizeSquare)
				{
					for (int column=0; column<getSudokuSize();column+=sizeSquare)
					{
						for(i=0;i<sizeSquare;i++)
						{
							for(j=0;j<sizeSquare;j++)
							{
								if(actualGrid[i + row][j + column] != -1)
									options.add(actualGrid[i + row][j + column]);
							}
						}
						listOptions.add(options);
						options = new ArrayList<Integer>();
					}
				}
				
				//options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(getSudokuSize());
				while(options.contains(val))
				{
					val = random.nextInt(getSudokuSize());
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				//sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
		}
		
	}
}
