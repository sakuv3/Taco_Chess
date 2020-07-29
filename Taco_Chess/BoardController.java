package Taco_Chess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import Taco_Chess.Figures.Abstract_Figure;
import Taco_Chess.Figures.Pawn;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class BoardController implements Initializable
{
    static final String linuxURL = "/home/saku/IdeaProjects/Taco/src/Taco_Chess/images/";
    static Board board;
    Button grid[][];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void init( Board board, Button grid[][])
    {
        this.board = board;
        this.grid = grid;
    }
    @FXML public void handleButtonMove( MouseEvent event )
    {
        Button btn  = (Button)event.getSource();
        int x       =  (int)btn.getLayoutX() /75;
        int y       =  (int)btn.getLayoutY() /75;

        Abstract_Figure [] figures = board.getAllFigures();
        Abstract_Figure clickedFigure = board.check_field( figures, x, y );

        //board.clear_possibilities( figures );

        // check if a figure has been clicked
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
                        if( board.check_field( figures, x, y+1) == null ) {
                            feld[x][y+1].setGraphic((new ImageView(new Image(new FileInputStream(linuxURL + "circle.png")))));



                            feld[x][y+1].setOnMouseClicked( e ->
                            {
                                board.clear_possibilities ( figures );
                                board.removeFigure( clickedFigure );

                                board.setFigure( clickedFigure, x, y+1, false );
                                try
                                {
                                    board.drawFigures();
                                } catch (FileNotFoundException ex) {
                                    ex.printStackTrace();
                                }

                            });
                        }
                        if( y == 1 )
                        {   // white pawn is in start-position -> can move 2 field straight
                            if (board.check_field(figures, x, y+2) == null)
                                feld[x][y+2].setGraphic((new ImageView(new Image(new FileInputStream(linuxURL + "circle.png")))));
                        }

                        if( x > 0 && x < 8 )
                        {   // white pawn can kill black enemy down-LEFT
                            Abstract_Figure enemy = board.check_field(figures, x-1, y+1);
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
    // makes it look just awesome *________*
    @FXML private void buttonHovered( MouseEvent event )
    {
        ((Button)event.getSource()).setStyle("-fx-border-color: #FF33CC; -fx-background-size: 52,52;");
    }
    @FXML private void buttonUnhovered( MouseEvent event )
    {
        ((Button)event.getSource()).setStyle("-fx-border-color: #A39300; -fx-background-size: 45,45;");
    }
}
