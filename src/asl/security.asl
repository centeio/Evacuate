// Agent seg in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- !move.

+!run: panicscale(_,P) <-
	segtellFire;
	alert;
	!run.
	
+!run: not panicscale(_,_)  <-
	.wait({+panicscale(X,Y)});
	!run.

+!move: panicscale(_,P) <-
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
	.print("Killing security");
	.my_name(N);
	killagent;
	.kill_agent(N).

+safe(X) <-
	.my_name(N);
	killagent;
	.kill_agent(N).