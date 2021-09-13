package com.chess.engine.pieces;
/*
 *
 * Notes:
 *   + the final keyword is used to make a constant variable
 *       ++ its a standard java practice to define constant variables in all caps as well as to
 *          separate words with underscores.
 *
 *
 * */

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.*;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece
{
    // members
    /*
     *
     * The pawns differs from the other pieces in a variety of different ways.
     *
     * 1. Depending on whether its Alliance it can only move towards the opposing side.
     *
     * 2. The first time a pawn moves it can move two spaces forward towards the opposing
     * side. So a white pawn moving towards the black side can move { -16 } and then
     * going forward all of its non attacking moves will be { -8 }.
     *
     * The first time a black pawn moves forward towards the white side its first
     * non attacking move will be { +16 } and then going forward all of its non
     * attacking moves will be { +8 }
     *
     * 3. If the pawn is attacking then its movement options change. If a black pawn is
     * attacking then it can move +7 or +9. If white pawn is attacking then it can
     * move -7 or -9.
     *
     * 4. There is also another special attacking movement a Pawn can employ called:
     * 'En passant' ---> Scenario: if a white pawn moves two spaces forward on its
     * first move and lands next to an opposing pawn then the opposing black pawn can
     * can make an attacking movement even though the white pawn is not located on
     * the black pawns target tile.
     *
     * 5. If a pawn makes it to the last square on the opposing side of its column
     * then it can be transformed to any piece except the king.
     *
     *
     * */
    private final static int[] CANDIDATE_MOVE_COORDINATES = {8, 16, 7, 9};

    // constructor
    public Pawn(
        final Alliance pieceAlliance,
        final int piecePosition)
    {
        // calling the constructor from the base class i.e Piece
        super(PieceType.PAWN,
            pieceAlliance,
            piecePosition,
            true);
    }

    public Pawn(
        final Alliance pieceAlliance,
        final int piecePosition,
        final boolean isFirstMove)
    {
        // calling the constructor from the base class i.e Piece
        super(PieceType.PAWN,
            pieceAlliance,
            piecePosition,
            isFirstMove);
    }

    // methods

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate =
                this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }
            if (currentCandidateOffset == 8 && board.getPiece(candidateDestinationCoordinate) == null) {
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(
                        new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, candidateDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                        new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, candidateDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                        new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, candidateDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                        new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, candidateDestinationCoordinate)));
                }
                else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            }
            else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                ((BoardUtils.INSTANCE.SECOND_ROW.get(this.piecePosition) && this.pieceAlliance.isBlack()) ||
                    (BoardUtils.INSTANCE.SEVENTH_ROW.get(this.piecePosition) && this.pieceAlliance.isWhite()))) {
                final int behindCandidateDestinationCoordinate =
                    this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                if (board.getPiece(candidateDestinationCoordinate) == null &&
                    board.getPiece(behindCandidateDestinationCoordinate) == null) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            else if (currentCandidateOffset == 7 &&
                !((BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
                if(board.getPiece(candidateDestinationCoordinate) != null) {
                    final Piece pieceOnCandidate = board.getPiece(candidateDestinationCoordinate);
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, candidateDestinationCoordinate)));
                        }
                        else {
                            legalMoves.add(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                    (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        legalMoves.add(
                            new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));

                    }
                }
            }
            else if (currentCandidateOffset == 9 &&
                !((BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
                if(board.getPiece(candidateDestinationCoordinate) != null) {
                    if (this.pieceAlliance !=
                        board.getPiece(candidateDestinationCoordinate).getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                    board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                    board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                    board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                    board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, candidateDestinationCoordinate)));
                        }
                        else {
                            legalMoves.add(
                                new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                    board.getPiece(candidateDestinationCoordinate)));
                        }
                    }
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                    (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        legalMoves.add(
                            new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));

                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public Pawn movePiece(final Move move)
    {
        // this will create a new bishop just in the new move position
        return new Pawn(move.getMovedPiece()
            .getPieceAllegiance(),
            move.getDestinationCoordinate());
    }

    @Override
    public String toString()
    {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece()
    {
        /**
         *  For simplicities sake we will always promote a pawn to a queen but as the rules of chess explain
         *  a pawn that is in a position for promotion can choose any piece to be promoted too.
         */
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }
}
