import java.awt.*;
import java.awt.event.*;
import java.awt.List;
import java.util.ArrayList;

import javax.swing.Timer;

public class ServerGameController extends GameController implements ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static ServerNetworkController networkController;
	Thread serverThread;
	List console;

	int[][] instantiator;

	boolean votingExists;

	ArrayList<Integer> votes;

	int conflictX;
	int conflictY;

	int voteCountDelay = 12000;
	Timer voteCountTimer;

	public ServerGameController()
	{
		super();
		setLayout(null);
		GameController.sudokuSize = 16;
		console = new List();
		console.setSize(500, 500);

		add(console);
		votingExists = false;
		votes = new ArrayList<>();
	}

	public void Print(String message)
	{
		console.add(message);
	}
	public void init()
	{
		super.init();
		Initialize();
		InitializeRandomProblem(40);
		networkController = new ServerNetworkController(this);
		serverThread = new Thread(networkController);
		serverThread.start();


		instantiator = new int[sudokuSize][sudokuSize];

		for (int i = 0; i < sudokuSize; i++) {
			for (int j = 0; j < sudokuSize; j++) {
				instantiator[i][j] = -1;
			}
		}

		/*try {
			networkController.Connect("127.0.0.1", 4433);
			networkController.SendMessage("Hello from Mudoku!");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	@Override
	public void paint ( Graphics gr )
	{
		switch(state)
		{
			case game:
				DrawGrid(gr);
				break;
			case conflictResolution:
				break;
			case initGame:
				break;
			case pregame:
				break;
			case start:
				break;
			default:
				break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		//super.actionPerformed(action);
		switch(action.getActionCommand())
		{
		case "voteCount":
			ConcludeVoting();
			break;
		}
	}
	public void ConcludeVoting()
	{
		votingExists = false;
		voteCountTimer.stop();
		if(EvaluateVote())
		{
			networkController.BroadcastMessage("clear#" + conflictX + "," + conflictY);
			ClearCell(conflictX, conflictY);
		}
	}
	String EncodeCurrentStatus()
	{
		String code = "";
		for(int i = 0; i < sudokuSize; i++)
		{
			for(int k = 0; k < sudokuSize; k++)
			{
				if(cells[i][k].current != -1)
				{
					code += i + "," + k + "," + cells[i][k].current + "," + instantiator[i][k] + "&";
				}
			}
		}
		return code.substring(0, code.length() - 1);
	}
	public synchronized void MessageReceived(ClientHandler clientHandler, String message)
	{
		String[] vars = message.split("#");
		String[] vars2 = null;
		try {
			switch(vars[0])
			{
			case "request":
				vars2 = vars[1].split("=");
				if(!vars2[0].contains("type"))
				{
					throw new Exception("Network message parse error");
				}
				switch(vars2[1])
				{
				case "init":
					String response = "init#ss=" + sudokuSize + "#iv=" + EncodeCurrentStatus();
					Print("Server: Sent Grid to Applet Agents");
					clientHandler.SendMessage(response);
					break;
				}
				break;
			case "instantiate":						//"instantiate#" + agentId + "," typeAgent + "," + activeX + "," + activeY + "," + val
				vars2 = vars[1].split(",");
				clientHandler.setAgentId(Integer.parseInt(vars2[0]));
				clientHandler.setAgentType(Integer.parseInt(vars2[1]));
				int x =Integer.parseInt(vars2[2]);
				int y = Integer.parseInt(vars2[3]);
				int val = Integer.parseInt(vars2[4]);
				Print("Client " + clientHandler.getAgentId() + " asked to instantiate [" + x + "," + y + "] : " + val );
				
				if(TryInstantiate(x,y,val))
				{
					Print("Instantiation succeeded");
					instantiator[x][y] = clientHandler.getAgentType();
					networkController.BroadcastMessage("instantiated#" + x + "," + y + "," + val + "," + instantiator[x][y]);	
				}
				else
				{
					Print("Instantiation failed");
					clientHandler.SendMessage("instantiate_failed");
				}
				break;
			case "clear":
				if(!votingExists)
				{
					vars2 = vars[1].split(",");
					conflictX =Integer.parseInt(vars2[0]);
					conflictY = Integer.parseInt(vars2[1]);

					Print("Client " + clientHandler.agentId + " asked to clear " + conflictX + "," + conflictY);
					networkController.BroadcastMessage("vote#clear=" + conflictX + "," + conflictY + "," + clientHandler.agentId);
					votes.clear();
					votingExists = true;
					voteCountTimer = new Timer(voteCountDelay, this);
					voteCountTimer.setActionCommand("voteCount");
					voteCountTimer.start();
				}
				else
				{
					clientHandler.SendMessage("rejected#clear=voting exists");
				}
				break;
			case "voted":
				vars2 = vars[1].split(",");
				int conX =Integer.parseInt(vars2[0]);
				int conY = Integer.parseInt(vars2[1]);
				if(!votingExists || conflictX != conX || conflictY != conY)
				{
					Print("Unexpected vote");
				}
				Integer voteVal = Integer.parseInt(vars2[2]);
				Print("vote received from client " + clientHandler.agentId + " : " + voteVal);
				votes.add(voteVal);
				if(votes.size() == networkController.GetClientCount())
				{
					ConcludeVoting();
				}
				break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean EvaluateVote()
	{
		int sum = 0;
		for(int i : votes)
		{
			sum += i;
		}
		Print("Votes evaluated to " + String.valueOf(sum > 0));
		return sum > 0;
	}

}