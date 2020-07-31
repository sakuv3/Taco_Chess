package Taco_Chess;
import java.io.FileNotFoundException;
import java.net.URL;
import Taco_Chess.Figures.*;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BoardController implements Initializable
{
    static final String linuxURL = "/home/saku/IdeaProjects/Taco/src/Taco_Chess/images/";
    static Board board;
    static GridPane grid;
    static Dialog dialog;
    static Circle circles[];
    static Rectangle rect[];
    static MoveInfo moveInfo;
    static Abstract_Figure enemy;
    static Button possibleMoves [];
    static Abstract_Figure activePlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void init( Board board, GridPane grid )
    {
        possibleMoves   = null;
        activePlayer    = null;
        this.board      = board;
        this.grid       = grid;
        dialog          = new Dialog( this.board );
        moveInfo        = new MoveInfo( this.board, this);
    }

    // return true if clicked button is a move
    public boolean button_is_valid_move( Button btn )
    {
        int xB = (int)(btn.getLayoutX() /100);
        int yB = (int)(btn.getLayoutY() /100);
        int xF, yF;
        if( possibleMoves != null )
        {
            for( int i=0; i<possibleMoves.length; i++ )
            {
                if( possibleMoves[i] == null )
                    return false;

                xF = (int)(possibleMoves[i].getLayoutX() /100);
                yF = (int)(possibleMoves[i].getLayoutY() /100);

                if( xF == xB && yF == yB )
                    return true;
            }
        }
        return false;
    }

    public void clear_active_fields()
    {
        if( rect != null )
        {
            grid.getChildren().remove( rect[0]);
            grid.getChildren().remove( rect[1]);
        }
        rect = null;
    }
    public void clear_possible_moves()
    {
        if( possibleMoves != null )
        {
            for(int i=0; i<64; i++)
            {
                if( possibleMoves[i] != null )
                {
                    grid.getChildren().remove(circles[i]);
                    //possibleMoves[i].setStyle("-fx-border-color: #A39300; ");
                    possibleMoves[i] = null;
                }
                else
                    break;
            }
        }
        circles = null;
        possibleMoves = null;
    }
    public void display_possible_moves() throws FileNotFoundException {
        int x,y;

        // mark active player with colored field
        rect = new Rectangle[2];
        rect[0] = new Rectangle(100, 100);
        rect[0].setOpacity(0.2);
        rect[0].setFill( Color.SKYBLUE);
        rect[0].setDisable(true);
        grid.add(rect[0], activePlayer.getXCoord(), activePlayer.getYCoord());

        if( possibleMoves != null )
        {
            for(int i=0;i<64; i++)
            {
                if( possibleMoves[i] != null && circles [i] != null )
                {
                    x = (int)(possibleMoves[i].getLayoutX() /100);
                    y = (int)(possibleMoves[i].getLayoutY() /100);
                    grid.add( circles[i],x,y);
                }
                else
                    break;
            }
        }
    }

    public void add_valid_move( Button btn )
    {
        if ( possibleMoves == null ) {
            possibleMoves = new Button[64];
            circles       = new Circle[64];
        }

        for(int i=0; i<64; i++)
        {
            if( possibleMoves[i] == null && circles[i] == null)
            {
                circles[i] = new Circle(15, Color.GOLDENROD );
                circles[i].setOpacity( 0.3);
                circles[i].setDisable(true);
                possibleMoves[i] = btn;
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

    public void handleButtonMove(Button btn ) {
        int x =  (int)btn.getLayoutX() /100;
        int y =  (int)btn.getLayoutY() /100;

        try {
            if( activePlayer == null ) {
                // a player has to be clicked first
                activePlayer = board.get_figure(x, y);

                if (activePlayer != null)
                {
                    clear_active_fields();
                    clear_possible_moves();
                    activePlayer.setBtn(btn);
                    set_possible_moves();
                }
            }

            // MOVE is possible
            else if (activePlayer != null)
            {
                if ( button_is_valid_move(btn) )
                {   // LETS MOVE
                    // CHECK IF PAWN HAS ARRIVED AT THE OTHER SIDE TO FETCH A NEW FIGURE
                    if( y == 7 && activePlayer.isBlack() && activePlayer instanceof Pawn )
                        Dialog.spawn_new_figure( activePlayer, btn, true );
                    else if( y == 0 && !activePlayer.isBlack() && activePlayer instanceof Pawn )
                        Dialog.spawn_new_figure( activePlayer, btn, false );

                    // mark active player with colored field
                    rect[1] = new Rectangle(100, 100);
                    rect[1].setOpacity(0.2);
                    rect[1].setFill( Color.LIGHTSKYBLUE);
                    rect[1].setDisable(true);
                    grid.add(rect[1], x, y);

                    board.move_player(activePlayer, btn);
                    clear_possible_moves();
                    activePlayer = null;
                }
                else {
                    // SHOW NEW MOVES
                    activePlayer = board.get_figure(x, y);

                    if (activePlayer != null)
                    {
                        clear_active_fields();
                        clear_possible_moves();
                        activePlayer.setBtn(btn);
                        set_possible_moves();
                    }
                }
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
        btn.setStyle("-fx-border-color: #A39300; -fx-background-size: 45,45;");
    }
}
