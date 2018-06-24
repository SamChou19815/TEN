package com.developersam.game.ten

/**
 * A data class that represents a client move.
 *
 * @property boardBeforeHumanMove specifies the board before the human move.
 * @property humanMove completes the picture by providing human's move in a tuple.
 */
class ClientMove private constructor(
        val boardBeforeHumanMove: BoardData, private val humanMove: IntArray
) {

    /**
     * [asMove] returns the move.
     */
    val asMove: Move get() = Move(a = humanMove[0], b = humanMove[1])

}
