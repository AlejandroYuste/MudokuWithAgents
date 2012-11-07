import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerNetworkController implements Runnable {

	int maxConnections=0;
	int port = 4433;
	
	static int lastClientId = 0;
	
	ArrayList<ClientHandler> clients;
	ServerGameController gameController;
	
	public ServerNetworkController(ServerGameController gameController_)
	{
		clients = new ArrayList<ClientHandler>();
		
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
				gameController.Print("Server: Client " + NextClientId() + " Connected to server");
				
				clients.add(new ClientHandler(this, server, NextClientId()));
				
			}
		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}
	
	int NextClientId()
	{
		int clientId = 0;
		for(ClientHandler client : clients)
		{
			if(clientId == client.agentId)
			{
				clientId++;
			}
			else if(clientId < client.agentId)
			{
				return clientId;
			}
		}
		return clientId;
	}
	
	public void BroadcastMessage(String message)
	{
		for(ClientHandler client : clients)
		{
			client.SendMessage(message);
		}
	}
	
	public void ClientDisconnected(int clientId)
	{
		gameController.Print("client " + clientId + " disconnected");
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).agentId == clientId)
			{
				clients.remove(clientId);
			}
		}
	}
	
	public int GetClientCount()
	{
		return clients.size();
	}
}
