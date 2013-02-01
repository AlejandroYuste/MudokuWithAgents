import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GameController extends Applet implements ActionListener {

	public enum GameState {start, initGame, pregame, game, conflictResolution}
	protected GameState state;

	/* cellState = 0 --> waitingValue
	 * cellState = 1 --> initializedByServer
	 * cellState = 2 --> Contributed By Rows
	 * cellState = 3 --> Contributed By Columns
	 * cellState = 4 --> Contributed By Squares
	 * cellState = 5 --> Contributed By User
	 * cellState = 6 --> Reported By Rows
	 * cellState = 7 --> Reported By Columns
	 * cellState = 8 --> Reported By Squares
	 * cellState = 9 --> Reported By Users
	 * cellState = 10 --> Committed By Rows
	 * cellState = 11 --> Committed By Columns
	 * cellState = 12 --> Committed By Squares
	 * cellState = 13 --> Committed By User
	 * cellState = 14 --> Not Committed
	 * cellState = 15 --> Accepted By Agent
	 * cellState = 16 --> Accepted By User
	 * cellState = 17 --> Rejected By Agent
	 * cellState = 18 --> Rejected By User
	 */
	
	public final static int waitingValue = 0;
	public final static int intializedByServer = 1;
	public final static int contributedByRows = 2;
	public final static int contributedByColumns = 3;
	public final static int contributedBySquares = 4;
	public final static int contributedByUser = 5;
	public final static int reportedByRows = 6;
	public final static int reportedByColumns = 7;
	public final static int reportedBySquares = 8;	
	public final static int reportedByUser = 9;	
	public final static int committedByTesterByRows = 10;	
	public final static int committedByTesterByColumns = 11;	
	public final static int committedByTesterBySquares = 12;	
	public final static int committedByTesterByUser = 13;	
	public final static int notCommitted = 14;	
	public final static int acceptedByAgent = 15;
	public final static int acceptedByUser = 16;	
	public final static int rejectedByAgent = 17;	
	public final static int rejectedByUser = 18;	
	
	/* AgentType:
	    0 --> Contributor by Rows
		1 --> Contributor by Columns
		2 --> Contributor by Squares
		4 --> Bug Reporter by Rows
		5 --> Bug Reporter by Columns
		6 --> Bug Reporter by Squares
		8 --> Tester by Rows
		9 --> Tester by Columns
		10 --> Tester by Squares
		12 --> Committer by Rows
		13 --> Committer by Columns
		14 --> Committer by Squares
		16 --> Project Leader 
	 */
	
	public final static int agentContributorByRows = 0;
	public final static int agentContributorByColumns = 1;
	public final static int agentContributorBySquares = 2;
	public final static int userContributor = 3;
	public final static int agentBugReporterByRows = 4;
	public final static int agentBugReporterByColumns = 5;
	public final static int agentBugReporterBySquares = 6;
	public final static int userBugReporter = 7;	
	public final static int agentTesterByRows = 8;	
	public final static int agentTesterByColumns = 9;	
	public final static int agentTesterBySquares = 10;	
	public final static int userTester = 11;	
	public final static int agentCommitterByRows = 12;	
	public final static int agentCommitterByColumns = 13;
	public final static int agentCommitterBySquares = 14;	
	public final static int userCommitter = 15;	
	public final static int agentLeader = 16;
	public final static int userLeader = 17;
	public final static int passiveUser = 18;
	
	public final static int NO_TYPE = -1;
	
	public final static int ID = 0;
	public final static int TYPE = 1;
	public final static int ACTION = 2;
	public final static int X = 3;
	public final static int Y = 4;
	public final static int VALUE = 5;
	public final static int RESULT_VOTING = 0;
	public final static int NEW_TYPE = 5;
	
	public final static int CONTRIBUTION = 0;
	public final static int BUG_REPORTED = 1;
	public final static int VOTING = 2;
	public final static int COMMITTED = 3;
	public final static int NOT_COMMITTED = 4;
	public final static int ACCEPTED = 5;
	public final static int REJECTED = 6;
	public final static int NEW_GAME = 7;
	public final static int PROMOTION = 8;
	
	private static final long serialVersionUID = 1L;
	static int screenWidth = 765;
	static int screenHeight = 600;
	static int sudokuSize;

	static int gridWidth = 400;
	static int gridHeight = 400;
	static int gridXOffset = 20;
	static int gridYOffset = 10;
	
	static int numberInstantiateX = 510;
	static int numberInstantiateY = 210;

	static int gridEndX = gridXOffset + gridWidth;
	static int gridEndY = gridYOffset + gridHeight;

	static Color mouseOverColor = new Color(0, 0, 205);						//	Color to move over the grid
	static Color activeBackgroundColor = new Color(238, 233, 191);			//	Color of the Background Active
	static Color activeCellColor = new Color(48, 128, 20);				//	Color of the Active Cell

	int activeX;
	int activeY;
	int votingX;
	int votingY;
	int mouseOverX;
	int mouseOverY;
	int mouseOverDomainIndex;
	boolean mouseOverDomain;
	boolean mouseOverGrid;
	
	static boolean userConnected = false;

	static float deltaX;
	static float deltaY;

	CellVariable[][] cells;

	static SudokuCPController cpController;
	static SudokuMouseController mouseController;

	// flicker solution
	private Image offScreenImage;
	private Dimension offScreenSize;
	private Graphics offScreenGraphics;

	public GameController() {
		cpController = new SudokuCPController();
		mouseController = new SudokuMouseController(this);
		state = GameState.start;
	}

	public void Initialize() 
	{
		deltaX = (float) gridWidth / sudokuSize;
		deltaY = (float) gridHeight / sudokuSize;
		CellVariable.deltaX = deltaX;
		CellVariable.deltaY = deltaY;
		CellVariable.domainXOffset = gridXOffset;
		CellVariable.domainYOffset = gridEndY + 10;

		activeX = 0;
		activeY = 0;
		mouseOverX = -1;
		mouseOverY = -1;
		mouseOverDomain = false;
		mouseOverGrid = false;

		cells = new CellVariable[sudokuSize][sudokuSize];
		for (int i = 0; i < sudokuSize; i++) {
			for (int j = 0; j < sudokuSize; j++) {
				cells[i][j] = new CellVariable(i, j);
			}
		}

		addMouseListener(mouseController);					//Afegim el Listener del Mouse
		addMouseMotionListener(mouseController);			//Afegim el Listener pel moviment del Mouse
		mouseController.init();
		cpController.InitCP();
	}

	// Instantiate "count" number of variables randomly
	public void RandomAssign(int count) 
	{
		/*int x = 0;
		int y = 0;
		int val = 0;
		
		Random random = new Random(System.nanoTime());
		while (count > 0) {
			x = random.nextInt(sudokuSize);
			y = random.nextInt(sudokuSize);

			// System.out.println(x + " , " + y);
			/*if (cells[x][y].current == -1) {
				val = cpController.GetCPVariable(x, y).getRandomDomainValue();
				cells[x][y].current = val;
				count--;
				
				if (TryInstantiate(x, y, val)) {
					
				}
				else
				{
					return;
				}
			}
		}*/
	}
	
	//Initializes a new random problem
	public void InitializeRandomProblem(int count) {
		int x = 0;
		int y = 0;
		int val = 0;
		
		
		
		for (int i=0; i<sudokuSize; i++) {						//Reset the whole Grid!
			for (int j=0; j<sudokuSize; j++) {
				SetValueAndState(i, j, -1, waitingValue);
			}
		}
		
		Random random = new Random(System.nanoTime());
		
		while (count > 0) 
		{
			x = random.nextInt(sudokuSize);
			y = random.nextInt(sudokuSize);

			if (cells[x][y].valueState == waitingValue) 
			{
				val = random.nextInt(sudokuSize) + 1;			//Random Values at random positions!
				
				if (checkPosition(x, y, val)) 
				{
					count--;
					SetValueAndState(x, y, val, intializedByServer);
					cells[x][y].IsConstant();
				}
			}
		}
	}

	boolean checkPosition(int x, int y, int val)
	{
		int i, j;
		
		ArrayList<Integer> number = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> listNumbers = new ArrayList<ArrayList<Integer>>();
				
		for(i=0;i<sudokuSize;i++)
		{
			for(j=0;j<sudokuSize;j++)
			{
				if(cells[i][j].valueState != waitingValue)
					number.add(cells[i][j].current);
			}
			listNumbers.add(number);
			number = new ArrayList<Integer>();
		}
			
		number = listNumbers.get(x);
		if(number.contains(val))
		{
			return false;
		}
		
		number = new ArrayList<Integer>();
		listNumbers = new ArrayList<ArrayList<Integer>>();
				
		for(j=0;j<sudokuSize;j++)
		{
			for(i=0;i<sudokuSize;i++)
			{
				if(cells[i][j].valueState != waitingValue)
					number.add(cells[i][j].current);
			}
			listNumbers.add(number);
			number = new ArrayList<Integer>();
		}
		
		number = listNumbers.get(y);
		if(number.contains(val))
		{
			return false;
		}
			
		number = new ArrayList<Integer>();
		listNumbers = new ArrayList<ArrayList<Integer>>();
		
		int sizeSquare = (int) Math.sqrt(sudokuSize);
		
		for (int row=0; row<sudokuSize;row+=sizeSquare)
		{
			for (int column=0; column<sudokuSize;column+=sizeSquare)
			{
				for(i=0;i<sizeSquare;i++)
				{
					for(j=0;j<sizeSquare;j++)
					{
						if(cells[i + row][j + column].valueState != waitingValue)
							number.add(cells[i + row][j + column].current);
					}
				}
				listNumbers.add(number);
				number = new ArrayList<Integer>();
			}
		}
				
		int region = getRegion(x, y);
		number = listNumbers.get(region);
		
		if(number.contains(val))
		{
			return false;
		}
		
		
		return true;
	}
	
	int getRegion(int x, int y)
	{
		int region = 0;
		
		if(x>=0 && x<4)
		{
			if (0<=y && y<4)
				region = 0;
			if (4<=y && y<8)
				region = 1;
			if (8<=y && y<12)
				region = 2;
			if (12<=y && y<16)
				region = 3;
		}
		else if (x>=4 && x<8)
		{
			if (0<=y && y<4)
				region = 4;
			if (4<=y && y<8)
				region = 5;
			if (8<=y && y<12)
				region = 6;
			if (12<=y && y<16)
				region = 7;
		}
		else if (x>=8 && x<12)
		{
			if (0<=y && y<4)
				region = 8;
			if (4<=y && y<8)
				region = 9;
			if (8<=y && y<12)
				region = 10;
			if (12<=y && y<16)
				region = 11;
		}
		else if (x>=12 && x<16)
		{
			if (0<=y && y<4)
				region = 12;
			if (4<=y && y<8)
				region = 13;
			if (8<=y && y<12)
				region = 14;
			if (12<=y && y<16)
				region = 15;
		}
		
		return region;
	}
	
	public void StartGame() {
		state = GameState.game;
	}

	//Called by applet engine
	public void init() {
		// Applet size
		setSize(screenWidth, screenHeight);
		this.setLayout(null);
		//setLayout(null);
	}

	//Called by applet engine
	public void destroy() {}
	
	//Called by applet engine
	public void initDraw(Graphics gr) 
	{
		gr.setFont(new Font("Calibri", Font.BOLD, 12));
		gr.setColor(Color.white);
		gr.fillRect(0, 0, screenWidth, screenHeight);
	}

	//Called by applet engine per frame
	@Override
	public final synchronized void update(Graphics g) {
		Dimension d = getSize();
		
		if ((offScreenImage == null) || (d.width != offScreenSize.width)
				|| (d.height != offScreenSize.height)) {
			offScreenImage = createImage(d.width, d.height);
			offScreenSize = d;
			offScreenGraphics = offScreenImage.getGraphics();
		}
		
		offScreenGraphics.clearRect(0, 0, d.width, d.height);
		paint(offScreenGraphics);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	@Override
	public void paint(Graphics gr) {
		//initDraw(gr);
		//DrawGrid(gr);
	}

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

		stroke = new BasicStroke(1);
		((Graphics2D) gr).setStroke(stroke);
		lineX = gridXOffset;
		lineY = gridYOffset;
		tempLineY = lineY;
		tempLineX = lineX;
		// Draw Values
		for (int y = 0; y < sudokuSize; y++) {
			for (int x = 0; x < sudokuSize; x++) {
				if (cells[x][y].valueState == waitingValue) {
					if (cpController.GetCPVariable(x, y).getDomainSize() == 1) {
						gr.drawString("*", (int) (lineX + deltaX / 2) - 5, (int) (lineY + deltaY) - 10);
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
					gr.setColor(Color.black);
				}
				
				gr.drawString(String.valueOf(cpController.GetCPVariable(x, y)
						.getVal()), (int) (lineX + deltaX / 2) - 5,
						(int) (lineY + deltaY) - 10);
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

		//cells[activeX][activeY].DrawDomainA(gr, mouseOverDomainIndex, activeX, activeY);

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

	//Handles button clicks
	@Override
	public void actionPerformed(ActionEvent action) 
	{
		System.out.println("GameController --> actionPerformed. Accio: " + action.getActionCommand());
		switch (action.getActionCommand()) {
		case "sweep":
			repaint();
			break;
		}
	}
	
	public void ClearCell(int x, int y, int cellState)
	{
		if(cells[x][y].IsConstant())
		{
			return;
		}
		
		SetValueAndState(x, y, cells[x][y].current, cellState);	
		repaint();
	}
	
	public void SetValueAndState(int x, int y, int value, int state) 
	{
		cells[x][y].current = value;
		cells[x][y].valueState = state;
	}

	public void MouseOverCell(int x, int y) 
	{
		mouseOverGrid = true;
		mouseOverDomain = false;
		mouseOverDomainIndex = -1;

		mouseOverX = x;
		mouseOverY = y;
		repaint();
	}

	public void MouseOverDomain(int index) {
		mouseOverDomain = true;
		mouseOverGrid = false;
		mouseOverDomainIndex = index;
		
		repaint();
	}

	public void CellClick(int x, int y) {
		activeX = x;
		activeY = y;
		
		mouseOverX = -1;
		mouseOverY = -1;
		
		repaint();
	}

	public void DomainClick(int index) 		
	{
		/*IntDomain idom = cpController.GetCPVariable(activeX, activeY) .getDomain();
		DisposableIntIterator iter = idom.getIterator();
		int val = 1;
		for (; index >= 0 && iter.hasNext(); index--) {
			val = iter.next();
		}

		if(!TryInstantiate(activeX, activeY, val))
		{
			System.out.println("domain click failed");
		}
		
		repaint();*/
	}

	public void DomainWipeOut() 
	{
		for (int i = 0; i < sudokuSize; i++) {
			for (int k = 0; k < sudokuSize; k++) {
				if (cells[i][k].valueState == waitingValue) {
					if (cpController.GetCPVariable(i, k).getDomainSize() == 0) {
						System.out.println("domain wipe out at " + i + "," + k);
					}
				}
			}
		}
	}

	public void MouseOverEmpty() {
		mouseOverGrid = false;
		mouseOverDomain = false;
		mouseOverDomainIndex = -1;
	}
	
	int getCellState(int x, int y)
	{
		return cells[x][y].valueState;
	}
	
	int getCountCorrect()
	{
		int count = 0;
		
		for(int i=0;i<sudokuSize;i++) {
			for(int j=0;j<sudokuSize;j++) {
				if(cells[i][j].valueState == intializedByServer || cells[i][j].valueState == acceptedByAgent || cells[i][j].valueState == acceptedByUser)
					count++;
			}	
		}
		
		return count;
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
	
	public boolean checkCorrectPosition(int x, int y, int val)
	{
		int i, j;
		
		int[][] actualGrid = getActualGrid();
		int[][] actualState = getActualState();
		
		ArrayList<Integer> number = new ArrayList<Integer>();
		
		for(i=0;i<AgentNetworkController.getSudokuSize();i++)
		{
			if((actualState[i][y] != GameController.waitingValue && actualState[i][y] != GameController.reportedByRows && 
			    actualState[i][y] != GameController.reportedByColumns &&  actualState[i][y] != GameController.reportedBySquares &&
			    actualState[i][y] != GameController.reportedByUser &&  actualState[i][y] != GameController.rejectedByAgent &&
			    actualState[i][y] != GameController.rejectedByUser) && x != i)
			{
				number.add(actualGrid[i][y]);
			}
		}
			
		if(number.contains(val))
		{
			return false;
		}
		
		number = new ArrayList<Integer>();
				
		for(i=0;i<AgentNetworkController.getSudokuSize();i++)
		{
			if((actualState[x][i] != GameController.waitingValue && actualState[x][i] != GameController.reportedByRows && 
			    actualState[x][i] != GameController.reportedByColumns &&  actualState[x][i] != GameController.reportedBySquares &&
			    actualState[x][i] != GameController.reportedByUser &&  actualState[x][i] != GameController.rejectedByAgent &&
			    actualState[x][i] != GameController.rejectedByUser) && y != i)
			{
				number.add(actualGrid[x][i]);
			}
		}
		
		if(number.contains(val))
		{
			return false;
		}
			
		number = new ArrayList<Integer>();
		
		int sizeSquare = (int) Math.sqrt(AgentNetworkController.getSudokuSize());
		int[] region = getRegionCheckPosition(x, y);
		
		for(i=0;i<sizeSquare;i++)
		{
			for(j=0;j<sizeSquare;j++)
			{
				if((actualState[i + region[0]][j + region[1]] != GameController.waitingValue && actualState[i + region[0]][j + region[1]] != GameController.reportedByRows && 
				    actualState[i + region[0]][j + region[1]] != GameController.reportedByColumns &&  actualState[i + region[0]][j + region[1]] != GameController.reportedBySquares &&
				    actualState[i + region[0]][j + region[1]] != GameController.reportedByUser &&  actualState[i + region[0]][j + region[1]] != GameController.rejectedByAgent &&
				    actualState[i + region[0]][j + region[1]] != GameController.rejectedByUser) && x != (i + region[0]) && y != (j + region[1]))
				{
					number.add(actualGrid[i + region[0]][j + region[1]]);
				}
			}
		}
		
		if(number.contains(val))
		{
			return false;
		}
		
		return true;
	}
	
	static int[] getRegionCheckPosition(int x, int y)
	{
		int[] region = new int[2];
		
		if(x>=0 && x<4)
		{
			if (0<=y && y<4)
			{
				region[0] = 0;
				region[1] = 0;
			}
			if (4<=y && y<8)
			{
				region[0] = 0;
				region[1] = 4;
			}
			if (8<=y && y<12)
			{
				region[0] = 0;
				region[1] = 8;
			}
			if (12<=y && y<16)
			{
				region[0] = 0;
				region[1] = 12;
			}
		}
		else if (x>=4 && x<8)
		{
			if (0<=y && y<4)
			{
				region[0] = 4;
				region[1] = 0;
			}
			if (4<=y && y<8)
			{
				region[0] = 4;
				region[1] = 4;
			}
			if (8<=y && y<12)
			{
				region[0] = 4;
				region[1] = 8;
			}
			if (12<=y && y<16)
			{
				region[0] = 4;
				region[1] = 12;
			}
		}
		else if (x>=8 && x<12)
		{
			if (0<=y && y<4)
			{
				region[0] = 8;
				region[1] = 0;
			}
			if (4<=y && y<8)
			{
				region[0] = 8;
				region[1] = 4;
			}
			if (8<=y && y<12)
			{
				region[0] = 8;
				region[1] = 8;
			}
			if (12<=y && y<16)
			{
				region[0] = 8;
				region[1] = 12;
			}
		}
		else if (x>=12 && x<16)
		{
			if (0<=y && y<4)
			{
				region[0] = 12;
				region[1] = 0;
			}
			if (4<=y && y<8)
			{
				region[0] = 12;
				region[1] = 4;
			}
			if (8<=y && y<12)
			{
				region[0] = 12;
				region[1] = 8;
			}
			if (12<=y && y<16)
			{
				region[0] = 12;
				region[1] = 12;
			}
		}
		
		return region;
	}
}