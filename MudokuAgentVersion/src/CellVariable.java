import java.awt.Color;
import java.awt.Graphics;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;


public class CellVariable {
	int current;
	boolean contradicting;

	static int domainXOffset = 350;
	static int domainYOffset = 400;

	static int domainWidth = 400;
	static float deltaX;
	static float deltaY;
	
	int x;
	int y;
	
	boolean isConstant;

	public CellVariable(int x_, int y_)
	{
		current = -1;
		contradicting = false;
		x = x_;
		y = y_;
	}
	
	public void DrawDomain(Graphics gr, int mouseOverIndex, int x, int y)
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
		
		/*if(mouseOverIndex != -1)
		{
			gr.setColor(AgentGameController.mouseOverColor);
			gr.drawRect((int)(domainXOffset + mouseOverIndex * deltaX), domainYOffset, (int)deltaX, (int)deltaY);
			gr.drawRect((int)(domainYOffset + mouseOverIndex * deltaY), domainXOffset, (int)deltaY, (int)deltaX);
		}
		
		IntDomainVar intvar = GameController.cpController.GetCPVariable(x, y);
		gr.setColor(Color.blue);
		for(DisposableIntIterator i = intvar.getDomain().getIterator(); i.hasNext();)
		{
			gr.drawRect(drawX, domainYOffset, (int)deltaX, (int)deltaY);
			gr.drawString(String.valueOf(i.next()), (int)(drawX + deltaX / 2) - 5, (int)(domainYOffset + deltaY) - 10);
			tempDrawX += deltaX;
			drawX = (int) tempDrawX;
		}*/
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
