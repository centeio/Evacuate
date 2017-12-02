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

+!move: panicscale(_,P) <- 
	.print(P);
	.print("randomly moving");
	move;
	!move.

-!move: not panicscale(_,_)  <-
	.wait("+panicscale(X,Y)", 3000);
	!move.
	
+!nextplan: panicscale(_,P) & P <= 0.5 <-
	!move.

+!nextplan: panicscale(_,P) & P > 0.5 <-
	!run.
		
+accident <-
	.succeed_goal(move);
	panicscale;
	!nextplan;
	.print("panicscale changed").

+fire[source(Ag)] :  Ag \== self
<- .print("Agent ", Ag, " said there's a fire!! Run!!").
