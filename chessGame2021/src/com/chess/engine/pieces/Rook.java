package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Rook extends Piece
{
    // Members
    final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-8, -1, 1, 8};

    // Constructor
    // 1st Rook Move
    public Rook(
        final Alliance pieceAlliance,
        final int piecePosition)
    {
        super(PieceType.ROOK, pieceAlliance , piecePosition, true);
    }

    // Non 1st Rook Move
    public Rook(
        final Alliance pieceAlliance,
        final int piecePosition,
        final boolean isFirstMove)
    {
        super(PieceType.ROOK, pieceAlliance , piecePosition, isFirstMove);
    }

    // Methods
    @Override
    public Collection<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();

        // Here we are going to loop through the CANDIDATE_MOVE_VECTOR_COORDINATES
        for(final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES)
        {
            int candidateDestinationCoordinate = this.piecePosition;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                if(isFirstColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)
                    || isEightColumnExclusion(candidateDestinationCoordinate, candidateCoordinateOffset))
                {
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;

                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
                {
                    final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
                    if(pieceAtDestination == null)
                    {
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                    }
                    else
                    {
                        final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();
                        if(this.pieceAlliance != pieceAtDestinationAllegiance)
                        {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }

                        break;
                    }

                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Rook movePiece(final Move move)
    {
        // this will create a new bishop just in the new move position
        return new Rook(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString()
    {
        return PieceType.ROOK.toString();
    }

    // Edge Cases:
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && (candidateOffset == -1);
    }

    private static boolean isEightColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && (candidateOffset == +1);
    }
}
