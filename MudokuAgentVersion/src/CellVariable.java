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
	
	public void DrawDomain(Graphics gr, int mouseOverIndex)
	{
		float tempDrawX = domainXOffset;
		int drawX = (int) tempDrawX;
		IntDomainVar intvar = GameController.cpController.GetCPVariable(x, y);
		for(DisposableIntIterator i = intvar.getDomain().getIterator(); i.hasNext();)
		{
			gr.drawRect(drawX, domainYOffset, (int)deltaX, (int)deltaY);
			gr.drawString(String.valueOf(i.next()), (int)(drawX + deltaX / 2) - 5, (int)(domainYOffset + deltaY) - 10);
			tempDrawX += deltaX;
			drawX = (int) tempDrawX;
		}
		if(mouseOverIndex != -1)
		{
			gr.setColor(AgentGameController.mouseOverColor);
			gr.drawRect((int)(domainXOffset + mouseOverIndex * deltaX), domainYOffset, (int)deltaX, (int)deltaY);
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
