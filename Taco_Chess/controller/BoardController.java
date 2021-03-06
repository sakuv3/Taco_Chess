package Taco_Chess.controller;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import Taco_Chess.view.Dialog;
import Taco_Chess.Figures.*;
import java.util.ResourceBundle;

import Taco_Chess.model.Board;
import Taco_Chess.view.View;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class BoardController implements Initializable
{
    static int CNT =0;
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
        try
        {
            if (activePlayer == null)
                init_moves(btn);

            else
            {   // selected move is possible
                if (button_is_valid_move(btn))
                {
                    if (!turn_is_valid(btn))
                        return; // its not your turn mate
                    if( move_player( btn, true) == false )
                        AI_MOVE();  //AI_RANDOM( collect_nxt() );
                }
                else  // DIFFERENT FIGURE ON THE SAME TEAM HAS BEEN CLICKED
                {
                    view.clear_possible_circles();
                    init_moves( btn );
                }
            }
        }
        catch(FileNotFoundException d)
        { System.out.println("File not found"); }
        catch (IOException e)
        { System.out.println("IOException"); }
        catch (InterruptedException e) { System.out.println("Interrupted Exception"); }
    }

    // returns false upon checkmate-situation otherwise true
    private boolean move_player( Button btn, boolean mark ) throws FileNotFoundException
    {

            // if a pawn has crossed enemy lines
            if( pawn_can_choose_a_queen(activePlayer.getYCoord()) )
                dialog.spawn_new_figure( activePlayer, btn , activePlayer.isBlack() );

            view.update( activePlayer, btn, mark );
            activePlayer = board.move_player(activePlayer, btn);
            nextMoves = null;

            if( isCheck() )
            {
                setIsCheck(false);
                criticalKINGMove =null;
            }

            collect_next_moves( activePlayer.isBlack(), false, true );// falls der neue Zug den Gegner in Schach setzt
            if( isCheck() ) // ja hat er
            {
                setIsCheck(true);
                if( is_checkMate() ) {
                    System.out.println("CHECKMATE");
                    dialog.GAMEOVER( !activePlayer.isBlack(), true );
                    activePlayer =null;
                    possibleMoves=null;
                    criticalKINGMove=null;
                    return true;
                }
            }

            // player took valid moved, reset possible moves
            activePlayer  = null;
            possibleMoves = null;
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

            // checks possible moves for a chosen figure
            possibleMoves = null;
            set_moves( false ); // collect possible moves for chosen figure

            if( isCheck() )// check if its check-mate
                CRITICAL_MOVE();    // limit possible moves to protect king

            // limit possible moves to keep protecting the king
            protect_king( btn );

            if( possibleMoves != null )
                view.draw_possible_circles( activePlayer );
        }
    }

    private void AI_MOVE() throws IOException, InterruptedException
    {
        AI_MOVES ai_moves[] = collect_ai_moves( true );
        if( ai_moves != null )
        {
            //AI_RANDOM( ai_moves );

            AI_MOVES bestMove = MINIMAX(null, 3, true );
            System.out.println("minimax-calls: "+CNT);
            if( bestMOVE != null ) {
                activePlayer = bestMOVE.getFig();
                move_player(bestMOVE.getMove(), false);
                bestMOVE =null;
            }
            else
                AI_RANDOM( ai_moves );

        }
        else
            if( isCheck() )
                dialog.GAMEOVER( false, true  );
            else
                dialog.GAMEOVER( false, false  );

        CNT=0;
    }
    static AI_MOVES bestMOVE;
    private AI_MOVES MINIMAX( AI_MOVES move, int depth, boolean maximze ) throws FileNotFoundException
    {
        CNT++;
        if( depth == 0 )
            return evaluate( move );

        if( maximze )
        {   // AI PLAYER
            AI_MOVES maxMove = null;
            AI_MOVES eval =null;
            AI_MOVES moves[] = collect_ai_moves( true );

            if( moves == null )
                return null;
            for(int i=0;i<moves.length;i++)
            {
                if( moves[i] == null )
                    break;

                Abstract_Figure figCOPY = copy_fig( moves[i].getFig() );
                Abstract_Figure keep    = board.get_figure( moves[i].getMove() );
                AI_MOVES moveCPY        = new AI_MOVES( moves[i], figCOPY );

                board.move_player(moves[i].getFig(), moves[i].getMove() );
                setIsCheck(false);
                eval = MINIMAX( moveCPY, depth-1, false);

                // UNDO PREVIOUS MOVE
                board.remove_player( moves[i].getFig() );
                board.move_player( moves[i].getFig(), figCOPY.getBtn() );
                board.add_player( keep );

                maxMove = max( maxMove, eval );
            }
            return maxMove;
        }
        else
        {   // HUMAN PLAYER
            AI_MOVES minimalMove = null;
            AI_MOVES eval =null;
            AI_MOVES moves[] = collect_ai_moves( false );
            for(int i=0;i<moves.length;i++)
            {
                if( moves[i] == null )
                    break;

                Abstract_Figure figCOPY = copy_fig( moves[i].getFig() );
                Abstract_Figure keep    = board.get_figure( moves[i].getMove() );
                AI_MOVES moveCPY        = new AI_MOVES( moves[i], figCOPY );

                board.move_player(moves[i].getFig(), moves[i].getMove() );
                setIsCheck(false);
                eval = MINIMAX( moveCPY, depth-1, true);

                // UNDO PREVIOUS MOVE
                board.remove_player( moves[i].getFig() );
                board.move_player( moves[i].getFig(), figCOPY.getBtn() );
                board.add_player( keep );

                minimalMove = min( minimalMove, eval );
            }
            return minimalMove;
        }
    }

    private AI_MOVES evaluate( AI_MOVES move )
    {
        Abstract_Figure black[] = board.get_team(true);
        Abstract_Figure white[] = board.get_team(false);
        int score=0, score2=0;
        for(int i=0;i<black.length;i++)
            score += view.get_credits( black[i] )*100;
        for(int i=0;i<white.length;i++)
            score2 += view.get_credits( white[i] )*100;

        move.setScore(score-score2);
        return move;
    }
    private int evaluate_moves( boolean maximize, int kill ) throws FileNotFoundException
    {
        int score =kill;
        AI_MOVES moves[] = collect_ai_moves( maximize );
        moves = extract_best( moves );
        if( moves == null )
            return 0;
        for(int i=0;i<moves.length;i++)
        {
            if( moves[i] == null )
                break;
            score += moves[i].getScore();
        }

        int score2 =0;
        AI_MOVES opposite[] = collect_ai_moves( !maximize );
        opposite = extract_best( opposite );
        if( opposite == null )
            return 0;
        for(int i=0;i<opposite.length;i++)
        {
            if( opposite[i] == null )
                break;
            score2 += opposite[i].getScore();
        }

        return score -score2;
    }
    private AI_MOVES max( AI_MOVES max, AI_MOVES val )
    {
        if( max == null )
            return val;
        if( val ==null )
            return max;
        if( max.getScore() > val.getScore() )
            return max;
        return val;
    }
    private AI_MOVES min( AI_MOVES min, AI_MOVES val)
    {
        if( min == null )
            return val;
        if( val == null )
            return min;
        if( min.getScore() <= val.getScore() )
            return min;
        return val;
    }

    private int rand( int min, int max )
    {
        double rand = Math.random();
        return (int) (rand * ((max-min))) +min;
    }
    private void AI_RANDOM( AI_MOVES ai_moves[] ) throws FileNotFoundException
    {
        AI_MOVES[] tmp = extract_best( ai_moves );
        AI_MOVES move;
        if( tmp[0] == null ) {
            int cnt =0;
            for(int i=0;i<1024;i++)
            {
                if( ai_moves[i] == null )
                    break;
                cnt++;
            }
            int rand = rand(0,cnt);
            move = ai_moves[ rand ];
        }
        else {
            int best = 0;
            for(int i=0;i<tmp.length;i++)
            {
                if( tmp[i] == null)
                    break;
                if( best < tmp[i].getScore() )
                    best = i;
            }
            move = tmp[best];
        }

        if( move == null )
            return;
        activePlayer = move.getFig();
        Button btn = move.getMove();

        move_player( btn, false );
    }
    private AI_MOVES[] extract_best( AI_MOVES moves[] )
    {
        AI_MOVES tmp[] = new AI_MOVES[1024];
        int cnt =0;
        for(int i=0;i<moves.length;i++)
        {
            if( moves[i] == null )
                break;
            if( moves[i].getScore() > 0 )
                tmp[cnt++] = moves[i];
        }
        return tmp;
    }
    public void protect_king( Button btn ) throws FileNotFoundException
    {
        if( isCheck() )
            return;

        Abstract_Figure activeFIG   = activePlayer;
        Button tmp[]                = new Button[64];
        Button copy []              = possibleMoves;
        Button copyNXT[]            = nextMoves;

        int CNT =0;
        if( possibleMoves != null )
        {
            for (int i = 0; i < copy.length; i++)
            {
                if( copy[i] == null )
                    break;
                possibleMoves = copy;

                nextMoves =null;
                Abstract_Figure KEEP = board.get_figure( copy[i] );

                board.move_player( activePlayer, copy[i] );
                collect_next_moves( !activePlayer.isBlack(), true, true );   // then collect all moves to see if this opens up a deadly check

                activePlayer = board.move_player( activeFIG, btn );
                board.add_player( KEEP );

                if( isCheck() ) {
                    setIsCheck(false);
                    continue;
                }

                tmp[CNT++] = copy[i];
            }
        }
        possibleMoves   = tmp;
        nextMoves       = copyNXT;
        criticalKINGMove= null;
        setIsCheck( false );
    }

    private boolean is_checkMate() throws FileNotFoundException {
        Abstract_Figure team[] = board.get_team( !activePlayer.isBlack() );
        Button tmp[]=null;
        possibleMoves = null;
        for(int i=0;i<team.length;i++)
        {
            activePlayer = team[i];
            set_moves(false);
            CRITICAL_MOVE();

            for(int q=0;q<possibleMoves.length;q++)
            {
                if(possibleMoves[q] ==null)
                    break;
                protect_king(possibleMoves[q]);
            }
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
                    boolean redundant = false;
                    for(int k=0;k<possibleMoves.length;k++)
                    {
                        if( possibleMoves[k] != null ) {
                            if (tmp[j].getId().equals(possibleMoves[k].getId())) {
                                redundant = true;
                                break;
                            }
                        }
                        else
                            break;
                    }
                    if( redundant )
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
    public void CRITICAL_MOVE( )
    {
        // LIMIT POSSIBLE MOVES based on critical moves
        // Thats either for ALL figures except King OR
        // just for the king, because he cant walk in a critical field
        int CNT      = 0;
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
                    if( criticalKINGMove != null )
                    {
                        for (int j = 0; j < criticalKINGMove.length; j++) {
                            if (criticalKINGMove[j] == null)
                                break;

                            if (criticalKINGMove[j].getId().equals(possibleMoves[i].getId()))
                                tmp[CNT++] = criticalKINGMove[j]; // VERY LIMITED CRITICAL MOVE
                        }
                    }
                    else
                        tmp[CNT++] = possibleMoves[i];
                }
            }
        }
        possibleMoves = tmp;
    }
    // Finds out ALL critical positions ( where the king cant go )
    public Button[]  collect_next_moves( boolean isBlack, boolean checkForMate, boolean collect ) throws FileNotFoundException
    {
        Abstract_Figure team[] = board.get_team(isBlack );

        moveInfo.set_COLLECTING_NEXT_MOVES( collect );

        for(int i=0;i<team.length;i++)
        {
            possibleMoves = null;
            activePlayer = team[i];
            set_moves( true );
        }
        if( nextMoves != null && ! checkForMate && WALLHACK_MODE )
            view.draw_next_moves( activePlayer.isBlack() ) ;

        moveInfo.set_COLLECTING_NEXT_MOVES( false );
        return nextMoves;
    }

    public AI_MOVES[] collect_ai_moves( boolean isBlack ) throws FileNotFoundException
    {
        AI_MOVES ai_moves[]     = new AI_MOVES[1024];
        Abstract_Figure team[]  = board.get_team( isBlack );

        nextMoves =null;
        nextMoves = collect_next_moves( !isBlack, true, true);
        int a =0;
        boolean nullMoves = true;
        for(int i=0;i<team.length;i++)
        {
            possibleMoves   = null;
            activePlayer    = team[i];
            set_moves( false );

            if( isCheck() )// check if its check-mate
                CRITICAL_MOVE();    // limit possible moves to protect king

            if( possibleMoves == null )
                continue;

            nullMoves = false;
            for(int j=0;j<64;j++)
            {
                    if (possibleMoves[j] == null)
                        break;
                    ai_moves[a] = new AI_MOVES();
                    ai_moves[a].setFig(team[i]);
                    ai_moves[a].setMove(possibleMoves[j]);
                    Abstract_Figure enemy = board.get_figure( possibleMoves[j] );
                    ai_moves[a].setScore( set_kill_score( team[i], enemy ) );
                    a++;
            }
        }
        if( nullMoves == true )
            return null;
        return ai_moves;
    }

    private int set_kill_score( Abstract_Figure killer, Abstract_Figure target )
    {
        if( target == null )
            return 0;

        int KILLER  = board.get_type( killer ) +1;
        int TARGET  = ( board.get_type( target ) +1 ) *10;

        return TARGET - KILLER;
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

    public Abstract_Figure copy_fig( Abstract_Figure x )
    {
        if( x instanceof  Pawn )
            return new Pawn( (Pawn)x );
        if( x instanceof  Bishop )
            return new Bishop( (Bishop)x );
        if( x instanceof  Horse )
            return new Horse( (Horse)x );
        if( x instanceof  Rook )
            return new Rook( (Rook)x );
        if( x instanceof  Queen )
            return new Queen( (Queen)x );
        if( x instanceof  King )
            return new King( (King)x );
        return null;    // never reached
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

    public Button[] getPossibleMoves()
    {
        return possibleMoves;
    }

    // makes it look super responsive *________*
    public void buttonEnter(  Button btn )
    {
        view.setHOVER( btn );
        view.setColor_before( btn.getStyle() );
        btn.setStyle( "-fx-border-color: deepskyblue;" );
    }
    public void buttonExit( Button btn )
    {
        btn.setStyle( view.getColor_before() );
    }

    public  void setCOLOR(String BEFORE) {
        this.COLOR_before = BEFORE;
    }

    public static Button[] getCriticalKINGMove() {
        return criticalKINGMove;
    }

    public static void setNextMoves(Button[] nextMoves) {
        BoardController.nextMoves = nextMoves;
    }

    public static void setPossibleMoves(Button[] possibleMoves) {
        BoardController.possibleMoves = possibleMoves;
    }

    public static void setCriticalKINGMove(Button[] criticalKINGMove) {
        BoardController.criticalKINGMove = criticalKINGMove;
    }

    public static void setWallhackMode(boolean wallhackMode) {
        WALLHACK_MODE = wallhackMode;
    }
}