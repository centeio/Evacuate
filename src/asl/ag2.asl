// Agent ag2 in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- 
	.print("started waiting");
	wait;
	.print("finished waiting");
	.broadcast(tell, accident).


+fire[source(Ag)]
   :  Ag \== self
   <- .print("AAAAAHHHH! Agent ", Ag, " told me there's a fire");
   .broadcast(tell,fire).