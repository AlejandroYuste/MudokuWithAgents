import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.Timer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
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
	Label RandomCommunity;
	Label RandomCommunityMember;
	
	TextField NumAgentsField;
	TextField NumRadomAgentsField;
	Choice TypeAgentsField;
	List listPanel;

	Button connectClientButton;
	Button connectAgentsButton;
	Button connectRandomAgentsButton;
	Button clearButton;
	Button disconnectButton;
	Button pauseExecution;
	Button playExecution;
	
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

	int val;
	
	public AgentGameController()
	{
		super();
		clearRequester = -1;
		GameController.sudokuSize = 16;
		networkState = NetworkState.idle;
		agentColors = new Color[7];
		conflictExists = false;
		
		agentColors[0] = new Color(39, 64, 139);		//Contributed By Rows 		--> royalblue 3
		agentColors[1] = new Color(0, 139, 63);			//Contributed By Columns 	--> springgreen 3
		agentColors[2] = new Color(238, 201, 0);		//Contributed By Squares 	--> Gold 2
		agentColors[3] = new Color(255, 0, 0);			//Contributed by User 		--> red
		agentColors[4] = new Color(142, 142, 46);		//Cell committed			--> sgi olivedrab
		agentColors[5] = new Color(139, 69, 19);		//Cell Accepted				--> Chocolate
		agentColors[6] = new Color(0, 0, 0);			//Cell from the Server		--> black
	}

	public void init()										//Aqui comen�a l'execucio del Applet
	{
		super.init();										//Cirdem al init de GameController
		
		// Components from Here:
		listPanel = new List(10);
		listPanel.setLocation(gridEndX + 75, 40);
		listPanel.setSize(240, 160);
		listPanel.setVisible(false);
		add(listPanel);
		
		NumRadomAgentsField = new TextField(20);
		NumRadomAgentsField.setSize(40, 20);
		NumRadomAgentsField.setLocation(gridXOffset + 200, 560);
		NumRadomAgentsField.setText("10");
		add(NumRadomAgentsField);
		NumRadomAgentsField.setVisible(false);
		
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
		TypeAgentsField.add("------------------");
		TypeAgentsField.add("Row Tester");
		TypeAgentsField.add("Column Tester");
		TypeAgentsField.add("Square Tester");
		TypeAgentsField.add("------------------");
		TypeAgentsField.add("Row Committer");
		TypeAgentsField.add("Column Committer");
		TypeAgentsField.add("Square Committer");
		TypeAgentsField.add("------------------");
		TypeAgentsField.add("Project Leader");
		TypeAgentsField.setVisible(false);	
		TypeAgentsField.setLocation(gridXOffset + 160 + 20, 520);
		add(TypeAgentsField);
		
		//Buttons from here!
		connectRandomAgentsButton = new Button("Connect Random Agents");
		connectRandomAgentsButton.setSize(160, 20);
		connectRandomAgentsButton.setLocation(gridXOffset + 180 + 100 + 80, 560);
		connectRandomAgentsButton.setActionCommand("connectRandomAgents");
		connectRandomAgentsButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectRandomAgentsButton);
		connectRandomAgentsButton.setVisible(false);
		
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
		disconnectButton.setLocation(gridEndX + 135, 210);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		disconnectButton.setVisible(false);
		add(disconnectButton);
		
		pauseExecution = new Button("Pause");
		pauseExecution.setSize(120,40);
		pauseExecution.setLocation(gridEndX + 135, 350);
		pauseExecution.setActionCommand("pauseExecution");
		pauseExecution.addActionListener(this);				//Afegim el Listener al button "Connect"
		pauseExecution.setVisible(false);
		add(pauseExecution);
		
		playExecution = new Button("Play");
		playExecution.setSize(120,40);
		playExecution.setLocation(gridEndX + 135, 300);
		playExecution.setActionCommand("playExecution");
		playExecution.addActionListener(this);				//Afegim el Listener al button "Connect"
		playExecution.setVisible(false);
		add(playExecution);
		
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
		AddNewAgents.setLocation(gridXOffset + 150, 470);
		add(AddNewAgents);
		AddNewAgents.setVisible(false);
		
		AgentsConnected = new Label("Agents Connected:");
		AgentsConnected.setSize(150,20);
		AgentsConnected.setLocation(gridEndX + 140, 20);
		add(AgentsConnected);
		AgentsConnected.setVisible(false);
		
		NumAgentsLabel = new Label("Select the Number of Agents:");
		NumAgentsLabel.setSize(180,20);
		NumAgentsLabel.setLocation(gridXOffset, 500);
		//NumAgentsLabel.setForeground(Color.green);			//Podem Canviar el Colors de les etiquetes
		add(NumAgentsLabel);
		NumAgentsLabel.setVisible(false);
		
		TypeAgentsLabel = new Label("Select the Type of Agents:");
		TypeAgentsLabel.setSize(180,20);
		TypeAgentsLabel.setLocation(gridXOffset + 160 + 20, 500);
		add(TypeAgentsLabel);
		TypeAgentsLabel.setVisible(false);
		
		RandomCommunity = new Label("Create a Random Community with");
		RandomCommunity.setSize(200,20);
		RandomCommunity.setLocation(gridXOffset, 560);
		add(RandomCommunity);
		RandomCommunity.setVisible(false);
		
		RandomCommunityMember = new Label("members.");
		RandomCommunityMember.setSize(100,20);
		RandomCommunityMember.setLocation(gridXOffset + 245, 560);
		add(RandomCommunityMember);
		RandomCommunityMember.setVisible(false);
	
		networkController = new AgentNetworkController(this);
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
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		gr.drawLine(20, 460, 470, 460);			//Separador Grid Horizintal
		gr.drawLine(470, 10, 470, 460);			//Separador Grid Vertical-Dreta
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);	//Separador de la Part d'afegir Agents
		gr.drawLine(10, 495, 550, 495);			
		gr.drawLine(10, 550, 550, 550);			
		gr.drawLine(10, 590, 550, 590);			
		gr.drawLine(10, 495, 10, 590);
		gr.drawLine(550, 495, 550, 590);
		
		gr.drawLine(485, 10, 485, 240);			//Separador del Panel d'Agents Connectats	
		gr.drawLine(485, 10, 745, 10);			
		gr.drawLine(745, 10, 745, 240);			
		gr.drawLine(485, 240, 745, 240);
		
		gr.drawLine(485, 280, 485, 420);			//Separador del Pause-Play	
		gr.drawLine(485, 280, 745, 280);			
		gr.drawLine(745, 280, 745, 420);			
		gr.drawLine(485, 420, 745, 420);
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		lineX = gridXOffset;
		lineY = gridYOffset;
		tempLineY = lineY;
		tempLineX = lineX;
		
		// Draw Values
		for (int y = 0; y < sudokuSize; y++) 
		{
			for (int x = 0; x < sudokuSize; x++) 
			{
				if (cells[x][y].valueState == 0) 			//Si no hi ha cap valor a la cel�la no feim res, pasem la posicio
				{
					gr.setColor(Color.black);
					/*if(cpController.GetCPVariable(x, y).getDomainSize() == 1)			//Quan nomes podem tenir un unic valor a una casella
					{
						gr.drawString("*", (int) (lineX + deltaX / 2) - 5,
								(int) (lineY + deltaY) - 10);							//sa puta merda de asterisc esta aqui
					}*/
					
					tempLineX += deltaX;
					lineX = (int) tempLineX;
					continue;
				}
				else
				{
					switch(cells[x][y].valueState)			
					{
						case 1:
							gr.setColor(agentColors[6]);			//Cell from the Server
							break;
						case 2:
							gr.setColor(agentColors[0]);			//Contributed By Rows 
							break;
						case 3:
							gr.setColor(agentColors[1]);			//Contributed By Columns
							break;
						case 4:
							gr.setColor(agentColors[2]);			//Contributed By Squares
							break;
						case 5:
							gr.setColor(agentColors[3]);			//Contributed By User
							break;
						case 6:
							gr.setColor(agentColors[4]);			//Cell committed
							break;
						case 7:
							gr.setColor(agentColors[5]);			//Cell Accepted
							break;
					}
					
					if(conflictExists && x == conflictX && y == conflictY)
					{
						gr.setColor(Color.red);
						
						int conX = (int) (gridXOffset + conflictX * deltaX);
						int conY = (int) (gridYOffset + conflictY * deltaY);
						gr.fillRect(conX, conY, (int) deltaX, (int) deltaY);
						gr.setColor(agentColors[clearRequester]);
					}
				}
					
				//Pintem els numeros al Grid
				gr.drawString(String.valueOf(cells[x][y].current), (int) (lineX + deltaX / 2) - 5, (int) (lineY + deltaY) - 10);
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
			case "connectRandomAgents":
				networkController.createRandomCommunity(Integer.parseInt(NumRadomAgentsField.getText()));
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
				voteTimer.stop();
				break;
		}
	}
	
	public boolean getConflictExists()
	{
		return conflictExists;
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
		for(;index >= 0 && iter.hasNext(); index--)
		{
			val = iter.next();
		}
		
		repaint();
	}
	
	@Override
	public void Initialize()
	{
		super.Initialize();
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
						//conflictExists = true;
						
						coordinates = vars2[1].split(",");
						conflictX = Integer.parseInt(coordinates[0]);
						conflictY = Integer.parseInt(coordinates[1]);
						clearRequester = Integer.parseInt(coordinates[2]);
						
						networkController.setPositionConflic(conflictX, conflictY);
						
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
			case "NOclear":
				conflictExists = false;
				voteTimer.stop();
				
				vars2 = vars[1].split(",");
				
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				
				SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, 6);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addToGrid(String code)				//Afegim un nou valor al Grid
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
		RandomCommunity.setVisible(true);
		NumRadomAgentsField.setVisible(true);
		RandomCommunityMember.setVisible(true);
		connectRandomAgentsButton.setVisible(true);
		playExecution.setVisible(true);
		pauseExecution.setVisible(true);

		state = GameState.game;
	}
	
	@Override
	public void CellClick(int x, int y) {
		activeX = x;
		activeY = y;
	
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
	
	public int[][] getActualState()
	{
		int[][] gridState = new int[sudokuSize][sudokuSize];
		
		for (int i = 0; i < sudokuSize; i++) {
			for (int j = 0; j < sudokuSize; j++) {
				gridState[i][j] = cells[i][j].valueState;
			}
		}
		
		return gridState;
	}
}