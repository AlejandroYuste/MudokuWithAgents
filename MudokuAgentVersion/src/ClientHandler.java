import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

class ClientHandler {
		private Socket server;
		protected int clientId;
		
		ServerReader reader;
		Thread readerThread;
		PrintStream writer;
		ServerNetworkController networkController;
		
		ClientHandler(ServerNetworkController networkController_, Socket server, int clientId_) throws IOException 
		{
			this.server=server;
			networkController = networkController_;
			clientId = clientId_;
			
			reader = new ServerReader(server.getInputStream(), this);
			readerThread = new Thread(reader);
			readerThread.start();
			writer = new PrintStream(server.getOutputStream());
		}
		
		public void MessageReceived(String message)
		{
			networkController.MessageReceived(this, message);
		}
		
		public void SendMessage(String message)
		{
			writer.println(message);
		}
		
		public void ReaderStopped()
		{
			//networkController.ClientDisconnected(agentId);
		}
		
		@Override
		protected void finalize() throws Throwable {
			// TODO Auto-generated method stub
			super.finalize();
			server.close();
		}
	}