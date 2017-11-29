package world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.Vector;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class RoomModel extends GridWorldModel {
	
	public static final int DOOR  = 16;
	public static final int FIRE = 32;
	public static final int FIRESPREAD = 40;
	private Vector<Location> firePositions = new Vector<Location>();
	
	// singleton pattern
    protected static RoomModel model = null;

    synchronized public static RoomModel create(int w, int h, int nbAgs, int nbSegs) {
        if (model == null) {
            model = new RoomModel(w, h, nbAgs + nbSegs);
        }
        
        FileReader file;
        try {
        	file = new FileReader("worldMaps/Map1.txt");
        	BufferedReader br = new BufferedReader(file);
            
            String line;
            while ((line = br.readLine()) != null) {
            	String[] lineValues = line.split("\\s+");
            	
            	switch(lineValues[0]) {
	            	case "OBSTACLE":
	            		model.add(RoomModel.OBSTACLE, Integer.parseInt(lineValues[1]), Integer.parseInt(lineValues[2]));
	            		break;
	            	case "DOOR":
	            		model.add(RoomModel.DOOR, Integer.parseInt(lineValues[1]), Integer.parseInt(lineValues[2]));
	            		break;
	            	default:
	            		break;
            	}
            }
            br.close();
        }catch(Exception e) {
        	System.out.println(e.getMessage());
        }
        
        //Random agent position
        Random randomGenerator = new Random(System.currentTimeMillis());
		int x, y;
		
        try {
        	for(int i = 0; i < nbAgs + nbSegs; i++) {        		
        		do {
        			x = randomGenerator.nextInt(model.getWidth());
        			y = randomGenerator.nextInt(model.getHeight());
        		}while(!model.isFree(x,y));
        		
        		model.setAgPos(i, x, y);
        	}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return model;
    }

	private RoomModel(int w, int h, int nAgs) {
		super(w, h, nAgs);
	}

	public void move_randomly(String agName, EscapeRoom escapeRoom) {
		
		int agent = getAgentByName(agName);
		
		Location r1 = getAgPos(agent);
		Random randomGenerator = new Random(System.currentTimeMillis());
		int randomX, randomY;
		
		do {
			randomX = randomGenerator.nextInt(3) - 1;
			randomY = randomGenerator.nextInt(3) - 1;
		}while(r1.x + randomX < 0 || r1.x + randomX > getWidth() || r1.y + randomY < 0 || r1.y + randomY > getHeight() || !model.isFreeOfObstacle( r1.x + randomX,  r1.y + randomY));
		
		r1.x += randomX;
		r1.y += randomY;
		
        setAgPos(agent, r1);
	}

	
	public void move_alert(String agName, EscapeRoom escapeRoom) {
		int agent = getAgentByName(agName);
		
		Location r1 = getAgPos(agent);
		r1.x++;
		
		if (r1.x == getWidth()) {
            r1.x = 0;
            r1.y++;
        }
        // finished searching the whole grid
        if (r1.y == getHeight()) {
            return;
        }
        setAgPos(agent, r1);		
	}

	public void agentWait() {
		
		Random randomGenerator = new Random(System.currentTimeMillis());
		try {
			Thread.sleep(3000 + randomGenerator.nextInt(5)*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void create_fire() {
		Random randomGenerator = new Random(System.currentTimeMillis());
		int x, y;
		
		try {
    		do {
    			x = randomGenerator.nextInt(model.getWidth());
    			y = randomGenerator.nextInt(model.getHeight());
    		}while(!model.isFree(x,y));
    		
    		model.add(RoomModel.FIRE, x, y);
    		
    		firePositions.add(new Location(x,y));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void environment() {
		Random randomGenerator = new Random(System.currentTimeMillis());
		int count = 0;
		Vector<Location> copy = firePositions;
		
		for(int i = 0; i < copy.size(); i++) {
						
			//TOP
			Location top = new Location(copy.get(i).x, copy.get(i).y - 1);
			
			if(model.isFreeOfObstacle(top) &&
					model.isFree(FIRE, top)) {
				
				if(randomGenerator.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, top);
					firePositions.add(top);
				}
				
			} else
				count++;
			
			
			//RIGHT
			Location right = new Location(copy.get(i).x + 1, copy.get(i).y);
			
			if(model.isFreeOfObstacle(right) &&
					model.isFree(FIRE, right)) {
				
				if(randomGenerator.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, right);
					firePositions.add(right);
				}
				
			} else
				count++;
			
			//BOTTOM
			Location bottom = new Location(copy.get(i).x, copy.get(i).y + 1);
			
			if(model.isFreeOfObstacle(bottom) &&
					model.isFree(FIRE, bottom)) {
				
				if(randomGenerator.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, bottom);
					firePositions.add(bottom);
				}
				
			} else
				count++;
			
			//LEFT
			Location left = new Location(copy.get(i).x - 1, copy.get(i).y);
			
			if(model.isFreeOfObstacle(left) &&
					model.isFree(FIRE, left)) {

				if(randomGenerator.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, left);
					firePositions.add(left);
				}
				
			} else
				count++;
			
			if(count == 4)
				firePositions.remove(i);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private int getAgentByName(String agName) {
		return Integer.parseInt(agName.substring(3, agName.length()));
	}	
}
