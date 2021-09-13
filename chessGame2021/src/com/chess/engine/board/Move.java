package com.chess.engine.board;

/*
 *
 * Notes:
 *   + the Java.lang.object has two abstract methods
 *       1. hashCode()
 *       2. equals()
 *
 *   + These two methods are used mainly when constructing a hashTable of some sort
 *
 *   + The equals() is used to compare equality of two Objects. The equality can be compared in two ways.
 *       1. Shallow comparison:
 *           + the default implementation of equals method is defined in Java.lang.Object class
 *             which simply checks if two Objects references (say x and y) refer to the same Object i.e it
 *             checks if x == y.
 *       2. Deep Comparison:
 *           + Suppose a class provides its own implementation of equals() in order to compare the Obj of
 *             that class with the state of the Objects, meaning that the data members (i.e fields) of
 *             Objects are to be compared with one another. So you are going deeper into the objects that
 *             you are comparing.
 *
 *   + Anytime a class overrides the equals() method so to must the hashCode() be overridden.
 *   + hashCode() is used in hashing based collections like: HashMaps, HashTables, HashSets
 *
 *   + General contract of hashCode() is:
 *       1. During the execution of the application, if hashCode() is invoked more than once on the same
 *          Object then it must consistently return the same Integer value, provided no information used
 *          in equals(Object) comparison on the Object is modified. It is not necessary that this integer
 *          value remain the same from one execution of the app to another execution of the app.
 *
 *       2. If two Objects are equal, according to the equals(Obj) method, then hashCode() method must produce
 *           the same Int on each of the two Objects.
 *
 *       3. If two Objects are unequal, according to the equals(object) method, it is not necessary that the Int
 *          value produced by hashCode() method on each of the two Objects will be distinct it is a good practice but
 *          not necessary.
 *
 *       *** Equal Objects must produce the same hash code as long as they are equal, however unequal objects
 *           need not produce distinct hash codes.
 *
 * */


import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move
{
    // members
    protected final Board board;

    protected final Piece movedPiece;

    protected final int destinationCoordinate;

    protected final boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    // Constructor
    private Move(
        final Board board,
        final Piece movedPiece,
        final int destinationCoordinate)
    {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move(
        final Board board,
        final int destinationCoordinate)
    {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.movedPiece.hashCode();
        result = prime * result + this.movedPiece.getPiecePosition();

        return result;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof Move))
        {
            return false;
        }

        final Move otherMove = (Move) other;

        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
            getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
            getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public Board getBoard()
    {
        return this.board;
    }

    public int getDestinationCoordinate()
    {
        return this.destinationCoordinate;
    }

    public boolean isAttack()
    {
        return false;
    }

    public boolean isCastlingMove()
    {
        return false;
    }

    public Piece getAttackedPiece()
    {
        return null;
    }

    /*
     *   To make a move on the board ---> does not mean that you are going to mutate the board --->
     *   it means you are going to materialize a new board into existence that will represent the board
     *   that would exist if you made a move on the incoming board.
     *
     *   The move has a reference to the incoming board so when you execute() the move it will return
     *   a new board with the results of your move.
     * */
    public Board execute()
    {
        /*
         * Creating a new instance of the builder class --> remember that in order to create a board
         * we need to employ the builder class which calls the createStandardBoard().
         *
         * */
        System.out.println("move.execute line 135");
        final Builder builder = new Builder();
        System.out.println("move.execute line 137");
        /*
         *
         * We next want to loop through all the active pieces of our player and as long as the piece
         * is not our moved piece (i.e the piece we have our hand on) then we want to set that piece
         * back to its current position on the new board we will return.
         *
         * */
        for (final Piece piece : this.board.currentPlayer()
            .getActivePieces())
        {
            // in order of the equals() to work we need to have a hashcode() and equals() on the move class
            if (!this.movedPiece.equals(piece))
            {
                builder.setPiece(piece);
            }
        }
        System.out.println("move.execute line 154");

        /*
         *   This will loop through all the active pieces of our current players opponent --> placing
         *   each of the opponents pieces back on the board in their current position.
         *
         * */
        for (final Piece piece : this.board.currentPlayer()
            .getOpponent()
            .getActivePieces())
        {
            builder.setPiece(piece);
        }
        System.out.println("move.execute line 165");

        // move the moved piece!
        builder.setPiece(this.movedPiece.movePiece(this));
        System.out.println("move.execute line 169");

        // this sets the next moveMaker ---> so if white now black
        builder.setMoveMaker(this.board.currentPlayer()
            .getOpponent()
            .getAlliance());
        System.out.println("move.execute line 173");

        // the build() is a method of the builder class that returns a new instance of the board class
        return builder.build();
    }

    public Piece getMovedPiece()
    {
        return this.movedPiece;
    }

    public int getCurrentCoordinate()
    {
        return this.getMovedPiece()
            .getPiecePosition();
    }

    /*
     *
     * */

    public static class MajorAttackMove extends AttackMove
    {
        // Members
        // Constructors
        public MajorAttackMove(
            final Board board,
            final Piece pieceMoved,
            final int destinationCoordinate,
            final Piece pieceAttacked)
        {
            super(board,
                pieceMoved,
                destinationCoordinate,
                pieceAttacked);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return movedPiece.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class MajorMove extends Move
    {
        public MajorMove(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate)
        {
            super(board,
                movedPiece,
                destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return movedPiece.getPieceType()
                .toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class AttackMove extends Move
    {
        // we need to keep track of the piece being attacked
        final Piece attackedPiece;

        public AttackMove(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate,
            final Piece attackedPiece)
        {
            super(board,
                movedPiece,
                destinationCoordinate);

            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode()
        {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other)
        {
            if (this == other)
            {
                return true;
            }

            if (!(other instanceof AttackMove))
            {
                return false;
            }

            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public boolean isAttack()
        {
            return true;
        }

        @Override
        public Piece getAttackedPiece()
        {
            return this.attackedPiece;
        }
    }

    public static class PawnMove extends Move
    {
        public PawnMove(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate)
        {
            super(board,
                movedPiece,
                destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove
    {
        public PawnAttackMove(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate,
            final Piece attackedPiece)
        {
            super(board,
                movedPiece,
                destinationCoordinate,
                attackedPiece);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePosition())
                .substring(0,
                    1) + "x" +
                BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttack extends PawnAttackMove
    {
        public PawnEnPassantAttack(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate,
            final Piece attackedPiece)
        {
            super(board,
                movedPiece,
                destinationCoordinate,
                attackedPiece);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof PawnEnPassantAttack && super.equals(other);
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer()
                .getActivePieces())
            {
                if (!this.movedPiece.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : this.board.currentPlayer()
                .getOpponent()
                .getActivePieces())
            {
                if (!piece.equals(this.getAttackedPiece()))
                {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer()
                .getOpponent()
                .getAlliance());
            return builder.build();
        }
    }

    public static class PawnPromotion extends Move
    {
        // Members:
        final Move decoratedMove;
        final Pawn promotedPawn;
        final Piece promotionPiece;

        // Constructor:
        /*
         * Employing the design pattern called the 'Decorator':
         * Note:
         *
         *
         * */
        public PawnPromotion(final Move decoratedMove,
                             final Piece promotionPiece)
        {
            super(decoratedMove.getBoard(),
                decoratedMove.getMovedPiece(),
                decoratedMove.getDestinationCoordinate());

            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
            this.promotionPiece = promotionPiece;
        }

        // Methods:

        @Override
        public Board execute()
        {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();

            for(final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces())
            {
                if(!this.promotedPawn.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }

            for(final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());

            return builder.build();
        }

        @Override
        public boolean isAttack()
        {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece()
        {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString()
        {
            return "";
        }

        @Override
        public int hashCode()
        {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }
    }

    public static final class PawnJump extends Move
    {
        public PawnJump(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate)
        {
            super(board,
                movedPiece,
                destinationCoordinate);
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();
            // loop through current players active pieces and place them onto the new transition board
            for (final Piece piece : this.board.currentPlayer()
                .getActivePieces())
            {
                if (!this.movedPiece.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            // loop through opponents active pieces and place them onto new transition board
            for (final Piece piece : this.board.currentPlayer()
                .getOpponent()
                .getActivePieces())
            {
                builder.setPiece(piece);
            }
            // set moved piece
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            //switches the player to make the next move to the opposite of current player
            builder.setMoveMaker(this.board.currentPlayer()
                .getOpponent()
                .getAlliance());
            return builder.build();
        }

        @Override
        public String toString()
        {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    static abstract class CastleMove extends Move
    {
        // Members
        protected final Rook castleRook;

        protected final int castleRookStart;

        protected final int castleRookDestination;

        public CastleMove(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate,
            final Rook castleRook,
            final int castleRookStart,
            final int castleRookDestination)
        {
            super(board,
                movedPiece,
                destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        // Methods
        public Rook getCastleRook()
        {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove()
        {
            return true;
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();
            // get a list of the current players active pieces and place them on the new transition
            // board as long as they are not the moved piece.
            for (final Piece piece : this.board.currentPlayer()
                .getActivePieces())
            {
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            // get a list of the current players opponents pieces and replace them on the new transition board.
            for (final Piece piece : this.board.currentPlayer()
                .getOpponent()
                .getActivePieces())
            {
                builder.setPiece(piece);
            }

            // place the moved piece onto the new transition board
            builder.setPiece(this.movedPiece.movePiece(this));

            // put the newly moved rook in is correct castled position
            builder.setPiece(new Rook(this.castleRook.getPieceAllegiance(),
                this.castleRookDestination));

            // setMoveMaker to the opponent
            builder.setMoveMaker(this.board.currentPlayer()
                .getOpponent()
                .getAlliance());

            // this will return a new board
            return builder.build();
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other)
        {
            if (this == other)
            {
                return true;
            }
            if (!(other instanceof CastleMove))
            {
                return false;
            }

            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static final class KingSideCastleMove extends CastleMove
    {
        // Constructor
        public KingSideCastleMove(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate,
            final Rook castleRook,
            final int castleRookStart,
            final int castleRookDestination)
        {
            super(board,
                movedPiece,
                destinationCoordinate,
                castleRook,
                castleRookStart,
                castleRookDestination);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString()
        {

            return "0-0";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove
    {
        public QueenSideCastleMove(
            final Board board,
            final Piece movedPiece,
            final int destinationCoordinate,
            final Rook castleRook,
            final int castleRookStart,
            final int castleRookDestination)
        {
            super(board,
                movedPiece,
                destinationCoordinate,
                castleRook,
                castleRookStart,
                castleRookDestination);
        }

        @Override
        public String toString()
        {
            return "0-0-0";
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }
    }

    public static final class NullMove extends Move
    {
        public NullMove()
        {
            // 65 is an invalid destination coordinate
            super(null,
                65);
        }

        @Override
        public Board execute()
        {
            throw new RuntimeException("Cannot execute the null move!");
        }

        @Override
        public int getCurrentCoordinate()
        {
            // means that there is no current coordinate or that it is invalid
            return -1;
        }
    }

    public static class MoveFactory
    {
        // members
        private static final Move NULL_MOVE = new NullMove();
        // will not be instantiable
        private MoveFactory()
        {
            throw new RuntimeException("Not instantiable!");
        }

        public static Move getNullMove()
        {
            return NULL_MOVE;
        }

        // this method does the work
        public static Move createMove(
            final Board board,
            final int currentCoordinate,
            final int destinationCoordinate)
        {
            for (final Move move : board.getAllLegalMoves())
            {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.getDestinationCoordinate() == destinationCoordinate)
                {
                    return move;
                }
            }

            return NULL_MOVE;
        }
    }

}
