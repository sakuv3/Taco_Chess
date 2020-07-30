package Taco_Chess;
import Taco_Chess.Figures.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    static Button fields[][];

    private King king[];
    private ArrayList <Queen>   queens;
    private ArrayList <Bishop>  bishops;
    private ArrayList<Horse>    horses;
    private ArrayList <Rook>    rooks;
    private ArrayList <Pawn>    pawns;

    public Board( ) throws FileNotFoundException, IOException
    {
        super();
        fields        = new Button[8][8];
        figures       = new Abstract_Figure[8][8];
        controller    = new BoardController();

        createFields();
        define_start_positions();
        drawFigures();
        controller.init( this, fields );
    };

    public void createFields () throws IOException
    {// create 64 fields, each equipping with sensors
        GridPane root = FXMLLoader.load(getClass().getResource("Board.fxml"));
        for( int y=0;y<8;y++)
        {
            for(int x=0;x<8;x++)
            {
                final int xVal = x;
                final int yVal = y;
                fields[x][y] = new Button();
                fields[x][y].setPrefWidth(75);
                fields[x][y].setPrefHeight(75);
                fields[x][y].setId( Integer.toString(x) + Integer.toString(y) );
                fields[x][y].setOnMouseEntered( enter -> controller.buttonEnter( fields[xVal][yVal]) );
                fields[x][y].setOnMouseExited(  exit -> controller.buttonExit( fields[xVal][yVal]) );
                fields[x][y].setOnMouseClicked( clicked -> controller.handleButtonMove( fields[xVal][yVal]) );
                root.add(fields[x][y], x, y);
            }
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Board.css").toExternalForm());
        setScene(scene);
    }

    public void drawFigures( ) throws FileNotFoundException
    {
        int x,y;
        String blackOrWhite         = "";
        String figureType           = "";
        Abstract_Figure[] activeFigs   = getAllFigures();

        for( int i=0; i<activeFigs.length; i++ )
        {
            x = activeFigs[i].getXCoord();
            y = activeFigs[i].getyCoord();

            if( activeFigs[i].isBlack() )
                blackOrWhite = "black/";
            else
                blackOrWhite = "white/";

            if( activeFigs[i] instanceof King )
                figureType = "king.png";
            else if( activeFigs[i] instanceof Queen )
                figureType = "queen.png";
            else if( activeFigs[i] instanceof Rook )
                figureType = "rook.png";
            else if( activeFigs[i] instanceof Horse )
                figureType = "horse.png";
            else if( activeFigs[i] instanceof Bishop )
                figureType = "bishop.png";
            else if( activeFigs[i] instanceof Pawn )
                figureType = "pawn.png";

            /* lets D R A W */
            String PATH = linuxURL + blackOrWhite + figureType;
            fields[x][y].setGraphic( new ImageView(new Image(new FileInputStream( PATH ))) );
        }
    }

    public void removeFigure( Abstract_Figure figure )
    {
        int x = figure.getXCoord();
        int y = figure.getyCoord();

        if( x >=0 && y >=0 && figures[x][y] != null )
        {
            figures[x][y].setCoordinates( -1, -1 );
            figures[x][y] = null;
        }
    }

    public void move_white( Abstract_Figure clickedFigure, Button dest, String PATH ) throws FileNotFoundException {
        int xOld = clickedFigure.getXCoord();
        int yOld = clickedFigure.getyCoord();
        int xNew = (int)dest.getLayoutX() /75;
        int yNew = (int)dest.getLayoutY() /75;

        clickedFigure.setCoordinates(xNew, yNew);

        figures[xOld][yOld] = null;
        figures[xNew][yNew] = clickedFigure;

        fields[xOld][yOld].setGraphic( null );
        fields[xNew][yNew].setGraphic( null );
        fields[xNew][yNew].setGraphic( new ImageView(new Image(new FileInputStream( PATH ))) );
        fields[xNew][yNew].setOnMouseClicked( clicked -> controller.handleButtonMove( fields[xNew][yNew]) );

    }

    public void display_pawn_move( Abstract_Figure clickedFigure, int newX, int newY, final String PATH ) throws FileNotFoundException
    {
        Button newBtn;
        clickedFigure.setValid( true );
        if ((newBtn = get_field(newX, newY)) != null)
        {
            final Button tmp = newBtn;

            Abstract_Figure player = check_figure(newX, newY);
            if( player == null )
                tmp.setGraphic((new ImageView(new Image(new FileInputStream(linuxURL + "circle.png")))));

            else if( player.isBlack() && clickedFigure.isBlack() ||  !player.isBlack() && !clickedFigure.isBlack() ) {
                return; // keine Teamkills hier, ja??!!!!
            }

            tmp.addEventHandler();
            tmp.setOnMouseClicked( click ->
            {
                try {

                    if( clickedFigure.isValid() )
                    {
                        clickedFigure.setValid(false);
                        clear_possible_moves();
                        move_white(clickedFigure, tmp, PATH);
                    }
                }
                catch( FileNotFoundException fex ) {
                    System.out.println("fnfe");
                }
            });
        }
    }
    public void show_possible_moves_black( Abstract_Figure clickedFigure, int x, int y ) throws FileNotFoundException
    {
        clear_possible_moves( );
        final String PATH = linuxURL + "/black/pawn.png";

        if( clickedFigure instanceof Pawn )
        {
            if( y<6 )
            {
                if( check_figure( x, y+1) == null )          // 1up
                    display_pawn_move( clickedFigure, x, y+1, PATH );
                if( y == 1 && check_figure(x, y+2) == null ) // 2up
                    display_pawn_move ( clickedFigure, x, y+2, PATH );

                if( x>0 && check_figure(x-1, y+1) != null)// down-left
                        display_pawn_move( clickedFigure, x-1, y+1, PATH );
                if( x<7 && check_figure(x+1, y+1) != null )   // down-right
                    display_pawn_move( clickedFigure, x+1, y+1, PATH );
            }
        }
    }

    public void show_possible_moves_white( Abstract_Figure clickedFigure, int x, int y ) throws FileNotFoundException
    {
        clear_possible_moves( );
        final String PATH = linuxURL + "/white/pawn.png";

        if (clickedFigure instanceof Pawn)
        {
            if( y>1 )
            {
                if( check_figure( x, y-1) == null ) {
                    display_pawn_move(clickedFigure, x, y - 1, PATH);
                    if (y == 6 && check_figure(x, y - 2) == null)
                        display_pawn_move(clickedFigure, x, y - 2, PATH);
                }

                if( x>0 && check_figure(x-1, y-1) != null )// down-left
                    display_pawn_move( clickedFigure, x-1, y-1, PATH );

                if( x<7 && check_figure(x+1, y-1) != null )   // down-right
                    display_pawn_move( clickedFigure, x+1, y-1, PATH );
            }
        }
    }
    public void clear_possible_moves(  )
    {
        for( int y=0; y<8; y++ )
        {
            for(int x=0; x<8; x++ )
            {
                if( check_figure( x, y) == null )
                    fields[x][y].setGraphic( null );
            }
        }
    }

    // returns the figure, on the field clicked or null if no figure is in the field
    public Abstract_Figure check_figure( int x, int y )
    {
        Abstract_Figure [] figures = getAllFigures();
        for( int i=0; i<figures.length; i++ )
        {
            if( figures[i].getXCoord() == x && figures[i].getyCoord() == y )
                return figures[i];
        }
        return null;
    }

    public Abstract_Figure getFigure( int xCoord, int yCoord )
    {
        for(int y=0; y<8; y++ )
        {
            for(int x=0; x<8; x++ )
            {
                if( figures[x][y].getXCoord() == xCoord && figures[x][y].getyCoord() == yCoord )
                    return figures[x][y];
            }
        }
        return null;
    }

    public Button get_field( int xCoord, int yCoord )
    {
        for(int y=0; y<8; y++ )
        {
            for( int x=0; x<8; x++ )
            {
                if( (int)(fields[x][y].getLayoutX() /75) == xCoord )
                    if( (int)(fields[x][y].getLayoutY() /75) == yCoord )
                        return fields[x][y];
            }
        }
        return null;
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

    public Abstract_Figure[] getAllFigures()
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
        return fields;
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
