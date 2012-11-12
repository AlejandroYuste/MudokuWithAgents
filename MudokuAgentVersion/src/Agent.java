import java.util.ArrayList;
import java.util.Random;


class Agent implements Runnable{

	int agentId;
	int agentType;
	AgentNetworkController controller;
	
	Random random;
	
	int[][] actualGrid;
	
	static boolean modifyGrid = false;
	
	Agent(AgentNetworkController controller_, int agentId_, int agentType_)
	{
		agentId = agentId_;
		agentType = agentType_;
		controller = controller_;

		random = new Random(System.nanoTime());
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
		
		//if (agentId == 1) modifyGrid = true;
		int putValue;
		while(true)
		{
			try {
				putValue = random.nextInt(controller.getNumAgentsConnected()) + 1;
				Thread.sleep(putValue*1000);
				
				
				/*if (controller.getConflictExists())
				{
					System.out.println("Hi ha un conflicte");
					voteConfict();
				}
				else
					AgentNetworkController.checkGrid(agentId, agentType);*/
				
				//System.out.println("Agent --> putValue: " + putValue);
				if (agentId == putValue)
					AgentNetworkController.setValue(agentId, agentType);
				
				
			} catch (InterruptedException e) {
				System.out.println("Error Produit al Run del Thread del Agent: " + agentId);
				e.printStackTrace();
			}
			
		}
	}	
	
	void voteConfict()
	{
		actualGrid = controller.getActualGrid();
		int i, j, val;
		ArrayList<Integer> options = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> listOptions = new ArrayList<ArrayList<Integer>>();
		
		int xConflict = controller.getPositionXConflict();
		int yConflict = controller.getPositionYConflict();
		
		int vote = -1;
		
		switch(agentType)
		{
			case(0):		//Agent que nomes treballa per files
				
				System.out.println("Enterm al cas per Files");
				
				for(i=0;i<controller.getSudokuSize();i++)
				{
					for(j=0;j<controller.getSudokuSize();j++)
					{
						if(actualGrid[i][j] != -1 && i != xConflict && j != yConflict)
							options.add(actualGrid[i][j]);
					}
					listOptions.add(options);
					options = new ArrayList<Integer>();
				}
			
				options = listOptions.get(xConflict);	//Obtenim les opcions que tenim per aquella fila
				if (options.contains(actualGrid[xConflict][yConflict]))
				{
					vote = 1;
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				//sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				sendMessage("voted#" + xConflict + "," + yConflict + "," + vote);
				break;
				
			case(1):		//Agent que nomes treballa per Columnes
				
				System.out.println("Enterm al cas per Columnes");
			
				for(j=0;j<controller.getSudokuSize();j++)
				{
					for(i=0;i<controller.getSudokuSize();i++)
					{
						if(actualGrid[j][i] != -1)
							options.add(actualGrid[j][i]);
					}
					listOptions.add(options);
					options = new ArrayList<Integer>();
				}
				
				options = listOptions.get(xConflict);	//Obtenim les opcions que tenim per aquella fila
				if (options.contains(actualGrid[xConflict][yConflict]))
				{
					vote = 1;
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				//sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				sendMessage("voted#" + xConflict + "," + yConflict + "," + vote);
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
							}
						}
						listOptions.add(options);
						options = new ArrayList<Integer>();
					}
				}				
				
				//options = listOptions.get(i);	//Obtenim les opcions que tenim per aquella fila
				
				val = random.nextInt(controller.getSudokuSize());
				while(options.contains(val))
				{
					val = random.nextInt(controller.getSudokuSize());
				}
				
				//System.out.println("Agent --> triat valor: " + val + "per la posicio " + i + "," + j);
				//sendMessage("instantiate#" + agentId + "," + agentType + "," + i + "," + j + "," + val);
				break;
		}
	}
}

