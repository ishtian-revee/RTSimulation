# RTSimulation

Partial implementation of Real Time Edge Detection Simulation using **OpenCV**
library in android. This repository contains only the edge and contour detection
using computer vision. The output is to have what computer vision looks like
after edge and contour detection.

## Project Concept

This general idea of this project is to have the taste of computer vision integrating
in android. First to get the real time edge detection and contour detection
using **OpenCV** modules for computer vision. After successfully detection edges
need to implement some cool game type simulations.

The simulation concept is something like this, after successfully detecting edges,
need to attach ***rigid body*** component over the edges. Therefore, simple physics library
can be implemented over the edges.

After implementation of this rigid body over the edges we can implement some
simulations like, there is a ball which is falling form top of the screen. And
there is a target hole some where bottom of the screen. The goal is to put the
ball into that target. But there is no input or gesture events are occurring on
the app. Therefore user have no control over the app. So the question is how to
control the ball or what actions need to be done to get the ball into that target.

This is what this project is offering about. See there is only 2 things are available
in the app. A ball at the top middle of the screen and a target which is located bottom right
of the screen. If the ball is falling from middle position it will go to end of the screen
without even touching the target. So what user can do is they can set the phone
just a bit top of a white page in a way so that the camera device can get the whole page.
Now if they draw a line on the page with a pencil or pen, what will the app is going
to do is that it will detect that line or edges of that line and implement rigid body
on it. This will be done in real time. Therefore, after detecting that line there
will be 3 things available on the app including the line. And now the ball can be
affected by that line because it has physics components included in it. As a
result, the ball will have a collision with that line and reflect according to physics
rules. Now the main task of the users is to draw some line or lines in a way so
that the ball will get to the target after several collision with those line objects.

So the overall project idea can be represent like this:

**a ball or some continuous object simulation -> open camera device -> real time
object, edge and contour detection -> rigid body implementation -> new rigid body
object creation -> necessary collision or events with all objects -> finish
the target simulation**
