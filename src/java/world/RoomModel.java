package world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import graph.*;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class RoomModel extends GridWorldModel {

	public static int nAgents;
	public static int nSecurity;
	public static final int DOOR  = 8;
	public static final int MAINDOOR = 16;
	public static final int FIRE = 32;
	public static final int FIRESPREAD = 30;
	private static final double MAXSPEED = 4.0;

	private static Random random = new Random(System.currentTimeMillis());

	private Graph graph = new Graph();
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
	public ArrayList<Boolean> safe = new ArrayList<Boolean>();
	public ArrayList<Boolean> kArea = new ArrayList<Boolean>();

	public boolean isAgSafe(int i) {
		return safe.get(i);
	}

	public void setSafe(int i, boolean s) {
		safe.set(i, s);
	}

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
				model.safe.add(i, false);
				model.kArea.add(i, false);
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
		
		createGraph();
		
		return model;
	}

	private static void createGraph() {
		
		for(int i = 0; i < model.data.length; i++)
			for(int j = 0; j <  model.data[0].length; j++)
				model.graph.addVertex(new Vertex(new Location(i,j)), true);
		
		for(Location p0 : model.graph.vertexKeys()) {
			if(p0.x > 0) {
				//LEFT
				Location left = new Location(p0.x-1, p0.y);
				if(model.data[left.x][left.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(left));
			}
			
			if(p0.x < model.data.length - 1) {
				//RIGHT
				Location right = new Location(p0.x+1, p0.y);
				if(model.data[right.x][right.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(right));
			}
			
			if(p0.y > 0) {
				//TOP
				Location top = new Location(p0.x, p0.y-1);
				if(model.data[top.x][top.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(top));
			}
			
			if(p0.y < model.data[0].length - 1) {
				//BOTTOM
				Location bottom = new Location(p0.x, p0.y+1);
				if(model.data[bottom.x][bottom.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(bottom));
			}
			
			if(p0.x > 0 && p0.y > 0) {
				//LEFT - TOP
				Location dg = new Location(p0.x-1, p0.y-1);
				if(model.data[dg.x][dg.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(dg));
			}
			
			if(p0.x > 0 && p0.y < model.data[0].length - 1) {
				//LEFT - BOTTOM
				Location dg = new Location(p0.x-1, p0.y+1);
				if(model.data[dg.x][dg.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(dg));
			}
			
			if(p0.x < model.data.length - 1 && p0.y > 0) {
				//RIGHT - TOP
				Location dg = new Location(p0.x+1, p0.y-1);
				if(model.data[dg.x][dg.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(dg));
			}
			
			if(p0.x < model.data.length - 1 && p0.y < model.data[0].length - 1) {
				//RIGHT - BOTTOM
				Location dg = new Location(p0.x+1, p0.y+1);
				if(model.data[dg.x][dg.y] != GridWorldModel.OBSTACLE)
					model.graph.addEdge(model.graph.getVertex(p0), model.graph.getVertex(dg));
			}
		}
		
		for(Edge edge : model.graph.getEdges()) {
			Location p = new Location(1,1);
			if(edge.getOne().getLocation().equals(p))
				System.out.println(edge.getOne() + " -> " + edge.getTwo());
		}
			
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
		model.setAgPanic(model.getAgentByName(agent), panic);
	}

	private void selflessness(String agent) {
		double selflessness = random.nextInt(10)/10.0;
		model.setAgSelflessness(model.getAgentByName(agent), selflessness);	
	} 

	public boolean getkArea(int i) {
		return model.kArea.get(i);
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
		kArea.set(agent, false);
		Location oldloc = getAgPos(agent);
		Location newloc = null;
		
		int agHelping;
		//agent being helped
		if((agHelping = ishelping.indexOf(agent)) != -1) {
			setAgentPos(agent, getAgPos(agHelping));
			return;
		}
				
		for(Location door : mainDoorsPositions) {
			if(oldloc == door) {
				model.safe.set(agent, true);
				model.kArea.set(agent, true);
				return;
			}
			if((newloc = doesAgSeeIt(agent, door)) != null) {
				model.kArea.set(agent, true);
				setAgentPos(agent,newloc);
				return;
			}
		}
		//checks if anybody knows
		if((newloc = lookaround(agent)) != null) {
			setAgentPos(agent,newloc);			
		}
		//finds best door
		Location bestdoor = null;
		int bestdist = Integer.MAX_VALUE;
		int i = 0;
		for(Location door : doorsPositions) {
			if(doesAgSeeIt(agent, door) != null && model.doorsDistance.get(i) < bestdist && model.doorsVisited.get(agent).get(i)) {
				bestdoor = door;
				bestdist = model.doorsDistance.get(i);
				i++;
			}
		}
		if(bestdoor != null) {
			model.kArea.set(agent, true);
			setAgentPos(agent, doesAgSeeIt(agent, bestdoor));
			return;
		}
		
		//tries any not worse path		
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
		//tries anything
		move_randomly(agName);
	}

	private Location lookaround(int agent) {
		for(int i = 0; i < nAgents ; i++) {
			Location pos;
			if((pos = doesAgSeeIt(agent, getAgPos(i))) != null && getAgPos(agent).distanceManhattan(getAgPos(i)) <= 5) {
				//propagar panico
				if(panicscales.get(i) > panicscales.get(agent)) {
					panicscales.set(agent, panicscales.get(i));
				}
				//ver se alguem sabe onde e a saida
				if(kArea.get(i) == true) {
					kArea.set(agent, true);
					return pos;
				}
				//ver se alguem precisa de ajuda
				if(ishelping.get(agent) != -1) {
					double prob = 0;

					prob = random.nextInt(10)/10.0;
					if(!ishelping.contains(i) && agentSpeed(agent)>agentSpeed(i) && helpful(i) > prob) {
						setIsHelping(agent, i);
					}
				}
			}			
		}
		return null;
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

	    Vector<Location> points = linepp(p0,p1);
	    if(points == null)
	    	return null;
	    if(Math.round(agentSpeed(ag)) >= points.size())
	    	return points.lastElement();
	    else
	    	return points.get(Math.toIntExact(Math.round(agentSpeed(ag))));
	}

	public Vector<Location> linepp(Location p0, Location p1) {
		Vector<Location> points = new Vector<Location>();
		if(p0.equals(p1)) {
			points.add(p0);
			return points;
		}
		
	    int dx = p1.x-p0.x, dy = p1.y-p0.y;
	    int nx = Math.abs(dx), ny = Math.abs(dy);
	    int sign_x = dx > 0? 1 : -1, sign_y = dy > 0? 1 : -1;

	    Location p = new Location(p0.x, p0.y);
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
	    return points;
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
			Location fire;
			if((fire = firedist(agi)) != null) {
				setAgPanic(i, 1.0);
				
				int dist = agi.distanceManhattan(fire);
				if(dist <= 4) {
					System.out.println("Agent " + getAgAtPos(agi) + " injury: " + injscales.get(getAgAtPos(agi)));
					setAgInjScale(i, Math.min(1,injscales.get(i)+(1-dist*0.2)));
					System.out.println("Agent " + getAgAtPos(agi) + " injury: " + injscales.get(getAgAtPos(agi)));
				}
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
			if(l.distanceManhattan(firePositions.elementAt(i)) < dist && linepp(l, firePositions.elementAt(i)) != null) {
				dist = l.distanceManhattan(firePositions.elementAt(i));
				fire = firePositions.elementAt(i);
			}
		}
		return fire;
	}
	
	private double helpful(int i) {
		return (1 - panicscales.get(i))*selflessness.get(i);
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
				
				if(panicscales.get(i) < 0.8)
					panicscales.set(i, 0.8);
			}
		}	
	}

	public void kill(String agName) {
		agentDead.set(getAgentByName(agName), true);
		setAgPos(getAgentByName(agName), getAgPos(getAgentByName(agName)));
	}


}
