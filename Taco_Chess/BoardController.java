package Taco_Chess;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import Taco_Chess.Figures.*;
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
    static MoveInfo moveInfo;
    static Abstract_Figure enemy;
    static Abstract_Figure activePlayer;
    static Button possibleMovesBlack[];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void init( Board board )
    {
        possibleMovesBlack  = null;
        activePlayer        = null;
        this.board          = board;
        moveInfo                = new MoveInfo( this.board, this);
    }

    // return true if clicked button is a move
    public boolean buttonIsMove( Button btn )
    {
        int xB = (int)(btn.getLayoutX() /75);
        int yB = (int)(btn.getLayoutY() /75);
        int xF, yF;
        if( possibleMovesBlack != null )
        {
            for( int i=0; i<possibleMovesBlack.length; i++ )
            {
                if( possibleMovesBlack[i] == null )
                    return false;

                xF = (int)(possibleMovesBlack[i].getLayoutX() /75);
                yF = (int)(possibleMovesBlack[i].getLayoutY() /75);

                if( xF == xB && yF == yB )
                    return true;
            }
        }
        return false;
    }

    public void clear_possible_moves()
    {
        if( possibleMovesBlack != null )
        {
            for(int i=0; i<64; i++)
            {
                if( possibleMovesBlack[i] != null )
                {
                    possibleMovesBlack[i].setStyle("-fx-border-color: #A39300; ");
                    possibleMovesBlack[i] = null;
                }
                else
                    break;
            }
        }
        possibleMovesBlack = null;
    }
    public void display_possible_moves() throws FileNotFoundException {
        if( possibleMovesBlack != null )
        {
            for(int i=0;i<64; i++)
            {
                if( possibleMovesBlack[i] != null )
                    possibleMovesBlack[i].setStyle(" -fx-border-color: #fee11a; ");
                   // possibleMovesBlack[i].setGraphic((new ImageView(new Image(new FileInputStream(linuxURL + "circle.png")))));
                else
                    break;
            }
        }
    }

    public void add_valid_move( Button btn )
    {
        if ( possibleMovesBlack == null )
            possibleMovesBlack = new Button[64];

        for(int i=0; i<64; i++)
        {
            if( possibleMovesBlack[i] == null ) {
                possibleMovesBlack[i] = btn;
                break;
            }
        }
    }

    public void set_possible_moves() throws FileNotFoundException
    {
        enemy = null;
        int x = activePlayer.getXCoord();
        int y = activePlayer.getYCoord();
        boolean isBlack = activePlayer.isBlack();

        if( activePlayer instanceof Pawn )
            moveInfo.pawn(x, y, isBlack );
        else if( activePlayer instanceof Horse )
            moveInfo.horse(x, y, isBlack );
        else if( activePlayer instanceof Rook )
            moveInfo.rook(x, y, isBlack );
        else if( activePlayer instanceof Bishop )
            moveInfo.bishop(x, y, isBlack );
        else if( activePlayer instanceof  King )
            moveInfo.king(x, y, isBlack);
        else if( activePlayer instanceof Queen )
            moveInfo.queen(x, y, isBlack);

        display_possible_moves();
    }

    public void handleButtonMove( Button btn ) {
        int x =  (int)btn.getLayoutX() /75;
        int y =  (int)btn.getLayoutY() /75;

        try {
            if( activePlayer == null ) {
                // a player has to be clicked first
                activePlayer = board.get_figure(x, y);

                if (activePlayer != null)
                        set_possible_moves();
            }

            // MOVE is possible
            else if (activePlayer != null)
            {
                if ( buttonIsMove(btn) )
                {
                    clear_possible_moves();
                    board.move_player(activePlayer, btn);
                }
                else
                    clear_possible_moves();

                activePlayer = null;
            }
        }
        catch( FileNotFoundException fex )
        {
            System.out.println("fnfe");
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
        if( buttonIsMove(btn) )
            btn.setStyle("-fx-border-color: #fee11a; -fx-background-size: 45,45;");
        else
            btn.setStyle("-fx-border-color: #A39300; -fx-background-size: 45,45;");
    }
}
