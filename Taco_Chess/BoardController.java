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
    static private  View view;
    static private  Board board;
    static private  Dialog dialog;
    static private  String BEFORE;
    static private  MoveInfo moveInfo;
    static private  Button possibleMoves [];
    static private Circle circles[];
    static private  Abstract_Figure activePlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void init( Board board, View view )
    {
        possibleMoves   = null;
        activePlayer    = null;
        this.board      = board;
        this.view       = view;

        dialog          = new Dialog( this.board, this.view );
        moveInfo        = new MoveInfo( this.board, this);

        circles         = new Circle[64];
        for(int i=0; i<64; i++) {
            circles[i] = new Circle(15, Color.YELLOW);
            circles[i].setOpacity(0.3);
            circles[i].setDisable(true);
        }
    }

    // return true if clicked button is a move
    public boolean button_is_valid_move( Button btn )
    {
        int xB = board.get_xCoord_btn( btn );
        int yB = board.get_yCoord_btn( btn );
        int xF, yF;
        if( possibleMoves != null )
        {
            for( int i=0; i<possibleMoves.length; i++ )
            {
                if( possibleMoves[i] == null )
                    return false;

                xF = board.get_xCoord_btn( possibleMoves[i] );
                yF = board.get_yCoord_btn( possibleMoves[i] );

                if( xF == xB && yF == yB )
                    return true;
            }
        }
        return false;
    }

    public void add_valid_move( Button btn )
    {
        if ( possibleMoves == null )
            possibleMoves = new Button[64];

        for(int i=0; i<64; i++)
        {
            if( possibleMoves[i] == null  )
            {
                possibleMoves[i] = btn;
                break;
            }
        }
    }

    public void set_possible_moves() throws FileNotFoundException
    {
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

        if( possibleMoves != null )
            view.draw_possible_circles(x,y);
    }

    private void init_moves( Button btn ) throws FileNotFoundException
    {
        activePlayer = board.get_figure( btn );
        view.clear_active_fields();
        if (activePlayer != null)
        {
            possibleMoves = null;
            activePlayer.setBtn(btn);
            set_possible_moves();
        }
    }
    public void handleButtonMove( Button btn )
    {
        int x ,y;
        try {
            if( activePlayer == null )
                init_moves( btn );

            else
            {   // MOVE is possible
                x = activePlayer.getXCoord();
                y = activePlayer.getYCoord();

                if ( button_is_valid_move(btn) )
                {   // LETS MOVE

                    Abstract_Figure killed = board.get_figure( btn );
                    if( killed != null )
                        view.add_killed_figure( killed );
                    // if a pawn has crosses enemy lines
                    if( pawn_can_choose_a_queen( y ) )
                       Dialog.spawn_new_figure( activePlayer, btn , activePlayer.isBlack() );

                    // mark players destination field with color
                    x = board.get_xCoord_btn(btn);
                    y = board.get_yCoord_btn(btn);
                    view.set_active_field( 1, x, y );

                    view.update( activePlayer, btn );
                    activePlayer = board.move_player(activePlayer, btn);
                    possibleMoves = null;
                }
                else  // DIFFERENT FIGURE HAS BEEN CLICKED
                {
                    view.clear_possible_circles();
                    init_moves(btn);
                }
            }
        }
        catch( FileNotFoundException fex )
        {
            System.out.println("file not found ");
        }
    }

    public boolean pawn_can_choose_a_queen( int y ) throws FileNotFoundException
    {
        if( activePlayer instanceof Pawn )
        {
            if (y == 6 && activePlayer.isBlack() )
                return true;
            else if( y == 1 && !activePlayer.isBlack() )
                return true;
            else
                return false;
        }

        return false;
    }

    public static Circle[] getCircles( )
    {
        return circles;
    }

    public static Button[] getPossibleMoves()
    {
        return possibleMoves;
    }

    // makes it look just awesome *________*
    public void buttonEnter(  Button btn )
    {
        BEFORE = btn.getStyle();
        btn.setStyle("-fx-border-color: #000000;");
    }
    public void buttonExit( Button btn )
    {
        btn.setStyle( BEFORE );
    }
}
