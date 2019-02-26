package com.developersam.game.ten;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * The MCTS decider.
 */
final class MCTS {

    /**
     * The global random object.
     */
    private static final Random RANDOM = new Random();

    /**
     * Select and return a node starting from parent, according to selection rule in MCTS.
     *
     * @param root the root node.
     * @return the selected node.
     */
    private static Node selection(Node root) {
        boolean isPlayer = true;
        while (true) {
            // Find optimal move and loop down.
            // noinspection ConstantConditions
            List<Node> children = root.children;
            if (children.isEmpty()) {
                return root;
            }
            double max = -100000;
            Node n = null;
            for (Node node : children) {
                double ucb = node.getUpperConfidenceBound(isPlayer);
                if (ucb > max) {
                    max = ucb;
                    n = node;
                }
            }
            isPlayer = !isPlayer; // switch player identity
            root = n;
        }
    }

    /**
     * Perform simulation for a specific board and gives back a win value between 0 and 1.
     *
     * @param playerIdentity the identity of the player.
     * @param board the board to do the simulation.
     * @return the win value.
     */
    private static int simulation(int playerIdentity, Board board) {
        Board b = board;
        int status = b.getGameStatus();
        while (status == 0) {
            List<Move> moves = b.getAllLegalMovesForAI();
            b = b.makeMoveWithoutCheck(moves.get(RANDOM.nextInt(moves.size())));
            status = b.getGameStatus();
        }
        return status == playerIdentity ? 1 : 0;
    }

    /**
     * A method that connected all parts of of MCTS to build an evaluation tree.
     *
     * @param root the root the think on.
     * @param timeLimit the time limit.
     * @return the simulation counter.
     */
    private static int think(Node root, long timeLimit) {
        int playerIdentity = root.board.getPlayerIdentity();
        long tStart = System.currentTimeMillis();
        int simulationCounter = 0;
        while (System.currentTimeMillis() - tStart < timeLimit) {
            Node selectedNode = selection(root);
            Board b = selectedNode.board;
            // Expansion: Get all legal moves from a current board
            List<Move> allLegalMoves = b.getAllLegalMovesForAI();
            int len = allLegalMoves.size();
            if (len == 0) {
                // board no longer needed at parent level.
                Node n = selectedNode;
                while (n != null) {
                    n.winningProbNumerator += playerIdentity == b.getGameStatus() ? 10000 : 0;
                    n.winningProbDenominator += 10000;
                    n = n.parent;
                }
                simulationCounter += 1;
            } else {
                // board no longer needed at parent level.
                selectedNode.board = null;
                List<Node> newChildren = allLegalMoves.parallelStream().unordered().map(move -> {
                    Board newBoard = b.makeMoveWithoutCheck(move);
                    return new Node(
                            selectedNode, move, newBoard, simulation(playerIdentity, newBoard)
                    );
                }).collect(Collectors.toList());
                selectedNode.children = newChildren;
                int winCount = 0;
                for (int i = 0; i < len; i++) {
                    winCount += newChildren.get(i).winningProbNumerator;
                }
                Node n = selectedNode;
                while (n != null) {
                    n.winningProbNumerator += winCount;
                    n.winningProbDenominator += len;
                    n = n.parent;
                }
                simulationCounter += len;
            }
        }
        System.out.println("# of simulations: " + simulationCounter);
        return simulationCounter;
    }

    /**
     * Give the final move chosen by AI with the format
     * (...decided move, winning probability percentage by that move).
     *
     * @param board the initial board.
     * @param timeLimit time limit in milliseconds.
     * @return the decision.
     */
    static Decision selectMove(@NotNull Board board, long timeLimit) {
        Node root = new Node(board);
        int simulationCounter = think(root, timeLimit);
        Node nodeChosen = null;
        // Find the best move
        double maxWinningProbability = 0;
        for (Node n : root.children) {
            double value = n.getWinningProbability();
            if (value > maxWinningProbability) {
                maxWinningProbability = value;
                nodeChosen = n;
            }
        }
        // noinspection ConstantConditions
        return new Decision(
                nodeChosen.move,
                nodeChosen.getWinningProbabilityInPercentage(),
                simulationCounter
        );
    }

    /**
     * The decision object.
     */
    static final class Decision {

        /**
         * The decided move.
         */
        final Move move;
        /**
         * The winning probability of the decided move.
         */
        final int winningPercentage;
        /**
         * The counter that records the number of simulation done.
         */
        final int simulationCounter;

        private Decision(Move move, int winningPercentage, int simulationCounter) {
            this.move = move;
            this.winningPercentage = winningPercentage;
            this.simulationCounter = simulationCounter;
        }

    }

    /**
     * The internal node.
     */
    private static final class Node {

        /**
         * A parent node. Root node does not have a parent node.
         */
        final @Nullable
        Node parent;
        /**
         * The move on the node.
         */
        final @NotNull
        Move move;
        /**
         * The board on the node.
         */
        Board board;
        /**
         * A list of children nodes.
         */
        @NotNull
        List<Node> children;
        /**
         * Winning probability tracker.
         */
        int winningProbNumerator, winningProbDenominator;

        Node(Board board) {
            this.board = board;
            parent = null;
            move = Move.DUMMY_MOVE;
            children = Collections.emptyList();
            winningProbNumerator = 0;
            winningProbDenominator = 0;
        }

        Node(@NotNull Node parent, @NotNull Move move, @NotNull Board board, int winValue) {
            this.parent = parent;
            this.move = move;
            this.board = board;
            children = Collections.emptyList();
            winningProbNumerator = winValue;
            winningProbDenominator = 1;
        }

        /**
         * @return winning probability between 0 and 1.
         */
        double getWinningProbability() {
            return ((double) winningProbNumerator) / ((double) winningProbDenominator);
        }

        /**
         * @return winning probability in percentage.
         */
        int getWinningProbabilityInPercentage() {
            return (int) (getWinningProbability() * 100);
        }

        /**
         * Get upper confidence bound in MCTS, which needs a [isPlayer] parameter
         * to tell whether to calculate in favor or against the player.
         * <p>
         * Requires: the node is not the root.
         *
         * @param isPlayer whether the user is player.
         */
        double getUpperConfidenceBound(boolean isPlayer) {
            if (parent == null) {
                throw new IllegalArgumentException("Cannot be called on root element!");
            }
            double lnt = Math.log(parent.winningProbDenominator);
            double winningProb = isPlayer ? getWinningProbability() : 1 - getWinningProbability();
            return winningProb + Math.sqrt(2 * lnt / winningProbDenominator);
        }

    }

}
