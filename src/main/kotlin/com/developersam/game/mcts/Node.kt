package com.developersam.game.mcts

import com.developersam.game.ten.Board
import com.developersam.game.ten.Move

/**
 * A node in the simulation tree.
 *
 * It is initialized by an optional parent [parent] (which is used to track back
 * to update winning probability), a move [move] of the user or AI, the board
 * [board] bind on the node.
 */
internal class Node(
        private val parent: Node?,
        internal val move: Move?,
        board: Board
) {

    /**
     * Children node, which will be initialized later.
     */
    internal var children: List<Node>? = null
    /**
     * The game board stored on the node.
     * It is the backing field of the property.
     */
    private var _board: Board? = null
    /**
     * [winningProbNumerator] is the numerator for winning probability.
     */
    private var winningProbNumerator: Int = 0
    /**
     * [winningProbDenominator] is the denominator for winning probability.
     */
    private var winningProbDenominator: Int = 0

    /**
     * Obtain the [board] associated with the node.
     */
    internal val board: Board? get() = _board

    /**
     * Obtain winning probability in percentage.
     */
    internal val winningProbabilityInPercentage: Int
        get() = (winningProbability * 100).toInt()

    /**
     * Construct a node without a parent and without a move, with only a
     * starting board.
     * This node can only be root node.
     *
     * @param board the starting board.
     */
    internal constructor(board: Board) : this(null, null, board)

    init {
        this._board = board
    }

    /**
     * Obtain [winningProbability] between 0 and 1.
     */
    internal val winningProbability: Double
        get() = winningProbNumerator.toDouble() / winningProbDenominator.toDouble()

    /**
     * Plus one for winning probability denominator and plus the [winValue] for
     * the numerator. This method does this iteratively until reaching the root.
     */
    internal fun winningStatisticsPlusOne(winValue: Int) {
        var node: Node? = this
        while (node != null) {
            node.winningProbNumerator += winValue
            node.winningProbDenominator += 1
            node = node.parent
        }
    }

    /**
     * Get upper confidence bound in MCTS, which needs a [isPlayer] parameter
     * to tell whether to calculate in favor or against the player.
     *
     * Requires: the node is not the root.
     */
    internal fun getUpperConfidenceBound(isPlayer: Boolean): Double {
        if (parent == null) {
            throw IllegalArgumentException("Cannot be called on root element!")
        }
        val lnt = Math.log(parent.winningProbDenominator.toDouble())
        val winningProb = if (isPlayer) winningProbability else 1 - winningProbability
        val c = 1.0
        return winningProb + Math.sqrt(2 * lnt / winningProbDenominator) * c
    }

    /**
     * Remove the board from the node to allow it to be garbage collected.
     */
    internal fun dereferenceBoard() {
        this._board = null
    }

}
