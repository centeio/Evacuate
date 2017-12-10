package world;

import java.awt.Color;
import java.awt.Graphics;

import jason.environment.grid.GridWorldView;

public class RoomView extends GridWorldView {

	private static final long serialVersionUID = 1L;
	private int numberAgents;
	//private int numberSecurity;

	public RoomView(RoomModel model, String title, int windowSize, int numberAgents, int numberSecurity) {
		super(model, title, windowSize);
		setVisible(true);
		setSize(windowSize, 9 * windowSize / 16);
		this.numberAgents = numberAgents;
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
        case RoomModel.MAINDOOR:
        	drawMainDoor(g,x,y);
        	break;
        }
    }
	
	public void drawDoor(Graphics g, int x, int y) {
        g.setColor(Color.GREEN);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    }
	
	public void drawMainDoor(Graphics g, int x, int y) {
        g.setColor(Color.RED);
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
		RoomModel model = (RoomModel)this.model;
		
		if(!model.isDead(id) && !model.isAgSafe(id)) {
			String label;
			
			if(id >= numberAgents) {
				c = Color.BLUE;
				label = "S" + id;
			}
			else {
				if(model.getAgInjScale(id) > 0) {
					c = new Color(255 - Math.toIntExact(Math.round(255 * model.getAgInjScale(id))), 0, 0);
				}
				else
					c = Color.PINK;
				label = "P" + id;
			}
			super.drawAgent(g, x, y, c, -1);
			
			g.setColor(Color.BLACK);
			super.drawString(g, x, y, defaultFont, label);
		}
		
		else if(model.isDead(id)){
			
			g.drawLine(x * cellSizeW, y * cellSizeH, x  * cellSizeW + cellSizeW, y * cellSizeH + cellSizeH);
	        g.drawLine(x * cellSizeW, y * cellSizeH + cellSizeH, x  * cellSizeW + cellSizeW, y * cellSizeH);
	        c = Color.BLACK;
	        
	        super.drawAgent(g, x, y, c, -1);
		}
    }
}
