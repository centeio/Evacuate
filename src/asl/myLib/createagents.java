// Internal action code for project escape
package myLib;

import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.runtime.RuntimeServicesInfraTier;
import jason.runtime.Settings;

public class createagents extends DefaultInternalAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // execute the internal action
        ts.getAg().getLogger().info("executing internal action 'world.CreateAgents'");
        
        int numAgents = Integer.parseInt(args[0].toString()), numSegs = Integer.parseInt(args[1].toString());
        
        for(int i = 0; i < numAgents; i++) {
        	Settings s = new Settings();

            RuntimeServicesInfraTier rs = ts.getUserAgArch().getRuntimeServices();
            String name = "Bob"+i;
            name = rs.createAgent(name, "ag1.asl", null, null, null, s, ts.getAg());
            rs.startAgent(name);
        }
        
        for(int i = numAgents; i < numAgents + numSegs; i++) {
        	Settings s = new Settings();

            RuntimeServicesInfraTier rs = ts.getUserAgArch().getRuntimeServices();
            String name = "Seg"+i;
            name = rs.createAgent(name, "seg.asl", null, null, null, s, ts.getAg());
            rs.startAgent(name);
        }
        
        // everything ok, so returns true
        return true;
    }
}
