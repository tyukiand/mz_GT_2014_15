/*
 * Decompiled with CFR 0_91.
 */
import IteratedGameInterface;
import Player;

public class FriendlyPlayer
extends Player {
    public FriendlyPlayer(IteratedGameInterface game, int playerID) {
        super(game, playerID);
        this.name = "Mahatma Gandhi";
    }

    @Override
    public int nextMove(int opponent) {
        return 0;
    }
}
