// Agent ag1 in project escape

/* Initial beliefs and rules */

/* Initial goals */

!move_randomly.

/* Plans */

+!move_randomly : true <- 
	move(agent);
	!move_randomly.

+fire[source(Ag)] :  Ag \== self
<- .print("Agent ", Ag, " said there's a fire!! Run!!").
