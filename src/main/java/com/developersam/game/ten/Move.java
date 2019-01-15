package com.developersam.game.ten;

import org.jetbrains.annotations.NotNull;

/**
 * The move object.
 */
final class Move {
    
    /**
     * A dummy move.
     */
    @NotNull
    static final Move DUMMY_MOVE = new Move();
    /**
     * The coordinates.
     */
    final int a, b;
    
    /**
     * Create a move.
     *
     * @param a a.
     * @param b b.
     */
    Move(int a, int b) {
        this.a = a;
        this.b = b;
    }
    
    /**
     * Constructor for GSON and dummy mov.
     */
    private Move() {
        a = -1;
        b = -1;
    }
    
}
