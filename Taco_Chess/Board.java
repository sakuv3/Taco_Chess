package Taco_Chess;
import java.io.FileNotFoundException;

import Taco_Chess.Figures.*;
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Board extends GridPane
{
    static final String linuxURL = "/home/saku/IdeaProjects/Taco/src/Taco_Chess/images/";
    public static Label feld[][];   // das Schachfeld
    public Abstract_Figure figures[][];

    private King king[];
    private ArrayList <Queen>    queens;
    private ArrayList <Bishop>   bishops;
    private ArrayList <Horse>    horses;
    private ArrayList <Rook>     rooks;
    private ArrayList <Pawn>     pawns;

    public Board() throws FileNotFoundException
    {
        feld    = new Label[8][8];
        figures = new Abstract_Figure[8][8];

        createTable();
        initFigures();
        drawFigures();
    };

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
        return (Abstract_Figure[])retFigures.toArray(figs);
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
            feld[x][y].setGraphic( null );
            figures[x][y].setCoordinates( -1, -1 );
            figures[x][y] = null;
        }
    }

    public boolean setFigure( Abstract_Figure figure, int x, int y, boolean isBlack )
    {
        figure.isBlack  = isBlack;
        figures[x][y]   = figure;
        figure.setCoordinates( x, y );
        return true;
    }

    private void initFigures() throws FileNotFoundException
    {
        Queen queen;
        king    = new King[2];
        queens  = new ArrayList();
        rooks   = new ArrayList();
        bishops = new ArrayList();
        horses  = new ArrayList();
        pawns   = new ArrayList();
        boolean isBlack =false;

        Abstract_Figure[][] FIGUREZ = figures;
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
        setFigure( queen, 3, 0, false );
        setFigure( king[0], 4, 0, false);
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
        setFigure( queen, 3, 7, true);
        setFigure( king[1], 4, 7, true);
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

    private void createTable()
    {
        // erzeuge schwarz/weiße Felder
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++)
            {
                feld[i][j] = new Label();
                feld[i][j].setMinSize(50, 50);

                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0))
                    feld[i][j].setStyle("-fx-background-color: sienna");
                else
                    feld[i][j].setStyle("-fx-background-color: blanchedalmond");

                this.add(feld[i][j], i, j);
            }
        }
    }

    private void drawFigures(  ) throws FileNotFoundException
    {
        int x,y;
        String blackOrWhite         = "white";
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

            /* DRAWS here */
            String PATH = linuxURL + blackOrWhite + figureType;
            feld[x][y].setGraphic((new ImageView(new Image(new FileInputStream( PATH )))));

            /* AB HIER WERDEN EVENTS GEHANDLED !!! BEWARE DANGEROUS ZONE xD */

            /* Lambda-Expression zum einfärben der Feld-Position */
            feld[x][y].setOnMouseEntered(event ->
            {
                String color ="";
                int xCoord,yCoord;
                xCoord = (int)((Label)event.getSource()).getLayoutX() / 50;
                yCoord = (int)((Label)event.getSource()).getLayoutY() / 50;

                if( (xCoord + yCoord) % 2 == 0 )// schwarzes Feld
                    color = "sienna";


                else // weißes Feld
                    color = "blanchedalmond";

                ((Label) event.getSource()).setStyle(
                        "-fx-border-color: #FF33CC;"
                                + "-fx-background-color: " +color );

                //System.out.printf("X: %.0f Y: %.0f\n", xCoord, yCoord);
            });

            /* Lambda-Expression zum ausfärben der Feld-Position */
            feld[x][y].setOnMouseExited( event ->
            {
                String color ="";
                int xCoord,yCoord;
                xCoord = (int)((Label)event.getSource()).getLayoutX() / 50;
                yCoord = (int)((Label)event.getSource()).getLayoutY() / 50;

                if( (xCoord + yCoord) % 2 == 0 )// schwarzes Feld
                   color = "sienna";


                else // weißes Feld
                    color = "blanchedalmond";

                ((Label) event.getSource()).setStyle(
                        "-fx-border-color: " +color +";"
                                + "-fx-background-color: " +color);

            });

            /* Spieler klickt auf den Spieler mit dem er sich Fortbewegen will */
            feld[x][y].setOnMouseClicked( event ->
            {
                int xCoord,yCoord;
                xCoord = (int)((Label)event.getSource()).getLayoutX() / 50;
                yCoord = (int)((Label)event.getSource()).getLayoutY() / 50;

                show_possibilisites_white( activeFigs, xCoord, yCoord );
            });
        }
    }

    public void show_possibilisites_white( Abstract_Figure [] activeFigs, int x, int y )
    {
        clear_possibilities( activeFigs );

        Abstract_Figure clickedFigure = check_field( activeFigs, x, y );
        if( clickedFigure != null )
        {
            try
            {
                if (clickedFigure instanceof Pawn)
                {
                    System.out.println("PAWN");

                    if ( y>0 && y<8 )
                    {
                        // white pawn can move 1 field straight
                        if( check_field(activeFigs, x, y+1) == null ) {
                            feld[x][y+1].setGraphic((new ImageView(new Image(new FileInputStream(linuxURL + "circle.png")))));

                            feld[x][y+1].setOnMouseClicked( event ->
                            {
                                clear_possibilities ( activeFigs );
                                removeFigure( clickedFigure );

                                setFigure( clickedFigure, x, y+1, false );
                                try
                                {
                                    drawFigures();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                            });
                        }
                        if( y == 1 )
                        {   // white pawn is in start-position -> can move 2 field straight
                            if (check_field(activeFigs, x, y+2) == null)
                                feld[x][y+2].setGraphic((new ImageView(new Image(new FileInputStream(linuxURL + "circle.png")))));
                        }

                        if( x > 0 && x < 8 )
                        {   // white pawn can kill black enemy down-LEFT
                            Abstract_Figure enemy = check_field(activeFigs, x-1, y+1);
                            if( enemy != null )
                            {
                                if( enemy.isBlack )
                                {
                                    feld[x-1][y+1].setGraphic((new ImageView(new Image(new FileInputStream(linuxURL + "circle.png")))));
                                }
                            }
                        }

                        // white pawn can kill black enemy down-RIGHT
                    }
                    else
                    {

                    }
                }
            }
            catch( FileNotFoundException ex )
            {
                System.out.println("FileNotFoundException in move_figure");
            }
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
        for( int y=0; y<8; y++ )
        {
            for(int x=0; x<8; x++ )
            {
                if( check_field(activeFigs, x, y) == null )
                    feld[x][y].setGraphic( null );
            }
        }
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
    public static void setFeld(Label[][] feld) {
        Board.feld = feld;
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
