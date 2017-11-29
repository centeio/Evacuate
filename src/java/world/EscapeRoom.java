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
                panic(agName);
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
    
	public void panic(String agent) {
		Random randomGenerator = new Random(System.currentTimeMillis());
		double panic = randomGenerator.nextInt(10)/10.0;
		Literal p = Literal.parseLiteral("panicscale("+ panic + ").");
		addPercept(agent, p);		
	}    
	
		
    void updatePercepts() {
        clearPercepts();

        Location r1Loc = model.getAgPos(0);
        Literal pos1 = Literal.parseLiteral("pos(ag1," + r1Loc.x + "," + r1Loc.y + ")");
        addPercept(pos1);
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
