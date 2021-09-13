package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.chess.engine.board.Move.*;

public class Bishop extends Piece
{

    /*
     *     a    b    c   d   e   f   g   h
     * 1   0    1    2   3   4   5   6   7
     * 2   8    9   10  11  12  13   14  15
     * 3   16   17  18  19  20  21   22  23
     * 4   24   25  26  27  28  29   30  31
     * 5   32   33  34  35  36  37   38  39
     * 6   40   41  42  43  44  45   46  47
     * 7   48   49  50  51  52  53   54  55
     * 8   56   57  58  59  60  61   62  63
     *
     * Assume the above grid are all the tiles on a chess board. Now assume a bishop is located
     * on d4 or the 35 vertex. We know that a bishop can move diagonally upper right, upper left, lower right
     * and lower left. If we study these possible moves we see that a bishop can move +7, +9, -7, -9 verts
     * barring any Edge cases.
     *
     * */

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -7, 7, 9};

    // Constructor
    public Bishop(final Alliance pieceAlliance,
           final int piecePosition)
    {
        // super is calling the parent class constructor
        super(PieceType.BISHOP, pieceAlliance, piecePosition, true);
    }

    public Bishop(final Alliance pieceAlliance,
                  final int piecePosition,
                  final boolean isFirstMove)
    {
        // super is calling the parent class constructor
        super(PieceType.BISHOP, pieceAlliance, piecePosition, isFirstMove);
    }

    // Methods:
    /*
    *
    * We are going to loop through each of the CANDIDATE_MOVE_VECTOR_COORDINATES vectors
    * and for each we are going to say that the candidate we want to consider is it valid. If so then apply
    * the offset to that position or ( candidateDestinationCoordinate += candidateCoordinateOffset ) and
    * if that is a valid position ie on the board then we want to check if that position is occupied.
    * If it isn't occupied then we have a new majorMove. If it is occupied by an enemy piece then
    * we execute attackMove. After this check
    *
    * */
    @Override
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();

        // Here we are going to loop through the CANDIDATE_MOVE_VECTOR_COORDINATES
        for(final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATES)
        {
            int candidateDestinationCoordinate = this.piecePosition;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                if(isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate) ||
                  isEighthColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate))
                {
                    break;
                }

                candidateDestinationCoordinate += currentCandidateOffset;

                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
                {
                    final Piece pieceAtDestination = board.getPiece(candidateDestinationCoordinate);
                    if(pieceAtDestination == null)
                    {
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
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

    // Edge Cases:

    private static boolean isFirstColumnExclusion(final int currentCandidate,
                                                  final int candidateDestinationCoordinate)
    {
        return(BoardUtils.INSTANCE.FIRST_COLUMN.get(candidateDestinationCoordinate) &&
            (currentCandidate == -9) || (currentCandidate == 7));
    }

    private static boolean isEighthColumnExclusion(final int currentCandidate,
                                                   final int candidateDestinationCoordinate)
    {
        return (BoardUtils.INSTANCE.EIGHTH_COLUMN.get(candidateDestinationCoordinate) &&
               (currentCandidate == -7) || (currentCandidate == 9));
    }


    // Override Methods:
    @Override
    public String toString()
    {
        return PieceType.BISHOP.toString();
    }

    @Override
    public Bishop movePiece(final Move move)
    {
        // this will create a new bishop just in the new move position
        return new Bishop(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }
}
