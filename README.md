# aquaticadventures
this is the sea world project
So we have the following Organisms
Sharks (predator)
Orcas (Apex predator)
Whales (predator)
Angler Fish (nocturnal predator)
Tuna (a predator)
cod (prey)
salmon (prey)
algea (plant)



Overall process we are looking at:

Sharks:
  eat tuna, cod and salmon
  Live for max of 70 years
  can breed at 5 years old
  breeding probability 0.04
  max litter size 3

Orcas:
  can eat shark
  has to eat sharks every 15 steps
  live for a max of 80 years
  can breed at 9 years
  breeding probability 0.02
  max litter size 2
  
Tuna:
  eat cod and salmon
  has to eat either every 5 steps
  live for a max of 20 years
  breeding probability 0.12
  max litter size 5
  
Whale:
  eat Tuna and cod and salmon and algae
  has to eat any every 25 steps
  live for 100 years
  can breed at 15
  breeding probability 0.03
  max litter size 1
  
cod:
  eats algae
  has to eat every 3 steps
  live for 10 years
  can breed at 2 years
  breeding probability 0.15
  max litter size 7

Salmon:
  eats algae 
  has to eat every 2 steps
  live for 8 yeears
  can breed at 1 years
  breeding probabilty 0.18
  max litter size 8


angelar fish:
  eat cod and salmon
  has to eat every 7 steps
  they are nocturnal
  live for 40 years
  can breed at 10 years
  breeding probability of 0.1
  max litter size 4

disease slows Organism down and if Organism with disease is eaten then it becomes diseased itself + if 2 Organisms breed they will pass on disease

warmer temperatures lead to faster processes:
  require more food 
  don't move as much
  breed more often



bug: 
there was a bug in the SimulatoeView class in which the dead animals were being shown as alive and taking up space causing discrpencies between the real simulation and the GUI
this was fixed by a .isAlive() check on the showStatus() method in simulator view