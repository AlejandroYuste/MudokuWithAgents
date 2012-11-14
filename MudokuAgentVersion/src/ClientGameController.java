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

	public enum NetworkState { idle, waitingInit, waitingConfirm}

	NetworkState networkState;

	Button connectClientButton;
	Button clearButton;
	Button yesButton;
	Button noButton;
	Label conflictLabel;
	Label Title;
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
		clientColors = new Color[7];
		conflictExists = false;
		
		clientColors[0] = new Color(58, 95, 205);
		clientColors[1] = new Color(0, 205, 102);
		clientColors[2] = new Color(255, 215, 0);
		clientColors[3] = new Color(142, 56, 142);
		clientColors[4] = new Color(142, 142, 56);
		clientColors[5] = new Color(255, 127, 0);
		clientColors[6] = new Color(0, 206, 209);
	}

	public void init()			//Aqui comença l'execucio del Applet
	{
		super.init();			//Cirdem al init de GameController
		
		Title = new Label("Welcome to Mudoku-Agents Version");
		Title.setAlignment(Label.CENTER);
		Title.setSize(250,20);
		Title.setLocation(200, 20);
		add(Title);

		connectClientButton = new Button("Connect to the Server");
		connectClientButton.setSize(200,150);
		connectClientButton.setLocation(220, 100);
		connectClientButton.setActionCommand("connect");
		connectClientButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectClientButton);
		
		
		yesButton = new Button("Yes");
		yesButton.setLocation(gridEndX + 20, gridYOffset + 30);
		yesButton.setSize(70,20);
		yesButton.setActionCommand("yes");
		yesButton.addActionListener(this);
		add(yesButton);
		
		noButton = new Button("No");
		noButton.setLocation(gridEndX + 20, gridYOffset + 60);
		noButton.setSize(70,20);
		noButton.setActionCommand("no");
		noButton.addActionListener(this);
		add(noButton);
		
		conflictLabel = new Label("label");
		conflictLabel.setLocation(gridEndX + 20, gridYOffset);
		conflictLabel.setSize(120,20);
		add(conflictLabel);
		
		conflictLabel.setText("Conflict on ");
		
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
		
		// Draw Values
		for (int y = 0; y < sudokuSize; y++) {
			for (int x = 0; x < sudokuSize; x++) {
				if (cells[x][y].current == -1) {
					gr.setColor(Color.black);
					/*if(cpController.GetCPVariable(x, y).getDomainSize() == 1)
					{
						gr.drawString("*", (int) (lineX + deltaX / 2) - 5,
								(int) (lineY + deltaY) - 10);
					}*/
					tempLineX += deltaX;
					lineX = (int) tempLineX;
					continue;
				}
				else if(cells[x][y].contradicting)
				{
					gr.setColor(Color.red);
				}
				else
				{
					if(instantiator[x][y] == -1)
					{
						gr.setColor(Color.black);
					}
					else
					{
						gr.setColor(clientColors[instantiator[x][y]]);
					}
				}
				
				gr.drawString(String.valueOf(cells[x][y].current), (int) (lineX + deltaX / 2) - 5, (int) (lineY + deltaY) - 10);
				tempLineX += deltaX;
				lineX = (int) tempLineX;
			}
			tempLineY += deltaY;
			lineY = (int) tempLineY;
			lineX = gridXOffset;
			tempLineX = lineX;
		}
		gr.setColor(Color.black);
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);

		switch(networkState)
		{
		case idle:
			cells[activeX][activeY].DrawDomain(gr, mouseOverDomainIndex, activeX, activeY);
			break;
		case waitingConfirm:
			gr.drawString("Waiting response from server", gridXOffset, gridEndY + 20);
			break;
		default:
			break;
		}

		if (mouseOverGrid) {
			gr.setColor(mouseOverColor);
			gr.drawRect((int) (gridXOffset + mouseOverX * deltaX),
					(int) (gridYOffset + mouseOverY * deltaY), (int) deltaX,
					(int) deltaY);
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
		conflictLabel.setText("Clear Cell<" + conflictX + "," + conflictY + ">?");
		conflictLabel.setVisible(true);
		yesButton.setVisible(true);
		noButton.setVisible(true);
	}
	
	public void HideVote()
	{
		conflictLabel.setVisible(false);
		yesButton.setVisible(false);
		noButton.setVisible(false);
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

		clearButton = new Button("Clear");
		clearButton.setLocation(gridXOffset, CellVariable.domainYOffset + (int)deltaY + 5);
		clearButton.setSize(70,20);
		clearButton.setActionCommand("askToClear");
		clearButton.addActionListener(this);
		add(clearButton);
		clearButton.setVisible(false);
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

						AssignFromCode(vars2[1]);
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
				AssignFromCode(vars[1]);
				networkState = NetworkState.idle;
				CellClick(activeX, activeY);
				break;
			case "instantiate_failed":
					networkState = NetworkState.idle;
				break;
			case "vote":
				vars2 = vars[1].split("=");
				switch(vars2[0])
				{
				case "clear":
					String[] coordinates = vars2[1].split(",");
					conflictX = Integer.parseInt(coordinates[0]);
					conflictY = Integer.parseInt(coordinates[1]);
					clearRequester = Integer.parseInt(coordinates[2]);
					conflictExists = true;
					voteTimer = new Timer(voteDelay, this);
					voteTimer.setActionCommand("voteEnd");
					voteTimer.start();
					ShowVote();
					break;
				}
				break;
			case "clear":
				vars2 = vars[1].split(",");
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				ClearCell(conflictX, conflictY);
				break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void AssignFromCode(String code)
	{
		String[] values = code.split("&");
		for(String value : values)
		{
			String[] component = value.split(",");
			int x = Integer.parseInt(component[0]);
			int y = Integer.parseInt(component[1]);
			int val = Integer.parseInt(component[2]);
			SetValue(x, y, val);
			instantiator[x][y] = Integer.parseInt(component[3]);
		}
		try {
			cpController.Propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void StartGame()
	{
		connectClientButton.setVisible(false);
		Title.setVisible(false);
		
		state = GameState.game;
	}
	
	@Override
	public void CellClick(int x, int y) {
		activeX = x;
		activeY = y;
		if(instantiator[x][y] > -1)
		{
			clearButton.setVisible(true);
		}
		else
		{
			clearButton.setVisible(false);
		}
		repaint();
	}
}