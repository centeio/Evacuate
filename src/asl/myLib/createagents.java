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
        
        for(int i = 0; i < Integer.parseInt(args[0].toString()); i++) {
        	Settings s = new Settings();

            RuntimeServicesInfraTier rs = ts.getUserAgArch().getRuntimeServices();
            String name = "Bob"+i;
            name = rs.createAgent(name, "ag1.asl", null, null, null, s, ts.getAg());
            rs.startAgent(name);
        }
        
        // everything ok, so returns true
        return true;
    }
}
