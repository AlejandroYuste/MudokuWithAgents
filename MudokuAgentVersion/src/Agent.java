import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class Agent 
{

	int agentId;
	int agentType;
	
	AgentContributor agentContributor;
	Thread agentContributorThread;
	
	AgentBugReporter agentBugReporter;
	Thread agentBugReporterThread;
	
	AgentTester agentTester;
	Thread agentTesterThread;
	
	AgentCommitter agentCommitter;
	Thread agentCommitterThread;
	
	AgentLeader agentLeader;
	Thread agentLeaderThread;
	
	ThreadsInformation threadInfo;
	static List<ThreadsInformation> ThreadsAgent = new ArrayList<ThreadsInformation>();
	
	static AgentNetworkController controller;
	
	int[][] actualGrid;
		
	static boolean modifyGrid = false;
	static boolean alreadyVoted = false;
	
	Agent(AgentNetworkController controller_, int agentId_, int agentType_)
	{
		agentId = agentId_;
		agentType = agentType_;
		controller = controller_;
	}
	
	int getNumAgentsConnected()
	{
		return controller.getNumAgentsConnected();
	}
	
	static boolean getConflictExists()
	{
		return controller.getConflictExists();
	}
	
	static void SendMessage(String message)
	{
		AgentNetworkController.SendMessage(message);
	}
	
	static ThreadsInformation getThreadInformationList(int index)
	{
		return ThreadsAgent.get(index);
	}
	
	static int getAgentListSize()
	{
		return ThreadsAgent.size();
	}
	
	@SuppressWarnings("deprecation")
	void pauseExecution()
	{
		for(ThreadsInformation threadInfo : ThreadsAgent)
			threadInfo.threadInfo.suspend();
	}
	
	@SuppressWarnings("deprecation")
	void playExecution()
	{
		for(ThreadsInformation threadInfo : ThreadsAgent)
			threadInfo.threadInfo.resume();
	}
	
	@SuppressWarnings("deprecation")
	void stopExecuting(int agentId_)
	{
		for(ThreadsInformation threadInfo : ThreadsAgent)
		{
			if(threadInfo.agentId == agentId_) {
				threadInfo.threadInfo.stop();
				SendMessage("disconnect#" + threadInfo.agentId + "," + threadInfo.agentType);
			}
		}
		

	}
	
	void executarAgents(int agentId, int agentType) 
	{
		System.out.println("Agent --> Executant el Thread del Agent: " + agentId + " i tipus: " + agentType);
		
		switch(agentType)
		{
			case GameController.agentContributorByRows: case GameController.agentContributorByColumns: case GameController.agentContributorBySquares:
				agentContributor = new AgentContributor(this, agentId, agentType);
				agentContributorThread = new Thread(agentContributor);        
				agentContributorThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentContributorThread, agentId, agentType)); 
				break;
			case GameController.agentBugReporterByRows: case GameController.agentBugReporterByColumns: case GameController.agentBugReporterBySquares:
				agentBugReporter = new AgentBugReporter(this, agentId, agentType);
				agentBugReporterThread = new Thread(agentBugReporter);        
				agentBugReporterThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentBugReporterThread, agentId, agentType)); 
				break;
			case GameController.agentTesterByRows: case GameController.agentTesterByColumns: case GameController.agentTesterBySquares:
				agentTester = new AgentTester(this, agentId, agentType);
				agentTesterThread = new Thread(agentTester);        
				agentTesterThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentTesterThread, agentId, agentType)); 
				break;
			case GameController.agentCommitterByRows: case GameController.agentCommitterByColumns: case GameController.agentCommitterBySquares:
				agentCommitter = new AgentCommitter(this, agentId, agentType);
				agentCommitterThread = new Thread(agentCommitter);        
				agentCommitterThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentCommitterThread, agentId, agentType)); 
				break;
			case GameController.agentLeader:
				agentLeader = new AgentLeader(this, agentId, agentType);
				agentLeaderThread = new Thread(agentLeader);        
				agentLeaderThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentLeaderThread, agentId, agentType)); 
				break;
		}
	}	
		
	//#######################################    Set Value    ############################################
	
		static synchronized void setValue(int agentId, int agentType)
		{
			
			try {		
				Thread.sleep(500);			//Donam teps a que s'actualitzi el grid
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(!getConflictExists())
			{
				
				int[][] actualGrid = AgentNetworkController.getActualGrid();
				int[][] actualState = AgentNetworkController.getActualState();
				
				/*
				public final static int waitingValue = 0;
				public final static int intializedByServer = 1;
				public final static int contributedByRows = 2;
				public final static int contributedByColumns = 3;
				public final static int contributedBySquares = 4;
				public final static int contributedByUser = 5;
				public final static int reportedByRows = 6;
				public final static int reportedByColumns = 7;
				public final static int reportedBySquares = 8;	
				public final static int reportedByUser = 9;	
				public final static int testedByRows = 10;	
				public final static int testedByColumns = 11;	
				public final static int testedBySquares = 12;	
				public final static int testedByUser = 13;	
				public final static int acceptedByAgent = 14;
				public final static int acceptedByUser = 15;	
				public final static int rejectedByAgent = 16;	
				public final static int rejectedByUser = 17;	*/
				
				int i, j, val;
				ArrayList<Integer> options = new ArrayList<Integer>();
				ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
				
				int[] emptyPosition;
				ArrayList<int[]> emptyPositionList = new ArrayList<int[]>();
				
				Random random = new Random(System.nanoTime());
				
				switch(agentType)
				{
					case GameController.agentContributorByRows:		//Agent que nomes treballa per files		
						
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							for(i=0;i<AgentNetworkController.getSudokuSize();i++)
							{								
								if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
								   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
								   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
								   actualState[i][j] != GameController.rejectedByUser)
								{
									options.add(actualGrid[i][j]);
								}
								else
								{
									emptyPosition = new int[2];
									emptyPosition[0] = i;
									emptyPosition[1] = j;
									emptyPositionList.add(emptyPosition);
								}
							}
							
							listOptions.add(options);
							options = new ArrayList<Integer>();
						}
					
						if(!emptyPositionList.isEmpty())
						{
							emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
							i = emptyPosition[0];
							j = emptyPosition[1];
													
							options = listOptions.get(j);	//Obtenim les opcions que tenim per aquella fila
							val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							
							while(options.contains(val))
							{
								val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							}
							
							SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
						}
						
						break;
						
					case GameController.agentContributorByColumns:		//Agent que nomes treballa per Columnes
									
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							for(j=0;j<AgentNetworkController.getSudokuSize();j++)
							{
								if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
								   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
								   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
								   actualState[i][j] != GameController.rejectedByUser)
								{
									options.add(actualGrid[i][j]);
								}
								else
								{
									emptyPosition = new int[2];
									emptyPosition[0] = i;
									emptyPosition[1] = j;
									emptyPositionList.add(emptyPosition);
								}
							}
							
							listOptions.add(options);
							options = new ArrayList<Integer>();
							
						}
					
						if(!emptyPositionList.isEmpty())
						{
							emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
							i = emptyPosition[0];
							j = emptyPosition[1];
													
							options = listOptions.get(i);			//Obtenim les opcions que tenim per aquella fila
							val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							
							while(options.contains(val))
							{
								val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							}
							
							SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
						}
						break;
						
					case GameController.agentContributorBySquares:		////Agent que nomes treballa per Quadrats
						
						int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
						
						for (int row = 0; row < AgentNetworkController.getSudokuSize(); row += sizeSquare)
						{
							for (int column = 0; column < AgentNetworkController.getSudokuSize(); column += sizeSquare)
							{
								for(i=0;i<sizeSquare;i++)
								{
									for(j=0;j<sizeSquare;j++)
									{
										if(actualState[i + row][j + column] != GameController.waitingValue && actualState[i + row][j + column] != GameController.reportedByRows && 
										   actualState[i + row][j + column] != GameController.reportedByColumns &&  actualState[i + row][j + column] != GameController.reportedBySquares &&
										   actualState[i + row][j + column] != GameController.reportedByUser &&  actualState[i + row][j + column] != GameController.rejectedByAgent &&
										   actualState[i + row][j + column] != GameController.rejectedByUser)
										{
											options.add(actualGrid[i + row][j + column]);
										}
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
						
						if(!emptyPositionList.isEmpty())
						{
							emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
							i = emptyPosition[0];
							j = emptyPosition[1];
							
							options = listOptions.get(controller.getRegion(i, j));	//Obtenim les opcions que tenim per aquella fila
							val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							
							while(options.contains(val))
							{
								val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							}
							
							SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
						}
						break;
				}
				
				try 
				{		
					Thread.sleep(500);					//Time to updte the grid in the server and the clients
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		//#######################################    Check Bugs    ############################################
		
		static synchronized void checkBugs(int agentId, int agentType)
		{			
			if(!getConflictExists())
			{
				Random random = new Random(System.nanoTime());
				
				boolean foundOnce;
				boolean cleanPosition = false;
				
				int[][] actualGrid = AgentNetworkController.getActualGrid();
				int[][] actualState = AgentNetworkController.getActualState();
				int i, j, k, val, loopSize, loopSizeSearchingList;
				
				ArrayList<Integer> optionsToAsk = new ArrayList<Integer>();		
				ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();		//Guardem nomes aquells Valors pels que podem preguntar
				
				ArrayList<Integer> optionsToSearch = new ArrayList<Integer>();
				ArrayList<ArrayList<Integer>> searchingList = new ArrayList<ArrayList<Integer>>();		//Guardem tots els valors del Grid
				
				int[] position;
				ArrayList<int[]> optionsPosition = new ArrayList<int[]>();
				ArrayList<ArrayList<int[]>> listOptionsPosition = new ArrayList<ArrayList<int[]>>();
				
				ArrayList<int[]> positionsToClean = new ArrayList<int[]>();
				
				switch(agentType)
				{
					case GameController.agentBugReporterByRows:				//Tester que nomes treballa per files
						
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							for(i=0;i<AgentNetworkController.getSudokuSize();i++)
							{
								if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
								   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
								   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
								   actualState[i][j] != GameController.rejectedByUser)
								{
									optionsToSearch.add(actualGrid[i][j]);		
									
									if(actualState[i][j] == GameController.contributedByColumns || actualState[i][j] == GameController.contributedBySquares || actualState[i][j] == GameController.contributedByUser)
									{
										optionsToAsk.add(actualGrid[i][j]);
										
										position = new int[2];
										position[0] = i;
										position[1] = j;
										optionsPosition.add(position);
									}
								}
							}
							
							listOptions.add(optionsToAsk);
							optionsToAsk = new ArrayList<Integer>();
							
							searchingList.add(optionsToSearch);
							optionsToSearch = new ArrayList<Integer>();
						
							listOptionsPosition.add(optionsPosition);
							optionsPosition = new ArrayList<int[]>();
						}
										
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							optionsToAsk = new ArrayList<Integer>();
							optionsToSearch = new ArrayList<Integer>();
							optionsPosition = new ArrayList<int[]>();
							
							optionsToAsk = listOptions.get(i);
							optionsToSearch = searchingList.get(i);
							optionsPosition = listOptionsPosition.get(i);
							
							loopSize = optionsToAsk.size();
							for(j=0; j<loopSize;j++)
							{
								val = optionsToAsk.get(j);
	
								foundOnce = false;
								loopSizeSearchingList = optionsToSearch.size();
								
								for(k=0; k<loopSizeSearchingList; k++)
								{	
									if(optionsToSearch.get(k) == val)
									{
										if(foundOnce)
										{
											position = new int[2];
											position[0] = optionsPosition.get(j)[0];
											position[1] = optionsPosition.get(j)[1];
											positionsToClean.add(position);
											
											cleanPosition = true;
										}
										
										foundOnce = true;
									}
								}
							}
						}
						
						if (cleanPosition)
						{
							val = random.nextInt(positionsToClean.size());
							SendMessage("bugReported#" + agentId + "," + agentType + "," + positionsToClean.get(val)[0] + "," + positionsToClean.get(val)[1]);
						}
						
						break;
						
					case GameController.agentBugReporterByColumns:		//Agent que nomes treballa per Columnes	
						
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							for(j=0;j<AgentNetworkController.getSudokuSize();j++)
							{
								if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
								   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
								   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
								   actualState[i][j] != GameController.rejectedByUser)
								{
									optionsToSearch.add(actualGrid[i][j]);								
									if(actualState[i][j] == GameController.contributedByRows || actualState[i][j] == GameController.contributedBySquares || actualState[i][j] == GameController.contributedByUser)
									{
										optionsToAsk.add(actualGrid[i][j]);
										
										position = new int[2];
										position[0] = i;
										position[1] = j;
										optionsPosition.add(position);
									}
								}
							}
							
							listOptions.add(optionsToAsk);
							optionsToAsk = new ArrayList<Integer>();
							
							searchingList.add(optionsToSearch);
							optionsToSearch = new ArrayList<Integer>();
							
							listOptionsPosition.add(optionsPosition);
							optionsPosition = new ArrayList<int[]>();
						}
										
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							optionsToAsk = new ArrayList<Integer>();
							optionsToSearch = new ArrayList<Integer>();
							optionsPosition = new ArrayList<int[]>();
							
							optionsToAsk = listOptions.get(i);
							optionsToSearch = searchingList.get(i);
							optionsPosition = listOptionsPosition.get(i);
							
							loopSize = optionsToAsk.size();
							for(j=0; j<loopSize;j++)
							{
								val = optionsToAsk.get(j);
	
								foundOnce = false;
								loopSizeSearchingList = optionsToSearch.size();
								
								for(k=0; k<loopSizeSearchingList; k++)
								{	
									if(optionsToSearch.get(k) == val)
									{
										if(foundOnce)
										{
											position = new int[2];
											position[0] = optionsPosition.get(j)[0];
											position[1] = optionsPosition.get(j)[1];
											positionsToClean.add(position);
											
											cleanPosition = true;
										}
										
										foundOnce = true;
									}
								}
							}
						}
						
						if (cleanPosition)
						{
							val = random.nextInt(positionsToClean.size());
							SendMessage("bugReported#" + agentId + "," + agentType + "," + positionsToClean.get(val)[0] + "," + positionsToClean.get(val)[1]);
						}
						
						break;
						
					case 6:		////Agent que nomes treballa per Quadrats
								
						int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
						
						for (int row=0; row<AgentNetworkController.getSudokuSize();row+=sizeSquare)
						{
							for (int column=0; column<AgentNetworkController.getSudokuSize();column+=sizeSquare)
							{
								for(i=0;i<sizeSquare;i++)
								{
									for(j=0;j<sizeSquare;j++)
									{
										if(actualState[i + row][j + column] != GameController.waitingValue && actualState[i + row][j + column] != GameController.reportedByRows && 
										   actualState[i + row][j + column] != GameController.reportedByColumns &&  actualState[i + row][j + column] != GameController.reportedBySquares &&
										   actualState[i + row][j + column] != GameController.reportedByUser &&  actualState[i + row][j + column] != GameController.rejectedByAgent &&
										   actualState[i + row][j + column] != GameController.rejectedByUser)
										{
											optionsToSearch.add(actualGrid[i + row][j + column]);								
											if(actualState[i + row][j + column] == GameController.contributedByRows || actualState[i + row][j + column] == GameController.contributedByColumns || actualState[i + row][j + column] == GameController.contributedByUser)
											{
												optionsToAsk.add(actualGrid[i + row][j + column]);
												
												position = new int[2];
												position[0] = i + row;
												position[1] = j + column;
												optionsPosition.add(position);
											}
										}
									}
								}
								
								listOptions.add(optionsToAsk);
								optionsToAsk = new ArrayList<Integer>();
								
								searchingList.add(optionsToSearch);
								optionsToSearch = new ArrayList<Integer>();
								
								listOptionsPosition.add(optionsPosition);
								optionsPosition = new ArrayList<int[]>();
							}
						}
						
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							optionsToAsk = new ArrayList<Integer>();
							optionsToSearch = new ArrayList<Integer>();
							optionsPosition = new ArrayList<int[]>();
							
							optionsToAsk = listOptions.get(i);
							optionsToSearch = searchingList.get(i);
							optionsPosition = listOptionsPosition.get(i);
							
							loopSize = optionsToAsk.size();
							for(j=0; j<loopSize;j++)
							{
								val = optionsToAsk.get(j);
	
								foundOnce = false;
								loopSizeSearchingList = optionsToSearch.size();
								
								for(k=0; k<loopSizeSearchingList; k++)
								{	
									if(optionsToSearch.get(k) == val)
									{
										if(foundOnce)
										{
											position = new int[2];
											position[0] = optionsPosition.get(j)[0];
											position[1] = optionsPosition.get(j)[1];
											positionsToClean.add(position);
											
											cleanPosition = true;
										}
										
										foundOnce = true;
									}
								}
							}
						}
						
						if (cleanPosition)
						{
							val = random.nextInt(positionsToClean.size());
							SendMessage("bugReported#" + agentId + "," + agentType + "," + positionsToClean.get(val)[0] + "," + positionsToClean.get(val)[1]);
						}
						
						break;
				}
			}
		}
		
		
		//#######################################    TEST VALUES    ############################################
		
		static synchronized void testValues(int agentId, int agentType)
		{						
			
			if(!getConflictExists())
			{
				Random random = new Random(System.nanoTime());
				
				boolean foundOnce, foundTwice;
				boolean cleanPosition = false;
				
				int[][] actualGrid = AgentNetworkController.getActualGrid();
				int[][] actualState = AgentNetworkController.getActualState();
				int i, j, k, val, loopSize, loopSizeSearchingList;
				
				ArrayList<Integer> optionsToAsk = new ArrayList<Integer>();		
				ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();		//Guardem nomes aquells Valors pels que podem preguntar
				
				ArrayList<Integer> optionsToSearch = new ArrayList<Integer>();
				ArrayList<ArrayList<Integer>> searchingList = new ArrayList<ArrayList<Integer>>();		//Guardem tots els valors del Grid
				
				int[] position;
				ArrayList<int[]> optionsPosition = new ArrayList<int[]>();
				ArrayList<ArrayList<int[]>> listOptionsPosition = new ArrayList<ArrayList<int[]>>();
				
				ArrayList<int[]> positionsTested = new ArrayList<int[]>();
				
				switch(agentType)
				{
					case GameController.agentTesterByRows:				//Tester que nomes treballa per files
						
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							for(i=0;i<AgentNetworkController.getSudokuSize();i++)
							{
								if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
								   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
								   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
								   actualState[i][j] != GameController.rejectedByUser)
								{
									optionsToSearch.add(actualGrid[i][j]);								
									if(actualState[i][j] == GameController.contributedByColumns || actualState[i][j] == GameController.contributedBySquares || actualState[i][j] == GameController.contributedByUser)
									{
										optionsToAsk.add(actualGrid[i][j]);
										
										position = new int[2];
										position[0] = i;
										position[1] = j;
										optionsPosition.add(position);
									}
								}
							}
							
							listOptions.add(optionsToAsk);
							optionsToAsk = new ArrayList<Integer>();
														
							searchingList.add(optionsToSearch);
							optionsToSearch = new ArrayList<Integer>();
							
							listOptionsPosition.add(optionsPosition);
							optionsPosition = new ArrayList<int[]>();
						}
										
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							optionsToAsk = new ArrayList<Integer>();
							optionsToSearch = new ArrayList<Integer>();
							optionsPosition = new ArrayList<int[]>();
							
							optionsToAsk = listOptions.get(i);
							optionsToSearch = searchingList.get(i);
							optionsPosition = listOptionsPosition.get(i);
							
							loopSize = optionsToAsk.size();
							for(j=0; j<loopSize;j++)
							{
								val = optionsToAsk.get(j);
	
								foundOnce = false;
								foundTwice = false;
								loopSizeSearchingList = optionsToSearch.size();
								
								for(k=0; k<loopSizeSearchingList; k++)
								{	
									if(optionsToSearch.get(k) == val)
									{
										if(foundOnce)
											foundTwice = true;
										
										foundOnce = true;
									}
									
									if(k == loopSizeSearchingList-1 && !foundTwice)
									{
										position = new int[2];
										position[0] = optionsPosition.get(j)[0];
										position[1] = optionsPosition.get(j)[1];
										positionsTested.add(position);
										
										cleanPosition = true;
									}
								}
								
							}
						}
						
						if (cleanPosition)
						{
							val = random.nextInt(positionsTested.size());
							SendMessage("clear#" + agentId + "," + agentType + "," + positionsTested.get(val)[0] + "," + positionsTested.get(val)[1]);
						}
						
						break;
						
					case GameController.agentTesterByColumns:		//Agent que nomes treballa per Columnes	
						
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							for(j=0;j<AgentNetworkController.getSudokuSize();j++)
							{
								if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
								   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
								   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
								   actualState[i][j] != GameController.rejectedByUser)
								{
									optionsToSearch.add(actualGrid[i][j]);								
									if(actualState[i][j] == GameController.contributedByRows || actualState[i][j] == GameController.contributedBySquares || actualState[i][j] == GameController.contributedByUser)
									{
										optionsToAsk.add(actualGrid[i][j]);
										
										position = new int[2];
										position[0] = i;
										position[1] = j;
										optionsPosition.add(position);
									}
								}
							}
							
							listOptions.add(optionsToAsk);
							optionsToAsk = new ArrayList<Integer>();
							
							searchingList.add(optionsToSearch);
							optionsToSearch = new ArrayList<Integer>();
							
							listOptionsPosition.add(optionsPosition);
							optionsPosition = new ArrayList<int[]>();
						}
										
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							optionsToAsk = new ArrayList<Integer>();
							optionsToSearch = new ArrayList<Integer>();
							optionsPosition = new ArrayList<int[]>();
							
							optionsToAsk = listOptions.get(i);
							optionsToSearch = searchingList.get(i);
							optionsPosition = listOptionsPosition.get(i);
							
							loopSize = optionsToAsk.size();
							for(j=0; j<loopSize;j++)
							{
								val = optionsToAsk.get(j);
	
								foundOnce = false;
								foundTwice = false;
								loopSizeSearchingList = optionsToSearch.size();
								
								for(k=0; k<loopSizeSearchingList; k++)
								{	
									if(optionsToSearch.get(k) == val)
									{
										if(foundOnce)
											foundTwice = true;
										
										foundOnce = true;
									}
									
									if(k == loopSizeSearchingList-1 && !foundTwice)
									{
										position = new int[2];
										position[0] = optionsPosition.get(j)[0];
										position[1] = optionsPosition.get(j)[1];
										positionsTested.add(position);
										
										cleanPosition = true;
									}
								}
								
							}
						}
						
						if (cleanPosition)
						{
							val = random.nextInt(positionsTested.size());
							SendMessage("clear#" + agentId + "," + agentType + "," + positionsTested.get(val)[0] + "," + positionsTested.get(val)[1]);
						}
						
						break;
						
					case GameController.agentTesterBySquares:		////Agent que nomes treballa per Quadrats
								
						int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
						
						for (int row=0; row<AgentNetworkController.getSudokuSize();row+=sizeSquare)
						{
							for (int column=0; column<AgentNetworkController.getSudokuSize();column+=sizeSquare)
							{
								for(i=0;i<sizeSquare;i++)
								{
									for(j=0;j<sizeSquare;j++)
									{
										if(actualState[i + row][j + column] != GameController.waitingValue && actualState[i + row][j + column] != GameController.reportedByRows && 
										   actualState[i + row][j + column] != GameController.reportedByColumns &&  actualState[i + row][j + column] != GameController.reportedBySquares &&
										   actualState[i + row][j + column] != GameController.reportedByUser &&  actualState[i + row][j + column] != GameController.rejectedByAgent &&
										   actualState[i + row][j + column] != GameController.rejectedByUser)
										{
											optionsToSearch.add(actualGrid[i + row][j + column]);			
											
											if(actualState[i + row][j + column] == GameController.contributedByRows || actualState[i + row][j + column] == GameController.contributedByColumns || actualState[i + row][j + column] == GameController.contributedByUser)
											{
												optionsToAsk.add(actualGrid[i + row][j + column]);
												
												position = new int[2];
												position[0] = i + row;
												position[1] = j + column;
												optionsPosition.add(position);
											}
										}
									}
								}
								
								listOptions.add(optionsToAsk);
								optionsToAsk = new ArrayList<Integer>();
								
								searchingList.add(optionsToSearch);
								optionsToSearch = new ArrayList<Integer>();
								
								listOptionsPosition.add(optionsPosition);
								optionsPosition = new ArrayList<int[]>();
							}
						}
						
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							optionsToAsk = new ArrayList<Integer>();
							optionsToSearch = new ArrayList<Integer>();
							optionsPosition = new ArrayList<int[]>();
							
							optionsToAsk = listOptions.get(i);
							optionsToSearch = searchingList.get(i);
							optionsPosition = listOptionsPosition.get(i);
							
							loopSize = optionsToAsk.size();
							for(j=0; j<loopSize;j++)
							{
								val = optionsToAsk.get(j);
	
								foundOnce = false;
								foundTwice = false;
								loopSizeSearchingList = optionsToSearch.size();
								
								for(k=0; k<loopSizeSearchingList; k++)
								{	
									if(optionsToSearch.get(k) == val)
									{
										if(foundOnce)
											foundTwice = true;
										
										foundOnce = true;
									}
									
									if(k == loopSizeSearchingList-1 && !foundTwice)
									{
										position = new int[2];
										position[0] = optionsPosition.get(j)[0];
										position[1] = optionsPosition.get(j)[1];
										positionsTested.add(position);
										
										cleanPosition = true;
									}
								}
							}
						}
						
						if (cleanPosition)
						{
							val = random.nextInt(positionsTested.size());
							SendMessage("clear#" + agentId + "," + agentType + "," + positionsTested.get(val)[0] + "," + positionsTested.get(val)[1]);
						}
						
						break;
				}
			}
		}
		
		
		//#######################################    VOTE CONFLICT   ############################################
		
		static synchronized void voteConflict(int agentId, int agentType)
		{				
			boolean firstTime;
			boolean alreadyVoted = false;
			
			int[][] actualGrid = AgentNetworkController.getActualGrid();
			int[][] actualState = AgentNetworkController.getActualState();
			
			int i, j, val, conflictX, conflictY, randomVal;
			ArrayList<Integer> options = new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
			
			int[] position;
			ArrayList<int[]> optionsPosition = new ArrayList<int[]>();
			ArrayList<ArrayList<int[]>> listOptionsPosition = new ArrayList<ArrayList<int[]>>();
			
			Random random = new Random(System.nanoTime());
					
			switch(agentType)
			{
				case GameController.agentCommitterByRows:		//Agent que nomes treballa per files
							
				for(j=0;j<AgentNetworkController.getSudokuSize();j++)
				{
					for(i=0;i<AgentNetworkController.getSudokuSize();i++)
					{
						if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
						   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
						   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
						   actualState[i][j] != GameController.rejectedByUser)
						{
							options.add(actualGrid[i][j]);
							
							position = new int[2];
							position[0] = i;
							position[1] = j;
							optionsPosition.add(position);
						}
					}
					
					listOptions.add(options);
					options = new ArrayList<Integer>();
					
					listOptionsPosition.add(optionsPosition);
					optionsPosition = new ArrayList<int[]>();
				}
								
				conflictX = AgentNetworkController.getPositionXConflict();
				conflictY = AgentNetworkController.getPositionYConflict();
				
				options = listOptions.get(conflictY);
				val = actualGrid[conflictX][conflictY];
								
				firstTime = false;	
				for(int value : options)
				{
					if(value == val || firstTime)
					{
						if(value == val && firstTime)
						{
							randomVal = random.nextInt(10);
							if (randomVal >= 0 && randomVal < 8)			//Add a Random possibility (20%) that the Agents Fails the vote
								SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
							else
								SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
							
							alreadyVoted = true;
							break;
						}
						firstTime = true;
					}
				}

				if (!alreadyVoted)
				{
					randomVal = random.nextInt(10);
					if (randomVal >= 0 && randomVal < 8)
						SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
					else
						SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
				}
				
				break;
					
				case GameController.agentCommitterByColumns:		//Agent que nomes treballa per Columnes	

					for(i=0;i<AgentNetworkController.getSudokuSize();i++)
					{
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							if(actualState[i][j] != GameController.waitingValue && actualState[i][j] != GameController.reportedByRows && 
							   actualState[i][j] != GameController.reportedByColumns &&  actualState[i][j] != GameController.reportedBySquares &&
							   actualState[i][j] != GameController.reportedByUser &&  actualState[i][j] != GameController.rejectedByAgent &&
							   actualState[i][j] != GameController.rejectedByUser)
							{
								options.add(actualGrid[i][j]);
								
								position = new int[2];
								position[0] = i;
								position[1] = j;
								optionsPosition.add(position);
							}
						}
						listOptions.add(options);
						options = new ArrayList<Integer>();
						
						listOptionsPosition.add(optionsPosition);
						optionsPosition = new ArrayList<int[]>();
					}
						
					conflictX = AgentNetworkController.getPositionXConflict();
					conflictY = AgentNetworkController.getPositionYConflict();
					
					options = listOptions.get(conflictX);
					val = actualGrid[conflictX][conflictY];
					
					firstTime = false;	
					for(int value : options)
					{
						if(value == val || firstTime)
						{
							if(value == val && firstTime)
							{
								randomVal = random.nextInt(10);
								if (randomVal >= 0 && randomVal < 7)
									SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
								else
									SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
								
								alreadyVoted = true;
								break;
							}
							firstTime = true;
						}
					}
		
					if (!alreadyVoted)
					{
						randomVal = random.nextInt(10);
						if (randomVal >= 0 && randomVal < 8)
							SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
						else
							SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
					}
					
					break;
					
				case GameController.agentCommitterBySquares:		////Agent que nomes treballa per Quadrats
							
					int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
					
					for (int row=0; row<AgentNetworkController.getSudokuSize();row+=sizeSquare)
					{
						for (int column=0; column<AgentNetworkController.getSudokuSize();column+=sizeSquare)
						{
							for(i=0;i<sizeSquare;i++)
							{
								for(j=0;j<sizeSquare;j++)
								{
									if(actualState[i + row][j + column] != GameController.waitingValue && actualState[i + row][j + column] != GameController.reportedByRows && 
									   actualState[i + row][j + column] != GameController.reportedByColumns &&  actualState[i + row][j + column] != GameController.reportedBySquares &&
									   actualState[i + row][j + column] != GameController.reportedByUser &&  actualState[i + row][j + column] != GameController.rejectedByAgent &&
									   actualState[i + row][j + column] != GameController.rejectedByUser)
									{
										options.add(actualGrid[i + row][j + column]);
										
										position = new int[2];
										position[0] = i + row;
										position[1] = j + column;
										optionsPosition.add(position);
									}
								}
							}
							listOptions.add(options);
							options = new ArrayList<Integer>();
							
							listOptionsPosition.add(optionsPosition);
							optionsPosition = new ArrayList<int[]>();
						}
					}
					
					conflictX = AgentNetworkController.getPositionXConflict();
					conflictY = AgentNetworkController.getPositionYConflict();
					
					val = controller.getRegion(conflictX, conflictY);
					options = listOptions.get(val);
					val = actualGrid[conflictX][conflictY];
					
					firstTime = false;	
					for(int value : options)
					{
						if(value == val || firstTime)
						{
							if(value == val && firstTime)
							{
								randomVal = random.nextInt(10);
								if (randomVal >= 0 && randomVal < 7)
									SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
								else
									SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
								
								alreadyVoted = true;
								break;
							}
							firstTime = true;
						}
					}

					if (!alreadyVoted)
					{
						randomVal = random.nextInt(10);
						if (randomVal >= 0 && randomVal < 8)
							SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
						else
							SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
					}
					
					break;
			}
		}
		
		
		//#######################################    LEADER   ############################################
		
		static synchronized void Leader(int agentId, int agentType)
		{	
			int[][] actualGrid = AgentNetworkController.getActualGrid();
			int i, j, val, x, y;
						
			int[] committedList;
			ArrayList<int[]> committedPositionList = new ArrayList<int[]>();
			
			Random random = new Random(System.nanoTime());
			
			for(i=0;i<AgentNetworkController.getSudokuSize();i++)
			{
				for(j=0;j<AgentNetworkController.getSudokuSize();j++)
				{
					if(controller.getCellState(i, j) == GameController.committedByTesterByRows || controller.getCellState(i, j) == GameController.committedByTesterByColumns || 
					   controller.getCellState(i, j) == GameController.committedByTesterBySquares || controller.getCellState(i, j) == GameController.committedByTesterByUser)
					{
						committedList = new int[2];
						committedList[0] = i;
						committedList[1] = j;
						committedPositionList.add(committedList);
					}
				}
			}
			
			if(!committedPositionList.isEmpty())
			{
				val = random.nextInt(committedPositionList.size());
				x = committedPositionList.get(val)[0];
				y = committedPositionList.get(val)[1];
				
				boolean correct = checkPosition(x, y, actualGrid[x][y]);
				
				if(correct)
					SendMessage("accepted#" + agentId + "," + agentType + "," + x + "," + y);
				else
					SendMessage("rejected#" + agentId + "," + agentType + "," + x + "," + y);
				
			}
			else
			{
				try {		
					Thread.sleep(2500);			//Donam temps a que s'actualitzi el grid
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		static boolean checkPosition(int x, int y, int val)
		{
			
			int i, j;
			
			int[][] actualGrid = AgentNetworkController.getActualGrid();
			int[][] actualState = AgentNetworkController.getActualState();
			
			ArrayList<Integer> number = new ArrayList<Integer>();
			
			for(i=0;i<AgentNetworkController.getSudokuSize();i++)
			{
				if((actualState[i][y] != GameController.waitingValue && actualState[i][y] != GameController.reportedByRows && 
				    actualState[i][y] != GameController.reportedByColumns &&  actualState[i][y] != GameController.reportedBySquares &&
				    actualState[i][y] != GameController.reportedByUser &&  actualState[i][y] != GameController.rejectedByAgent &&
				    actualState[i][y] != GameController.rejectedByUser) && x != i)
				{
					number.add(actualGrid[i][y]);
				}
			}
				
			if(number.contains(val))
			{
				return false;
			}
			
			number = new ArrayList<Integer>();
					
			for(i=0;i<AgentNetworkController.getSudokuSize();i++)
			{
				if((actualState[x][i] != GameController.waitingValue && actualState[x][i] != GameController.reportedByRows && 
				    actualState[x][i] != GameController.reportedByColumns &&  actualState[x][i] != GameController.reportedBySquares &&
				    actualState[x][i] != GameController.reportedByUser &&  actualState[x][i] != GameController.rejectedByAgent &&
				    actualState[x][i] != GameController.rejectedByUser) && y != i)
				{
					number.add(actualGrid[x][i]);
				}
			}
			
			if(number.contains(val))
			{
				return false;
			}
				
			number = new ArrayList<Integer>();
			
			int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
			int[] region = getRegion(x, y);
			
			for(i=0;i<sizeSquare;i++)
			{
				for(j=0;j<sizeSquare;j++)
				{
					if((actualState[i + region[0]][j + region[1]] != GameController.waitingValue && actualState[i + region[0]][j + region[1]] != GameController.reportedByRows && 
					    actualState[i + region[0]][j + region[1]] != GameController.reportedByColumns &&  actualState[i + region[0]][j + region[1]] != GameController.reportedBySquares &&
					    actualState[i + region[0]][j + region[1]] != GameController.reportedByUser &&  actualState[i + region[0]][j + region[1]] != GameController.rejectedByAgent &&
					    actualState[i + region[0]][j + region[1]] != GameController.rejectedByUser) && x != (i + region[0]) && y != (j + region[1]))
					{
						number.add(actualGrid[i + region[0]][j + region[1]]);
					}
				}
			}
			
			if(number.contains(val))
			{
				return false;
			}
			
			return true;
		}
		
		static int[] getRegion(int x, int y)
		{
			int[] region = new int[2];
			
			if(x>=0 && x<4)
			{
				if (0<=y && y<4)
				{
					region[0] = 0;
					region[1] = 0;
				}
				if (4<=y && y<8)
				{
					region[0] = 0;
					region[1] = 4;
				}
				if (8<=y && y<12)
				{
					region[0] = 0;
					region[1] = 8;
				}
				if (12<=y && y<16)
				{
					region[0] = 0;
					region[1] = 12;
				}
			}
			else if (x>=4 && x<8)
			{
				if (0<=y && y<4)
				{
					region[0] = 4;
					region[1] = 0;
				}
				if (4<=y && y<8)
				{
					region[0] = 4;
					region[1] = 4;
				}
				if (8<=y && y<12)
				{
					region[0] = 4;
					region[1] = 8;
				}
				if (12<=y && y<16)
				{
					region[0] = 4;
					region[1] = 12;
				}
			}
			else if (x>=8 && x<12)
			{
				if (0<=y && y<4)
				{
					region[0] = 8;
					region[1] = 0;
				}
				if (4<=y && y<8)
				{
					region[0] = 8;
					region[1] = 4;
				}
				if (8<=y && y<12)
				{
					region[0] = 8;
					region[1] = 8;
				}
				if (12<=y && y<16)
				{
					region[0] = 8;
					region[1] = 12;
				}
			}
			else if (x>=12 && x<16)
			{
				if (0<=y && y<4)
				{
					region[0] = 12;
					region[1] = 0;
				}
				if (4<=y && y<8)
				{
					region[0] = 12;
					region[1] = 4;
				}
				if (8<=y && y<12)
				{
					region[0] = 12;
					region[1] = 8;
				}
				if (12<=y && y<16)
				{
					region[0] = 12;
					region[1] = 12;
				}
			}
			
			return region;
		}
}

