package com.developersam.game.ten;

/**
 * The board data class to hold data from the client.
 */
@SuppressWarnings("WeakerAccess")
public final class BoardData {
    /**
     * The raw tiles.
     */
    final int[] tiles;
    /**
     * The big square to pick.
     */
    final int bigSquareToPick;
    /**
     * The identity of AI.
     */
    final int playerIdentity;

    /**
     * Disable external construction.
     */
    BoardData() {
        // must initialize here to avoid gson reflection bug
        tiles = new int[0];
        bigSquareToPick = -1;
        playerIdentity = -1;
    }

}
