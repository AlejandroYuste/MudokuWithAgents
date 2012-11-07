import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.Timer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomain;

public class AgentGameController extends GameController implements ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum NetworkState {idle, waitingInit, waitingConfirm}

	NetworkState networkState;

	Label Title;
	Label NumAgentsLabel;
	Label TypeAgentsLabel;
	Label AddNewAgents;
	TextField NumAgentsField;
	Choice TypeAgentsField;

	Button connectButton;
	Button clearButton;
	//Button yesButton;
	//Button noButton;
	//Label conflictLabel;
	
	int[][] instantiator;
	static AgentNetworkController networkController;
	
	String ipField = "127.0.0.1";
	String portField = "4433";
	
	boolean firstTimeConnect;
	
	static Color[] agentColors;		// = new Color[]{Color.green, Color.blue, Color.yellow, Color.ORANGE, Color.gray};
	
	int conflictX;
	int conflictY;
	
	int voteDelay = 10000;
	
	Timer voteTimer;
	
	boolean conflictExists;
	int clearRequester;
	
	public AgentGameController()
	{
		super();
		clearRequester = -1;
		GameController.sudokuSize = 16;
		networkState = NetworkState.idle;
		agentColors = new Color[7];
		conflictExists = false;
		
		agentColors[0] = new Color(58, 95, 205);
		agentColors[1] = new Color(0, 205, 102);
		agentColors[2] = new Color(255, 215, 0);
		agentColors[3] = new Color(142, 56, 142);
		agentColors[4] = new Color(142, 142, 56);
		agentColors[5] = new Color(255, 127, 0);
		agentColors[6] = new Color(0, 206, 209);
	}

	public void init()										//Aqui comença l'execucio del Applet
	{
		super.init();										//Cirdem al init de GameController
		
		// Components from Here:
		
		NumAgentsField = new TextField(20);
		NumAgentsField.setSize(150,20);
		NumAgentsField.setLocation(gridXOffset, 100);
		NumAgentsField.setText("1");			//127.0.0.1
		add(NumAgentsField);
		
		TypeAgentsField = new Choice();
		TypeAgentsField.add("Type 1");
		TypeAgentsField.add("Type 2");
		TypeAgentsField.add("Type 3");
		
		TypeAgentsField.setSize(100,20);
		TypeAgentsField.setLocation(gridXOffset + 150 + 20, 100);
		add(TypeAgentsField);
		
		//Buttons from here!
		
		connectButton = new Button("Connect Agents");
		connectButton.setSize(120,20);
		connectButton.setLocation(gridXOffset + 150 + 100 + 80, 100);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectButton);
		
		/*yesButton = new Button("Yes");
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
		add(noButton);*/
		
		
		// Labels From here:
		
		Title = new Label("Welcome to Mudoku-Agents Version");
		Title.setAlignment(Label.CENTER);
		Title.setSize(250,20);
		Title.setLocation(200, 20);
		add(Title);
		
		AddNewAgents = new Label("Add New Agents:");
		AddNewAgents.setSize(250,20);
		AddNewAgents.setLocation(gridXOffset, 475);
		add(AddNewAgents);
		AddNewAgents.setVisible(false);
		
		NumAgentsLabel = new Label("Number of Agents:");
		NumAgentsLabel.setSize(150,20);
		NumAgentsLabel.setLocation(gridXOffset, 80);
		add(NumAgentsLabel);
		
		TypeAgentsLabel = new Label("Select Type Agents:");
		TypeAgentsLabel.setSize(100,20);
		TypeAgentsLabel.setLocation(gridXOffset + 150 + 20, 80);
		add(TypeAgentsLabel);
		
		/*conflictLabel = new Label("label");
		conflictLabel.setLocation(gridEndX + 20, gridYOffset);
		conflictLabel.setSize(120,20);
		add(conflictLabel);
		
		conflictLabel.setText("Conflict on ");*/
		
		System.out.println("gridEndX: " + gridEndX);
		System.out.println("gridYOffset: " + gridYOffset);
		System.out.println("gridEndY: " + gridEndY);
		System.out.println("gridXOffset: " + gridXOffset);
		
		firstTimeConnect = true;
		networkController = new AgentNetworkController(this);
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
	public void DrawGrid(Graphics gr) {
		
		if(conflictExists)
		{
			gr.setColor(Color.LIGHT_GRAY);
			int conX = (int) (gridXOffset + conflictX * deltaX);
			int conY = (int) (gridYOffset + conflictY * deltaY);
			gr.fillRect(conX, conY, (int) deltaX, (int) deltaY);
			gr.setColor(agentColors[clearRequester]);
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

		gr.drawLine(20, 460, 470, 460);		//Linia Entre el Sudoku i Connectar mes Agents
		
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
					if(cpController.GetCPVariable(x, y).getDomainSize() == 1)
					{
						gr.drawString("*", (int) (lineX + deltaX / 2) - 5,
								(int) (lineY + deltaY) - 10);
					}
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
						gr.setColor(agentColors[instantiator[x][y]]);
					}
				}
				
				gr.drawString(
						String.valueOf(cpController.GetCPVariable(x, y).getVal()), (int) (lineX + deltaX / 2) - 5,
						(int) (lineY + deltaY) - 10);
				tempLineX += deltaX;
				lineX = (int) tempLineX;
			}
			
			tempLineY += deltaY;
			lineY = (int) tempLineY;
			lineX = gridXOffset;
			tempLineX = lineX;
		}
		
		gr.setColor(Color.pink);
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);

		switch(networkState)
		{
		case idle:
			cells[activeX][activeY].DrawDomain(gr, mouseOverDomainIndex);		//Aqui pintem els Possibles Valors Abaix del Sudoku
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
		
		super.actionPerformed(action);
		
		switch(action.getActionCommand())
		{
			case "connect":
				if(!ipField.equals("") && !portField.equals(""))
				{
					try {	
						networkController.Connect(ipField, Integer.parseInt(portField), Integer.parseInt(NumAgentsField.getText()), TypeAgentsField.getSelectedIndex());

						//networkController.Connect(ipField, Integer.parseInt(portField), NumAgentsLabel.getText(), );
						if (firstTimeConnect)
						{
							firstTimeConnect = false;
							RequestInit();					//Per afegir el Grid al Applet dels Agents
						}
						
						//RequestInit();
					} catch (IOException e) {
						System.out.println("Can not connect to server: " + e);
						e.printStackTrace();
					}  
				}
				else
				{
					System.out.println("Check --> IP & port incorrect");
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
					networkController.SendMessage("voted#" + conflictX + "," + conflictY + "," + "1");
					conflictExists = false;
					HideVote();
				}
				break;
			case "no":
				if(conflictExists)
				{
					networkController.SendMessage("voted#" + conflictX + "," + conflictY + "," + "-1");
					conflictExists = false;
					HideVote();
				}
				break;
			case "askToClear":
				networkController.SendMessage("clear#" + activeX + "," + activeY);
				break;
		}
	}
	
	public void ShowVote()
	{
		/*conflictLabel.setText("Clear Cell<" + conflictX + "," + conflictY + ">?");
		conflictLabel.setVisible(true);
		yesButton.setVisible(true);
		noButton.setVisible(true);*/
	}
	
	public void HideVote()
	{
		/*conflictLabel.setVisible(false);
		yesButton.setVisible(false);
		noButton.setVisible(false);*/
	}
	
	public void RequestInit()
	{
		networkController.SendMessage("request#type=init");			//Aqui no es important el clientId ja que nomes hi ha un framework
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
		/*System.out.println("Asking to instantiate " + activeX + "," + activeY + " : " + val);
		networkState = NetworkState.waitingConfirm;
		networkController.SendMessage("instantiate#" + activeX + "," + activeY + "," + val);*/
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
		System.out.println("AgentGameController --> Message Received: " + message);
		
		String[] vars = message.split("#");
		String[] vars2 = null;
		String equation = null;
		try {
			switch(vars[0])
			{
			case "init":
				System.out.println("Initializing Agents Applet");
				
				for(int i = 1; i < vars.length; i++)
				{
					equation = vars[i];
					vars2 = equation.split("=");
					
					switch(vars2[0])
					{
					case "ss":		//sudoku size
						sudokuSize = Integer.parseInt(vars2[1]);
						Initialize();
						break;
					case "iv":		// initial values
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
			e.printStackTrace();
		}
	}
	
	public void AssignFromCode(String code)				//Afegim un nou valor al Grid
	{
		String[] values = code.split("&");
		for(String value : values)
		{
			String[] component = value.split(",");
			int x = Integer.parseInt(component[0]);
			int y = Integer.parseInt(component[1]);
			int val = Integer.parseInt(component[2]);
			SetValue(x, y, val);
			instantiator[x][y] = Integer.parseInt(component[3]);		//Color amb el que afegirem el nou valor
		}
		try {
			cpController.Propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void StartGame()
	{
		Title.setVisible(false);
		AddNewAgents.setVisible(true);
		
		NumAgentsLabel.setLocation(gridXOffset, 500);
		//NumAgentsLabel.setForeground(Color.green);			//Podem Canviar el Colors dels Menus
		NumAgentsField.setLocation(gridXOffset, 520);
		
		TypeAgentsLabel.setLocation(gridXOffset + 150 + 20, 500);
		TypeAgentsField.setLocation(gridXOffset + 150 + 20, 520);
		
		connectButton.setLocation(gridXOffset + 150 + 100 + 80, 520);
		state = GameState.game;
	}
	
	@Override
	public void CellClick(int x, int y) {
		activeX = x;
		activeY = y;
		
		if(instantiator[x][y] > -1)
		{
			//clearButton.setVisible(true);
		}
		else
		{
			clearButton.setVisible(false);
		}
		
		repaint();
	}
	
	public int[][] getActualGrid()
	{
		int[][] gridActual = new int[sudokuSize][sudokuSize];
		
		for (int i = 0; i < sudokuSize; i++) {
			for (int j = 0; j < sudokuSize; j++) {
				gridActual[i][j] = cells[i][j].current;
			}
		}
		
		return gridActual;
	}
}