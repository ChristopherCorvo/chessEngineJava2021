package com.chess.gui;


import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.board.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.chess.engine.player.ai.MoveStrategy;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;


/*
*
* The Observer class is deprecated so will need a work around.
*
* Lookup the Observable Java Pattern
*/
public class Table extends Observable
{
    // Members
    /*
     * What is JFrame?
     *
     * JFrame is a class that creates a 'top-level container that provides a window on the screen. A
     * frame is actually a base window on which other components rely, namely the menu bar, panels,
     * labels, text fields, buttons, etc.
     *
     * */
    private final JFrame gameFrame;

    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;

    private final BoardPanel boardPanel;

    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Board chessBoard;

    private Piece sourceTile;

    private Tile destinationTile;

    private Piece humanMovedPiece;

    private BoardDirection boardDirection;
    private Move computerMove;

    private boolean highlightLegalMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600,
        600);

    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,
        350);

    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10,
        10);

    private static String defaultPieceImagePath = "art/pieces/plain/";

    private final Color lightTileColor = Color.decode("#FFFACD");

    private final Color darkTileColor = Color.decode("#593E1A");

    // Constructors
    /*
    * Research the Java Singleton Pattern: https://www.tutorialspoint.com/design_pattern/singleton_pattern.htm
    *
    * */
    private static final Table INSTANCE = new Table();
    private Table()
    {
        // creates a new instance of JFrame
        this.gameFrame = new JFrame("JChess");
        /*
         * JMenuBar: is an implementation of menu bar. The JMenuBar contains one or more JMenu objects
         * when the JMenu objects are selected they display a popup showing one or more JMenuItems.
         * */
        final JMenuBar tableMenuBar = createTableMenuBar();
//        populateMenuBar(tableMenuBar);
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setLayout(new BorderLayout());
        this.chessBoard = Board.createStandardBoard();
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMoves = false;
//        this.useBook = false;
//        this.pieceIconPath = "art/holywarriors/";
        this.gameHistoryPanel = new GameHistoryPanel();
//        this.debugPanel = new DebugPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
//        this.gameFrame.add(debugPanel, BorderLayout.SOUTH);
//        setDefaultLookAndFeeDecorated(true);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
//        center(this.gameFrame);
        this.gameFrame.setVisible(true);
    }

    // Methods:

    public void show()
    {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
//        Table.get().getDebugPanel().redo();
    }

    public static Table get()
    {
        return INSTANCE;
    }

    private GameSetup getGameSetup()
    {
        return this.gameSetup;
    }

    private Board getGameBoard()
    {
        return this.chessBoard;
    }

    /*
    * Any additional Menus or menu items will be added to this createTableMenuBar().
    * When this method is called in the constructor all the menu features are created or
    * instantiated.
    *
    * */
    private JMenuBar createTableMenuBar()
    {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu()
    {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        /*
         * ActionListener in Java is a class that is responsible for handling all action events such
         * as when the user clicks on a component.
         * */
        openPGN.addActionListener(new ActionListener()
        {
            /*
             * When the user clicks the onscreen button, the button fires an action event. This
             * results in the invocation of the action listener's actionPerformed method (the only method
             * in the ActionListener interface). The single argument to the method is an ActionEvent obj
             * that gives information about the event and its source.
             *
             * */
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("open up that pgn file!");
            }
        });

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        fileMenu.add(openPGN);
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    /*
    * This is the pattern for creating menu's and menuItems on a GUI
    * */
    private JMenu createPreferencesMenu()
    {
        final JMenu preferenceMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferenceMenu.add(flipBoardMenuItem);
        preferenceMenu.addSeparator();
        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
        legalMoveHighlighterCheckbox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
            }
        });

        preferenceMenu.add(legalMoveHighlighterCheckbox);
        return preferenceMenu;
    }

    private JMenu createOptionsMenu()
    {
        final JMenu optionsMenu = new JMenu("Options");

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });

        optionsMenu.add(setupGameMenuItem);
        return optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup)
    {
        setChanged();
        notifyObservers(gameSetup);
    }

    public void updateGameBoard(final Board board)
    {
        this.chessBoard = board;
    }

    public void updateComputerMove(final Move move)
    {
        this.computerMove = move;
    }

    private MoveLog getMoveLog()
    {
        return this.moveLog;
    }

    private GameHistoryPanel getGameHistoryPanel()
    {
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel()
    {
        return this.takenPiecesPanel;
    }

    private BoardPanel getBoardPanel()
    {
        return this.boardPanel;
    }

    private void moveMadeUpdate(final PlayerType playerType)
    {
        setChanged();
        notifyObservers(playerType);
    }

    /*
    * Research SwingWorker Java Pattern -->
    *
    *
    * */
    private static class AIThinkTank extends SwingWorker<Move, String >
    {
        // Constructor:
        private AIThinkTank()
        {

        }

        // Key method you need to override
        @Override
        protected Move doInBackground() throws Exception
        {
            // This is the point where we tie back into the MiniMax() search algorithm.
            // Members:
            final MoveStrategy miniMax = new MiniMax(4); // to go further then 4 you need alphabeta
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done()
        {
            try
            {
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(),Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }
        }
    }
    // The Observer class is deprecated so will need a work around
    private static class TableGameAIWatcher implements Observer
    {
        @Override
        public void update(
            Observable o,
            Object arg)
        {
            // if the currentPlayer is an AI then we want to make the AI move
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
              !Table.get().getGameBoard().currentPlayer().isInCheckMate() &&
              !Table.get().getGameBoard().currentPlayer().isInStaleMate())
            {
                //create an AI thread
                //execute ai work
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            // game is over --> checkMate
            if(Table.get().getGameBoard().currentPlayer().isInCheckMate())
            {
                System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!");
            }

            // game is over --> staleMate
            if(Table.get().getGameBoard().currentPlayer().isInStaleMate())
            {
                System.out.println("Game Over, " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!");

            }
        }
    }

    /*
    * Research: Swing worker
    *
    * */
    /*
    * This enum is going to tell us in which direction the board is currently set up. Is it setup in
    * the NORMAL or FLIPPED fashion.
    * */
    public enum BoardDirection
    {
        NORMAL
            {
                @Override
                List<TilePanel> traverse(final List<TilePanel> boardTiles)
                {
                    return boardTiles;
                }

                @Override
                BoardDirection opposite()
                {
                    return FLIPPED;
                }
            },

        FLIPPED
            {
                @Override
                List<TilePanel> traverse(final List<TilePanel> boardTiles)
                {
                    /*
                    * Lists.reverse is a google guava method that allows you to easily go through
                    * an Array list in the reverse order. (Convenience method)
                    */
                    return Lists.reverse(boardTiles);
                }

                @Override
                BoardDirection opposite()
                {
                    return NORMAL;
                }
            };

        // how to
        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        // for a given enum value get the opposite
        abstract BoardDirection opposite();
    }

    // Sub or Inner Classes
    /*
     *
     * JPanel is a class in java that is a container that can store a group of components. The
     * main task of JPanel is to organize components, various layouts can be set in JPanel which
     * provides better organization of components.
     *
     * */
    // corresponds to a chess board
    private class BoardPanel extends JPanel
    {
        final List<TilePanel> boardTiles;

        // constructor
        BoardPanel()
        {
            /*

             * GridLayout = a layout that places its children in a rectangular grid. The grid is
             * composed of a set of infinitely thin lines that separate the viewing area into cells.
             * Throughout the API, grid lines are referenced by grid indices.
             *
             * */
            super(new GridLayout(8,
                8));
            this.boardTiles = new ArrayList<>();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++)
            {
                final TilePanel tilePanel = new TilePanel(this,
                    i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }

            /*
             *
             * setPreferredSize == will layout the components as expected if a layout manager is present (GridBagLayout);
             * most layout managers work by getting the preferred (as well as minimum and maximum) sizes of their
             * components, then using setSize() and setLocation() to position those components according to the layouts
             * rules.
             *
             * */
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        // methods
        public void drawBoard(final Board board)
        {
            removeAll();
            /*
             * Execute the for loop in the direction of boardDirection if it is NORMAL traverse in normal
             * order and if it is set to Opposite then traverse in reverse order.
             */
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles))
            {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog
    {
        // members
        private final List<Move> moves;

        // constructors
        MoveLog()
        {
            this.moves = new ArrayList<>();
        }
        // methods
        public List<Move> getMoves()
        {
            return this.moves;
        }
        public void addMove(final Move move)
        {
            this.moves.add(move);
        }
        public int size()
        {
            return this.moves.size();
        }
        public void clear()
        {
            this.moves.clear();
        }
        /************************************************/
        /******** Example of Method Overriding***********/
        public Move removeMove(int index)
        {
            return this.moves.remove(index);
        }
        public boolean removeMove(final Move move)
        {
            return this.moves.remove(move);
        }
        /************************************************/
    }

    enum PlayerType
    {
        HUMAN,
        COMPUTER
    }

    // Corresponds to an individual tile on a chess board
    private class TilePanel extends JPanel
    {

        private final int tileId;

        // constructor
        TilePanel(
            final BoardPanel boardPanel,
            final int tileId)
        {
            /*
             *
             * GridBagLayout == is one of the most flexible and complex layout managers the java platform
             * provides. A GridBagLayout places components in a grid of rows and columns, allowing specified
             * components to span multiple rows or columns
             *
             * */
            super(new GridBagLayout());
            this.tileId = tileId;

            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);
            highlightTileBorder(chessBoard);

            /*
             * MouseListener() --> is an interface in java.awt.event package. Mouse events are of two types.
             * MouseListener handles the events when the mouse is not in motion. While MouseMotionListener handles
             * the events when mouse is in motion. There are five abstract functions that represent these five events.
             *   1. mouseRelease --> mouse key is released
             *   2. mouseClicked --> mouse key is pressed/released
             *   3. mouseExited --> Mouse exited the component
             *   4. mouseEntered --> Mouse entered the component
             *   5. mousePressed --> Mouse key is pressed
             *
             *  These 5 methods are overridden and unique logic is applied to them for a specific use case.
             *
             * */
            addMouseListener(new MouseListener()
            {
                // When you click a tile on the board its going to come into this area.
                @Override
                public void mouseClicked(final MouseEvent e)
                {
                    if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) ||
                    BoardUtils.isEndGame(Table.get().getGameBoard()))
                    {
                        return;
                    }

                    if (isRightMouseButton(e))
                    {
                        sourceTile = null;
                        humanMovedPiece = null;
                    }
                    else if (isLeftMouseButton(e))
                    {
                        // did you make a sourceTile selection? if not then do this
                        if (sourceTile == null)
                        {
                            sourceTile = chessBoard.getPiece(tileId);
                            humanMovedPiece = sourceTile;


                            // if you dont end up making a selection then reset sourceTile
                            if (humanMovedPiece == null)
                            {
                                sourceTile = null;
                            }
                        }
                        else
                        {
                            final Move move = Move.MoveFactory.createMove(chessBoard,
                                sourceTile.getPiecePosition(),
                                tileId);

                            final MoveTransition transition = chessBoard.currentPlayer()
                            .makeMove(move);

                            if (transition.getMoveStatus().isDone())
                            {
                                chessBoard = transition.getToBoard();
                                moveLog.addMove(move);
                            }
                            // After successfully making the move reset variables back to null
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                /*
                                * moveLog represents the moves that were made on the
                                * board we are currently looking at.
                                * */
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if(gameSetup.isAIPlayer(chessBoard.currentPlayer()))
                                {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }


                @Override
                public void mousePressed(final MouseEvent e)
                {

                }

                @Override
                public void mouseReleased(final MouseEvent e)
                {

                }

                @Override
                public void mouseEntered(final MouseEvent e)
                {

                }

                @Override
                public void mouseExited(final MouseEvent e)
                {

                }
            });

            validate();

        }

        // Private Methods:

        /*
         * If you are on the 1st, 3rd, 5th or 7th --> then
         * we ask if the tile id is even then color the tile in with a light color else
         * fill in with a black color.
         *
         * Else if we are on 2nd, 4th, 6th or 8th row --> then
         * if the tile id is not even then color light and if even color dark.
         *
         * If you look at a chess board then this will make sense.
         * */
        private void assignTileColor()
        {
            if (BoardUtils.INSTANCE.EIGHTH_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.SIXTH_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.FOURTH_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.SECOND_ROW.get(this.tileId))
            {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.INSTANCE.SEVENTH_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.FIFTH_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.THIRD_ROW.get(this.tileId) ||
                BoardUtils.INSTANCE.FIRST_ROW.get(this.tileId))
            {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }

        public void drawTile(final Board board)
        {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightTileBorder(board);
            highlightLegals(board);
            highlightAIMove();
            /*
            * validate() -->
            * */
            validate();
            /*
            * repaint() -->
            * */
            repaint();
        }

        private void assignTilePieceIcon(final Board board)
        {
            this.removeAll();
            if (board.getPiece(this.tileId) == null)
            {

                try
                {
                    /*
                     * BufferedImage is a subclass or descendant of the Image class.
                     *
                     * ImageIO --> javax.imageio.ImageIO is a utility class which provides lots of utility
                     * methods related to images processing in java. Most common is reading from image
                     * files and writing images to file in java.
                     *
                     * */
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagePath +
                        board.getPiece(this.tileId)
                        .getPieceAllegiance()
                        .toString()
                        .substring(0, 1) +
                        board.getPiece(this.tileId)
                        .toString() +
                        ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        /*
        * This method will be part of a menu option that the user gets to decide about.
        * */
        private void highlightLegals(final Board board)
        {
            if(highlightLegalMoves)
            {
                for(final Move move : pieceLegalMoves(board))
                {
                    if(move.getDestinationCoordinate() == this.tileId)
                    {
                        try
                        {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        }
                        catch(Exception e)
                        {
                            /*
                            * printStackTrace() -->
                            * */
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private void highlightTileBorder(final Board board)
        {
            if(humanMovedPiece != null &&
               humanMovedPiece.getPieceAllegiance() == board.currentPlayer().getAlliance() &&
               humanMovedPiece.getPiecePosition() == this.tileId)
            {
                setBorder(BorderFactory.createLineBorder(Color.cyan));
            }
            else
            {
                setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        }
        private void highlightAIMove()
        {
            if(computerMove != null)
            {
                if(this.tileId == computerMove.getCurrentCoordinate())
                {
                    setBackground(Color.pink);
                }
                else if (this.tileId == computerMove.getDestinationCoordinate())
                {
                    setBackground(Color.red);
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board)
        {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAllegiance() == board.currentPlayer().getAlliance())
            {
                return humanMovedPiece.calculateLegalMoves(board);
            }

            return Collections.emptyList();
        }

    }


}
