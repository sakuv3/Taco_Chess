package Taco_Chess;
import Taco_Chess.Figures.*;

import java.io.FileNotFoundException;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Board extends Stage
{
    static private  Abstract_Figure figures[][];
    static private  BoardController controller;
    static private  GridPane chessBoard;
    static private  Button buttons[][];
    static private boolean isWhitesMove = true;

    public Board(  ) throws FileNotFoundException, IOException
    {
        super();
        buttons     = new Button[8][8];
        controller  = new BoardController();
        figures     = new Abstract_Figure[8][8];
        chessBoard  = FXMLLoader.load(getClass().getResource("Board.fxml"));

        create_chessBoard();
        define_start_positions();
    };

    public void create_chessBoard() throws IOException
    {
        // makes it look super awesome *___*
        Rectangle r = new Rectangle();
        r.setFill(Color.AQUA);
        r.setWidth(100);
        r.setHeight(100);
        r.setArcHeight(5);
        r.setArcWidth(5);

        // create 64 fields, each getting equipped with sensors
        for( int y=0;y<8;y++)
        {
            for(int x=0;x<8;x++)
            {
                final int xx = x;
                final int yy = y;

                buttons[x][y] = new Button();
                buttons[x][y].setMaxHeight(100);
                buttons[x][y].setMaxWidth(100);
                buttons[x][y].setShape( r );
                buttons[x][y].setId( Integer.toString(x) + Integer.toString(y) );
                buttons[x][y].setOnMouseEntered( enter -> controller.buttonEnter( buttons[xx][yy] ));
                buttons[x][y].setOnMouseExited(  exit -> controller.buttonExit( buttons[xx][yy] ));
                buttons[x][y].setOnMouseClicked( clicked -> controller.handleButtonMove( buttons[xx][yy] ));
                chessBoard.add(buttons[x][y], x, y);
            }
        }
    }

    public void set_figure( Abstract_Figure figure, int x, int y, boolean isBlack )
    {
        figure.setBlack( isBlack );
        figures[x][y]   = figure;
        figures[x][y].setBtn( buttons[x][y] );
        figures[x][y].setCoordinates( x, y );
    }

    private void define_start_positions() throws FileNotFoundException
    {
        Queen queen[]     = new Queen[2];
        King king[]       = new King[2];
        Rook rook[]       = new Rook[4];
        Horse horse[]     = new Horse[4];
        Bishop bishop[]   = new Bishop[4];
        boolean isBlack   = false;

        // rooks, horses & pawns - jeweils 2 für jedes Team

        for( int i=0; i<4; i++ )
        {
            rook[i] = new Rook();
            horse[i] = new Horse();
            bishop[i] = new Bishop();
        }

        // white Team
        king[0]     = new King();
        queen[0]    = new Queen();
        set_figure( rook[0], 0, 0, true);
        set_figure( horse[0], 1, 0, true );
        set_figure( bishop[0], 2, 0, true );
        set_figure( queen[0], 3, 0, true );
        set_figure( king[0], 4, 0, true);
        set_figure( bishop[1], 5, 0,true );
        set_figure( horse[1], 6, 0, true );
        set_figure( rook[1], 7, 0, true );

        // black Team
        king[1]     = new King();
        queen[1]    = new Queen();
        set_figure( rook[2], 0, 7, false );
        set_figure( horse[2], 1, 7, false );
        set_figure( bishop[2], 2, 7, false );
        set_figure( queen[1], 3, 7, false);
        set_figure( king[1], 4, 7, false);
        set_figure( bishop[3], 5, 7, false );
        set_figure( horse[3], 6, 7, false );
        set_figure( rook[3], 7, 7, false );

        // 16 pawns - 8 for each Team
        for( int i=0; i<16; i++ )
        {
            int x,y;
            Pawn pawn = new Pawn();

            if( i < 8 ) // white Team
            {
                x = i;
                y = 1;
                isBlack = true;
            }

            else        // black Team
            {
                x = i-8;
                y = 6;
                isBlack = false;
            }
            set_figure( pawn, x, y, isBlack );
        }
    }

    public Abstract_Figure move_player( Abstract_Figure player, Button dest ) throws FileNotFoundException
    {
        int xOld = player.getXCoord();
        int yOld = player.getYCoord();
        int xNew = get_xCoord_btn( dest );
        int yNew = get_yCoord_btn( dest );

        figures[xOld][yOld] = null;
        figures[xNew][yNew] = player;
        figures[xNew][yNew].setBtn(dest);
        figures[xNew][yNew].setCoordinates(xNew, yNew);

        return figures[xNew][yNew];
    }

    public int get_type( Abstract_Figure figure )
    {
        if( figure instanceof Pawn )
            return 0;
        else if( figure instanceof Bishop )
            return 1;
        else if( figure instanceof Horse)
            return 2;
        else if( figure instanceof Rook)
            return 3;
        else if( figure instanceof Queen)
            return 4;
        else if( figure instanceof King)
            return 5;

        return -1;  // never reached
    }

    public int get_xCoord_btn( Button btn )
    {
        return Character.getNumericValue( btn.getId().charAt(0) );
    }
    public int get_yCoord_btn( Button btn )
    {
        return Character.getNumericValue( btn.getId().charAt(1) );
    }

    public Button get_button( int xCoord, int yCoord )
    {
        if( xCoord >=0 && xCoord <8 && yCoord >=0 && yCoord <8)
            return buttons[xCoord][yCoord];
        return null;
    }

    public Button get_king_btn(  boolean isBblack )
    {
        Abstract_Figure[] figs = get_team( isBblack );

        for (int i = 0; i < figs.length; i++) {
            if( get_type(figs[i]) == 5 )
                return get_button( figs[i].getXCoord(), figs[i].getYCoord() );
        }
        return null;
    }
    // returns the figure, on the field clicked or null if no figure is in the field
    public Abstract_Figure get_figure( Button btn )
    {
        if( btn != null ) {
            Abstract_Figure[] figs = get_all_figures();
            for (int i = 0; i < figs.length; i++) {
                if (figs[i].getBtn().getId().equals(btn.getId()))
                    return figs[i];
            }
        }
        return null;
    }

    public Abstract_Figure[] get_team( boolean isBlack )
    {
        ArrayList<Abstract_Figure> retFigures = new ArrayList();

        for( int y=0; y<8; y++ )
        {
            for( int x=0; x<8; x++ )
            {
                if( figures[x][y] != null && figures[x][y].isBlack() == isBlack )
                    retFigures.add( figures[x][y] );
            }
        }

        Abstract_Figure[] figs = new Abstract_Figure[retFigures.size()];
        return retFigures.toArray(figs);
    }
    public Abstract_Figure[] get_all_figures()
    {
        ArrayList<Abstract_Figure> retFigures = new ArrayList();

        for( int y=0; y<8; y++ )
        {
            for( int x=0; x<8; x++ )
            {
                if( figures[x][y] != null )
                    retFigures.add( figures[x][y] );
            }
        }

        Abstract_Figure[] figs = new Abstract_Figure[retFigures.size()];
        return retFigures.toArray(figs);
    }

    public void setFigures(Abstract_Figure[][] figures) {
        this.figures = figures;
    }

    public GridPane getChessBoard() {
        return chessBoard;
    }

    public static boolean isWhitesMove() {
        return isWhitesMove;
    }

    public static void setIsWhitesMove(boolean isWhitesMove) {
        Board.isWhitesMove = isWhitesMove;
    }

    public BoardController getController() {
        return controller;
    }

    public Button[][] getFields() {
        return buttons;
    }

    public Abstract_Figure[][] getFigures() {
        return figures;
    }

}
