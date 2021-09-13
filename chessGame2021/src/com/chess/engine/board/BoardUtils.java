package com.chess.engine.board;

import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import java.util.*;

/*
*
* This is a utility class where we can put some useful variables
* and methods.
*
* */
public enum BoardUtils
{
    INSTANCE;
    // Column Variables
    public final List<Boolean> FIRST_COLUMN =initColumn(0);
    public final List<Boolean> SECOND_COLUMN = initColumn(1);
    public final List<Boolean> THIRD_COLUMN = initColumn(2);
    public final List<Boolean> FOURTH_COLUMN = initColumn(3);
    public final List<Boolean> FIFTH_COLUMN = initColumn(4);
    public final List<Boolean> SIXTH_COLUMN = initColumn(5);
    public final List<Boolean> SEVENTH_COLUMN = initColumn(6);
    public final List<Boolean> EIGHTH_COLUMN = initColumn(7);

    // Row Variables (Need these specifically for pawn first moves)
    public static final List<Boolean> FIRST_ROW = initRow(0);
    public static final List<Boolean> SECOND_ROW = initRow(8);
    public static final List<Boolean> THIRD_ROW = initRow(16);
    public static final List<Boolean> FOURTH_ROW = initRow(24);
    public static final List<Boolean> FIFTH_ROW = initRow(32);
    public static final List<Boolean> SIXTH_ROW = initRow(40);
    public static final List<Boolean> SEVENTH_ROW = initRow(48);
    public static final List<Boolean> EIGHTH_ROW = initRow(56);

    public static final String[] ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

    /*
    * We are going to use the below two constant variables where ever we need to put the int 64 or 8.
    * We do this for organization and security.
    *
    * */
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;



    //constructor
    private BoardUtils()
    {
        throw new RuntimeException("You cannot instantiate me!");
    }


    // Methods
    /*
     *
     * The method initColumn first declares an array of type boolean. It specifies that the array
     * will have 64 index's i.e there are 64 tiles on a chess board. The do-while loop will then mark
     * all of the tiles in inputted columnNumber true.
     *
     *
     * */
    private static List<Boolean> initColumn(int columnNumber)
    {
        final Boolean[] column = new Boolean[NUM_TILES];
        for(int i = 0; i < column.length; i++)
        {
            column[i] = false;
        }
        do
        {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        }
        while(columnNumber < NUM_TILES);

        return Collections.unmodifiableList(Arrays.asList(column));
    }

    private static List<Boolean> initRow( int rowNumber)
    {
        final Boolean[] row = new Boolean[NUM_TILES];
        for(int i = 0; i<row.length; i++)
        {
            row[i] = false;
        }
        do
        {
            row[rowNumber] = true;
            rowNumber ++;

        } while (rowNumber % NUM_TILES_PER_ROW != 0);
       return Collections.unmodifiableList(Arrays.asList(row));
    }

    /*
    *
    * Originally this method was a private method within the Knight class
    * but since it can be used by other pieces we have moved it into this class
    * and have made it publicly accessible.
    *
    *
    * */
    public static boolean isValidTileCoordinate(final int coordinate)
    {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }

    private static Map<String, Integer> initializePositionToCoordinateMap()
    {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();

        for(int i = 0; i < NUM_TILES; i++)
        {
            positionToCoordinate.put(ALGEBRAIC_NOTATION[i], i);
        }

        return ImmutableMap.copyOf(positionToCoordinate);
    }

    private static String[] initializeAlgebraicNotation()
    {
        return new String[]
            {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
            };
    }


    public static int getCoordinateAtPosition(final String position)
    {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(final int coordinate)
    {
        return ALGEBRAIC_NOTATION[coordinate];
    }

    public static boolean isKingPawnTrap(final Board board,
                                         final King king,
                                         final int frontTile) {
        final Piece piece = board.getPiece(frontTile);
        return piece != null &&
            piece.getPieceType() == Piece.PieceType.PAWN &&
            piece.getPieceAllegiance() != king.getPieceAllegiance();
    }

    public static boolean isEndGame(final Board board) {
        return board.currentPlayer().isInCheckMate() ||
            board.currentPlayer().isInStaleMate();
    }

    public static boolean isThreatenedBoardImmediate(final Board board) {
        return board.whitePlayer().isInCheck() || board.blackPlayer().isInCheck();
    }
}
