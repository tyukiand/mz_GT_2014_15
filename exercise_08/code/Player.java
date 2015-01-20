
public abstract class Player {
	protected final IteratedGameInterface game;
	protected final int playerId;
	protected String name;
	
	public Player(final IteratedGameInterface game, final int playerID) {
		this.game = game;
		this.playerId = playerID;
	}
	
	/**
	 * Returns this player's ID in the game.
	 * @return
	 */
	public final int getMyID() {
		return this.playerId;
	}
	
	/**
	 * Returns the name of the player.
	 * @return
	 */
	public final String getName() {
		return this.name;
	}
	
	public final float getScore() {
		return this.game.getLastScore(this.getMyID());
	}
	
	/**
	 * Returns the strategy chosen by this player.
	 * @param opponent
	 * @return
	 */
	public abstract int nextMove(final int opponent);
}
