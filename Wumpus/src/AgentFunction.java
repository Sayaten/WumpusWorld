/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.util.Random;

class AgentFunction {

	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";

	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private int[] actionTable;
	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;
	private Random rand;

	// added variable
	private int brzcnt;
	private int stchcnt;

	private int worldSize;
	private int scoremap[][][];
	// if scoremap[][][0] == -1 then probability of pit
	// if scoremap[][][0] <= -2 then high probability is of pit
	// if scoremap[][][0] == 1 then there is no pit
	// if scoremap[][][1] == -1 then probability of wumpus
	// if scoremap[][][1] <= -2 then high probability of wumpus
	// if scoremap[][][1] == 1 then there is no wumpus
	// if scoremap[][][2] == 0 then there is non-explored
	// if scoremap[][][2] == 1 then there is explored

	private char map[][][];

	private boolean isFirstMoving;
	private boolean isShooted;

	public AgentFunction(int worldSize, int[] agentPos) {
		// for illustration purposes; you may delete all code
		// inside this constructor when implementing your
		// own intelligent agent

		// this integer array will store the agent actions
		actionTable = new int[5];

		actionTable[0] = Action.GO_FORWARD;
		actionTable[1] = Action.TURN_RIGHT;
		actionTable[2] = Action.TURN_LEFT;
		actionTable[3] = Action.GRAB;
		actionTable[4] = Action.SHOOT;

		// new random number generator, for
		// randomly picking actions to execute
		rand = new Random();

		// added initial code
		brzcnt = 0;
		stchcnt = 0;
		this.worldSize = worldSize;
		scoremap = new int[this.worldSize][this.worldSize][3];
		isFirstMoving = true;
		isShooted = false;

		for (int i = 0; i < worldSize; ++i) {
			for (int j = 0; j < worldSize; ++j) {
				scoremap[i][j][0] = 0;
				scoremap[i][j][1] = 0;
				scoremap[i][j][2] = 0;
			}
		}
		scoremap[agentPos[0]][agentPos[1]][2] = 1;
	}

	public int process(TransferPercept tp, int[] agentPos, int agentDir) {
		// To build your own intelligent agent, replace
		// all code below this comment block. You have
		// access to all percepts through the object
		// 'tp' as illustrated here:

		// read in the current percepts
		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();

		/*
		 * if (bump == true || glitter == true || breeze == true || stench ==
		 * true || scream == true) { // do something }
		 */

		// do something

		scoremap[agentPos[0]][agentPos[1]][2] = 1;

		if (glitter) {
			return Action.GRAB;
		}
		
		// not fished
		/*
		if (breeze || stench)
		{
			if (breeze && stench) 
			{
				if(isFirstMoving)
				{
					if(!isShooted)
					{
						isShooted = true;
						return Action.SHOOT;
					}
					else
					{
						// update score map
					
						// return action
						isFirstMoving = false;
						return Action.GO_FORWARD;
					}
				}	
				else
				{
					if (brzcnt == 0) {
						++brzcnt;
						return Action.TURN_RIGHT;
					} else if (brzcnt == 1) {
						++brzcnt;
						return Action.TURN_RIGHT;
					} else if (brzcnt == 2) {
						brzcnt = 0;
						isFirstMoving = false;
						return Action.GO_FORWARD;
					}
				}
			} else {
				if (breeze) { // only breeze
					// update score map
					if (agentPos[0] == 0) {
						if(scoremap[agentPos[0] + 1][agentPos[1]][0] != 1)
						{
							scoremap[agentPos[0] + 1][agentPos[1]][0] -= 2;
						}
					} else if (agentPos[0] == worldSize - 1) {
						if(scoremap[agentPos[0] - 1][agentPos[1]][0] != 1)
						{
							scoremap[agentPos[0] - 1][agentPos[1]][0] -= 2;
						}
					} else {
						if(scoremap[agentPos[0] + 1][agentPos[1]][0] != 1)
						{
							scoremap[agentPos[0] + 1][agentPos[1]][0] -= 2;
						}

						if(scoremap[agentPos[0] - 1][agentPos[1]][0] != 1)
						{
							scoremap[agentPos[0] - 1][agentPos[1]][0] -= 2;
						}
					}

					if (agentPos[1] == 0) {
						if(scoremap[agentPos[0]][agentPos[1] + 1][0] != 1)
						{
							scoremap[agentPos[0]][agentPos[1] + 1][0] -= 2;
						}
					} else if (agentPos[1] == worldSize - 1) {
						if(scoremap[agentPos[0]][agentPos[1] + 1][1] != 1)
						{
							scoremap[agentPos[0]][agentPos[1] + 1][1] -= 2;
						}
					} else {
						if(scoremap[agentPos[0]][agentPos[1] + 1][0] != 1)
						{
							scoremap[agentPos[0]][agentPos[1] + 1][0] -= 2;
						}
						if(scoremap[agentPos[0]][agentPos[1] + 1][1] != 1)
						{
							scoremap[agentPos[0]][agentPos[1] + 1][1] -= 2;
						}
					}

					// return action
					if (isFirstMoving) {
						isFirstMoving = false;
						return actionTable[rand.nextInt(3)];
					} else {
						if (brzcnt == 0) {
							++brzcnt;
							return Action.TURN_RIGHT;
						} else if (brzcnt == 1) {
							++brzcnt;
							return Action.TURN_RIGHT;
						} else if (brzcnt == 2) {
							brzcnt = 0;
							isFirstMoving = false;
							return Action.GO_FORWARD;
						}
					}
				} else { // only stench
					if (isFirstMoving) {
						if (!isShooted) {
							return Action.SHOOT;
						} else {
							isFirstMoving = false;
							return Action.GO_FORWARD;
						}
					} else {
						if (stchcnt == 0) {
							++brzcnt;
							return Action.TURN_RIGHT;
						} else if (stchcnt == 1) {
							++brzcnt;
							return Action.TURN_RIGHT;
						} else if (stchcnt == 2) {
							brzcnt = 0;
							isFirstMoving = false;
							return Action.GO_FORWARD;
						}
					}
				}
			}
		}
		*/
		
		if (bump) {
			if (agentDir == 'N') {
				if (agentPos[1] - 1 >= 0) // Agent is far form west wall
				{
					if (agentPos[1] + 1 < worldSize) // Agent is far from west and east wall
					{
						if (scoremap[agentPos[0]][agentPos[1] - 1][2] == 0) {
							return Action.TURN_LEFT;
						} else {
							return Action.TURN_RIGHT;
						}
					} else // Agent is near by east wall
					{
						return Action.TURN_LEFT;
					}
				} else // Agent is near by west wall
				{
					return Action.TURN_RIGHT;
				}
			} else if (agentDir == 'S') {
				if (agentPos[1] - 1 >= 0) // Agent is far form west wall
				{
					if (agentPos[1] + 1 < worldSize) // Agent is far from west and east wall
					{
						if (scoremap[agentPos[0]][agentPos[1] - 1][2] == 0) {
							return Action.TURN_RIGHT;
						} else {
							return Action.TURN_LEFT;
						}
					} else // Agent is near by east wall
					{
						return Action.TURN_RIGHT;
					}
				} else // Agent is near by west wall
				{
					return Action.TURN_LEFT;
				}
			} else if (agentDir == 'W') {
				if (agentPos[0] - 1 >= 0) // Agent is far from south wall
				{
					if (agentPos[0] + 1 < worldSize) // Agent is far from south and north wall
					{
						if (scoremap[agentPos[0] - 1][agentPos[1]][2] == 0) {
							return Action.TURN_LEFT;
						} else {
							return Action.TURN_RIGHT;
						}
					} else // Agent is near by north wall
					{
						return Action.TURN_LEFT;
					}
				} else // Agent is near by south wall
				{
					return Action.TURN_RIGHT;
				}
			} else if (agentDir == 'E') {
				if (agentPos[0] - 1 >= 0) // Agent is far from south wall
				{
					if (agentPos[0] + 1 < worldSize) // Agent is far from south and north wall
					{
						if (scoremap[agentPos[0] - 1][agentPos[1]][2] == 0) {
							return Action.TURN_RIGHT;
						} else {
							return Action.TURN_LEFT;
						}
					} else // Agent is near by north wall
					{
						return Action.TURN_RIGHT;
					}
				} else // Agent is near by south wall
				{
					return Action.TURN_LEFT;
				}
			}
		}
		
		if (agentDir == 'N')
		{
			if(agentPos[0] < worldSize - 1)
			{
				if( scoremap[agentPos[0] + 1][agentPos[1]][2] == 0 )
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
				else if( agentPos[1] + 1 < worldSize && scoremap[agentPos[0]][agentPos[1] + 1][2] == 0 )
				{
					return Action.TURN_RIGHT;
				}
				else if( agentPos[1] - 1 > 0 && scoremap[agentPos[0]][agentPos[1] - 1][2] == 0 )
				{
					return Action.TURN_LEFT;
				}
				else
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
			}
			else // make percept bump
			{
				isFirstMoving = false;
				return Action.GO_FORWARD;
			}
		}
		else if(agentDir == 'S')
		{
			if(agentPos[0] > 0)
			{
				if( scoremap[agentPos[0] - 1][agentPos[1]][2] == 0 )
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
				else if( agentPos[1] + 1 < worldSize && scoremap[agentPos[0]][agentPos[1] + 1][2] == 0 )
				{
					return Action.TURN_LEFT;
				}
				else if( agentPos[1] - 1 > 0 && scoremap[agentPos[0]][agentPos[1] - 1][2] == 0 )
				{
					return Action.TURN_RIGHT;
				}
				else
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
			}
			else // make percept bump
			{
				isFirstMoving = false;
				return Action.GO_FORWARD;
			}
		}
		else if(agentDir == 'W')
		{
			if(agentPos[1] > 0)
			{
				if( scoremap[agentPos[0]][agentPos[1] + 1][2] == 0 )
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
				else if( agentPos[0] + 1 < worldSize && scoremap[agentPos[0] + 1][agentPos[1]][2] == 0 )
				{
					return Action.TURN_RIGHT;
				}
				else if( agentPos[0] - 1 > 0 && scoremap[agentPos[0] - 1][agentPos[1]][2] == 0 )
				{
					return Action.TURN_LEFT;
				}
				else
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
			}
			else // make percept bump
			{
				isFirstMoving = false;
				return Action.GO_FORWARD;
			}
		}
		else if(agentDir == 'E')
		{
			if(agentPos[1] < worldSize - 1)
			{
				if( scoremap[agentPos[0]][agentPos[1] + 1][2] == 0 )
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
				else if( agentPos[0] + 1 < worldSize && scoremap[agentPos[0] + 1][agentPos[1]][2] == 0 )
				{
					return Action.TURN_LEFT;
				}
				else if( agentPos[0] - 1 > 0 && scoremap[agentPos[0] - 1][agentPos[1]][2] == 0 )
				{
					return Action.TURN_RIGHT;
				}
				else
				{
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
			}
			else // make percept bump
			{
				isFirstMoving = false;
				return Action.GO_FORWARD;
			}
		}

		isFirstMoving = false;
		return Action.GO_FORWARD;
		// return action to be performed
		// return actionTable[rand.nextInt(8)];
	}

	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}