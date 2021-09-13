package com.chess.engine;

import com.chess.engine.board.BoardUtils;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;

/*
* An enum is a special 'class' that represents a group of constants(unchangeable
* variables, like final variables).
*
* Enum constants are implicitly static and final and
* can not be changed once created.
*
*
*
* */
public enum Alliance
{
    WHITE
        {
            @Override
            public int getDirection()
            {
                return -1;
            }

            @Override
            public int getOppositeDirection()
            {
                return 1;
            }

            @Override
            public boolean isWhite()
            {
                return true;
            }

            @Override
            public boolean isBlack()
            {
                return false;
            }

            @Override
            public boolean isPawnPromotionSquare(int position)
            {
                return BoardUtils.INSTANCE.EIGHTH_ROW.get(position);
            }

            @Override
            public Player choosePlayer(
                final WhitePlayer whitePlayer,
                final BlackPlayer blackPlayer)
            {
                return whitePlayer;
            }
        },
    BLACK
        {
            @Override
            public int getDirection()
            {
                return 1;
            }

            @Override
            public int getOppositeDirection()
            {
                return -1;
            }

            @Override
            public boolean isWhite()
            {
                return false;
            }

            @Override
            public boolean isBlack()
            {
                return true;
            }

            @Override
            public boolean isPawnPromotionSquare(int position)
            {
                return BoardUtils.INSTANCE.FIRST_ROW.get(position);
            }

            @Override
            public Player choosePlayer(
                final WhitePlayer whitePlayer,
                final BlackPlayer blackPlayer)
            {
                return blackPlayer;
            }
        };

    // Methods
    public abstract int getDirection();
    public abstract int getOppositeDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    // given a tile id i want to know if the tile is a pawn promotion tile
    public abstract boolean isPawnPromotionSquare(int position);

    public abstract Player choosePlayer(
        WhitePlayer whitePlayer,
        BlackPlayer blackPlayer);
}
