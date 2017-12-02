// Agent ag1 in project escape

/* Initial beliefs and rules */
panicscale(0.0).

/* Initial goals */

!start.

/* Plans */

+!start : true <- !move.
	
+!move : panicscale(P) & P > 0.5 <-
	.print(P);
	.print("moving!!!");
	alert;
	!move.

+!move : panicscale(P) & P <= 0.5 <- 
	.print(P);
	.print("randomly moving");
	move;
	!move.

/*-!move<-
	!move.*/
	
		
+accident : true <- 
	panicscale;
	.print("panicscale changed").

+fire[source(Ag)] :  Ag \== self
<- .print("Agent ", Ag, " said there's a fire!! Run!!").
