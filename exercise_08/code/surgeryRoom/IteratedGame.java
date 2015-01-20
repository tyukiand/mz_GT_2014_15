/*
 * Decompiled with CFR 0_91.
 */
import IteratedGameInterface;
import Player;
import SymmetricPayoff;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

public class IteratedGame
implements IteratedGameInterface {
    private final String statsFile;
    private static final boolean separateFiles = 1;
    private static final long SEED = 42;
    private static final Random R = new Random(42);
    private boolean verbose = true;
    private Player[] players;
    private final SymmetricPayoff payoff;
    private final int numIterations;
    private int[][][] chosenStrategy;
    private float[][] scores;
    private int currentIteration;

    public IteratedGame(int numPlayers, int numIterations, SymmetricPayoff payoff, String statsFile) {
        this.numIterations = numIterations;
        this.payoff = payoff;
        this.statsFile = statsFile;
        this.players = new Player[numPlayers];
    }

    public void setPlayers(Player[] players) {
        this.players = players;
        this.chosenStrategy = new int[this.numIterations][players.length][players.length];
        for (int i = 0; i < this.numIterations; ++i) {
            for (int player = 0; player < this.players.length; ++player) {
                this.chosenStrategy[i][player][player] = -1000000;
            }
        }
        this.scores = new float[this.numIterations][this.players.length];
    }

    public void startGame() {
        this.currentIteration = 0;
        while (this.currentIteration < this.numIterations) {
            System.out.println("******************************");
            System.out.println("Iteration " + this.getCurrentIteration());
            System.out.println("Scores after the last iteration: " + Arrays.toString(this.getLastScores()));
            if (this.getCurrentIteration() >= 1) {
                System.arraycopy(this.scores[this.getLastIteration()], 0, this.scores[this.getCurrentIteration()], 0, this.players.length);
            }
            for (int player1 = 0; player1 < this.players.length; ++player1) {
                for (int player2 = player1 + 1; player2 < this.players.length; ++player2) {
                    int player2Strategy;
                    int player1Strategy;
                    if (this.verbose) {
                        System.out.println(String.valueOf(this.players[player1].name) + " against " + this.players[player2].name);
                    }
                    if ((player1Strategy = this.players[player1].nextMove(player2)) == -1000000) {
                        System.out.println("Problem player1");
                    }
                    if (this.payoff.outsideStrategyRange(player1Strategy)) {
                        player1Strategy = 0;
                    }
                    if ((player2Strategy = this.players[player2].nextMove(player1)) == -1000000) {
                        System.out.println("Problem player2");
                    }
                    if (this.payoff.outsideStrategyRange(player2Strategy)) {
                        player2Strategy = 0;
                    }
                    this.chosenStrategy[this.currentIteration][player1][player2] = player1Strategy;
                    this.chosenStrategy[this.currentIteration][player2][player1] = player2Strategy;
                    float payoffPlayer1 = this.payoff.myPayoff(player1Strategy, player2Strategy);
                    float payoffPlayer2 = this.payoff.myPayoff(player2Strategy, player1Strategy);
                    float[] arrf = this.scores[this.currentIteration];
                    int n = player1;
                    arrf[n] = arrf[n] + payoffPlayer1;
                    float[] arrf2 = this.scores[this.currentIteration];
                    int n2 = player2;
                    arrf2[n2] = arrf2[n2] + payoffPlayer2;
                    if (!this.verbose) continue;
                    System.out.println("Player " + player1 + " plays " + player1Strategy);
                    System.out.println("Player " + player2 + " plays " + player2Strategy);
                }
            }
            ++this.currentIteration;
        }
    }

    protected void stats() {
        try {
            for (int player = 0; player < this.players.length; ++player) {
                File f = new File(String.valueOf(this.statsFile) + player);
                FileWriter fw = new FileWriter(f);
                BufferedWriter bw = new BufferedWriter(fw);
                System.out.println("Write file: " + f.getName());
                String empty = "";
                String separate = "\t";
                int newline = 10;
                for (int i = 0; i < this.numIterations; ++i) {
                    String s = String.valueOf(i) + "\t" + this.scores[i][player];
                    s = String.valueOf(s) + '\n';
                    bw.write(s);
                }
                bw.close();
                fw.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Final scores:");
        for (int i = 0; i < this.players.length; ++i) {
            System.out.println(String.valueOf(this.players[i].getName()) + "\t" + this.scores[this.numIterations - 1][i]);
        }
    }

    @Override
    public int getNumberOfStrategies() {
        return this.payoff.getNumberOfStrategies();
    }

    @Override
    public float myPayoff(int myStrategy, int opponentStrategy) {
        return this.payoff.myPayoff(myStrategy, opponentStrategy);
    }

    @Override
    public float opponentPayoff(int myStrategy, int opponentStrategy) {
        return this.payoff.opponentPayoff(myStrategy, opponentStrategy);
    }

    @Override
    public int getNumberOfPlayers() {
        return this.players.length;
    }

    @Override
    public int getCurrentIteration() {
        return this.currentIteration;
    }

    @Override
    public int getLastIteration() {
        return this.getCurrentIteration() - 1;
    }

    @Override
    public int getStrategy(int time, int attacker, int victim) {
        if (time >= this.currentIteration || time < 0) {
            System.out.println("Wrong time: " + time);
            return -1000000;
        }
        if (attacker == victim) {
            if (this.verbose) {
                System.out.println("attacker = victim: " + attacker + "," + victim);
            }
            return -1000000;
        }
        return this.chosenStrategy[time][attacker][victim];
    }

    @Override
    public int[][] getStrategies(int time) {
        if (time >= this.currentIteration || time < 0) {
            return null;
        }
        int[][] strategies = new int[this.players.length][];
        for (int i = 0; i < this.chosenStrategy[time].length; ++i) {
            int[] m = this.chosenStrategy[time][i];
            strategies[i] = (int[])m.clone();
        }
        return strategies;
    }

    @Override
    public float getScore(int player, int time) {
        if (player >= this.players.length) {
            return -1000000.0f;
        }
        if (time >= this.currentIteration || time < 0) {
            return -1000000.0f;
        }
        return this.scores[this.currentIteration - 1][player];
    }

    @Override
    public float[] getScores(int time) {
        if (time >= this.currentIteration || time < 0) {
            return null;
        }
        return (float[])this.scores[time].clone();
    }

    @Override
    public float getLastScore(int player) {
        if (player >= this.players.length) {
            return -1000000.0f;
        }
        if (this.getCurrentIteration() == 0) {
            return 0.0f;
        }
        return this.scores[this.currentIteration - 1][player];
    }

    @Override
    public float[] getLastScores() {
        if (this.getCurrentIteration() == 0) {
            return new float[this.players.length];
        }
        return (float[])this.scores[this.currentIteration - 1].clone();
    }

    @Override
    public int getHighestScoringPlayer() {
        if (this.getCurrentIteration() == 0) {
            return 0;
        }
        int player = 0;
        float maxScore = this.scores[this.currentIteration - 1][player];
        for (int i = 0; i < this.players.length; ++i) {
            if (this.scores[this.currentIteration - 1][i] <= maxScore) continue;
            maxScore = this.scores[this.currentIteration - 1][i];
            player = i;
        }
        return player;
    }

    @Override
    public float getHighestScore() {
        float maxScore = this.scores[this.currentIteration - 1][0];
        for (int i = 0; i < this.players.length; ++i) {
            if (this.scores[this.currentIteration - 1][i] <= maxScore) continue;
            maxScore = this.scores[this.currentIteration - 1][i];
        }
        return maxScore;
    }

    @Override
    public float getV() {
        return this.payoff.getV();
    }

    @Override
    public float getC() {
        return this.payoff.getV();
    }

    public static long getSeed() {
        return 42;
    }

    public static int nextInt(int n) {
        return R.nextInt(n);
    }
}
