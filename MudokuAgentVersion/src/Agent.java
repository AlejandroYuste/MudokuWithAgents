import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


	class Agent implements Runnable
	{
		
		int agentId;
		int typeAgent;
		
		BufferedReader reader;
		AgentNetworkController controller;
		String message;
		
		boolean execute = true;
		
		int[][] actualGrid;
		
		enum AgentState {waiting, settingValue, checkingGrid}
		AgentState agentState;
				
		public Agent(InputStream inStream, AgentNetworkController controller_, int typeAgent_, int clientId_) throws IOException
		{
			reader = new BufferedReader(new InputStreamReader(inStream));
			controller = controller_;
			typeAgent = typeAgent_;
			
			agentId = clientId_;
			agentState = AgentState.waiting;
		}
		
		int getAgentId()
		{
			return agentId;
		}
		
		public void stopExecuting()
		{
			execute = false;
		}
		
		public void SendMessage(String message)
		{
			controller.SendMessage(message);
			System.out.println("Agent --> SendMessage. Agent " + agentId  + ": Sending message to server '" + message + "'");
		}
		
		public void MessageReceived(String message)
		{
			controller.MessageReceived(message);
			System.out.println("Agent --> MessageReceived. Agent " + agentId  + ": Received message from server '" + message + "'");
		}

		@Override
		public void run() 			//Implementar el Comportament dels Agents
		{
			try 
			{	
				System.out.println("Agent --> Es comença a Executar l'agent: " + agentId);
				Thread.sleep(5000);				//Si no afegueixo aixo envia el misstge abans d'establir connexio
				SendMessage("connect#" + agentId + "," + typeAgent);
				
				if (agentId == 1)		//"instantiate#client" + clientId + "," + activeX + "," + activeY + "," + val
				{
					Thread.sleep(2000);
					SendMessage("instantiate#" + agentId + "," + typeAgent + "," + 2 + "," + 3 + "," + 2);
				}
				else if (agentId == 4)		//"instantiate#client" + clientId + "," + activeX + "," + activeY + "," + val
				{
					Thread.sleep(2000);
					SendMessage("instantiate#" + agentId + "," + typeAgent + "," + 6 + "," + 7 + "," + 12);
				}

				
				
				while(execute)
				{
					System.out.println("Agent --> Executant el Thread del Agent: " + agentId);
					
					if((message = reader.readLine()) != null && !message.equals(".")) 
					{
						MessageReceived(message);
					}
				}
				
				System.out.println("Agent --> Disconected Agent: " + agentId);
				
			} catch (IOException | InterruptedException e) {
			//} catch (IOException e) {
		        System.out.println("ClientReader --> Run. S'ha produit un error al Thread: " + e);  
				e.printStackTrace();
			}
			
		}
	}