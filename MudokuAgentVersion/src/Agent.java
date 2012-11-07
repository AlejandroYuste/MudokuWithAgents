import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


	class Agent implements Runnable
	{
		
		int clientId;
		BufferedReader reader;
		AgentNetworkController controller;
		String message;
		int typeAgent;
		
		int[][] actualGrid;
		
		enum AgentState {waiting, settingValue, checkingGrid}
		AgentState agentState;
				
		public Agent(InputStream inStream, AgentNetworkController controller_, int typeAgent_, int clientId_) throws IOException
		{
			reader = new BufferedReader(new InputStreamReader(inStream));
			controller = controller_;
			typeAgent = typeAgent_;
			
			clientId = clientId_;
			agentState = AgentState.waiting;
		}
		
		int getAgentId()
		{
			return clientId;
		}
		
		public void SendMessage(String message)
		{
			controller.SendMessage(message);
			System.out.println("Agent --> SendMessage. Client " + clientId  + ": Sending message to server '" + message + "'");
		}
		
		public void MessageReceived(String message)
		{
			controller.MessageReceived(message);
			System.out.println("Agent --> MessageReceived. Client " + clientId  + ": Received message from server '" + message + "'");
		}

		@Override
		public void run() 			//Implementar el Comportament dels Agents
		{
			try 
			{	
				if (clientId == 1)		//"instantiate#client" + clientId + "," + activeX + "," + activeY + "," + val
				{
					Thread.sleep(2000);
					SendMessage("instantiate#" + clientId + "," + typeAgent + "," + 2 + "," + 3 + "," + 2);
				}
				else if (clientId == 4)		//"instantiate#client" + clientId + "," + activeX + "," + activeY + "," + val
				{
					Thread.sleep(2000);
					SendMessage("instantiate#" + clientId + "," + typeAgent + "," + 6 + "," + 7 + "," + 12);
				}


				
				while((message = reader.readLine()) != null && !message.equals(".")) 
				{
					MessageReceived(message);
				}
				
			} catch (IOException | InterruptedException e) {
			//} catch (IOException e) {
		        System.out.println("ClientReader --> Run. S'ha produit un error al Thread: " + e);  
				e.printStackTrace();
			}
			
		}
	}