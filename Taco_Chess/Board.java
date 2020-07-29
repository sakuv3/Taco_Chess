package Taco_Chess;
import Taco_Chess.Figures.*;
import java.io.FileNotFoundException;

import javafx.scene.control.Button;
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

    public Board(  ) throws FileNotFoundException, IOException
    {
        super();
        figures     = new Abstract_Figure[8][8];
        controller  = new BoardController();

        createTable();
        define_start_positions();
    };

    public void init( Button fields[][] )
    {
        this.fields = fields;
    }
    private void createTable() throws IOException
    {

    }

    public void setFigure( Abstract_Figure figure, int x, int y, boolean isBlack )
    {
        figure.isBlack  = isBlack;
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

        setFigure( rook[0], 0, 0, false);
        setFigure( horse[0], 1, 0, false );
        setFigure( bishop[0], 2, 0, false );
        setFigure( king[0], 3, 0, false);
        setFigure( queen, 4, 0, false );
        setFigure( bishop[1], 5, 0,false );
        setFigure( horse[1], 6, 0, false );
        setFigure( rook[1], 7, 0, false );

        // black Team
        king[1] = new King();
        queen   = new Queen();
        queens.add( queen );

        setFigure( rook[2], 0, 7, true );
        setFigure( horse[2], 1, 7, true );
        setFigure( bishop[2], 2, 7, true );
        setFigure( king[1], 3, 7, true);
        setFigure( queen, 4, 7, true);
        setFigure( bishop[3], 5, 7, true );
        setFigure( horse[3], 6, 7, true );
        setFigure( rook[3], 7, 7, true );

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
                isBlack = false;
            }

            else        // black Team
            {
                x = i-8;
                y = 6;
                isBlack = true;
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

    public void moveFigure( Abstract_Figure figure, int xNew, int yNew )
    {
        int xOld = figure.getXCoord();
        int yOld = figure.getyCoord();

        figures[xOld][yOld] = null;
        figures[xNew][yNew] = figure;

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

            if( activeFigs[i].isBlack )
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

            Button btn = new Button();

        }
    }

    public void show_possibilisites_black( Abstract_Figure[] activeFigs, int x, int y )
    {
        clear_possibilities( activeFigs );

        Abstract_Figure clickedFigure = check_field( activeFigs, x, y );
        if( clickedFigure != null )
        {
            if (clickedFigure instanceof Pawn)
            {
                System.out.println("PAWN");
                int y1 = -1;
                int y2 = -1;
                int x1 = -1;
                int x2 = -1;

                if (y == 6)
                {   // black-pawn
                    y1 = 5;
                    y2 = 4;
                }
            }
        }
    }

    public void clear_possibilities( Abstract_Figure[] activeFigs )
    {

    }

    // returns the figure, on the field clicked or null if no figure is in the field
    public Abstract_Figure check_field( Abstract_Figure []figures, int x, int y )
    {
        for( int i=0; i<figures.length; i++ )
        {
            if( figures[i].getXCoord() == x && figures[i].getyCoord() == y )
                return figures[i];
        }
        return null;
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
