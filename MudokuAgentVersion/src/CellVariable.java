import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.integer.IntDomain;

public class CellVariable 
{	
	int x, y, valueState, current;
	boolean contradicting, isConstant;

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
	 * cellState = 14 --> Accepted By Agent
	 * cellState = 15 --> Accepted By User
	 * cellState = 16 --> Rejected By Agent
	 * cellState = 17 --> Rejected By User
	 */

	static int domainXOffset = 350;
	static int domainYOffset = 400;

	static int domainWidth = 400;
	static float deltaX;
	static float deltaY;

	public CellVariable(int x_, int y_)		//TODO: Afegir lo del estat per cada cel·la segon l'estat de la contribucio
	{
		current = -1;
		valueState = GameController.waitingValue;
		
		contradicting = false;
		
		x = x_;
		y = y_;
	}
	
	public void DrawDomain(Graphics gr, int x, int y)
	{
		float tempDrawX = domainXOffset;
		int drawX = (int) tempDrawX;
		int activeDrawX = 0;
		int activeDrawY = 0;

		
		float tempDrawY = 0;
		int drawY = (int) tempDrawY;
		
		for(int i = 0; i<GameController.sudokuSize;i++)
		{
			gr.setColor(Color.black);
			if(i==x)
				activeDrawX = drawX;
	
			gr.drawRect(drawX, domainYOffset, (int)deltaX, (int)deltaY);
			gr.drawString(String.valueOf(i), (int)(drawX + deltaX / 2) - 5, (int)(domainYOffset + deltaY) - 10);
			tempDrawX += deltaX;
			drawX = (int) tempDrawX;
			
			
			if(i==y) 
				activeDrawY = drawY;

			gr.drawRect(domainYOffset + 10, 10 + drawY, (int)deltaX, (int)deltaY);
			gr.drawString(String.valueOf(i), (int)(deltaX) + domainYOffset - 5, (int)(drawY + deltaY / 2) + 15);
			tempDrawY += deltaY;
			drawY = (int) tempDrawY;
		}
		
		gr.setColor(new Color(220, 20, 60));
		gr.drawRect(activeDrawX, domainYOffset, (int)deltaX, (int)deltaY);
		gr.drawString(String.valueOf(x), (int)(activeDrawX + deltaX / 2) - 5, (int)(domainYOffset + deltaY) - 10);
		gr.drawRect(domainYOffset + 10, 10 + activeDrawY, (int)deltaX, (int)deltaY);
		gr.drawString(String.valueOf(y), (int)(deltaX) + domainYOffset - 5, (int)(activeDrawY + deltaY / 2) + 15);
	}
	
	public void DrawDomainConflict(Graphics gr, int conX, int conY, int cellX, int cellY, Color[] agentColors)
	{
		DrawDomain(gr, cellX, cellY);
		
		gr.setColor(agentColors[AgentGameController.votingColor]);
		gr.fillRect(conX, conY, (int) deltaX, (int) deltaY);
		
		gr.setColor(GameController.activeCellColor);
		gr.drawOval(conX + 1, conY + 1, (int) deltaX - 4, (int) deltaY - 4);
		
		Stroke stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		gr.setColor(Color.black);
		gr.drawRect(conX, conY, (int)deltaX, (int)deltaY);
	}

	public int EvaluateClick(int mouseX, int mouseY)
	{
		int index = (int)((mouseX - domainXOffset) / deltaX);
		IntDomain idom = AgentGameController.cpController.GetCPVariable(x, y).getDomain();
		DisposableIntIterator iter = idom.getIterator();
		
		int val = 1;
		for(;index >= 0 && iter.hasNext(); index--)
		{
			val = iter.next();
		}
		return val;
	}
	
	public void SetConstant()
	{
		isConstant = true;
	}
	
	public boolean IsConstant()
	{
		return isConstant;
	}
}
