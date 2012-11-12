import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerNetworkController implements Runnable {

	int maxConnections=0;
	int port = 4433;
	
	ArrayList<ClientHandler> clientFramework;
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

				gameController.Print("Server: Waiting for Clients");
				server = listener.accept();
				gameController.Print("Server: Client " + NextClientId()  + " Connected.");
				
				clientFramework.add(new ClientHandler(this, server, NextClientId()));
				
			}
			
		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}
	
	void addAgent(int clientId, int typeAgent)
	{
		gameController.Print("Server: Agent " + clientId + " Connected to server");
	}
	
	void removeAgent(int clientId)
	{
		gameController.Print("Server: Agent " + clientId + " Disconnected to server");
	}
	
	
	public void BroadcastMessage(String message)
	{
		for(ClientHandler client : clientFramework)
		{
			//gameController.Print("Enviat un Missatge Broadcast al client: " + client);
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
}
