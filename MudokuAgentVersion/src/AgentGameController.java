import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.Timer;

public class AgentGameController extends GameController implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	public enum NetworkState {idle, waitingInit, waitingConfirm}
	NetworkState networkState;

	//Labels of the welcome Interface
	
	Label Title;
	Label ipFieldLabel;
	Label portFieldLabel;
	Label ipFieldDoubtLabel;
	Label informationL1Label;
	Label informationL2Label;
	Label informationConnectionLabel;
	
	//Labels to add new agents to the community
	
	Label NumAgentsLabel;
	Label TypeAgentsLabel;
	Label AgentsConnected;
	Label AddNewAgents;
	Label RandomCommunity;
	Label RandomCommunityMember;
	
	//Labels of the Color Legend
	
	Label colorServerLabel;
	Label colorRowAgentLabel;
	Label colorColumnAgentLabel;
	Label colorSquareAgentLabel;
	Label colorLeaderLabel;
	Label colorContributedLabel;
	Label colorCommittedLabel;
	Label colorAcceptedLabel;
	Label colorUserContributorLabel;
	
	//Labels of the information boxes
	
	Label valuesContributedLabel;
	Label valuesCommittedLabel;
	Label contributedLabel;
	Label committedLabel;
	Label countContributedLabel;
	Label countCommittedLabel;
	Label correctLabel;
	Label valuesLabel;
	Label countLabel;
	Label valuesReportedLabel;
	Label reportedLabel;
	Label countReportedLabel;
	Label votingLabel;
	Label votingLabelON;
	Label cellStateInfoLabel;	
	Label cellValueLabel;
	Label cellStateLabel;
	
	//Components of the interface of the Applet
	
	TextField NumAgentsField;
	TextField NumRadomAgentsField;
	TextField ipField;
	TextField portField;
	Choice TypeAgentsField;
	List listPanel;

	//Buttons of the interface of the Applet
	
	Button connectClientButton;
	Button connectAgentsButton;
	Button connectRandomAgentsButton;
	Button clearButton;
	Button disconnectButton;
	Button pauseExecution;
	Button playExecution;
	
	//Other Variables
	
	static AgentNetworkController networkController;
	
	int voteDelay = 10000;				//Time for the voting (Timer System)
	Timer voteTimer;
	
	int countContributed = 0;
	int countCommitted = 0;
	int countReported = 0;
	
	boolean conflictExists;				//Indicates if there's a Voting Open
	int conflictX, conflictY;
	
	//Constants for the colors
	
	static Color[] agentColors;
	
	final int serverColor = 0;
	final int rowAgentColor = 1;
	final int columnAgentColor = 2;
	final int squareAgentColor = 3;
	final int userColor = 4;
	final int agentLeaderColor = 5;
	final int userLeaderColor = 6;
	final int valueContributedColor = 7;
	final int valueReportedColor = 8;
	final int valueCommittedColor = 9;
	final int valueNotCommittedColor = 10;
	final int valueAcceptedColor = 11;
	final int valueRejectedColor = 12;
	final static int votingColor = 13;
	
	public AgentGameController()
	{
		super();
		GameController.sudokuSize = 16;
		networkState = NetworkState.idle;
		agentColors = new Color[14];
		conflictExists = false;
		
		agentColors[serverColor] = new Color(255, 193, 37);						// 0.  Server 				--> goldenrod 1
		agentColors[rowAgentColor] = new Color(188,	210, 238);					// 1.  Row Agent 			--> lightsteelblue 2
		agentColors[columnAgentColor] = new Color(173, 255, 47);				// 2.  Columns Agent 		--> greenyellow
		agentColors[squareAgentColor] = new Color(255, 236,	139);				// 3.  Square Agent			--> lightgoldenrod 1
		agentColors[userColor] = new Color(238, 180, 180);						// 4.  User 				--> rosybrown 2
		agentColors[agentLeaderColor] = new Color(205, 201,	201);				// 5.  Leader Agent			--> snow 3
		agentColors[userLeaderColor] = new Color(0, 0, 0);						// 6.  User Leader			--> black
		agentColors[valueContributedColor] = new Color(56, 142, 142);			// 7.  Value Contributed	--> sgi teal
		agentColors[valueReportedColor] = new Color(255, 255, 255);				// 8.  Value Reported		--> white
		agentColors[valueCommittedColor] = new Color(220, 20, 60);				// 9.  Value Committed		--> crimson
		agentColors[valueNotCommittedColor] = new Color(255, 255, 255);			// 10. Value NOT Committed	--> white
		agentColors[valueAcceptedColor] = new Color(255, 193, 37);				// 11. Value Accepted 		--> goldenrod 1
		agentColors[valueRejectedColor] = new Color(255, 255, 255);				// 12. Value Rejected		--> white
		agentColors[votingColor] = new Color(220, 20, 60);						// 13. Voting Color			--> crimson
	}

	public void init()										
	{
		super.init();										
		
		//-------------------------------         COMPONENTS FROM HERE         -------------------------------//	
		
		ipField = new TextField(20);
		ipField.setSize(100,20);
		ipField.setLocation(200, 400);
		ipField.setText("127.0.0.1");						//IP of the host where is the server located
		add(ipField);

		portField = new TextField(4);
		portField.setSize(100,20);
		portField.setLocation(200, 420);
		portField.setText("4433");							//Port where the server is waiting for data (Default 4433)
		add(portField);
		
		listPanel = new List(10);
		listPanel.setLocation(gridEndX + 75, 40);
		listPanel.setSize(240, 160);
		listPanel.setVisible(false);
		add(listPanel);
		
		NumRadomAgentsField = new TextField(20);
		NumRadomAgentsField.setSize(40, 20);
		NumRadomAgentsField.setLocation(gridXOffset + 200, 560);
		NumRadomAgentsField.setText("15");
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
		TypeAgentsField.add("Row Bug Reporter");
		TypeAgentsField.add("Column Bug Reporter");
		TypeAgentsField.add("Square Bug Reporter");
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
		
		//-------------------------------         BUTTONS FROM HERE         -------------------------------//
		
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
		
		//-------------------------------         LABELS FROM HERE         -------------------------------//
		
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
		
		//------------------------------------------------------------------------------------------> Add New Agents to the Community
		
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
		
		//------------------------------------------------------------------------------------------> VALUES CONTRIBUTED
		
		valuesContributedLabel = new Label("Values");
		valuesContributedLabel.setSize(50, 10);
		valuesContributedLabel.setLocation(505, 255);
		valuesContributedLabel.setVisible(false);
		add(valuesContributedLabel);	
		
		contributedLabel = new Label("Contributed");
		contributedLabel.setSize(70, 10);
		contributedLabel.setLocation(492, 270);
		contributedLabel.setVisible(false);
		add(contributedLabel);	
		
		countContributedLabel = new Label("0");
		countContributedLabel.setSize(30, 10);
		countContributedLabel.setLocation(505, 285);
		countContributedLabel.setVisible(false);
		add(countContributedLabel);	
		
		//------------------------------------------------------------------------------------------> VALUES COMMITTED
		
		valuesCommittedLabel = new Label("Values");
		valuesCommittedLabel.setSize(50, 10);
		valuesCommittedLabel.setLocation(595, 255);
		valuesCommittedLabel.setVisible(false);
		add(valuesCommittedLabel);	
		
		committedLabel = new Label("Committed");
		committedLabel.setSize(70, 10);
		committedLabel.setLocation(585, 270);
		committedLabel.setVisible(false);
		add(committedLabel);	
		
		countCommittedLabel = new Label("0");
		countCommittedLabel.setSize(30, 10);
		countCommittedLabel.setLocation(585, 285);
		countCommittedLabel.setVisible(false);
		add(countCommittedLabel);	
		
		//------------------------------------------------------------------------------------------> BUGS REPORTED
		
		valuesReportedLabel = new Label("Values");
		valuesReportedLabel.setSize(50, 10);
		valuesReportedLabel.setLocation(685, 255);
		valuesReportedLabel.setVisible(false);
		add(valuesReportedLabel);	
		
		reportedLabel = new Label("Reported");
		reportedLabel.setSize(60, 15);
		reportedLabel.setLocation(680, 268);
		reportedLabel.setVisible(false);
		add(reportedLabel);	
		
		countReportedLabel = new Label("0");
		countReportedLabel.setSize(30, 10);
		countReportedLabel.setLocation(695, 285);
		countReportedLabel.setVisible(false);
		add(countReportedLabel);	
		
		//------------------------------------------------------------------------------------------> CORRECT VALUES
		
		correctLabel = new Label("Correct");
		correctLabel.setSize(50, 10);
		correctLabel.setLocation(495, 400);
		correctLabel.setVisible(false);
		add(correctLabel);
		
		valuesLabel = new Label("Values");
		valuesLabel.setSize(50, 10);
		valuesLabel.setLocation(497, 415);
		valuesLabel.setVisible(false);
		add(valuesLabel);	
		
		countLabel = new Label(0 + "/" + sudokuSize*sudokuSize);
		countLabel.setSize(50, 10);
		countLabel.setLocation(500, 430);
		countLabel.setVisible(false);
		add(countLabel);
		
		//------------------------------------------------------------------------------------------> Voting ON?
		
		votingLabel = new Label("Voting");
		votingLabel.setSize(50, 20);
		votingLabel.setLocation(500, 320);
		votingLabel.setVisible(false);
		add(votingLabel);	
		
		votingLabelON = new Label("ON?");
		votingLabelON.setSize(30, 10);
		votingLabelON.setLocation(505, 340);
		votingLabelON.setVisible(false);
		add(votingLabelON);
		
		//------------------------------------------------------------------------------------------> Cell State
		
		cellStateInfoLabel = new Label("Active Cell:");
		cellStateInfoLabel.setSize(140, 20);
		cellStateInfoLabel.setLocation(570, 315);
		cellStateInfoLabel.setVisible(false);
		add(cellStateInfoLabel);	
		
		cellValueLabel = new Label("Value: ");
		cellValueLabel.setSize(100, 20);
		cellValueLabel.setLocation(570, 335);
		cellValueLabel.setVisible(false);
		add(cellValueLabel);
		
		cellStateLabel = new Label("State: ");
		cellStateLabel.setSize(170, 20);
		cellStateLabel.setLocation(570, 355);
		cellStateLabel.setVisible(false);
		add(cellStateLabel);

		//------------------------------------------------------------------------------------------> COLOR LEGEND
		
		colorServerLabel = new Label("Initialized by Server");
		colorServerLabel.setSize(140,20);
		colorServerLabel.setLocation(600, 395);
		colorServerLabel.setVisible(false);
		add(colorServerLabel);

		colorContributedLabel = new Label("Value Contributed");
		colorContributedLabel.setSize(140,20);
		colorContributedLabel.setLocation(600, 415);
		colorContributedLabel.setVisible(false);
		add(colorContributedLabel);
		
		colorCommittedLabel = new Label("Value Committed");
		colorCommittedLabel.setSize(140,20);
		colorCommittedLabel.setLocation(600, 435);
		colorCommittedLabel.setVisible(false);
		add(colorCommittedLabel);
		
		colorAcceptedLabel = new Label("Value Accepted");
		colorAcceptedLabel.setSize(140,20);
		colorAcceptedLabel.setLocation(600, 455);
		colorAcceptedLabel.setVisible(false);
		add(colorAcceptedLabel);	
		
		colorRowAgentLabel = new Label("Row Agent");
		colorRowAgentLabel.setSize(140,20);
		colorRowAgentLabel.setLocation(600, 485);
		colorRowAgentLabel.setVisible(false);
		add(colorRowAgentLabel);
		
		colorColumnAgentLabel = new Label("Column Agent");
		colorColumnAgentLabel.setSize(140,20);
		colorColumnAgentLabel.setLocation(600, 505);
		colorColumnAgentLabel.setVisible(false);
		add(colorColumnAgentLabel);
		
		colorSquareAgentLabel = new Label("Square Agent");
		colorSquareAgentLabel.setSize(140,20);
		colorSquareAgentLabel.setLocation(600, 525);
		colorSquareAgentLabel.setVisible(false);
		add(colorSquareAgentLabel);
		
		colorLeaderLabel = new Label("Leader Agent");
		colorLeaderLabel.setSize(140,20);
		colorLeaderLabel.setLocation(600, 545);
		colorLeaderLabel.setVisible(false);
		add(colorLeaderLabel);
		
		colorUserContributorLabel = new Label("User");
		colorUserContributorLabel.setSize(140,20);
		colorUserContributorLabel.setLocation(600, 565);
		colorUserContributorLabel.setVisible(false);
		add(colorUserContributorLabel);
		
		//------------------------------------------------------------------------------------------
		
		networkController = new AgentNetworkController(this);
	}

	@Override
	public void paint ( Graphics gr )
	{
		initDraw(gr);				//Clean the Applet
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
	public void DrawGrid(Graphics gr) 
	{
		gr.setColor(Color.black);
		
		Stroke stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		int lineX = gridXOffset;
		float tempLineX = lineX;
		int lineY = gridYOffset;
		float tempLineY = lineY;

		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		gr.drawLine(20, 460, 470, 460);				//Separator Grid Horizintal
		gr.drawLine(470, 10, 470, 460);				//Separator Grid Vertical-Right
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);		
		
		gr.drawLine(10, 495, 550, 495);				//Add Agents Box
		gr.drawLine(10, 550, 550, 550);			
		gr.drawLine(10, 590, 550, 590);			
		gr.drawLine(10, 495, 10, 590);
		gr.drawLine(550, 495, 550, 590);
		
		gr.drawLine(485, 10, 485, 240);				//Connected Agents Box
		gr.drawLine(485, 10, 745, 10);			
		gr.drawLine(745, 10, 745, 240);			
		gr.drawLine(485, 240, 745, 240);
		
		gr.drawLine(565, 310, 565, 380);			//Box Cell State
		gr.drawLine(565, 310, 745, 310);			
		gr.drawLine(745, 310, 745, 380);			
		gr.drawLine(565, 380, 745, 380);
				
		gr.drawLine(565, 390, 565, 590);			//Box Colors Legend
		gr.drawLine(565, 390, 745, 390);			
		gr.drawLine(745, 390, 745, 590);			
		gr.drawLine(565, 590, 745, 590);
		
		gr.drawLine(485, 250, 485, 300);			//Box Values Contributed
		gr.drawLine(485, 250, 565, 250);			
		gr.drawLine(565, 250, 565, 300);			
		gr.drawLine(485, 300, 565, 300);
		
		gr.drawLine(575, 250, 575, 300);			//Box Values Committed
		gr.drawLine(575, 250, 655, 250);			
		gr.drawLine(655, 250, 655, 300);			
		gr.drawLine(575, 300, 655, 300);
		
		gr.drawLine(665, 250, 665, 300);			//Box Bugs Reported
		gr.drawLine(665, 250, 745, 250);			
		gr.drawLine(745, 250, 745, 300);			
		gr.drawLine(665, 300, 745, 300);
		
		gr.drawLine(485, 310, 485, 380);			//Box Voting On
		gr.drawLine(485, 310, 550, 310);			
		gr.drawLine(550, 310, 550, 380);			
		gr.drawLine(485, 380, 550, 380);
		
		gr.setColor(agentColors[votingColor]);		//Voting Box		
		if (getConflictExists())
			gr.setColor(Color.green);
		else
			gr.setColor(Color.red);
		
		gr.fillRect(500, 357, 35, 15);
		gr.setColor(Color.black);
		gr.drawRect(500, 357, 35, 15);
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(485, 390, 485, 460);			//Correct Values
		gr.drawLine(485, 390, 550, 390);			
		gr.drawLine(550, 390, 550, 460);			
		gr.drawLine(485, 460, 550, 460);
		
		//---------------------------------------------------------------------------> LEGEND
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.setColor(agentColors[serverColor]);					// Initialized by the Server
		gr.fillRect(582, 400, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 400, 10, 10);
		
		gr.setColor(agentColors[valueContributedColor]);		// Value Contributed
		gr.fillRect(582, 420, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 420, 10, 10);
		
		gr.setColor(agentColors[valueCommittedColor]);			// Value Committed
		gr.fillRect(582, 440, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 440, 10, 10);
		
		gr.setColor(agentColors[valueAcceptedColor]);			// Value Acceptted
		gr.fillRect(582, 460, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 460, 10, 10);
		
		
		gr.setColor(agentColors[rowAgentColor]);				// Rows Agent
		gr.fillRect(582, 490, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 490, 10, 10);
		
		gr.setColor(agentColors[columnAgentColor]);				// Columns Agent
		gr.fillRect(582, 510, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 510, 10, 10);
		
		gr.setColor(agentColors[squareAgentColor]);				// Square Agent
		gr.fillRect(582, 530, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 530, 10, 10);
		
		gr.setColor(agentColors[agentLeaderColor]);				// Leader Agent
		gr.fillRect(582, 550, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 550, 10, 10);
		
		gr.setColor(agentColors[userColor]);					// User
		gr.fillRect(582, 570, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(582, 570, 10, 10);
		
		//---------------------------------------------------------------------------> Information Boxes
		
		int val = getCountCorrect();
		if (val>0 && val<100)
			countLabel.setLocation(497, 437);
		else if (val>=100)
			countLabel.setLocation(495, 437);
		countLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
		val = getCountContributed();
		if (val < 10)
			countContributedLabel.setLocation(520, 285);
		else if (val>0 && val<100)
			countContributedLabel.setLocation(517, 285);
		else if (val>=100)
			countContributedLabel.setLocation(515, 285);		
		countContributedLabel.setText(val + "");
		
		val = getCountCommitted();
		if (val < 10)
			countCommittedLabel.setLocation(610, 285);
		else if (val>9 && val<100)
			countCommittedLabel.setLocation(608, 285);
		else if (val>=100)
			countCommittedLabel.setLocation(605, 285);
		countCommittedLabel.setText(val + "");
		
		val = getCountReported();
		if (val < 10)
			countReportedLabel.setLocation(700, 285);
		else if (val>9 && val<100)
			countReportedLabel.setLocation(698, 285);
		else if (val>=100)
			countReportedLabel.setLocation(695, 285);
		countReportedLabel.setText(val + "");
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		lineX = gridXOffset;
		lineY = gridYOffset;
		tempLineY = lineY;
		tempLineX = lineX;
		
		//---------------------------------------------------------------------------> Cell States
		
		cellStateInfoLabel.setText("Active Cell: " + "[" + activeX + "][" + activeY + "]");
		cellValueLabel.setText("Current Value: " + cells[activeX][activeY].current);
		cellStateLabel.setText("State: " + getLabelStateCell(cells[activeX][activeY].valueState));
		
		//---------------------------------------------------------------------------> Draw the bakground of the Active Cell
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.setColor(activeBackgroundColor);
		int activeRectX;
		int activeRectY;
		int[] region;
		
		/*if(!conflictExists)
		{
			activeRectX = (int) (gridXOffset + activeX * deltaX);
			activeRectY = (int) (gridYOffset + activeY * deltaY);
			region = Agent.getRegion(activeX, activeY);
		}
		else
		{
			activeRectX = (int) (gridXOffset + conflictX * deltaX);
			activeRectY = (int) (gridYOffset + conflictY * deltaY);
			region = Agent.getRegion(conflictX, conflictY);
		}*/
		
		activeRectX = (int) (gridXOffset + activeX * deltaX);
		activeRectY = (int) (gridYOffset + activeY * deltaY);
		region = Agent.getRegion(activeX, activeY);
		
		
		int activeRegionRectX = (int) (gridXOffset + region[0] * deltaX);
		int activeRegionRectY = (int) (gridYOffset + region[1] * deltaY);
		
		gr.fillRect(activeRectX, gridYOffset, (int) deltaX, (int) deltaY * 16);
		gr.fillRect(gridXOffset, activeRectY, (int) deltaX * 16, (int) deltaY);
		gr.fillRect(activeRegionRectX, activeRegionRectY, (int) deltaX * 4, (int) deltaY * 4);
		
		//---------------------------------------------------------------------------> Draw Values
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		Color backgroundCell = null;
		Color ovallCell = null;
		
		for (int y = 0; y < sudokuSize; y++) 
		{
			for (int x = 0; x < sudokuSize; x++) 
			{
				if (cells[x][y].valueState == waitingValue) 			//If There's not value in the cell
				{
					tempLineX += deltaX;
					lineX = (int) tempLineX;
					continue;
				}
				else				
				{
					switch(cells[x][y].valueState)			
					{
						case intializedByServer:
							backgroundCell = new Color(238, 233, 191);						//Cell from the Server
							ovallCell = agentColors[serverColor];
							break;
						case contributedByRows:
							backgroundCell = agentColors[rowAgentColor];					//Contributed By Rows 
							ovallCell = agentColors[valueContributedColor];
							break;
						case contributedByColumns:
							backgroundCell = agentColors[columnAgentColor];					//Contributed By Columns	
							ovallCell = agentColors[valueContributedColor];
							break;
						case contributedBySquares:
							backgroundCell = agentColors[squareAgentColor];					//Contributed By Squares	
							ovallCell = agentColors[valueContributedColor];
							break;
						case contributedByUser:
							backgroundCell = agentColors[userColor];						//Contributed By User		
							ovallCell = agentColors[valueContributedColor];
							break;
						case reportedByRows:
							backgroundCell = agentColors[rowAgentColor];					//Reported By Rows 		
							ovallCell = agentColors[valueReportedColor];
							break;
						case reportedByColumns:
							backgroundCell = agentColors[columnAgentColor];					//Reported By Columns	
							ovallCell = agentColors[valueReportedColor];
							break;
						case reportedBySquares:
							backgroundCell = agentColors[squareAgentColor];					//Reported By Squares	
							ovallCell = agentColors[valueReportedColor];
							break;
						case reportedByUser:
							backgroundCell = agentColors[userColor];						//Reported By User		
							ovallCell = agentColors[valueReportedColor];
							break;
						case committedByTesterByRows:
							backgroundCell = agentColors[rowAgentColor];					//Committed By Rows 		
							ovallCell = agentColors[valueCommittedColor];
							break;
						case committedByTesterByColumns:
							backgroundCell = agentColors[columnAgentColor];					//Committed By Columns				
							ovallCell = agentColors[valueCommittedColor];
							break;
						case committedByTesterBySquares:	
							backgroundCell = agentColors[squareAgentColor];					//Committed By Squares	
							ovallCell = agentColors[valueCommittedColor];
							break;
						case committedByTesterByUser:
							backgroundCell = agentColors[userColor];						//Committed By User		
							ovallCell = agentColors[valueCommittedColor];
							break;
						case notCommitted:
							backgroundCell = agentColors[valueCommittedColor];				//Not Committed!
							ovallCell = agentColors[valueNotCommittedColor];
							break;
						case acceptedByAgent:
							backgroundCell = agentColors[agentLeaderColor];					//Accepted by Agent		
							ovallCell = agentColors[valueAcceptedColor];
							break;
						case acceptedByUser:
							backgroundCell = agentColors[userLeaderColor];					//Accepted by User	
							ovallCell = agentColors[valueAcceptedColor];
							break;
						case rejectedByAgent:
							backgroundCell = agentColors[agentLeaderColor];				//Rejected by Agent	
							ovallCell = agentColors[valueRejectedColor];
							break;
						case rejectedByUser:
							backgroundCell = agentColors[userLeaderColor];				//Rejected By User			
							ovallCell = agentColors[valueRejectedColor];
							break;
					}
					
					gr.setColor(backgroundCell);
					gr.fillRect(lineX, lineY, (int) deltaX, (int) deltaY);
					
					gr.setColor(Color.black);
					gr.drawOval(lineX + 2, lineY + 2, (int) deltaX - 4, (int) deltaY - 4);
					
					gr.setColor(ovallCell);
					gr.fillOval(lineX + 2, lineY + 2, (int) deltaX - 4, (int) deltaY - 4);	
					
					if(conflictExists && x == conflictX && y == conflictY)				//If there's any voting open
					{
						int conX = (int) (gridXOffset + conflictX * deltaX);
						int conY = (int) (gridYOffset + conflictY * deltaY);
						
						setVotingCell(conflictX, conflictY);
						cells[activeX][activeY].DrawAgentDomainConflict(gr, conX, conY, conflictX, conflictY, agentColors);
						gr.setColor(agentColors[votingColor]);
					}
				}
				
				//---------------------------------------------------------------------------> Write Values on the grid
				
				if(cells[x][y].valueState != waitingValue)	
				{
					gr.setColor(Color.white);
					if(cells[x][y].current < 10) 
					{
						gr.drawString(String.valueOf(cells[x][y].current), (int) (lineX + deltaX / 2) - 2, (int) (lineY + deltaY) - 10);
					}
					else
					{
						gr.drawString(String.valueOf(cells[x][y].current), (int) (lineX + deltaX / 2) - 5, (int) (lineY + deltaY) - 10);
					}
				}
				
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

		//---------------------------------------------------------------------------> Draw Vertical Lines
		
		gr.setColor(Color.black);
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		lineX = gridXOffset;
		tempLineX = lineX;
		lineY = gridYOffset;
		tempLineY = lineY;
		
		
		for (int i = 0; i <= sudokuSize; i++) {
			gr.drawLine(lineX, lineY, lineX, lineY + gridHeight);
			tempLineX += deltaX;
			lineX = (int) tempLineX;
		}

		//---------------------------------------------------------------------------> Draw Horizontal Lines
		
		lineX = gridXOffset;
		lineY = gridYOffset;
		
		for (int i = 0; i <= sudokuSize; i++) {
			gr.drawLine(lineX, lineY, lineX + gridWidth, lineY);
			tempLineY += deltaY;
			lineY = (int) tempLineY;
		}

		//---------------------------------------------------------------------------> Draw Thick Lines
		
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
		
		//---------------------------------------------------------------------------> Draw The Square of the cell where the mouse is poiting at
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		
		if (mouseOverGrid) 			
		{
			gr.setColor(mouseOverColor);
			gr.drawRect((int) (gridXOffset + mouseOverX * deltaX), (int) (gridYOffset + mouseOverY * deltaY), (int) deltaX, (int) deltaY);
		}
		
		//---------------------------------------------------------------------------> Draw The Square on the Active Cell
		
		gr.setColor(activeCellColor);
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
	
	public void destroy()
	{
		ThreadsInformation threadInfo;
		
		for(int i=0; i < Agent.getAgentListSize(); i++)
		{
			threadInfo = Agent.getThreadInformationList(i);
			AgentNetworkController.SendMessage("disconnect#" + threadInfo.agentId + "," + threadInfo.agentType);
		}
	}
	
	public void MessageReceived(String message)
	{		
		String[] vars = message.split("#");
		String[] vars2 = null;
		String equation = null;
		
		int x, y;
		
		int agentId, agentType;
		
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
					countContributed++;
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
							
							networkController.setPositionConflic(conflictX, conflictY);
							
							voteTimer = new Timer(voteDelay, this);
							voteTimer.setActionCommand("voteEnd");
							voteTimer.start();
							break;
						case "voting exists":
							//TODO: Nothing to do?
							repaint();
							break;
					}
					break;
				case "clear":
					conflictExists = false;
					voteTimer.stop();
					
					vars2 = vars[1].split(",");
					conflictX = Integer.parseInt(vars2[0]);
					conflictY = Integer.parseInt(vars2[1]);
					int clearState = Integer.parseInt(vars2[2]);
					
					ClearCell(conflictX, conflictY, clearState);
					
					repaint();
					break;
				case "bugFound":
					vars2 = vars[1].split(",");
					int xClear = Integer.parseInt(vars2[0]);
					int yClear = Integer.parseInt(vars2[1]);
					int bugState = Integer.parseInt(vars2[2]);
					ClearCell(xClear, yClear, bugState);
					
					countReported++;
					repaint();
					break;
				case "committed":
					conflictExists = false;
					voteTimer.stop();
					
					vars2 = vars[1].split(",");
					conflictX = Integer.parseInt(vars2[0]);
					conflictY = Integer.parseInt(vars2[1]);
					int committedState = Integer.parseInt(vars2[2]);
					
					SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, committedState);
					
					countCommitted++;
					repaint();
					break;	
				case "accepted":		//networkController.BroadcastMessage("accepted#" + x + "," + y + "," + cells[x][y].current + "," + 7);
			
					vars2 = vars[1].split(",");
					x = Integer.parseInt(vars2[0]);
					y = Integer.parseInt(vars2[1]);
					int acceptedState = Integer.parseInt(vars2[2]);
					
					System.out.println("Accepted State: " + acceptedState);
					
					SetValueAndState(x, y, cells[x][y].current, acceptedState);				
					break;
				case "rejected":		//networkController.BroadcastMessage("rejected#" + x + "," + y + "," + rejected);
				
					vars2 = vars[1].split(",");
					x = Integer.parseInt(vars2[0]);
					y = Integer.parseInt(vars2[1]);
					int rejectedState = Integer.parseInt(vars2[2]);
					
					ClearCell(x, y, rejectedState);			
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
		//playExecution.setVisible(true);
		//pauseExecution.setVisible(true);
		cellStateInfoLabel.setVisible(true);	
		cellValueLabel.setVisible(true);
		cellStateLabel.setVisible(true);
		
		colorServerLabel.setVisible(true);
		colorRowAgentLabel.setVisible(true);
		colorColumnAgentLabel.setVisible(true);
		colorSquareAgentLabel.setVisible(true);
		colorLeaderLabel.setVisible(true);
		colorUserContributorLabel.setVisible(true);
		colorCommittedLabel.setVisible(true);
		colorAcceptedLabel.setVisible(true);
		colorContributedLabel.setVisible(true);
		votingLabel.setVisible(true);
		votingLabelON.setVisible(true);
		
		valuesLabel.setVisible(true);
		correctLabel.setVisible(true);
		countLabel.setVisible(true);
		valuesContributedLabel.setVisible(true);
		countContributedLabel.setVisible(true);
		contributedLabel.setVisible(true);
		valuesCommittedLabel.setVisible(true);
		committedLabel.setVisible(true);
		countCommittedLabel.setVisible(true);
		valuesReportedLabel.setVisible(true);
		reportedLabel.setVisible(true);
		countReportedLabel.setVisible(true);

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
	
	public void setVotingCell(int x, int y)
	{
		votingX = x;
		votingY = y;
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
		return countContributed;
	}
	
	int getCountCommitted()
	{
		return countCommitted;
	}
	
	int getCountReported()
	{
		return countReported;
	}
	
	String getLabelTypeAgent(int typeAgent)
	{
		String stringAgentType = "";
		
		switch(typeAgent)
		{
			case 0:
				stringAgentType = "Contributor by Rows";
				break;
			case 1:
				stringAgentType = "Contributor by Columns";
				break;
			case 2:
				stringAgentType = "Contributor by Squares";
				break;
			case 3:
				stringAgentType = "User Contributor";
				break;
			case 4:
				stringAgentType = "Bug Reporter by Rows";
				break;
			case 5:
				stringAgentType = "Bug Reporter by Columns";
				break;
			case 6:
				stringAgentType = "Bug Reporter by Squares";
				break;
			case 7:
				stringAgentType = "User Bug Reporter";
				break;
			case 8:
				stringAgentType = "Tester by Rows";
				break;
			case 9:
				stringAgentType = "Tester by Columns";
				break;
			case 10:
				stringAgentType = "Tester by Squares";
				break;
			case 11:
				stringAgentType = "User Tester";
				break;
			case 12:
				stringAgentType = "Comitter by Rows";
				break;
			case 13:
				stringAgentType = "Comitter by Columns";
				break;
			case 14:
				stringAgentType = "Comitter by Squares";
				break;
			case 15:
				stringAgentType = "User Committer";
				break;
			case 16:
				stringAgentType = "Agent Project Leader";
				break;
			case 17:
				stringAgentType = "User Project Leader";
				break;
		}
		
		return stringAgentType;
	}
	
	String getLabelStateCell(int state)
	{
		String stringState = "";
		
		switch(state)
		{
			case 0:
				stringState = "Waiting Value";
				break;
			case 1:
				stringState = "Initialized by Server";
				break;
			case 2:
				stringState = "Contributed (Row Ag.)";
				break;
			case 3:
				stringState = "Contributed (Col. Agent)";
				break;
			case 4:
				stringState = "Contributed (Square Ag.)";
				break;
			case 5:
				stringState = "Contributed (User)";
				break;
			case 6:
				stringState = "Reported (Row Ag.)";
				break;
			case 7:
				stringState = "Reported (Col. Agent)";
				break;
			case 8:
				stringState = "Reported (Square Ag.)";
				break;
			case 9:
				stringState = "Reported (User)";
				break;
			case 10:
				stringState = "Committed (Row Ag.)";
				break;
			case 11:
				stringState = "Committed (Col. Agent)";
				break;
			case 12:
				stringState = "Committed (Square Ag.)";
				break;
			case 13:
				stringState = "Committed (User)";
				break;
			case 14:
				stringState = "NOT Committed (Voting)";
				break;
			case 15:
				stringState = "Accepted (Ag. Leader)";
				break;
			case 16:
				stringState = "Accepted (User Leader)";
				break;
			case 17:
				stringState = "Rejected (Ag. Leader)";
				break;
			case 18:
				stringState = "Rejected (User Leader)";
				break;
		}
		
		return stringState;
	}
}