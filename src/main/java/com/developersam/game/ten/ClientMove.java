package com.developersam.game.ten;

/**
 * A data class that represents a client move.
 */
@SuppressWarnings("WeakerAccess")
public final class ClientMove {
    /**
     * Specifies the board before the human move.
     */
    final BoardData boardBeforeHumanMove = new BoardData();
    /**
     * Completes the picture by providing a human's move.
     */
    final Move humanMove = Move.DUMMY_MOVE;
    
    /**
     * Disable construction.
     */
    private ClientMove() {}
    
}
