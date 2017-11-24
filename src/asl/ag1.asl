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

+!start : true <- !move_randomly.

+!move_randomly : true <- 
	.print("randomly moving");
	move;
	!move_randomly.
	
+!move_accident : panicscale(P) & P > 0.5 <-
	.print("moving!!!");
	alert;
/* -!move_randomly */	
	!move_accident.

+!move_accident : true <-
	.print("eheh");
	!move_randomly.
	
+accident: true <- 
	panicscale;
	.print("panicscale changed");
	!move_accident.

+fire[source(Ag)] :  Ag \== self
<- .print("Agent ", Ag, " said there's a fire!! Run!!").
