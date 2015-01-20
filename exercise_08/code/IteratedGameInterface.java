/**
 * A game starting in iteration 0 for players 0...numberOfPlayers-1 with available strategies 0...numberOfStrategies-1;
 * Note that any array returned is a COPY of the original data structure, i.e. manipulating the game history is not possible.
 * 
 * The statistics functions give you the possibility to see how all players interacted with each other.
 * Some functions are redundant, they were added for your convenience.
 * 
 * @author mablumen
 */

public interface IteratedGameInterface {
	public static final int ERROR = -1000000; // A constant number that is returned for illegitimate queries.
	
	/**
	 * Returns the number of available strategies.
	 * @return
	 */
	public int getNumberOfStrategies();

	/**
	 * Returns the number of players.
	 * @return
	 */
	public int getNumberOfPlayers();

	/**
	 * Returns the current iteration of the game. No statistics are available for the current iteration, of course.
	 * @return
	 */
	public int getCurrentIteration();
	
	/**
	 * Returns the last iteration of the game (where statistics are available), i.e. not the current.
	 * This returns -1 in iteration 0.
	 * @return
	 */
	public int getLastIteration();

	/*
	 * AUTOMATICALLY COLLECTED GAME STATISTICS
	 */
	
	/**
	 * Returns the strategy played by the 'attacker' against the 'victim' at a specified point in time.
	 * The current iteration of the game and the future cannot be queried (ERROR is returned).
	 * If attacker and victim are equal, ERROR is returned.
	 * @param time
	 * @param attacker
	 * @param victim
	 * @return
	 */
	public int getStrategy(int time, int attacker, int victim);

	/**
	 * Returns the strategies played by all players at a specified point in time.
	 * The current iteration of the game and the future cannot be queried (ERROR is returned).
	 * First coordinate: 'attacker', second coordinate: 'victim'.
	 * @param time
	 * @return
	 */
	public int[][] getStrategies(int time);

	/**
	 * Returns the score of a player at a given point in time.
	 * The current iteration of the game and the future cannot be queried (ERROR is returned).
	 * @param player
	 * @param time
	 * @return
	 */
	public float getScore(int player, int time);

	/**
	 * Returns the scores of all players at a given point in time.
	 * The current iteration of the game and the future cannot be queried (ERROR is returned).
	 * @param time
	 * @return
	 */
	public float[] getScores(int time);

	/**
	 * Returns the score of a specified player in the last iteration of the game.
	 * In iteration 0, ERROR is returned.
	 * Querying a non-existing player returns (float)ERROR.
	 * @param player
	 * @return
	 */
	public float getLastScore(int player);

	/**
	 * Returns the scores of all players in the last iteration of the game.
	 * In iteration 0, an array containing zeroes is returned.
	 * @return
	 */
	public float[] getLastScores();

	/**
	 * Returns (one of) the highest scoring player from the last iteration of the game.
	 * This always returns player 0 in iteration 0.
	 * @return
	 */
	public int getHighestScoringPlayer();
	
	/**
	 * Returns the highest score from the last iteration.
	 * @return
	 */
	public float getHighestScore();
	
	/*
	 * Access payoff function
	 */
	
	/**
	 * Returns the payoff for two given strategies.
	 * A positive value is beneficial for "me" (as in myStrategy), a negative value is detrimental.
	 * @param myStrategy
	 * @param opponentStrategy
	 * @return
	 */
	public float myPayoff(final int myStrategy, final int opponentStrategy);
	
	/**
	 * Returns the payoff for two given strategies.
	 * A positive value is beneficial for the opponent, a negative value is detrimental for him or her.
	 * @param myStrategy
	 * @param opponentStrategy
	 * @return
	 */
	public float opponentPayoff(final int myStrategy, final int opponentStrategy);
	
	/**
	 * Returns V in the hawk-dove game.
	 * @return
	 */
	public float getV();
	
	/**
	 * Returns C in the hawk-dove game.
	 * @return
	 */
	public float getC();
}