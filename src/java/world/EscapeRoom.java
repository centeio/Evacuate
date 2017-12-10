package world;
// Environment code for project escape

import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.Location;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.*;

public class EscapeRoom extends Environment {

    private Logger logger =  Logger.getLogger("escape."+EscapeRoom.class.getName());
    RoomModel model;
    RoomView view;
    
    public static final Term    move = Literal.parseLiteral("move");
    public static final Term    wait = Literal.parseLiteral("wait");
    public static final Term    alert = Literal.parseLiteral("alert");
    public static final Term    panicEnv = Literal.parseLiteral("panicscale(environment)");
    public static final Term    panicSeg = Literal.parseLiteral("segtellFire");
    public static final Term    createFire = Literal.parseLiteral("createFire");
    public static final Term    environment = Literal.parseLiteral("environment");
    public static final Term    start = Literal.parseLiteral("start");
    public static final Term    kill = Literal.parseLiteral("killagent");
    public static final Term    safe = Literal.parseLiteral("safeagent");
    public static final Term    setKnowlege = Literal.parseLiteral("setKnowledge");
    private static final int 	numberAgents = 10;
    private static final int 	numberSecurity = 2;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        
        model = RoomModel.create(30, 20, numberAgents, numberSecurity);
        view = new RoomView(model, "Escape Room", 700, numberAgents, numberSecurity);
        model.setView(view);
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
    	logger.info(agName+" doing: "+ action);
        
        try {
            if (action.equals(move))
                model.moveRandomly(agName);
            
            else if (action.equals(wait))
                model.agentWait();
            
            else if (action.equals(alert))
                model.moveAlert(agName);
            
            else if(action.equals(createFire))     	
            	model.createFire();
            
            else if(action.equals(environment))
            	model.environment();
            
            else if(action.equals(panicEnv))
            	model.panicEnv(agName);
            
            else if(action.equals(panicSeg))
            	model.panicSeg(agName);
            
            else if(action.equals(kill)) {
            	model.died(agName);
            	model.kill(agName);
            }
            
            else if(action.equals(safe))
            	model.kill(agName);
            
            else if(action.equals(setKnowlege))
            	model.setKnowledge(model.getAgentByName(agName), true);
            
            else if(action.equals(start))
            	System.out.println("Starting system.");
            else
                return false;
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePercepts();

        try {
            Thread.sleep(500);
        } catch (Exception e) {}
        
        informAgsEnvironmentChanged();
        return true;
    }
      
    private void updatePercepts() {
        
        model.updateInjuryPanicScale();
        int count = 0;
        
        for(int i = 0; i < numberAgents; i++) {
        	clearPercepts("Bob"+i);
        	if(model.isAgSafe(i) || model.isDead(i))
        		count ++;
        	
        	//agent's location
        	Location agloc = model.getAgPos(i);
	        Literal pos = Literal.parseLiteral("pos(Bob" + i + "," + agloc.x + "," + agloc.y + ")");
	        addPercept("Bob"+i, pos);
	        
	        //agent's panic scale
	        double panic = model.getAgPanic(i);
	        Literal agpanic = Literal.parseLiteral("panicscale(Bob"+i+","+ panic +")");
	        addPercept("Bob"+i,agpanic);
	        
	        //agent's injury scale
	        double injury = model.getAgInjScale(i);
	        Literal aginjuty = Literal.parseLiteral("injuryscale(Bob"+i+","+ injury +")");
	        addPercept("Bob"+i, aginjuty);
	        
	        //agent's selflessness
	        double selfln = model.getAgSelflessness(i);
	        Literal agselfln = Literal.parseLiteral("selflessness(Bob"+i+","+ selfln +")");
	        addPercept("Bob"+i,agselfln);
	        
	        //agent's safety
	        if(model.isAgSafe(i)) {
		        Literal agsafe = Literal.parseLiteral("safe(Bob"+i+")");
		        addPercept("Bob"+i,agsafe);
	        }
	        
		    //agent's knowledge of the area
	        Literal karea = Literal.parseLiteral("kowledgeofArea(Bob"+i+", 0)");
	        if(model.getkArea(i))
		        karea = Literal.parseLiteral("kowledgeofArea(Bob"+i+", 1)");
	        addPercept("Bob"+i,karea);
        }
        
        for(int i = numberAgents; i < numberAgents + numberSecurity; i++) {
        	clearPercepts("Seg"+i);
        	
        	if(model.isAgSafe(i) || model.isDead(i))
        		count ++;
        	
        	//agent's location
        	Location agloc = model.getAgPos(i);
	        Literal pos = Literal.parseLiteral("pos(Seg" + i + "," + agloc.x + "," + agloc.y + ")");
	        addPercept("Seg"+i, pos);
	        
	        //agent's panic scale
	        double panic = model.getAgPanic(i);
	        Literal agpanic = Literal.parseLiteral("panicscale(Seg"+i+","+ panic +")");
	        addPercept("Seg"+i, agpanic);
	        
	        //agent's injury scale
	        double injury = model.getAgInjScale(i);
	        Literal aginjury = Literal.parseLiteral("injuryscale(Seg"+i+","+ injury +")");
	        addPercept("Seg"+i, aginjury);
	        
	        //agent's selflessness
	        double selfln = model.getAgSelflessness(i);
	        Literal agselfln = Literal.parseLiteral("selflessness(Seg"+i+","+ selfln +")");
	        addPercept("Seg"+i, agselfln);
	        
	      //agent's safety
	        if(model.isAgSafe(i)) {
		        Literal agsafe = Literal.parseLiteral("safe(Seg"+i+")");
		        addPercept("Seg"+i,agsafe);
	        }
		    
	        Literal karea = Literal.parseLiteral("kowledgeofArea(Seg"+i+", 1)");
	        addPercept("Seg"+i,karea);
	        
	        if(count == numberAgents + numberSecurity) {
		        Literal shutdown = Literal.parseLiteral("shutdown");
		        addPercept("environmentAg",shutdown);
		        
				int safe = 0;
				long times = 0;
				
				ArrayList<Long> safetimes = new ArrayList<Long>();
				
				for(int t = 0; t< model.getTimes().size();t++) {
					if(model.getSafe().get(t)) {
						safe++;
						times += model.getTimes().get(t);
						safetimes.add(model.getTimes().get(t));
					}				
				}
				
				int middle = safetimes.size()/2;
				Long medianValue = (long) 0.0; //declare variable 
				if (safetimes.size()%2 == 1) 
				    medianValue = safetimes.get(middle);
				else
				   medianValue = (safetimes.get(middle-1) + safetimes.get(middle))/ 2;
				
		        
		        PrintWriter writer;
				try {					
					writer = new PrintWriter("statitics.txt", "UTF-8");
					int ns = numberAgents + numberSecurity;
			        writer.println("Number of agents: "+ ns);
			        writer.println("Number of deaths: "+ model.getDead());
			        writer.println("Average Time: " + times/safe);
			        writer.println("Median Time: " + medianValue);
			        writer.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	        }
        }
    }

	/** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
    
    
}
