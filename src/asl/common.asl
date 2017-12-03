// Agent ag1 in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start <-
	.print("started");
	!move.
	
+!run: panicscale(_,P) <-
	.print(P);
	.print("moving!!!");
	alert;
	!run.
	
+!run: not panicscale(_,_)  <-
	.wait("+panicscale(X,Y)", 1000);
	!run.

+!move: panicscale(_,P) <- 
	.print(P);
	.print("randomly moving");
	move;
	!move.

+!move: not panicscale(_,_)  <-
	.wait("+panicscale(X,Y)", 1000);
	!move.
	
+!nextplan: panicscale(_,P) & P <= 0.5 <-
	!move.

+!nextplan: panicscale(_,P) & P > 0.5 <-
	!run.
	
+!nextplan: not panicscale(_,P) <-
	.wait("+panicscale(X,Y)", 1000);
	!nextplan.
		
+accident <-
	.succeed_goal(move);
	panicscale;
	!nextplan;
	.print("panicscale changed").
	
+accidentEnv <-
	.succeed_goal(move);
	panicscale(environment);
	!nextplan;
	.print("panicscale changed").
	
	
