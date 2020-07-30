package Taco_Chess;
import Taco_Chess.Figures.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Board extends Stage
{
    static final String linuxURL = "/home/saku/IdeaProjects/Taco/src/Taco_Chess/images/";
    static BoardController controller;
    public Abstract_Figure figures[][];
    static Button chessBoard[][];
    static GridPane root;

    private King king[];
    private ArrayList <Queen>   queens;
    private ArrayList <Bishop>  bishops;
    private ArrayList<Horse>    horses;
    private ArrayList <Rook>    rooks;
    private ArrayList <Pawn>    pawns;

    public Board( ) throws FileNotFoundException, IOException
    {
        super();
        chessBoard    = new Button[8][8];
        figures       = new Abstract_Figure[8][8];
        controller    = new BoardController();

        create_fields();
        define_start_positions();
        draw_figures();
        controller.init( this, root );
    };

    public void create_fields () throws IOException
    {// create 64 fields, each equipping with sensors
        root   = FXMLLoader.load(getClass().getResource("Board.fxml"));
        Scene scene     = new Scene(root);
        for( int y=0;y<8;y++)
        {
            for(int x=0;x<8;x++)
            {
                final int xVal = x;
                final int yVal = y;
                final GridPane grid = root;

                chessBoard[x][y] = new Button();
                chessBoard[x][y].setPrefWidth(100);
                chessBoard[x][y].setPrefHeight(100);
                chessBoard[x][y].setId( Integer.toString(x) + Integer.toString(y) );
                chessBoard[x][y].setOnMouseEntered( enter -> controller.buttonEnter(chessBoard[xVal][yVal]) );
                chessBoard[x][y].setOnMouseExited(  exit -> controller.buttonExit(chessBoard[xVal][yVal]) );
                chessBoard[x][y].setOnMouseClicked( clicked -> controller.handleButtonMove(chessBoard[xVal][yVal]) );
                root.add(chessBoard[x][y], x, y);
            }
        }
        scene.getStylesheets().add(getClass().getResource("Board.css").toExternalForm());
        setScene(scene);
    }

    public void draw_figures( ) throws FileNotFoundException
    {
        int x,y;
        String PATH;
        Abstract_Figure[] activeFigs = get_all_figures();

        for( int i=0; i<activeFigs.length; i++ )
        {
            x = activeFigs[i].getXCoord();
            y = activeFigs[i].getYCoord();

            /* lets D R A W */
            PATH = get_figure_type(activeFigs[i] );
            chessBoard[x][y].setGraphic( new ImageView(new Image(new FileInputStream( PATH ))) );
        }
    }

    public void move_player( Abstract_Figure activePlayer, Button dest ) throws FileNotFoundException
    {
        String PATH = get_figure_type( activePlayer );

        int xOld = activePlayer.getXCoord();
        int yOld = activePlayer.getYCoord();
        int xNew = (int)(dest.getLayoutX() /100);
        int yNew = (int)(dest.getLayoutY() /100);

        figures[xOld][yOld] = null;
        figures[xNew][yNew] = activePlayer;
        figures[xNew][yNew].setCoordinates(xNew, yNew);

        chessBoard[xOld][yOld].setGraphic( null );
        chessBoard[xNew][yNew].setGraphic( new ImageView(new Image(new FileInputStream( PATH ))) );
    }

    public Button get_button( int xCoord, int yCoord )
    {
        for(int y=0; y<8; y++ )
        {
            for( int x=0; x<8; x++ )
            {
                if( (int)(chessBoard[x][y].getLayoutX() /100) == xCoord )
                    if( (int)(chessBoard[x][y].getLayoutY() /100) == yCoord )
                        return chessBoard[x][y];
            }
        }
        return null;
    }

    public void remove_figure( Abstract_Figure figure )
    {
        int x = figure.getXCoord();
        int y = figure.getYCoord();

        if( x >=0 && y >=0 && figures[x][y] != null )
        {
            figures[x][y].setCoordinates( -1, -1 );
            figures[x][y] = null;
        }
    }

    // returns the figure, on the field clicked or null if no figure is in the field
    public Abstract_Figure get_figure( int x, int y )
    {
        Abstract_Figure [] figs = get_all_figures();
        for( int i=0; i<figs.length; i++ )
        {
            if( figs[i].getXCoord() == x && figs[i].getYCoord() == y )
                return figs[i];
        }
        return null;
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

    public String get_figure_type( Abstract_Figure figure )
    {
        String PATH;
        if( figure.isBlack() )
            PATH = linuxURL + "black/";
        else
            PATH = linuxURL +"white/";

        if( figure instanceof King)
            PATH = PATH + "king.png";
        else if( figure instanceof Queen)
            PATH = PATH +"queen.png";
        else if( figure instanceof Rook)
            PATH = PATH + "rook.png";
        else if( figure instanceof Horse )
            PATH = PATH + "horse.png";
        else if( figure instanceof Bishop )
            PATH = PATH + "bishop.png";
        else if( figure instanceof Pawn )
            PATH = PATH + "pawn.png";
        return PATH;
    }

    public void setFigure( Abstract_Figure figure, int x, int y, boolean isBlack )
    {
        figure.setBlack( isBlack );
        figures[x][y]   = figure;
        figure.setCoordinates( x, y );
    }
    private void define_start_positions() throws FileNotFoundException
    {
        Queen queen;
        king    = new King[2];
        queens  = new ArrayList();
        rooks   = new ArrayList();
        bishops = new ArrayList();
        horses  = new ArrayList();
        pawns   = new ArrayList();
        boolean isBlack =false;

        // rooks, horses & pawns - jeweils 2 für jedes Team


        Rook rook[]       = new Rook[4];
        Horse horse[]     = new Horse[4];
        Bishop bishop[]   = new Bishop[4];

        for( int i=0; i<4; i++ )
        {
            rook[i] = new Rook();
            horse[i] = new Horse();
            bishop[i] = new Bishop();

            rooks.add( rook[i] );
            horses.add( horse[i] );
            bishops.add( bishop[i] );
        }

        // white Team
        king[0] = new King();
        queen   = new Queen();
        queens.add( queen );

        setFigure( rook[0], 0, 0, true);
        setFigure( horse[0], 1, 0, true );
        setFigure( bishop[0], 2, 0, true );
        setFigure( queen, 3, 0, true );
        setFigure( king[0], 4, 0, true);
        setFigure( bishop[1], 5, 0,true );
        setFigure( horse[1], 6, 0, true );
        setFigure( rook[1], 7, 0, true );

        // black Team
        king[1] = new King();
        queen   = new Queen();
        queens.add( queen );

        setFigure( rook[2], 0, 7, false );
        setFigure( horse[2], 1, 7, false );
        setFigure( bishop[2], 2, 7, false );
        setFigure( queen, 3, 7, false);
        setFigure( king[1], 4, 7, false);
        setFigure( bishop[3], 5, 7, false );
        setFigure( horse[3], 6, 7, false );
        setFigure( rook[3], 7, 7, false );

        // 16 pawns - 8 for each Team
        for( int i=0; i<16; i++ )
        {
            int x,y;
            Pawn pawn = new Pawn();
            pawns.add( pawn );

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
            setFigure( pawn, x, y, isBlack );
        }
    }

    public void setBishops(ArrayList<Bishop> bishops) {
        this.bishops = bishops;
    }

    public void setFigures(Abstract_Figure[][] figures) {
        this.figures = figures;
    }

    public void setHorses(ArrayList<Horse> horses) {
        this.horses = horses;
    }

    public void setKing(King[] king) {
        this.king = king;
    }

    public void setPawns(ArrayList<Pawn> pawns) {
        this.pawns = pawns;
    }

    public void setQueens(ArrayList<Queen> queens) {
        this.queens = queens;
    }

    public void setRooks(ArrayList<Rook> rooks) {
        this.rooks = rooks;
    }

    public static BoardController getController() {
        return controller;
    }

    public static Button[][] getFields() {
        return chessBoard;
    }

    public ArrayList<Bishop> getBishops() {
        return bishops;
    }

    public ArrayList<Pawn> getPawns() {
        return pawns;
    }

    public ArrayList<Horse> getHorses() {
        return horses;
    }

    public ArrayList<Queen> getQueens() {
        return queens;
    }

    public Abstract_Figure[][] getFigures() {
        return figures;
    }

    public ArrayList<Rook> getRooks() {
        return rooks;
    }

}
