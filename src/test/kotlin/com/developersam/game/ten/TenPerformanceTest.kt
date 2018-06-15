package com.developersam.game.ten

import org.junit.Test

/**
 * Run a performance test on TEN.
 */
class TenPerformanceTest {

    /**
     * Test the performance of the game by run a game between two MCTS AI.
     */
    @Test
    fun testPerformance() {
        Board.runAGameBetweenTwoAIs(
                aiThinkingTime = 50, printGameStatus = false)
    }

}