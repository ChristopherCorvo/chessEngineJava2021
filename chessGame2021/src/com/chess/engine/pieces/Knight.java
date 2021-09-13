package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Knight extends Piece
{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-17, -15, -10, -6, 6, 10, 15, 17};

    /*
     *
     * Constructor:
     *       + In this constructor we are using super to call the parent constructor
     *         in the Piece class.
     *
     * */
    public Knight(final Alliance pieceAlliance ,
                  final int piecePosition )
    {
        super(PieceType.KNIGHT, pieceAlliance,piecePosition, true );
    }

    public Knight(final Alliance pieceAlliance ,
                  final int piecePosition,
                  final boolean isFirstMove )
    {
        super(PieceType.KNIGHT, pieceAlliance,piecePosition, isFirstMove );
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES)
        {
            if(isFirstColumnExclusion(this.piecePosition,currentCandidateOffset) ||
                isSecondColumnExclusion(this.piecePosition, currentCandidateOffset)||
                isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset)||
                isEighthColumnExclusion(this.piecePosition, currentCandidateOffset))
            {
                continue;
            }
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
                if(pieceAtDestination == null)
                {
                    legalMoves.add(new MajorMove(board,
                        this,
                        candidateDestinationCoordinate));
                }
                else
                {

                    final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();
                    if(this.pieceAlliance != pieceAtDestinationAllegiance)
                    {
                        legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Knight movePiece(final Move move)
    {
        // this will create a new bishop just in the new move position
        return new Knight(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString()
    {
        return PieceType.KNIGHT.toString();
    }

    // Edge cases for knight coordinates
    /*
     * The rule for where a knight can move breaks down depending on which column it is located on.
     * If the knight is located on the last or first column for example there are moves that will take
     * the piece of the table.
     *
     * These below exception methods return a boolean (true or false) and will be placed within
     * the main algorithm calculateLegalMoves()
     *
     * */
    private static boolean isFirstColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        /*
         * BoardUtils.FIRST_COLUMN == an Array of booleans
         *
         * This is saying if the current position of the knight can be found within the FIRST_COLUMN and
         * the possible next moves i.e candidate offset is either -17, -10, 6 or 15 then we know the above algorithm
         * calculateLegalMoves breaks down, so then I now know to skip remaining logic.
         *
         * Note: YOU WILL SEE THIS METHOD IN THE ABOVE calculateLegalMoves()
         * */
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && (candidateOffset == -17 ||
            candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15);
    }

    private static boolean isSecondColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        return BoardUtils.INSTANCE.SECOND_COLUMN.get(currentPosition) && (candidateOffset == -10
            || candidateOffset == 6);
    }

    private static boolean isSeventhColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        return BoardUtils.INSTANCE.SEVENTH_COLUMN.get(currentPosition) && (candidateOffset == -6
            || candidateOffset == 10);
    }

    private static boolean isEighthColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && (candidateOffset == -15 ||
            candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17);
    }
}
