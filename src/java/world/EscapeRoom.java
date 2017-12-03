package world;
// Environment code for project escape

import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.Location;

import java.util.Random;
import java.util.logging.*;

public class EscapeRoom extends Environment {

    private Logger logger =  Logger.getLogger("escape."+EscapeRoom.class.getName());
    RoomModel model;
    RoomView view;
    
    public static final Term    move = Literal.parseLiteral("move");
    public static final Term    wait = Literal.parseLiteral("wait");
    public static final Term    alert = Literal.parseLiteral("alert");
    public static final Term    panic = Literal.parseLiteral("panicscale");
    public static final Term    createFire = Literal.parseLiteral("createFire");
    public static final Term    environment = Literal.parseLiteral("environment");
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
            if (action.equals(move)) {
                model.move_randomly(agName, this);
            }
            else if (action.equals(wait)) {
                model.agentWait();
            }           
            else if (action.equals(alert)) {
                model.move_alert(agName, this);
            }
            else if (action.equals(panic)) {
            	System.out.println("PANIC");
                model.panic(agName);
            }
            else if(action.equals(createFire))
            	model.create_fire();
            else if(action.equals(environment)) {
            	model.environment();
            }
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePercepts();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;
    }
      
	
    void updatePercepts() {
        
        model.updateInjuryScale();
        
        for(int i=0; i<model.getnAgs(); i++) {
        	clearPercepts("Bob"+i);
        	
        	//agent's location
        	Location agloc;
        	if(model.ishelping.contains(i)) {
        		//agent is being helped
        		agloc = model.getAgPos(model.ishelping.indexOf(i));
        	}
        	else {
    	        agloc = model.getAgPos(0);
        	}
	        Literal pos = Literal.parseLiteral("pos(Bob" + i + "," + agloc.x + "," + agloc.y + ")");
	        addPercept("Bob"+i, pos);
	        //agent's panic scale
	        double panic = model.getAgPanic(i);
	        Literal agpanic = Literal.parseLiteral("panicscale(Bob"+i+","+ panic +")");
	        addPercept("Bob"+i,agpanic);
	        //agent's selflessness
	        double selfln = model.getAgSelflessness(i);
	        Literal agselfln = Literal.parseLiteral("selflessness(Bob"+i+","+ selfln +")");
	        addPercept("Bob"+i,agselfln);
	        //agent's injury scale if needed
	        
        }
        informAgsEnvironmentChanged();
    }



	/** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
