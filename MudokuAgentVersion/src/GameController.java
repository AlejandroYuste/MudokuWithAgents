import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.variables.integer.IntDomain;

public class GameController extends Applet implements ActionListener {

	public enum GameState {
		start, initGame, pregame, game, conflictResolution
	}

	protected GameState state;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int screenWidth = 650;
	static int screenHeight = 600;
	static int sudokuSize = 9;

	static int gridWidth = 400;
	static int gridHeight = 400;
	static int gridXOffset = 20;
	static int gridYOffset = 10;

	static int gridEndX = gridXOffset + gridWidth;
	static int gridEndY = gridYOffset + gridHeight;

	static Color mouseOverColor = Color.green;
	static Color activeCellColor = Color.blue;

	int activeX;
	int activeY;
	int mouseOverX;
	int mouseOverY;
	int mouseOverDomainIndex;
	boolean mouseOverDomain;
	boolean mouseOverGrid;

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

	public void Initialize() {
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
	public void RandomAssign(int count) {
		int x = 0;
		int y = 0;
		int val = 0;
		Random random = new Random(System.nanoTime());
		while (count > 0) {
			x = random.nextInt(sudokuSize);
			y = random.nextInt(sudokuSize);

			// System.out.println(x + " , " + y);
			if (cells[x][y].current == -1) {
				val = cpController.GetCPVariable(x, y).getRandomDomainValue();

				if (TryInstantiate(x, y, val)) {
					count--;
				}
				else
				{
					return;
				}
			}
		}
	}
	
	//Initializes a new random problem
	public void InitializeRandomProblem(int count) {
		int x = 0;
		int y = 0;
		int val = 0;
		Random random = new Random(System.nanoTime());
		while (count > 0) {
			x = random.nextInt(sudokuSize);
			y = random.nextInt(sudokuSize);

			System.out.println(x + " , " + y);
			if (cells[x][y].current == -1) {
				val = cpController.GetCPVariable(x, y).getRandomDomainValue();
				if (TryInstantiate(x, y, val)) {
					count--;
					cells[x][y].SetConstant();
				}
			}
		}
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
	public void initDraw(Graphics gr) {
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
		// initDraw(gr);
		// DrawGrid(gr);
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
				if (cells[x][y].current == -1) {
					if (cpController.GetCPVariable(x, y).getDomainSize() == 1) {
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

		cells[activeX][activeY].DrawDomain(gr, mouseOverDomainIndex);

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
	public void actionPerformed(ActionEvent action) {
		// TODO Auto-generated method stub
		System.out.println("GameController --> actionPerformed. Accio: " + action.getActionCommand());
		switch (action.getActionCommand()) {
		case "sweep":
			Sweep();
			repaint();
			break;
		}
	}
	
	public void ClearCell(int x, int y)
	{
		if(cells[x][y].IsConstant())
		{
			return;
		}
		cells[x][y].current = -1;
		try {
			cpController.Refresh(cells);
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			System.out.println("refresh failed");
			e.printStackTrace();
		}
		repaint();
	}
	public void Sweep() {
		for (int i = 0; i < sudokuSize; i++) {
			for (int k = 0; k < sudokuSize; k++) {
				if (cells[i][k].current == -1) {
					if (cpController.GetCPVariable(i, k).getDomainSize() == 1) {
						TryInstantiate(i, k, cpController.GetCPVariable(i, k)
								.getDomain().getIterator().next());
					}
				}
			}
		}
	}

	//Instantiates active cell with given value
	public void SetValue(int value) {
		cells[activeX][activeY].current = value;
		try {
			cpController.InstantiateVar(activeX, activeY, value);// setVal(selectedValue);
			cpController.Propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean TryInstantiate(int x, int y, int value) {
		boolean success = true;
		cpController.solver.worldPush();

		try {
			cpController.InstantiateVar(x, y, value);
		} catch (ContradictionException e1) {
			// TODO Auto-generated catch block
			System.out.println("instantiation failed : " + e1.toString());
			success = false;
		}
		if (success) {
			try {
				cpController.Propagate();
			} catch (ContradictionException e) {
				// TODO Auto-generated catch block
				System.out.println("propagation failed : "
						+ e.getLocalizedMessage().toString());
				DomainWipeOut();
				success = false;
				cpController.solver.worldPop();
			}
		}
		if (success) {
			cells[x][y].current = value;
			cells[x][y].contradicting = false;
		}
		else
		{
			System.out.println("contradicted");
			cells[x][y].contradicting = true;
			cells[x][y].current = -1;
		}
		return success;
	}

	public void SetValue(int x, int y, int value) {
		cells[x][y].current = value;
		try {
			cpController.InstantiateVar(x, y, value);// setVal(selectedValue);
			// cpController.Propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void MouseOverCell(int x, int y) {
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
		repaint();
	}

	public void DomainClick(int index) {
		IntDomain idom = cpController.GetCPVariable(activeX, activeY)
				.getDomain();
		DisposableIntIterator iter = idom.getIterator();
		int val = 1;
		for (; index >= 0 && iter.hasNext(); index--) {
			val = iter.next();
		}

		if(!TryInstantiate(activeX, activeY, val))
		{
			System.out.println("domain click failed");
		}
		
		repaint();
	}

	public void DomainWipeOut() {
		for (int i = 0; i < sudokuSize; i++) {
			for (int k = 0; k < sudokuSize; k++) {
				if (cells[i][k].current == -1) {
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
}