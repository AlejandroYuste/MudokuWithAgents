
public class ThreadsInformation {
	Thread threadInfo;
	int agentId;
	int agentType;
	
	ThreadsInformation(Thread agentThread, int agentId_, int agentType_)
	{
		threadInfo = agentThread;
		agentId = agentId_;
		agentType = agentType_;
	}

	Thread getThread() {
		return threadInfo;
	}
	
	int getAgentId() {
		return agentId;
	}
	
	int getAgentType() {
		return agentType;
	}
	
	void setAgentId(int _agentId) {
		agentId = _agentId;
	}
}
