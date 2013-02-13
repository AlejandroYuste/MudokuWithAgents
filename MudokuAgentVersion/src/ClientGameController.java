import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.integer.IntDomain;

public class ClientGameController extends GameController implements ActionListener 
{
	private static final long serialVersionUID = 1L;

	public enum NetworkState {idle, waitingInit, waitingConfirm}
	NetworkState networkState;
	
	protected enum ActualRole {PreGame, PassiveUser, Contributor, BugReporter, Tester, Committer, Leader}
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
	Button joinCommunityButton;
	Button yesAnswerButton;
	Button noAnswerButton;
	Button getPromotedContributor;
	
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
	Button getObserver;
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
	
	//Labels Stats of the Game
	Label valuesContributedLabel;
	Label valuesReportedLabel;
	Label valuesTestedLabel;
	Label valuesCommittedLabel;
	Label valuesAcceptedLabel;
	Label valuesRejectedLabel;
	
	//Labels Information Game 
	Label gameInfoRow1Label;
	Label gameInfoRow2Label;
	Label gameInfoRow3Label;
	Label gameInfoRow4Label;
	Label gameInfoRow5Label;
	Label gameInfoRow6Label;
	Label gameInfoRow7Label;
	
	//Other Variables
	String userName;
	
	int[] gameSettings;
	
	int[][] instantiator;
	static ClientNetworkController networkController;
	
	int[] positionContributed;
	ArrayList<int[]> positionContributedList = new ArrayList<int[]>();
	int numContributionsCommited = 0;
	
	int numBugsReported = 0;
	
	int[] positionCommitted;
	ArrayList<int[]> positionCommittedList = new ArrayList<int[]>();
	int numCommittedSatisfactory = 0;
	
	int[] positionVoted;
	ArrayList<int[]> positionVotedPositivelyList = new ArrayList<int[]>();
	ArrayList<int[]> positionVotedNegativelyList = new ArrayList<int[]>();
	int numVotedSatisfactory = 0;
	
	int numLeaderActionsSatisfactory = 0;
		
	boolean observer = false;
	boolean contributor = false;
	boolean reporter = false;
	boolean tester = false;
	boolean committer = false;
	boolean leader = false;
	
	boolean isFirstTime = true;
	
	int conflictX;
	int conflictY;
	
	int voteDelay = 10000;
	
	int clientId;
	int clientType;
	
	int correctAnswers = 0;
	int questionsDone = 1;
	int questionNumber = 0;
	int xAsk, yAsk;
	
	Timer voteTimer;
	
	boolean conflictExists;
	int clearRequester;
	
	//Constants of the Questions
	String[] questionsList;
	
	final int isEmptyPosition = 0;
	final int isCorrect = 1;
	final int isBug = 2;
	final int isByServer = 3;
	final int isContribution = 4;
	final int isReported = 5;
	final int isCommitted = 6;
	final int isNotCommitted = 7;
	final int isAccepted = 8;
	final int isRejected = 9;
	//final int isVotingOn = 10;
	
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
	
	//Constants of the Game Settings
	final int numQuestionsSettings = 0;
	final int numContributionsSettings = 1;
	final int numBugReportedSettings = 2;
	final int numTestedSettings = 3;
	final int numCommittedSettings = 4;
	
	public ClientGameController()
	{
		super();
		clearRequester = -1;
		GameController.sudokuSize = 16;
		networkState = NetworkState.idle;
		actualRole = ActualRole.PreGame;
		clientType = passiveUser;
		gameSettings = new int[5];
		conflictExists = false;
		
		clientColors = new Color[14];
		clientColors[serverColor] = new Color(255, 193, 37);						// 0.  Server 				--> goldenrod 1
		clientColors[rowAgentColor] = new Color(188,	210, 238);					// 1.  Row Agent 			--> lightsteelblue 2
		clientColors[columnAgentColor] = new Color(173, 255, 47);					// 2.  Columns Agent 		--> greenyellow
		clientColors[squareAgentColor] = new Color(255, 236,	139);				// 3.  Square Agent			--> lightgoldenrod 1
		clientColors[userColor] = new Color(238, 180, 180);							// 4.  User 				--> rosybrown 2
		clientColors[agentLeaderColor] = new Color(255, 187, 255);					// 5.  Leader Agent			--> plum 1
		clientColors[userLeaderColor] = new Color(238, 180, 180);					// 6.  User Leader			--> rosybrown 2
		clientColors[valueContributedColor] = new Color(56, 142, 142);				// 7.  Value Contributed	--> sgi teal
		clientColors[valueReportedColor] = new Color(255, 255, 255);				// 8.  Value Reported		--> white
		clientColors[valueCommittedColor] = new Color(220, 20, 60);					// 9.  Value Committed		--> crimson
		clientColors[valueNotCommittedColor] = new Color(255, 255, 255);			// 10. Value NOT Committed	--> white
		clientColors[valueAcceptedColor] = new Color(205, 155, 29);					// 11. Value Accepted 		--> goldenrod 3
		clientColors[valueRejectedColor] = new Color(255, 255, 255);				// 12. Value Rejected		--> white
		clientColors[votingColor] = new Color(220, 20, 60);							// 13. Voting Color			--> crimson
		
		questionsList = new String[10];
		questionsList[isEmptyPosition] = "an empty Position?";
		questionsList[isCorrect] = "a valid value?";
		questionsList[isBug] = "a bug value?";
		questionsList[isByServer] = "a value Initialized by Server?";
		questionsList[isContribution] = "a Contributed value?";
		questionsList[isReported] = "a Bug Reported?";
		questionsList[isCommitted] = "a value Committed?";
		questionsList[isNotCommitted] = "a value Not Committed?";
		questionsList[isAccepted] = "a value Accepted?";
		questionsList[isRejected] = "a value Rejected?";
		//questionsList[isVotingOn] = " - Is any Voting on?";
	}

	public void init()			
	{
		super.init();
		
		userConnected = true;
		
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
		ipFieldDoubtLabel.setLocation(65, 490);
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
		governationRulesLabel = new Label("Game Settings:");
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
		questionsToJoinField.setText("5");
		add(questionsToJoinField);
		
		contributionsToBePromotedLabel = new Label(" - Contributions to be Promoted: ");
		contributionsToBePromotedLabel.setSize(200, 20);
		contributionsToBePromotedLabel.setLocation(450, 420);
		add(contributionsToBePromotedLabel);
		
		contributionsToBePromotedField = new TextField(5);			//Contributions to Be Promoted
		contributionsToBePromotedField.setSize(40, 20);
		contributionsToBePromotedField.setLocation(660, 420);
		contributionsToBePromotedField.setText("4");
		add(contributionsToBePromotedField);
		
		bugsReportedToBePromotedLabel = new Label(" - Bugs Reported to be Promoted: ");
		bugsReportedToBePromotedLabel.setSize(200,20);
		bugsReportedToBePromotedLabel.setLocation(450, 440);
		add(bugsReportedToBePromotedLabel);
		
		bugsReportedToBePromotedField = new TextField(5);			//Bugs Reported to Be Promoted
		bugsReportedToBePromotedField.setSize(40, 20);
		bugsReportedToBePromotedField.setLocation(660, 440);
		bugsReportedToBePromotedField.setText("6");
		add(bugsReportedToBePromotedField);
		
		valuesTestedToBePromotedLabel = new Label(" - Values Tested to be Promoted: ");
		valuesTestedToBePromotedLabel.setSize(200,20);
		valuesTestedToBePromotedLabel.setLocation(450, 460);
		add(valuesTestedToBePromotedLabel);
		
		valuesTestedToBePromotedField = new TextField(5);			//Values Tested to Be Promoted
		valuesTestedToBePromotedField.setSize(40, 20);
		valuesTestedToBePromotedField.setLocation(660, 460);
		valuesTestedToBePromotedField.setText("8");
		add(valuesTestedToBePromotedField);
		
		correctVotesToBePromotedLabel = new Label(" - Votes to be Promoted: ");
		correctVotesToBePromotedLabel.setSize(200,20);
		correctVotesToBePromotedLabel.setLocation(450, 480);
		add(correctVotesToBePromotedLabel);
		
		correctVotesToBePromotedField = new TextField(5);			//Votes to Be Promoted
		correctVotesToBePromotedField.setSize(40, 20);
		correctVotesToBePromotedField.setLocation(660, 480);
		correctVotesToBePromotedField.setText("10");
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
		votingLabel.setLocation(497, 375);
		votingLabel.setVisible(false);
		add(votingLabel);	
		
		votingLabelON = new Label("ON?");
		votingLabelON.setSize(30, 10);
		votingLabelON.setLocation(502, 395);
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
		
		//------------------------------------------------------------------------------------------> Stats of the Game
		
		valuesContributedLabel = new Label("Values Contributed: 0");
		valuesContributedLabel.setSize(50, 10);
		valuesContributedLabel.setLocation(595, 340);
		valuesContributedLabel.setVisible(false);
		add(valuesContributedLabel);	
		
		valuesReportedLabel = new Label("Values Reported: 0");
		valuesReportedLabel.setSize(50, 10);
		valuesReportedLabel.setLocation(595, 340);
		valuesReportedLabel.setVisible(false);
		add(valuesReportedLabel);	
		
		valuesTestedLabel = new Label("Values Tested: 0");
		valuesTestedLabel.setSize(50, 10);
		valuesTestedLabel.setLocation(505, 340);
		valuesTestedLabel.setVisible(false);
		add(valuesTestedLabel);

		valuesCommittedLabel = new Label("Participation in Voting: 0");
		valuesCommittedLabel.setSize(50, 10);
		valuesCommittedLabel.setLocation(505, 340);
		valuesCommittedLabel.setVisible(false);
		add(valuesCommittedLabel);
		
		valuesAcceptedLabel = new Label("Values Accepted: 0");
		valuesAcceptedLabel.setSize(50, 10);
		valuesAcceptedLabel.setLocation(505, 340);
		valuesAcceptedLabel.setVisible(false);
		add(valuesAcceptedLabel);
		
		valuesRejectedLabel = new Label("Values Rejected: 0");
		valuesRejectedLabel.setSize(50, 10);
		valuesRejectedLabel.setLocation(505, 340);
		valuesRejectedLabel.setVisible(false);
		add(valuesRejectedLabel);
		
		//------------------------------------------------------------------------------------------> Information of every Role
		
		gameInfoRow1Label = new Label("");
		gameInfoRow1Label.setVisible(false);
		add(gameInfoRow1Label);
		
		gameInfoRow2Label = new Label("");
		gameInfoRow2Label.setVisible(false);
		add(gameInfoRow2Label);
		
		gameInfoRow3Label = new Label("");
		gameInfoRow3Label.setVisible(false);
		add(gameInfoRow3Label);
		
		gameInfoRow4Label = new Label("");
		gameInfoRow4Label.setVisible(false);
		add(gameInfoRow4Label);
		
		gameInfoRow5Label = new Label("");
		gameInfoRow5Label.setVisible(false);
		add(gameInfoRow5Label);
		
		gameInfoRow6Label = new Label("");
		gameInfoRow6Label.setVisible(false);
		add(gameInfoRow6Label);
		
		gameInfoRow7Label = new Label("");
		gameInfoRow7Label.setVisible(false);
		add(gameInfoRow7Label);
		
		// Components From Here
		
		console = new List();
		console.setLocation(20, 470);
		console.setSize(450, 125);
		console.setVisible(false);
		add(console);
		
		//------------------------------------------------------------------------------------------> Change Roles Buttons
		
		getObserver = new Button("Obser.");
		getObserver.setSize(44,15);
		getObserver.setLocation(480, 65);
		getObserver.setActionCommand("getObserver");
		getObserver.addActionListener(this);				
		getObserver.setVisible(false);
		add(getObserver);
		
		getContributor = new Button("Contri.");
		getContributor.setSize(44,15);
		getContributor.setLocation(524, 65);
		getContributor.setActionCommand("getContributor");
		getContributor.addActionListener(this);				
		getContributor.setVisible(false);
		add(getContributor);
		
		getReporter = new Button("Bug R.");
		getReporter.setSize(44,15);
		getReporter.setLocation(568, 65);
		getReporter.setActionCommand("getReporter");
		getReporter.addActionListener(this);				
		getReporter.setVisible(false);
		add(getReporter);
		
		getTester = new Button("Tester");
		getTester.setSize(44,15);
		getTester.setLocation(613, 65);
		getTester.setActionCommand("getTester");
		getTester.addActionListener(this);				
		getTester.setVisible(false);
		add(getTester);
		
		getCommitter = new Button("Commi.");
		getCommitter.setSize(44,15);
		getCommitter.setLocation(657, 65);
		getCommitter.setActionCommand("getCommitter");
		getCommitter.addActionListener(this);				
		getCommitter.setVisible(false);
		add(getCommitter);
		
		getLeader = new Button("Leader");
		getLeader.setSize(44,15);
		getLeader.setLocation(701, 65);
		getLeader.setActionCommand("getLeader");
		getLeader.addActionListener(this);				
		getLeader.setVisible(false);
		add(getLeader);
		
		//------------------------------------------------------------------------------------------> Observer Role Buttons
		
		joinCommunityButton = new Button("Try to Join!");
		joinCommunityButton.setActionCommand("joinCommunity");
		joinCommunityButton.addActionListener(this);						
		joinCommunityButton.setVisible(false);
		add(joinCommunityButton);
		
		yesAnswerButton = new Button("Yes");
		yesAnswerButton.setActionCommand("answerYes");
		yesAnswerButton.setLocation(540, 205);
		yesAnswerButton.setSize(60, 30);
		yesAnswerButton.addActionListener(this);						
		yesAnswerButton.setVisible(false);
		add(yesAnswerButton);

		noAnswerButton = new Button("No");
		noAnswerButton.setActionCommand("answerNo");
		noAnswerButton.setLocation(620, 205);
		noAnswerButton.setSize(60, 30);
		noAnswerButton.addActionListener(this);						
		noAnswerButton.setVisible(false);
		add(noAnswerButton);
		
		getPromotedContributor = new Button("Join The Community");
		getPromotedContributor.setSize(200,40);
		getPromotedContributor.setLocation(510, 300);
		getPromotedContributor.setActionCommand("getContributor");
		getPromotedContributor.addActionListener(this);					
		getPromotedContributor.setVisible(false);	
		add(getPromotedContributor);
		
		//------------------------------------------------------------------------------------------> Contributor Role Buttons
		
		getPromotedReporter = new Button("Get Bug Reporter!");
		getPromotedReporter.setSize(200,40);
		getPromotedReporter.setLocation(510, 300);
		getPromotedReporter.setActionCommand("getReporter");
		getPromotedReporter.addActionListener(this);					
		getPromotedReporter.setVisible(false);	
		add(getPromotedReporter);
		
		//------------------------------------------------------------------------------------------> Bug Reporter Role Buttons
		
		getPromotedTester = new Button("Get Tester!");
		getPromotedTester.setSize(200,40);
		getPromotedTester.setLocation(510, 310);
		getPromotedTester.setActionCommand("getTester");
		getPromotedTester.addActionListener(this);					
		getPromotedTester.setVisible(false);	
		add(getPromotedTester);
		
		removeValue = new Button("Report the Bug");
		removeValue.setSize(180,30);
		removeValue.setLocation(520, 240);
		removeValue.setActionCommand("removeValue");
		removeValue.addActionListener(this);				
		removeValue.setVisible(false);
		add(removeValue);
		
		//------------------------------------------------------------------------------------------> Tester Role Buttons
		
		getPromotedCommitter = new Button("Get Committer!");
		getPromotedCommitter.setSize(200,40);
		getPromotedCommitter.setLocation(510, 310);
		getPromotedCommitter.setActionCommand("getCommitter");
		getPromotedCommitter.addActionListener(this);				
		getPromotedCommitter.setVisible(false);
		add(getPromotedCommitter);
		
		
		askForCommiting = new Button("Ask for Committing");
		askForCommiting.setSize(180,30);
		askForCommiting.setLocation(520, 240);
		askForCommiting.setActionCommand("askCommitting");
		askForCommiting.addActionListener(this);
		askForCommiting.setVisible(false);
		add(askForCommiting);
		
		//------------------------------------------------------------------------------------------> Committer Role Buttons
		
		getPromotedLeader = new Button("Get Leader!");
		getPromotedLeader.setSize(200,40);
		getPromotedLeader.setLocation(510, 310);
		getPromotedLeader.setActionCommand("getLeader");
		getPromotedLeader.addActionListener(this);				
		getPromotedLeader.setVisible(false);
		add(getPromotedLeader);
		
		voteCommiting = new Button("Commit it!");
		voteCommiting.setSize(80,30);
		voteCommiting.setLocation(520, 240);
		voteCommiting.setActionCommand("no");
		voteCommiting.addActionListener(this);				
		voteCommiting.setVisible(false);
		add(voteCommiting);
		
		voteRemove = new Button("Remove it!");
		voteRemove.setSize(80,30);
		voteRemove.setLocation(620, 240);
		voteRemove.setActionCommand("yes");
		voteRemove.addActionListener(this);					
		voteRemove.setVisible(false);
		add(voteRemove);
		
		//------------------------------------------------------------------------------------------> Leader Role Buttons
		
		acceptValueLeader = new Button("Accept it!");
		acceptValueLeader.setSize(80,30);
		acceptValueLeader.setLocation(520, 240);
		acceptValueLeader.setActionCommand("acceptLeader");
		acceptValueLeader.addActionListener(this);				
		acceptValueLeader.setVisible(false);
		add(acceptValueLeader);
		
		removeValueLeader = new Button("Remove it!");
		removeValueLeader.setSize(80,30);
		removeValueLeader.setLocation(620, 240);
		removeValueLeader.setActionCommand("removeLeader");
		removeValueLeader.addActionListener(this);				
		removeValueLeader.setVisible(false);
		add(removeValueLeader);
		

		networkController = new ClientNetworkController(this);
	}

	@Override
	public void paint ( Graphics gr )
	{
		initDraw(gr);							//Clean the Frame.
		switch(actualRole)
		{
		case PreGame:
			DrawPreGame(gr);
			break;
		case Contributor:
			DrawNumbersToInstantiate(gr);
			DrawGrid(gr);
			break;
		default:
			if (state == GameState.game) {
				DrawGrid(gr);
			}
			break;
		}
	}
	
	public void DrawPreGame(Graphics gr) {
		gr.setColor(Color.black);
		
		Stroke stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(10, 350, 370, 350);			// Connection Settings Box
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
		
		stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(20, 460, 470, 460);				//Separator Grid Horizintal
		gr.drawLine(470, 10, 470, 460);				//Separator Grid Vertical-Right
		
		gr.drawLine(480, 10, 480, 60);				//Box Current Role
		gr.drawLine(480, 10, 745, 10);			
		gr.drawLine(745, 10, 745, 60);			
		gr.drawLine(480, 60, 745, 60);
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(480, 450, 480, 595);			//Box Colors Legend
		gr.drawLine(480, 450, 745, 450);			
		gr.drawLine(745, 450, 745, 595);			
		gr.drawLine(480, 595, 745, 595);
		
		gr.drawLine(480, 85, 480, 135);				//Box Information of the Role
		gr.drawLine(480, 85, 745, 85);			
		gr.drawLine(745, 85, 745, 135);			
		gr.drawLine(480, 135, 745, 135);
		
		gr.drawLine(480, 145, 480, 360);			//Box Activities of the Role
		gr.drawLine(480, 145, 745, 145);			
		gr.drawLine(745, 145, 745, 360);			
		gr.drawLine(480, 360, 745, 360);
		
		gr.drawLine(480, 440, 480, 370);			//Box Voting On
		gr.drawLine(480, 440, 550, 440);			
		gr.drawLine(550, 440, 550, 370);			
		gr.drawLine(480, 370, 550, 370);
		
				
		if (conflictExists)
			gr.setColor(clientColors[votingColor]);	
		else
			gr.setColor(Color.white);
		
		gr.fillRect(497, 415, 35, 15);
		gr.setColor(Color.black);
		gr.drawRect(497, 415, 35, 15);
		
		gr.drawLine(560, 440, 560, 370);				//Box Cell State
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
			setActive(conflictX, conflictY);
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
						cells[activeX][activeY].DrawDomainConflict(gr, conX, conY, conflictX, conflictY, clientColors);
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
			cells[activeX][activeY].DrawDomain(gr, activeX, activeY);		
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

	public void DrawNumbersToInstantiate(Graphics gr) 
	{
		int numberX = numberInstantiateX;
		int numberY = numberInstantiateY;
		int numerToPrint = 1;
		
		float tempDrawX = numberX;
		int drawX = (int) tempDrawX;
		
		Stroke stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		gr.setColor(Color.black);
		
		for(int i = 0; i<2;i++)
		{
			for(int j = 0; j<GameController.sudokuSize/2;j++)
			{
				gr.drawRect(drawX, numberY, (int)deltaX, (int)deltaY);
				
				if (numerToPrint < 10) {
					gr.drawString(String.valueOf(numerToPrint), (int)(drawX + deltaX / 2) - 2, (int)(numberY + deltaY) - 10);
				} else {
					gr.drawString(String.valueOf(numerToPrint), (int)(drawX + deltaX / 2) - 5, (int)(numberY + deltaY) - 10);
				}
				tempDrawX += deltaX;
				drawX = (int) tempDrawX;
				numerToPrint++;
			}
			
			numberY += deltaY;
			tempDrawX = numberX;
			drawX = (int) tempDrawX;
		}
		
		if(mouseOverDomainIndex != -1) 
		{
			System.out.println("mouseOverDomainIndex: " + mouseOverDomainIndex);
			
			gr.setColor(ClientGameController.mouseOverColor);
			if(mouseOverDomainIndex < 8) {
				gr.drawRect((int)(numberInstantiateX + mouseOverDomainIndex * deltaX), numberInstantiateY, (int) deltaX, (int) deltaY);
			} else {
				gr.drawRect((int)(numberInstantiateX + (mouseOverDomainIndex - 8) * deltaX), (int) (numberInstantiateY + deltaY), (int) deltaX, (int) deltaY);
			}
		}
	}
	
	public void setActive(int x, int y) {
		activeX = x;
		activeY = y;
		
		switch(actualRole)
		{
			case Leader:
				if (cells[activeX][activeY].valueState == committedByTesterByRows || cells[activeX][activeY].valueState == committedByTesterByColumns ||
					cells[activeX][activeY].valueState == committedByTesterBySquares || cells[activeX][activeY].valueState == committedByTesterByUser) 
				{
					acceptValueLeader.setVisible(true);
					removeValueLeader.setVisible(true);
				}
				else {
					acceptValueLeader.setVisible(false);
					removeValueLeader.setVisible(false);
				}
				
				break;
			default:
				break;
		}
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
					} catch (IOException e) {
						System.out.println("Can not connect to server");
					}  
				} else {
					System.out.println("Check ip & port");
				}
				break;
			case "voteEnd":
				conflictExists = false;
				
				switch (actualRole)
				{
					case Tester:
						askForCommiting.setVisible(true);
						break;
					case Committer:
						voteRemove.setVisible(false);
						voteCommiting.setVisible(false);
						break;
					default:
						break;
				}

				voteTimer.stop();
				break;
			case "yes":
				if(conflictExists)
				{
					networkController.SendMessage("voted#" + clientId + "," + clientType + "," + conflictX + "," + conflictY + "," + "1" + "," + userName);
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
					networkController.SendMessage("voted#" + clientId + "," + clientType + "," + conflictX + "," + conflictY + "," + "-1" + "," + userName);
					voteRemove.setVisible(false);
					voteCommiting.setVisible(false);
					
					positionVoted = new int[2];
					positionVoted[0] = conflictX;
					positionVoted[1] = conflictY;
					positionVotedNegativelyList.add(positionVoted);
				}
				break;
			case "askCommitting":
				if ((cells[activeX][activeY].valueState == contributedByRows || cells[activeX][activeY].valueState == contributedByColumns || 
					 cells[activeX][activeY].valueState == contributedBySquares || cells[activeX][activeY].valueState == contributedByUser) && !conflictExists)
				{
					networkController.SendMessage("clear#" + clientId + "," + clientType + "," + activeX + "," + activeY + "," + userName);
					
					positionCommitted = new int[2];
					positionCommitted[0] = activeX;
					positionCommitted[1] = activeY;
					positionCommittedList.add(positionCommitted);
				}
				else {
					if (conflictExists) {
						Print("You can not ask for Committing during a Voting Session.");
					} else {
						Print("You can not ask for Committing this value.");
					}
				}
				
				break;
			case "removeValue":
				if (cells[activeX][activeY].valueState == contributedByRows || cells[activeX][activeY].valueState == contributedByColumns ||
					cells[activeX][activeY].valueState == contributedBySquares || cells[activeX][activeY].valueState == contributedByUser) 
				{
					networkController.SendMessage("bugReported#" + clientId + "," + clientType + "," + activeX + "," + activeY + "," + userName);
										
					if (!checkCorrectPosition(activeX, activeY, cells[activeX][activeY].current)) 
					{
						numBugsReported++;	
						gameInfoRow6Label.setText("You have Reported " + numBugsReported + " Correct Bugs");
						
						if (numBugsReported >= gameSettings[numBugReportedSettings] && !tester) {
							getPromotedTester.setVisible(true);
						}
					}
					else
					{
						numBugsReported--;
						
						if (numBugsReported < -1) {
							reporter = false;
							tester = false;
							committer = false;
							leader = false;
							Print("You have been discharged from Bug Reporter for your bad Results.");
							numBugsReported = 0;
							getContributor();
						}
					}
				}
				else {
					Print("You can not Report this value.");
				}
				
				break;
			case "acceptLeader":
				networkController.SendMessage("accepted#" + clientId + "," + clientType + "," + activeX + "," + activeY + "," + userName);
				
				if(checkCorrectPosition(activeX, activeY, cells[activeX][activeY].current)) {
					numLeaderActionsSatisfactory++;
				} else {
					numLeaderActionsSatisfactory--;
					
					if (numLeaderActionsSatisfactory < -1) {
						leader = false;
						numLeaderActionsSatisfactory = 0;
						Print("You have been discharged from Leader for your bad Results.");
						getCommitter();
					}
				}
				
				acceptValueLeader.setVisible(false);
				removeValueLeader.setVisible(false);
				break;
			case "removeLeader":
				networkController.SendMessage("rejected#" + clientId + "," + clientType + "," + activeX + "," + activeY + "," + userName);
				
				if(!checkCorrectPosition(activeX, activeY, cells[activeX][activeY].current)) {
					numLeaderActionsSatisfactory++;
				} else {
					numLeaderActionsSatisfactory--;
					
					if (numLeaderActionsSatisfactory < -1) {
						leader = false;
						numLeaderActionsSatisfactory = 0;
						Print("You have been discharged from Leader for your bad Results.");
						getCommitter();
					}
				}
				
				acceptValueLeader.setVisible(false);
				removeValueLeader.setVisible(false);
				break;
			case "joinCommunity":
				askQuestions();
				break;
			case "answerYes":
				answerYes();
				break;
			case "answerNo":
				answerNo();
				break;
			case "getObserver":
				getObserver();
				break;
			case "getContributor":
				getContributor();
				break;
			case "getReporter":
				getReporter();
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
		}							
	}
	
	public void getObserver()
	{
		networkController.SendMessage("getPromotion#" + clientId + "," + clientType + "," + passiveUser + ","  + userName);
		clientType = passiveUser;
		observer = true;
		
		cleanLabels();
	
		if(contributor)
			getContributor.setVisible(true);
		if(reporter)
			getReporter.setVisible(true);
		if(tester)
			getTester.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		actualRole = ActualRole.PassiveUser;
		actualRoleLabel.setLocation(525, 20);
		actualRoleLabel.setText("Current Role: Passive User");
		actualRoleLabel.setVisible(true);
		
		gameInfoRow1Label.setText("In this Role you can check what the others");
		gameInfoRow1Label.setSize(240, 20);
		gameInfoRow1Label.setLocation(497, 90);
		gameInfoRow1Label.setVisible(true);
		
		gameInfoRow2Label.setText("members of the community are doing.");
		gameInfoRow2Label.setLocation(506, 110);
		gameInfoRow2Label.setSize(230, 20);
		gameInfoRow2Label.setVisible(true);
		
		gameInfoRow3Label.setText("Would you like to join the Community?");
		gameInfoRow3Label.setLocation(510, 160);
		gameInfoRow3Label.setSize(230, 20);
		gameInfoRow3Label.setVisible(true);

		joinCommunityButton.setSize(180,40);
		joinCommunityButton.setLocation(525, 200);
		joinCommunityButton.setVisible(true);
	}
	
	public void askQuestions()
	{	
		joinCommunityButton.setVisible(false);
		
		if (correctAnswers >= gameSettings[numQuestionsSettings] && !contributor) {
			getPromotedContributor.setVisible(true);
		}
		
		gameInfoRow1Label.setText("You need " + gameSettings[numQuestionsSettings] + " points to join the Community!");
		gameInfoRow1Label.setSize(240, 20);
		gameInfoRow1Label.setLocation(500, 90);
		gameInfoRow1Label.setVisible(true);
		
		gameInfoRow2Label.setText(" - Current Score: " + correctAnswers);
		gameInfoRow2Label.setLocation(560, 110);
		gameInfoRow2Label.setSize(140, 20);
		gameInfoRow2Label.setVisible(true);
		
		Random random = new Random(System.nanoTime());
		questionNumber = random.nextInt(10);
		
		xAsk = random.nextInt(sudokuSize);
		yAsk = random.nextInt(sudokuSize);
		
		gameInfoRow3Label.setText("Question " + questionsDone + ":");
		gameInfoRow3Label.setLocation(510, 160);
		gameInfoRow3Label.setSize(200, 20);
		gameInfoRow3Label.setVisible(true);
		
		setActive(xAsk, yAsk);
		
		gameInfoRow4Label.setText(" - Is the cell [" + xAsk + "][" + yAsk + "] " + questionsList[questionNumber]);
		gameInfoRow4Label.setLocation(485, 180);
		gameInfoRow4Label.setSize(255, 20);
		gameInfoRow4Label.setVisible(true);
		
		informationQuestion(questionNumber);
		
		yesAnswerButton.setVisible(true);
		noAnswerButton.setVisible(true);

		questionsDone++;
		repaint();
	}
	
	public void getContributor() 
	{	
		networkController.SendMessage("getPromotion#" + clientId + "," + clientType + "," + userContributor + ","  + userName);
		clientType = userContributor;
		contributor = true;
		
		cleanLabels();
		
		if(observer)
			getObserver.setVisible(true);
		
		if(reporter)
			getReporter.setVisible(true);
		if(tester)
			getTester.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		actualRole = ActualRole.Contributor;
		actualRoleLabel.setLocation(530, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		actualRoleLabel.setVisible(true);
				
		if (numContributionsCommited >= gameSettings[numContributionsSettings] && !reporter) {
			getPromotedReporter.setVisible(true);
		}
		
		gameInfoRow1Label.setText("Contribute with " + gameSettings[numContributionsSettings]  + " correct values");
		gameInfoRow1Label.setSize(200, 20);
		gameInfoRow1Label.setLocation(525, 90);
		gameInfoRow1Label.setVisible(true);
		
		gameInfoRow2Label.setText("to get promoted as a Bug Reporter.");
		gameInfoRow2Label.setLocation(515, 110);
		gameInfoRow2Label.setSize(230, 20);
		gameInfoRow2Label.setVisible(true);
		
		gameInfoRow3Label.setText("Select a Cell and then a number!");
		gameInfoRow3Label.setLocation(520, 160);
		gameInfoRow3Label.setSize(200, 20);
		gameInfoRow3Label.setVisible(true);
		
		gameInfoRow6Label.setText("To be promoted the values must be committed");
		gameInfoRow6Label.setLocation(483, 180);
		gameInfoRow6Label.setSize(260, 20);
		gameInfoRow6Label.setVisible(true);
		
		gameInfoRow7Label.setText("Number of contributions committed: " + numContributionsCommited);
		gameInfoRow7Label.setLocation(505, 270);
		gameInfoRow7Label.setSize(225, 20);
		gameInfoRow7Label.setVisible(true);
	}
	
	public void getReporter() {
		networkController.SendMessage("getPromotion#" + clientId + "," + clientType + "," + userBugReporter + ","  + userName);
		clientType = userBugReporter;
		reporter = true;

		cleanLabels();
		
		if(observer)
			getObserver.setVisible(true);
		if(contributor)
			getContributor.setVisible(true);
		
		if(tester)
			getTester.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		actualRole = ActualRole.BugReporter;
		actualRoleLabel.setLocation(525, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		actualRoleLabel.setVisible(true);
		
		if (numBugsReported >= gameSettings[numBugReportedSettings] && !tester) {
			getPromotedTester.setVisible(true);
		}
		
		gameInfoRow1Label.setText("Report " + gameSettings[numBugReportedSettings]  + " Bugs");
		gameInfoRow1Label.setSize(150, 20);
		gameInfoRow1Label.setLocation(565, 90);
		gameInfoRow1Label.setVisible(true);
		
		gameInfoRow2Label.setText("to get promoted as a Tester.");
		gameInfoRow2Label.setLocation(535, 110);
		gameInfoRow2Label.setSize(180, 20);
		gameInfoRow2Label.setVisible(true);
		
		gameInfoRow3Label.setText("Select a value you think is a bug");
		gameInfoRow3Label.setLocation(520, 160);
		gameInfoRow3Label.setSize(200, 20);
		gameInfoRow3Label.setVisible(true);
		
		gameInfoRow4Label.setText("and push the button to remove it!");
		gameInfoRow4Label.setLocation(520, 180);
		gameInfoRow4Label.setSize(200, 20);
		gameInfoRow4Label.setVisible(true);
		
		gameInfoRow5Label.setText("(You only can report Contributed Values)");
		gameInfoRow5Label.setLocation(500, 200);
		gameInfoRow5Label.setSize(230, 20);
		gameInfoRow5Label.setVisible(true);
	
		removeValue.setVisible(true);
		
		gameInfoRow6Label.setText("You have Reported " + numBugsReported + " Correct Bugs");
		gameInfoRow6Label.setLocation(510, 280);
		gameInfoRow6Label.setSize(230, 20);
		gameInfoRow6Label.setVisible(true);
	}
	
	public void getTester()
	{
		networkController.SendMessage("getPromotion#" + clientId + "," + clientType + "," + userTester + ","  + userName);
		clientType = userTester;
		tester = true;

		cleanLabels();
		
		if(observer)
			getObserver.setVisible(true);
		if(contributor)
			getContributor.setVisible(true);
		if(reporter)
			getReporter.setVisible(true);
		
		if(committer)
			getCommitter.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		actualRole = ActualRole.Tester;
		actualRoleLabel.setLocation(545, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		actualRoleLabel.setVisible(true);
		
		if (numCommittedSatisfactory >= gameSettings[numTestedSettings] && !committer) {
			getPromotedCommitter.setVisible(true);
		}
		
		gameInfoRow1Label.setText("Test " + gameSettings[numTestedSettings]  + " values and ask for Committing");
		gameInfoRow1Label.setSize(225, 20);
		gameInfoRow1Label.setLocation(505, 90);
		gameInfoRow1Label.setVisible(true);
		
		gameInfoRow2Label.setText("to get promoted as a Committer.");
		gameInfoRow2Label.setLocation(520, 110);
		gameInfoRow2Label.setSize(180, 20);
		gameInfoRow2Label.setVisible(true);
		
		gameInfoRow3Label.setText("Select a value you think is a correct and push");
		gameInfoRow3Label.setLocation(490, 160);
		gameInfoRow3Label.setSize(250, 20);
		gameInfoRow3Label.setVisible(true);
		
		gameInfoRow4Label.setText("the button to start a Voting Session!");
		gameInfoRow4Label.setLocation(520, 180);
		gameInfoRow4Label.setSize(200, 20);
		gameInfoRow4Label.setVisible(true);
		
		gameInfoRow5Label.setText("(If there is not another voting session active)");
		gameInfoRow5Label.setLocation(490, 205);
		gameInfoRow5Label.setSize(250, 20);
		gameInfoRow5Label.setVisible(true);
				
		gameInfoRow6Label.setText("Number of values committed: " + numCommittedSatisfactory);
		gameInfoRow6Label.setLocation(522, 280);
		gameInfoRow6Label.setSize(200, 20);
		gameInfoRow6Label.setVisible(true);
		
		repaint();
	}

	public void getCommitter()
	{
		networkController.SendMessage("getPromotion#" + clientId + "," + clientType + "," + userCommitter + ","  + userName);
		clientType = userCommitter;
		committer = true;

		cleanLabels();
		
		if(observer)
			getObserver.setVisible(true);
		if(contributor)
			getContributor.setVisible(true);
		if(reporter)
			getReporter.setVisible(true);
		if(tester)
			getTester.setVisible(true);
		
		if(leader)
			getLeader.setVisible(true);
		
		actualRole = ActualRole.Committer;
		actualRoleLabel.setLocation(540, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		actualRoleLabel.setVisible(true);
		
		if (numVotedSatisfactory >= gameSettings[numCommittedSettings] && !leader) {
			getPromotedLeader.setVisible(true);
		}
		
		gameInfoRow1Label.setText("Vote " + gameSettings[numCommittedSettings]  + " times correctly");
		gameInfoRow1Label.setSize(150, 20);
		gameInfoRow1Label.setLocation(550, 90);
		gameInfoRow1Label.setVisible(true);
		
		gameInfoRow2Label.setText("to get promoted as a Project Leader.");
		gameInfoRow2Label.setLocation(510, 110);
		gameInfoRow2Label.setSize(225, 20);
		gameInfoRow2Label.setVisible(true);
		
		gameInfoRow3Label.setText("You are now a Committer. When there is a");
		gameInfoRow3Label.setLocation(495, 160);
		gameInfoRow3Label.setSize(240, 20);
		gameInfoRow3Label.setVisible(true);
		
		gameInfoRow4Label.setText("voting you can vote for committing values.");
		gameInfoRow4Label.setLocation(500, 180);
		gameInfoRow4Label.setSize(240, 20);
		gameInfoRow4Label.setVisible(true);
		
		if (conflictExists) {
			gameInfoRow5Label.setText("Voting at the Position [" + conflictX + "][" + conflictY + "] for the value " + cells[conflictX][conflictY].current);
			gameInfoRow5Label.setLocation(495, 205);
			gameInfoRow5Label.setSize(250, 20);
			gameInfoRow5Label.setVisible(true);
			
			voteCommiting.setVisible(true);
			voteRemove.setVisible(true);
		} else  {
			gameInfoRow5Label.setText("There is not a voting at the moment.");
			gameInfoRow5Label.setLocation(510, 205);
			gameInfoRow5Label.setSize(225, 20);
			gameInfoRow5Label.setVisible(true);
		}
		
		gameInfoRow6Label.setText("Number of votes corrects: " + numVotedSatisfactory);
		gameInfoRow6Label.setLocation(530, 280);
		gameInfoRow6Label.setSize(200, 20);
		gameInfoRow6Label.setVisible(true);
		
		repaint();
	}

	public void getLeader()
	{
		networkController.SendMessage("getPromotion#" + clientId + "," + clientType + "," + userLeader + ","  + userName);
		clientType = userLeader;
		leader = true;

		cleanLabels();
		
		if(observer)
			getObserver.setVisible(true);
		if(contributor)
			getContributor.setVisible(true);
		if(reporter)
			getReporter.setVisible(true);
		if(tester)
			getTester.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		
		actualRole = ActualRole.Leader;
		actualRoleLabel.setLocation(540, 20);
		actualRoleLabel.setText("Current Role: " + actualRole);
		actualRoleLabel.setVisible(true);
		
		gameInfoRow1Label.setText("You are a Project Leader");
		gameInfoRow1Label.setSize(150, 20);
		gameInfoRow1Label.setLocation(540, 100);
		gameInfoRow1Label.setVisible(true);
		
		gameInfoRow3Label.setText("You can decide which values are accepted");
		gameInfoRow3Label.setLocation(495, 160);
		gameInfoRow3Label.setSize(240, 20);
		gameInfoRow3Label.setVisible(true);
		
		gameInfoRow4Label.setText("or remove from the grid. Be Carefull!");
		gameInfoRow4Label.setLocation(510, 180);
		gameInfoRow4Label.setSize(220, 20);
		gameInfoRow4Label.setVisible(true);
		
		gameInfoRow5Label.setText("Select a Committed Value and decide.");
		gameInfoRow5Label.setLocation(505, 205);
		gameInfoRow5Label.setSize(220, 20);
		gameInfoRow5Label.setVisible(true);
		
		if (cells[activeX][activeY].valueState == committedByTesterByRows || cells[activeX][activeY].valueState == committedByTesterByColumns ||
			cells[activeX][activeY].valueState == committedByTesterBySquares || cells[activeX][activeY].valueState == committedByTesterByUser) 
		{
			acceptValueLeader.setVisible(true);
			removeValueLeader.setVisible(true);
		}
		else {
			acceptValueLeader.setVisible(false);
			removeValueLeader.setVisible(false);
		}
		
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
		if(index != -1) {				// If The click was out of the grid
			IntDomain idom = cpController.GetCPVariable(activeX, activeY).getDomain();
			DisposableIntIterator iter = idom.getIterator();
			int val = 1;
			
			for(;index >= 0 && iter.hasNext(); index--) {
				val = iter.next();
			}
			
			if (actualRole == ActualRole.Contributor)
			{
				networkState = NetworkState.waitingConfirm;
				networkController.SendMessage("instantiate#" + clientId + "," + clientType + "," + activeX + "," + activeY + "," + val + "," + userName);
			
				positionContributed = new int[2];
				positionContributed[0] = activeX;
				positionContributed[1] = activeY;
				positionContributedList.add(positionContributed);
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
			
			int x, y;
			
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
				Print("Game received from the Server");
				
				networkState = NetworkState.idle;
				repaint();
				break;
			case "memberConnected":
				vars2 = vars[1].split(",");
				clientId = Integer.parseInt(vars2[0]);
				clientType = Integer.parseInt(vars2[1]);
				Print("You have been connected to the server.");
				getObserver();
				break;
			case "instantiated":
				addToGrid(vars[1]);

				networkState = NetworkState.idle;
				CellClick(activeX, activeY);
				break;
			case "instantiate_failed":
					networkState = NetworkState.idle;
					Print("Instantiation Failed!");
				break;
			case "vote":
				vars2 = vars[1].split("=");
				String[] coordinates;
				
				switch(vars2[0])
				{
					case "clear":
						conflictExists = true;
						isFirstTime = true;
						
						coordinates = vars2[1].split(",");
						conflictX = Integer.parseInt(coordinates[0]);
						conflictY = Integer.parseInt(coordinates[1]);
						clearRequester = Integer.parseInt(coordinates[2]);
												
						Print("A voting session has started for the position " + conflictX + "][" + conflictY + "]");
						
						switch (actualRole)
						{
							case Tester:
								askForCommiting.setVisible(false);
								break;
							case Committer:
								gameInfoRow5Label.setText("Voting at the Position [" + conflictX + "][" + conflictY + "] for the value " + cells[conflictX][conflictY].current);
								gameInfoRow5Label.setLocation(495, 205);
								gameInfoRow5Label.setSize(250, 20);
								gameInfoRow5Label.setVisible(true);
								
								voteCommiting.setVisible(true);
								voteRemove.setVisible(true);
								break;
							default:
								break;
						}
						
						repaint();
						
						voteTimer = new Timer(voteDelay, this);
						voteTimer.setActionCommand("voteEnd");
						voteTimer.start();
						break;
					}
				break;
			case "bugFound":
				vars2 = vars[1].split(",");
				int xClear = Integer.parseInt(vars2[0]);
				int yClear = Integer.parseInt(vars2[1]);
				int bugState = Integer.parseInt(vars2[2]);
				
				Print("Bug Found at the position " + xClear + "][" + yClear + "]");
				
				ClearCell(xClear, yClear, bugState);
				repaint();
				break;
			case "committed":
				vars2 = vars[1].split(",");
				conflictExists = false;
				
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				
				int committedState = Integer.parseInt(vars2[2]);
				
				SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, committedState);
				
				switch (actualRole)
				{
					case Contributor:
						for (int i=0;i<positionContributedList.size();i++) {						
							if (positionContributedList.get(i)[0] == conflictX && positionContributedList.get(i)[1] == conflictY) {
								positionContributedList.remove(i);
								numContributionsCommited++;
								Print("The value you contributed at the position [" + conflictX + "][" + conflictY + "] has been committed!");
								break;
							}
						}
						
						gameInfoRow7Label.setText("Number of contributions committed: " + numContributionsCommited);
						gameInfoRow7Label.setLocation(505, 270);
						gameInfoRow7Label.setSize(225, 20);
						gameInfoRow7Label.setVisible(true);
						
						if (numContributionsCommited >= gameSettings[numContributionsSettings] && !reporter) {
							getPromotedReporter.setVisible(true);
						}
						
					break;
					case Tester:
						askForCommiting.setVisible(true);
						
						for (int i=0;i<positionCommittedList.size();i++) {
							if (positionCommittedList.get(i)[0] == conflictX && positionCommittedList.get(i)[1] == conflictY) {
								positionCommittedList.remove(i);
								Print("The value you asked for Committing at the position ["+conflictX+"]["+conflictY+"] has been committed!");
								numCommittedSatisfactory++;
								break;
							}
						}
						
						if (numCommittedSatisfactory >= gameSettings[numTestedSettings] && !committer) {
							getPromotedCommitter.setVisible(true);
						}
						
						gameInfoRow6Label.setText("Number of values committed: " + numCommittedSatisfactory);
						gameInfoRow6Label.setLocation(522, 280);
						gameInfoRow6Label.setSize(200, 20);
						gameInfoRow6Label.setVisible(true);
						
						break;
					case Committer:
						boolean found = false;
						
						voteCommiting.setVisible(false);
						voteRemove.setVisible(false);
						
						for (int i=0; i<positionVotedPositivelyList.size();i++) {
							if (positionVotedPositivelyList.get(i)[0] == conflictX && positionVotedPositivelyList.get(i)[1] == conflictY) {
								Print("The value you voted for Removing at the position [" + conflictX + "][" + conflictY + "] has been Removed! :)");
								positionVotedPositivelyList.remove(i);
								numVotedSatisfactory++;
								found = true;
								break;
							}
						}
						
						for (int i=0; i<positionVotedNegativelyList.size();i++) {
							if (positionVotedNegativelyList.get(i)[0] == conflictX && positionVotedNegativelyList.get(i)[1] == conflictY) {
								Print("The value you voted for Keeping at the position [" + conflictX + "][" + conflictY + "] has been Committed! :)");
								positionVotedNegativelyList.remove(i);
								numVotedSatisfactory++;
								found = true;
								break;
							}
						}
						
						gameInfoRow6Label.setText("Number of votes corrects: " + numVotedSatisfactory);
						
						if (numVotedSatisfactory >= gameSettings[numCommittedSettings] && !leader) {
							getPromotedLeader.setVisible(true);
						}
						
						if (!found) {
							Print("The value at the position [" + conflictX + "][" + conflictY + "] has been committed by Votation.");
						}	

					break;
					default:
						break;
				}
				
				repaint();
				break;	
			case "notCommitted":
				vars2 = vars[1].split(",");
				conflictExists = false;
				
				conflictX = Integer.parseInt(vars2[0]);
				conflictY = Integer.parseInt(vars2[1]);
				
				committedState = Integer.parseInt(vars2[2]);
				
				SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, committedState);
				
				switch (actualRole)
				{
					case Contributor:
						for (int i=0;i<positionContributedList.size();i++) {						
							if (positionContributedList.get(i)[0] == conflictX && positionContributedList.get(i)[1] == conflictY) {
								positionContributedList.remove(i);
								numContributionsCommited--;
								Print("The value you contributed at the position [" + conflictX + "][" + conflictY + "] has not been Committed. :(");
								break;
							}
						}
						
						gameInfoRow7Label.setText("Number of contributions committed: " + numContributionsCommited);
						gameInfoRow7Label.setLocation(505, 270);
						gameInfoRow7Label.setSize(225, 20);
						gameInfoRow7Label.setVisible(true);	
	
						if (numContributionsCommited < -1) {
							contributor = false;
							reporter = false;
							tester = false;
							committer = false;
							leader = false;
							Print("You have been discharged from Contributor for your bad Results.");
							numContributionsCommited = 0;
							getObserver();
						}
						
					break;
					case Tester:
						askForCommiting.setVisible(true);
						
						for (int i=0;i<positionCommittedList.size();i++) {
							if (positionCommittedList.get(i)[0] == conflictX && positionCommittedList.get(i)[1] == conflictY) {
								positionCommittedList.remove(i);
								Print("The value you asked for Committing at the position ["+conflictX+"]["+conflictY+"] has not been Committed. :(");
								numCommittedSatisfactory--;
								break;
							}
						}
						
						gameInfoRow6Label.setText("Number of values committed: " + numCommittedSatisfactory);
						gameInfoRow6Label.setLocation(522, 280);
						gameInfoRow6Label.setSize(200, 20);
						gameInfoRow6Label.setVisible(true);
						
						if (numCommittedSatisfactory < -1) {
							tester = false;
							committer = false;
							leader = false;
							numCommittedSatisfactory = 0;
							Print("You have been discharged from Tester for your bad Results.");
							getReporter();
						}
						
						break;
					case Committer:
						boolean foundPositively = false;
						boolean foundNegatively = false;
						
						voteCommiting.setVisible(false);
						voteRemove.setVisible(false);
						
						for (int i=0; i<positionVotedPositivelyList.size();i++) {
							if (positionVotedPositivelyList.get(i)[0] == conflictX && positionVotedPositivelyList.get(i)[1] == conflictY) {
								Print("The value you voted for Removing at the position [" + conflictX + "][" + conflictY + "] has not been Removed. :(");
								positionVotedPositivelyList.remove(i);
								numVotedSatisfactory--;
								foundPositively = true;
								break;
							}
						}
						
						for (int i=0; i<positionVotedNegativelyList.size();i++) {
							if (positionVotedNegativelyList.get(i)[0] == conflictX && positionVotedNegativelyList.get(i)[1] == conflictY) {
								Print("The value you voted for Keeping at the position [" + conflictX + "][" + conflictY + "] has not been Committed. :(");
								positionVotedNegativelyList.remove(i);
								numVotedSatisfactory--;
								foundNegatively = true;
								break;
							}
						}
						
						gameInfoRow6Label.setText("Number of votes corrects: " + numVotedSatisfactory);
						
						if (!foundPositively && !foundNegatively) {
							Print("The value at the position [" + conflictX + "][" + conflictY + "] has not been committed by Votation.");
						}
						
						if (numVotedSatisfactory < -1) {
							committer = false;
							leader = false;
							numVotedSatisfactory = 0;
							Print("You have been discharged from Committer for your bad Results.");
							getTester();
						}
			
					break;
					default:
						break;
				}
				
				repaint();
				break;	
			case "accepted":		
				vars2 = vars[1].split(",");
				x = Integer.parseInt(vars2[0]);
				y = Integer.parseInt(vars2[1]);
				int acceptedState = Integer.parseInt(vars2[2]);
									
				Print("A Project Leader has accepted at the position [" + x + "][" + y + "] the value: " + cells[x][y].current);
				
				SetValueAndState(x, y, cells[x][y].current, acceptedState);				
				break;
			case "rejected":		
				vars2 = vars[1].split(",");
				x = Integer.parseInt(vars2[0]);
				y = Integer.parseInt(vars2[1]);
				int rejectedState = Integer.parseInt(vars2[2]);
				
				Print("A Project Leader has rejected at the position [" + x + "][" + y + "] the value: " + cells[x][y].current);
				
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
			
			Print("New Contribution at the Position [" + x + "][" + y + "] with the value: " + val);
			
			SetValueAndState(x, y, val, state);
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
		gameSettings[numCommittedSettings] = Integer.parseInt(correctVotesToBePromotedField.getText());

		console.setVisible(true);

		state = GameState.game;
	}
	
	public void cleanLabels() 
	{	
		gameInfoRow1Label.setVisible(false);
		gameInfoRow2Label.setVisible(false);
		gameInfoRow3Label.setVisible(false);
		gameInfoRow4Label.setVisible(false);
		gameInfoRow5Label.setVisible(false);
		gameInfoRow6Label.setVisible(false);
		gameInfoRow7Label.setVisible(false);
		
		getPromotedContributor.setVisible(false);
		getPromotedReporter.setVisible(false);
		getPromotedTester.setVisible(false);
		getPromotedCommitter.setVisible(false);
		getPromotedLeader.setVisible(false);
		
		getObserver.setVisible(false);
		getContributor.setVisible(false);
		getReporter.setVisible(false);
		getTester.setVisible(false);
		getCommitter.setVisible(false);
		getLeader.setVisible(false);
		
		yesAnswerButton.setVisible(false);
		noAnswerButton.setVisible(false);
		joinCommunityButton.setVisible(false);
		removeValue.setVisible(false);
		askForCommiting.setVisible(false);
		voteCommiting.setVisible(false);
		voteRemove.setVisible(false);
		acceptValueLeader.setVisible(false);
		removeValueLeader.setVisible(false);
	}
	
	public void informationQuestion(int questionNumber_) 
	{
		switch (questionNumber_) 
		{
			case isEmptyPosition:
				gameInfoRow5Label.setText("A position is empty when the cell is");
				gameInfoRow5Label.setLocation(515, 250);
				gameInfoRow5Label.setSize(200, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("waiting for a value.");
				gameInfoRow6Label.setLocation(560, 270);
				gameInfoRow6Label.setSize(150, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isCorrect:
				gameInfoRow5Label.setText("A value is correct when there is not");
				gameInfoRow5Label.setLocation(515, 250);
				gameInfoRow5Label.setSize(200, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("any conflict with others values.");
				gameInfoRow6Label.setLocation(525, 270);
				gameInfoRow6Label.setSize(200, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isBug:
				gameInfoRow5Label.setText("A value is a bug when there is");
				gameInfoRow5Label.setLocation(525, 240);
				gameInfoRow5Label.setSize(200, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("a conflict with others values.");
				gameInfoRow6Label.setLocation(530, 260);
				gameInfoRow6Label.setSize(200, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isByServer:
				gameInfoRow5Label.setText("This kind of Values have been initialized");
				gameInfoRow5Label.setLocation(500, 240);
				gameInfoRow5Label.setSize(230, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("by the server and are allways Correct.");
				gameInfoRow6Label.setLocation(510, 260);
				gameInfoRow6Label.setSize(230, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isContribution:
				gameInfoRow5Label.setText("A value is a Contribution when have been");
				gameInfoRow5Label.setLocation(500, 240);
				gameInfoRow5Label.setSize(230, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("initialized by a Contributor");
				gameInfoRow6Label.setLocation(540, 260);
				gameInfoRow6Label.setSize(180, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isReported:
				gameInfoRow5Label.setText("A value is Reported when have been");
				gameInfoRow5Label.setLocation(510, 240);
				gameInfoRow5Label.setSize(230, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("recognized as a Bug.");
				gameInfoRow6Label.setLocation(550, 260);
				gameInfoRow6Label.setSize(180, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isCommitted:
				gameInfoRow5Label.setText("A value is Comittted when the");
				gameInfoRow5Label.setLocation(525, 240);
				gameInfoRow5Label.setSize(180, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("Committers have voted to keep it.");
				gameInfoRow6Label.setLocation(515, 260);
				gameInfoRow6Label.setSize(200, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isNotCommitted:
				gameInfoRow5Label.setText("A value is Not Comittted when the");
				gameInfoRow5Label.setLocation(515, 240);
				gameInfoRow5Label.setSize(200, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("Committers have voted to remove it.");
				gameInfoRow6Label.setLocation(510, 260);
				gameInfoRow6Label.setSize(200, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isAccepted:
				gameInfoRow5Label.setText("A value is Accepted when a Leader");
				gameInfoRow5Label.setLocation(515, 240);
				gameInfoRow5Label.setSize(200, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("has decided it is correct.");
				gameInfoRow6Label.setLocation(545, 260);
				gameInfoRow6Label.setSize(200, 20);
				gameInfoRow6Label.setVisible(true);
				break;
			case isRejected:
				gameInfoRow5Label.setText("A value is Rejected when a Leader");
				gameInfoRow5Label.setLocation(515, 240);
				gameInfoRow5Label.setSize(200, 20);
				gameInfoRow5Label.setVisible(true);
				
				gameInfoRow6Label.setText("has decided it was not correct.");
				gameInfoRow6Label.setLocation(525, 260);
				gameInfoRow6Label.setSize(200, 20);
				gameInfoRow6Label.setVisible(true);
				break;
		}
	}
	
	public void answerYes() 
	{
		yesAnswerButton.setVisible(false);
		noAnswerButton.setVisible(false);
		
		switch (questionNumber) 
		{
			case isEmptyPosition:				
				if (cells[xAsk][yAsk].valueState == waitingValue) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isCorrect:
				if (checkCorrectPosition(xAsk, yAsk,cells[xAsk][yAsk].current)) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isBug:
				if (!checkCorrectPosition(xAsk, yAsk,cells[xAsk][yAsk].current)) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isByServer:
				if (cells[xAsk][yAsk].valueState == intializedByServer) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isContribution:
				if (cells[xAsk][yAsk].valueState == contributedByRows || cells[xAsk][yAsk].valueState == contributedByColumns ||
				cells[xAsk][yAsk].valueState == contributedBySquares || cells[xAsk][yAsk].valueState == contributedByUser) 
				{
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isReported:
				if (cells[xAsk][yAsk].valueState == reportedByRows || cells[xAsk][yAsk].valueState == reportedByColumns ||
					cells[xAsk][yAsk].valueState == reportedBySquares || cells[xAsk][yAsk].valueState == reportedByUser) 
				{
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isCommitted:
				if (cells[xAsk][yAsk].valueState == committedByTesterByRows || cells[xAsk][yAsk].valueState == committedByTesterByColumns ||
					cells[xAsk][yAsk].valueState == committedByTesterBySquares || cells[xAsk][yAsk].valueState == committedByTesterByUser) 
				{
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isNotCommitted:
				if (cells[xAsk][yAsk].valueState == notCommitted) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isAccepted:
				if (cells[xAsk][yAsk].valueState == acceptedByAgent || cells[xAsk][yAsk].valueState == acceptedByUser) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isRejected:
				if (cells[xAsk][yAsk].valueState == rejectedByAgent || cells[xAsk][yAsk].valueState == rejectedByUser) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
		}

		askQuestions();
	}
	
	public void answerNo() 
	{
		yesAnswerButton.setVisible(false);
		noAnswerButton.setVisible(false);
		
		switch (questionNumber) 
		{
			case isEmptyPosition:
				if (cells[xAsk][yAsk].valueState != waitingValue) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isCorrect:
				if (!checkCorrectPosition(xAsk, yAsk,cells[xAsk][yAsk].current)) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isBug:
				if (checkCorrectPosition(xAsk, yAsk,cells[xAsk][yAsk].current)) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isByServer:
				if (cells[xAsk][yAsk].valueState != intializedByServer) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isContribution:
				if (cells[xAsk][yAsk].valueState != contributedByRows || cells[xAsk][yAsk].valueState != contributedByColumns ||
				cells[xAsk][yAsk].valueState != contributedBySquares || cells[xAsk][yAsk].valueState != contributedByUser) 
				{
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isReported:
				if (cells[xAsk][yAsk].valueState != reportedByRows || cells[xAsk][yAsk].valueState != reportedByColumns ||
					cells[xAsk][yAsk].valueState != reportedBySquares || cells[xAsk][yAsk].valueState != reportedByUser) 
				{
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isCommitted:
				if (cells[xAsk][yAsk].valueState != committedByTesterByRows || cells[xAsk][yAsk].valueState != committedByTesterByColumns ||
					cells[xAsk][yAsk].valueState != committedByTesterBySquares || cells[xAsk][yAsk].valueState != committedByTesterByUser) 
				{
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isNotCommitted:
				if (cells[xAsk][yAsk].valueState != notCommitted) {
					correctAnswers++;
				}
				break;
			case isAccepted:
				if (cells[xAsk][yAsk].valueState != acceptedByAgent || cells[xAsk][yAsk].valueState != acceptedByUser) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
			case isRejected:
				if (cells[xAsk][yAsk].valueState != rejectedByAgent || cells[xAsk][yAsk].valueState != rejectedByUser) {
					correctAnswers++;
				} else {
					correctAnswers--;
				}
				break;
		}

		askQuestions();
	}
		
	public void Print(String message)
	{
		console.add(message);
		console.makeVisible(console.getItemCount()-1);
	}
	
	@Override
	public void CellClick(int x, int y) 
	{
		setActive(x, y);

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