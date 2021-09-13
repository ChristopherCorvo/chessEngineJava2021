package com.chess.engine.player;
/*
 *
 *   Notes:
 *     ++ Java modifiers:
 *        + We divide modifiers into two groups:
 *             1. Access Modifiers : controls the access level
 *             2. Non-Access Modifiers : do not control access level, but provides other functionality
 *
 *        + For classes:
 *             + you can only use:
 *               + public: The class is accessible by any other class.
 *               + default: the class is only accessible by classes in the same package. This is used
 *                 when you don't specify a modifier.
 *
 *        + For attributes, methods, and constructors:
 *              + public: The code is accessible for all classes
 *              + private: The code is only accessible within the declared class
 *              + default: The code is only accessible in the same package. This is used when you don't
 *                         specify a modifier.
 *              + protected: The code is accessible in the same package and subclasses.
 *
 *        + Non-Access Modifiers:
 *
 *           + For Classes:
 *               + final: the class cannot be inherited by other classes.
 *               + abstract: The class cannot be used to create objects
 *                           ++ The access an abstract class, it must be inherited from another class.
 *
 *           + For attributes, methods, and constructors:
 *               + final: attributes and methods cannot be overridden/modified
 *               + static: Attributes and methods belong to the class, rather than an object
 *               + abstract: Can only be used in an abstract class, and can only be used on methods.
 *                       ++ the method does not have a body.
 *                       ++ the body is provided by the subclass (inherited from)
 *               + transient: Attributes and methods are skipped when serializing the object containing them
 *               + synchronized: Methods can only be accessed by one thread at a time.
 *               + volatile: The value of an attribute is not cached thread-locally, and is always
 *                           read from the "main memory"
 *
 *
 *
 * */

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player
{
    // Member Fields
    //-----------------------------
    // the board being played on
    protected final Board board;

    protected final King playerKing;

    protected final Collection<Move> legalMoves;

    private final boolean isInCheck;


    // Constructor
    public Player(
        final Board board,
        final Collection<Move> legalMoves,
        final Collection<Move> opponentMoves)
    {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.
            concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        //calculateAttackOnTile returns a collection isEmpty() is a built in collection method
        this.isInCheck = !Player.calculateAttackOnTile(this.playerKing.getPiecePosition(),
            opponentMoves)
            .isEmpty();
    }

    // Methods
    //------------------------------------

    public Collection<Move> getLegalMoves()
    {
        return this.legalMoves;
    }
    public King getPlayerKing()
    {
        return this.playerKing;
    }
    /*
     * We are going to pass in the Kings current tile location and we are also
     * going to pass in the enemies possible moves and  we are going to check
     * if the enemies moves overlaps with the kings position, if it does then
     * it is attacking the king.
     *
     * */
    protected static Collection<Move> calculateAttackOnTile(
        int piecePosition,
        Collection<Move> moves)
    {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves)
        {
            if (piecePosition == move.getDestinationCoordinate())
            {
                attackMoves.add(move);
            }
        }

        return ImmutableList.copyOf(attackMoves);
    }


    private King establishKing()
    {
        for (final Piece piece : getActivePieces())
        {
            if (piece.getPieceType()
                .isKing())
            {
                return (King) piece;
            }
        }
        /*
         *   Notes:
         *       + the 'throw' keyword is used to create a custom error.
         *       + The 'throw' statement is used together with an exception type.
         *       + The exception type is often used together with a custom method.
         *       + not to be mistaken for 'throws'
         * */
        throw new RuntimeException("Should not reach here! Not a valid board!");
    }

    //Todo: implement these methods below !!!!
    public boolean isMoveLegal(final Move move)
    {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck()
    {
        return false;
    }

    public boolean isInCheckMate()
    {
        return this.isInCheck && !hasEscapeMoves();
    }

    /*
     *
     * */
    protected boolean hasEscapeMoves()
    {
        for (final Move move : this.legalMoves)
        {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus()
                .isDone())
            {
                return true;
            }
        }
        return false;
    }
    //*****************************************************************
    // Why would you have a method that essentially outsources itself to the same method in another class
    public boolean isKingSideCastleCapable()
    {
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable()
    {
        return this.playerKing.isQueenSideCastleCapable();
    }
    //*****************************************************************

    public boolean isInStaleMate()
    {
        return false;
    }

    public boolean isCastled()
    {
        return false;
    }

    /*
     * When we make a move we are going to return a move transition which is
     * going to wrap the board that we transition too if the move was able to
     * take place i.e legal.
     *
     * ---> We came into this method and we first ask if the move is illegal meaning
     * it is not part of the collection of legal moves that the player has then the move transition
     * that you return does not take us to a new board it returns the same board and the move status
     * is illegal. Then we use the move to polymorphic execute the move() and return a new board
     * we transition to. Then we ask are there any attacks on the current players king when we move.
     * And if there are then we should not be able to make that move because you cannot make a move
     * that exposes your king to check. So if those attacks are not empty then again return this board and
     * and move status of leave player in check. Otherwise return the move transition board wrapped
     * in a new transition.
     *
     *
     * */
    public MoveTransition makeMove(final Move move)
    {
        System.out.println("move.currentCoordinate " + move.getCurrentCoordinate() + " move.destinationcoordinate " + move.getDestinationCoordinate());
        System.out.println("makeMove() line 206");
        if (!isMoveLegal(move))
        {
            System.out.println("makeMove line 209");
            return new MoveTransition(this.board,
                this.board,
                move,
                MoveStatus.ILLEGAL_MOVE);
        }
        System.out.println("makeMove() line 214");
        final Board transitionBoard = move.execute(); // method stops here
        System.out.println("makeMove() line 216");
        final Collection<Move> kingAttacks = Player.calculateAttackOnTile(transitionBoard.currentPlayer()
            .getOpponent().getPlayerKing().getPiecePosition(), transitionBoard.currentPlayer().getLegalMoves());
        System.out.println("makeMove() line 219");
        if(!kingAttacks.isEmpty())
        {
            System.out.println("makeMove() line 222");
            return new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        System.out.println("makeMove() func end line 225");
        return new MoveTransition(this.board, transitionBoard, move, MoveStatus.DONE);
    }

    //Abstract Methods
    public abstract Collection<Piece> getActivePieces();

    public abstract Alliance getAlliance();

    public abstract Player getOpponent();

    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegals);

}
