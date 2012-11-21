import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class Agent {		//TODO: Separar cada agent en subclasses i cridar desde aqui al run per cada thread.

	int agentId;
	int agentType;
	
	AgentContributor agentContributor;
	Thread agentContributorThread;
	AgentTester agentTester;
	Thread agentTesterThread;
	AgentCommitter agentCommitter;
	Thread agentCommitterThread;
	AgentLeader agentLeader;
	Thread agentLeaderThread;
	
	ThreadsInformation threadInfo;
	List<ThreadsInformation> ThreadsAgent = new ArrayList<ThreadsInformation>();
	
	static AgentNetworkController controller;
	
	int[][] actualGrid;
		
	static boolean modifyGrid = false;
	boolean alreadyVoted = false;
	
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
	
	@SuppressWarnings("deprecation")
	void pauseExecution()
	{
		for(ThreadsInformation threadInfo : ThreadsAgent)
		{
			threadInfo.threadInfo.suspend();
		}
	}
	
	@SuppressWarnings("deprecation")
	void playExecution()
	{
		for(ThreadsInformation threadInfo : ThreadsAgent)
		{
			threadInfo.threadInfo.resume();
		}
	}
	
	@SuppressWarnings("deprecation")
	void stopExecuting(int agentId_)
	{
		for(ThreadsInformation threadInfo : ThreadsAgent)
		{
			if(threadInfo.agentId == agentId_)
			{
				threadInfo.threadInfo.stop();
			}
		}
	}
	
	
	void executarAgents(int agentId, int agentType) 
	{
		//System.out.println("Agent --> Executant el Thread del Agent: " + agentId);
		switch(agentType)
		{
			case 0: case 1: case 2:
				agentContributor = new AgentContributor(this, agentId, agentType);
				agentContributorThread = new Thread(agentContributor);        
				agentContributorThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentContributorThread, agentId)); 
				break;
			case 4: case 5: case 6:
				agentTester = new AgentTester(this, agentId, agentType);
				agentTesterThread = new Thread(agentTester);        
				agentTesterThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentTesterThread, agentId)); 
				break;
			case 8: case 9: case 10:			//TODO: Votacions no Funcionen correctament
				agentCommitter = new AgentCommitter(this, agentId, agentType);
				agentCommitterThread = new Thread(agentCommitter);        
				agentCommitterThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentCommitterThread, agentId)); 
				break;
			case 12:
				agentLeader = new AgentLeader(this, agentId, agentType);
				agentLeaderThread = new Thread(agentLeader);        
				agentLeaderThread.start();
				
				ThreadsAgent.add(new ThreadsInformation(agentLeaderThread, agentId)); 
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
				
				int i, j, val;
				ArrayList<Integer> options = new ArrayList<Integer>();
				ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
				
				int[] emptyPosition;
				ArrayList<int[]> emptyPositionList = new ArrayList<int[]>();
				
				Random random = new Random(System.nanoTime());
				
				switch(agentType)
				{
					case(0):		//Agent que nomes treballa per files		
						
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							for(i=0;i<AgentNetworkController.getSudokuSize();i++)
							{
								if(actualGrid[i][j] != -1)
									options.add(actualGrid[i][j]);
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
						
					case(1):		//Agent que nomes treballa per Columnes
									
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							for(i=0;i<AgentNetworkController.getSudokuSize();i++)
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
					
						if(!emptyPositionList.isEmpty())
						{
							emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
							i = emptyPosition[0];
							j = emptyPosition[1];
													
							options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
							val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							
							while(options.contains(val))
							{
								val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							}
							
							SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
						}
						break;
						
					case(2):		////Agent que nomes treballa per Quadrats
						
						int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
						
						for (int row=0; row<AgentNetworkController.getSudokuSize();row+=sizeSquare)
						{
							for (int column=0; column<AgentNetworkController.getSudokuSize();column+=sizeSquare)
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
						
						if(!emptyPositionList.isEmpty())
						{
							emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
							i = emptyPosition[0];
							j = emptyPosition[1];
							
							options = listOptions.get(controller.getRegion(i, j));	//Obtenim les opcions que tenim per aquella fila
							System.out.println("Opciones possibles: " + options);
							val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							
							while(options.contains(val))
							{
								val = random.nextInt(AgentNetworkController.getSudokuSize()) + 1;
							}
							
							SendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
						}
						break;
				}
				
				try {		
					Thread.sleep(500);			//Donam teps a que s'actualitzi el grid
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		//#######################################    Check Grid    ############################################
		
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
					case 4:				//Tester que nomes treballa per files
						
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							for(i=0;i<AgentNetworkController.getSudokuSize();i++)
							{
								if(actualGrid[i][j] != -1)
								{
									optionsToSearch.add(actualGrid[i][j]);								
									if(actualState[i][j] == 3 || actualState[i][j] == 4 || actualState[i][j] == 5)
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
							SendMessage("testerClear#" + agentId + "," + agentType + "," + positionsToClean.get(val)[0] + "," + positionsToClean.get(val)[1]);
						}
						
						break;
						
					case 5:		//Agent que nomes treballa per Columnes	
						
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							for(j=0;j<AgentNetworkController.getSudokuSize();j++)
							{
								if(actualGrid[i][j] != -1)
								{
									optionsToSearch.add(actualGrid[i][j]);								
									if(actualState[i][j] == 2 || actualState[i][j] == 3 || actualState[i][j] == 4 || actualState[i][j] == 5)
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
							SendMessage("testerClear#" + agentId + "," + agentType + "," + positionsToClean.get(val)[0] + "," + positionsToClean.get(val)[1]);
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
										if(actualGrid[i + row][j + column] != -1)
										{
											optionsToSearch.add(actualGrid[i][j]);								
											if(actualState[i][j] == 2 || actualState[i][j] == 3 || actualState[i][j] == 4 || actualState[i][j] == 5)
											{
												optionsToAsk.add(actualGrid[i][j]);
												
												position = new int[2];
												position[0] = i;
												position[1] = j;
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
							SendMessage("testerClear#" + agentId + "," + agentType + "," + positionsToClean.get(val)[0] + "," + positionsToClean.get(val)[1]);
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
					case 4:				//Tester que nomes treballa per files
						
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
						{
							for(i=0;i<AgentNetworkController.getSudokuSize();i++)
							{
								if(actualGrid[i][j] != -1)
								{
									optionsToSearch.add(actualGrid[i][j]);								
									if(actualState[i][j] == 3 || actualState[i][j] == 4 || actualState[i][j] == 5)
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
						
					case 5:		//Agent que nomes treballa per Columnes	
						
						for(i=0;i<AgentNetworkController.getSudokuSize();i++)
						{
							for(j=0;j<AgentNetworkController.getSudokuSize();j++)
							{
								if(actualGrid[i][j] != -1)
								{
									optionsToSearch.add(actualGrid[i][j]);								
									if(actualState[i][j] == 2 || actualState[i][j] == 4 || actualState[i][j] == 5)
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
										if(actualGrid[i + row][j + column] != -1)
										{
											optionsToSearch.add(actualGrid[i][j]);								
											if(actualState[i][j] == 2 || actualState[i][j] == 3 || actualState[i][j] == 5)
											{
												optionsToAsk.add(actualGrid[i][j]);
												
												position = new int[2];
												position[0] = i;
												position[1] = j;
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
			int i, j, val, conflictX, conflictY;
			ArrayList<Integer> options = new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
			
			int[] position;
			ArrayList<int[]> optionsPosition = new ArrayList<int[]>();
			ArrayList<ArrayList<int[]>> listOptionsPosition = new ArrayList<ArrayList<int[]>>();
					
			switch(agentType)
			{
				case 8:		//Agent que nomes treballa per files
							
				for(j=0;j<AgentNetworkController.getSudokuSize();j++)
				{
					for(i=0;i<AgentNetworkController.getSudokuSize();i++)
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
							SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
							alreadyVoted = true;
							break;
						}
						firstTime = true;
					}
				}

				if (!alreadyVoted)
					SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
				
				break;
					
				case 9:		//Agent que nomes treballa per Columnes	

					for(i=0;i<AgentNetworkController.getSudokuSize();i++)
					{
						for(j=0;j<AgentNetworkController.getSudokuSize();j++)
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
								SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
								alreadyVoted = true;
								break;
							}
							firstTime = true;
						}
					}
		
					if (!alreadyVoted)
						SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
					
					break;
					
				case 10:		////Agent que nomes treballa per Quadrats
							
					int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
					
					for (int row=0; row<AgentNetworkController.getSudokuSize();row+=sizeSquare)
					{
						for (int column=0; column<AgentNetworkController.getSudokuSize();column+=sizeSquare)
						{
							for(i=0;i<sizeSquare;i++)
							{
								for(j=0;j<sizeSquare;j++)
								{
									if(actualGrid[i + row][j + column] != -1)
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
								SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + 1);
								alreadyVoted = true;
								break;
							}
							firstTime = true;
						}
					}

					if(!alreadyVoted)
						SendMessage("voted#" + agentId + "," + agentType + "," + conflictX + "," + conflictY + "," + -1);
					
					break;
			}
		}
		
		
		//#######################################    LEADER   ############################################
		
		static synchronized void Leader(int agentId, int agentType)
		{	
			int[][] actualGrid = AgentNetworkController.getActualGrid();
			int i, j, val, x, y;
						
			int[] emptyPosition;
			ArrayList<int[]> emptyPositionList = new ArrayList<int[]>();
			
			Random random = new Random(System.nanoTime());
			
			for(j=0;j<AgentNetworkController.getSudokuSize();j++)
			{
				for(i=0;i<AgentNetworkController.getSudokuSize();i++)
				{
					if(controller.getCellState(i, j) == 6)
					{
						emptyPosition = new int[2];
						emptyPosition[0] = i;
						emptyPosition[1] = j;
						emptyPositionList.add(emptyPosition);
					}
				}
			}
			
			if(!emptyPositionList.isEmpty())
			{
				val = random.nextInt(emptyPositionList.size());
				x = emptyPositionList.get(val)[0];
				y = emptyPositionList.get(val)[1];
				
				boolean correct = checkPosition(x, y, actualGrid[x][y]);
				
				if(correct)
					SendMessage("accepted#"+ x + "," + y);
				else
					SendMessage("rejected#"+ x + "," + y);
				
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
			
			ArrayList<Integer> number = new ArrayList<Integer>();
			
			for(i=0;i<AgentNetworkController.getSudokuSize();i++)
			{
				if(actualGrid[i][y] != -1 && x != i)
					number.add(actualGrid[i][y]);
			}
				
			if(number.contains(val))
			{
				return false;
			}
			
			number = new ArrayList<Integer>();
					
			for(i=0;i<AgentNetworkController.getSudokuSize();i++)
			{
				if(actualGrid[x][i] != -1 && y != i)
					number.add(actualGrid[x][i]);
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
					if(actualGrid[i + region[0]][j + region[1]] != -1 && x != (i + region[0]) && y != (j + region[1]))
						number.add(actualGrid[i + region[0]][j + region[1]]);
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

