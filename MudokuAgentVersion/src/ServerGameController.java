import java.awt.*;
import java.awt.event.*;
import java.awt.List;
import java.util.ArrayList;
import javax.swing.Timer;

public class ServerGameController extends GameController implements ActionListener 		
{	
	private static final long serialVersionUID = 1L;
	
	public enum ServerMode {graphic, text}
	ServerMode serverMode;

	static ServerNetworkController networkController;
	Thread serverThread;
	
	Label serverLogLabel;
	
	Label passiveUserLabel;
	Label contributorLabel;
	Label reporterLabel;
	Label testerLabel;
	Label committerLabel;
	Label leaderConnectedLabel;
	
	Label rowCommittersLabel;
	Label columnCommittersLabel;
	Label squareCommittersLabel;
	Label userCommittersLabel;
	
	Label correctLabel;
	Label correctValuesLabel;
	
	Label numMemebersLabel;
	Label reportedLabel;
	Label contributionLabel;
	Label votingLabel;
	Label commitedLabel;
	Label notCommitedLabel;
	Label rejectedLabel;
	Label acceptedLabel;
	
	Label infoCommitters;

	Button changeMode;
	List console;

	int legendOffSetX = 125;
	int legendOffSetY = 20;
	
	int valuesVoting;
	int valuesBugReported;
	int valuesContributed;
	int valuesCommitted;
	int valuesNotCommited;
	int valuesRejected;
	int valuesAccepted;
	
	ArrayList<Integer> agentIdList;			// This list saves all the IDs of Agents/Users of the same type connected to the server
	ArrayList<ArrayList<Integer>> agentsList = new ArrayList<ArrayList<Integer>>();
	
	int[] committerByRowsList, committerByColumnsList, committerBySquaresList;
	
	int[] lastAction;				
	ArrayList<int[]> lastActionsList = new ArrayList<int[]>();
	
	int[][] instantiator;
	boolean votingExists;

	ArrayList<Integer> votes;

	int conflictX;
	int conflictY;
	int lastVoting;
	
	int lastVotingX;
	int lastVotingY;
	int lastVotingValue;

	int voteCountDelay = 12000;
	Timer voteCountTimer;
	
	static Color[] colors;
	
	final int contributorByRowsColor = 0;
	final int contributorByColumnsColor = 1;
	final int contributorBySquaresColor = 2;
	final int reporterByRowsColor = 3;
	final int reporterByColumnsColor = 4;
	final int reporterBySquaresColor = 5;
	final int testerByRowsColor = 6;
	final int testerByColumnsColor = 7;
	final int testerBySquaresColor = 8;
	final int committerByRowsColor = 9;
	final int committerByColumnsColor = 10;
	final int committerBySquaresColor = 11;
	final int agentLeaderColor = 12;
	final int userLeaderColor = 13;
	final int userColor = 14;
	final int votingColor = 15;
	final int valueContributedColor = 16;
	final int valueReportedColor = 17;
	final int valueCommittedColor = 18;
	final int valueNotCommittedColor = 19;
	final int valueAcceptedColor = 20;
	final int valueRejectedColor = 21;
	
	public ServerGameController() {
		super();
		setLayout(null);
		GameController.sudokuSize = 16;
	}

	public void Print(String message) {
		console.add(message);
		console.makeVisible(console.getItemCount() - 1);
	}
	
	public void init()
	{
		super.init();
		Initialize();
		
		//-------------------------------         COMPONENTS FROM HERE         -------------------------------//	
		
		console = new List();
		console.setLocation(15, 50);
		console.setSize(735, 525);
		add(console);
		
		//-------------------------------         BUTTONS FROM HERE         -------------------------------//
		
		changeMode = new Button("Change Mode");
		changeMode.setSize(100,20);
		changeMode.setLocation(640, 20);
		changeMode.setActionCommand("changeMode");
		changeMode.addActionListener(this);						//Add the "Listener" to the button.
		add(changeMode);
		
		//-------------------------------         LABELS FROM HERE         -------------------------------//
		
		//-------------------------------> Text Mode
		
		serverLogLabel = new Label("Server Log:");
		serverLogLabel.setSize(90,20);
		serverLogLabel.setLocation(30, 20);
		add(serverLogLabel);
		
		//-------------------------------> Graphic Mode
		
		passiveUserLabel = new Label("Passive Users");
		passiveUserLabel.setSize(90,20);
		passiveUserLabel.setLocation(20, 22);
		add(passiveUserLabel);
		
		contributorLabel = new Label("Contributors");
		contributorLabel.setSize(90,20);
		contributorLabel.setLocation(20, 47);
		add(contributorLabel);
		
		reporterLabel = new Label("Bug Reporters");
		reporterLabel.setSize(90,20);
		reporterLabel.setLocation(20, 72);
		add(reporterLabel);
		
		testerLabel = new Label("Testers");
		testerLabel.setSize(90,20);
		testerLabel.setLocation(20, 97);
		add(testerLabel);
		
		committerLabel = new Label("Committers");
		committerLabel.setSize(90,20);
		committerLabel.setLocation(20, 122);
		add(committerLabel);
		
		leaderConnectedLabel = new Label("Project Leaders");
		leaderConnectedLabel.setSize(90,20);
		leaderConnectedLabel.setLocation(20, 147);
		add(leaderConnectedLabel);
		
	
		infoCommitters = new Label("The Committers are waiting for the next Voting.");
		infoCommitters.setSize(500,15);
		infoCommitters.setLocation(40, 480);
		add(infoCommitters);
		
		
		rowCommittersLabel = new Label("Row Committers");
		rowCommittersLabel.setSize(100,15);
		rowCommittersLabel.setLocation(65, 500);
		add(rowCommittersLabel);
		
		columnCommittersLabel = new Label("Column Committers");
		columnCommittersLabel.setSize(120,20);
		columnCommittersLabel.setLocation(238, 500);
		add(columnCommittersLabel);
		
		squareCommittersLabel = new Label("Square Committers");
		squareCommittersLabel.setSize(120,20);
		squareCommittersLabel.setLocation(418, 500);
		add(squareCommittersLabel);
		
		userCommittersLabel = new Label("User Committers");
		userCommittersLabel.setSize(100,20);
		userCommittersLabel.setLocation(608, 500);
		add(userCommittersLabel);
		
		
		correctLabel = new Label("Correct Values: ");
		correctLabel.setSize(100,20);
		correctLabel.setLocation(55, 190);
		add(correctLabel);
		
		correctValuesLabel = new Label("0 / " + sudokuSize*sudokuSize);
		correctValuesLabel.setSize(80,20);
		correctValuesLabel.setLocation(77, 200);
		add(correctValuesLabel);
		
		
		numMemebersLabel = new Label("The Comunity has 0 Memebers.");
		numMemebersLabel.setSize(250,20);
		numMemebersLabel.setLocation(415, 20);
		add(numMemebersLabel);
		
		contributionLabel = new Label("Contributions: 0");
		contributionLabel.setSize(130,20);
		contributionLabel.setLocation(35, 250);
		add(contributionLabel);
		
		reportedLabel = new Label("Bugs Reported: 0");
		reportedLabel.setSize(130,20);
		reportedLabel.setLocation(35, 280);
		add(reportedLabel);
		
		votingLabel = new Label("Voting Sessions: 0");
		votingLabel.setSize(130,20);
		votingLabel.setLocation(35, 310);
		add(votingLabel);
		
		commitedLabel = new Label("Committed: 0");
		commitedLabel.setSize(130,20);
		commitedLabel.setLocation(35, 340);
		add(commitedLabel);
		
		notCommitedLabel = new Label("Not Committed: 0");
		notCommitedLabel.setSize(130,20);
		notCommitedLabel.setLocation(35, 370);
		add(notCommitedLabel);
		
		rejectedLabel = new Label("Rejected: 0");
		rejectedLabel.setSize(130,20);
		rejectedLabel.setLocation(35, 400);
		add(rejectedLabel);
		
		acceptedLabel = new Label("Accpeted: 0");
		acceptedLabel.setSize(130,20);
		acceptedLabel.setLocation(35, 430);
		add(acceptedLabel);
		
		//-------------------------------         COLORS         -------------------------------//
		
		votingExists = false;
		votes = new ArrayList<>();
		
		colors = new Color[22];
		
		colors[contributorByRowsColor] = new Color(99, 184, 255);				// Contributed By Rows 		--> steelblue 1
		colors[contributorByColumnsColor] = new Color(127, 255,	0);				// Contributed By Columns 	--> chartreuse 1
		colors[contributorBySquaresColor] = new Color(255, 215, 0);				// Contributed By Squares 	--> gold 1
		colors[reporterByRowsColor] = new Color(92, 172, 238);					// Reporter By Rows 		--> steelblue 2
		colors[reporterByColumnsColor] = new Color(118,	238, 0);				// Reporter By Columns 		--> chartreuse 2
		colors[reporterBySquaresColor] = new Color(238,	201, 0);				// Reporter By Squares 		--> gold 2
		colors[testerByRowsColor] = new Color(79, 148, 205);					// Tester by Rows 			--> steelblue 3
		colors[testerByColumnsColor] = new Color(102, 205, 0);					// Tester By Columns 		--> chartreuse 3
		colors[testerBySquaresColor] = new Color(205, 173, 0);					// Tester By Squares 		--> gold 3
		colors[committerByRowsColor] = new Color(54, 100, 139);					// Committer by Rows 		--> steelblue 4
		colors[committerByColumnsColor] = new Color(69,	139, 0);				// Committer by Columns 	--> chartreuse 4
		colors[committerBySquaresColor] = new Color(139, 117, 0);				// Committer by Squares 	--> gold 4
		colors[agentLeaderColor] = new Color(205, 201,	201);					// Agent Leader 			--> snow 3
		colors[userLeaderColor] = new Color(238, 180, 180);						// User Leader				--> rosybrown 2
		colors[userColor] = new Color(238, 180, 180);							// User 					--> rosybrown 2
		colors[votingColor] = new Color(220, 20, 60);							// Conflict Color	 		--> crimson
		colors[valueContributedColor] = new Color(56, 142, 142);				// Value Contributed		--> sgi teal
		colors[valueReportedColor] = new Color(255, 255, 255);					// Value Reported			--> white
		colors[valueCommittedColor] = new Color(220, 20, 60);					// Value Committed			--> crimson
		colors[valueNotCommittedColor] = new Color(255, 255, 255);				// Value NOT Committed		--> white
		colors[valueAcceptedColor] = new Color(255, 193, 37);					// Value Accepted 			--> goldenrod 1
		colors[valueRejectedColor] = new Color(255, 255, 255);					// Value Rejected			--> white
		
		//-------------------------------         INITIALIZE THE GAME         -------------------------------//
		
		networkController = new ServerNetworkController(this);
		serverThread = new Thread(networkController);
		serverThread.start();
		
		newGame();
		repaint();
	}
	
	public void newGame() 
	{
		InitializeRandomProblem(40);
		
		lastAction = new int[6];
		lastAction[ID] = -1;
		lastAction[TYPE] = NO_TYPE;											// NO CASE. Voting Finished.
		lastAction[ACTION] = NEW_GAME;
		lastAction[X] = -1;
		lastAction[Y] = -1;
		lastAction[VALUE] = -1;
		
		lastActionsList.add(0, lastAction);
		Print("Server: Initialized a New Game.");
		
		serverMode = ServerMode.graphic;

		instantiator = new int[sudokuSize][sudokuSize];

		for (int i = 0; i < sudokuSize; i++) {
			for (int j = 0; j < sudokuSize; j++) {
				instantiator[i][j] = -1;
			}
		}
		
		for (int i = 0; i<19; i++) {
			agentsList.add(new ArrayList<Integer>());
		}
		
		committerByRowsList = new int[agentsList.get(agentCommitterByRows).size()];
		for(int i=0; i<agentsList.get(agentCommitterByRows).size(); i++) {
			committerByRowsList[i] = 0;
		}
		
		committerByColumnsList = new int[agentsList.get(agentCommitterByColumns).size()];
		for(int i=0; i<agentsList.get(agentCommitterByColumns).size(); i++) {
			committerByColumnsList[i] = 0;
		}
		
		committerBySquaresList = new int[agentsList.get(agentCommitterBySquares).size()];
		for(int i=0; i<agentsList.get(agentCommitterBySquares).size(); i++) {
			committerBySquaresList[i] = 0;
		}
		
		valuesVoting = 0;
		valuesBugReported = 0;
		valuesContributed = 0;
		valuesCommitted = 0;
		valuesNotCommited = 0;
		valuesRejected = 0;
		valuesAccepted = 0;
	}
	
	@Override
	public void paint(Graphics gr)
	{
		initDraw(gr);
		
		switch(serverMode)
		{
			case graphic:
				setGraphicMode();
				DrawGraphicMode(gr);
				break;
			case text:
				setTextMode();
				DrawTextMode(gr);
				break;
		default:
			break;
		}
	}
	
	public void DrawTextMode(Graphics gr) 
	{
		gr.setColor(Color.black);		
		
		Stroke stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		gr.drawLine(10, 10, 10, 580);			// General Box
		gr.drawLine(755, 10, 755, 580);	
		gr.drawLine(10, 10, 755, 10);			
		gr.drawLine(10, 580, 755, 580);
	}
	
	public void DrawGraphicMode(Graphics gr) 
	{
		gr.setColor(Color.black);		
		
		Stroke stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
				
		gr.drawLine(10, 10, 10, 580);			// General Box
		gr.drawLine(755, 10, 755, 580);	
		gr.drawLine(10, 10, 755, 10);			
		gr.drawLine(10, 580, 755, 580);
		
		gr.drawLine(120, 10, 120, 175);			// Vertical Line (Agents Connected)
		gr.drawLine(10, 175, 755, 175);			// Separator Agents Connected - Actions
		gr.drawLine(10, 470, 755, 470);			// Separator Actions - Voting
		
		// -------------------------------------------------------------->  Elements in Actions Box
		
		if (!votingExists) {					
			gr.setColor(colors[votingColor]);
		}
		else {
			gr.setColor(Color.black);
		}
			
		gr.drawLine(180, 180, 180, 465);		// Separator Information Boxes - Actions
		
		gr.drawLine(15, 180, 15, 465);			// Box Actions
		gr.drawLine(750, 180, 750, 465);	
		gr.drawLine(15, 180, 750, 180);			
		gr.drawLine(15, 465, 750, 465);
		
		gr.drawLine(25, 185, 25, 235);			// Correct Values Box
		gr.drawLine(170, 185, 170, 235);	
		gr.drawLine(25, 185, 170, 185);			
		gr.drawLine(25, 235, 170, 235);
		
		numMemebersLabel.setText("The Community has " + getNumMembers() + " Members");
		
		int correctValues = getCountCorrect();
		correctValuesLabel.setText(correctValues + " / " + sudokuSize*sudokuSize);
		if (correctValues < 100) {
			correctValuesLabel.setLocation(73, 210);
		} 
		else {
			correctValuesLabel.setLocation(70, 210);
		}	
		
		gr.drawLine(25, 245, 25, 455);			// Misc Information Box
		gr.drawLine(170, 245, 170, 455);	
		gr.drawLine(25, 245, 170, 245);			
		gr.drawLine(25, 455, 170, 455);			
		
												//Num Members Connected
		contributionLabel.setText("Contributions: " + valuesContributed);	
		
		gr.drawLine(25, 275, 170, 275);			//Num Contributions
		reportedLabel.setText("Bugs Reported: " + valuesBugReported);	

		gr.drawLine(25, 305, 170, 305);			//Num Values Reported
		votingLabel.setText("Voting Sessions: " + valuesVoting);	
		
		gr.drawLine(25, 335, 170, 335);			//Num Voting Sessions
		commitedLabel.setText("Committed: " + valuesCommitted);	
		
		gr.drawLine(25, 365, 170, 365);			//Num Values Committed
		notCommitedLabel.setText("Not Committed: " + valuesNotCommited);
		
		gr.drawLine(25, 395, 170, 395);			//Num Values Not Committed
		rejectedLabel.setText("Rejected: " + valuesRejected);
		
		gr.drawLine(25, 425, 170, 425);			//Num Values Rejected
		acceptedLabel.setText("Accepted: " + valuesAccepted);
		
		// -------------------------------------------------------------->  Elements in Voting Box
		
		if (votingExists) {
			gr.setColor(colors[votingColor]);
		}
		else {
			gr.setColor(Color.black);
		}
		
		gr.drawLine(15, 475, 15, 575);			// Voting Box
		gr.drawLine(750, 475, 750, 575);	
		gr.drawLine(15, 475, 750, 475);			
		gr.drawLine(15, 575, 750, 575);
		
		gr.drawLine(26, 487, 36, 487);	
		
		gr.drawLine(26, 520, 196, 520);			// Row Committers
		gr.drawLine(207, 520, 377, 520);		// Columns Committers
		gr.drawLine(388, 520, 558, 520);		// Squares Committers
		gr.drawLine(569, 520, 739, 520);		// User Committers

		// -------------------------------------------------------------->  Draw Agents + Users Connected
		
		gr.setColor(Color.black);
		
		int drawingX = legendOffSetX;
		int drawingY = legendOffSetX;
		
		for(int i=0; i<19; i++)															// Type Agent
		{
			switch(i)
			{
				case passiveUser:
					drawingY = legendOffSetY;
					drawingX = legendOffSetX;
					break;
				case agentContributorByRows:
					drawingY = legendOffSetY + (int) deltaY;
					drawingX = legendOffSetX;
					break;
				case agentBugReporterByRows: 
					drawingY = legendOffSetY + (int) deltaY * 2;
					drawingX = legendOffSetX;
					break;
				case agentTesterByRows: 						
					drawingY = legendOffSetY + (int) deltaY * 3;
					drawingX = legendOffSetX;
					break;
				case agentCommitterByRows: 
					drawingY = legendOffSetY + (int) deltaY * 4;
					drawingX = legendOffSetX;
					break;
				case agentLeader:
					drawingY = legendOffSetY + (int) deltaY * 5;
					drawingX = legendOffSetX;
					break;
			}
			
			agentIdList = agentsList.get(i);
			
			for(int j = 0; j<agentIdList.size() && j<8; j++)			//The limit of Agents of each type that It's going to be show is 8!
			{
				switch(i)
				{	
					case passiveUser: case userContributor: case userBugReporter: 
					case userTester: case userCommitter:									// User
						gr.setColor(colors[userColor]);
						break;
					case agentContributorByRows:											// Contributor by Rows
						gr.setColor(colors[contributorByRowsColor]);
						break;
					case agentContributorByColumns:											//	Contributor by Columns
						gr.setColor(colors[contributorByColumnsColor]);		
						break;
					case agentContributorBySquares:											//	Contributor by Squares
						gr.setColor(colors[contributorBySquaresColor]);		
						break;
					case agentBugReporterByRows: 											// Reporter by Rows
						gr.setColor(colors[reporterByRowsColor]);		
						break;
					case agentBugReporterByColumns: 										// Reporter by Columns
						gr.setColor(colors[reporterByColumnsColor]);	
						break;
					case agentBugReporterBySquares:											// Reporter by Squares
						gr.setColor(colors[reporterBySquaresColor]);	
						break;
					case agentTesterByRows: 												// Tester by Rows
						gr.setColor(colors[testerByRowsColor]);			
						break;
					case agentTesterByColumns: 												// Tester by Columns
						gr.setColor(colors[testerByColumnsColor]);		
						break;
					case agentTesterBySquares:												// Tester by Squares
						gr.setColor(colors[testerBySquaresColor]);		
						break;
					case agentCommitterByRows: 												//Committer by Columns
						gr.setColor(colors[committerByRowsColor]);	
						break;
					case agentCommitterByColumns: 											//Committer by Columns
						gr.setColor(colors[committerByColumnsColor]);		
						break;
					case agentCommitterBySquares:											//Committer by Squares
						gr.setColor(colors[committerBySquaresColor]);		
						break;	
					case agentLeader:														//Agent Project Leader				
						gr.setColor(colors[agentLeaderColor]);			
						break;
					case userLeader:														//Agent Project Leader				
						gr.setColor(colors[userLeaderColor]);				
						break;
				}
				
				gr.fillRect(drawingX, drawingY, (int) deltaX, (int) deltaY);
				gr.setColor(Color.black);
				gr.drawRect(drawingX, drawingY, (int) deltaX, (int) deltaY);
				gr.setColor(Color.white);
				
				if(agentIdList.get(j) < 10) {
					gr.drawString(String.valueOf(agentIdList.get(j)), (int) (drawingX + deltaX / 2) - 2, (int) (drawingY + deltaY) - 10);
				}
				else if(agentIdList.get(j) < 99) {
					gr.drawString(String.valueOf(agentIdList.get(j)), (int) (drawingX + deltaX / 2) - 5, (int) (drawingY + deltaY) - 10);
				}
				else {
					gr.drawString(String.valueOf(agentIdList.get(j)), (int) (drawingX + deltaX / 2) - 8, (int) (drawingY + deltaY) - 10);
				}
			
				drawingX += deltaX;
			}
		}
		
		// -------------------------------------------------------------->  Draw Last Actions
		
		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		
		int drawActionOffSetX = 200;
		int drawActionOffSetY = 205;
		
		boolean isUser = false;
		boolean isVotingFinished = false;
		boolean isNewGame = false;
		
		for(int i=0; i<lastActionsList.size() && i < 7; i++)
		{
			isUser = false;
			isVotingFinished = false;
			isNewGame = false;
			
			lastAction = lastActionsList.get(i);
			switch(lastAction[TYPE])													//It chooses the color of the Squares of the Agent.
			{
				case passiveUser: case userContributor: case userBugReporter: 
				case userTester: case userCommitter:									// User
					gr.setColor(colors[userColor]);
					isUser = true;
					break;
				case agentContributorByRows:											// Contributor by Rows
					gr.setColor(colors[contributorByRowsColor]);
					break;
				case agentContributorByColumns:											//	Contributor by Columns
					gr.setColor(colors[contributorByColumnsColor]);		
					break;
				case agentContributorBySquares:											//	Contributor by Squares
					gr.setColor(colors[contributorBySquaresColor]);		
					break;
				case agentBugReporterByRows: 											// Reporter by Rows
					gr.setColor(colors[reporterByRowsColor]);		
					break;
				case agentBugReporterByColumns: 										// Reporter by Columns
					gr.setColor(colors[reporterByColumnsColor]);	
					break;
				case agentBugReporterBySquares:											// Reporter by Squares
					gr.setColor(colors[reporterBySquaresColor]);	
					break;
				case agentTesterByRows: 												// Tester by Rows
					gr.setColor(colors[testerByRowsColor]);		
					break;
				case agentTesterByColumns: 												// Tester by Columns
					gr.setColor(colors[testerByColumnsColor]);		
					break;
				case agentTesterBySquares:												// Tester by Squares
					gr.setColor(colors[testerBySquaresColor]);		
					break;
				case agentCommitterByRows: 												//Committer by Columns
					gr.setColor(colors[committerByRowsColor]);	
					break;
				case agentCommitterByColumns: 											//Committer by Columns
					gr.setColor(colors[committerByColumnsColor]);		
					break;
				case agentCommitterBySquares:											//Committer by Squares
					gr.setColor(colors[committerBySquaresColor]);		
					break;	
				case agentLeader:														//Agent Project Leader				
					gr.setColor(colors[agentLeaderColor]);			
					break;
				case userLeader:														//User Project Leader				
					gr.setColor(colors[userLeaderColor]);				
					break;
				case NO_TYPE:
					if (lastAction[ACTION] == COMMITTED || lastAction[ACTION] == NOT_COMMITTED) {
						isVotingFinished = true;
					} 
					else if (lastAction[ACTION] == NEW_GAME) {
						isNewGame = true;
					}
					break;
			}
				
			if(isUser && !isVotingFinished && !isNewGame) 
			{																								//Write the Word: "Agent" or "User"
				gr.drawString("User ", drawActionOffSetX, drawActionOffSetY);
				
				//TODO: Afegir el nom del user! (No esta fet el Sistma).
			}
			else if (!isUser && !isVotingFinished && !isNewGame)
			{
				gr.fillRect(drawActionOffSetX + 40, drawActionOffSetY - 15, (int) deltaX, (int) deltaY);		//Fill the square of the Agent ID
				
				gr.setColor(Color.black);			
				gr.drawRect(drawActionOffSetX + 40, drawActionOffSetY - 15, (int) deltaX, (int) deltaY);		//Draw the square of the Agent ID
				gr.drawString("Agent ", drawActionOffSetX, drawActionOffSetY);
				
				gr.setColor(Color.white);
				if(lastAction[ID] < 10) {																		//Write the ID of the Agent
					gr.drawString(String.valueOf(lastAction[ID]), (int) (drawActionOffSetX + 40 + deltaX / 2) - 2, (int) (drawActionOffSetY - 15 + deltaY) - 10);
				}
				else if(lastAction[ID] < 99) {
					gr.drawString(String.valueOf(lastAction[ID]), (int) (drawActionOffSetX + 40 + deltaX / 2) - 5, (int) (drawActionOffSetY - 15 + deltaY) - 10);
				}
				else {
					gr.drawString(String.valueOf(lastAction[ID]), (int) (drawActionOffSetX + 40 + deltaX / 2) - 8, (int) (drawActionOffSetY - 15 + deltaY) - 10);
				}
			}
			
			gr.setColor(Color.black);
			
			switch(lastAction[ACTION])
			{
				case NEW_GAME:
					gr.drawString("New Game Started!", drawActionOffSetX, drawActionOffSetY);
				break;
				case CONTRIBUTION:
					gr.drawOval(drawActionOffSetX + 330, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
					gr.drawString("Contributed at the position [" + lastAction[X] + "][" + lastAction[Y] + "] with the Value: ", drawActionOffSetX + 75, drawActionOffSetY);	
					
					gr.setColor(colors[valueContributedColor]);
					gr.fillOval(drawActionOffSetX + 330, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
					
					gr.setColor(Color.white);
					if(lastAction[VALUE] < 10) {																		//Write the ID of the Agent
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 338, drawActionOffSetY);
					}
					else {
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 336, drawActionOffSetY);
					}
					
					break;
				case BUG_REPORTED:
					gr.drawOval(drawActionOffSetX + 345, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);

					gr.drawString("Reported at the position [" + lastAction[X] + "][" + lastAction[Y] + "] a bug with the Value: ", drawActionOffSetX + 75, drawActionOffSetY);
					gr.setColor(colors[valueReportedColor]);
					gr.fillOval(drawActionOffSetX + 345, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
					gr.setColor(Color.black);
					
					if(lastAction[VALUE] < 10) {																		//Write the ID of the Agent
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 354, drawActionOffSetY);
					}
					else {
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 351, drawActionOffSetY);
					}
					
					break;
				case VOTING:
					gr.drawOval(drawActionOffSetX + 335, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);

					gr.drawString("Tested Correctly the Position [" + lastAction[X] + "][" + lastAction[Y] + "] for the Value: ", drawActionOffSetX + 75, drawActionOffSetY);
					gr.drawString("Voting Started!", drawActionOffSetX + 365, drawActionOffSetY);
					gr.setColor(colors[valueCommittedColor]);
					gr.fillOval(drawActionOffSetX + 335, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
					gr.setColor(Color.white);
					
					if(lastAction[VALUE] < 10) {																		//Write the Value in the correct Position
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 344, drawActionOffSetY);
					}
					else {
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 341, drawActionOffSetY);
					}
					break;
				case COMMITTED: case NOT_COMMITTED:
					gr.drawString("Voting Finished! The Value", drawActionOffSetX, drawActionOffSetY);
					gr.drawOval(drawActionOffSetX + 140, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);

					if (lastAction[ACTION] == COMMITTED) {
						gr.drawString("at the position [" + lastAction[X] + "][" + lastAction[Y] + "] has been Committed!", drawActionOffSetX + 170, drawActionOffSetY);
						gr.setColor(colors[valueCommittedColor]);
						gr.fillOval(drawActionOffSetX + 140, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
						gr.setColor(Color.white);
					}
					else if (lastAction[ACTION] == NOT_COMMITTED) {
						gr.drawString("at the position [" + lastAction[X] + "][" + lastAction[Y] + "] has not been Committed.", drawActionOffSetX + 170, drawActionOffSetY);
						gr.setColor(colors[valueNotCommittedColor]);
						gr.fillOval(drawActionOffSetX + 140, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
						gr.setColor(Color.black);
					}
					
					if(lastAction[VALUE] < 10) {																		//Write the Value in the correct Position
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 149, drawActionOffSetY);
					}
					else {
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 146, drawActionOffSetY);
					}
					
					break;
				case ACCEPTED: case REJECTED:
					
					gr.drawOval(drawActionOffSetX + 290, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);

					if (lastAction[ACTION] == ACCEPTED) {
						gr.drawString("Accpeted at the position [" + lastAction[X] + "][" + lastAction[Y] + "] the Value: ", drawActionOffSetX + 75, drawActionOffSetY);
						gr.setColor(colors[valueAcceptedColor]);
						gr.fillOval(drawActionOffSetX + 290, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
						gr.setColor(Color.white);
					}
					else {
						gr.drawString("Rejected at the position [" + lastAction[X] + "][" + lastAction[Y] + "] the Value: ", drawActionOffSetX + 75, drawActionOffSetY);
						gr.setColor(colors[valueRejectedColor]);
						gr.fillOval(drawActionOffSetX + 290, drawActionOffSetY - 15, (int) deltaX - 4, (int) deltaY - 4);
						gr.setColor(Color.black);
					}

					if(lastAction[VALUE] < 10) {																		//Write the ID of the Agent
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 299, drawActionOffSetY);
					}
					else {
						gr.drawString(String.valueOf(lastAction[VALUE]), drawActionOffSetX + 296, drawActionOffSetY);
					}
					
					break;
			}
			
			drawActionOffSetY += 40;
		}
		
		// -------------------------------------------------------------->  Draw Voting System
		
		lastAction = lastActionsList.get(0);
		
		if(votingExists) {
			infoCommitters.setText("The Committers are Voting to Commit the Value " + lastVotingValue + " at the position [" + lastVotingX + "][" + lastVotingY + "]");
		}
		else {
			infoCommitters.setText("The Committers are waiting for the next Voting.");
		}
	
		int committerY = 525;
		int committerMoveX = 0;
		
		int committerByRowsX = 25;		
		int committerByColumnsX = 207;
		int committerBySquaresX = 388;
		int committerByUserX = 569;
		
		// Draw the Different Agents Committers we have
		
		for (int i=0; i<agentsList.get(agentCommitterByRows).size() && i<6; i++)
		{
			gr.setColor(colors[committerByRowsColor]);
			gr.fillRect(committerByRowsX + committerMoveX, committerY, (int) deltaX, (int) deltaY);
			gr.setColor(Color.black);
			gr.drawRect(committerByRowsX + committerMoveX, committerY, (int) deltaX, (int) deltaY);
			gr.setColor(Color.white);
			
			if(agentsList.get(agentCommitterByRows).get(i) < 99) {
				gr.drawString(String.valueOf(agentsList.get(agentCommitterByRows).get(i)), committerByRowsX + committerMoveX + 8, committerY + 17);
			}
			else {
				gr.drawString(String.valueOf(agentsList.get(agentCommitterByRows).get(i)), committerByRowsX + committerMoveX + 5, committerY + 17);
			}
			
					
			if (committerByRowsList.length > i && committerByRowsList[i] == -1) {
				gr.setColor(Color.green);			
			}
			else if (committerByRowsList.length > i && committerByRowsList[i] == 0) {
				gr.setColor(Color.white);
			}
			else if (committerByRowsList.length > i && committerByRowsList[i] == 1) {
				gr.setColor(Color.red);
			}
			else {
				gr.setColor(Color.white);
			}
				
			gr.fillRect(committerByRowsX + committerMoveX + 7, committerY + 30, 10, 10);
			gr.setColor(Color.black);
			gr.drawRect(committerByRowsX + committerMoveX + 7, committerY + 30, 10, 10);

			committerMoveX += 29;
		}	
		
		committerMoveX = 0;
		
		for (int i=0; i<agentsList.get(agentCommitterByColumns).size() && i<6; i++)
		{
			gr.setColor(colors[committerByColumnsColor]);
			gr.fillRect(committerByColumnsX + committerMoveX, committerY, (int) deltaX, (int) deltaY);
			gr.setColor(Color.black);
			gr.drawRect(committerByColumnsX + committerMoveX, committerY, (int) deltaX, (int)deltaY);
			gr.setColor(Color.white);
			
			if(agentsList.get(agentCommitterByColumns).get(i) < 99) {
				gr.drawString(String.valueOf(agentsList.get(agentCommitterByColumns).get(i)), committerByColumnsX + committerMoveX + 8, committerY + 17);
			}
			else {
				gr.drawString(String.valueOf(agentsList.get(agentCommitterByColumns).get(i)), committerByColumnsX + committerMoveX + 5, committerY + 17);
			}
			

			if (committerByColumnsList.length > i && committerByColumnsList[i] == -1) {
				gr.setColor(Color.green);			
			}
			else if (committerByColumnsList.length > i && committerByColumnsList[i] == 0) {
				gr.setColor(Color.white);
			}
			else if (committerByColumnsList.length > i && committerByColumnsList[i] == 1) {
				gr.setColor(Color.red);
			}
			else {
				gr.setColor(Color.white);
			}
			
			gr.fillRect(committerByColumnsX + committerMoveX + 7, committerY + 30, 10, 10);
			gr.setColor(Color.black);
			gr.drawRect(committerByColumnsX + committerMoveX + 7, committerY + 30, 10, 10);

			committerMoveX += 29;
		}
		
		committerMoveX = 0;
		
		for (int i=0; i<agentsList.get(agentCommitterBySquares).size() && i<6; i++)
		{
			gr.setColor(colors[committerBySquaresColor]);
			gr.fillRect(committerBySquaresX + committerMoveX, committerY, (int) deltaX, (int) deltaY);
			gr.setColor(Color.black);
			gr.drawRect(committerBySquaresX + committerMoveX, committerY, (int) deltaX, (int)deltaY);
			gr.setColor(Color.white);

			if(agentsList.get(agentCommitterBySquares).get(i) < 99) {																		//Write the ID of the Agent
				gr.drawString(String.valueOf(agentsList.get(agentCommitterBySquares).get(i)), committerBySquaresX + committerMoveX + 8, committerY + 17);
			}
			else {
				gr.drawString(String.valueOf(agentsList.get(agentCommitterBySquares).get(i)), committerBySquaresX + committerMoveX + 5, committerY + 17);
			}
			
			if (committerBySquaresList.length > i && committerBySquaresList[i] == -1) {
				gr.setColor(Color.green);		
			}
			else if (committerBySquaresList.length > i && committerBySquaresList[i] == 0) {
				gr.setColor(Color.white);
			}
			else if (committerBySquaresList.length > i && committerBySquaresList[i] == 1) {
				gr.setColor(Color.red);
			}
			else {
				gr.setColor(Color.white);
			}
			
			
			gr.fillRect(committerBySquaresX + committerMoveX + 7, committerY + 30, 10, 10);
			gr.setColor(Color.black);
			gr.drawRect(committerBySquaresX + committerMoveX + 7, committerY + 30, 10, 10);

			committerMoveX += 29;
		}
		
		//TODO: Falta afegir tot los dels users aqui!
	}

	int getVotingResult()
	{
		int result = 0;
		
		for(int i=0; i<committerByRowsList.length;i++)
			result += committerByRowsList[i];
		
		for(int i=0; i<committerByColumnsList.length;i++)
			result += committerByColumnsList[i];
		
		for(int i=0; i<committerBySquaresList.length;i++)
			result += committerBySquaresList[i];
		
		return result;
	}
	
	public void setGraphicMode()
	{
		serverMode = ServerMode.graphic;
		
		console.setVisible(false);
		serverLogLabel.setVisible(false);
		
		passiveUserLabel.setVisible(true);
		contributorLabel.setVisible(true);
		reporterLabel.setVisible(true);
		testerLabel.setVisible(true);
		committerLabel.setVisible(true);
		leaderConnectedLabel.setVisible(true);
		
		rowCommittersLabel.setVisible(true);
		columnCommittersLabel.setVisible(true);
		squareCommittersLabel.setVisible(true);
		userCommittersLabel.setVisible(true);
		
		correctLabel.setVisible(true);
		correctValuesLabel.setVisible(true);
		
		numMemebersLabel.setVisible(true);
		reportedLabel.setVisible(true);
		contributionLabel.setVisible(true);
		votingLabel.setVisible(true);
		commitedLabel.setVisible(true);
		notCommitedLabel.setVisible(true);
		rejectedLabel.setVisible(true);
		acceptedLabel.setVisible(true);
		
		infoCommitters.setVisible(true);
		
		repaint();
	}
	
	public void setTextMode()
	{
		serverMode = ServerMode.text;
		
		console.setVisible(true);
		serverLogLabel.setVisible(true);
		
		passiveUserLabel.setVisible(false);
		contributorLabel.setVisible(false);
		reporterLabel.setVisible(false);
		testerLabel.setVisible(false);
		committerLabel.setVisible(false);
		leaderConnectedLabel.setVisible(false);
		
		rowCommittersLabel.setVisible(false);
		columnCommittersLabel.setVisible(false);
		squareCommittersLabel.setVisible(false);
		userCommittersLabel.setVisible(false);
		
		correctLabel.setVisible(false);
		correctValuesLabel.setVisible(false);
		
		numMemebersLabel.setVisible(false);
		reportedLabel.setVisible(false);
		contributionLabel.setVisible(false);
		votingLabel.setVisible(false);
		commitedLabel.setVisible(false);
		notCommitedLabel.setVisible(false);
		rejectedLabel.setVisible(false);
		acceptedLabel.setVisible(false);
		
		infoCommitters.setVisible(false);
		
		repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent action) 
	{
		//super.actionPerformed(action);
		switch(action.getActionCommand())
		{
			case "voteCount":
				ConcludeVoting();
				break;
			case "changeMode":
				if(serverMode == ServerMode.graphic) {
					setTextMode();
				}
				else {
					setGraphicMode();
				}
				
				break;
		}
	}
	
	public void ConcludeVoting()
	{
		votingExists = false;
		voteCountTimer.stop();
		
		if(lastActionsList.size() == 7) {
			lastActionsList.remove(6);
		}
		
		lastAction = new int[6];
		lastAction[TYPE] = NO_TYPE;											// NO CASE. Voting Finished.
		lastAction[X] = conflictX;
		lastAction[Y] = conflictY;
		lastAction[VALUE] = cells[conflictX][conflictY].current;
		lastAction[RESULT_VOTING] = getVotingResult();
		
		if(EvaluateVote()) {
			lastAction[ACTION] = NOT_COMMITTED;
			networkController.BroadcastMessage("notCommitted#" + conflictX + "," + conflictY + "," + lastVoting);
			ClearCell(conflictX, conflictY, notCommitted);
			valuesNotCommited++;
		}
		else {
			lastAction[ACTION] = COMMITTED;
			networkController.BroadcastMessage("committed#" + conflictX + "," + conflictY + "," + lastVoting);
			SetValueAndState(conflictX, conflictY, cells[conflictX][conflictY].current, lastVoting);
			valuesCommitted++;
		}
		
		lastActionsList.add(0, lastAction);
		repaint();
	}
	
	String EncodeCurrentStatus()
	{
		String code = "";
		for(int i = 0; i < sudokuSize; i++)
		{
			for(int k = 0; k < sudokuSize; k++)
			{
				if(cells[i][k].valueState != waitingValue)
					code += i + "," + k + "," + cells[i][k].current + "," + cells[i][k].valueState + "," + instantiator[i][k] + "&";
			}
		}
		
		return code.substring(0, code.length() - 1);
	}
	
	public synchronized void MessageReceived(ClientHandler clientHandler, String message)
	{
		String[] vars = message.split("#");
		String[] vars2 = null;
		
		int agentId, agentType, x, y, val, sizeList;
		boolean isAgent;
		
		try 
		{
			switch(vars[0])
			{
			case "request":
				vars2 = vars[1].split("=");
				if(!vars2[0].contains("type")) {
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
			case "connect":				//	When an Agent tries to connect to the server.
				vars2 = vars[1].split(",");
				agentId = Integer.parseInt(vars2[0]);
				agentType = Integer.parseInt(vars2[1]);	
								
				agentIdList = new ArrayList<Integer>();
				
				sizeList = agentsList.get(agentType).size();
				if (sizeList > 0)
				{
					for(int i = 0; i<sizeList; i++) {
						agentIdList.add(agentsList.get(agentType).get(i));
					}
				}
				
				agentIdList.add(agentId);
				agentsList.set(agentType, agentIdList);
				
				networkController.addAgent(agentId, agentType);
				
				break;					//When an Agent tries to disconnect from the server.
			case "disconnect":
				vars2 = vars[1].split(",");
				agentId = Integer.parseInt(vars2[0]);
				agentType = Integer.parseInt(vars2[1]);
				
				agentIdList = new ArrayList<Integer>();
				agentIdList = agentsList.get(agentType);
				
				sizeList = agentIdList.size();
				if (sizeList > 0) {
					for(int i = 0; i<sizeList; i++) {
						if(agentIdList.get(i) == agentId) {			
							agentIdList.remove(i);
							break;								//I had to include the break to avoid errors in next iterations by sizeList.
						}
					}
				}
				
				agentsList.set(agentType, agentIdList);
				
				networkController.removeAgent(agentId);
				break;
			case "instantiate":			//	When an Agent or User tries to add a new Value to the grid.
										//	"instantiate#" + agentId + "," typeAgent + "," + X + "," + Y + "," + value
				
				vars2 = vars[1].split(",");
				//agentId = Integer.parseInt(vars2[0]);			//For the userName.
				agentType = Integer.parseInt(vars2[1]);
				x = Integer.parseInt(vars2[2]);
				y = Integer.parseInt(vars2[3]);
				val = Integer.parseInt(vars2[4]);

				int contributed = -1;
				boolean instantiateFailed = false;
				isAgent = false;
				
				switch(agentType)
				{
					case agentContributorByRows:
						contributed = contributedByRows;
						isAgent = true;
						break;
					case agentContributorByColumns:
						isAgent = true;
						contributed = contributedByColumns;
						break;
					case agentContributorBySquares:	
						isAgent = true;
						contributed = contributedBySquares;
						break;
					case userContributor:	
						if (cells[x][y].valueState == waitingValue || cells[x][y].valueState == reportedByRows || cells[x][y].valueState == reportedByColumns || 
						    cells[x][y].valueState == reportedBySquares || cells[x][y].valueState == reportedByUser || cells[x][y].valueState == rejectedByAgent || 
						    cells[x][y].valueState == rejectedByUser) 
						{
							contributed = contributedByUser;
						}
						else 
						{
							clientHandler.SendMessage("instantiate_failed");
							instantiateFailed = true;
						}
						
						break;
				}
				
				if(isAgent)				//Graphic Mode
				{
					agentId = Integer.parseInt(vars2[0]);
					Print("Server: Agent " + agentId + " Contributed at the Position [" + x +"][" + y +"] with the Value [" + val + "]");
					
					if(lastActionsList.size() == 7) {
						lastActionsList.remove(6);
					}
					
					lastAction = new int[6];
					lastAction[ID] = agentId;
					lastAction[TYPE] = agentType;
					lastAction[ACTION] = CONTRIBUTION;
					lastAction[X] = x;
					lastAction[Y] = y;
					lastAction[VALUE] = val;
					
					lastActionsList.add(0, lastAction);
					repaint();
				}
				else {
					String userName = vars2[0];
					Print("Server: " + userName + " Contributed at the Position [" + x +"][" + y +"] with the Value [" + val + "]");
					//TODO: User Part for the Graphic Mode!
				}
				
				
				if (!instantiateFailed)
				{
					SetValueAndState(x, y, val, contributed);
					networkController.BroadcastMessage("instantiated#" + x + "," + y + "," + val + "," + contributed);
					valuesContributed++;
				}
				
				break;
			case "bugReported":
				vars2 = vars[1].split(",");
				//agentId = Integer.parseInt(vars2[0]);			//For the userName.
				agentType = Integer.parseInt(vars2[1]);
				int cleanX = Integer.parseInt(vars2[2]);
				int cleanY = Integer.parseInt(vars2[3]);
				int reported = -1;
				
				isAgent = false;
				
				switch(agentType)
				{
					case agentBugReporterByRows:
						isAgent = true;	
						reported = reportedByRows;
						break;
					case agentBugReporterByColumns:
						isAgent = true;
						reported = reportedByColumns;
						break;
					case agentBugReporterBySquares:
						isAgent = true;
						reported = reportedBySquares;
						break;
					case userBugReporter:	
						reported = reportedByUser;
						break;
				}
				
				if (isAgent)			//Graphic Mode
				{
					agentId = Integer.parseInt(vars2[0]);
					Print("Server: Agent " + agentId + " has found a bug at the position [" + cleanX + "][" + cleanY +"]. The value will be removed.");
					
					if(lastActionsList.size() == 7) {
						lastActionsList.remove(6);
					}
					
					lastAction = new int[6];
					lastAction[ID] = agentId;
					lastAction[TYPE] = agentType;
					lastAction[ACTION] = BUG_REPORTED;
					lastAction[X] = cleanX;
					lastAction[Y] = cleanY;
					lastAction[VALUE] = cells[cleanX][cleanY].current;
					
					lastActionsList.add(0, lastAction);
					repaint();
				}
				else {
					String userName = vars2[0];
					Print("Server: " + userName + " has found a bug at the position [" + cleanX + "][" + cleanY +"]. The value will be removed.");
					//TODO: User Part for the Graphic Mode!
				}
				
				networkController.BroadcastMessage("bugFound#" + cleanX + "," + cleanY + "," + reported);
				ClearCell(cleanX, cleanY, reported);

				valuesBugReported++;
				
				break;
			case "clear":
				
				if(!votingExists)
				{
					vars2 = vars[1].split(",");
					//agentId = Integer.parseInt(vars2[0]);			// For the userName.
					agentType = Integer.parseInt(vars2[1]);
					conflictX = Integer.parseInt(vars2[2]);
					conflictY = Integer.parseInt(vars2[3]);
					
					int tested = -1;
					isAgent = false;
					
					//Clean the votes of past Votings
					committerByRowsList = new int[agentsList.get(agentCommitterByRows).size()];
					for(int i=0; i<agentsList.get(agentCommitterByRows).size(); i++)
						committerByRowsList[i] = 0;
					
					committerByColumnsList = new int[agentsList.get(agentCommitterByColumns).size()];
					for(int i=0; i<agentsList.get(agentCommitterByColumns).size(); i++)
						committerByColumnsList[i] = 0;
					
					committerBySquaresList = new int[agentsList.get(agentCommitterBySquares).size()];
					for(int i=0; i<agentsList.get(agentCommitterBySquares).size(); i++)
						committerBySquaresList[i] = 0;
					
					switch(agentType)
					{
						case agentTesterByRows:
							isAgent = true;
							tested = committedByTesterByRows;
							break;
						case agentTesterByColumns:
							isAgent = true;
							tested = committedByTesterByColumns;
							break;
						case agentTesterBySquares:
							isAgent = true;
							tested = committedByTesterBySquares;
							break;
						case userTester:
							tested = committedByTesterByUser;
							break;
					}
					
					if (isAgent)
					{
						agentId = Integer.parseInt(vars2[0]);
						Print("Server: Agent " + agentId + " has tested correctly the position [" + conflictX + "][" + conflictY +"]. Votation for committing.");
						
						if(lastActionsList.size() == 7) {
							lastActionsList.remove(6);
						}
						
						lastAction = new int[6];
						lastAction[ID] = agentId;
						lastAction[TYPE] = agentType;
						lastAction[ACTION] = VOTING;
						lastAction[X] = conflictX;
						lastAction[Y] = conflictY;
						lastAction[VALUE] = cells[conflictX][conflictY].current;
						
						lastActionsList.add(0, lastAction);
						repaint();
					}
					else
					{
						String userName = vars2[0];
						Print("Server: " + userName + " has tested correctly the position [" + conflictX + "][" + conflictY +"]. Votation for committing.");
						//TODO: User Part for the Graphic Mode!
					}
					
					lastVotingX = conflictX;
					lastVotingY = conflictY;
					lastVotingValue = cells[conflictX][conflictY].current;
					
					votes.clear();
					votingExists = true;
					voteCountTimer = new Timer(voteCountDelay, this);
					voteCountTimer.setActionCommand("voteCount");
					voteCountTimer.start();
					
					lastVoting = tested;
					networkController.BroadcastMessage("vote#clear=" + conflictX + "," + conflictY + "," + clientHandler.clientId + "," + tested);
					
					valuesVoting++;
				}
				else {
					clientHandler.SendMessage("vote#voting exists");
				}
				
				break;
			case "voted":
				vars2 = vars[1].split(",");
				//agentId = Integer.parseInt(vars2[0]);			// For the userName.
				agentType = Integer.parseInt(vars2[1]);
				int conX =Integer.parseInt(vars2[2]);
				int conY = Integer.parseInt(vars2[3]);
				int voteVal = Integer.parseInt(vars2[4]);
				
				if (agentType == userCommitter)			//User Committer
				{
					String userName = vars2[0];
					if(!votingExists || conflictX != conX || conflictY != conY) {
						Print("Server: Received an Unexpected vote from User " + userName + " for the position [" + conX + "][" + conY +"]");
					}
					else
					{				
						if(voteVal == -1) {
							Print("Server: User " + userName + " voted to keep the Contribution at the position [" + conX + "][" + conY +"]");
						}
						else if (voteVal == 1) {
							Print("Server: User " + userName + " voted to remove the Contribution at the position [" + conX + "][" + conY +"]");
						}
					}
					
					//TODO: User Part for the Graphic Mode!
				}		
				else									//Agent Committer
				{
					agentId = Integer.parseInt(vars2[0]);
					
					if(!votingExists || conflictX != conX || conflictY != conY)
						Print("Server: Received an Unexpected vote from Agent " + agentId + " for the position [" + conX + "][" + conY +"]");
					else
					{				
						switch(agentType)
						{
							case agentCommitterByRows:
								for(int i=0; i<agentsList.get(agentCommitterByRows).size(); i++)
								{
									if (agentsList.get(agentCommitterByRows).get(i) == agentId)
										committerByRowsList[i] = voteVal;
								}
								break;
							case agentCommitterByColumns:
								for(int i=0; i<agentsList.get(agentCommitterByColumns).size(); i++)
								{
									if (agentsList.get(agentCommitterByColumns).get(i) == agentId)
										committerByColumnsList[i] = voteVal;
								}
								break;
							case agentCommitterBySquares:
								for(int i=0; i<agentsList.get(agentCommitterBySquares).size(); i++)
								{
									if (agentsList.get(agentCommitterBySquares).get(i) == agentId)
										committerBySquaresList[i] = voteVal;
								}
								break;
						}
						
						if(voteVal == -1) {
							Print("Server: Agent " + agentId + " voted to keep the Contribution at the position [" + conX + "][" + conY +"]");
						}
						else if (voteVal == 1) {
							Print("Server: Agent " + agentId + " voted to remove the Contribution at the position [" + conX + "][" + conY +"]");
						}
						
						repaint();
					}
				}
				
				votes.add(voteVal);
				
				break;
			case "accepted":
				vars2 = vars[1].split(",");
				//agentId = Integer.parseInt(vars2[0]);				//For the userName.
				agentType = Integer.parseInt(vars2[1]);
				x = Integer.parseInt(vars2[2]);
				y = Integer.parseInt(vars2[3]);
				
				int accepted = -1;
				
				switch(agentType)
				{
					case agentLeader:
						agentId = Integer.parseInt(vars2[0]);
						Print("Server: Agent " + agentId + " has accepted the value " + cells[x][y].current + " for the position [" + x + "][" + y +"]");
						
						if(lastActionsList.size() == 7) {
							lastActionsList.remove(6);
						}
						
						lastAction = new int[6];
						lastAction[ID] = agentId;
						lastAction[TYPE] = agentType;
						lastAction[ACTION] = ACCEPTED;
						lastAction[X] = x;
						lastAction[Y] = y;
						lastAction[VALUE] = cells[x][y].current;
						
						lastActionsList.add(0, lastAction);
						repaint();
						
						accepted = acceptedByAgent;
						break;
					case userLeader:
						String userName = vars2[0];
						Print("Server: User " + userName + " has accepted the value " + cells[x][y].current + " for the position [" + x + "][" + y +"]");
						
						accepted = acceptedByUser;
						
						//TODO: User Part for the Graphic Mode!
						break;
				}
				
				SetValueAndState(x, y, cells[x][y].current, accepted);		
				networkController.BroadcastMessage("accepted#" + x + "," + y + "," + accepted);
				valuesAccepted++;
				
				break;
			case "rejected":
				vars2 = vars[1].split(",");
				//agentId = Integer.parseInt(vars2[0]);
				agentType = Integer.parseInt(vars2[1]);
				x = Integer.parseInt(vars2[2]);
				y = Integer.parseInt(vars2[3]);
				
				int rejected = -1;
				
				switch(agentType)
				{
					case agentLeader:
						agentId = Integer.parseInt(vars2[0]);
						Print("Server: Agent " + agentId + " has rejected the value " + cells[x][y].current + " for the position [" + x + "][" + y +"]");
						
						if(lastActionsList.size() == 7) {
							lastActionsList.remove(6);
						}
						
						lastAction = new int[6];
						lastAction[ID] = agentId;
						lastAction[TYPE] = agentType;
						lastAction[ACTION] = REJECTED;
						lastAction[X] = x;
						lastAction[Y] = y;
						lastAction[VALUE] = cells[x][y].current;
						
						lastActionsList.add(0, lastAction);
						repaint();
						
						rejected = rejectedByAgent;
						break;
					case userLeader:
						String userName = vars2[0];
						Print("Server: User " + userName + " has rejected the value " + cells[x][y].current + " for the position [" + x + "][" + y +"]");
						
						rejected = rejectedByUser;
						
						//TODO: User Part for the Graphic Mode!
						break;
				}
				
				ClearCell(x, y, rejected);
				networkController.BroadcastMessage("rejected#" + x + "," + y + "," + rejected);
				valuesRejected++;
				
				break;
			}

		} catch (Exception e) 
		{
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
		
		boolean resultVotation = sum > 0;
		
		if (resultVotation)
			Print("Server: The committers have decided to remove the value by votation.");
		else
			Print("Server: The committers have decided to keep the value by votation.");
		
		return resultVotation;
	}
	
	int getNumMembers() {
		
		int numMembers = 0;
		
		agentIdList = new ArrayList<Integer>();
		
		for (int i=0; i<agentsList.size(); i++) {
			agentIdList = agentsList.get(i);
			for (int j=0; j<agentIdList.size(); j++) {
				numMembers++;
			}
		}
		
		//TODO: Falta Sumar els Users
		return numMembers;
	}
}