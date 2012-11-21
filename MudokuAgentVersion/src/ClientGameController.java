import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.Timer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomain;

public class ClientGameController extends GameController implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	public enum NetworkState {idle, waitingInit, waitingConfirm}
	NetworkState networkState;
	
	protected enum ActualRol {Observer, Contributor, Tester, Committer, Leader}

	protected ActualRol actualRol;

	Button connectClientButton;
	Label Title;
	Label logLabel;
	Label actualRolLabel;
	
	Label colorServerLabel;
	Label colorRowContributorLabel;
	Label colorColumnContributorLabel;
	Label colorSquareContributorLabel;
	Label colorCommittedLabel;
	Label colorAcceptedLabel;
	Label colorConflictLabel;
	Label colorUserContributorLabel;
	
	Label valuesContributedLabel;
	Label valuesCommittedLabel;
	Label contributedLabel;
	Label committedLabel;
	Label countContributedLabel;
	Label countCommittedLabel;
	Label correctLabel;
	Label valuesLabel;
	Label countLabel;
	
	List console;
	
	int[][] instantiator;
	static ClientNetworkController networkController;
	
	static Color[] clientColors;		// = new Color[]{Color.green, Color.blue, Color.yellow, Color.ORANGE, Color.gray};
	
	String ipField = "127.0.0.1";
	String portField = "4433";
	
	int conflictX;
	int conflictY;
	
	int voteDelay = 10000;
	
	Timer voteTimer;
	
	boolean conflictExists;
	int clearRequester;
	
	public ClientGameController()
	{
		super();
		clearRequester = -1;
		GameController.sudokuSize = 16;
		networkState = NetworkState.idle;
		actualRol = ActualRol.Observer;
		clientColors = new Color[8];
		conflictExists = false;
		
		clientColors[0] = new Color(28, 134, 238);		//Contributed By Rows 		--> dodgerblue 2
		clientColors[1] = new Color(0, 205, 0);			//Contributed By Columns 	--> green 3
		clientColors[2] = new Color(255, 215, 0);		//Contributed By Squares 	--> Gold
		clientColors[3] = new Color(255, 0, 0);			//Contributed by User 		--> red
		clientColors[4] = new Color(142, 56, 142);		//Cell committed			--> sgi beet
		clientColors[5] = new Color(139, 69, 19);		//Cell Accepted				--> Chocolate
		clientColors[6] = new Color(0, 0, 0);			//Cell from the Server		--> black
		clientColors[7] = new Color(220, 20, 60);		//Color de la casella de conflicte --> crimson
	}

	public void init()			//Aqui comença l'execucio del Applet
	{
		super.init();			//Cirdem al init de GameController
		
		// Buttons From Here
		connectClientButton = new Button("Connect to the Server");
		connectClientButton.setSize(200,150);
		connectClientButton.setLocation(275, 100);
		connectClientButton.setActionCommand("connect");
		connectClientButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectClientButton);
		
		// Labels From Here
		Title = new Label("Welcome to Mudoku-Agents Version");
		Font font = new Font("SansSerif", Font.BOLD, 15);
		Title.setFont(font);
		Title.setAlignment(Label.CENTER);
		Title.setSize(400,150);
		Title.setLocation(175, 0);
		add(Title);
		
		logLabel = new Label("Historic of the Game:");
		logLabel.setSize(250,20);
		logLabel.setLocation(gridXOffset + 40, 470);
		logLabel.setVisible(false);
		add(logLabel);
		
		actualRolLabel = new Label("Actual Rol: " + actualRol);
		actualRolLabel.setSize(175, 30);
		font = new Font("SansSerif", Font.PLAIN, 14);
		actualRolLabel.setFont(font);
		actualRolLabel.setLocation(550, 20);
		actualRolLabel.setVisible(false);
		add(actualRolLabel);
		
		//------------------------------------------------------------------
		
		colorServerLabel = new Label("Initialized by Server");
		colorServerLabel.setSize(140,20);
		colorServerLabel.setLocation(600, 420);
		colorServerLabel.setVisible(false);
		add(colorServerLabel);

		colorRowContributorLabel = new Label("Contributed by Rows");
		colorRowContributorLabel.setSize(140,20);
		colorRowContributorLabel.setLocation(600, 440);
		colorRowContributorLabel.setVisible(false);
		add(colorRowContributorLabel);
		
		colorColumnContributorLabel = new Label("Contributed by Columns");
		colorColumnContributorLabel.setSize(140,20);
		colorColumnContributorLabel.setLocation(600, 460);
		colorColumnContributorLabel.setVisible(false);
		add(colorColumnContributorLabel);
		
		colorSquareContributorLabel = new Label("Contributed by Squares");
		colorSquareContributorLabel.setSize(140,20);
		colorSquareContributorLabel.setLocation(600, 480);
		colorSquareContributorLabel.setVisible(false);
		add(colorSquareContributorLabel);
		
		colorUserContributorLabel = new Label("Contributed by User");
		colorUserContributorLabel.setSize(140,20);
		colorUserContributorLabel.setLocation(600, 500);
		colorUserContributorLabel.setVisible(false);
		add(colorUserContributorLabel);
		
		colorCommittedLabel = new Label("Contribution Committed");
		colorCommittedLabel.setSize(140,20);
		colorCommittedLabel.setLocation(600, 520);
		colorCommittedLabel.setVisible(false);
		add(colorCommittedLabel);
		
		colorAcceptedLabel = new Label("Contribution Accepted");
		colorAcceptedLabel.setSize(140,20);
		colorAcceptedLabel.setLocation(600, 540);
		colorAcceptedLabel.setVisible(false);
		add(colorAcceptedLabel);
		
		colorConflictLabel = new Label("Votation Active");
		colorConflictLabel.setSize(140,20);
		colorConflictLabel.setLocation(600, 560);
		colorConflictLabel.setVisible(false);
		add(colorConflictLabel);	
		
		//-------------------------------------------------------------
		
		correctLabel = new Label("Correct");
		correctLabel.setSize(50, 10);
		correctLabel.setLocation(495, 420);
		correctLabel.setVisible(false);
		add(correctLabel);
		
		valuesLabel = new Label("Values");
		valuesLabel.setSize(50, 10);
		valuesLabel.setLocation(497, 435);
		valuesLabel.setVisible(false);
		add(valuesLabel);	
		
		countLabel = new Label(0 + "/" + sudokuSize*sudokuSize);
		countLabel.setSize(50, 10);
		countLabel.setLocation(500, 455);
		countLabel.setVisible(false);
		add(countLabel);

		
		// Components From Here
		
		console = new List();
		console.setLocation(20, 495);
		console.setSize(520, 95);
		console.setVisible(false);
		add(console);
		
		networkController = new ClientNetworkController(this);
		HideVote();					//Ocultem els botons innecessaris
	}

	@Override
	public void paint ( Graphics gr )
	{
		initDraw(gr);		//S'ha d'inicialitzar cada vegada?
		switch(state)
		{
		case game:
			DrawGrid(gr);
			break;
		default:
			System.out.println("ClientGameControllet --> Paint: S'ha entrat al cas per defecte");
			break;
		}
	}
	
	@Override
	public void DrawGrid(Graphics gr) {
		
		if(conflictExists)
		{
			gr.setColor(Color.LIGHT_GRAY);
			int conX = (int) (gridXOffset + conflictX * deltaX);
			int conY = (int) (gridYOffset + conflictY * deltaY);
			gr.fillRect(conX, conY, (int) deltaX, (int) deltaY);
			gr.setColor(clientColors[clearRequester]);
			gr.drawString("Clear Requester", gridEndX + 20, gridYOffset + 100);
		}

		gr.setColor(Color.black);
		Stroke stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		int lineX = gridXOffset;
		float tempLineX = lineX;
		int lineY = gridYOffset;
		float tempLineY = lineY;

		// Draw Vertical Lines
		for (int i = 0; i <= sudokuSize; i++) {
			gr.drawLine(lineX, lineY, lineX, lineY + gridHeight);
			tempLineX += deltaX;
			lineX = (int) tempLineX;
		}

		lineX = gridXOffset;
		lineY = gridYOffset;
		// Draw Horizontal Lines
		for (int i = 0; i <= sudokuSize; i++) {
			gr.drawLine(lineX, lineY, lineX + gridWidth, lineY);
			tempLineY += deltaY;
			lineY = (int) tempLineY;
		}

		// Draw Thick Lines
		stroke = new BasicStroke(3);
		((Graphics2D) gr).setStroke(stroke);

		lineY = gridYOffset;
		lineX = gridXOffset;
		tempLineX = lineX;
		int rootsize = (int) Math.pow(sudokuSize, 0.5);
		for (int i = 0; i <= rootsize; i++) {
			gr.drawLine(lineX, lineY, lineX, lineY + gridHeight);
			tempLineX += deltaX * rootsize;
			lineX = (int) tempLineX;
		}

		lineX = gridXOffset;
		lineY = gridYOffset;
		tempLineY = lineY;
		for (int i = 0; i <= rootsize; i++) {
			gr.drawLine(lineX, lineY, lineX + gridWidth, lineY);
			tempLineY += deltaY * rootsize;
			lineY = (int) tempLineY;
		}

		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		lineX = gridXOffset;
		lineY = gridYOffset;
		tempLineY = lineY;
		tempLineX = lineX;
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		gr.drawLine(20, 460, 470, 460);				//Separador Grid Horizintal
		gr.drawLine(470, 10, 470, 460);				//Separador Grid Vertical-Dreta
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(485, 10, 485, 60);			//Caixa Per al Rol
		gr.drawLine(485, 10, 745, 10);			
		gr.drawLine(745, 10, 745, 60);			
		gr.drawLine(485, 60, 745, 60);
		
		gr.drawLine(565, 410, 565, 590);			//Pintem la llegenda de Colors
		gr.drawLine(565, 410, 745, 410);			
		gr.drawLine(745, 410, 745, 590);			
		gr.drawLine(565, 590, 745, 590);
		
		gr.drawLine(485, 410, 485, 475);			//Caixa de la Puntuacio
		gr.drawLine(485, 410, 550, 410);			
		gr.drawLine(550, 410, 550, 475);			
		gr.drawLine(485, 475, 550, 475);
		
		gr.setColor(clientColors[6]);
		gr.fillRect(582, 425, 10, 10);
		
		gr.setColor(clientColors[0]);
		gr.fillRect(582, 445, 10, 10);
		
		gr.setColor(clientColors[1]);
		gr.fillRect(582, 465, 10, 10);
		
		gr.setColor(clientColors[2]);
		gr.fillRect(582, 485, 10, 10);
		
		gr.setColor(clientColors[3]);
		gr.fillRect(582, 505, 10, 10);
		
		gr.setColor(clientColors[4]);
		gr.fillRect(582, 525, 10, 10);
		
		gr.setColor(clientColors[5]);
		gr.fillRect(582, 545, 10, 10);
		
		gr.setColor(clientColors[7]);
		gr.fillRect(582, 565, 10, 10);
		
		// Draw Values
		for (int y = 0; y < sudokuSize; y++) {
			for (int x = 0; x < sudokuSize; x++) {
				if (cells[x][y].valueState == 0) 			//Si no hi ha cap valor a la cel·la no feim res, pasem la posicio
				{
					tempLineX += deltaX;
					lineX = (int) tempLineX;
					continue;
				}
				else
				{
					switch(cells[x][y].valueState)			
					{
						case 1:
							gr.setColor(clientColors[6]);			//Cell from the Server
							clearRequester = 6;
							break;
						case 2:
							gr.setColor(clientColors[0]);			//Contributed By Rows 
							clearRequester = 0;
							break;
						case 3:
							gr.setColor(clientColors[1]);			//Contributed By Columns
							clearRequester = 1;
							break;
						case 4:
							gr.setColor(clientColors[2]);			//Contributed By Squares
							clearRequester = 2;
							break;
						case 5:
							gr.setColor(clientColors[3]);			//Contributed By User
							clearRequester = 3;
							break;
						case 6:
							gr.setColor(clientColors[4]);			//Cell committed
							clearRequester = 4;
							break;
						case 7:
							gr.setColor(clientColors[5]);			//Cell Accepted
							clearRequester = 5;
							break;
					}
				}
				
				if(cells[x][y].current != -1)
					gr.drawString(String.valueOf(cells[x][y].current), (int) (lineX + deltaX / 2) - 5, (int) (lineY + deltaY) - 10);
				
				tempLineX += deltaX;
				lineX = (int) tempLineX;
			}
			
			tempLineY += deltaY;
			lineY = (int) tempLineY;
			lineX = gridXOffset;
			tempLineX = lineX;
		}
		
		//gr.setColor(Color.black);
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);

		switch(networkState)
		{
		case idle:
			cells[activeX][activeY].DrawDomain(gr, mouseOverDomainIndex, activeX, activeY, actualRol);
			break;
		case waitingConfirm:
			gr.drawString("Waiting response from server", gridXOffset, gridEndY + 20);
			break;
		default:
			break;
		}

		if (mouseOverGrid) {
			gr.setColor(mouseOverColor);
			gr.drawRect((int) (gridXOffset + mouseOverX * deltaX), (int) (gridYOffset + mouseOverY * deltaY), (int) deltaX, (int) deltaY);
		}

		gr.setColor(activeCellColor);
		int activeRectX = (int) (gridXOffset + activeX * deltaX);
		int activeRectY = (int) (gridYOffset + activeY * deltaY);

		// DrawActive
		gr.drawRect(activeRectX, activeRectY, (int) deltaX, (int) deltaY);

	}

	@Override
	public void actionPerformed(ActionEvent action) {
		System.out.println("S'ha produit una accio");
		super.actionPerformed(action);
		// TODO Auto-generated method stub
		switch(action.getActionCommand())
		{
		case "connect":
			if(!ipField.equals("") && !portField.equals(""))
			{
				try {

					networkController.Connect(ipField, Integer.parseInt(portField));
					RequestInit();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Can not connect to server");
				}  
			}
			else
			{
				System.out.println("Check ip & port");
			}
			break;
		case "voteEnd":
			conflictExists = false;
			HideVote();
			voteTimer.stop();
			break;
		case "yes":
			if(conflictExists)
			{
				networkController.SendMessage("voted#0,0," + conflictX + "," + conflictY + "," + "1");
				conflictExists = false;
				HideVote();
			}
			break;
		case "no":
			if(conflictExists)
			{
				networkController.SendMessage("voted#0,0," + conflictX + "," + conflictY + "," + "-1");
				conflictExists = false;
				HideVote();
			}
			break;
		case "askToClear":
			networkController.SendMessage("clear#0,0," + activeX + "," + activeY);
			break;
		}
	}
	public void ShowVote()
	{
		//conflictLabel.setText("Clear Cell<" + conflictX + "," + conflictY + ">?");

	}
	
	public void HideVote()
	{

	}
	
	public void RequestInit()
	{
		networkController.SendMessage("request#type=init");
		networkState = NetworkState.waitingInit;
	}
	
	@Override
	public void DomainClick(int index)
	{
		IntDomain idom = cpController.GetCPVariable(activeX, activeY).getDomain();
		DisposableIntIterator iter = idom.getIterator();
		int val = 1;
		for(;index >= 0 && iter.hasNext(); index--)
		{
			val = iter.next();
		}

		//send click to server
		System.out.println("Asking to instantiate " + activeX + "," + activeY + " : " + val);
		networkState = NetworkState.waitingConfirm;
		networkController.SendMessage("instantiate#0,0," + activeX + "," + activeY + "," + val);
		repaint();
	}
	
	@Override
	public void Initialize()
	{
		super.Initialize();
		instantiator = new int[sudokuSize][sudokuSize];
		
		for (int i = 0; i < sudokuSize; i++) {
			for (int j = 0; j < sudokuSize; j++) {
				instantiator[i][j] = -1;
			}
		}

		/*clearButton = new Button("Clear");
		clearButton.setLocation(gridXOffset, CellVariable.domainYOffset + (int)deltaY + 5);
		clearButton.setSize(70,20);
		clearButton.setActionCommand("askToClear");
		clearButton.addActionListener(this);
		add(clearButton);
		clearButton.setVisible(false);*/
	}
	
	public void MessageReceived(String message)
	{
		String[] vars = message.split("#");
		String[] vars2 = null;
		String equation = null;
		try {
			switch(vars[0])
			{
			case "init":
				System.out.println("Client: Initializing client for " + message);
				
				for(int i = 1; i < vars.length; i++)
				{
					equation = vars[i];
					vars2 = equation.split("=");
					
					switch(vars2[0])
					{
					case "ss":// sudoku size
						sudokuSize = Integer.parseInt(vars2[1]);
						Initialize();
						break;
					case "ci":// client id
						networkController.clientId = Integer.parseInt(vars2[1]);
						break;
					case "iv":// initial values
						addToGrid(vars2[1]);
						break;
					default:
						System.out.println(vars2[0]);
						break;
					}
				}
				StartGame();
				
				networkState = NetworkState.idle;
				repaint();
				break;
			case "instantiated":
				addToGrid(vars[1]);
				networkState = NetworkState.idle;
				CellClick(activeX, activeY);
				break;
			case "instantiate_failed":
					networkState = NetworkState.idle;
				break;
			case "vote":
				vars2 = vars[1].split("=");
				String[] coordinates;
				
				switch(vars2[0])
				{
					case "clear":
						conflictExists = true;
						
						repaint();
						coordinates = vars2[1].split(",");
						conflictX = Integer.parseInt(coordinates[0]);
						conflictY = Integer.parseInt(coordinates[1]);
						clearRequester = Integer.parseInt(coordinates[2]);
						
						//networkController.setPositionConflic(conflictX, conflictY);
						
						voteTimer = new Timer(voteDelay, this);
						voteTimer.setActionCommand("voteEnd");
						voteTimer.start();
						break;
					}
				break;
			case "clear":
				vars2 = vars[1].split(",");
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				ClearCell(conflictX, conflictY);
				break;
			case "committed":
				//conflictExists = false;
				//voteTimer.stop();
				
				vars2 = vars[1].split(",");
				
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				
				SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, 6);
				break;	
			case "accepted":		//networkController.BroadcastMessage("accepted#" + x + "," + y + "," + cells[x][y].current + "," + 7);
		
				vars2 = vars[1].split(",");
				int x = Integer.parseInt(vars2[0]);
				int y = Integer.parseInt(vars2[1]);
				SetValueAndState(x, y, cells[x][y].current, 7);				
				break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToGrid(String code)
	{
		String[] values = code.split("&");
		for(String value : values)
		{
			String[] component = value.split(",");
			
			int x = Integer.parseInt(component[0]);
			int y = Integer.parseInt(component[1]);
			int val = Integer.parseInt(component[2]);
			int state = Integer.parseInt(component[3]);
			
			SetValueAndState(x, y, val, state);
		}
	}
	
	@Override
	public void StartGame()
	{
		connectClientButton.setVisible(false);
		Title.setVisible(false);
		
		console.setVisible(true);
		logLabel.setVisible(true);
		actualRolLabel.setVisible(true);
		
		colorServerLabel.setVisible(true);
		colorRowContributorLabel.setVisible(true);
		colorColumnContributorLabel.setVisible(true);
		colorSquareContributorLabel.setVisible(true);
		colorUserContributorLabel.setVisible(true);
		colorCommittedLabel.setVisible(true);
		colorAcceptedLabel.setVisible(true);
		colorConflictLabel.setVisible(true);
		
		correctLabel.setVisible(true);
		valuesLabel.setVisible(true);
		countLabel.setVisible(true);
		
		state = GameState.game;
	}
	
	@Override
	public void CellClick(int x, int y) {
		activeX = x;
		activeY = y;
		
		
		/*if(instantiator[x][y] > -1)
		{
			clearButton.setVisible(true);
		}
		else
		{
			clearButton.setVisible(false);
		}*/
		repaint();
	}
	
	int getCountContributed()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++)
		{
			for(int j=0;j<sudokuSize;j++)
			{
				if(cells[i][j].valueState == 2 || cells[i][j].valueState == 3 || cells[i][j].valueState == 4 || cells[i][j].valueState == 5)
					count++;
			}
				
		}
		
		return count;
	}
	
	int getCountCommitted()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++)
		{
			for(int j=0;j<sudokuSize;j++)
			{
				if(cells[i][j].valueState == 6)
					count++;
			}
				
		}
		
		return count;
	}
	
	int getCountCorrect()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++)
		{
			for(int j=0;j<sudokuSize;j++)
			{
				if(cells[i][j].valueState == 1 || cells[i][j].valueState == 7)
					count++;
			}
				
		}
		
		return count;
	}
}