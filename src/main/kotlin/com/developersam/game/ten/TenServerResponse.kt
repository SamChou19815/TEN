package com.developersam.game.ten

/**
 * A data class that represents a server response to the client move.
 *
 * Construct a [TenServerResponse] from a range of arguments that can
 * help the client decide the status of the game after the transmission:
 * - [aiMove] specifies the move of the AI, which can be a place holder value.
 * - [currentBigSquareLegalPosition] specifies the current big square legal
 * position after AI move.
 * - [status] specifies the status of the game after the move.
 * - [aiWinningProbability] specifies the winning probability of AI.
 */
class TenServerResponse(
        private val aiMove: IntArray,
        private val currentBigSquareLegalPosition: Int,
        private val status: Int,
        private val aiWinningProbability: Int
) {

    companion object {
        /**
         * A standard placeholder AI move.
         */
        private val placeholderMove = intArrayOf(-1, -1)
        /**
         * A standard response to an illegal move.
         */
        val illegalMoveResponse = TenServerResponse(aiMove = placeholderMove,
                currentBigSquareLegalPosition = -1, status = 2,
                aiWinningProbability = 0)

        /**
         * Create a [TenServerResponse] when the player wins before the AI can
         * move.
         */
        fun whenPlayerWin(winnerIdentity: Int): TenServerResponse =
                TenServerResponse(aiMove = placeholderMove,
                        currentBigSquareLegalPosition = -1,
                        status = winnerIdentity, aiWinningProbability = 0)
    }

}