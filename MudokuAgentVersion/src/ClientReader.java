import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


	class ClientReader implements Runnable
	{
		DataInputStream reader;
		ClientNetworkController controller;
		String message;
		
		public ClientReader(InputStream inStream, ClientNetworkController controller_)
		{
			reader = new DataInputStream(inStream);
			controller = controller_;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while((message = reader.readLine()) != null && !message.equals(".")) {
					System.out.println("Client received message: " + message);
					controller.MessageReceived(message);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}