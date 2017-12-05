package world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class RoomModel extends GridWorldModel {

	public static int nAgents;
	public static int nSecurity;
	public static final int DOOR  = 8;
	public static final int MAINDOOR = 16;
	public static final int FIRE = 32;
	public static final int FIRESPREAD = 40;
	private static final double MAXSPEED = 4.0;

	private static Random random = new Random(System.currentTimeMillis());

	private Vector<Location> firePositions = new Vector<Location>();
	private Vector<Location> doorsPositions = new Vector<Location>();
	private ArrayList<Integer> doorsDistance = new ArrayList<Integer>();
	private Vector<Location> mainDoorsPositions = new Vector<Location>();
	private ArrayList<ArrayList<Boolean>> doorsVisited = new ArrayList<ArrayList<Boolean>>();
	private Vector<Boolean> agentDead = new Vector<Boolean>();
	private ArrayList<Double> panicscales;
	private ArrayList<Double> selflessness;
	private ArrayList<Double> injscales;
	public ArrayList<Integer> ishelping;

	// singleton pattern
	protected static RoomModel model = null;

	synchronized public static RoomModel create(int w, int h, int nbAgs, int nbSegs) {
		if (model == null) {
			model = new RoomModel(w, h, nbAgs + nbSegs);
		}
		nAgents = nbAgs;
		nSecurity = nbSegs;

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
					model.doorsPositions.addElement(new Location(Integer.parseInt(lineValues[1]), Integer.parseInt(lineValues[2])));
					break;
				case "MAINDOOR":
					model.add(RoomModel.MAINDOOR, Integer.parseInt(lineValues[1]), Integer.parseInt(lineValues[2]));
					model.mainDoorsPositions.addElement(new Location(Integer.parseInt(lineValues[1]), Integer.parseInt(lineValues[2])));
					break;
				default:
					break;
				}
			}
			br.close();
			
			for(int i=0; i<model.doorsPositions.size();i++) {
				Location doorpos = model.doorsPositions.get(i);
				int dist = doorpos.distanceManhattan(model.mainDoorsPositions.get(0));
				int index = 0;
				for(int j=1; j<model.mainDoorsPositions.size();j++){
					int disttemp;
					Location maindoor = model.mainDoorsPositions.get(j);
					if((disttemp = doorpos.distanceManhattan(maindoor)) < dist) {
						dist = disttemp;
						index = j;
					}					
				}
				
				model.doorsDistance.add(index);
			}
			
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}

		//Random agent position
		int x, y;

		try {
			for(int i = 0; i < nbAgs + nbSegs; i++) {        		
				do {
					x = random.nextInt(model.getWidth());
					y = random.nextInt(model.getHeight());
				}while(!model.isFree(x,y));
				
				
				model.setAgPos(i, x, y);
				model.panicscales.add(i, 0.0);
				model.ishelping.add(i, -1);
				model.injscales.add(i, 0.0);
				model.setAgSelflessness(i, -1);
				model.doorsVisited.add(i, new ArrayList<Boolean>());
				for(int j=0; j<model.doorsPositions.size();j++) {
					model.doorsVisited.get(i).add(j,false);
				}
				model.agentDead.add(i, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
	}

	private RoomModel(int w, int h, int nAgs) {
		super(w, h, nAgs);
		panicscales = new ArrayList<Double>();
		selflessness = new ArrayList<Double>();
		injscales = new ArrayList<Double>();
		ishelping = new ArrayList<Integer>();
	}

	public double getAgSelflessness(int i) { return selflessness.get(i); }
	public double getAgPanic(int i) { return panicscales.get(i); }
	public double getAgInjScale(int i) { return injscales.get(i); }
	public int getIsHelping(int i) { return ishelping.get(i); }
	public int getnAgs() { return nAgents; }
	public int[][] getMap() { return model.data; }
	public Boolean isDead(String agName) { return agentDead.get(getAgentByName(agName)); }
	public boolean isDead(int id) { return agentDead.get(id); }

	public void setAgSelflessness(int i, double j) {
		try {
			selflessness.set(i, j);		
		}catch (IndexOutOfBoundsException e) {
			selflessness.add(i,0.0);
			selflessness("Bob"+i);
		}
	}	
	public void setAgPanic(int i, double panic) { panicscales.set(i, panic); }	
	public void setAgInjScale(int i, double is) { injscales.set(i, is); }	
	public void setIsHelping(int i, int ag) { ishelping.set(i, ag); }

	public void panic(String agent) {
		double panic = random.nextInt(10)/10.0;
		System.out.println(agent + " panic " + panic);
		model.setAgPanic(model.getAgentByName(agent), panic);
	}

	private void selflessness(String agent) {
		double selflessness = random.nextInt(10)/10.0;
		model.setAgSelflessness(model.getAgentByName(agent), selflessness);	
	} 

	public void move_randomly(String agName) {

		int agent = getAgentByName(agName);
		
		if(agent < nAgents) {
			
			Location r1 = getAgPos(agent);
			int randomX, randomY;
	
			do {
				randomX = random.nextInt(3) - 1;
				randomY = random.nextInt(3) - 1;
			}while(r1.x + randomX < 0 || r1.x + randomX > getWidth() || r1.y + randomY < 0 || r1.y + randomY > getHeight() || !model.isFreeOfObstacle( r1.x + randomX,  r1.y + randomY) || !model.isFree(FIRE,new Location(r1.x + randomX,  r1.y + randomY)));
	
			r1.x += randomX;
			r1.y += randomY;
	
			setAgentPos(agent, r1);
		} else {
			setAgentPos(agent, getAgPos(agent));
		}
	}


	public void setAgentPos(int i, Location l) {
		int d = 0;
		setAgPos(i,l);
		for(Location door: model.doorsPositions) {
			if(door == l) {
				model.doorsVisited.get(i).set(d, true);
			}
		}

	}
	public void move_alert(String agName) {
		int agent = getAgentByName(agName);
		Location oldloc = getAgPos(agent);
		Location newloc = null;
				
		for(Location door : mainDoorsPositions) {
			if((newloc = doesAgSeeIt(agent, door)) != null) {
				setAgentPos(agent,newloc);
				return;
			}
		}
		
		Location bestdoor = null;
		int bestdist = Integer.MAX_VALUE;
		int i = 0;
		for(Location door : doorsPositions) {
			if((newloc = doesAgSeeIt(agent, door)) != null && model.doorsDistance.get(i) < bestdist && model.doorsVisited.get(agent).get(i)) {
				bestdoor = door;
				bestdist = model.doorsDistance.get(i);
				i++;
			}
		}
		if(bestdoor != null) {
			System.out.println(bestdoor + "aqui com best door");
			setAgentPos(agent,doesAgSeeIt(agent, bestdoor));
			return;
		}
		System.out.println("reinei aqui");
		Location fire = firedist(oldloc);
		int firedist = -1;
		if(fire != null)
			firedist = fire.distanceManhattan(oldloc);
		
		newloc = new Location(oldloc.x-1,oldloc.y);
		if(newloc.x >= 0 && newloc.x < getWidth() && newloc.y >= 0 && newloc.y < getHeight() && model.isFreeOfObstacle(newloc) && model.isFree(FIRE, newloc)) {
			Location newfire = firedist(newloc);
			if(newfire == null || !(newfire.distanceManhattan(newloc) < firedist)) {
				setAgentPos(agent, newloc);
				return;
			}
		}
		
		newloc = new Location(oldloc.x+1,oldloc.y);
		if(newloc.x >= 0 && newloc.x < getWidth() && newloc.y >= 0 && newloc.y < getHeight() && model.isFreeOfObstacle(newloc) && model.isFree(FIRE, newloc)) {
			Location newfire = firedist(newloc);
			if(newfire == null || !(newfire.distanceManhattan(newloc) < firedist)) {
				setAgentPos(agent, newloc);
				return;
			}
		}
		
		newloc = new Location(oldloc.x,oldloc.y-1);
		if(newloc.x >= 0 && newloc.x < getWidth() && newloc.y >= 0 && newloc.y < getHeight() && model.isFreeOfObstacle(newloc) && model.isFree(FIRE, newloc)) {
			Location newfire = firedist(newloc);
			if(newfire == null || !(newfire.distanceManhattan(newloc) < firedist)) {
				setAgentPos(agent, newloc);
				return;
			}
		}
		
		newloc = new Location(oldloc.x,oldloc.y+1);
		if(newloc.x >= 0 && newloc.x < getWidth() && newloc.y >= 0 && newloc.y < getHeight() && model.isFreeOfObstacle(newloc) && model.isFree(FIRE, newloc)) {
			Location newfire = firedist(newloc);
			if(newfire == null || !(newfire.distanceManhattan(newloc) < firedist)) {
				setAgentPos(agent, newloc);
				return;
			}
		}
	}

	public void agentWait() {

		try {
			Thread.sleep(3000 + random.nextInt(5)*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a generic accident (in this case, represented by fire) in a random location.
	 */
	public void create_fire() {
		int x, y;

		try {
			do {
				x = random.nextInt(model.getWidth());
				y = random.nextInt(model.getHeight());
			}while(!model.isFree(x,y));

			model.add(RoomModel.FIRE, x, y);
			
			System.out.println("Creating fire!");
			firePositions.add(new Location(x,y));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Environment change that happens each cycle.
	 * 1. Fire spread
	 * TO-DO: MORE.
	 */
	public void environment() {
		spreadFire();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Spreads the fire each cycle with a probability of FIRESPREAD.	
	 */
	@SuppressWarnings("unchecked")
	private void spreadFire() {
		int count = 0;
		Vector<Location> copy = (Vector<Location>) firePositions.clone();

		for(int i = 0; i < copy.size(); i++) {
			
			//TOP
			Location top = new Location(copy.get(i).x, copy.get(i).y - 1);

			if(model.isFreeOfObstacle(top) &&
					model.isFree(FIRE, top)) {

				if(random.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, top);
					firePositions.add(top);
				}

			} else
				count++;


			//RIGHT
			Location right = new Location(copy.get(i).x + 1, copy.get(i).y);

			if(model.isFreeOfObstacle(right) &&
					model.isFree(FIRE, right)) {

				if(random.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, right);
					firePositions.add(right);
				}

			} else
				count++;

			//BOTTOM
			Location bottom = new Location(copy.get(i).x, copy.get(i).y + 1);

			if(model.isFreeOfObstacle(bottom) &&
					model.isFree(FIRE, bottom)) {

				if(random.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, bottom);
					firePositions.add(bottom);
				}

			} else
				count++;

			//LEFT
			Location left = new Location(copy.get(i).x - 1, copy.get(i).y);

			if(model.isFreeOfObstacle(left) &&
					model.isFree(FIRE, left)) {

				if(random.nextInt(100) + 1 <= FIRESPREAD) {
					model.add(FIRE, left);
					firePositions.add(left);
				}

			} else
				count++;

			if(count == 4)
				firePositions.remove(i);
		}
	}

	/**
	 * Gets the model agent index given its name.
	 * @param agName Agent.
	 * @return Index of the agent in the model.
	 */
	public int getAgentByName(String agName) {
		return Integer.parseInt(agName.substring(3, agName.length()));
	}
	
	public ArrayList<Location> find(int obj){
		ArrayList<Location> objs = new ArrayList<Location>();
		
		for(int i = 0; i < model.data.length; i++) {
			for(int j = 0; j < model.data[i].length; j++) {
				if(data[i][j] == obj)
					objs.add(new Location(i,j));
			}
		}
		
		return objs;
	}

	/**
	 * Gets the next position of the agent towards location p1, if agent can see it.
	 * It depends on its speed.
	 * @param ag Agent to move.
	 * @param p1 Destination
	 * @return Location of next position.
	 */
	private Location doesAgSeeIt(int ag, Location p1) {
		
		Location p0 = getAgPos(ag);
		if(p0.equals(p1))
			return p0;
		
	    int dx = p1.x-p0.x, dy = p1.y-p0.y;
	    int nx = Math.abs(dx), ny = Math.abs(dy);
	    int sign_x = dx > 0? 1 : -1, sign_y = dy > 0? 1 : -1;

	    Location p = new Location(p0.x, p0.y);
	    Vector<Location> points = new Vector<Location>();
	    for (int ix = 0, iy = 0; ix < nx || iy < ny;) {
	        if ((0.5+ix) / nx == (0.5+iy) / ny) {
	            // next step is diagonal
	            p.x += sign_x;
	            p.y += sign_y;
	            ix++;
	            iy++;
	        } else if ((0.5+ix) / nx < (0.5+iy) / ny) {
	            // next step is horizontal
	            p.x += sign_x;
	            ix++;
	        } else {
	            // next step is vertical
	            p.y += sign_y;
	            iy++;
	        }
	        if(!model.isFreeOfObstacle(p) || !model.isFree(FIRE, p))
	        	return null;
	        points.add(new Location(p.x, p.y));
	    }
	    
	    if(Math.round(agentSpeed(ag)) >= points.size())
	    	return points.lastElement();
	    else
	    	return points.get(Math.toIntExact(Math.round(agentSpeed(ag))));
	}

	public Location doesAgSeeIt(Location p0, Location p1) {
		if(p0.equals(p1))
			return p0;
		
	    int dx = p1.x-p0.x, dy = p1.y-p0.y;
	    int nx = Math.abs(dx), ny = Math.abs(dy);
	    int sign_x = dx > 0? 1 : -1, sign_y = dy > 0? 1 : -1;

	    Location p = new Location(p0.x, p0.y);
	    ArrayList<Location> points = new ArrayList<Location>();
	    for (int ix = 0, iy = 0; ix < nx || iy < ny;) {
	        if ((0.5+ix) / nx == (0.5+iy) / ny) {
	            // next step is diagonal
	            p.x += sign_x;
	            p.y += sign_y;
	            ix++;
	            iy++;
	        } else if ((0.5+ix) / nx < (0.5+iy) / ny) {
	            // next step is horizontal
	            p.x += sign_x;
	            ix++;
	        } else {
	            // next step is vertical
	            p.y += sign_y;
	            iy++;
	        }
	        if(!model.isFreeOfObstacle(p)) {
	        	return null;
	        }
	        points.add(new Location(p.x, p.y));
	    }
	    return points.get(0);
	}

	private double agentSpeed(int ag) {
		int ag2;
		double is_aj = 0;
		if((ag2 = ishelping.get(ag)) != -1)
			is_aj = injscales.get(ag2);

		return ((1 - injscales.get(ag))*panicscales.get(ag)*MAXSPEED)/(1+is_aj);
	}

	public void updateInjuryPanicScale() {

		for(int i = 0; i < nAgents + nSecurity ; i++) {
			Location agi = getAgPos(i);
			double prob = 0;

			//see if another agent is hurting, i.e. see if two are in the same cell
			for(int j = i + 1; j < nAgents + nSecurity ; j++) {
				Location agj = getAgPos(i);

				if(agi.equals(agj)) {
					//probability of i hurting j
					prob = random.nextInt(10)/10.0;
					if((1-helpful(i)) > prob) {
						setAgInjScale(j, Math.min(injscales.get(j)*1.1,1));
					}

					//probability of j hurting i
					prob = random.nextInt(10)/10.0;
					if((1-helpful(j)) > prob) {
						setAgInjScale(i, Math.min(injscales.get(i)*1.1,1));
					}
				}
			}

			//see if agent near to accident or inside the accident
			for(int j = 0; j < firePositions.size(); j++) {
				int dist;
				if((dist = agi.distanceManhattan(firePositions.elementAt(j))) <= 4 && doesAgSeeIt(agi, firePositions.elementAt(j)) != null) {
					setAgInjScale(i, Math.min(1,injscales.get(i)+(1-dist*0.2)));
				}
				if(doesAgSeeIt(agi, firePositions.elementAt(j)) != null) {
					setAgPanic(i, 1.0);
				}
				
				if(agi == firePositions.get(j))
					setAgInjScale(i, 1.0);
			}


			//see if agent fell
			prob = random.nextInt(100);
			if(prob == 1) {
				//agent falls
				double hurts = 1.1;
				if(random.nextInt(1) == 0) {
					hurts = 1.2;
				}
				setAgInjScale(i, Math.min(injscales.get(i)*hurts,1));
			}
		}
	}

	public Location firedist(Location l) {
		int dist = 11;
		Location fire = null;

		for(int i = 0; i < firePositions.size(); i++) {
			if(l.distanceManhattan(firePositions.elementAt(i)) < dist && (doesAgSeeIt(l, firePositions.elementAt(i)) != null)) {
				dist = l.distanceManhattan(firePositions.elementAt(i));
				fire = firePositions.elementAt(i);
			}
		}
		return fire;
	}
	
	private double helpful(int i) {
		return (1 - panicscales.get(i))*selflessness.get(i);
	}

	public void updateHelp() {
		for(int i = 0; i < getnAgs() ; i++) {
			Location agi = getAgPos(i);
			if(ishelping.get(i) != -1) {
				double prob = 0;

				//see who needs help
				for(int j = i + 1; j < getnAgs(); j++) {
					Location agj = getAgPos(i);

					prob = random.nextInt(10)/10.0;
					if(!ishelping.contains(j) && agentSpeed(i)>agentSpeed(j) && agi.distanceManhattan(agj) <= 5 && helpful(i) > prob) {
						setIsHelping(i, j);
						return;
					}
				}
			}
		}
	}

	/**
	 * Changes the panic scale of every agent after hearing the alarm ring.
	 * @param agName Environment agent that triggers this event.
	 */
	public void panicEnv(String agName) {
		
		int agent = getAgentByName(agName);
		
		if(agent < nAgents) {
			if(random.nextInt(100) + 1 <= 25)
				panicscales.set(agent, 1.0);
			else {
				if(random.nextInt(100) + 1 <= 45) {
					double panic = random.nextInt(10)/10.0;
					
					if(panicscales.get(agent) < panic)
						panicscales.set(agent, panic);
				}
			}
		} else {
			panicscales.set(agent, 1.0);
		}
	}

	/**
	 * Changes the panic scale of every agent if a security guard is nearby. The guard assures the agent
	 *  an accident is happening.
	 * @param agName Security agent that triggers this event.
	 */
	public void panicSeg(String agName) {
		
		for(int i = 0; i < nAgents ; i++) {
			if(getAgPos(getAgentByName(agName)).distanceManhattan(getAgPos(i)) <= 5) {
				System.out.println(agName + " telling Bob" + i + " there's a fire.");
				
				if(panicscales.get(i) < 0.8)
					panicscales.set(i, 0.8);
			}
		}	
	}

	public void kill(String agName) {
		agentDead.set(getAgentByName(agName), true);
	}
}
