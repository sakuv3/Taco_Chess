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
    static private  Circle circles[];
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

        // to indicate possible moves for a chosen figure
        circles         = new Circle[64];
        for(int i=0; i<64; i++) {
            circles[i] = new Circle(15, Color.YELLOW);
            circles[i].setOpacity(0.3);
            circles[i].setDisable(true);
        }
    }

    public void handleButtonMove( Button btn )
    {
        try {
            if( activePlayer == null )
                init_moves(btn);

            else
            {   // MOVE is possible
                if (  button_is_valid_move(btn) )
                {
                    if( !turn_is_valid( btn ) )
                        return; // its not your turn mate

                    // if a pawn has crosses enemy lines
                    if( pawn_can_choose_a_queen(activePlayer.getYCoord()) )
                       dialog.spawn_new_figure( activePlayer, btn , activePlayer.isBlack() );

                    switch_turn();
                    view.update( activePlayer, btn, true );
                    activePlayer = board.move_player(activePlayer, btn);

                    //if( is_mate() )
                      //  System.out.println("MATE");
                    activePlayer  = null;
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
            System.out.println("Wo bin ich hier?");
        }
    }

    public boolean is_mate( ) throws FileNotFoundException {
        Button enemyKing = board.get_king_btn( !activePlayer.isBlack() );
        Abstract_Figure team[] = board.get_team( activePlayer.isBlack() );

        for(int i=0;i<team.length;i++)
        {
            activePlayer = team[i];
            set_possible_moves( true );

            if( possibleMoves == null )
                continue;
            for(int j=0;j<possibleMoves.length;j++)
            {
                if( possibleMoves[i] == null )
                    continue;
                if( enemyKing.getId().equals( possibleMoves[i].getId() ))
                    return true;
            }
        }
        return false;
    }

    private void init_moves( Button btn ) throws FileNotFoundException
    {
        view.clear_active_fields();
        activePlayer = board.get_figure( btn );
        
        if (activePlayer != null)
        {
            if( !turn_is_valid( btn ))
                return; // its not your turn mate
            possibleMoves = null;
            set_possible_moves(false );
        }
    }

    private void set_possible_moves( boolean TEST ) throws FileNotFoundException
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

        if( TEST )
            return;
        if( possibleMoves != null )
            view.draw_possible_circles(x,y);
    }

    // return true if clicked button is a valid move
    private boolean button_is_valid_move( Button btn )
    {
        int xBtn = board.get_xCoord_btn( btn );
        int yBtn = board.get_yCoord_btn( btn );
        int xFig, yFig;
        if( possibleMoves != null )
        {
            for( int i=0; i<possibleMoves.length; i++ )
            {
                if( possibleMoves[i] == null )
                    return false;

                xFig = board.get_xCoord_btn( possibleMoves[i] );
                yFig = board.get_yCoord_btn( possibleMoves[i] );

                if( xFig == xBtn && yFig == yBtn )
                    return true;
            }
        }
        return false;
    }

    public void add_limited_move( Button btn )
    {

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

    private void switch_turn()
    {
        if( activePlayer.isBlack() && !board.isWhitesMove() )
            board.setIsWhitesMove( true );
        else
            board.setIsWhitesMove( false );
    }
    private boolean turn_is_valid( Button btn )
    {
        // blacks move
        if( activePlayer.isBlack() && board.isWhitesMove() )
            return false;

        // whites move
        if( !activePlayer.isBlack() && !board.isWhitesMove() )
            return false;

        return true; // turn is valid
    }

    private boolean pawn_can_choose_a_queen( int y ) throws FileNotFoundException
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
    
    // makes it look super responsive *________*
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
