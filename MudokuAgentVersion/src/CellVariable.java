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
	
	public void DrawDomain(Graphics gr, int mouseOverIndex, int x, int y, ClientGameController.ActualRol actualRol)
	{
		float tempDrawX = domainXOffset;
		int drawX = (int) tempDrawX;
		
		if (actualRol == ClientGameController.ActualRol.Observer)
		{
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
		else
		{
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
	}
	
	
	public void DrawDomainAgent(Graphics gr, int x, int y)
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
		DrawDomainAgent(gr, cellX, cellY);
		
		gr.setColor(agentColors[7]);
		
		gr.fillRect(conX, conY, (int) deltaX, (int) deltaY);
		
		Stroke stroke = new BasicStroke(2);
		((Graphics2D) gr).setStroke(stroke);
		gr.setColor(Color.black);
		gr.drawRect(conX, conY, (int)deltaX, (int)deltaY);
		
		gr.setColor(Color.white);
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
