// Agent seg in project escape

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").

+accidentEnv <-
	segtellFire.
