// Agent enviroment in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	myLib.createagents(10, 2);
	start;
	.wait(5000);
	createFire;
	.broadcast(tell, accidentEnv);
	!continue.
	
+!continue : true <-
	environment;
	!continue.
	
+shutdown : true <-
	.stopMAS.
