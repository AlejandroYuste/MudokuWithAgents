import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.Timer;

public class AgentGameController extends GameController implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	public enum NetworkState {idle, waitingInit, waitingConfirm}
	NetworkState networkState;

	Label Title;
	Label ipFieldLabel;
	Label portFieldLabel;
	Label ipFieldDoubtLabel;
	Label informationL1Label;
	Label informationL2Label;
	Label informationConnectionLabel;
	
	Label NumAgentsLabel;
	Label TypeAgentsLabel;
	Label AgentsConnected;
	Label AddNewAgents;
	Label RandomCommunity;
	Label RandomCommunityMember;
	
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
	
	TextField NumAgentsField;
	TextField NumRadomAgentsField;
	TextField ipField;
	TextField portField;
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
		agentColors = new Color[8];
		conflictExists = false;
		
		agentColors[0] = new Color(28, 134, 238);		//Contributed By Rows 		--> dodgerblue 2
		agentColors[1] = new Color(0, 205, 0);			//Contributed By Columns 	--> green 3
		agentColors[2] = new Color(255, 215, 0);		//Contributed By Squares 	--> Gold
		agentColors[3] = new Color(255, 0, 0);			//Contributed by User 		--> red
		agentColors[4] = new Color(142, 56, 142);		//Cell committed			--> sgi beet
		agentColors[5] = new Color(139, 69, 19);		//Cell Accepted				--> Chocolate
		agentColors[6] = new Color(0, 0, 0);			//Cell from the Server		--> black
		agentColors[7] = new Color(220, 20, 60);		//Color de la casella de conflicte --> crimson
	}

	public void init()										//Aqui comença l'execucio del Applet
	{
		super.init();										//Cirdem al init de GameController
		
		// Components from Here:	
		ipField = new TextField(20);
		ipField.setSize(100,20);
		ipField.setLocation(200, 400);
		ipField.setText("127.0.0.1");			//IP del client o del servidor?
		add(ipField);

		portField = new TextField(4);
		portField.setSize(100,20);
		portField.setLocation(200, 420);
		portField.setText("4433");				//Port
		add(portField);
		
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
		connectClientButton = new Button("Connect to the Server");
		connectClientButton.setSize(150,75);
		connectClientButton.setLocation(300, 200);
		connectClientButton.setActionCommand("connect");
		connectClientButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectClientButton);
		
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
		
		disconnectButton = new Button("Disconnect Agent");
		disconnectButton.setSize(120,20);
		disconnectButton.setLocation(gridEndX + 135, 210);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		disconnectButton.setVisible(false);
		add(disconnectButton);
		
		pauseExecution = new Button("Pause Agents");
		pauseExecution.setSize(120,40);
		pauseExecution.setLocation(gridEndX + 185, 330);
		pauseExecution.setActionCommand("pauseExecution");
		pauseExecution.addActionListener(this);				//Afegim el Listener al button "Connect"
		pauseExecution.setVisible(false);
		add(pauseExecution);
		
		playExecution = new Button("Resume Agents");
		playExecution.setSize(120,40);
		playExecution.setLocation(gridEndX + 185, 280);
		playExecution.setActionCommand("playExecution");
		playExecution.addActionListener(this);				//Afegim el Listener al button "Connect"
		playExecution.setVisible(false);
		add(playExecution);
		
		//Labels from Here
		Title = new Label("Welcome to Mudoku-Agents Version");
		Font font = new Font("SansSerif", Font.BOLD, 15);
		Title.setFont(font);
		Title.setAlignment(Label.CENTER);
		Title.setSize(400,50);
		Title.setLocation(175, 50);
		add(Title);
		
		informationL1Label = new Label();
		informationL1Label.setLocation(100, 120);
		informationL1Label.setSize(600, 20);
		informationL1Label.setText("This tool have been created with an eductaional purpose. In this framework have been implemented a");
		add(informationL1Label);
		
		informationL2Label = new Label();
		informationL2Label.setLocation(100, 140);
		informationL2Label.setSize(600, 20);
		informationL2Label.setText("  a simulation of an Open Source Community with Artificial Agents that colaborate to solver a Sudoku.");
		add(informationL2Label);
		
		informationConnectionLabel = new Label("(To run this aplication a server must be running)");
		informationConnectionLabel.setLocation(240, 300);
		informationConnectionLabel.setSize(600, 20);
		add(informationConnectionLabel);
		
		
		ipFieldLabel = new Label("Select the IP of the Server:");
		ipFieldLabel.setSize(180,20);
		ipFieldLabel.setLocation(20, 400);
		add(ipFieldLabel);
		
		ipFieldDoubtLabel = new Label("(If you have any doubt use the default values)");
		ipFieldDoubtLabel.setSize(250,20);
		ipFieldDoubtLabel.setLocation(320, 400);
		add(ipFieldDoubtLabel);
		
		portFieldLabel = new Label("Select the Port of the Server:");
		portFieldLabel.setSize(180,20);
		portFieldLabel.setLocation(20, 420);
		add(portFieldLabel);
		
		//------------------------------------------------------------------------------------------
		
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
		
		//------------------------------------------------------------------------------------------
		
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
		
		//------------------------------------------------------------------------------------------
		
		valuesContributedLabel = new Label("Values");
		valuesContributedLabel.setSize(50, 10);
		valuesContributedLabel.setLocation(505, 270);
		valuesContributedLabel.setVisible(false);
		add(valuesContributedLabel);	
		
		contributedLabel = new Label("Contributed");
		contributedLabel.setSize(70, 10);
		contributedLabel.setLocation(492, 285);
		contributedLabel.setVisible(false);
		add(contributedLabel);	
		
		countContributedLabel = new Label(0 + "/" + sudokuSize*sudokuSize);
		countContributedLabel.setSize(50, 10);
		countContributedLabel.setLocation(505, 302);
		countContributedLabel.setVisible(false);
		add(countContributedLabel);	
		
		//------------------------------------------------------------------------------------------
		
		valuesCommittedLabel = new Label("Values");
		valuesCommittedLabel.setSize(50, 10);
		valuesCommittedLabel.setLocation(505, 340);
		valuesCommittedLabel.setVisible(false);
		add(valuesCommittedLabel);	
		
		committedLabel = new Label("Committed");
		committedLabel.setSize(70, 10);
		committedLabel.setLocation(493, 355);
		committedLabel.setVisible(false);
		add(committedLabel);	
		
		countCommittedLabel = new Label(0 + "/" + sudokuSize*sudokuSize);
		countCommittedLabel.setSize(50, 10);
		countCommittedLabel.setLocation(505, 370);
		countCommittedLabel.setVisible(false);
		add(countCommittedLabel);	
		
		//------------------------------------------------------------------------------------------
		
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
		
		//------------------------------------------------------------------------------------------
		
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

		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		gr.drawLine(20, 460, 470, 460);				//Separador Grid Horizintal
		gr.drawLine(470, 10, 470, 460);				//Separador Grid Vertical-Dreta
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);		//Separador de la Part d'afegir Agents
		gr.drawLine(10, 495, 550, 495);			
		gr.drawLine(10, 550, 550, 550);			
		gr.drawLine(10, 590, 550, 590);			
		gr.drawLine(10, 495, 10, 590);
		gr.drawLine(550, 495, 550, 590);
		
		gr.drawLine(485, 10, 485, 240);				//Separador del Panel d'Agents Connectats	
		gr.drawLine(485, 10, 745, 10);			
		gr.drawLine(745, 10, 745, 240);			
		gr.drawLine(485, 240, 745, 240);
		
		gr.drawLine(585, 260, 585, 390);			//Separador del Pause-Play	
		gr.drawLine(585, 260, 745, 260);			
		gr.drawLine(745, 260, 745, 390);			
		gr.drawLine(585, 390, 745, 390);
				
		gr.drawLine(565, 410, 565, 590);			//Pintem la llegenda de Colors
		gr.drawLine(565, 410, 745, 410);			
		gr.drawLine(745, 410, 745, 590);			
		gr.drawLine(565, 590, 745, 590);
		
		gr.drawLine(485, 260, 485, 320);			//Caixa valors contributed
		gr.drawLine(485, 260, 565, 260);			
		gr.drawLine(565, 260, 565, 320);			
		gr.drawLine(485, 320, 565, 320);
		
		gr.drawLine(485, 330, 485, 390);			//Caixa valors committed
		gr.drawLine(485, 330, 565, 330);			
		gr.drawLine(565, 330, 565, 390);			
		gr.drawLine(485, 390, 565, 390);
		
		gr.drawLine(485, 410, 485, 475);			//Caixa de la Puntuacio
		gr.drawLine(485, 410, 550, 410);			
		gr.drawLine(550, 410, 550, 475);			
		gr.drawLine(485, 475, 550, 475);
		
		gr.setColor(agentColors[6]);
		gr.fillRect(582, 425, 10, 10);
		
		gr.setColor(agentColors[0]);
		gr.fillRect(582, 445, 10, 10);
		
		gr.setColor(agentColors[1]);
		gr.fillRect(582, 465, 10, 10);
		
		gr.setColor(agentColors[2]);
		gr.fillRect(582, 485, 10, 10);
		
		gr.setColor(agentColors[3]);
		gr.fillRect(582, 505, 10, 10);
		
		gr.setColor(agentColors[4]);
		gr.fillRect(582, 525, 10, 10);
		
		gr.setColor(agentColors[5]);
		gr.fillRect(582, 545, 10, 10);
		
		gr.setColor(agentColors[7]);
		gr.fillRect(582, 565, 10, 10);
		
		int val = getCountCorrect();
		if (val>0 && val<100)
			countLabel.setLocation(497, 455);
		else if (val>=100)
			countLabel.setLocation(495, 455);
		countLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
		val = getCountContributed();
		if (val < 10)
			countContributedLabel.setLocation(508, 302);
		else if (val>0 && val<100)
			countContributedLabel.setLocation(505, 302);
		else if (val>=100)
			countContributedLabel.setLocation(502, 302);		
		countContributedLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
		val = getCountCommitted();
		if (val < 10)
			countCommittedLabel.setLocation(508, 370);
		else if (val>9 && val<100)
			countCommittedLabel.setLocation(505, 370);
		else if (val>=100)
			countCommittedLabel.setLocation(502, 370);
		countCommittedLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
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
							gr.setColor(agentColors[6]);			//Cell from the Server
							clearRequester = 6;
							break;
						case 2:
							gr.setColor(agentColors[0]);			//Contributed By Rows 
							clearRequester = 0;
							break;
						case 3:
							gr.setColor(agentColors[1]);			//Contributed By Columns
							clearRequester = 1;
							break;
						case 4:
							gr.setColor(agentColors[2]);			//Contributed By Squares
							clearRequester = 2;
							break;
						case 5:
							gr.setColor(agentColors[3]);			//Contributed By User
							clearRequester = 3;
							break;
						case 6:
							gr.setColor(agentColors[4]);			//Cell committed
							clearRequester = 4;
							break;
						case 7:
							gr.setColor(agentColors[5]);			//Cell Accepted
							clearRequester = 5;
							break;
					}
					
					if(conflictExists && x == conflictX && y == conflictY)
					{
						int conX = (int) (gridXOffset + conflictX * deltaX);
						int conY = (int) (gridYOffset + conflictY * deltaY);
						
						setActive(conflictX, conflictY);
						cells[activeX][activeY].DrawAgentDomainConflict(gr, conX, conY, conflictX, conflictY, agentColors);
					}
				}
					
				//Pintem els numeros al Grid
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
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);

		switch(networkState)
		{
		case idle:
			cells[activeX][activeY].DrawDomainAgent(gr, activeX, activeY);		
			break;
		case waitingConfirm:
			gr.drawString("Waiting response from server", gridXOffset, gridEndY + 20);
			break;
		default:
			break;
		}

		if (mouseOverGrid) 
		{
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
		
		super.actionPerformed(action);
		
		switch(action.getActionCommand())
		{
			case "connect":
				if(!ipField.equals("") && !portField.equals(""))
				{
					try 
					{	
						networkController.Connect(ipField.getText(), Integer.parseInt(portField.getText()));
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
				String[] vars = AgentName.split("\\[");
				String[] vars1 = vars[1].split("\\]");
				int agentId = Integer.parseInt(vars1[0]);
								
				networkController.stopExecuting(agentId);
				listPanel.remove(index);
				break;
				
			case "voteEnd":
				conflictExists = false;
				voteTimer.stop();
				break;
			case "pauseExecution":
				networkController.pauseExecution();
				break;
			case "playExecution":
				networkController.playExecution();
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
	public void Initialize()
	{
		super.Initialize();
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
						conflictExists = true;
						
						repaint();
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
				conflictExists = false;
				voteTimer.stop();
				
				vars2 = vars[1].split(",");
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				ClearCell(conflictX, conflictY);
				
				repaint();
				break;
			case "committed":
				conflictExists = false;
				voteTimer.stop();
				
				vars2 = vars[1].split(",");
				
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				
				SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, 6);
				repaint();
				break;	
			case "accepted":		//networkController.BroadcastMessage("accepted#" + x + "," + y + "," + cells[x][y].current + "," + 7);
		
				vars2 = vars[1].split(",");
				int x = Integer.parseInt(vars2[0]);
				int y = Integer.parseInt(vars2[1]);
				SetValueAndState(x, y, cells[x][y].current, 7);				
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
		ipField.setVisible(false);
		portField.setVisible(false);
		informationL1Label.setVisible(false);
		informationL2Label.setVisible(false);
		informationConnectionLabel.setVisible(false);
		ipFieldLabel.setVisible(false);
		ipFieldDoubtLabel.setVisible(false);
		portFieldLabel.setVisible(false);
		
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
		valuesContributedLabel.setVisible(true);
		valuesCommittedLabel.setVisible(true);
		contributedLabel.setVisible(true);
		committedLabel.setVisible(true);
		countContributedLabel.setVisible(true);
		countCommittedLabel.setVisible(true);

		state = GameState.game;
	}
	
	@Override
	public void CellClick(int x, int y) 
	{
		activeX = x;
		activeY = y;
	
		repaint();
	}
	
	public void setActive(int x, int y) 
	{
		activeX = x;
		activeY = y;
	}
	
	int getRegion(int x, int y) {
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
	
	int getCellState(int x, int y)
	{
		return super.getCellState(x, y);
	}
	
	int getCountContributed()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++) {
			for(int j=0;j<sudokuSize;j++) {
				if(cells[i][j].valueState == 2 || cells[i][j].valueState == 3 || cells[i][j].valueState == 4 || cells[i][j].valueState == 5)
					count++;
			}	
		}
		
		return count;
	}
	
	int getCountCommitted()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++) {
			for(int j=0;j<sudokuSize;j++) {
				if(cells[i][j].valueState == 6)
					count++;
			}	
		}
		
		return count;
	}
	
	int getCountCorrect()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++) {
			for(int j=0;j<sudokuSize;j++) {
				if(cells[i][j].valueState == 1 || cells[i][j].valueState == 7)
					count++;
			}	
		}
		
		return count;
	}
}