package com.developersam.game.ten

/**
 * A data class that represents a server response to the client move.
 *
 * Construct a [ServerResponse] from a range of arguments that can help the client decide the status
 * of the game after the transmission.
 *
 * @property aiMove specifies the move of the AI, which can be a place holder value.
 * @property currentBigSquareLegalPosition specifies the current big square legal position after AI
 * move.
 * @property status specifies the status of the game after the move.
 * @property aiWinningProbability specifies the winning probability of AI.
 */
class ServerResponse(
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
        @JvmField
        val illegalMoveResponse = ServerResponse(aiMove = placeholderMove,
                currentBigSquareLegalPosition = -1, status = 2,
                aiWinningProbability = 0)

        /**
         * Create a [ServerResponse] when the player wins before the AI can
         * move.
         */
        @JvmStatic
        fun whenPlayerWin(winnerIdentity: Int): ServerResponse =
                ServerResponse(aiMove = placeholderMove,
                        currentBigSquareLegalPosition = -1,
                        status = winnerIdentity, aiWinningProbability = 0)
    }

}