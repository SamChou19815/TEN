package com.developersam.game.ten;

/**
 * The server response data class.
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public final class ServerResponse {

    /**
     * Specifies the move of the AI, which can be a place holder value.
     */
    private final int[] move;
    /**
     * The winning probability of the decided move.
     */
    private final int winningPercentage;
    /**
     * The counter that records the number of simulation done.
     */
    private final int simulationCounter;

    /**
     * Construct a response.
     */
    ServerResponse(Move aiMove, int winningPercentage, int simulationCounter) {
        this.move = new int[]{aiMove.a, aiMove.b};
        this.winningPercentage = winningPercentage;
        this.simulationCounter = simulationCounter;
    }

}
