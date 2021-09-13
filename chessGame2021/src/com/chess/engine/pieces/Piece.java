package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import java.util.Collection;


public abstract class Piece
{
    //Members:
    //-------------------------------
    protected final PieceType pieceType;
    //Every piece has a piece position
    protected final int piecePosition;

    // piece will be either white or black i.e has an Alliance
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cachedHashCode;




    // constructor
    Piece(final PieceType pieceType,
          final Alliance pieceAlliance,
          final int piecePosition,
          final boolean isFirstMove)
    {
        this.pieceType = pieceType;
        this.pieceAlliance = pieceAlliance;
        this.piecePosition = piecePosition;
        // TODO more work here!!
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1: 0);

        return result;
    }

    //Methods:
    // Getter Methods
    public Alliance getPieceAllegiance()
    {
        return this.pieceAlliance;
    }

    public boolean isFirstMove()
    {
        return this.isFirstMove;
    }

    public PieceType getPieceType()
    {
        return this.pieceType;
    }

    public int getPiecePosition()
    {
        return this.piecePosition;
    }

    public int getPieceValue()
    {
        return this.pieceType.getPieceValue();
    }

    /*
    *
    *  Any time you interact with collections of objects you will need to implement he hashCode() and equals().
    *
    * */
    @Override
    public boolean equals(final Object other)
    {
        if(this == other)
        {
            return true;
        }

        if(!(other instanceof Piece))
        {
            return false;
        }

        final Piece otherPiece = (Piece) other;

        return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() &&
               pieceAlliance == otherPiece.getPieceAllegiance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode()
    {
        return this.cachedHashCode;
    }

    // Abstract Methods
    /*
    * Need a method that is responsible for calculating the legal moves
    * for a piece. That is going to return a collection of legal moves.
    *
    * NOTE: originally we had this method set as a List but we changed it to a
    * generic Collection so that the person calling it can choose which collection is best suited
    * */
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    // this method will return a new piece with an updated piece position
    public abstract Piece movePiece(Move move);



    /*
    *
    * A java enum is a special Java type used to define collections of constants example -->
    * imagine we made an enum class called compassDirections. It would have North, South, East and
    * West as constants. Enum is a special type of class.
    *
    * */
    public enum PieceType
    {
        PAWN("P", 100)
            {
                @Override
                public boolean isKing()
                {
                    return false;
                }

                @Override
                public boolean isRook()
                {
                    return false;
                }
            },
        KNIGHT("N", 300)
            {
                @Override
                public boolean isKing()
                {
                    return false;
                }

                @Override
                public boolean isRook()
                {
                    return false;
                }
            },
        BISHOP("B", 300)
            {
                @Override
                public boolean isKing()
                {
                    return false;
                }

                @Override
                public boolean isRook()
                {
                    return false;
                }
            },
        ROOK("R", 500)
            {
                @Override
                public boolean isKing()
                {
                    return false;
                }

                @Override
                public boolean isRook()
                {
                    return true;
                }
            },
        QUEEN("Q", 900)
            {
                @Override
                public boolean isKing()
                {
                    return false;
                }

                @Override
                public boolean isRook()
                {
                    return false;
                }
            },
        KING("K", 10000)
            {
                @Override
                public boolean isKing()
                {
                    return true;
                }

                @Override
                public boolean isRook()
                {
                    return false;
                }
            };

        private String pieceName;
        private int pieceValue;

        // constructor
        PieceType(final String pieceName, final int pieceValue)
        {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();

        public int getPieceValue()
        {
            return this.getPieceValue();
        }

        @Override
        public String toString()
        {
            return this.pieceName;
        }


    }
}
