package com.developersam.game.ten;

import org.jetbrains.annotations.NotNull;

/**
 * The server response data class.
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public final class ServerResponse {
    
    /**
     * A standard response to an illegal move.
     */
    @NotNull
    static final ServerResponse ILLEGAL_MOVE_RESP =
            new ServerResponse(Move.DUMMY_MOVE, -1, 2, 0);
    
    /**
     * Specifies the move of the AI, which can be a place holder value.
     */
    private final Move aiMove;
    /**
     * Specifies the big square to pick after AI move.
     */
    private final int bigSquareToPick;
    /**
     * Specifies the status of the game after the move.
     */
    private final int status;
    /**
     * Specifies the winning probability of AI.
     */
    private final int aiWinningProbability;
    
    /**
     * Construct a response.
     *
     * @param aiMove aiMove as specified above.
     * @param bigSquareToPick bigSquareToPick as specified above.
     * @param status status as specified above.
     * @param aiWinningProbability aiWinningProbability as specified above.
     */
    ServerResponse(Move aiMove, int bigSquareToPick, int status, int aiWinningProbability) {
        this.aiMove = aiMove;
        this.bigSquareToPick = bigSquareToPick;
        this.status = status;
        this.aiWinningProbability = aiWinningProbability;
    }
    
    /**
     * @param winnerIdentity the identity of the winner.
     * @return the server response when the player wins before the AI can move.
     */
    @NotNull
    static ServerResponse whenPlayerWin(int winnerIdentity) {
        return new ServerResponse(Move.DUMMY_MOVE, -1, winnerIdentity, 0);
    }
    
}
