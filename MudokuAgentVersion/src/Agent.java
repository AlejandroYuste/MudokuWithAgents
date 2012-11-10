import java.util.ArrayList;
import java.util.Random;


class Agent implements Runnable{

	int agentId;
	int agentType;
	AgentNetworkController controller;
	
	int[][] actualGrid;
	
	static boolean modifyGrid = false;
	
	Agent(AgentNetworkController controller_, int agentId_, int agentType_)
	{
		agentId = agentId_;
		agentType = agentType_;
		controller = controller_;

	}
	
	void sendMessage(String message)
	{
		controller.SendMessage(message);
	}
	
	int getAgentId()
	{
		return agentId;
	}
	
	@Override
	public void run() {
		//System.out.println("Agent --> Executant el Thread del Agent: " + agentId);
		
		if (agentId == 1) modifyGrid = true;
		while(true)
		{
			instatiateValue();
		}
	}	
	
	synchronized void instatiateValue()
	{
        try 
        {
        	/*synchronized(this)
        	{
	        	while (modifyGrid == false) {
	        		 System.out.println("Agent --> Eseprant l'Agent: " + agentId);
	        		 wait();
	        		 modifyGrid = true;
	        		 System.out.println("Agent --> Desperta l'Agent: " + agentId);
	        	}
	        	
	            System.out.println("Agent --> Afegueix Valor l'Agent: " + agentId);
	
	        	setValue();
	        	Thread.sleep(2000);
	        	
	            System.out.println("Agent --> Fa el notify l'agent: " + agentId);
	            modifyGrid = false;
	            notify();
	            System.out.println("Agent --> Fet el notify per l'agent: " + agentId);
        	}*/
        	
        	setValue();
        	Thread.sleep(500);
        } 
        catch (InterruptedException e) 
        {
        	e.printStackTrace();
        }
	}
	
	void setValue()
	{
		actualGrid = controller.getActualGrid();
		int i, j, val;
		ArrayList<Integer> options = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
		
		int[] emptyPosition;
		ArrayList<int[]> emptyPositionList = new ArrayList<int[]>();
		
		Random random = new Random(System.nanoTime());
		
		switch(agentType)
		{
			case(0):		//Agent que nomes treballa per files
				
				System.out.println("Enterm al cas per Files");
				
				for(i=0;i<controller.getSudokuSize();i++)
				{
					for(j=0;j<controller.getSudokuSize();j++)
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
			
				emptyPosition = emptyPositionList.get(random.nextInt(emptyPositionList.size()));
				i = emptyPosition[0];
				j = emptyPosition[1];
				
				options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(controller.getSudokuSize());
				while(options.contains(val))
				{
					val = random.nextInt(controller.getSudokuSize());
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
				
			case(1):		//Agent que nomes treballa per Columnes
				
				System.out.println("Enterm al cas per Columnes");
			
				for(j=0;j<controller.getSudokuSize();j++)
				{
					for(i=0;i<controller.getSudokuSize();i++)
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
				
				val = random.nextInt(controller.getSudokuSize());
				while(options.contains(val))
				{
					val = random.nextInt(controller.getSudokuSize());
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
				
			case(2):		////Agent que nomes treballa per Quadrats
				
				System.out.println("Enterm al cas per Quadrats");
			
				int sizeSquare = (int) Math.sqrt(controller.getSudokuSize());
				
				for (int row=0; row<controller.getSudokuSize();row+=sizeSquare)
				{
					for (int column=0; column<controller.getSudokuSize();column+=sizeSquare)
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
				
				val = random.nextInt(controller.getSudokuSize());
				while(options.contains(val))
				{
					val = random.nextInt(controller.getSudokuSize());
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
		}
	}
}