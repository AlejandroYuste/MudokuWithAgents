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
	
	@SuppressWarnings("resource")
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
	
	void addMember(int memberId, int memberType, String userName)
	{
		switch (memberType)
		{
			case GameController.passiveUser: case GameController.userContributor: case GameController.userBugReporter:  
			case GameController.userTester: case GameController.userCommitter: case GameController.userLeader:
				gameController.Print("Server: " + userName + " with the ID " + memberId + " has been Connected to server");
				break;
			default:
				gameController.Print("Server: Agent " + memberId + " Connected to server");
				break;
		}
	}
	
	void removeAgent(int memberId, int memberType, String userName)
	{
		switch (memberType)
		{
			case GameController.passiveUser: case GameController.userContributor: case GameController.userBugReporter:  
			case GameController.userTester: case GameController.userCommitter: case GameController.userLeader:
				gameController.Print("Server: User " + userName + " with the ID " + memberId + " has been Disconnected to server");
				break;
			default:
				gameController.Print("Server: Agent " + memberId + " Disconnected to server");
				break;
		}
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
		gameController.Print("Server: Client " + clientId + " has been Disconnected.");
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
