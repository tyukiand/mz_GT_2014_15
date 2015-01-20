/*
 * Decompiled with CFR 0_91.
 */
public class SymmetricPayoff {
    private final int numberOfStrategies;
    public final float v = 1.0f;
    public final float c = 2.0f;
    private final float[][] payoff;

    public SymmetricPayoff(float[][] matrix) {
        this.numberOfStrategies = matrix.length;
        this.payoff = matrix;
    }

    public int getNumberOfStrategies() {
        return this.numberOfStrategies;
    }

    protected boolean outsideStrategyRange(int str) {
        if (str >= this.numberOfStrategies || str < 0) {
            return true;
        }
        return false;
    }

    public float myPayoff(int myStrategy, int opponentStrategy) {
        if (this.outsideStrategyRange(myStrategy) || this.outsideStrategyRange(opponentStrategy)) {
            return -1000000.0f;
        }
        return this.payoff[myStrategy][opponentStrategy];
    }

    public float opponentPayoff(int myStrategy, int opponentStrategy) {
        if (this.outsideStrategyRange(myStrategy) || this.outsideStrategyRange(opponentStrategy)) {
            return -1000000.0f;
        }
        return this.myPayoff(opponentStrategy, myStrategy);
    }

    public float getV() {
        return 1.0f;
    }

    public float getC() {
        return 2.0f;
    }
}
