// Agent sample_agent in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").

+fire[source(Ag)] :  Ag \== self
<- .print("Agent ", Ag, " said there's a fire!! Run!!").