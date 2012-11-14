import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.Timer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomain;

public class AgentGameController extends GameController implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	public enum NetworkState {idle, waitingInit, waitingConfirm}
	NetworkState networkState;

	Label Title;
	Label NumAgentsLabel;
	Label TypeAgentsLabel;
	Label AgentsConnected;
	Label AddNewAgents;
	
	TextField NumAgentsField;
	Choice TypeAgentsField;
	List listPanel;

	Button connectClientButton;
	Button connectAgentsButton;
	Button clearButton;
	Button disconnectButton;
	
	int[][] instantiator;
	static AgentNetworkController networkController;
	
	String ipField = "127.0.0.1";
	String portField = "4433";
	
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
		listPanel = new List(10);
		listPanel.setLocation(gridEndX + 75, 40);
		listPanel.setSize(240, 160);
		listPanel.setVisible(false);
		add(listPanel);
		
		NumAgentsField = new TextField(20);
		NumAgentsField.setSize(160, 20);
		NumAgentsField.setLocation(gridXOffset, 520);
		NumAgentsField.setText("1");
		add(NumAgentsField);
		NumAgentsField.setVisible(false);
		
		TypeAgentsField = new Choice();
		TypeAgentsField.setSize(150, 500);
		TypeAgentsField.add("Row Contributor");
		TypeAgentsField.add("Column Contributor");
		TypeAgentsField.add("Square Contributor");
		TypeAgentsField.add("Row Tester");
		TypeAgentsField.add("Column Tester");
		TypeAgentsField.add("Square Tester");
		TypeAgentsField.add("Row Committer");
		TypeAgentsField.add("Column Committer");
		TypeAgentsField.add("Square Committer");
		TypeAgentsField.setVisible(false);	
		TypeAgentsField.setLocation(gridXOffset + 160 + 20, 520);
		add(TypeAgentsField);
		
		//Buttons from here!
		connectAgentsButton = new Button("Connect Agents");
		connectAgentsButton.setSize(160, 40);
		connectAgentsButton.setLocation(gridXOffset + 180 + 100 + 80, 500);
		connectAgentsButton.setActionCommand("connectAgents");
		connectAgentsButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectAgentsButton);
		connectAgentsButton.setVisible(false);
		
		connectClientButton = new Button("Connect to the Server");
		connectClientButton.setSize(200,150);
		connectClientButton.setLocation(300, 100);
		connectClientButton.setActionCommand("connect");
		connectClientButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectClientButton);
		
		disconnectButton = new Button("Disconnect Agent");
		disconnectButton.setSize(120,20);
		disconnectButton.setLocation(gridEndX + 140, 210);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		disconnectButton.setVisible(false);
		add(disconnectButton);
		
		//Labels from Here
		Title = new Label("Welcome to Mudoku-Agents Version");
		Font font = new Font("SansSerif", Font.BOLD, 15);
		Title.setFont(font);
		Title.setAlignment(Label.CENTER);
		Title.setSize(400,150);
		Title.setLocation(200, 0);
		add(Title);
		
		AddNewAgents = new Label("Add New Agents to the Community:");
		AddNewAgents.setSize(250,20);
		AddNewAgents.setLocation(gridXOffset, 475);
		add(AddNewAgents);
		AddNewAgents.setVisible(false);
		
		AgentsConnected = new Label("Agents Connected:");
		AgentsConnected.setSize(250,20);
		AgentsConnected.setLocation(gridEndX + 140, 20);
		add(AgentsConnected);
		AgentsConnected.setVisible(false);
		
		NumAgentsLabel = new Label("Select the Number of Agents:");
		NumAgentsLabel.setSize(180,20);
		NumAgentsLabel.setLocation(gridXOffset, 500);
		//NumAgentsLabel.setForeground(Color.green);			//Podem Canviar el Colors dels Menus
		add(NumAgentsLabel);
		NumAgentsLabel.setVisible(false);
		
		TypeAgentsLabel = new Label("Select the Type of Agents:");
		TypeAgentsLabel.setSize(180,20);
		TypeAgentsLabel.setLocation(gridXOffset + 160 + 20, 500);
		add(TypeAgentsLabel);
		TypeAgentsLabel.setVisible(false);
	
		networkController = new AgentNetworkController(this);
		HideVote();					//Ocultem els botons innecessaris
	}

	@Override
	public void paint ( Graphics gr )
	{
		initDraw(gr);				//S'ha d'inicialitzar cada vegada?
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
	public void DrawGrid(Graphics gr) {
		
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

		//TODO: Milorar interficie grafica separant les seccions del Applet
		//gr.drawLine(0, 10, 0, 460);			//Separador Grid Vertical-Esquerra
		gr.drawLine(20, 460, 470, 460);		//Separador Grid Horizintal
		//gr.drawLine(0, 470, 470, 470);	//Segon Separador Grid Horizintal
		gr.drawLine(470, 10, 470, 460);		//Separador Grid Vertical-Dreta
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		lineX = gridXOffset;
		lineY = gridYOffset;
		tempLineY = lineY;
		tempLineX = lineX;
		
		// Draw Values
		for (int y = 0; y < sudokuSize; y++) 
		{
			for (int x = 0; x < sudokuSize; x++) {
				if (cells[x][y].current == -1) {
					gr.setColor(Color.black);
					/*if(cpController.GetCPVariable(x, y).getDomainSize() == 1)	//Quan nomes podem tenir un unic valor a una casella
					{
						gr.drawString("*", (int) (lineX + deltaX / 2) - 5,
								(int) (lineY + deltaY) - 10);					//sa puta merda de asterisc esta aqui
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
					else if(conflictExists && x == conflictX && y == conflictY)
					{
						gr.setColor(Color.red);
						
						int conX = (int) (gridXOffset + conflictX * deltaX);
						int conY = (int) (gridYOffset + conflictY * deltaY);
						gr.fillRect(conX, conY, (int) deltaX, (int) deltaY);
						gr.setColor(agentColors[clearRequester]);
						//gr.drawString("Clear Requester", gridEndX + 20, gridYOffset + 100);
					}
					else
					{
						gr.setColor(agentColors[instantiator[x][y]]);
					}
				}
					
				//Pintem els numeros al Grid
				gr.drawString( String.valueOf(cells[x][y].current), (int) (lineX + deltaX / 2) - 5, (int) (lineY + deltaY) - 10);
				tempLineX += deltaX;
				lineX = (int) tempLineX;
			}
			
			tempLineY += deltaY;
			lineY = (int) tempLineY;
			lineX = gridXOffset;
			tempLineX = lineX;
		}
		
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);

		switch(networkState)
		{
		case idle:
			cells[activeX][activeY].DrawDomainAgent(gr, mouseOverDomainIndex, activeX, activeY);		
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
					try 
					{	
						networkController.Connect(ipField, Integer.parseInt(portField));
						RequestInit();					//Per afegir el Grid al Applet dels Agents
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
			case "connectAgents":
				networkController.Connect(Integer.parseInt(NumAgentsField.getText()), TypeAgentsField.getSelectedIndex());
				break;
			case "disconnect":
				int index = listPanel.getSelectedIndex();
				String AgentName = listPanel.getSelectedItem();
				
				
				String[] vars = AgentName.split(" ");
				//System.out.println("AgentGameController --> Volem para el Client: " + AgentName + " amb id: " + vars[1]);
				
				int agentId = Integer.parseInt(vars[1]);
				networkController.stopExecuting(agentId);
				listPanel.remove(index);
				break;
				
			case "voteEnd":
				conflictExists = false;
				HideVote();
				voteTimer.stop();
				break;
			/*case "yes":
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
				break;*/
		}
	}
	
	public boolean getConflictExists()
	{
		return conflictExists;
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
		AgentNetworkController.SendMessage("request#type=init");			//Aqui no es important el clientId ja que nomes hi ha un framework
		networkState = NetworkState.waitingInit;
	}
	
	@Override
	public void DomainClick(int index)
	{
		IntDomain idom = cpController.GetCPVariable(activeX, activeY).getDomain();
		DisposableIntIterator iter = idom.getIterator();
		int val;
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
		//System.out.println("AgentGameController --> Message Received: " + message);
		
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
					
					networkController.setPositionConflic(conflictX, conflictY);
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
			System.out.println("Es prudueix un error a la propagacio: " + e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void StartGame()
	{
		Title.setVisible(false);
		connectClientButton.setVisible(false);
		connectAgentsButton.setVisible(true);
		AddNewAgents.setVisible(true);
		AgentsConnected.setVisible(true);
		disconnectButton.setVisible(true);
		listPanel.setVisible(true);
		NumAgentsLabel.setVisible(true);
		NumAgentsField.setVisible(true);
		TypeAgentsLabel.setVisible(true);
		TypeAgentsField.setVisible(true);

		state = GameState.game;
	}
	
	@Override
	public void CellClick(int x, int y) {
		activeX = x;
		activeY = y;
		
		/*if(instantiator[x][y] > -1)
		{
			//clearButton.setVisible(true);
		}
		else
		{
			clearButton.setVisible(false);
		}*/
		
		repaint();
	}
	
	int getRegion(int x, int y)
	{
		return super.getRegion(x, y);
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