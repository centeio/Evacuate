package world;

import java.awt.Color;
import java.awt.Graphics;

import jason.environment.grid.GridWorldView;

public class RoomView extends GridWorldView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoomView(RoomModel model, String title, int windowSize) {
		super(model, title, windowSize);
		setVisible(true);
		setSize(1200, 600);
	}
	
	@Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
        case RoomModel.DOOR:
            drawDoor(g, x, y);
            break;
        }
    }
	
	public void drawDoor(Graphics g, int x, int y) {
        g.setColor(Color.green);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    }
	
	@Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
		c = Color.blue;
		super.drawAgent(g, x, y, c, id);
    }

}
