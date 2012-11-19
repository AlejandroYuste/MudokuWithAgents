import java.awt.Color;
import java.awt.Graphics;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.integer.IntDomain;

public class CellVariable 
{
	
	int x, y, valueState, current;
	boolean contradicting, isConstant;

	/* cellState = 0 --> waitingValue
	 * cellState = 1 --> initializedByServer
	 * cellState = 2 --> contribution By Rows
	 * cellState = 3 --> contribution By Columns
	 * cellState = 4 --> contribution By Squares
	 * cellState = 5 --> contribution By User
	 * cellState = 6 --> committed
	 * cellState = 7 --> accepted
	 */

	static int domainXOffset = 350;
	static int domainYOffset = 400;

	static int domainWidth = 400;
	static float deltaX;
	static float deltaY;

	public CellVariable(int x_, int y_)		//TODO: Afeguir lo del estat per cada cel·la segon l'estat de la contribucio
	{
		current = -1;
		valueState = 0;
		
		contradicting = false;
		
		x = x_;
		y = y_;
	}
	
	public void DrawDomain(Graphics gr, int mouseOverIndex, int x, int y)
	{
		float tempDrawX = domainXOffset;
		int drawX = (int) tempDrawX;
		gr.setColor(Color.black);
		
		for(int i = 0; i<GameController.sudokuSize;i++)
		{
			gr.drawRect(drawX, domainYOffset, (int)deltaX, (int)deltaY);
			gr.drawString(String.valueOf(i+1), (int)(drawX + deltaX / 2) - 5, (int)(domainYOffset + deltaY) - 10);
			tempDrawX += deltaX;
			drawX = (int) tempDrawX;
		}
		if(mouseOverIndex != -1)
		{
			gr.setColor(ClientGameController.mouseOverColor);
			gr.drawRect((int)(domainXOffset + mouseOverIndex * deltaX), domainYOffset, (int)deltaX, (int)deltaY);
		}
	}
	
	
	public void DrawDomainAgent(Graphics gr, int mouseOverIndex, int x, int y)
	{
		float tempDrawX = domainXOffset;
		int drawX = (int) tempDrawX;
		
		float tempDrawY = 0;
		int drawY = (int) tempDrawY;
		
		for(int i = 0; i<GameController.sudokuSize;i++)
		{
			gr.setColor(Color.black);
			if(i==x) gr.setColor(Color.blue);
			gr.drawRect(drawX, domainYOffset, (int)deltaX, (int)deltaY);
			gr.drawString(String.valueOf(i), (int)(drawX + deltaX / 2) - 5, (int)(domainYOffset + deltaY) - 10);
			tempDrawX += deltaX;
			drawX = (int) tempDrawX;
			
			gr.setColor(Color.black);
			if(i==y) gr.setColor(Color.blue);
			gr.drawRect(domainYOffset + 10, 10 + drawY, (int)deltaX, (int)deltaY);
			gr.drawString(String.valueOf(i), (int)(deltaX) + domainYOffset - 5, (int)(drawY + deltaY / 2) + 15);
			tempDrawY += deltaY;
			drawY = (int) tempDrawY;
		}
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
