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
            	System.out.println("Here");
                model.agentWait();
            }
            else if (action.equals(alert)) {
                model.move_alert(agName, this);
            }
            else if (action.equals(panic)) {
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
       /* clearPercepts("ag2");
        Location agloc1 = model.getAgPos(0);
        Literal pos1 = Literal.parseLiteral("pos(bob" + 0 + "," + agloc1.x + "," + agloc1.y + ")");
        addPercept("ag2", pos1);*/
        
        for(int i=0; i<model.getnAgs(); i++) {
        	clearPercepts("bob"+i);
        	
        	//agent's location
	        Location agloc = model.getAgPos(0);
	        Literal pos = Literal.parseLiteral("pos(bob" + i + "," + agloc.x + "," + agloc.y + ")");
	        addPercept("bob"+i, pos);
	        //agent's panic scale
	        double panic = model.getAgPanic(i);
	        Literal agpanic = Literal.parseLiteral("panicscale(bob"+i+","+ panic +")");
	        addPercept("bob"+i,agpanic);
	        //agent's selflessness
	        double selfln = model.getAgPanic(i);
	        Literal agselfln = Literal.parseLiteral("selflessness(bob"+i+","+ selfln +")");
	        addPercept("bob"+i,agselfln);
	        
        }
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
