package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;

public class MiniMax implements MoveStrategy
{
    // Members:
    private final BoardEvaluator boardEvaluator;
    private long boardsEvaluated;
    private final int searchDepth;

    // Constructor:
    public MiniMax(final int searchDepth)
    {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }


    // Methods:
    @Override
    public String toString()
    {
        return "MiniMax";
    }

    @Override
    public long getNumBoardsEvaluated()
    {
        return this.boardsEvaluated;
    }


    @Override
    public Move execute(Board board)
    {
        final long startTime = System.currentTimeMillis(); // captures current time in milliseconds
        Move bestMove = null;

        // Setting this variable to a very high negative number so that any number we see will be higher
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);

        int numMoves = board.currentPlayer().getLegalMoves().size();

        for(final Move move : board.currentPlayer().getLegalMoves())
        {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone())
            {
                /*
                * White is the maximizing player
                * Black is the minimizing player
                * */
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                    min(moveTransition.getToBoard(), this.searchDepth - 1) :
                    max(moveTransition.getToBoard(), this.searchDepth - 1);

                if(board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue)
                {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if(board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue)
                {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;
        return null;
    }

    /*
     * MiniMax Algorithm:
     * **********************************************************************************
     *  + See: https://en.wikipedia.org/wiki/Minimax#/media/File:Minimax.svg
     *  + Co-recursive algorithm: Min calls Max and Max calls Min
     *
     * */
    public int min(final Board board,
                   final int depth)
    {
        if(depth == 0 || isEndGameScenario(board))
        {
            return this.boardEvaluator.evaluate(board, depth);
        }

        int lowestSeenValue = Integer.MAX_VALUE; // this max value will never be hit
        for(final Move move: board.currentPlayer().getLegalMoves())
        {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone())
            {
                final int currentValue = max(moveTransition.getToBoard(), depth - 1);
                if(currentValue <= lowestSeenValue)
                {
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private static boolean isEndGameScenario(final Board board)
    {
        return board.currentPlayer().isInCheckMate() ||
               board.currentPlayer().isInStaleMate();
    }

    public int max(final Board board,
                   final int depth)
    {
        if(depth == 0 || isEndGameScenario(board))
        {
            return this.boardEvaluator.evaluate(board, depth);
        }

        int highestSeenValue = Integer.MIN_VALUE; // this low value will never be hit
        for(final Move move: board.currentPlayer().getLegalMoves())
        {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone())
            {
                final int currentValue = min(moveTransition.getToBoard(), depth - 1);
                if(currentValue >= highestSeenValue)
                {
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }


}
