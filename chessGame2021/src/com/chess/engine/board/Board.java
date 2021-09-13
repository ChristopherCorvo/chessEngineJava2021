package com.chess.engine.board;

import com.chess.engine.Alliance;
import com.chess.engine.board.Move.MoveFactory;
import com.chess.engine.pieces.*;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
* Notes: taken from Effective Java 2nd edition
*   + The Builder Pattern: www.informit.com/articles/article.aspx?p=1216151&seqNum=2
*
*       ++ static factories and constructors share a limitation: they do not scale
*           well to large numbers of optional parameters.
*
*       ++ Telescoping constructor pattern --> historically programmers would provide a
*           variety of constructors for a given class with different combinations of parameters.
*           So a class might have 6 constructors that would cover all the variation of parameters
*           a constructor might be called with.
*
*               ++ problem --> is that your code becomes messy and you have to predict
*                   every possible constructor parameter combination that might be
*                   called.
*
*        ++ JavaBeans Pattern --> in this pattern you call a parameterless constructor to create the
*           object and then call setter methods to set each required parameter and each optional parameter
*           of interest.
*               ++ pros --> easy if a bit wordy, to create instances, and easy to read the resulting code.
*               ++ cons -->
*                   ++ the construction of the object i.e the call to the constructor and the setting
*                       of its state via setter methods is spread out over multiple calls. If a problem arises
*                       partway through construction then you are left with an incomplete object instance.
*                   ++ "class does not have the option of enforcing consistency merely by checking the
*                       validity of the constructor parameters"
*                   ++ Attempting to use an object when its in an inconsistent state i.e not complete may
*                       cause failures that are far removed from the code containing the bug, hence difficult
*                       to debug.
*                   ++ A related disadvantage is that the javaBeans pattern precludes the possibility of
*                       making a class immutable and requires added effort on the part of the programmer
*                       to ensure thread safety.
*
*         ++ The builder Pattern:
*               ++ combines the safety of the telescoping constructor pattern with the readability of
*                   JavaBeans pattern.
*               ++ Instead of making the desired object directly, the client calls a constructor
*                   (or static factory) with all of the required parameters and gets a 'builder object'.
*                   The the client calls setter-like methods on the builder object to set each optional parameter
*                   of interest. Finally, the client calls a parameterless build method to generate the
*                   object, which is immutable. The builder is a static member class of the class
*                   it builds.
*

* */
public class Board
{
    // Members
    private final Map<Integer,Piece> boardConfig;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;
    private final Move transitionMove;


    private static final Board STANDARD_BOARD = createStandardBoardImpl();

    // Constructor takes in an instance of the builder class
    private Board(final Builder builder)
    {
//        this.gameBoard = createGameBoard(builder);
        this.boardConfig = Collections.unmodifiableMap(builder.boardConfig);
        this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
        this.transitionMove = builder.transitionMove != null ? builder.transitionMove : MoveFactory.getNullMove();
    }

    /*
    * Notes:
    *   + the java toString():
    *       ++ If you want to represent any object as a string, toString() comes into existence.
    *       ++ The toString() method returns the string representation of the object.
    *       ++ If you print any object, java compiler internally invokes the toString() on the object. So
    *           overriding the toString() method, returns the desired output, it can ve the state of
    *           an object etc, depends on your implementation. \
    *       ++ By overriding the toString() method of the Object class, we can return values of the
    *           object, so we dont need to write much code.
    *
    *   + Java provides three classes to represent a sequence of characters: String, StringBuffer, and
    *       StringBuilder.
    *   + The String class is an immutable class where as StringBuffer and StringBuilder classes are mutable.
    * */
    /*
    * The toString() method has been written so that it goes through and prints out the board
    * in an ascii text way that we will be able to quickly interpret.
    *
    * */
    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        for(int i=0; i < BoardUtils.NUM_TILES; i++)
        {
            final String tileText = prettyPrint(this.boardConfig.get(i));
            builder.append(String.format("%3s",tileText));
            if((i + 1) % 8 == 0)
            {
                builder.append("\n");

            }
        }
        return builder.toString();
    }

    private static String prettyPrint(final Piece piece)
    {
        if(piece != null)
        {
            return piece.getPieceAllegiance().isBlack() ?
                piece.toString().toLowerCase() : piece.toString();
        }

        return "-";
    }

    public Player blackPlayer()
    {
        return this.blackPlayer;
    }

    public Player whitePlayer()
    {
        return this.whitePlayer;
    }

    public Player currentPlayer()
    {
        return this.currentPlayer;
    }

    public Piece getPiece(final int coordinate)
    {
        return this.boardConfig.get(coordinate);
    }

    public Pawn getEnPassantPawn()
    {
        return this.enPassantPawn;
    }

    public Collection<Piece> getBlackPieces()
    {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces()
    {
        return this.whitePieces;
    }

    public Collection<Piece> getAllPieces()
    {
        return Stream.concat(this.whitePieces.stream(),
                             this.blackPieces.stream()).collect(Collectors.toList());
    }

    public Move getTransitionMove() {
        return this.transitionMove;
    }


    // this calculates the legal moves for a given alliance
    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces)
    {
        final List<Move> legalMoves = new ArrayList<>();

        for(final Piece piece: pieces)
        {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }

        return ImmutableList.copyOf(legalMoves);
    }

    // this method allows us to track the white or black active pieces
    /*
    * When a member is declared static, then it essentially means that hte member is shared by
    * all the instances of a class without making copies per instance. A static variable is in essence
    * a global variable.
    *
    * Use static methods to modify static variables.
    *
    * */
    private static Collection<Piece> calculateActivePieces( final Builder builder,
                                                            final Alliance alliance)
    {

        return builder.boardConfig.values().stream()
            .filter(piece -> piece.getPieceAllegiance() == alliance)
            .collect(Collectors.toList());
    }

    // methods:
//    public Tile getTile(final int tileCoordinate)
//    {
//        return gameBoard.get(tileCoordinate);
//    }

//    private static List<Tile> createGameBoard(final Builder builder)
//    {
//        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
//        for(int i = 0; i<BoardUtils.NUM_TILES; i++)
//        {
//            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
//        }
//
//        return ImmutableList.copyOf(tiles);
//    }

    public static Board createStandardBoard()
    {
        return STANDARD_BOARD;
    }

    // This method will use the builder class to create a new standard chess board.
    public static Board createStandardBoardImpl()
    {
        final Builder builder = new Builder();

        // Black Layout
        builder.setPiece(new Rook(Alliance.BLACK, 0));
        builder.setPiece(new Knight(Alliance.BLACK, 1));
        builder.setPiece(new Bishop(Alliance.BLACK, 2));
        builder.setPiece(new Queen(Alliance.BLACK, 3));
        builder.setPiece(new King(Alliance.BLACK, 4, true, true));
        builder.setPiece(new Bishop(Alliance.BLACK, 5));
        builder.setPiece(new Knight(Alliance.BLACK, 6));
        builder.setPiece(new Rook(Alliance.BLACK, 7));

        builder.setPiece(new Pawn(Alliance.BLACK, 8));
        builder.setPiece(new Pawn(Alliance.BLACK, 9));
        builder.setPiece(new Pawn(Alliance.BLACK, 10));
        builder.setPiece(new Pawn(Alliance.BLACK, 11));
        builder.setPiece(new Pawn(Alliance.BLACK, 12));
        builder.setPiece(new Pawn(Alliance.BLACK, 13));
        builder.setPiece(new Pawn(Alliance.BLACK, 14));
        builder.setPiece(new Pawn(Alliance.BLACK, 15));

        // White Layout
        builder.setPiece(new Pawn(Alliance.WHITE, 48));
        builder.setPiece(new Pawn(Alliance.WHITE, 49));
        builder.setPiece(new Pawn(Alliance.WHITE, 50));
        builder.setPiece(new Pawn(Alliance.WHITE, 51));
        builder.setPiece(new Pawn(Alliance.WHITE, 52));
        builder.setPiece(new Pawn(Alliance.WHITE, 53));
        builder.setPiece(new Pawn(Alliance.WHITE, 54));
        builder.setPiece(new Pawn(Alliance.WHITE, 55));

        builder.setPiece(new Rook(Alliance.WHITE, 56));
        builder.setPiece(new Knight(Alliance.WHITE, 57));
        builder.setPiece(new Bishop(Alliance.WHITE, 58));
        builder.setPiece(new Queen(Alliance.WHITE, 59));
        builder.setPiece(new King(Alliance.WHITE, 60, true, true));
        builder.setPiece(new Bishop(Alliance.WHITE, 61));
        builder.setPiece(new Knight(Alliance.WHITE, 62));
        builder.setPiece(new Rook(Alliance.WHITE, 63));

        //white to move
        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();

    }

    public Iterable<Move> getAllLegalMoves()
    {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(),
            this.blackPlayer.getLegalMoves()));
    }


    // builder class
    public static class Builder
    {
        // mutable fields:
        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;

        // builder constructor
        public Builder()
        {
            this.boardConfig = new HashMap<>(32, 1.0f);
        }

        // methods
        public Builder setPiece(final Piece piece)
        {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance nextMoveMaker)
        {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        // this method actually performs the creation of an immutable new board instance
        public Board build()
        {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn movedPawn)
        {
            this.enPassantPawn = enPassantPawn;
        }
    }
}





































