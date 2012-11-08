import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class ServerReader implements Runnable
	{
		BufferedReader reader;
		ClientHandler handler;
		String message;
		boolean threadRunning;
		
		public ServerReader(InputStream inStream_, ClientHandler clientHandler)
		{	
			reader = new BufferedReader(new InputStreamReader(inStream_));
			handler = clientHandler;
			threadRunning = true;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while((message = reader.readLine()) != null && !message.equals(".")) {
					System.out.println("Server received message: " + message);
					handler.MessageReceived(message);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				handler.ReaderStopped();
			}
		}
		
	}