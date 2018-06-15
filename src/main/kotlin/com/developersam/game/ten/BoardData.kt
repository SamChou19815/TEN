package com.developersam.game.ten

import java.util.Arrays

/**
 * A class that is responsible for holding the simplified version of the TEN
 * board, without tracking extra game status. The class is designed as
 * a transmission object.
 * - [board] describes the 9x9 board.
 * - [currentBigSquareLegalPosition] is used to determine the current game
 * status.
 * - [currentPlayerIdentity] is used to determine the identity of AI.
 */
internal data class BoardData(
        val board: Array<IntArray> = emptyArray(),
        val currentBigSquareLegalPosition: Int = -1,
        val currentPlayerIdentity: Int = -1
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BoardData
        if (!Arrays.equals(board, other.board)) return false
        if (currentBigSquareLegalPosition != other.currentBigSquareLegalPosition) return false
        if (currentPlayerIdentity != other.currentPlayerIdentity) return false
        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(board)
        result = 31 * result + currentBigSquareLegalPosition
        result = 31 * result + currentPlayerIdentity
        return result
    }

}