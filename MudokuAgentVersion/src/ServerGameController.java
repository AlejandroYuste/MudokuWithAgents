import java.awt.*;
import java.awt.event.*;
import java.awt.List;
import java.util.ArrayList;

import javax.swing.Timer;

public class ServerGameController extends GameController implements ActionListener 		
{
	// TODO: Quan hi ha votacions i s'elimina un valor aquest no s'elimina correctament!
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static ServerNetworkController networkController;
	Thread serverThread;
	
	Label Title;
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
		
		Title = new Label("Server Log:");
		Title.setSize(250,20);
		Title.setLocation(20, 10);
		add(Title);
		
		console = new List();
		console.setLocation(10, 40);
		console.setSize(745, 560);

		add(console);
		votingExists = false;
		votes = new ArrayList<>();
	}

	public void Print(String message)
	{
		console.add(message);
		console.makeVisible(console.getItemCount()-1);
	}
	
	public void init()
	{
		super.init();
		Initialize();
		InitializeRandomProblem(40);
		Print("Server: Initialized Random Solution.");
		
		networkController = new ServerNetworkController(this);
		serverThread = new Thread(networkController);
		serverThread.start();

		instantiator = new int[sudokuSize][sudokuSize];

		for (int i = 0; i < sudokuSize; i++) {
			for (int j = 0; j < sudokuSize; j++) {
				instantiator[i][j] = -1;
			}
		}
	}
	@Override
	public void paint ( Graphics gr )
	{
		switch(state)
		{
			case game:
				DrawGrid(gr);
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
			SetValueAndState(conflictX, conflictY, -1, 0);
		}
		else
		{
			networkController.BroadcastMessage("committed#" + conflictX + "," + conflictY);
			SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, 6);
		}
	}
	
	String EncodeCurrentStatus()
	{
		String code = "";
		for(int i = 0; i < sudokuSize; i++)
		{
			for(int k = 0; k < sudokuSize; k++)
			{
				if(cells[i][k].valueState != 0)
				{
					code += i + "," + k + "," + cells[i][k].current + "," + cells[i][k].valueState + "," + instantiator[i][k] + "&";
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
			case "connect":
				vars2 = vars[1].split(",");
				int agentId = Integer.parseInt(vars2[0]);
				int agentType = Integer.parseInt(vars2[1]);
				networkController.addAgent(agentId, agentType);
				break;
			case "disconnect":
				vars2 = vars[1].split(",");
				agentId = Integer.parseInt(vars2[0]);
				networkController.removeAgent(agentId);
				break;
			case "instantiate":			 //"instantiate#" + agentId + "," typeAgent + "," + activeX + "," + activeY + "," + val
				vars2 = vars[1].split(",");
				
				agentType = Integer.parseInt(vars2[1]);
				int x =Integer.parseInt(vars2[2]);
				int y = Integer.parseInt(vars2[3]);
				int val = Integer.parseInt(vars2[4]);

				if (agentType == -1)
				{
					String userName = vars2[0];
					Print("Server: " + userName + " Contributed at the Position [" + x +"][" + y +"] with the Value [" + val + "]");		//Acceptem totes les instanciacions		
				}
				else
				{
					agentId = Integer.parseInt(vars2[0]);
					Print("Server: Agent " + agentId + " Contributed at the Position [" + x +"][" + y +"] with the Value [" + val + "]");
				}
				
				switch(agentType)
				{
					case 0:
						SetValueAndState(x, y, val, 2);		//cellState = 2 --> contribution By Rows
						networkController.BroadcastMessage("instantiated#" + x + "," + y + "," + val + "," + 2);	
						break;
					case 1:
						SetValueAndState(x, y, val, 3);		//cellState = 3 --> contribution By Columns
						networkController.BroadcastMessage("instantiated#" + x + "," + y + "," + val + "," + 3);	
						break;
					case 2:	
						SetValueAndState(x, y, val, 4);		//cellState = 4 --> contribution By Squares
						networkController.BroadcastMessage("instantiated#" + x + "," + y + "," + val + "," + 4);	
						break;
					case -1:	
						if (cells[x][y].valueState == 0)
						{
							SetValueAndState(x, y, val, 5);		//cellState = 4 --> contribution By Squares
							networkController.BroadcastMessage("instantiated#" + x + "," + y + "," + val + "," + 5);	
						}
						else
							clientHandler.SendMessage("instantiate_failed");
						
						break;
				}
				
				break;
				
			case "clear":
				
				if(!votingExists)
				{
					vars2 = vars[1].split(",");
					agentId = Integer.parseInt(vars2[0]);
					agentType = Integer.parseInt(vars2[1]);
					conflictX = Integer.parseInt(vars2[2]);
					conflictY = Integer.parseInt(vars2[3]);
					networkController.BroadcastMessage("vote#clear=" + conflictX + "," + conflictY + "," + clientHandler.clientId);
					
					Print("Server: Agent " + agentId + " has tested correctly the position [" + conflictX + "][" + conflictY +"]. Votation for committing.");
					
					votes.clear();
					votingExists = true;
					voteCountTimer = new Timer(voteCountDelay, this);
					voteCountTimer.setActionCommand("voteCount");
					voteCountTimer.start();
				}
				else
					clientHandler.SendMessage("rejected#clear=voting exists");
				
				break;
			case "testerClear":
				vars2 = vars[1].split(",");
				agentId = Integer.parseInt(vars2[0]);
				agentType = Integer.parseInt(vars2[1]);
				int cleanX = Integer.parseInt(vars2[2]);
				int cleanY = Integer.parseInt(vars2[3]);
				
				Print("Server: Agent " + agentId + " has found a bug at the position [" + cleanX + "][" + cleanY +"]. The value will be removed.");
				
				networkController.BroadcastMessage("clear#" + cleanX + "," + cleanY);
				ClearCell(cleanX, cleanY);
				
				break;
			case "voted":
				vars2 = vars[1].split(",");
				agentId = Integer.parseInt(vars2[0]);
				agentType = Integer.parseInt(vars2[1]);
				int conX =Integer.parseInt(vars2[2]);
				int conY = Integer.parseInt(vars2[3]);
				int voteVal = Integer.parseInt(vars2[4]);
				
				if(!votingExists || conflictX != conX || conflictY != conY)
					Print("Server: Received an Unexpected vote from Agent " + agentId + " for the position [" + conX + "][" + conY +"]");
				else
				{				
					if(voteVal == -1)
						Print("Server: Agent " + agentId + " voted to keep the Contribution at the position [" + conX + "][" + conY +"]");
					else if (voteVal == 1)
						Print("Server: Agent " + agentId + " voted to remove the Contribution at the position [" + conX + "][" + conY +"]");
				}
				
				votes.add(voteVal);
				break;
			case "accepted":
				vars2 = vars[1].split(",");
				x = Integer.parseInt(vars2[0]);
				y = Integer.parseInt(vars2[1]);
				
				Print("Server: Project Leader has accepted the value " + cells[x][y].current + " for the position [" + x + "][" + y +"]");
				SetValueAndState(x, y, cells[x][y].current, 7);		//cellState = 4 --> contribution By Squares
				networkController.BroadcastMessage("accepted#" + x + "," + y + "," + cells[x][y].current + "," + 7);
				
				//cells[x][y].IsConstant();		//Potser s'ha d'eliminar si en algun moment s'ha de fer backtracking
				break;
			case "rejected":
				vars2 = vars[1].split(",");
				x = Integer.parseInt(vars2[0]);
				y = Integer.parseInt(vars2[1]);
				
				Print("Server: Project Leader has rejected the value " + cells[x][y].current + " for the position [" + x + "][" + y +"]");
				
				networkController.BroadcastMessage("clear#" + x + "," + y);
				ClearCell(x, y);
				
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
		
		boolean resultVotation = sum >= 0;
		
		if (resultVotation)
			Print("Server: The committers have decided to remove the value by votation.");
		else
			Print("Server: The committers have decided to keep the value by votation.");
		
		return resultVotation;
	}

}