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
    Button field[][];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void init( Board board, Button fields[][] )
    {
        this.board = board;
        this.field = board.getFields();
    }

    public void handleButtonMove( Button btn )
    {
        int x =  (int)btn.getLayoutX() /75;
        int y =  (int)btn.getLayoutY() /75;

        Abstract_Figure clickedFigure = board.check_figure( x, y );

        if(clickedFigure != null )
        {
            try {
                if( clickedFigure.isBlack() )
                    board.show_possible_moves_black(clickedFigure, x, y);
                else
                    board.show_possible_moves_white( clickedFigure, x, y);
            }
            catch( FileNotFoundException fex )
            {
                System.out.println("file not found in handleBtnMove");
            }
        }
        else
        {
            System.out.println("NULL");
        }


    }
    // makes it look just awesome *________*
    public void buttonEnter(  Button btn )
    {
       // btn.setStyle();
        btn.setStyle("-fx-border-color: #FF33CC; -fx-background-size: 52,52;");
    }
    public void buttonExit( Button btn )
    {
        btn.setStyle("-fx-border-color: #A39300; -fx-background-size: 45,45;");
    }
}
