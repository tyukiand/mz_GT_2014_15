
/**
 * Please change the class name to your name(s) (same for the constructor). E.g. MaxMoritzPlayer.
 * @author mablumen
 *
 */
public class MyPlayer extends Player {
	
	public MyPlayer(final IteratedGameInterface game, final int ID) {
		super(game, ID); // do not alter.
		
		super.name = "INSERT NAME HERE";	// Name your player, e.g. Siegfried, Superman, Mozart, Gauss, ...
	}

	/**
	 * Insert your algorithm here.
	 */
	public int nextMove(final int opponent) {
		// TODO Insert your algorithm here
		// Access the game via this.game or super.game
		
		// Example
		if (this.game.getCurrentIteration() == 0) {	// First round.
			return 0;	// Always play strategy 0 first.
		}
		else {
			// Play the same strategy as in the last iteration against this opponent.
			return this.game.getStrategy(this.game.getLastIteration(), this.getMyID(), opponent);
		}
		
		// END TODO
	}
	
	
	// If you use the .jar file, which contains the missing classes, you can test the game by de-commenting the following function:
	
//	public static void main(String[] args) {
//		// Strategie 0: Schweigen
//		// Strategie 1: Gestehen
//		
//								// Sp. 1 schweigt, gesteht
//		final float[][] matrix = { 	{-2, -6},	// Spieler 0 schweigt
//									{-1, -4}	// Spieler 0 gesteht
//								};
//		final SymmetricPayoff payoff = new SymmetricPayoff(matrix);
//		final int numIterations = 100;
//		final IteratedGame game = new IteratedGame(2, numIterations, payoff, null); 	// 2 players, 100 iterations, the payoff matrix, no output file
//		final Player p0 = new RandomPlayer(game, 0);	// player 0
//		final Player p1 = new MyPlayer(game, 1);		// player 1
//		
//		final Player[] players = {p0, p1};
//		
//		game.setPlayers(players);
//		game.startGame();
//	}
}