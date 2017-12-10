// Agent enviroment in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : mode(N) & N == run & numberAgents(Agents) & numberSecurity(Security) <-
	myLib.createagents(Agents, Security);
	start;
	.wait(5000);
	createFire;
	.broadcast(tell, accidentEnv);
	!continue.
	
+!start : mode(N) & N == create <- 
	myLib.createmap(48, 27, 10, 2).
	
+!start : not mode(_) <-
	.wait({+mode(X)});
	!start.
	
+!start : not numberAgents(_) <-
	.wait({+numberAgents(X)});
	!start.
	
+!start : not numberSecurity(_) <-
	.wait({+numberSecurity(X)});
	!start.
	
+!continue : true <-
	environment;
	!continue.
