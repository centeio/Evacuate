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
	
+!start : mode(N) & N == create & width(W) & height(H) & createAgents(CA) & createSecurity(CS) <- 
	myLib.createmap(W, H, CA, CS).
	
+!start : not mode(_) <-
	.wait({+mode(X)});
	!start.
	
+!start : mode(N) & N == run & not numberAgents(_) <-
	.wait({+numberAgents(X)});
	!start.
	
+!start : mode(N) & N == run & not numberSecurity(_) <-
	.wait({+numberSecurity(X)});
	!start.
	
+!start : mode(N) & N == create & not width(_) <-
	.wait({+width(X)});
	!start.
	
+!start : mode(N) & N == create & not height(_) <-
	.wait({+height(X)});
	!start.
	
+!start : mode(N) & N == create & not createAgents(_) <-
	.wait({+createAgents(X)});
	!start.
	
+!start : mode(N) & N == create & not createSecurity(_) <-
	.wait({+createSecurity(X)});
	!start.
	
+!continue : true <-
	environment;
	!continue.
	
+shutdown : true <-
	.stopMAS.
	
+width(_) : true <-
	.print("Received width").
+height(_) : true <-
	.print("Received height").
+createAgents(_) : true <-
	.print("Received agents").
+createSecurity(_) : true <-
	.print("Received security").

