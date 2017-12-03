// Agent enviroment in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	myLib.createagents(10, 2);
	createFire;
	.broadcast(tell, accidentEnv);
	!continue.
	
+!continue : true <-
	environment;
	!continue.
