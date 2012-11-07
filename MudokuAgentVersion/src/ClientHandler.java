import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

class ClientHandler {
		private Socket server;
		protected int agentId;
		protected int agentType;
		
		ServerReader reader;
		Thread readerThread;
		PrintStream writer;
		ServerNetworkController networkController;
		
		ClientHandler(ServerNetworkController networkController_, Socket server, int agentId_) throws IOException 
		{
			this.server=server;
			networkController = networkController_;
			agentId = agentId_;
			
			reader = new ServerReader(server.getInputStream(), this);
			readerThread = new Thread(reader);
			readerThread.start();
			writer = new PrintStream(server.getOutputStream());
			
			System.out.println("Client " + agentId_ + " connected to handler");
		}

		int getAgentId()
		{
			return agentId;
		}
		
		void setAgentId(int agentId_)
		{
			agentId = agentId_;
		}
		
		int getAgentType()
		{
			return agentId;
		}
		
		void setAgentType(int agentType_)
		{
			agentType = agentType_;
		}
		
		public void SendMessage(String message)
		{
			writer.println(message);
		}
		public void ReaderStopped()
		{
			networkController.ClientDisconnected(agentId);
		}
		public void MessageReceived(String message)
		{
			networkController.MessageReceived(this, message);
		}
		
		@Override
		protected void finalize() throws Throwable {
			// TODO Auto-generated method stub
			super.finalize();
			server.close();
		}
	}