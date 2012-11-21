import java.util.Random;


public class AgentTester implements Runnable{

	Random random = new Random(System.nanoTime());
	int agentId;
	int agentType;
	
	Agent agent;
	
	int val;
	
	AgentTester(Agent agent_, int agentId_, int agentType_)
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
			try
			{
				Thread.sleep((random.nextInt(agent.getNumAgentsConnected()) + 1) * 1000);
				
				if (!Agent.getConflictExists())
				{
					val = random.nextInt(3);
					
					if (val == 0 || val == 1)
						Agent.testValues(agentId, agentType);
					else if (val == 2)
						Agent.checkBugs(agentId, agentType);
					else
						Thread.sleep((random.nextInt(agent.getNumAgentsConnected()) + 1) * 2000);
				}
				else Thread.sleep(10000);
				
			} catch (InterruptedException e) {
				System.out.println("Error Produit al Run del Thread del Agent: " + agentId);
				e.printStackTrace();
			}
		}
	}

}
