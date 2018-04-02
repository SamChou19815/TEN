package com.developersam.game.ten

/**
 * A class that is responsible for holding the simplified version of the TEN
 * board, without tracking extra game status. The class is designed as
 * a transmission object.
 * - [board] describes the 9x9 board.
 * - [currentBigSquareLegalPosition] is used to determine the current game
 * status.
 * - [currentPlayerIdentity] is used to determine the identity of AI.
 */
internal class TenBoardData(
        val board: Array<IntArray>,
        val currentBigSquareLegalPosition: Int,
        val currentPlayerIdentity: Int
)