import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


class AgentReader implements Runnable
{
	BufferedReader reader;  

	AgentNetworkController controller;
	String message;
	
	public AgentReader(InputStream inStream, AgentNetworkController controller_)
	{
		reader = new BufferedReader(new InputStreamReader(inStream));
		controller = controller_;
	}

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
