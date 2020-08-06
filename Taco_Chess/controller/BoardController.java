package Taco_Chess.controller;
import java.io.FileNotFoundException;
import java.net.URL;

import Taco_Chess.view.Dialog;
import Taco_Chess.Figures.*;
import java.util.ResourceBundle;

import Taco_Chess.model.Board;
import Taco_Chess.view.View;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BoardController implements Initializable
{
    static private View view;
    static private Board board;
    static private Dialog dialog;
    static private  String COLOR_before, COLOR_after;
    static private  MoveInfo moveInfo;
    static private  Button possibleMoves [];
    static private  Button criticalKINGMove [], nextMoves[];
    static private  Circle circles[];
    static private  Abstract_Figure activePlayer;
    static private boolean  IS_CHECK;
    static private boolean WALLHACK_MODE =false;

    public Button[] getCriticalKINGMove() {
        return criticalKINGMove;
    }
    public Button[] get_next_moves()
    {
        return nextMoves;
    }
    public Button[] getPossibleMoves()
    {
        return possibleMoves;
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

    public void handleButtonMove( Button btn )
    {
        try {
            if( activePlayer == null )
                init_moves( btn );

            else
            {   // selected move is possible
                if (  button_is_valid_move(btn) )
                {
                    if( !turn_is_valid( btn ) )
                        return; // its not your turn mate
                    else
                        switch_turn();

                    // if a pawn has crossed enemy lines
                    if( pawn_can_choose_a_queen(activePlayer.getYCoord()) )
                        dialog.spawn_new_figure( activePlayer, btn , activePlayer.isBlack() );

                    view.update( activePlayer, btn, true );
                    activePlayer = board.move_player(activePlayer, btn);
                    nextMoves = null;

                    if( isCheck() )
                    {
                        setIsCheck(false);
                        criticalKINGMove =null;
                    }

                    boolean isBlack = activePlayer.isBlack();
                    collect_next_moves( isBlack, false );// falls der neue Zug den Gegner in Schach setzt
                    if( isCheck() ) // ja hat er
                    {
                        setIsCheck(true);
                        if( is_checkMate() ) {
                            System.out.println("CHECKMATE");
                            activePlayer =null;
                            possibleMoves=null;
                            criticalKINGMove=null;
                            setIsCheck(false);
                            dialog.GAMEOVER( isBlack);
                            return;
                        }
                    }

                    // player took valid moved, reset possible moves
                    activePlayer  = null;
                    possibleMoves = null;

                }
                else  // DIFFERENT FIGURE ON THE SAME TEAM HAS BEEN CLICKED
                {
                    view.clear_possible_circles();
                    possibleMoves=null;
                    init_moves( btn );
                }
            }
        }
        catch( FileNotFoundException fex )
        { System.out.println("File not found"); }
    }

    private void init_moves( Button btn ) throws FileNotFoundException
    {
        view.clear_active_fields();
        activePlayer = board.get_figure( btn );

        if (activePlayer != null)
        {
            if( !turn_is_valid( btn ))
                return; // its not your turn mate

            // checks possible moves for a chosen figure
            possibleMoves = null;
            set_moves( false );

            if( isCheck() )// check if its check-mate
                CRITICAL_MOVE();

            if( possibleMoves != null )
                view.draw_possible_circles( activePlayer );
        }
    }

    public void CRITICAL_MOVE( )
    {
        // LIMIT POSSIBLE MOVES based on critical moves
        // Thats either for ALL figures except King OR
        // just for the king, because he cant walk in a critical field
        //System.out.println("I AM BEING MATED");;
        int CNT=0;

        // compare possible moves with critical moves
        Button tmp[] = new Button[64];
        boolean freeForKing=true;

        if( nextMoves != null && possibleMoves != null )
        {
            for(int i=0; i<possibleMoves.length; i++)
            {
                if( possibleMoves[i] == null )
                    break;

                /*      K I N G     */
                if( activePlayer instanceof King )
                {
                    for (int j = 0; j < nextMoves.length; j++)
                    {
                        if (nextMoves[j] == null)
                            break;

                        if ( nextMoves[j].getId().equals(possibleMoves[i].getId()) )
                            freeForKing = false;
                    }
                    if( freeForKing )
                        tmp[CNT++] = possibleMoves[i];
                    freeForKing=true;
                }

                /*      A L L    O T H E R S        */
                else {
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

    private boolean is_checkMate() throws FileNotFoundException {
        Abstract_Figure team[] = board.get_team( !activePlayer.isBlack() );
        Button tmp[]=null;
        int CNT = 0;
        possibleMoves = null;
        for(int i=0;i<team.length;i++)
        {
            activePlayer = team[i];
            set_moves(false);
            CRITICAL_MOVE();

            // possible moves needs to be added in tmp
            for(int j=0;j<64;j++)
            {
                if( tmp == null)
                    tmp = new Button[64];
                if( tmp[j] == null )
                {
                    for (int k = 0; k < possibleMoves.length; k++)
                    {
                        if (possibleMoves[k] != null)
                            tmp[j++] = possibleMoves[k];
                        else
                            break;
                    }
                    break;
                }
                else if( tmp[j] != null )
                {
                    boolean isClone = false;
                    for(int k=0;k<possibleMoves.length;k++)
                    {
                        if( possibleMoves[k] != null ) {
                            if (tmp[j].getId().equals(possibleMoves[k].getId())) {

                                isClone = true;
                                break;
                            }
                        }
                        else
                            break;
                    }
                    if( isClone )
                        break;
                }

            }
        }

        // tmp now has all limited critical moves or null, if checkmate
        if ( tmp[0] == null )
            return true;
        else
            return false;
    }
    // Finds out ALL critical positions ( where the king cant go )
    public void  collect_next_moves( boolean isBlack, boolean checkForMate ) throws FileNotFoundException
    {
        Abstract_Figure team[] = board.get_team(isBlack );

        moveInfo.set_COLLECTING_NEXT_MOVES( true );

        for(int i=0;i<team.length;i++)
        {
            possibleMoves = null;
            activePlayer = team[i];
            set_moves( true );
        }
        if( nextMoves != null && ! checkForMate && WALLHACK_MODE )
           view.draw_next_moves( activePlayer.isBlack() ) ;

        moveInfo.set_COLLECTING_NEXT_MOVES(false);
    }

    // for ONE Figure
    private void set_moves( boolean collecting_next ) throws FileNotFoundException
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

        if( collecting_next )
            add_next_moves();

    }

    // critical moves have already been set,
    // here just checking if taken move is in one of the critical fields
    public boolean is_critical_move( Button btn )
    {
        if( nextMoves != null && btn != null)
        {
            for(int i=0;i<nextMoves.length;i++)
            {
                if(nextMoves[i] == null)
                    break;
                if( btn.getId().equals( nextMoves[i].getId() ) )
                    return true;
            }
        }
        return false;
    }

    public void add_possible_move( Button btn )
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

    public void add_next_moves()
    {
        if( possibleMoves == null )
            return;

        for(int i=0;i<possibleMoves.length;i++)
        {
            if( possibleMoves[i] != null )
                add_next_move( possibleMoves[i] );
            else
                break;
        }
    }
    public void add_next_move( Button btn )
    {
        if ( nextMoves == null )
            nextMoves = new Button[1024];

        for(int i=0; i<nextMoves.length; i++)
        {
            if( nextMoves[i] == null  )
            {
                nextMoves[i] = btn;
                break;
            }
            else if( nextMoves[i].getId().equals( btn.getId() ))
                break;
        }
    }

    public void add_critical_move( Button btn )
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

    public static Button[] getNextMoves() {
        return nextMoves;
    }


    // makes it look super responsive *________*
    public void buttonEnter(  Button btn )
    {
        COLOR_before = btn.getStyle();
        btn.setStyle( "-fx-border-color: deepskyblue;" );
    }
    public void buttonExit( Button btn )
    {
            btn.setStyle( COLOR_before );
    }

    public  void setCOLOR(String BEFORE) {
       this.COLOR_before = BEFORE;
    }

    public static String getCOLOR() {
        return COLOR_before;
    }

    public static void setWallhackMode(boolean wallhackMode) {
        WALLHACK_MODE = wallhackMode;
    }
}
