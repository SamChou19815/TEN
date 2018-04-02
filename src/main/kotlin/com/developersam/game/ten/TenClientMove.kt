package com.developersam.game.ten

/**
 * A data class that represents a client move.
 * - [boardBeforeHumanMove] specifies the board before the human move.
 * - [humanMove] completes the picture by providing human's move in a tuple.
 */
class TenClientMove private constructor(
        internal val boardBeforeHumanMove: TenBoardData,
        internal val humanMove: IntArray
)