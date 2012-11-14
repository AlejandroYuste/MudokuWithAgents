import java.util.Random;


public class AgentContributor implements Runnable{

	Random random = new Random(System.nanoTime());
	int agentId;
	int agentType;
	
	Agent agent;
	
	
	AgentContributor(Agent agent_, int agentId_, int agentType_)
	{
		agentId = agentId_;
		agentType = agentType_;
		agent = agent_;
	}
	
	@Override
	public void run() 
	{
		  
		while(true)
		{
			//controller.agentActions(agentId, agentType);
			try
			{
				Thread.sleep((random.nextInt(agent.getNumAgentsConnected()) + 1) * 1000);
				
				if (agent.getConflictExists())
				{
					agent.voteConflict(agentId, agentType);
					Thread.sleep(14000);
				}
				else
				{
					if (agentId == random.nextInt(agent.getNumAgentsConnected()) + 1)
					{
						agent.setValue(agentId, agentType);
						Thread.sleep((random.nextInt(agent.getNumAgentsConnected()) + 1) * 3000);
					}
			
					if (agentId == random.nextInt(agent.getNumAgentsConnected()) + 1)
					{
						agent.checkGrid(agentId, agentType);
						Thread.sleep((random.nextInt(agent.getNumAgentsConnected()) + 1) * 3000);
					}
				}			
				
				
			} catch (InterruptedException e) {
				System.out.println("Error Produit al Run del Thread del Agent: " + agentId);
				e.printStackTrace();
			}
		}
		
	}

}
