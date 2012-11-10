
public class ThreadsInformation {
	Thread threadInfo;
	int agentId;
	
	ThreadsInformation(Thread agentThread, int agentId_)
	{
		threadInfo = agentThread;
		agentId = agentId_;
	}

Thread getThread()
	{
		return threadInfo;
	}
	
	int getAgentId()
	{
		return agentId;
	}
}
