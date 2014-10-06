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

	private int TYPE_PIT = 0;
	private int TYPE_WUMPUS = 1;
	private int TYPE_EXPLORED = 2;
	private int TYPE_BUMP = 3;
	
	private int POS_Y = 0;
	private int POS_X = 1;

	private int agentPos[];
	private char agentDir;
	
	private int scoreMap[][][];
	// if scoreMap[][][TYPE_PIT] == -1 then probability of pit
	// if scoreMap[][][TYPE_PIT] <= -2 then high probability is of pit
	// if scoreMap[][][TYPE_PIT] == 1 then there is no pit
	// if scoreMap[][][TYPE_WUMPUS] == -1 then probability of wumpus
	// if scoreMap[][][TYPE_WUMPUS] <= -2 then high probability of wumpus
	// if scoreMap[][][TYPE_WUMPUS] == 1 then there is no wumpus
	// if scoreMap[][][TYPE_EXPLORED] == 0 then there is non-explored
	// if scoreMap[][][TYPE_EXPLORED] == 1 then there is explored
	// if scoreMap[][][TYPE_EXPLORED] == 2 then there is wall
	
	private boolean isFirstMoving;
	private boolean isShooted;
	private boolean isLastMoveBackward;
	private boolean mustShoot;
	private int stchRoomCnt;
	private boolean isFindWumpus;
	private boolean isBeforePerceptBrz;

	public AgentFunction(int worldSize, int[] agentPos, char agentDir) {
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
		stchRoomCnt = 0;
		isFindWumpus = false;
		this.worldSize = worldSize;
		
		//scoreMap = new int[this.worldSize][this.worldSize][3];
		scoreMap = new int[this.worldSize + 2][this.worldSize + 2][3];
		
		isFirstMoving = true;
		isShooted = false;
		mustShoot = false;
		isBeforePerceptBrz = false;

		for (int i = 0; i < worldSize + 2; ++i) {
			for (int j = 0; j < worldSize + 2; ++j) {
				scoreMap[i][j][TYPE_PIT] = 0;
				scoreMap[i][j][TYPE_WUMPUS] = 0;
				scoreMap[i][j][TYPE_EXPLORED] = 0;
			}
		}
		//scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] = 1;
		isLastMoveBackward = false;
		this.agentPos = new int[2];
		this.agentPos[POS_Y] = agentPos[POS_Y] + 1;
		this.agentPos[POS_X] = agentPos[POS_X] + 1;
		this.agentDir = agentDir;
	}

	public int process(TransferPercept tp) {
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

		// do something

		if (glitter) {
			return Action.GRAB;
		}

		if (scream) // Wumpus is not in map
		{
			for (int i = 0; i < worldSize; ++i) {
				for (int j = 0; j < worldSize; ++j) {
					scoreMap[i][j][TYPE_WUMPUS] = 1;
				}
			}
		}
		
		if (bump) {
			updateScoreMap(agentPos, TYPE_BUMP);
			if (agentDir == 'N') {
				if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] != 2) 
				{
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] != 2)
					{
						if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 0) {
							updateAgentDir(Action.TURN_LEFT);
							return Action.TURN_LEFT;
						} else {
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						}
					}
					else
					{
						updateAgentDir(Action.TURN_LEFT);
						return Action.TURN_LEFT;
					}
				}
				else // Agent is nearby west wall
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;
				}
			} else if (agentDir == 'S') {
				if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] != 2) 
				{
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] != 2)
					{
						if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 0) {
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						} else {
							updateAgentDir(Action.TURN_LEFT);
							return Action.TURN_LEFT;
						}
					}
					else
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					}
				}
				else
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
			} else if (agentDir == 'W') {
				if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] != 2) 
				{
					if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] != 2)
					{
						if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 0) {
							updateAgentDir(Action.TURN_LEFT);
							return Action.TURN_LEFT;
						} else {
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						}
					}
					else
					{
						updateAgentDir(Action.TURN_LEFT);
						return Action.TURN_LEFT;
					}
				}
				else 
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;
				}
			} else if (agentDir == 'E') {
				if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] != 2) 
				{
					if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] != 2)
					{
						if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 0) {
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						} else {
							updateAgentDir(Action.TURN_LEFT);
							return Action.TURN_LEFT;
						}
					}
					else
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					}
				}
				else 
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
			}
		}
		
		if (breeze || stench) {
			if (breeze && stench) // agent is nearby pit and wumpus
			{
				if(scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] == 0)
				{
					updateScoreMap(agentPos, TYPE_PIT);
					scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] = 0;
					updateScoreMap(agentPos, TYPE_WUMPUS);
				}
				isBeforePerceptBrz = true;
				if (isFirstMoving) {
					if (!isShooted) {
						// update scoreMap
						isShooted = true;
						return Action.SHOOT;
					} else {
						// return action
						isFirstMoving = false;
						updateAgentPos();
						return Action.GO_FORWARD;
					}
				} else {
					if (isLastMoveBackward && stchcnt == 0) {
						if (isBeforePerceptBrz) {
							++stchcnt;
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						} else if (!isShooted) {
							return Action.SHOOT;
						} else {
							isLastMoveBackward = false;
							updateAgentPos();
							return Action.GO_FORWARD;
						}
					}
					else if(isSafe(agentPos, agentDir))
					{
						isLastMoveBackward = false;
						updateAgentPos();
						return Action.GO_FORWARD;
					}
					// go to backward
					else if (stchcnt == 0) {
						if (mustShoot) {
							if (!isShooted) {
								isShooted = true;
								return Action.SHOOT;
							} else {
								++stchcnt;
								updateAgentDir(Action.TURN_RIGHT);
								return Action.TURN_RIGHT;
							}
						}
						++stchcnt;
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					} else if (stchcnt == 1) {
						++stchcnt;
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					} else if (stchcnt == 2) {
						stchcnt = 0;
						isFirstMoving = false;
						isLastMoveBackward = true;
						updateAgentPos();
						return Action.GO_FORWARD;
					}
				}
			} else {
				if (breeze) { // only breeze
					isBeforePerceptBrz = true;
					updateScoreMap(agentPos, TYPE_PIT);
					if (isFirstMoving) {
						// can't choose action because agent can't guess where
						// pit is.
						// choose random action
						int randNum = rand.nextInt(3);
						if(actionTable[randNum] == actionTable[0])
						{
							isFirstMoving = false;
							updateAgentPos();
						}
						else
						{
							updateAgentDir(actionTable[randNum]);
						}
						return actionTable[randNum];
					} else {
						if (isLastMoveBackward && brzcnt == 0) {
							if (isBeforePerceptBrz) {
								int randNum = rand.nextInt(3);
								if (actionTable[randNum] == actionTable[0]) {
									isLastMoveBackward = false;
									updateAgentPos();
								}
								else
								{
									updateAgentDir(actionTable[randNum]);
								}
								return actionTable[randNum];
							} else {
								isLastMoveBackward = false;
								updateAgentPos();
								return Action.GO_FORWARD;
							}
						}
						// go to backward
						else if (brzcnt == 0) {
							++brzcnt;
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						} else if (brzcnt == 1) {
							++brzcnt;
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						} else if (brzcnt == 2) {
							brzcnt = 0;
							isFirstMoving = false;
							isLastMoveBackward = true;
							updateAgentPos();
							return Action.GO_FORWARD;
						}
					}
				}
				if (stench) { // only stench
					updateScoreMap(agentPos, TYPE_WUMPUS);
					if (isBeforePerceptBrz) {
						if (isSafe(agentPos, agentDir)) {
							isBeforePerceptBrz = false;
							updateAgentPos();
							return Action.GO_FORWARD;
						}
					}
					isBeforePerceptBrz = false;
					if (isFirstMoving) {
						if (!isShooted) {
							isShooted = true;
							return Action.SHOOT;
						} else {
							isFirstMoving = false;
							updateAgentPos();
							return Action.GO_FORWARD;
						}
					} else {
						if (isLastMoveBackward) {
							if (!isShooted) {
								isShooted = true;
								return Action.SHOOT;
							} else {
								isLastMoveBackward = false;
								updateAgentPos();
								return Action.GO_FORWARD;
							}
						}
						else if(isSafe(agentPos, agentDir))
						{
							isLastMoveBackward = false;
							updateAgentPos();
							return Action.GO_FORWARD;
						}
						// go to backward
						else if (stchcnt == 0) {
							if (mustShoot) {
								if (!isShooted) {
									isShooted = true;
									return Action.SHOOT;
								} else {
									++stchcnt;
									updateAgentDir(Action.TURN_RIGHT);
									return Action.TURN_RIGHT;
								}
							}
							++stchcnt;
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						} else if (stchcnt == 1) {
							++stchcnt;
							updateAgentDir(Action.TURN_RIGHT);
							return Action.TURN_RIGHT;
						} else if (stchcnt == 2) {
							stchcnt = 0;
							isFirstMoving = false;
							isLastMoveBackward = true;
							updateAgentPos();
							return Action.GO_FORWARD;
						}
					}
				}
			}
		}

		updateScoreMap(agentPos, TYPE_EXPLORED);
		isBeforePerceptBrz = false;

		isLastMoveBackward = false;
		
		if (agentDir == 'N') {
			/*
			if (agentPos[POS_Y] < worldSize - 1) {
				if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0) {
					isFirstMoving = false;
					return Action.GO_FORWARD;
				} else if (agentPos[POS_X] + 1 < worldSize
						&& scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 0) {
					return Action.TURN_RIGHT;
				} else if (agentPos[POS_X] - 1 > 0
						&& scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 0) {
					return Action.TURN_LEFT;
				} else {
					isFirstMoving = false;
					return Action.GO_FORWARD;
				}
			} else // make percept bump
			{
				isFirstMoving = false;
				return Action.GO_FORWARD;
			}
			*/
			if(scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 2 ||
					scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 1)
			{
				if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 0 ||
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 2 )
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;
				}
				else if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 0)
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
				else
				{
					updateAgentPos();
					return Action.GO_FORWARD;
				}
			}
			else if(!isSafe(agentPos, agentDir))
			{
				//|| scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 2
				if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 0 )
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;
				}
				else if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 1 ) 
				{
					if(scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 2)
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					}
					else if(scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 1)
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					}
					else
					{
						updateAgentDir(Action.TURN_LEFT);
						return Action.TURN_LEFT;	
					}
				}
				else
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;	
				}
			}
			else //if(scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0)
			{
				isFirstMoving = false;
				updateAgentPos();
				return Action.GO_FORWARD;
			}
		} else if (agentDir == 'S') {
			if(scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 2 ||
					scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 1)
			{
				if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 0 ||
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 2 )
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
				else if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 0)
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_RIGHT;
				}
				else
				{
					updateAgentPos();
					return Action.GO_FORWARD;
				}
			}
			else if(!isSafe(agentPos, agentDir))
			{
				if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 0 )
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
				else if( scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 1 ) 
				{
					if(scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 2)
					{
						updateAgentDir(Action.TURN_LEFT);
						return Action.TURN_LEFT;
					}
					else if(scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 1)
					{
						updateAgentDir(Action.TURN_LEFT);
						return Action.TURN_LEFT;
					}
					else
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;	
					}
				}
				else
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;	
				}
			}
			else //if(scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0)
			{
				isFirstMoving = false;
				updateAgentPos();
				return Action.GO_FORWARD;
			}
		} else if (agentDir == 'W') {
			if(scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 2 ||
					scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 1)
			{
				if( scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0 ||
						scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 2 )
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;
				}
				else if( scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 0)
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
				else
				{
					updateAgentPos();
					return Action.GO_FORWARD;
				}
			}
			else if(!isSafe(agentPos, agentDir))
			{
				if( scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0 )
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;
				}
				else if( scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 1 ) 
				{
					if(scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 2)
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					}
					else if(scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 1)
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					}
					else
					{
						updateAgentDir(Action.TURN_LEFT);
						return Action.TURN_LEFT;	
					}
				}
				else
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;	
				}
			}
			else //if(scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0)
			{
				isFirstMoving = false;
				updateAgentPos();
				return Action.GO_FORWARD;
			}
		} else if (agentDir == 'E') {
			if(scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 2 ||
					scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_EXPLORED] == 1)
			{
				if( scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0 ||
						scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 2 )
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
				else if( scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 0)
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_RIGHT;
				}
				else
				{
					updateAgentPos();
					return Action.GO_FORWARD;
				}
			}
			else if(!isSafe(agentPos, agentDir))
			{
				if( scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0 )
				{
					updateAgentDir(Action.TURN_LEFT);
					return Action.TURN_LEFT;
				}
				else if( scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 1 ) 
				{
					if(scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 2)
					{
						updateAgentDir(Action.TURN_LEFT);
						return Action.TURN_LEFT;
					}
					else if(scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 1)
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;
					}
					else
					{
						updateAgentDir(Action.TURN_RIGHT);
						return Action.TURN_RIGHT;	
					}
				}
				else
				{
					updateAgentDir(Action.TURN_RIGHT);
					return Action.TURN_RIGHT;	
				}
			}
			else //if(scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 0)
			{
				isFirstMoving = false;
				updateAgentPos();
				return Action.GO_FORWARD;
			}
		}

		isFirstMoving = false;
		updateAgentPos();
		return Action.GO_FORWARD;
	}

	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}

	// if type == 0, then it is pit
	// if type == 1, then it is wumpus
	// if type == 2, then it is explored room
	// if type == 3, then it is process of wall
	void updateScoreMap(int agentPos[], int type) {
		switch (type) {
		case 0:
			if (scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] != 1) {
				if (agentPos[POS_Y] == 0) {
					if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_PIT] -= 1;
					}
				} else if (agentPos[POS_Y] == worldSize - 1) {
					if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_PIT] -= 1;
					}
				} else {
					if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_PIT] -= 1;
					}

					if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_PIT] -= 1;
					}
				}

				if (agentPos[POS_X] == 0) {
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_PIT] -= 1;
					}
				} else if (agentPos[POS_X] == worldSize - 1) {
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_PIT] -= 1;
					}
				} else {
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_PIT] -= 1;
					}
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_PIT] != 1) {
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_PIT] -= 1;
					}
				}
				scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_PIT] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_WUMPUS] = 1;
			}
			break;
		case 1:
			if (scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] != 1) {
				if (!isFindWumpus) {
					++stchRoomCnt;

					if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_WUMPUS] != 1) {
						scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_WUMPUS] -= 1;
					}

					if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_WUMPUS] != 1) {
						scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_WUMPUS] -= 1;
					}
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_WUMPUS] != 1) {
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_WUMPUS] -= 1;
					}
					if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_WUMPUS] != 1) {
						scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_WUMPUS] -= 1;
					}
					
					int wumpusRoomCnt = 0;
					for(int i = 0 ; i < worldSize + 2 ; ++i)
					{
						for(int j = 0 ; j < worldSize + 2 ; ++j)
						{
							if (scoreMap[i][j][TYPE_WUMPUS] <= -2)
							{
								++wumpusRoomCnt;
							}
						}
					}
					if (wumpusRoomCnt == 1)
					{
						for(int i = 0 ; i < worldSize + 2 ; ++i)
						{
							for(int j = 0 ; j < worldSize + 2 ; ++j)
							{
								if (scoreMap[i][j][TYPE_WUMPUS] > -2)
								{
									scoreMap[i][j][TYPE_WUMPUS] = 1;
								}
								else
								{
									scoreMap[i][j][TYPE_WUMPUS] = -5;
									isFindWumpus = true;
								}
								
							}
						}
					}
					if (stchRoomCnt >= 2) {
						int wumpusRoom[][] = new int[2][3];
						int cnt = 0;
						for (int i = 0; i < worldSize + 2; ++i) {
							for (int j = 0; j < worldSize + 2; ++j) {
								if (scoreMap[i][j][TYPE_WUMPUS] <= -2) {
									wumpusRoom[cnt][POS_Y] = i;
									wumpusRoom[cnt][POS_X] = j;
									wumpusRoom[cnt++][2] = scoreMap[i][j][TYPE_WUMPUS];
								}
							}
						}

						if (cnt == 2) {
							for (int i = 0; i < worldSize + 2; ++i) {
								for (int j = 0; j < worldSize + 2; ++j) {
									scoreMap[i][j][TYPE_WUMPUS] = 1;
								}
							}
							if (wumpusRoom[0][2] == wumpusRoom[1][2]) {
								scoreMap[wumpusRoom[0][POS_Y]][wumpusRoom[0][POS_X]][TYPE_WUMPUS] = -5;
								scoreMap[wumpusRoom[1][POS_Y]][wumpusRoom[1][POS_X]][TYPE_WUMPUS] = -5;
								mustShoot = true;
							} else if (wumpusRoom[0][2] < wumpusRoom[1][2]) {
								scoreMap[wumpusRoom[0][POS_Y]][wumpusRoom[0][POS_X]][TYPE_WUMPUS] = -5;
								scoreMap[wumpusRoom[1][POS_Y]][wumpusRoom[1][POS_X]][TYPE_WUMPUS] = 1;
								isFindWumpus = true;
							} else {
								scoreMap[wumpusRoom[0][POS_Y]][wumpusRoom[0][POS_X]][TYPE_WUMPUS] = 1;
								scoreMap[wumpusRoom[1][POS_Y]][wumpusRoom[1][POS_X]][TYPE_WUMPUS] = -5;
								isFindWumpus = true;
							}
						} else if (cnt == 1) {
							for (int i = 0; i < worldSize + 2; ++i) {
								for (int j = 0; j < worldSize + 2; ++j) {
									scoreMap[i][j][TYPE_WUMPUS] = 1;
								}
							}
							scoreMap[wumpusRoom[0][POS_Y]][wumpusRoom[0][POS_X]][TYPE_WUMPUS] = -5;
							isFindWumpus = true;
						}
					}
					scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] = 1;
					scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_PIT] = 1;
					scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_WUMPUS] = 1;
				}
			}
			break;
		case 2:
			if (scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] != 1) {
				scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_PIT] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_WUMPUS] = 1;
				scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_PIT] = 1;
				scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_WUMPUS] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_PIT] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_WUMPUS] = 1;
				scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_PIT] = 1;
				scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_WUMPUS] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_PIT] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_WUMPUS] = 1;
				scoreMap[agentPos[POS_Y]][agentPos[POS_X]][TYPE_EXPLORED] = 1;
			}
			break;
		case 3:
			if (agentDir == 'N' || agentDir == 'S') {
				for (int i = 0; i < worldSize + 2; ++i) {
					scoreMap[agentPos[POS_Y]][i][TYPE_EXPLORED] = 2;
					scoreMap[agentPos[POS_Y]][i][TYPE_PIT] = 1;
					scoreMap[agentPos[POS_Y]][i][TYPE_WUMPUS] = 1;
				}
				if (agentDir == 'N') {
					agentPos[POS_Y] -= 1;
				} else {
					agentPos[POS_Y] += 1;
				}
			} else {
				for (int i = 0; i < worldSize + 2; ++i) {
					scoreMap[i][agentPos[POS_X]][TYPE_PIT] = 1;
					scoreMap[i][agentPos[POS_X]][TYPE_WUMPUS] = 1;
					scoreMap[i][agentPos[POS_X]][TYPE_EXPLORED] = 2;
				}
				if (agentDir == 'E') {
					agentPos[POS_X] -= 1;
				} else {
					agentPos[POS_X] += 1;
				}
			}
			break;
		}
	}

	boolean isSafe(int[] agentPos, char agentDir) {
		if (agentDir == 'N') {
			if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_EXPLORED] == 2) {
				return true;
			} else {
				if (scoreMap[agentPos[POS_Y] + 1][agentPos[POS_X]][TYPE_WUMPUS] >= 0) {
					return true;
				} else {
					return false;
				}
			}
		} else if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_EXPLORED] == 2) {
			if (agentPos[POS_Y] == 0) {
				return true;
			} else {
				if (scoreMap[agentPos[POS_Y] - 1][agentPos[POS_X]][TYPE_WUMPUS] >= 0) {
					return true;
				} else {
					return false;
				}
			}
		} else if (agentDir == 'W') {
			if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_EXPLORED] == 2) {
				return true;
			} else {
				if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] - 1][TYPE_WUMPUS] >= 0) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			if (scoreMap[agentPos[POS_Y]+1][agentPos[POS_X] + 1][TYPE_EXPLORED] == 2) {
				return true;
			} else {
				if (scoreMap[agentPos[POS_Y]][agentPos[POS_X] + 1][TYPE_WUMPUS] >= 0) {
					return true;
				} else {
					return false;
				}
			}
		}
	}
	
	void updateAgentPos()
	{
		if(agentDir == 'N'){
			agentPos[POS_Y] += 1;
		}
		else if(agentDir == 'S'){
			agentPos[POS_Y] -= 1;
		}
		else if(agentDir == 'W'){
			agentPos[POS_X] -= 1;
		}
		else if(agentDir == 'E'){
			agentPos[POS_X] += 1;
		}
	}
	void updateAgentDir(int turnDir)
	{
		if(turnDir == Action.TURN_RIGHT)
		{
			if(agentDir == 'N')
			{
				agentDir = 'E';
			}
			else if(agentDir == 'E')
			{
				agentDir = 'S';
			}
			else if(agentDir == 'S')
			{
				agentDir = 'W';
			}
			else if(agentDir == 'W')
			{
				agentDir = 'N';
			}
		}
		else if(turnDir == Action.TURN_LEFT)
		{
			if(agentDir == 'N')
			{
				agentDir = 'W';
			}
			else if(agentDir == 'W')
			{
				agentDir = 'S';
			}
			else if(agentDir == 'S')
			{
				agentDir = 'E';
			}
			else if(agentDir == 'E')
			{
				agentDir = 'N';
			}
		}
	}
}