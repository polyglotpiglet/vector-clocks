# vector-clocks

BADDIE didn't want to do homework with pen and paper so wrote some cr4ppy scala code to do it. 

Lots of duplicate computation, code kind of embarrassing but I needed to rush it to be able to use it. :D 

Usage should be clear from the tests, but here is a little example. 

Lookee here: 

![](https://github.com/polyglotpiglet/vector-clocks/blob/master/figs/Fig3.png)


You have three processes, P0, P1 and P2. There are local process 'events' (eg receiving a message from an external client process) which are shown with the blue dots. Also, the three processes send messages to each other. In this example there are four messages: M1 sent from P2 to P1, M2 sent from P0 to P1 etc. 

You can use the LamportEvaluator to return the lamport timestamps at each point: 

![](https://github.com/polyglotpiglet/vector-clocks/blob/master/figs/Fig3Lamport.png)

You can use the VectorClockEvaluator to return the vector clocks at each point: 

![](https://github.com/polyglotpiglet/vector-clocks/blob/master/figs/Fig3VectorClock.png)

