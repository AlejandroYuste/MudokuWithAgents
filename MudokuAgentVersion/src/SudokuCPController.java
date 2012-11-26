import static choco.Choco.allDifferent;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;


public class SudokuCPController {

	public CPModel model;
	public Solver solver;
	
	private IntegerVariable[][] cellVars;

	private int sudokuSize;
	
	public SudokuCPController()
	{
		
	}
	
	public void InitCP()
	{
		sudokuSize = AgentGameController.sudokuSize;
		cellVars = new IntegerVariable[sudokuSize][sudokuSize];
		for(int i  = 0; i < sudokuSize; i++)
		{
			for(int k = 0; k < sudokuSize; k++)
			{
				cellVars[i][k] = new IntegerVariable("Cell<" + (i + 1) + "," + (k + 1) + ">", 1, sudokuSize);
			}
		}
		
		model = new CPModel();
		solver = new CPSolver();

		// Create horizontal and vertical alldifferent constraints
		IntegerVariable[] horizontalVars = new IntegerVariable[sudokuSize];
		IntegerVariable[] verticalVars = new IntegerVariable[sudokuSize];
		for(int i = 0; i < sudokuSize; i++)
		{
			for(int k = 0; k < sudokuSize; k++)
			{
				horizontalVars[k] = cellVars[i][k];
				verticalVars[k] = cellVars[k][i];
			}
			model.addConstraint(allDifferent(horizontalVars));
			model.addConstraint(allDifferent(verticalVars));
		}
		
		// Create sub-region alldifferent constraints
		IntegerVariable[] vars = new IntegerVariable[sudokuSize];
		int varsIndex = 0;
		int cellX;
		int cellY;
		for(int x = 0; x < Math.pow(sudokuSize, 0.5); x++)
		{
			for(int y = 0; y < Math.pow(sudokuSize, 0.5); y++)
			{
				for(int i = 0; i < Math.pow(sudokuSize, 0.5); i++)
				{
					for(int k = 0; k < Math.pow(sudokuSize, 0.5); k++)
					{
						cellX = (int) ((x * Math.pow(sudokuSize, 0.5)) + i);
						cellY = (int) ((y * Math.pow(sudokuSize, 0.5)) + k);
						vars[varsIndex] = cellVars[cellX][cellY];
						varsIndex++;
					}
				}
				varsIndex = 0;
				model.addConstraint(allDifferent(vars));
			}
		}

		solver.read(model);
	}
	public IntDomainVar GetCPVariable(int x, int y)
	{
		return solver.getVar(cellVars[x][y]);
	}
	
	public void InstantiateVar(int x, int y, int val) throws ContradictionException
	{
		/*if(solver.getVar(cellVars[x][y]) == null)
		{
			System.out.println("unexpected null");
		}
		else
		{
			solver.getVar(cellVars[x][y]).setVal(val);
		}*/
		
		solver.getVar(cellVars[x][y]).setVal(val);
	}
	
	public void Propagate() throws ContradictionException
	{
		solver.propagate();
	}
	
	public void Refresh(CellVariable[][] cells) throws ContradictionException
	{
		System.out.println("begin refresh");
		solver.clear();
		solver.read(model);
		System.out.println("model read");
		for(int x = 0; x < sudokuSize; x++)
		{
			for(int y = 0; y < sudokuSize; y++)
			{
				if(cells[x][y].current != -1 && !cells[x][y].contradicting)
				{
					if(solver.getVar(cellVars[x][y]) == null)
					{
						System.out.println("Could not get var at : " + x + " - " + y);
					}
					solver.getVar(cellVars[x][y]).setVal(cells[x][y].current);
				}
			}
		}
		solver.propagate();
	}
}
