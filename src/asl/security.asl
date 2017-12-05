// Agent seg in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- !move.

+!run: panicscale(_,P) <-
	.print(P);
	.print("moving!!!");
	segtellFire;
	alert;
	!run.
	
+!run: not panicscale(_,_)  <-
	.wait({+panicscale(X,Y)});
	!run.

+!move: panicscale(_,P) <- 
	.print(P);
	.print("randomly moving");
	move;
	!move.

+!move: not panicscale(_,_)  <-
	.wait({+panicscale(X,Y)});
	!move.
	
+!nextplan: panicscale(_,P) & P <= 0.5 <-
	!move.

+!nextplan: panicscale(_,P) & P > 0.5 <-
	!run.
	
+!nextplan: not panicscale(_,P) <-
	.wait({+panicscale(X,Y)});
	!nextplan.

+accidentEnv <-
	.succeed_goal(move);
	panicscale(environment);
	!nextplan.
	
+injuryscale(X,Y) : Y == 1 <-
	.my_name(N);
	killagent;
	.kill_agent(N).
