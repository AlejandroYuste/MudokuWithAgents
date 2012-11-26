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
	
	protected enum ActualRol {Observer, Contributor, Tester, Committer, Leader}
	protected ActualRol actualRol;

	Button connectClientButton;
	Button joinCommunity;
	Button getPromotedTester;
	Button askForCommiting;
	Button removeValue;
	Button getPromotedCommitter;
	Button voteCommiting;
	Button voteRemove;
	Button getPromotedLeader;
	Button acceptValueLeader;
	Button removeValueLeader;
	
	Button getContributor;
	Button getTester;
	Button getCommitter;
	Button getLeader;
	
	
	Label Title;
	Label ipFieldLabel;
	Label portFieldLabel;
	Label ipFieldDoubtLabel;
	Label informationL1Label;
	Label informationL2Label;
	Label informationConnectionLabel;
	Label logLabel;
	Label actualRolLabel;
	Label userNameLabel;
	
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
	Label emptyCountFreeLabel;
	Label positionCountFreeLabel;
	Label countFreeLabel;
	
	Label rolInformation;
	Label rolL1Information;
	Label rolL2Information;
	Label rolL3Information;
	
	List console;
	TextField ipField;
	TextField portField;
	TextField userNameField;
	
	String userName;
	
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
	
	static Color[] clientColors;		// = new Color[]{Color.green, Color.blue, Color.yellow, Color.ORANGE, Color.gray};
	
	boolean contributor = false;
	boolean tester = false;
	boolean committer = false;
	boolean leader = false;
	
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
		connectClientButton.setSize(150,75);
		connectClientButton.setLocation(300, 200);
		connectClientButton.setActionCommand("connect");
		connectClientButton.addActionListener(this);				//Afegim el Listener al button "Connect"
		add(connectClientButton);
		
		joinCommunity = new Button("Join to the Community");
		joinCommunity.setSize(180,40);
		joinCommunity.setLocation(530, 100);
		joinCommunity.setActionCommand("joinCommunity");
		joinCommunity.addActionListener(this);				//Afegim el Listener al button "Connect"
		joinCommunity.setVisible(false);
		add(joinCommunity);
		
		getPromotedTester = new Button("Get Promoted!");
		getPromotedTester.setSize(180,40);
		getPromotedTester.setLocation(530, 250);
		getPromotedTester.setActionCommand("getTester");
		getPromotedTester.addActionListener(this);				//Afegim el Listener al button "Connect"
		getPromotedTester.setVisible(false);
		add(getPromotedTester);
		
		getPromotedCommitter = new Button("Get Promoted!");
		getPromotedCommitter.setSize(180,40);
		getPromotedCommitter.setLocation(530, 250);
		getPromotedCommitter.setActionCommand("getCommitter");
		getPromotedCommitter.addActionListener(this);				//Afegim el Listener al button "Connect"
		getPromotedCommitter.setVisible(false);
		add(getPromotedCommitter);
		
		getPromotedLeader = new Button("Get Promoted!");
		getPromotedLeader.setSize(180,40);
		getPromotedLeader.setLocation(530, 250);
		getPromotedLeader.setActionCommand("getLeader");
		getPromotedLeader.addActionListener(this);				//Afegim el Listener al button "Connect"
		getPromotedLeader.setVisible(false);
		add(getPromotedLeader);
		
		askForCommiting = new Button("Ask for Committing");
		askForCommiting.setSize(180,30);
		askForCommiting.setLocation(530, 170);
		askForCommiting.setActionCommand("askCommitting");
		askForCommiting.addActionListener(this);				//Afegim el Listener al button "Connect"
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
		
		
		// Labels From Here
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
		
		userNameLabel = new Label("Select an User Name:");
		userNameLabel.setSize(180,20);
		userNameLabel.setLocation(20, 440);
		add(userNameLabel);
		
		//-----------------------------------------------------------------
		
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
		
		userNameField = new TextField(15);
		userNameField.setSize(100,20);
		userNameField.setLocation(200, 440);
		userNameField.setText("Default User");
		add(userNameField);
		
		console = new List();
		console.setLocation(20, 495);
		console.setSize(520, 95);
		console.setVisible(false);
		add(console);
		
		networkController = new ClientNetworkController(this);
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
			//gr.setColor(clientColors[clearRequester]);
			//gr.drawString("Clear Requester", gridEndX + 20, gridYOffset + 100);
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
							gr.setColor(clientColors[4]);			//Value Committed
							clearRequester = 4;
							break;
						case 7:
							gr.setColor(clientColors[5]);			//Value Accepted
							clearRequester = 5;
							break;
					}
					
					if(conflictExists  && x == conflictX && y == conflictY)
					{
						int conX = (int) (gridXOffset + conflictX * deltaX);
						int conY = (int) (gridYOffset + conflictY * deltaY);
						
						//setActive(conflictX, conflictY);						//It's necessary if we want the user block during votations
						cells[activeX][activeY].DrawClientDomainConflict(gr, mouseOverDomainIndex, conX, conY, conflictX, conflictY, clientColors, actualRol);
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

		if (mouseOverGrid) 
		{
			gr.setColor(mouseOverColor);
			gr.drawRect((int) (gridXOffset + mouseOverX * deltaX), (int) (gridYOffset + mouseOverY * deltaY), (int) deltaX, (int) deltaY);
		}
		else if (mouseOverDomain && actualRol == ActualRol.Contributor)
		{
			gr.setColor(Color.black);
			gr.drawRect((int) (gridXOffset + mouseOverDomainIndex * deltaX), (int) (170 + gridYOffset * deltaY), (int) deltaX, (int) deltaY);
		}

		gr.setColor(activeCellColor);
		int activeRectX = (int) (gridXOffset + activeX * deltaX);
		int activeRectY = (int) (gridYOffset + activeY * deltaY);

		// DrawActive
		gr.drawRect(activeRectX, activeRectY, (int) deltaX, (int) deltaY);
	}

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
				if(!ipField.equals("") && !portField.equals(""))
				{
					try {
						networkController.Connect(ipField.getText(), Integer.parseInt(portField.getText()));
						userName = userNameField.getText();
						RequestInit();
					} catch (IOException e) {
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
				voteRemove.setVisible(false);
				voteCommiting.setVisible(false);
				voteTimer.stop();
				//repaint();
				break;
			case "yes":
				if(conflictExists)
				{
					networkController.SendMessage("voted#" + userName + ",-1," + conflictX + "," + conflictY + "," + "1");
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
					networkController.SendMessage("voted#" + userName + ",-1," + conflictX + "," + conflictY + "," + "-1");
					voteRemove.setVisible(false);
					voteCommiting.setVisible(false);
					
					positionVoted = new int[2];
					positionVoted[0] = conflictX;
					positionVoted[1] = conflictY;
					positionVotedNegativelyList.add(positionVoted);
				}
				break;
			case "askCommitting":
				networkController.SendMessage("clear#" + userName + ",-1," + activeX + "," + activeY);
				
				positionCommitted = new int[2];
				positionCommitted[0] = activeX;
				positionCommitted[1] = activeY;
				positionCommittedList.add(positionCommitted);
				
				break;
			case "removeValue":
				networkController.SendMessage("testerClear#" + userName + ",-1," + activeX + "," + activeY);
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
				networkController.SendMessage("accepted#" + activeX + "," + activeY);
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
	
	public void joinCommunity()
	{
		actualRol = ActualRol.Contributor;
		
		contributor = true;
		getContributor.setVisible(false);
		
		if(tester)
			getTester.setVisible(true);
		if(committer)
			getCommitter.setVisible(true);
		if(leader)
			getLeader.setVisible(true);
		
		
		joinCommunity.setVisible(false);
		actualRolLabel.setLocation(550, 20);
		
		actualRolLabel.setText("Actual Rol: " + actualRol);
		rolInformation.setVisible(true);
		rolL1Information.setVisible(true);
		rolL2Information.setVisible(true);
		rolL3Information.setVisible(true);
		repaint();
	}
	
	public void getTester()
	{
		actualRol = ActualRol.Tester;
		
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
		
		actualRolLabel.setLocation(560, 20);
		actualRolLabel.setText("Actual Rol: " + actualRol);
		repaint();
	}

	public void getCommitter()
	{
		actualRol = ActualRol.Committer;
		
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

		actualRolLabel.setLocation(560, 20);
		actualRolLabel.setText("Actual Rol: " + actualRol);
		repaint();
	}

	public void getLeader()
	{
		actualRol = ActualRol.Leader;
		
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

		actualRolLabel.setLocation(560, 20);
		actualRolLabel.setText("Actual Rol: " + actualRol);
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
			
			if (actualRol == ActualRol.Contributor && cells[activeX][activeY].valueState == 0)
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
			if (actualRol == ActualRol.Tester && !conflictExists)
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
			else if (actualRol == ActualRol.Leader && !conflictExists)
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
						
						if(actualRol == ActualRol.Committer)
						{
							voteCommiting.setVisible(true);
							voteRemove.setVisible(true);
						}
						else if (actualRol == ActualRol.Tester)
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
				ClearCell(conflictX, conflictY);
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
				
				if (actualRol == ActualRol.Contributor)
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
				else if (actualRol == ActualRol.Tester)
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
				else if (actualRol == ActualRol.Committer)
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
		connectClientButton.setVisible(false);
		ipField.setVisible(false);
		portField.setVisible(false);
		informationL1Label.setVisible(false);
		informationL2Label.setVisible(false);
		informationConnectionLabel.setVisible(false);
		ipFieldLabel.setVisible(false);
		ipFieldDoubtLabel.setVisible(false);
		portFieldLabel.setVisible(false);
		userNameField.setVisible(false);
		userNameLabel.setVisible(false);
		
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
		valuesContributedLabel.setVisible(true);
		valuesCommittedLabel.setVisible(true);
		contributedLabel.setVisible(true);
		committedLabel.setVisible(true);
		countContributedLabel.setVisible(true);
		countCommittedLabel.setVisible(true);
		emptyCountFreeLabel.setVisible(true);
		positionCountFreeLabel.setVisible(true);
		countFreeLabel.setVisible(true);
		
		joinCommunity.setVisible(true);
		
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
}