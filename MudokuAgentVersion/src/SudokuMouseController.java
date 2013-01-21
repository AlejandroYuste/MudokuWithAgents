import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class SudokuMouseController implements MouseListener, MouseMotionListener {

	int gridEndX;
	int gridEndY;
	
	float deltaX;
	float deltaY;
	
	int mouseOverX;
	int mouseOverY;
	
	int lastDomainIndex;
	
	int lastActiveX;
	int lastActiveY;
	
	GameController gameController;

	public SudokuMouseController(GameController gameController_)
	{
		gameController = gameController_;
	}
	public void init()
	{
		gridEndX = GameController.gridEndX;
		gridEndY = GameController.gridEndY;

		deltaX =  GameController.deltaX;
		deltaY =  GameController.deltaY;
		mouseOverX = 0;
		mouseOverY = 0;
		System.out.println("Mouse control initialized");
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		int activeX = 0;
		int activeY = 0;
		int index = -1;
		
		if(e.getY() > CellVariable.domainYOffset && e.getY() < CellVariable.domainYOffset + deltaY && e.getX() < 420)
			index = (int)((e.getX() - CellVariable.domainXOffset) / deltaX);
		else if(e.getX() < gridEndX && e.getX() > GameController.gridXOffset &&  e.getY() < gridEndY && e.getY() > AgentGameController.gridYOffset)
		{
			activeX = (int) ((e.getX() - GameController.gridXOffset) / deltaX);
			activeY = (int) ((e.getY() - GameController.gridYOffset) / deltaY);
			
			if(activeX != lastActiveX || activeY != lastActiveY)
			{
				gameController.CellClick(activeX, activeY);
				lastActiveX = activeX;
				lastActiveY = activeY;
			}
		}
			
		gameController.DomainClick(index);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void mouseMoved(MouseEvent e) {

		if(e.getX() > GameController.gridXOffset && e.getX() < gridEndX && e.getY() < gridEndY && e.getY() > GameController.gridYOffset)
		{
			int newMouseOverX = (int) ((e.getX() - GameController.gridXOffset) / deltaX);
			int newMouseOverY = (int) ((e.getY() - GameController.gridYOffset) / deltaY);
			
			if(newMouseOverX != mouseOverX || newMouseOverY != mouseOverY)
			{
				mouseOverX = newMouseOverX;
				mouseOverY = newMouseOverY;
				gameController.MouseOverCell(mouseOverX, mouseOverY);
				lastDomainIndex = -1;
			}
		}
		else if(e.getY() > CellVariable.domainYOffset && e.getY() < CellVariable.domainYOffset + deltaY && e.getX() < 420 && e.getX() > GameController.gridXOffset)
		{
			int index = (int)((e.getX() - CellVariable.domainXOffset) / deltaX);
						
			if (lastDomainIndex != index)
			{
				lastDomainIndex = index;
				gameController.MouseOverDomain(index);
				mouseOverX = -1;
				mouseOverY = -1;
			}
		}
		else
		{
			gameController.MouseOverEmpty();
		}
	}
}
