package com.developersam.game.ten;

/**
 * The board data class to hold data from the client.
 */
final class BoardData {
    /**
     * The raw tiles.
     */
    final int[] tiles = new int[0];
    /**
     * The big square to pick.
     */
    final int bigSquareToPick = -1;
    /**
     * The identity of AI.
     */
    final int aiIdentity = -1;
    
    /**
     * Disable external construction.
     */
    BoardData() {}
    
}
