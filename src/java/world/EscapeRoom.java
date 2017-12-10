package world;
// Environment code for project escape

import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.Location;

import java.io.BufferedReader;
import java.io.FileReader;
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
    public static final Term    setKnowlege = Literal.parseLiteral("setKnowledge");
    public static final Term    initialize = Literal.parseLiteral("initialize");
    private static int 	numberAgents = 10;
    private static int 	numberSecurity = 2;
    private static String 	mode = "run";
    private static String map = "worldMaps/Map2.txt";

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        
        int width = 30, height = 20;
        FileReader file;
		try {
			file = new FileReader(map);
			BufferedReader br = new BufferedReader(file);
			
			String line = br.readLine();
			String[] lineValues = line.split("\\s+");
			width = Integer.parseInt(lineValues[0]);
			height = Integer.parseInt(lineValues[1]);
			numberAgents = Integer.parseInt(lineValues[2]);
			numberSecurity = Integer.parseInt(lineValues[3]);
			
			br.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
        
        model = RoomModel.create(width, height, numberAgents, numberSecurity, map);
        view = new RoomView(model, "Escape Room", 1800, numberAgents, numberSecurity);
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
            
            else if(action.equals(kill))
            	model.kill(agName);
            
            else if(action.equals(setKnowlege))
            	model.setKnowledge(model.getAgentByName(agName), true);
            
            else if(action.equals(initialize))
            	return initialize();
            
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
        
        for(int i = 0; i < numberAgents; i++) {
        	clearPercepts("Bob"+i);
        	
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
        }
    }
	
    private boolean initialize() {
        
		try {
			this.getEnvironmentInfraTier().getRuntimeServices().createAgent("environmentAg", "environmentAg.asl", null, null, null, null, null);
			this.getEnvironmentInfraTier().getRuntimeServices().startAgent("environmentAg");
			
			Literal nAgents = Literal.parseLiteral("numberAgents(" + numberAgents + ")");
			addPercept("environmentAg", nAgents);
			
			Literal nSecurity = Literal.parseLiteral("numberSecurity(" + numberSecurity + ")");
			addPercept("environmentAg", nSecurity);
			
			Literal modeLiteral = Literal.parseLiteral("mode(" + mode + ")");
			addPercept("environmentAg", modeLiteral);
			
			informAgsEnvironmentChanged();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	/** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
    
    
}
