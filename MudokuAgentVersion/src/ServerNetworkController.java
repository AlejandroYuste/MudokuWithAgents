import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerNetworkController implements Runnable {

	int maxConnections=0;
	int port = 4433;
	
	ArrayList<ClientHandler> clientFramework;
	ArrayList<AgentHandler> listAgentsConnected;
	ServerGameController gameController;
	
	public ServerNetworkController(ServerGameController gameController_)
	{
		clientFramework = new ArrayList<ClientHandler>();
		gameController = gameController_;
	}
	
	public void MessageReceived(ClientHandler handler, String message)
	{
		gameController.MessageReceived(handler, message);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i=0;

		try{
			
			ServerSocket listener = new ServerSocket(port);
			Socket server;
			
			while((i++ < maxConnections) || (maxConnections == 0)){

				gameController.Print("Server: Waiting for connection");
				server = listener.accept();
				
				clientFramework.add(new ClientHandler(this, server, NextClientId()));
			}
			
		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}
	
	void addClient(int clientId, int typeAgent)
	{
		listAgentsConnected.add(new AgentHandler(clientId, typeAgent));
		gameController.Print("Server: Agent " + clientId + " Connected to server");
		gameController.Print("Server: Hi ha connectats: " + GetAgentCount() + "Agents");
	}
	
	
	public void BroadcastMessage(String message)
	{
		for(ClientHandler client : clientFramework)
		{
			client.SendMessage(message);
		}
	}
	
	public void ClientDisconnected(int clientId)
	{
		gameController.Print("client " + clientId + " disconnected");
		for(int i = 0; i < clientFramework.size(); i++)
		{
			if(clientFramework.get(i).clientId == clientId)
			{
				clientFramework.remove(clientId);
			}
		}
	}
	
	
	int NextClientId()
	{
		int clientId = 0;
		for(ClientHandler client : clientFramework)
		{
			if(clientId == client.clientId)
			{
				clientId++;
			}
			else if(clientId < client.clientId)
			{
				return clientId;
			}
		}
		return clientId;
	}
	
	public int GetClientCount()
	{
		return clientFramework.size();
	}
	
	public int GetAgentCount()
	{
		return listAgentsConnected.size();
	}
}
