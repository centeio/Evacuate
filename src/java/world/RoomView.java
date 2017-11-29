package world;

import java.awt.Color;
import java.awt.Graphics;

import jason.environment.grid.GridWorldView;

public class RoomView extends GridWorldView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numberAgents;
	//private int numberSecurity;

	public RoomView(RoomModel model, String title, int windowSize, int numberAgents, int numberSecurity) {
		super(model, title, windowSize);
		setVisible(true);
		setSize(1200, 600);
		this.numberAgents = numberAgents;
		//this.numberSecurity = numberSecurity;
	}
	
	@Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
        case RoomModel.DOOR:
            drawDoor(g, x, y);
            break;
        case RoomModel.FIRE:
        	drawFire(g,x,y);
        	break;
        }
    }
	
	public void drawDoor(Graphics g, int x, int y) {
        g.setColor(Color.GREEN);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    }
	
	public void drawFire(Graphics g, int x, int y) {
        g.setColor(Color.ORANGE);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    }
	
	@Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
		String label;
		
		if(id >= numberAgents) {
			c = Color.BLUE;
			label = "S" + (id+1);
		}
		else {
			c = Color.PINK;
			label = "P" + (id+1);
		}
		super.drawAgent(g, x, y, c, -1);
		
		g.setColor(Color.black);
		super.drawString(g, x, y, defaultFont, label);
    }

}
