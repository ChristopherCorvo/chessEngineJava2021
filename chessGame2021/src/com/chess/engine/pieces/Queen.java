package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Queen extends Piece
{

    // Member
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    // Constructor
    public Queen(
        final Alliance pieceAlliance,
        final int piecePosition)
    {
        super(PieceType.QUEEN, pieceAlliance,piecePosition, true);
    }

    public Queen(
        final Alliance pieceAlliance,
        final int piecePosition,
        final boolean isFirstMove)
    {
        super(PieceType.QUEEN, pieceAlliance,piecePosition, isFirstMove);
    }

    // Methods
    /*
     *
     * N
     *
     *
     * */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            while (true) {
                if (isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate) ||
                    isEightColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)) {
                    break;
                }
                candidateDestinationCoordinate += currentCandidateOffset;
                if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    break;
                } else {
                    final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
                    if (pieceAtDestination == null) {
                        legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                    } else {
                        final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();
                        if (this.pieceAlliance != pieceAtDestinationAllegiance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
                                pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Queen movePiece(final Move move)
    {
        // this will create a new bishop just in the new move position
        return new Queen(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString()
    {
        return PieceType.QUEEN.toString();
    }


    // Edge Cases:
    private static boolean isFirstColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && (candidateOffset == -1)
            || (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEightColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && (candidateOffset == -7) || (candidateOffset == 1
            || candidateOffset == 9);
    }


}
