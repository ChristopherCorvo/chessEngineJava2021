package com.chess.engine.pieces;
/*
 *
 * A King can make all the same moves that a Queen can make except that it can
 * only move in those directions one tile.
 *
 * So a King can move { +1, -1, +8, -8, +7, -7, +9, -9}
 *
 * A King can also perform a 'Kings Castle' as a special move.
 *
 *
 *
 *
 * */

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.MajorAttackMove;
import com.chess.engine.board.Move.MajorMove;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class King extends Piece
{
    // Members
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    private final boolean isCastled;

    private final boolean kingSideCastleCapable;

    private final boolean queenSideCastleCapable;


    // Constructor
    public King(
        final Alliance pieceAlliance,
        final int piecePosition,
        final boolean kingSideCastleCapable,
        final boolean queenSideCastleCapable)
    {
        super(PieceType.KING,
            pieceAlliance,
            piecePosition,
            true);
        this.isCastled = false;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public King(
        final Alliance pieceAlliance,
        final int piecePosition,
        final boolean isFirstMove,
        final boolean isCastled,
        final boolean kingSideCastleCapable,
        final boolean queenSideCastleCapable)
    {
        super(PieceType.KING,
            pieceAlliance,
            piecePosition,
            isFirstMove);
        this.isCastled = isCastled;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;

    }

    // Methods
    public boolean isCastled()
    {
        return this.isCastled;
    }

    public boolean isKingSideCastleCapable()
    {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable()
    {
        return this.queenSideCastleCapable;
    }


    @Override
    public Collection<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES)
        {
            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                isEighthColumnExclusion(this.piecePosition, currentCandidateOffset))
            {
                continue;
            }
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
                if (pieceAtDestination == null)
                {
                    legalMoves.add(new MajorMove(board,
                        this,
                        candidateDestinationCoordinate));
                }
                else
                {
                    final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();

                    if (this.pieceAlliance != pieceAtDestinationAllegiance)
                    {
                        legalMoves.add(new MajorAttackMove(board,
                            this,
                            candidateDestinationCoordinate,
                            pieceAtDestination));
                    }
                }
            }

        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public King movePiece(final Move move)
    {
        // this will create a new bishop just in the new move position
        return new King(move.getMovedPiece().getPieceAllegiance(),
                       move.getDestinationCoordinate(),
                       false,
                       move.isCastlingMove(),
                       false,
                       false);

    }

    @Override
    public String toString()
    {
        return PieceType.KING.toString();
    }
    // Exceptions:
    // White King on 1st Col cant move -9 or -1
    // Black King on 1st Col cant move +9 or + 1

    private static boolean isFirstColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && (candidateOffset == -9 ||
            candidateOffset == -1 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(
        final int currentPosition,
        final int candidateOffset)
    {
        return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && (candidateOffset == -7 ||
            candidateOffset == -1 || candidateOffset == 9);
    }
}
