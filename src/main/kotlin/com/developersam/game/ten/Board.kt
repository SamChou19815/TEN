package com.developersam.game.ten

import mcts.MCTS
import java.util.Arrays

/**
 * The board of the game ten. It implements the [Board] interface from the MCTS
 * framework so that there is an AI for it.
 */
class Board {

    /**
     * In variable names, a big square refers to a 3*3 square;
     * a tile refers to a 1*1 square.
     * Each tile is either 1, -1 or 0 (black, white, empty).
     * board[a * 9 + b] represents a tile at big square a and small square b.
     */
    private val board: Array<IntArray>
    /**
     * keep track of winning progress on big squares
     * 1, -1, 0, 2 mean black wins, white wins, inconclusive, and all occupied.
     */
    private val bigSquaresStatus: IntArray
    /**
     * The current legal big square to pick as next move. If it's value is -1,
     * that means the user can place the move everywhere.
     * This variable is important for maintaining the current game state.
     */
    var currentBigSquareLegalPosition: Int
        private set
    /**
     * The identity of the current player. Must be 1 or -1.
     */
    private var _currentPlayerIdentity: Int

    /**
     * [currentPlayerIdentity] returns identity of current player.
     */
    val currentPlayerIdentity: Int get() = _currentPlayerIdentity;

    /**
     * [copy] returns a deep copy of the board to allow different simulations on the
     * same board without interference.
     */
    val copy: Board get() = Board(oldBoard = this)

    /**
     * [allLegalMovesForAI] returns a list of all legal moves for AI.
     * DO NOT confuse: a legal move for human is not necessarily one for AI
     * because AI needs less moves to save time for computation.
     */
    val allLegalMovesForAI: List<Move>
        get() {
            val list = arrayListOf<Move>()
            if (currentBigSquareLegalPosition == -1) {
                // Can move in every big square
                for (i in 0 until 9) {
                    for (j in 0 until 9) {
                        if (isLegalMove(a = i, b = j)) {
                            list.add(element = Move(a = i, b = j))
                        }
                    }
                }
            } else {
                for (j in 0 until 9) {
                    // Can only move in the specified square
                    if (isLegalMove(a = currentBigSquareLegalPosition, b = j)) {
                        list.add(element = Move(a = currentBigSquareLegalPosition, b = j))
                    }
                }
            }
            return list
        }

    /**
     * [gameStatus] returns the game status on current board.
     * This happens immediately after a player makes a move, before
     * switching identity.
     * The status must be 1, -1, or 0 (inconclusive).
     */
    val gameStatus: Int
        get() {
            for (i in 0 until 9) {
                updateBigSquareStatus(i)
            }
            val simpleStatus = getSimpleStatusFromSquare(bigSquaresStatus)
            if (simpleStatus == 1 || simpleStatus == -1) {
                return simpleStatus
            }
            for (i in 0 until 9) {
                if (bigSquaresStatus[i] == 0) {
                    return 0
                }
            }
            var blackBigSquareCounter = 0
            var whiteBigSquareCounter = 0
            for (i in 0 until 9) {
                val status = bigSquaresStatus[i]
                if (status == 1) {
                    blackBigSquareCounter++
                } else if (status == -1) {
                    whiteBigSquareCounter++
                }
            }
            return if (blackBigSquareCounter > whiteBigSquareCounter) 1 else -1
        }

    /**
     * Construct a fresh new TEN board.
     */
    constructor() {
        board = Array(size = 9) { IntArray(size = 9) }
        bigSquaresStatus = IntArray(size = 9)
        // Black can choose any position initially
        currentBigSquareLegalPosition = -1
        // Black plays first.
        _currentPlayerIdentity = 1
    }

    /**
     * Initialize the board from a data class [BoardData] with all the
     * necessary info in [data]. Note that the big square legal positions array
     * is not presented because it can be computed at the server side without
     * extra information.
     */
    private constructor(data: BoardData) {
        board = data.board
        bigSquaresStatus = IntArray(size = 9)
        for (i in 0 until 9) {
            updateBigSquareStatus(i)
        }
        currentBigSquareLegalPosition = data.currentBigSquareLegalPosition
        _currentPlayerIdentity = data.currentPlayerIdentity
    }

    /**
     * Initialize the board from an [oldBoard].
     */
    private constructor(oldBoard: Board) {
        board = Array(size = 9) { IntArray(size = 0) }
        bigSquaresStatus = IntArray(size = 9)
        for (i in 0 until 9) {
            // Copy to maintain value safety.
            board[i] = Arrays.copyOf(oldBoard.board[i], 9)
            // Just copy value to speed up without another around of
            // calculation.
            bigSquaresStatus[i] = oldBoard.bigSquaresStatus[i]
        }
        currentBigSquareLegalPosition = oldBoard.currentBigSquareLegalPosition
        _currentPlayerIdentity = oldBoard._currentPlayerIdentity
    }

    /**
     * Decode int [i] stored internally in data structure to player name.
     */
    private fun decode(i: Int): String = when (i) {
        0 -> "0"
        1 -> "b"
        -1 -> "w"
        else -> throw Error("Bad Data in Board!")
    }

    /**
     * Print the board.
     */
    private fun print() {
        println("Current Player: " +
                if (currentPlayerIdentity == 1) "Black" else "White")
        println("Printing the board:")
        println("-----------------")
        for (row in 0..2) {
            for (innerRow in 0..2) {
                print(decode(board[row * 3][innerRow * 3]) + " "
                        + decode(board[row * 3][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3][innerRow * 3 + 2]) + "|")
                print(decode(board[row * 3 + 1][innerRow * 3]) + " "
                        + decode(board[row * 3 + 1][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3 + 1][innerRow * 3 + 2]) + "|")
                print(decode(board[row * 3 + 2][innerRow * 3]) + " "
                        + decode(board[row * 3 + 2][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3 + 2][innerRow * 3 + 2]))
                print('\n')
            }
            if (row != 2) {
                println("- - -*- - -*- - -")
            }
        }
        println("-----------------")
    }

    /**
     * Check whether a move is legal, where move is given by ([a], [b]).
     */
    private fun isLegalMove(a: Int, b: Int): Boolean {
        if (a < 0 || a > 8 || b < 0 || b > 8) {
            // Out of boundary values
            return false
        }
        return if (currentBigSquareLegalPosition != -1
                && currentBigSquareLegalPosition != a) {
            // in the wrong big square when it cannot have a free move
            false
        } else {
            // not in the occupied big square and on an empty tile
            bigSquaresStatus[a] == 0 && board[a][b] == 0
        }
    }


    /**
     * Make a [move] without any check, which can accelerate AI simulation.
     * It should also switch the identity of the current player.
     * The identity must be 1 or -1, so that checking game status can determine
     * who wins.
     *
     * Requires:
     * - [move] is a valid int array representation of a move in the game.
     */
    fun makeMoveWithoutCheck(move: Move) {
        val (a, b) = move
        board[a][b] = _currentPlayerIdentity
        updateBigSquareStatus(move.a)
        currentBigSquareLegalPosition = if (bigSquaresStatus[b] == 0) b else -1
        switchIdentity()
    }

    /**
     * Make a move [move] with legality check and tells whether the move is
     * legal/successful.
     */
    private fun makeMove(move: Move): Boolean {
        val (a, b) = move
        if (!isLegalMove(a = a, b = b)) {
            return false
        }
        makeMoveWithoutCheck(move)
        return true
    }

    /**
     * Perform a naive check on the square [s] about whether the player with
     * identity [id] win the square. It only checks according to the
     * primitive tic-tac-toe rule.
     */
    private fun playerSimplyWinSquare(s: IntArray, id: Int): Boolean {
        return s[0] == id && s[1] == id && s[2] == id
                || s[3] == id && s[4] == id && s[5] == id
                || s[6] == id && s[7] == id && s[8] == id
                || s[0] == id && s[3] == id && s[6] == id
                || s[1] == id && s[4] == id && s[7] == id
                || s[2] == id && s[5] == id && s[8] == id
                || s[0] == id && s[4] == id && s[8] == id
                || s[2] == id && s[4] == id && s[6] == id
    }

    /**
     * A function that helps to determine whether a square [square] belongs
     * to black (1) or white (-1).
     * If there is no direct victory, it will return 0.
     */
    private fun getSimpleStatusFromSquare(square: IntArray): Int {
        return when {
            playerSimplyWinSquare(s = square, id = 1) -> 1
            playerSimplyWinSquare(s = square, id = -1) -> -1
            else -> 0
        }
    }

    /**
     * Update the big square status for ONE big square of id [bigSquareID].
     */
    private fun updateBigSquareStatus(bigSquareID: Int) {
        val bigSquare = board[bigSquareID]
        val bigSquareStatus = getSimpleStatusFromSquare(bigSquare)
        if (bigSquareStatus == 1 || bigSquareStatus == -1) {
            bigSquaresStatus[bigSquareID] = bigSquareStatus
            return
            // already won by a player
        }
        for (i in 0..8) {
            if (bigSquare[i] == 0) {
                // there is a space left.
                bigSquaresStatus[bigSquareID] = 0
                return
            }
        }
        bigSquaresStatus[bigSquareID] = 2 // no space left.
    }

    /**
     * Switches the identity of the current player to complete a move.
     */
    private fun switchIdentity() {
        _currentPlayerIdentity = -currentPlayerIdentity
    }

    companion object {

        /**
         * Respond to a [clientMove] represented by a [ClientMove] object and
         * gives back the formatted [ServerResponse].
         */
        @Suppress(names = ["RedundantVisibilityModifier"])
        public fun respond(clientMove: ClientMove): ServerResponse {
            val board = Board(clientMove.boardBeforeHumanMove)
            board.switchIdentity()
            if (!board.makeMove(move = clientMove.asMove)) {
                // Stop illegal move from corrupting game data.
                return ServerResponse.illegalMoveResponse
            }
            var status = board.gameStatus
            when (status) {
                1, -1 -> // Black/White wins before AI move
                    return ServerResponse.whenPlayerWin(status)
            }
            // Let AI think
            val decision = MCTS(board = board, timeLimit = 1500)
            val aiMove = decision.selectMove()
            board.makeMove(move = Move(a = aiMove[0], b = aiMove[1]))
            status = board.gameStatus
            // A full response.
            return ServerResponse(aiMove = intArrayOf(aiMove[0], aiMove[1]),
                    currentBigSquareLegalPosition =
                    board.currentBigSquareLegalPosition,
                    status = status, aiWinningProbability = aiMove[2])
        }

        /**
         * Run a game between two AI with a specified [aiThinkingTime] in
         * milliseconds.
         * The user of the method can specify whether to print game status out
         * by [printGameStatus], which defaults to true.
         */
        internal fun runAGameBetweenTwoAIs(aiThinkingTime: Int,
                                           printGameStatus: Boolean = true) {
            val board = Board()
            var moveCounter = 1
            var status = 0
            while (status == 0) {
                if (printGameStatus) {
                    board.print()
                }
                val move = MCTS(board = board, timeLimit = aiThinkingTime)
                        .selectMove()
                board.makeMoveWithoutCheck(Move(a = move[0], b = move[1]))
                status = board.gameStatus
                if (printGameStatus) {
                    println("Move $moveCounter finished.")
                    val player = if (moveCounter % 2 == 0) "White" else "Black"
                    println("Winning Probability for $player is ${move[2]}%.")
                }
                moveCounter++
            }
            if (printGameStatus) {
                board.print()
                println((if (status == 1) "Black" else "White") + " wins.")
            }
        }
    }

}