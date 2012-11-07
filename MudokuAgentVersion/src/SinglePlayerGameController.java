import java.awt.*;
import java.awt.event.*;
import choco.kernel.solver.ContradictionException;

public class SinglePlayerGameController extends GameController implements ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Button sweepButton;
	Button randomButton;
	Button clearButton;
	
	public SinglePlayerGameController()
	{
		super();
		GameController.sudokuSize = 16;
	}

	public void init()
	{
		super.init();

		Initialize();
		InitializeRandomProblem(36);
		
		//Create Clear Button to uninstantiate a variable
		clearButton = new Button("Clear");
		clearButton.setLocation(gridXOffset, CellVariable.domainYOffset + (int)deltaY + 5);
		clearButton.setSize(70,20);
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(this);

		clearButton.setLocation(gridXOffset, CellVariable.domainYOffset + (int)deltaY + 5);
		
		add(clearButton);
		
		sweepButton = new Button("Sweep");
		sweepButton.setLocation(gridXOffset + gridWidth + 5, gridYOffset + 30);
		sweepButton.setSize(70,20);
		sweepButton.setActionCommand("sweep");
		sweepButton.addActionListener(this);
		add(sweepButton);
		
		randomButton = new Button("Random");
		randomButton.setLocation(gridXOffset + gridWidth + 5, gridYOffset + 60);
		randomButton.setSize(70,20);
		randomButton.setActionCommand("random");
		randomButton.addActionListener(this);
		add(randomButton);
		
		
		//cpController.solver.solve();
		state = GameState.game;
	}
	@Override
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
						+ e.getContradictionCause().toString());
				DomainWipeOut();
				success = false;
				cpController.solver.worldPop();
			}
		}
		if (success) {
			cells[x][y].current = value;
		}
		else
		{
			System.out.println("contradicted");
			cells[x][y].contradicting = true;
			cells[x][y].current = 0;
		}
		return success;
	}
	@Override
	public void paint ( Graphics gr )
	{
		//initDraw(gr);
		switch(state)
		{
		case game:
			DrawGrid(gr);
			break;
		case conflictResolution:
			break;
		case initGame:
			break;
		case pregame:
			break;
		case start:
			break;
		default:
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		super.actionPerformed(action);
		// TODO Auto-generated method stub
		switch(action.getActionCommand())
		{
		case "clear":
			
			ClearCell(activeX, activeY);
		break;
		case "random":
			RandomAssign(5);
			repaint();
			break;
		}
	}
}