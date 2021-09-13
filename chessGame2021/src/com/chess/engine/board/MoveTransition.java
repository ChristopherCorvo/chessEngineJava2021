package com.chess.engine.board;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveStatus;

/*
*
* This class represents when you make a move, the transition from one board to the next.
*
* Clarification: In essence everytime you make a move you are creating a new board object that has a new state.
* i.e think that each move creates a new snap shot of the board.
*
* */
public class MoveTransition
{
    // Member Fields
    private final Board fromBoard;
    private final Board toBoard;
    private final Move transitionMove;


    /**
     * Tells us if we will be able to do the move or if we cant. For example we could not move
     * ourselves into check or its not a legal move.
    **/
    private MoveStatus moveStatus;

    // Constructor
    public MoveTransition(final Board fromBoard,
                          final Board toBoard,
                          final Move transitionMove,
                          final MoveStatus moveStatus)
    {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.transitionMove = transitionMove;
        this.moveStatus = moveStatus;
    }

    // Methods

    public Board getFromBoard()
    {
        return this.fromBoard;
    }

    public Board getToBoard()
    {
        return this.toBoard;
    }

    public Move getTransitionMove()
    {
        return this.transitionMove;
    }

    public MoveStatus getMoveStatus()
    {
        return this.moveStatus;
    }
}
