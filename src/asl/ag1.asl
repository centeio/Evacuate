// Agent ag1 in project escape

/* Initial beliefs and rules */

injuryscale(0).
panicscale(0).
selflessness(0.5).
posX(5).
posY(5).

/* Initial goals */

!start.

/* Plans */

+!start : true <- !move.
	
+!move : panicscale(P) & P > 0.5 <-
	.print(P);
	.print("moving!!!");
	alert;
	!move.

+!move : panicscale(P) & P < 0.5 <- 
	.print(P);
	.print("randomly moving");
	move;
	!move.
		
+accident : true <- 
	panicscale;
	.print("panicscale changed").

+fire[source(Ag)] :  Ag \== self
<- .print("Agent ", Ag, " said there's a fire!! Run!!").
