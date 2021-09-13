package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

/*
* This interface will describe how we want the move engine to conform to.
* */
public interface MoveStrategy
{
    Move execute(Board board);
    long getNumBoardsEvaluated();
}
