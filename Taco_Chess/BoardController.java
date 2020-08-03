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
    static private  String COLOR_before, COLOR_after;
    static private  MoveInfo moveInfo;
    static private  Button possibleMoves [];
    static private  Button criticalKINGMove [], criticalMoves[];
    static private  Circle circles[];
    static private  Abstract_Figure activePlayer;
    static private boolean  IS_CHECK;

    public Button[] getCriticalKINGMove() {
        return criticalKINGMove;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    public void init( Board board, View view )
    {
        possibleMoves   = null;
        activePlayer    = null;
        this.board      = board;
        this.view       = view;

        dialog          = new Dialog( this.board, this.view, this );
        moveInfo        = new MoveInfo( this.board, this);

        // to indicate possible moves for a chosen figure
        circles         = new Circle[64];
        for(int i=0; i<64; i++) {
            circles[i] = new Circle(10, Color.DEEPSKYBLUE);
            circles[i].setOpacity(0.3);
            circles[i].setDisable(true);
        }
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
            set_moves( false );

            if( isCheck() )
                CRITICAL_MOVE();

            if( possibleMoves != null )
                view.draw_possible_circles( activePlayer );
        }
    }

    public void CRITICAL_MOVE()
    {
        // LIMIT POSSIBLE MOVES based on critical moves
        System.out.println("HELP I AM BEING MATED");;
        int CNT=0;
        // compare possible moves with critical moves
        Button tmp[] =new Button[64];
        boolean freeForKing=true;

        if( criticalMoves != null && possibleMoves != null )
        {
            for(int i=0; i<possibleMoves.length; i++)
            {
                if( possibleMoves[i] == null )
                    break;

                /*      K I N G     */
                if( activePlayer instanceof King )
                {
                    for (int j = 0; j < criticalMoves.length; j++)
                    {
                        if (criticalMoves[j] == null)
                            break;

                        if ( criticalMoves[j].getId().equals(possibleMoves[i].getId()) )
                            freeForKing = false;
                    }
                    if( freeForKing )
                        tmp[CNT++] = possibleMoves[i];
                    freeForKing=true;
                }

                /*      A L L    O T H E R S        */
                else {  // check if critical moves fit with possible moves
                    for (int j = 0; j < criticalKINGMove.length; j++)
                    {
                        if (criticalKINGMove[j] == null)
                            break;

                        if ( criticalKINGMove[j].getId().equals(possibleMoves[i].getId()))
                            tmp[CNT++] = criticalKINGMove[j]; // VERY LIMITED CRITICAL MOVE
                    }
                }
            }
        }
        possibleMoves = tmp;
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
                    else
                        switch_turn();

                    // if a pawn has crosses enemy lines
                    if( pawn_can_choose_a_queen(activePlayer.getYCoord()) )
                       dialog.spawn_new_figure( activePlayer, btn , activePlayer.isBlack() );

                    view.update( activePlayer, btn, true );
                    activePlayer = board.move_player(activePlayer, btn);
                    criticalMoves = null;

                    if( isCheck() )
                        setIsCheck( false );

                    check_for_mate( activePlayer.isBlack() );// falls der neue Zug den Gegner in Schach setzt
                    if( isCheck() ) // ja hat er
                    {
                        setIsCheck(true);
                        System.out.println("CHECK");
                    }

                    // player took valid moved, reset possible moves
                    activePlayer  = null;
                    possibleMoves = null;

                }
                else  // DIFFERENT FIGURE HAS BEEN CLICKED
                {
                    view.clear_possible_circles();
                    init_moves( btn );
                }
            }
        }
        catch( FileNotFoundException fex )
        { System.out.println("File not found"); }
    }

    // Finds out ALL critical positions ( where the king cant go )
    public void  check_for_mate( boolean isBlack ) throws FileNotFoundException
    {
        Abstract_Figure team[] = board.get_team(isBlack );

        moveInfo.set_Check_For_Mate( true );

        for(int i=0;i<team.length;i++)
        {
            possibleMoves = null;
            activePlayer = team[i];
            set_moves( true );
        }
        if( criticalMoves != null )
            view.draw_critical_moves( activePlayer.isBlack() ) ;

        moveInfo.set_Check_For_Mate(false);
    }

    // for ONE Figure
    private void set_moves( boolean check_for_mate ) throws FileNotFoundException
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

        Button POSS[] = possibleMoves;

        if( check_for_mate )
            add_critical_moves();

        Button CRIT[] = criticalMoves;
        System.out.print("");
    }

    public void add_critical_moves()
    {
        if( possibleMoves == null )
            return;

        for(int i=0;i<possibleMoves.length;i++)
        {
            if( possibleMoves[i] != null )
                add_critical_figure_move(possibleMoves[i] );
            else
                break;
        }
    }
    public void add_critical_figure_move( Button btn )
    {
        if ( criticalMoves == null )
            criticalMoves = new Button[1024];

        for(int i=0; i<criticalMoves.length; i++)
        {
            if( criticalMoves[i] == null  )
            {
                criticalMoves[i] = btn;
                break;
            }
            else if( criticalMoves[i].getId().equals( btn.getId() ))
                break;
        }
    }

    public void add_critical_king_move( Button btn )
    {
        if ( criticalKINGMove == null )
            criticalKINGMove = new Button[64];

        for(int i=0; i<criticalKINGMove.length; i++)
        {
            if( criticalKINGMove[i] == null  )
            {
                criticalKINGMove[i] = btn;
                break;
            }
            else if( criticalKINGMove[i].getId().equals( btn.getId() ))
                break;
        }
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

    // critical moves have already been set,
    // here just checking if taken move is in one of the critical fields
    public boolean is_critical_btn(Button btn)
    {
        if( criticalMoves != null && btn != null)
        {
            for(int i=0;i<criticalMoves.length;i++)
            {
                if(criticalMoves[i] == null)
                    break;
                else
                {
                    if( btn.getId().equals( criticalMoves[i].getId() ) )
                        return true;
                }
            }
        }
        return false;
    }

    // return true if clicked button is a valid move
    private boolean button_is_valid_move( Button btn ) throws FileNotFoundException {
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

    public static void setIsCheck(boolean isCheck) {
        IS_CHECK = isCheck;
    }

    public static boolean isCheck() {
        return IS_CHECK;
    }

    public static Circle[] getCircles( )
    {
        return circles;
    }

    public static Button[] getPossibleMoves()
    {
        return possibleMoves;
    }

    public static Button[] getCriticalMoves() {
        return criticalMoves;
    }


    // makes it look super responsive *________*
    public void buttonEnter(  Button btn )
    {
        COLOR_before = btn.getStyle();
        COLOR_after = "-fx-border-color: deepskyblue;";
        btn.setStyle( COLOR_after );
    }
    public void buttonExit( Button btn )
    {
        if( !btn.getStyle().equals(COLOR_after) )
            btn.setStyle( view.getCOLOR() );

        else
            btn.setStyle( COLOR_before );
    }

    public  void setCOLOR(String BEFORE) {
       this.COLOR_before = BEFORE;
    }

    public static String getCOLOR() {
        return COLOR_before;
    }
}
