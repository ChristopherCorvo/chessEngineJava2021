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

import static com.chess.engine.pieces.Piece.PieceType.*;

public class BlackPlayer extends Player
{

    // Member Fields

    // Constructor
    public BlackPlayer(final Board board,
                       final Collection<Move> blackStandardLegalMoves,
                       final Collection<Move> whiteStandardLegalMoves)
    {
        super(board, blackStandardLegalMoves, whiteStandardLegalMoves);
    }

    // Methods


    // Override Methods
    @Override
    public Collection<Piece> getActivePieces()
    {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance()
    {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent()
    {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(
        final Collection<Move> playerLegals,
        final Collection<Move> opponentsLegals)
    {
        final List<Move> kingCastles = new ArrayList<>();
        // Black king side castle
        // checking if this is the kings move and if the king is in check
        if(this.playerKing.isFirstMove() && !this.isInCheck())
        {
            // if above is true next check that there are no pieces in the 5 or 6 position
            if(this.board.getPiece(5) == null && this.board.getPiece(6) == null)
            {
                // if above true --> create a new rook piece
                final Piece kingSideRook = this.board.getPiece(7);
                // if there is a rook in the 7 piece coordinate and it has not been moved yet then proceed to castle
                if(kingSideRook != null && kingSideRook.isFirstMove() &&
                    Player.calculateAttackOnTile(5, opponentsLegals).isEmpty() &&
                    Player.calculateAttackOnTile(6,opponentsLegals).isEmpty() &&
                    kingSideRook.getPieceType()== ROOK)
                {
                    if(!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12))
                    {
                            kingCastles.add(new KingSideCastleMove(this.board,
                                this.playerKing,
                                6,
                                (Rook) kingSideRook,
                                kingSideRook.getPiecePosition(),
                                5));
                    }
                }
            }

            // Black Queen side castle
            if(this.board.getPiece(1) == null &&
                this.board.getPiece(2) == null &&
                this.board.getPiece(3) == null)
            {
                // if above is true create a new queen side rook piece
                final Piece queenSideRook = this.board.getPiece(0);

                if(queenSideRook != null && queenSideRook.isFirstMove() &&
                   Player.calculateAttackOnTile(2, opponentsLegals).isEmpty() &&
                   Player.calculateAttackOnTile(3, opponentsLegals).isEmpty() &&
                   queenSideRook.getPieceType()== ROOK)
                {
                    if(!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12))
                    {
                        kingCastles.add(new QueenSideCastleMove(this.board,
                                                                this.playerKing,
                                                               2,
                                                               (Rook) queenSideRook,
                                                               queenSideRook.getPiecePosition(),
                                                               3));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
}
