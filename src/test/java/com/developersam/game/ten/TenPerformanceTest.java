package com.developersam.game.ten;

import org.junit.Test;

/**
 * Run a performance test on TEN.
 */
public final class TenPerformanceTest {
    
    /**
     * Test the performance of the game by run a game between two MCTS AI.
     */
    @Test
    public void testPerformance() {
        Board.runAGameBetweenTwoAIs(50);
    }
    
}
