import java.util.Random;

public class AgentBugReporter implements Runnable
{
	Random random = new Random(System.nanoTime());
	
	int agentId;
	int agentType;
	Agent agent;
	
	AgentBugReporter(Agent agent_, int agentId_, int agentType_)
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
					Agent.checkBugs(agentId, agentType);
				else Thread.sleep(10000);
				
			} catch (InterruptedException e) {
				System.out.println("Error Produit al Run del Thread del Agent: " + agentId);
				e.printStackTrace();
			}
		}
	}
}