package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Move.KingSideCastleMove;
import com.chess.engine.board.Move.QueenSideCastleMove;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.engine.pieces.Piece.PieceType.ROOK;

public class WhitePlayer extends Player
{

    // Member Fields


    // Constructor
    public WhitePlayer(
        final Board board,
        final Collection<Move> whiteStandardLegalMoves,
        final Collection<Move> blackStandardLegalMoves)
    {
        super(board,
            whiteStandardLegalMoves,
            blackStandardLegalMoves);
    }

    // Methods

    // Override Methods
    @Override
    public Collection<Piece> getActivePieces()
    {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance()
    {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent()
    {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(
        final Collection<Move> playerLegals,
        final Collection<Move> opponentsLegals)
    {
        final List<Move> kingCastles = new ArrayList<>();
        // Whites king side castle
        // checking if this is the kings move and if the king is in check
        if (this.playerKing.isFirstMove() && !this.isInCheck())
        {
            // if above is true next check that there are no pieces in the 61 or 62 position
            if (this.board.getPiece(61) == null && this.board.getPiece(62) == null)
            {
                // if above true --> create a new rook piece
                final Piece kingSideRook = this.board.getPiece(63);
                // if there is a rook in the 63 piece coordinate and it has not been moved yet then proceed to castle
                if (kingSideRook != null && kingSideRook.isFirstMove())
                {
                    // check if coordinate 61 or 62 have an opponent piece attacking it i.e a Queen siting on he column
                    if (Player.calculateAttackOnTile(61,
                        opponentsLegals)
                        .isEmpty() &&
                        Player.calculateAttackOnTile(62,
                            opponentsLegals)
                            .isEmpty() &&
                        kingSideRook.getPieceType() == ROOK)
                    {
                        kingCastles.add(new KingSideCastleMove(this.board,
                            this.playerKing,
                            62,
                            (Rook) kingSideRook,
                            kingSideRook.getPiecePosition(),
                            61));
                    }
                }
            }

            // Whites Queen side castle
            if (this.board.getPiece(59) == null &&
                this.board.getPiece(58) == null &&
                this.board.getPiece(57) == null)
            {
                // if above is true create a new queen side rook piece
                final Piece queenSideRook = this.board.getPiece(56);

                if (queenSideRook != null && queenSideRook.isFirstMove())
                {
                    if (Player.calculateAttackOnTile(58,
                        opponentsLegals)
                        .isEmpty() &&
                        Player.calculateAttackOnTile(59,
                            opponentsLegals)
                            .isEmpty() &&
                        queenSideRook.getPieceType() == ROOK)
                    {
                        if (!BoardUtils.isKingPawnTrap(this.board,
                            this.playerKing,
                            52))
                        {
                            kingCastles.add(new QueenSideCastleMove(this.board,
                                this.playerKing,
                                58,
                                (Rook) queenSideRook,
                                queenSideRook.getPiecePosition(),
                                59));
                        }
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
