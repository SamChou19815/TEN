package com.developersam.game.ten;

import com.developersam.game.ten.MCTS.Decision;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An immutable representation of the game board.
 */
public final class Board {

    /**
     * In variable names, a big square refers to a 3*3 square;
     * A tile refers to a 1*1 square.
     * Each tile is either 1, -1 or 0 (black, white, empty).
     * tiles[a * 9 + b] represents a tile at big square a and small square b.
     */
    private final int[] tiles;
    /**
     * Keep track of winning progress on big squares.
     * 1, -1, 0, 2 mean black wins, white wins, inconclusive, and all occupied.
     */
    private final int[] bigSquaresStatusArray;
    /**
     * The current legal big square to pick as next move.
     * If it's value is -1, that means the user can place the move everywhere.
     * This variable is important for maintaining the current game state.
     */
    private final int bigSquareToPick;
    /**
     * Winning big squares counter for black and white.
     */
    private final int blackBigSquaresCounter, whiteBigSquaresCounter;
    /**
     * The identity of the current player. Must be 1 or -1.
     */
    private final int playerIdentity;

    /**
     * Create an empty board.
     */
    private Board() {
        this(new int[81], new int[9], -1,
                0, 0, 1);
    }

    /**
     * Construct a board with all its fields. Only used for internal construction.
     *
     * @param tiles tiles as specified above.
     * @param bigSquaresStatusArray bigSquaresStatusArray as specified above.
     * @param bigSquareToPick bigSquareToPick as specified above.
     * @param blackBigSquaresCounter blackBigSquaresCounter as specified above.
     * @param whiteBigSquaresCounter whiteBigSquaresCounter as specified above.
     * @param currentPlayerIdentity currentPlayerIdentity as specified above.
     */
    private Board(int[] tiles, int[] bigSquaresStatusArray, int bigSquareToPick,
                  int blackBigSquaresCounter, int whiteBigSquaresCounter,
                  int currentPlayerIdentity) {
        this.tiles = tiles;
        this.bigSquaresStatusArray = bigSquaresStatusArray;
        this.bigSquareToPick = bigSquareToPick;
        this.blackBigSquaresCounter = blackBigSquaresCounter;
        this.whiteBigSquaresCounter = whiteBigSquaresCounter;
        this.playerIdentity = currentPlayerIdentity;
    }

    /**
     * Initialize the board from a data class BoardData with all the necessary info.
     *
     * @param boardData the board data.
     */
    public Board(BoardData boardData) {
        int[] tiles = boardData.tiles;
        int[] bigSquaresStatusArray = new int[9];
        for (int i = 0; i < 9; i++) {
            bigSquaresStatusArray[i] = computeSquareStatus(tiles, i * 9);
        }
        int blackBigSquaresCounter = 0;
        int whiteBigSquaresCounter = 0;
        for (int i = 0; i < 9; i++) {
            int status = bigSquaresStatusArray[i];
            if (status == 1) {
                blackBigSquaresCounter++;
            } else if (status == -1) {
                whiteBigSquaresCounter++;
            }
        }
        this.tiles = tiles;
        this.bigSquaresStatusArray = bigSquaresStatusArray;
        this.bigSquareToPick = boardData.bigSquareToPick;
        this.blackBigSquaresCounter = blackBigSquaresCounter;
        this.whiteBigSquaresCounter = whiteBigSquaresCounter;
        this.playerIdentity = boardData.playerIdentity;
    }

    /**
     * @return the current player identity.
     */
    int getPlayerIdentity() {
        return playerIdentity;
    }

    /**
     * Perform a naive check on the square about whether the player with id win the square.
     * Rule:  primitive tic-tac-toe.
     *
     * @param s the array that contains a slice of the square.
     * @param offset the offset.
     * @param id id to compare against
     * @return whether the player wins the square.
     */
    private static boolean playerSimplyWinSquare(int[] s, int offset, int id) {
        return s[offset] == id && s[offset + 1] == id && s[offset + 2] == id
                || s[offset + 3] == id && s[offset + 4] == id && s[offset + 5] == id
                || s[offset + 6] == id && s[offset + 7] == id && s[offset + 8] == id
                || s[offset] == id && s[offset + 3] == id && s[offset + 6] == id
                || s[offset + 1] == id && s[offset + 4] == id && s[offset + 7] == id
                || s[offset + 2] == id && s[offset + 5] == id && s[offset + 8] == id
                || s[offset] == id && s[offset + 4] == id && s[offset + 8] == id
                || s[offset + 2] == id && s[offset + 4] == id && s[offset + 6] == id;
    }

    /**
     * A function that helps to determine whether a square with offset belongs
     * to black (1) or white (-1).
     * If all tiles are occupied, it returns 2; else (there is no direct victory), it returns 0.
     *
     * @param square the array that contains a slice of the square.
     * @param offset the offset.
     * @return the square status as specified above.
     */
    private static int computeSquareStatus(int[] square, int offset) {
        if (playerSimplyWinSquare(square, offset, 1)) {
            return 1;
        } else if (playerSimplyWinSquare(square, offset, -1)) {
            return -1;
        }
        for (int i = 0; i < 9; i++) {
            if (square[offset + i] == 0) {
                // there is a space left.
                return 0;
            }
        }
        return 2;
    }

    /**
     * Check whether a move is legal.
     *
     * @param move the move to check.
     * @return whether a move is legal.
     */
    private boolean isLegalMove(Move move) {
        int a = move.a, b = move.b;
        if (a < 0 || a > 8 || b < 0 || b > 8) {
            // Out of boundary values
            return false;
        }
        if (bigSquareToPick != -1 && bigSquareToPick != a) {
            // in the wrong big square when it cannot have a free move
            return false;
        } else {
            // not in the occupied big square and on an empty tile
            return bigSquaresStatusArray[a] == 0 && tiles[a * 9 + b] == 0;
        }
    }

    /**
     * @return a list of all legal moves for AI.
     */
    @SuppressWarnings("Duplicates")
    List<Move> getAllLegalMovesForAI() {
        List<Move> list = new ArrayList<>(40);
        if (bigSquareToPick == -1) {
            // Can move in every big square
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    Move move = new Move(i, j);
                    if (isLegalMove(move)) {
                        list.add(move);
                    }
                }
            }
        } else {
            for (int j = 0; j < 9; j++) {
                // Can only move in the specified square
                Move move = new Move(bigSquareToPick, j);
                if (isLegalMove(move)) {
                    list.add(move);
                }
            }
        }
        return list;
    }

    /**
     * Make a [move] without any check, which can accelerate AI simulation.
     * It should also switch the identity of the current player.
     * The identity must be 1 or -1, so that checking game status can determine
     * who wins.
     *
     * @param move a valid move in the game.
     * @return a new board after the move.
     */
    @NotNull
    Board makeMoveWithoutCheck(Move move) {
        int[] newTiles = Arrays.copyOf(tiles, 81);
        int a = move.a, b = move.b;
        newTiles[a * 9 + b] = playerIdentity;
        int[] newBigSquareStatusArray = Arrays.copyOf(bigSquaresStatusArray, 9);
        int newBigSquareStatus = computeSquareStatus(newTiles, a * 9);
        newBigSquareStatusArray[a] = newBigSquareStatus;
        int newBigSquareToPick = newBigSquareStatusArray[b] == 0 ? b : -1;
        int blackCounter = blackBigSquaresCounter;
        int whiteCounter = whiteBigSquaresCounter;
        if (newBigSquareStatus == 1) {
            blackCounter++;
        } else if (newBigSquareStatus == -1) {
            whiteCounter++;
        }
        return new Board(
                newTiles, newBigSquareStatusArray, newBigSquareToPick,
                blackCounter, whiteCounter, -playerIdentity
        );
    }

    /**
     * Make a move [move] with legality check and tells whether the move is
     * legal/successful.
     */
    @Nullable
    private Board makeMove(Move move) {
        return isLegalMove(move) ? makeMoveWithoutCheck(move) : null;
    }

    /**
     * Returns the game status on current board.
     * The status must be 1, -1, or 0 (inconclusive).
     *
     * @return the game status on current board.
     */
    int getGameStatus() {
        int simpleStatus = computeSquareStatus(bigSquaresStatusArray, 0);
        if (simpleStatus != 2) {
            return simpleStatus;
        }
        return blackBigSquaresCounter > whiteBigSquaresCounter ? 1 : -1;
    }

    /**
     * Print tile content at specified index.
     *
     * @param i index.
     */
    private void printTileContent(int i) {
        int c = tiles[i];
        if (c == 1) {
            System.out.print('b');
        } else if (c == -1) {
            System.out.print('w');
        } else if (c == 0) {
            System.out.print('0');
        } else {
            throw new Error("Bad board!");
        }
    }

    /**
     * Print the board.
     */
    private void print() {
        System.out.println(playerIdentity == 1 ? "Current Player: Black" : "Current Player: White");
        System.out.println("Printing the board:");
        System.out.println("-----------------");
        for (int row = 0; row < 3; row++) {
            for (int innerRow = 0; innerRow < 3; innerRow++) {
                printTileContent(row * 27 + innerRow * 3);
                System.out.print(' ');
                printTileContent(row * 27 + innerRow * 3 + 1);
                System.out.print(' ');
                printTileContent(row * 27 + innerRow * 3 + 2);
                System.out.print('|');
                printTileContent(row * 27 + innerRow * 3 + 9);
                System.out.print(' ');
                printTileContent(row * 27 + innerRow * 3 + 10);
                System.out.print(' ');
                printTileContent(row * 27 + innerRow * 3 + 11);
                System.out.print('|');
                printTileContent(row * 27 + innerRow * 3 + 18);
                System.out.print(' ');
                printTileContent(row * 27 + innerRow * 3 + 19);
                System.out.print(' ');
                printTileContent(row * 27 + innerRow * 3 + 20);
                System.out.print('\n');
            }
            if (row != 2) {
                System.out.println("- - -*- - -*- - -");
            }
        }
        System.out.println("-----------------");
    }

    /**
     * Respond to a client move.
     *
     * @param clientBoard the board of the client.
     * @return the server response.
     */
    @NotNull
    public static ServerResponse respondToClient(@NotNull BoardData clientBoard) {
        Board board = new Board(clientBoard);
        // Let AI think
        Decision decision = MCTS.selectMove(board, 1500);
        // A full response.
        return new ServerResponse(
                decision.move, decision.winningPercentage, decision.simulationCounter);
    }

    /**
     * Run a game between two AIs.
     *
     * @param timeLimit time limit in milliseconds.
     */
    static void runAGameBetweenTwoAIs(long timeLimit) {
        Board board = new Board();
        int moveCounter = 1;
        int status = 0;
        while (status == 0) {
            board.print();
            Decision decision = MCTS.selectMove(board, timeLimit);
            board = board.makeMoveWithoutCheck(decision.move);
            status = board.getGameStatus();
            System.out.format("Move %d finished.\n", moveCounter);
            String player = moveCounter % 2 == 0 ? "White" : "Black";
            System.out.format("Winning Probability for %s is %d%%.\n",
                    player, decision.winningPercentage);
            moveCounter++;
        }
        board.print();
        if (status == 1) {
            System.out.println("Black wins!");
        } else {
            System.out.println("White wins!");
        }
    }

}
