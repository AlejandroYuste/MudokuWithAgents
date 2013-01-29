import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.integer.IntDomain;

public class ClientGameController extends GameController implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	public enum NetworkState {idle, waitingInit, waitingConfirm}
	NetworkState networkState;
	
	protected enum ActualRole {PreGame, Observer, Contributor, BugReproter, Tester, Committer, Leader}
	protected ActualRole actualRole;

	//--------------> Elements from here:
	List console;
	
	TextField ipField;
	TextField portField;
	TextField userNameField;
	TextField questionsToJoinField;
	TextField contributionsToBePromotedField;
	TextField bugsReportedToBePromotedField;
	TextField valuesTestedToBePromotedField;
	TextField correctVotesToBePromotedField; 
	
	//--------------> Buttons from here:
	
	// PreGame Buttons
	Button connectClientButton;
	
	// Observer Buttons
	Button joinCommunity;
	
	// Contributor Buttons
	Button getPromotedReporter;
	
	// Bug Reporter Buttons
	Button getPromotedTester;
	Button removeValue;
	
	// Bug Tester Buttons
	Button getPromotedCommitter;
	Button askForCommiting;
	
	// Committer Buttons
	Button getPromotedLeader;
	Button voteCommiting;
	Button voteRemove;
	
	// Leader Buttons
	Button acceptValueLeader;
	Button removeValueLeader;
	
	// General Buttons
	Button getContributor;
	Button getReporter;
	Button getTester;
	Button getCommitter;
	Button getLeader;
	
	//--------------> Labels from here:
	
	// PreGame Labels
	Label Title;
	Label informationL1Label;
	Label informationL2Label;
	Label informationConnectionLabel;
	
	Label connectionSettingsLabel;
	Label ipFieldLabel;
	Label portFieldLabel;
	Label ipFieldDoubtLabel;
	Label userNameLabel;
	
	Label governationRulesLabel;
	Label questionsToJoinLabel;
	Label contributionsToBePromotedLabel;
	Label bugsReportedToBePromotedLabel;
	Label valuesTestedToBePromotedLabel;
	Label correctVotesToBePromotedLabel;
	
	// Game Labels
	Label logLabel;
	Label actualRoleLabel;
	
	
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
	Label backgroundLabel;
	Label foregroundLabel;
	
	//Labels State of the Cell
	Label cellStateInfoLabel;	
	Label cellValueLabel;
	Label cellStateLabel;
	
	//Labels Voting ON
	Label votingLabel;
	Label votingLabelON;
	
	Label valuesContributedLabel;
	Label valuesCommittedLabel;
	Label contributedLabel;
	Label committedLabel;
	Label countContributedLabel;
	Label countCommittedLabel;
	Label correctLabel;
	Label valuesLabel;
	Label countLabel;
	Label emptyCountFreeLabel;
	Label positionCountFreeLabel;
	Label countFreeLabel;
	
	Label rolInformation;
	Label rolL1Information;
	Label rolL2Information;
	Label rolL3Information;
	
	String userName;
	
	int[] gameSettings;
	
	int[][] instantiator;
	static ClientNetworkController networkController;
	
	int[] positionContributed;
	ArrayList<int[]> positionContributedList = new ArrayList<int[]>();
	int numContributionsCommited = 0;
	
	int[] positionCommitted;
	ArrayList<int[]> positionCommittedList = new ArrayList<int[]>();
	int numCommittedSatisfactory = 0;
	
	int[] positionVoted;
	ArrayList<int[]> positionVotedPositivelyList = new ArrayList<int[]>();
	ArrayList<int[]> positionVotedNegativelyList = new ArrayList<int[]>();
	int numVotedSatisfactory = 0;
		
	boolean contributor = false;
	boolean tester = false;
	boolean committer = false;
	boolean leader = false;
	boolean isFirstTime = true;
	
	int conflictX;
	int conflictY;
	
	int voteDelay = 10000;
	
	int clientId;
	int clientType;
	
	Timer voteTimer;
	
	boolean conflictExists;
	int clearRequester;
	
	//Constants for the colors
	
	static Color[] clientColors;
	
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
	
	final int numQuestionsSettings = 0;
	final int numContributionsSettings = 1;
	final int numBugReportedSettings = 2;
	final int numTestedSettings = 3;
	final int numVotesSettings = 4;
	
	public ClientGameController()
	{
		super();
		clearRequester = -1;
		GameController.sudokuSize = 16;
		networkState = NetworkState.idle;
		actualRole = ActualRole.PreGame;
		clientType = passiveUser;
		clientColors = new Color[14];
		gameSettings = new int[5];
		conflictExists = false;
		
		clientColors[serverColor] = new Color(255, 193, 37);						// 0.  Server 				--> goldenrod 1
		clientColors[rowAgentColor] = new Color(188,	210, 238);					// 1.  Row Agent 			--> lightsteelblue 2
		clientColors[columnAgentColor] = new Color(173, 255, 47);					// 2.  Columns Agent 		--> greenyellow
		clientColors[squareAgentColor] = new Color(255, 236,	139);				// 3.  Square Agent			--> lightgoldenrod 1
		clientColors[userColor] = new Color(238, 180, 180);							// 4.  User 				--> rosybrown 2
		clientColors[agentLeaderColor] = new Color(205, 201,	201);				// 5.  Leader Agent			--> snow 3
		clientColors[userLeaderColor] = new Color(0, 0, 0);							// 6.  User Leader			--> black
		clientColors[valueContributedColor] = new Color(56, 142, 142);				// 7.  Value Contributed	--> sgi teal
		clientColors[valueReportedColor] = new Color(255, 255, 255);				// 8.  Value Reported		--> white
		clientColors[valueCommittedColor] = new Color(220, 20, 60);					// 9.  Value Committed		--> crimson
		clientColors[valueNotCommittedColor] = new Color(255, 255, 255);			// 10. Value NOT Committed	--> white
		clientColors[valueAcceptedColor] = new Color(255, 193, 37);					// 11. Value Accepted 		--> goldenrod 1
		clientColors[valueRejectedColor] = new Color(255, 255, 255);				// 12. Value Rejected		--> white
		clientColors[votingColor] = new Color(220, 20, 60);							// 13. Voting Color			--> crimson
	}

	public void init()			
	{
		super.init();
		
		//-------------------------------         COMPONENTS FROM HERE         -------------------------------//
		
		//-----------------------------------------------------------------> PreGame Frame
		
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
		informationL1Label.setText("This tool have been created with an eductaional purpose. In this framework you can join to a simulation");
		add(informationL1Label);
		
		informationL2Label = new Label();
		informationL2Label.setLocation(92, 140);
		informationL2Label.setSize(600, 20);
		informationL2Label.setText("of an Open Source Community in order to participate in the different available roles and learn how it works.");
		add(informationL2Label);
		
		informationConnectionLabel = new Label("(To run this aplication a server must be running)");
		informationConnectionLabel.setLocation(240, 300);
		informationConnectionLabel.setSize(600, 20);
		add(informationConnectionLabel);
		
		connectClientButton = new Button("Connect to the Server");
		connectClientButton.setSize(150,75);
		connectClientButton.setLocation(300, 200);
		connectClientButton.setActionCommand("connect");
		connectClientButton.addActionListener(this);				
		add(connectClientButton);
		
		// Connections Settings
		
		connectionSettingsLabel = new Label("Connection Settings:");
		connectionSettingsLabel.setSize(250,20);
		connectionSettingsLabel.setLocation(35, 365);
		add(connectionSettingsLabel);
		
		ipFieldDoubtLabel = new Label("(If you have any doubt use the default values)");
		ipFieldDoubtLabel.setSize(250,20);
		ipFieldDoubtLabel.setLocation(65, 385);
		add(ipFieldDoubtLabel);
		
		ipFieldLabel = new Label(" - Select the IP of the Server:");
		ipFieldLabel.setSize(180,20);
		ipFieldLabel.setLocation(50, 420);
		add(ipFieldLabel);
		
		ipField = new TextField(20);								//IP of the Server
		ipField.setSize(100,20);
		ipField.setLocation(230, 420);
		ipField.setText("127.0.0.1");			
		add(ipField);
		
		portFieldLabel = new Label(" - Select the Port of the Server:");
		portFieldLabel.setSize(180,20);
		portFieldLabel.setLocation(50, 440);
		add(portFieldLabel);
		
		portField = new TextField(4);								//Port
		portField.setSize(100,20);
		portField.setLocation(230, 440);
		portField.setText("4433");				
		add(portField);
		
		userNameLabel = new Label(" - Select an User Name:");
		userNameLabel.setSize(180,20);
		userNameLabel.setLocation(50, 460);
		add(userNameLabel);
		
		userNameField = new TextField(15);							//UserName
		userNameField.setSize(100,20);
		userNameField.setLocation(230, 460);
		userNameField.setText("Default User");
		add(userNameField);
		
		// Game Settings
		governationRulesLabel = new Label("Settings of the Game:");
		governationRulesLabel.setSize(180,20);
		governationRulesLabel.setLocation(420, 365);
		add(governationRulesLabel);
		
		questionsToJoinLabel = new Label(" - Questions to Join the Community: ");
		questionsToJoinLabel.setSize(200, 20);
		questionsToJoinLabel.setLocation(450, 400);
		add(questionsToJoinLabel);
		
		questionsToJoinField = new TextField(5);					//Questions to Join the Community
		questionsToJoinField.setSize(40, 20);
		questionsToJoinField.setLocation(660, 400);
		questionsToJoinField.setText("3");
		add(questionsToJoinField);
		
		contributionsToBePromotedLabel = new Label(" - Contributions to be Promoted: ");
		contributionsToBePromotedLabel.setSize(200, 20);
		contributionsToBePromotedLabel.setLocation(450, 420);
		add(contributionsToBePromotedLabel);
		
		contributionsToBePromotedField = new TextField(5);			//Contributions to Be Promoted
		contributionsToBePromotedField.setSize(40, 20);
		contributionsToBePromotedField.setLocation(660, 420);
		contributionsToBePromotedField.setText("3");
		add(contributionsToBePromotedField);
		
		bugsReportedToBePromotedLabel = new Label(" - Bugs Reported to be Promoted: ");
		bugsReportedToBePromotedLabel.setSize(200,20);
		bugsReportedToBePromotedLabel.setLocation(450, 440);
		add(bugsReportedToBePromotedLabel);
		
		bugsReportedToBePromotedField = new TextField(5);			//Bugs Reported to Be Promoted
		bugsReportedToBePromotedField.setSize(40, 20);
		bugsReportedToBePromotedField.setLocation(660, 440);
		bugsReportedToBePromotedField.setText("3");
		add(bugsReportedToBePromotedField);
		
		valuesTestedToBePromotedLabel = new Label(" - Values Tested to be Promoted: ");
		valuesTestedToBePromotedLabel.setSize(200,20);
		valuesTestedToBePromotedLabel.setLocation(450, 460);
		add(valuesTestedToBePromotedLabel);
		
		valuesTestedToBePromotedField = new TextField(5);			//Values Tested to Be Promoted
		valuesTestedToBePromotedField.setSize(40, 20);
		valuesTestedToBePromotedField.setLocation(660, 460);
		valuesTestedToBePromotedField.setText("3");
		add(valuesTestedToBePromotedField);
		
		correctVotesToBePromotedLabel = new Label(" - Votes to be Promoted: ");
		correctVotesToBePromotedLabel.setSize(200,20);
		correctVotesToBePromotedLabel.setLocation(450, 480);
		add(correctVotesToBePromotedLabel);
		
		correctVotesToBePromotedField = new TextField(5);			//Votes to Be Promoted
		correctVotesToBePromotedField.setSize(40, 20);
		correctVotesToBePromotedField.setLocation(660, 480);
		correctVotesToBePromotedField.setText("3");
		add(correctVotesToBePromotedField);
		
		//-----------------------------------------------------------------> Game Frame
		
		logLabel = new Label("Historic of the Game:");
		logLabel.setSize(250,20);
		logLabel.setLocation(gridXOffset + 40, 470);
		logLabel.setVisible(false);
		add(logLabel);
		
		actualRoleLabel = new Label("Current Role: " + actualRole);
		actualRoleLabel.setSize(175, 30);
		font = new Font("SansSerif", Font.PLAIN, 14);
		actualRoleLabel.setFont(font);
		actualRoleLabel.setLocation(550, 20);
		actualRoleLabel.setVisible(false);
		add(actualRoleLabel);
				
		//------------------------------------------------------------------------------------------> COLOR LEGEND
		
		foregroundLabel = new Label("Foreground Color:");
		foregroundLabel.setSize(110,20);
		foregroundLabel.setLocation(490, 460);
		foregroundLabel.setVisible(false);
		add(foregroundLabel);
		
		colorServerLabel = new Label("Initialized by Server");
		colorServerLabel.setSize(110,20);
		colorServerLabel.setLocation(505, 485);
		colorServerLabel.setVisible(false);
		add(colorServerLabel);

		colorContributedLabel = new Label("Value Contributed");
		colorContributedLabel.setSize(110,20);
		colorContributedLabel.setLocation(505, 505);
		colorContributedLabel.setVisible(false);
		add(colorContributedLabel);
		
		colorCommittedLabel = new Label("Value Committed");
		colorCommittedLabel.setSize(110,20);
		colorCommittedLabel.setLocation(505, 525);
		colorCommittedLabel.setVisible(false);
		add(colorCommittedLabel);
		
		colorAcceptedLabel = new Label("Value Accepted");
		colorAcceptedLabel.setSize(110,20);
		colorAcceptedLabel.setLocation(505, 545);
		colorAcceptedLabel.setVisible(false);
		add(colorAcceptedLabel);	
		

		backgroundLabel = new Label("Background Color:");
		backgroundLabel.setSize(110,20);
		backgroundLabel.setLocation(630, 460);
		backgroundLabel.setVisible(false);
		add(backgroundLabel);
		
		colorRowAgentLabel = new Label("Row Agent");
		colorRowAgentLabel.setSize(80,20);
		colorRowAgentLabel.setLocation(650, 485);
		colorRowAgentLabel.setVisible(false);
		add(colorRowAgentLabel);
		
		colorColumnAgentLabel = new Label("Column Agent");
		colorColumnAgentLabel.setSize(80,20);
		colorColumnAgentLabel.setLocation(650, 505);
		colorColumnAgentLabel.setVisible(false);
		add(colorColumnAgentLabel);
		
		colorSquareAgentLabel = new Label("Square Agent");
		colorSquareAgentLabel.setSize(80,20);
		colorSquareAgentLabel.setLocation(650, 525);
		colorSquareAgentLabel.setVisible(false);
		add(colorSquareAgentLabel);
		
		colorLeaderLabel = new Label("Leader Agent");
		colorLeaderLabel.setSize(80,20);
		colorLeaderLabel.setLocation(650, 545);
		colorLeaderLabel.setVisible(false);
		add(colorLeaderLabel);
		
		colorUserContributorLabel = new Label("User");
		colorUserContributorLabel.setSize(80,20);
		colorUserContributorLabel.setLocation(650, 565);
		colorUserContributorLabel.setVisible(false);
		add(colorUserContributorLabel);
				
		//------------------------------------------------------------------------------------------> Voting ON?
		
		votingLabel = new Label("Voting");
		votingLabel.setSize(50, 20);
		votingLabel.setLocation(495, 375);
		votingLabel.setVisible(false);
		add(votingLabel);	
		
		votingLabelON = new Label("ON?");
		votingLabelON.setSize(30, 10);
		votingLabelON.setLocation(500, 395);
		votingLabelON.setVisible(false);
		add(votingLabelON);
		
		//------------------------------------------------------------------------------------------> Cell State
		
		cellStateInfoLabel = new Label("Active Cell:");
		cellStateInfoLabel.setSize(140, 20);
		cellStateInfoLabel.setLocation(570, 375);
		cellStateInfoLabel.setVisible(false);
		add(cellStateInfoLabel);	
		
		cellValueLabel = new Label("Value: ");
		cellValueLabel.setSize(100, 20);
		cellValueLabel.setLocation(570, 395);
		cellValueLabel.setVisible(false);
		add(cellValueLabel);
		
		cellStateLabel = new Label("State: ");
		cellStateLabel.setSize(170, 20);
		cellStateLabel.setLocation(570, 415);
		cellStateLabel.setVisible(false);
		add(cellStateLabel);
		
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
		
		valuesContributedLabel = new Label("Values");
		valuesContributedLabel.setSize(50, 10);
		valuesContributedLabel.setLocation(595, 340);
		valuesContributedLabel.setVisible(false);
		add(valuesContributedLabel);	
		
		contributedLabel = new Label("Contributed");
		contributedLabel.setSize(70, 10);
		contributedLabel.setLocation(582, 355);
		contributedLabel.setVisible(false);
		add(contributedLabel);	
		
		countContributedLabel = new Label(0 + "/" + sudokuSize*sudokuSize);
		countContributedLabel.setSize(50, 10);
		countContributedLabel.setLocation(595, 370);
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
		
		emptyCountFreeLabel = new Label("Empty");
		emptyCountFreeLabel.setSize(50, 10);
		emptyCountFreeLabel.setLocation(687, 340);
		emptyCountFreeLabel.setVisible(false);
		add(emptyCountFreeLabel);	
		
		positionCountFreeLabel = new Label("Positions");
		positionCountFreeLabel.setSize(60, 10);
		positionCountFreeLabel.setLocation(680, 355);
		positionCountFreeLabel.setVisible(false);
		add(positionCountFreeLabel);	
		
		countFreeLabel = new Label(0 + "/" + sudokuSize*sudokuSize);
		countFreeLabel.setSize(50, 10);
		countFreeLabel.setLocation(695, 370);
		countFreeLabel.setVisible(false);
		add(countFreeLabel);
		
		//------------------------------------------------------------------------------------------
		rolInformation = new Label("You can contribute adding Values to the Grid");
		rolInformation.setSize(280, 20);
		rolInformation.setLocation(492, 100);
		rolInformation.setVisible(false);
		add(rolInformation);
		
		rolL1Information = new Label(" - Use the Numbers below the Grid.");
		rolL1Information.setSize(280, 20);
		rolL1Information.setLocation(510, 120);
		rolL1Information.setVisible(false);
		add(rolL1Information);
		
		rolL2Information = new Label(" - You could be promoted when 5 of your");
		rolL2Information.setSize(280, 20);
		rolL2Information.setLocation(510, 140);
		rolL2Information.setVisible(false);
		add(rolL2Information);
		
		rolL3Information = new Label("    contributinons have been committed.");
		rolL3Information.setSize(280, 20);
		rolL3Information.setLocation(510, 160);
		rolL3Information.setVisible(false);
		add(rolL3Information);
		
		// Components From Here
		
		console = new List();
		console.setLocation(20, 470);
		console.setSize(450, 125);
		console.setVisible(false);
		add(console);
		
		joinCommunity = new Button("Join to the Community");
		joinCommunity.setSize(180,40);
		joinCommunity.setLocation(530, 100);
		joinCommunity.setActionCommand("joinCommunity");
		joinCommunity.addActionListener(this);						
		joinCommunity.setVisible(false);
		add(joinCommunity);
		
		getPromotedTester = new Button("Get Promoted!");
		getPromotedTester.setSize(180,40);
		getPromotedTester.setLocation(530, 250);
		getPromotedTester.setActionCommand("getTester");
		getPromotedTester.addActionListener(this);					
		getPromotedTester.setVisible(false);	
		add(getPromotedTester);
		
		getPromotedCommitter = new Button("Get Promoted!");
		getPromotedCommitter.setSize(180,40);
		getPromotedCommitter.setLocation(530, 250);
		getPromotedCommitter.setActionCommand("getCommitter");
		getPromotedCommitter.addActionListener(this);				
		getPromotedCommitter.setVisible(false);
		add(getPromotedCommitter);
		
		getPromotedLeader = new Button("Get Promoted!");
		getPromotedLeader.setSize(180,40);
		getPromotedLeader.setLocation(530, 250);
		getPromotedLeader.setActionCommand("getLeader");
		getPromotedLeader.addActionListener(this);				
		getPromotedLeader.setVisible(false);
		add(getPromotedLeader);
		
		askForCommiting = new Button("Ask for Committing");
		askForCommiting.setSize(180,30);
		askForCommiting.setLocation(530, 170);
		askForCommiting.setActionCommand("askCommitting");
		askForCommiting.addActionListener(this);
		askForCommiting.setVisible(false);
		add(askForCommiting);
		
		removeValue = new Button("Report a Bug");
		removeValue.setSize(180,30);
		removeValue.setLocation(530, 210);
		removeValue.setActionCommand("removeValue");
		removeValue.addActionListener(this);				//Afegim el Listener al button "Connect"
		removeValue.setVisible(false);
		add(removeValue);
		
		voteCommiting = new Button("Vote Committing");
		voteCommiting.setSize(180,30);
		voteCommiting.setLocation(530, 170);
		voteCommiting.setActionCommand("no");
		voteCommiting.addActionListener(this);				//Afegim el Listener al button "Connect"
		voteCommiting.setVisible(false);
		add(voteCommiting);
		
		voteRemove = new Button("Vote Remove");
		voteRemove.setSize(180,30);
		voteRemove.setLocation(530, 210);
		voteRemove.setActionCommand("yes");
		voteRemove.addActionListener(this);				//Afegim el Listener al button "Connect"
		voteRemove.setVisible(false);
		add(voteRemove);
		
		acceptValueLeader = new Button("Accept the Value");
		acceptValueLeader.setSize(180,30);
		acceptValueLeader.setLocation(530, 170);
		acceptValueLeader.setActionCommand("acceptLeader");
		acceptValueLeader.addActionListener(this);				//Afegim el Listener al button "Connect"
		acceptValueLeader.setVisible(false);
		add(acceptValueLeader);
		
		removeValueLeader = new Button("Remove the Value");
		removeValueLeader.setSize(180,30);
		removeValueLeader.setLocation(530, 210);
		removeValueLeader.setActionCommand("removeLeader");
		removeValueLeader.addActionListener(this);				//Afegim el Listener al button "Connect"
		removeValueLeader.setVisible(false);
		add(removeValueLeader);
		
		getContributor = new Button("Contributor");
		getContributor.setSize(70,20);
		getContributor.setLocation(485, 70);
		getContributor.setActionCommand("joinCommunity");
		getContributor.addActionListener(this);				//Afegim el Listener al button "Connect"
		getContributor.setVisible(false);
		add(getContributor);
		
		getTester = new Button("Tester");
		getTester.setSize(45,20);
		getTester.setLocation(565, 70);
		getTester.setActionCommand("getTester");
		getTester.addActionListener(this);				//Afegim el Listener al button "Connect"
		getTester.setVisible(false);
		add(getTester);
		
		getCommitter = new Button("Committer");
		getCommitter.setSize(65,20);
		getCommitter.setLocation(620, 70);
		getCommitter.setActionCommand("getCommitter");
		getCommitter.addActionListener(this);				//Afegim el Listener al button "Connect"
		getCommitter.setVisible(false);
		add(getCommitter);
		
		getLeader = new Button("Leader");
		getLeader.setSize(50,20);
		getLeader.setLocation(695, 70);
		getLeader.setActionCommand("getLeader");
		getLeader.addActionListener(this);				//Afegim el Listener al button "Connect"
		getLeader.setVisible(false);
		add(getLeader);
		
		networkController = new ClientNetworkController(this);
	}

	@Override
	public void paint ( Graphics gr )
	{
		initDraw(gr);						//Clean the Frame.
		switch(actualRole)
		{
		case PreGame:
			DrawPreGame(gr);
			break;
		case Observer:
			//DrawObserver(gr);
			DrawGrid(gr);
			break;
		default:
			DrawGrid(gr);
			break;
		}
	}
	
	public void DrawPreGame(Graphics gr) {
		gr.setColor(Color.black);
		
		Stroke stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(10, 350, 370, 350);		// Connection Settings Box
		gr.drawLine(10, 350, 10, 530);	
		gr.drawLine(10, 530, 370, 530);	
		gr.drawLine(370, 350, 370, 530);	
		
		gr.drawLine(390, 350, 755, 350);		// Game Settings Box
		gr.drawLine(390, 350, 390, 530);	
		gr.drawLine(390, 530, 755, 530);	
		gr.drawLine(755, 350, 755, 530);	
	}
	
	public void DrawGrid(Graphics gr)
	{
		Stroke stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		gr.setColor(Color.black);
		
		int lineX = gridXOffset;
		float tempLineX = lineX;
		int lineY = gridYOffset;
		float tempLineY = lineY;
		int activeRectX;
		int activeRectY;
		int[] region;
		
		gr.drawLine(485, 10, 485, 60);				//Caixa Per al Rol
		gr.drawLine(485, 10, 745, 10);			
		gr.drawLine(745, 10, 745, 60);			
		gr.drawLine(485, 60, 745, 60);
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(20, 460, 470, 460);				//Separator Grid Horizintal
		gr.drawLine(470, 10, 470, 460);				//Separator Grid Vertical-Right
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(480, 450, 480, 595);			//Box Colors Legend
		gr.drawLine(480, 450, 745, 450);			
		gr.drawLine(745, 450, 745, 595);			
		gr.drawLine(480, 595, 745, 595);
		
		gr.drawLine(480, 440, 480, 370);			//Box Voting On
		gr.drawLine(480, 440, 550, 440);			
		gr.drawLine(550, 440, 550, 370);			
		gr.drawLine(480, 370, 550, 370);
		
				
		if (conflictExists)
			gr.setColor(clientColors[votingColor]);		//Voting Box
		else
			gr.setColor(Color.white);
		
		gr.fillRect(497, 415, 35, 15);
		gr.setColor(Color.black);
		gr.drawRect(497, 415, 35, 15);
		
		gr.drawLine(560, 440, 560, 370);			//Box Cell State
		gr.drawLine(560, 440, 745, 440);			
		gr.drawLine(745, 440, 745, 370);			
		gr.drawLine(560, 370, 745, 370);
		
		//---------------------------------------------------------------------------> LEGEND
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.setColor(clientColors[serverColor]);					// Initialized by the Server
		gr.fillRect(490, 490, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(490, 490, 10, 10);
		
		gr.setColor(clientColors[valueContributedColor]);		// Value Contributed
		gr.fillRect(490, 510, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(490, 510, 10, 10);
		
		gr.setColor(clientColors[valueCommittedColor]);			// Value Committed
		gr.fillRect(490, 530, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(490, 530, 10, 10);
		
		gr.setColor(clientColors[valueAcceptedColor]);			// Value Acceptted
		gr.fillRect(490, 550, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(490, 550, 10, 10);
		
		
		gr.setColor(clientColors[rowAgentColor]);				// Rows Agent
		gr.fillRect(630, 490, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(630, 490, 10, 10);
		
		gr.setColor(clientColors[columnAgentColor]);			// Columns Agent
		gr.fillRect(630, 510, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(630, 510, 10, 10);
		
		gr.setColor(clientColors[squareAgentColor]);			// Square Agent
		gr.fillRect(630, 530, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(630, 530, 10, 10);
		
		gr.setColor(clientColors[agentLeaderColor]);			// Leader Agent
		gr.fillRect(630, 550, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(630, 550, 10, 10);
		
		gr.setColor(clientColors[userColor]);					// User
		gr.fillRect(630, 570, 10, 10);
		gr.setColor(Color.black);
		gr.drawRect(630, 570, 10, 10);
		
		//---------------------------------------------------------------------------> Cell States
		
		cellStateInfoLabel.setText("Active Cell: " + "[" + activeX + "][" + activeY + "]");
		cellValueLabel.setText("Current Value: " + cells[activeX][activeY].current);
		cellStateLabel.setText("State: " + getLabelStateCell(cells[activeX][activeY].valueState));
		
		//---------------------------------------------------------------------------> Draw the bakground of the Active Cell
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.setColor(activeBackgroundColor);
		if(conflictExists && isFirstTime) {
			activeX = conflictX;
			activeY = conflictY;
			isFirstTime = false;
		}

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
							ovallCell = clientColors[serverColor];
							break;
						case contributedByRows:
							backgroundCell = clientColors[rowAgentColor];					//Contributed By Rows 
							ovallCell = clientColors[valueContributedColor];
							break;
						case contributedByColumns:
							backgroundCell = clientColors[columnAgentColor];				//Contributed By Columns	
							ovallCell = clientColors[valueContributedColor];
							break;
						case contributedBySquares:
							backgroundCell = clientColors[squareAgentColor];				//Contributed By Squares	
							ovallCell = clientColors[valueContributedColor];
							break;
						case contributedByUser:
							backgroundCell = clientColors[userColor];						//Contributed By User		
							ovallCell = clientColors[valueContributedColor];
							break;
						case reportedByRows:
							backgroundCell = clientColors[rowAgentColor];					//Reported By Rows 		
							ovallCell = clientColors[valueReportedColor];
							break;
						case reportedByColumns:
							backgroundCell = clientColors[columnAgentColor];				//Reported By Columns	
							ovallCell = clientColors[valueReportedColor];
							break;
						case reportedBySquares:
							backgroundCell = clientColors[squareAgentColor];				//Reported By Squares	
							ovallCell = clientColors[valueReportedColor];
							break;
						case reportedByUser:
							backgroundCell = clientColors[userColor];						//Reported By User		
							ovallCell = clientColors[valueReportedColor];
							break;
						case committedByTesterByRows:
							backgroundCell = clientColors[rowAgentColor];					//Committed By Rows 		
							ovallCell = clientColors[valueCommittedColor];
							break;
						case committedByTesterByColumns:
							backgroundCell = clientColors[columnAgentColor];				//Committed By Columns				
							ovallCell = clientColors[valueCommittedColor];
							break;
						case committedByTesterBySquares:	
							backgroundCell = clientColors[squareAgentColor];				//Committed By Squares	
							ovallCell = clientColors[valueCommittedColor];
							break;
						case committedByTesterByUser:
							backgroundCell = clientColors[userColor];						//Committed By User		
							ovallCell = clientColors[valueCommittedColor];
							break;
						case notCommitted:
							backgroundCell = clientColors[valueCommittedColor];				//Not Committed!
							ovallCell = clientColors[valueNotCommittedColor];
							break;
						case acceptedByAgent:
							backgroundCell = clientColors[agentLeaderColor];				//Accepted by Agent		
							ovallCell = clientColors[valueAcceptedColor];
							break;
						case acceptedByUser:
							backgroundCell = clientColors[userLeaderColor];					//Accepted by User	
							ovallCell = clientColors[valueAcceptedColor];
							break;
						case rejectedByAgent:
							backgroundCell = clientColors[agentLeaderColor];				//Rejected by Agent	
							ovallCell = clientColors[valueRejectedColor];
							break;
						case rejectedByUser:
							backgroundCell = clientColors[userLeaderColor];					//Rejected By User			
							ovallCell = clientColors[valueRejectedColor];
							break;
					}
					
					gr.setColor(backgroundCell);
					gr.fillRect(lineX, lineY, (int) deltaX, (int) deltaY);
					
					gr.setColor(Color.black);
					gr.drawOval(lineX + 2, lineY + 2, (int) deltaX - 4, (int) deltaY - 4);
					
					gr.setColor(ovallCell);
					gr.fillOval(lineX + 2, lineY + 2, (int) deltaX - 4, (int) deltaY - 4);	
					
					if(conflictExists && x == conflictX && y == conflictY)					//If there's any voting open
					{
						int conX = (int) (gridXOffset + conflictX * deltaX);
						int conY = (int) (gridYOffset + conflictY * deltaY);
						
						setVotingCell(conflictX, conflictY);
						cells[activeX][activeY].DrawAgentDomainConflict(gr, conX, conY, conflictX, conflictY, clientColors);
						gr.setColor(clientColors[votingColor]);
					}
				}
				
				//---------------------------------------------------------------------------> Write Values on the grid
				
				if(cells[x][y].valueState != waitingValue)	
				{
					gr.setColor(Color.white);
					if(cells[x][y].current < 10) {
						gr.drawString(String.valueOf(cells[x][y].current), (int) (lineX + deltaX / 2) - 2, (int) (lineY + deltaY) - 10);
					}
					else {
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
			cells[activeX][activeY].DrawDomainClient(gr, mouseOverDomainIndex, activeX, activeY, actualRole);		
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
		
		if (mouseOverGrid) {
			gr.setColor(mouseOverColor);
			gr.drawRect((int) (gridXOffset + mouseOverX * deltaX), (int) (gridYOffset + mouseOverY * deltaY), (int) deltaX, (int) deltaY);
		}
		
		//---------------------------------------------------------------------------> Draw The Square on the Active Cell
		
		gr.setColor(activeCellColor);
		gr.drawRect(activeRectX, activeRectY, (int) deltaX, (int) deltaY);
	}
	
	/*
	@Override
	public void DrawGrid(Graphics gr) {			//TODO: Queda molt de curro! Començar amb el observer!
		
		gr.drawLine(485, 310, 745, 310);	
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(485, 10, 485, 60);				//Caixa Per al Rol
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
		
		gr.drawLine(485, 330, 485, 390);			//Caixa valors committed
		gr.drawLine(485, 330, 565, 330);			
		gr.drawLine(565, 330, 565, 390);			
		gr.drawLine(485, 390, 565, 390);
		
		gr.drawLine(575, 330, 575, 390);			//Caixa valors contributed
		gr.drawLine(575, 330, 655, 330);			
		gr.drawLine(655, 330, 655, 390);			
		gr.drawLine(575, 390, 655, 390);
		
		gr.drawLine(665, 330, 665, 390);			//Caixa valors empty Positions
		gr.drawLine(665, 330, 745, 330);			
		gr.drawLine(745, 330, 745, 390);			
		gr.drawLine(665, 390, 745, 390);
		
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
		
		int val = getCountCorrect();
		if (val>0 && val<100)
			countLabel.setLocation(497, 455);
		else if (val>=100)
			countLabel.setLocation(495, 455);
		countLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
		val = getCountCommitted();
		if (val < 10)
			countCommittedLabel.setLocation(508, 370);
		else if (val>9 && val<100)
			countCommittedLabel.setLocation(505, 370);
		else if (val>=100)
			countCommittedLabel.setLocation(502, 370);
		countCommittedLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
		val = getFreePositions();
		if (val < 10)
			countFreeLabel.setLocation(688, 370);
		else if (val>0 && val<100)
			countFreeLabel.setLocation(685, 370);
		else if (val>=100)
			countFreeLabel.setLocation(682, 370);	
		countFreeLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
		val = getCountContributed();
		if (val < 10)
			countContributedLabel.setLocation(598, 370);
		else if (val>0 && val<100)
			countContributedLabel.setLocation(595, 370);
		else if (val>=100)
			countContributedLabel.setLocation(592, 370);		
		countContributedLabel.setText(val + "/" + sudokuSize*sudokuSize);
		
	}*/

	public void setActive(int x, int y) {
		activeX = x;
		activeY = y;
	}
	
	@Override
	public void actionPerformed(ActionEvent action) {
		super.actionPerformed(action);
		switch(action.getActionCommand())
		{
			case "connect":
				if(!ipField.equals("") && !portField.equals("")) {
					try {
						userName = userNameField.getText();
						networkController.Connect(ipField.getText(), Integer.parseInt(portField.getText()), userName);
						RequestInit();
						getObserver();
					} catch (IOException e) {
						System.out.println("Can not connect to server");
					}  
				} else {
					System.out.println("Check ip & port");
				}
				break;
			case "voteEnd":
				conflictExists = false;
				voteRemove.setVisible(false);
				voteCommiting.setVisible(false);
				voteTimer.stop();
				//repaint();
				break;
			case "yes":
				if(conflictExists)
				{
					networkController.SendMessage("voted#" + clientId + ",-1," + conflictX + "," + conflictY + "," + "1" + "," + userName);
					voteRemove.setVisible(false);
					voteCommiting.setVisible(false);
					
					positionVoted = new int[2];
					positionVoted[0] = conflictX;
					positionVoted[1] = conflictY;
					positionVotedPositivelyList.add(positionVoted);
				}
				break;
			case "no":
				if(conflictExists)
				{
					networkController.SendMessage("voted#" + clientId + ",-1," + conflictX + "," + conflictY + "," + "-1" + "," + userName);
					voteRemove.setVisible(false);
					voteCommiting.setVisible(false);
					
					positionVoted = new int[2];
					positionVoted[0] = conflictX;
					positionVoted[1] = conflictY;
					positionVotedNegativelyList.add(positionVoted);
				}
				break;
			case "askCommitting":
				networkController.SendMessage("clear#" + clientId + ",-1," + activeX + "," + activeY + "," + userName);
				
				positionCommitted = new int[2];
				positionCommitted[0] = activeX;
				positionCommitted[1] = activeY;
				positionCommittedList.add(positionCommitted);
				
				break;
			case "removeValue":
				networkController.SendMessage("testerClear#" + clientId + ",-1," + activeX + "," + activeY + "," + userName);
				break;
			case "joinCommunity":
				joinCommunity();
				break;
			case "getTester":
				getTester();
				break;
			case "getCommitter":
				getCommitter();
				break;
			case "getLeader":
				getLeader();
				break;
			case "acceptLeader":
				networkController.SendMessage("accepted#" + activeX + "," + activeY + "," + userName);
				acceptValueLeader.setVisible(false);
				removeValueLeader.setVisible(false);
				break;
			case "removeLeader":
				networkController.SendMessage("rejected#" + activeX + "," + activeY);
				acceptValueLeader.setVisible(false);
				removeValueLeader.setVisible(false);
				break;
		}							
	}
	
	public void getObserver()
	{
		actualRole = ActualRole.Observer;
		actualRoleLabel.setLocation(540, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		actualRoleLabel.setVisible(true);
		
		Title.setVisible(false);
		informationL1Label.setVisible(false);
		informationL2Label.setVisible(false);
		informationConnectionLabel.setVisible(false);
		
		connectionSettingsLabel.setVisible(false);
		ipFieldLabel.setVisible(false);
		portFieldLabel.setVisible(false);
		ipFieldDoubtLabel.setVisible(false);
		userNameLabel.setVisible(false);
		
		governationRulesLabel.setVisible(false);
		questionsToJoinLabel.setVisible(false);
		contributionsToBePromotedLabel.setVisible(false);
		bugsReportedToBePromotedLabel.setVisible(false);
		valuesTestedToBePromotedLabel.setVisible(false);
		correctVotesToBePromotedLabel.setVisible(false);
	}
	
	public void joinCommunity()
	{
		actualRole = ActualRole.Contributor;
		
		contributor = true;
		getContributor.setVisible(false);
		
		if(tester)
			getTester.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		
		joinCommunity.setVisible(false);
		actualRoleLabel.setLocation(550, 20);
		
		actualRoleLabel.setText("Current Role: " + actualRole);
		rolInformation.setVisible(true);
		rolL1Information.setVisible(true);
		rolL2Information.setVisible(true);
		rolL3Information.setVisible(true);
		repaint();
	}
	
	public void getTester()
	{
		actualRole = ActualRole.Tester;
		
		tester = true;
		getTester.setVisible(false);
		
		if(contributor)
			getContributor.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		rolL1Information.setLocation(480, 120);
		rolL2Information.setLocation(480, 140);

		rolInformation.setText("As a Tester you can check values:");
		rolL1Information.setText(" - If you think a value is correct ask for committing.");
		rolL2Information.setText(" - If you think a value is wrong report the bug.");
		rolL3Information.setVisible(false);
		getPromotedTester.setVisible(false);
		
		actualRoleLabel.setLocation(560, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		repaint();
	}

	public void getCommitter()
	{
		actualRole = ActualRole.Committer;
		
		committer = true;
		getCommitter.setVisible(false);
		
		if(contributor)
			getContributor.setVisible(true);
		if(tester)
			getTester.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		rolL1Information.setLocation(480, 120);
		rolL2Information.setLocation(480, 140);

		rolInformation.setText("Now you participate in Votations:");
		rolL1Information.setText(" - If the value is correct vote for Committe it");
		rolL2Information.setText(" - If the value is wrong vote for Remove it");
		rolL3Information.setVisible(false);

		getPromotedCommitter.setVisible(false);
		askForCommiting.setVisible(false);
		removeValue.setVisible(false);

		actualRoleLabel.setLocation(560, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		repaint();
	}

	public void getLeader()
	{
		actualRole = ActualRole.Leader;
		
		leader = true;
		getLeader.setVisible(false);
		
		if(contributor)
			getContributor.setVisible(true);
		if(tester)
			getTester.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		
		rolInformation.setLocation(488, 100);
		rolL1Information.setLocation(480, 120);

		rolInformation.setText("You decide if the values committed are accepted:");
		rolL1Information.setText(" - Select them and choose wisely.");
		rolL2Information.setVisible(false);
		rolL3Information.setVisible(false);

		getPromotedLeader.setVisible(false);
		askForCommiting.setVisible(false);
		removeValue.setVisible(false);
		voteRemove.setVisible(false);
		voteCommiting.setVisible(false);

		actualRoleLabel.setLocation(560, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		repaint();
	}
	
	public void RequestInit()
	{
		networkController.SendMessage("request#type=init");
		networkState = NetworkState.waitingInit;
	}
	
	@Override
	public void DomainClick(int index)
	{
		if(index != -1)
		{
			IntDomain idom = cpController.GetCPVariable(activeX, activeY).getDomain();
			DisposableIntIterator iter = idom.getIterator();
			int val = 1;
			for(;index >= 0 && iter.hasNext(); index--)
			{
				val = iter.next();
			}
			
			if (actualRole == ActualRole.Contributor && cells[activeX][activeY].valueState == 0)
			{
				networkState = NetworkState.waitingConfirm;
				networkController.SendMessage("instantiate#" + userName + ",-1," + activeX + "," + activeY + "," + val);
			
				positionContributed = new int[2];
				positionContributed[0] = activeX;
				positionContributed[1] = activeY;
				positionContributedList.add(positionContributed);
			}
		}
		else
		{
			if (actualRole == ActualRole.Tester && !conflictExists)
			{
				if (cells[activeX][activeY].valueState == 2 || cells[activeX][activeY].valueState == 3 || cells[activeX][activeY].valueState == 4)
				{
					askForCommiting.setVisible(true);
					removeValue.setVisible(true);
				}
				else
				{
					askForCommiting.setVisible(false);
					removeValue.setVisible(false);
				}
			}
			else if (actualRole == ActualRole.Leader && !conflictExists)
			{
				if (cells[activeX][activeY].valueState == 6)
				{
					acceptValueLeader.setVisible(true);
					removeValueLeader.setVisible(true);
				}
				else
				{
					acceptValueLeader.setVisible(false);
					removeValueLeader.setVisible(false);
				}
			}
		}
		
		repaint();
	}
	
	public void setVotingCell(int x, int y)
	{
		votingX = x;
		votingY = y;
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
	}
	
	public void destroy() {
		networkController.SendMessage("disconnect#" + clientId + "," + clientType + "," + userName);
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
				for(int i = 1; i < vars.length; i++)
				{
					equation = vars[i];
					vars2 = equation.split("=");
					
					switch(vars2[0])
					{
					case "ss":												// sudoku size
						sudokuSize = Integer.parseInt(vars2[1]);
						Initialize();
						break;
					case "ci":												// client id
						networkController.clientId = Integer.parseInt(vars2[1]);
						break;
					case "iv":												// initial values
						addToGridInitialize(vars2[1]);
						break;
					default:
						//System.out.println(vars2[0]);
						break;
					}
				}
				
				StartGame();
				Print("State of the Game Received from the Server");
				
				networkState = NetworkState.idle;
				repaint();
				break;
			case "memberConnected":
				vars2 = vars[1].split(",");
				clientId = Integer.parseInt(vars2[0]);
				clientType = Integer.parseInt(vars2[1]);
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
						isFirstTime = true;
						
						if(actualRole == ActualRole.Committer)
						{
							voteCommiting.setVisible(true);
							voteRemove.setVisible(true);
						}
						else if (actualRole == ActualRole.Tester)
						{
							askForCommiting.setVisible(false);
							removeValue.setVisible(false);
						}
						
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
				conflictExists = false;
				//voteTimer.stop();
				
				vars2 = vars[1].split(",");
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				//ClearCell(conflictX, conflictY);
				Print("The value at the position [" + conflictX + "][" + conflictY + "] has been Removed.");
				
				repaint();
				break;
			case "committed":
				vars2 = vars[1].split(",");
				conflictExists = false;
				//voteTimer.stop();
				
				boolean found = false;
				boolean foundPositively = false;
				boolean foundNegatively = false;
				
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				
				int positionTemp = 0;
				
				SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, 6);
				
				if (actualRole == ActualRole.Contributor)
				{
					for (int i=0;i<positionContributedList.size();i++)
					{						
						if (positionContributedList.get(i)[0] == conflictX && positionContributedList.get(i)[1] == conflictY)
						{
							found = true;
							numContributionsCommited++;
							positionTemp = i;
						}
						
						if (numContributionsCommited >= 5 && !tester)
							getPromotedTester.setVisible(true);
					}
					
					if(found)
					{
						positionContributedList.remove(positionTemp);
						Print("The value you contributed at the position [" + conflictX + "][" + conflictY + "] has been committed!");
					}
				}
				else if (actualRole == ActualRole.Tester)
				{	
					for (int i=0;i<positionCommittedList.size();i++)
					{
						if (positionCommittedList.get(i)[0] == conflictX && positionCommittedList.get(i)[1] == conflictY)
						{
							found = true;
							numCommittedSatisfactory++;
							positionTemp = i;
						}
						
						if (numCommittedSatisfactory >= 5 && !committer)
							getPromotedCommitter.setVisible(true);
					}
					
					if(found)
					{
						positionCommittedList.remove(positionTemp);
						Print("The value you asked for Committing at the position ["+conflictX+"]["+conflictY+"] has been committed!");
					}
				}
				else if (actualRole == ActualRole.Committer)
				{	
					for (int i=0; i<positionVotedPositivelyList.size();i++)
					{
						if (positionVotedPositivelyList.get(i)[0] == conflictX && positionVotedPositivelyList.get(i)[1] == conflictY)
						{
							foundPositively = true;
							numVotedSatisfactory++;
							positionTemp = i;
						}
						
						if (numVotedSatisfactory >= 5 && !leader)
							getPromotedLeader.setVisible(true);
					}
					
					for (int i=0; i<positionVotedNegativelyList.size();i++)
					{
						if (positionVotedNegativelyList.get(i)[0] == conflictX && positionVotedNegativelyList.get(i)[1] == conflictY)
						{
							foundNegatively = true;
							numVotedSatisfactory++;
							positionTemp = i;
						}
						
						if (numVotedSatisfactory >= 5)
							getPromotedLeader.setVisible(true);
					}
					
					if(foundPositively)
					{
						Print("The value you voted for Removing at the position [" + conflictX + "][" + conflictY + "] has been Removed!");
						positionVotedPositivelyList.remove(positionTemp);
					}
					else if (foundNegatively)
					{
						Print("The value you voted for Keeping at the position [" + conflictX + "][" + conflictY + "] has been Committed!");
						positionVotedNegativelyList.remove(positionTemp);
					}
				}

				if (!found || (!foundPositively && !foundNegatively))
					Print("The value at the position [" + conflictX + "][" + conflictY + "] has been committed by Votation.");
								
				repaint();
				break;	
			case "accepted":		
		
				vars2 = vars[1].split(",");
				int x = Integer.parseInt(vars2[0]);
				int y = Integer.parseInt(vars2[1]);
				
				SetValueAndState(x, y, cells[x][y].current, 7);		
				Print("The Project Leader has accepted at the position [" + x + "][" + y + "] the value: " + cells[x][y].current);
				break;
			}

		} catch (Exception e) {
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
			if(state == 2 || state == 3 || state == 4)
				Print("An agent has contributed at the position [" + x + "][" + y + "] with the Value: " + val);
			else if (state == 5)
				Print("You have contributed at the position [" + x + "][" + y + "] with the Value: " + val);
		}
	}
	
	public void addToGridInitialize(String code)
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
		informationL1Label.setVisible(false);
		informationL2Label.setVisible(false);
		informationConnectionLabel.setVisible(false);
		
		connectionSettingsLabel.setVisible(false);
		ipFieldLabel.setVisible(false);
		portFieldLabel.setVisible(false);
		ipFieldDoubtLabel.setVisible(false);
		userNameLabel.setVisible(false);

		governationRulesLabel.setVisible(false);
		questionsToJoinLabel.setVisible(false);
		contributionsToBePromotedLabel.setVisible(false);
		bugsReportedToBePromotedLabel.setVisible(false);
		valuesTestedToBePromotedLabel.setVisible(false);
		correctVotesToBePromotedLabel.setVisible(false);

		connectClientButton.setVisible(false);

		userNameField.setVisible(false);
		portField.setVisible(false);
		ipField.setVisible(false);

		questionsToJoinField.setVisible(false);
		contributionsToBePromotedField.setVisible(false);
		bugsReportedToBePromotedField.setVisible(false);
		valuesTestedToBePromotedField.setVisible(false);
		correctVotesToBePromotedField.setVisible(false);

		colorServerLabel.setVisible(true);
		colorRowAgentLabel.setVisible(true);
		colorColumnAgentLabel.setVisible(true);
		colorSquareAgentLabel.setVisible(true);
		colorLeaderLabel.setVisible(true);
		colorContributedLabel.setVisible(true);
		colorCommittedLabel.setVisible(true);
		colorAcceptedLabel.setVisible(true);
		colorUserContributorLabel.setVisible(true);
		backgroundLabel.setVisible(true);
		foregroundLabel.setVisible(true);

		cellStateInfoLabel.setVisible(true);	
		cellValueLabel.setVisible(true);
		cellStateLabel.setVisible(true);
		
		votingLabel.setVisible(true);
		votingLabelON.setVisible(true);

		gameSettings[numQuestionsSettings] = Integer.parseInt(questionsToJoinField.getText());
		gameSettings[numContributionsSettings] = Integer.parseInt(contributionsToBePromotedField.getText());
		gameSettings[numBugReportedSettings] = Integer.parseInt(bugsReportedToBePromotedField.getText());
		gameSettings[numTestedSettings] = Integer.parseInt(valuesTestedToBePromotedField.getText());
		gameSettings[numVotesSettings] = Integer.parseInt(correctVotesToBePromotedField.getText());

		console.setVisible(true);

		state = GameState.game;
	}
	
	
	public void Print(String message)
	{
		console.add(message);
		console.makeVisible(console.getItemCount()-1);
	}
	
	@Override
	public void CellClick(int x, int y) 
	{
		activeX = x;
		activeY = y;

		repaint();
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
	
	int getFreePositions()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++) {
			for(int j=0;j<sudokuSize;j++) {
				if(cells[i][j].valueState == 0)
					count++;
			}	
		}
		
		return count;
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