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
                    move_player( btn, true);
                    AI_MOVE();
                    //AI_RANDOM( collect_nxt() );
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

    private void move_player( Button btn, boolean mark ) throws FileNotFoundException
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
                    dialog.GAMEOVER( !activePlayer.isBlack() );
                    activePlayer =null;
                    possibleMoves=null;
                    criticalKINGMove=null;
                    return;
                }
            }

            // player took valid moved, reset possible moves
            activePlayer  = null;
            possibleMoves = null;
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

    private AI_MOVES[] collect_nxt() throws FileNotFoundException {
        AI_MOVES ai_moves[] = collect_ai_moves( true );

        if( isCheck() )// check if its check
            ai_moves = CRITICAL_AI_MOVE( ai_moves );    // limit possible ai-moves to protect king

        // limit possible moves to keep protecting the king
        ai_moves = protect_king_ai( ai_moves );
        return ai_moves;
    }
    private void AI_MOVE() throws IOException, InterruptedException
    {
        AI_MOVES ai_moves[] = collect_nxt();
        if( ai_moves != null )
        {
            MINIMAX(ai_moves, 1, true, 0);

            if( MAXMOVE != null ) {
                activePlayer = MAXMOVE.getFig();
                move_player(MAXMOVE.getMove(), false);
                MAXMOVE = MINMOVE = null;
            }
            else {
                AI_RANDOM( collect_nxt() );
                System.out.println("MAYDAY");
            }
        }
        else
            dialog.GAMEOVER( false );

    }
    static AI_MOVES MINMOVE, MAXMOVE;
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
    private int MINIMAX( AI_MOVES moves[], int depth, boolean maximze, int kill ) throws FileNotFoundException
    {
        if( depth == 0 )
            return evaluate( !maximze, kill );

        if( maximze )
        {
            int maxEval = -1;
            int eval =-1;
            for(int i=0;i<moves.length;i++)
            {
                if( moves[i] == null )
                    break;

                Abstract_Figure COPY    = copy_fig( moves[i].getFig() );
                Abstract_Figure keep    = board.get_figure( moves[i].getMove() );

                int KILLL = moves[i].getScore()*10;
                board.move_player(moves[i].getFig(), moves[i].getMove() );
                eval = MINIMAX( collect_nxt(), depth-1, false, KILLL);

                // UNDO PREVIOUS MOVE
                board.remove_player( moves[i].getFig() );
                board.move_player( moves[i].getFig(), COPY.getBtn() );
                board.add_player( keep );

                if( eval > maxEval )
                    MAXMOVE = moves[i];
                maxEval = max( maxEval, eval );
            }
            return maxEval;
        }
        else
        {
            int minEval = 9999;
            int eval = 0;
            for(int i=0;i<moves.length;i++)
            {
                if( moves[i] == null )
                    break;

                Abstract_Figure COPY    = copy_fig( moves[i].getFig() );
                Abstract_Figure keep    = board.get_figure( moves[i].getMove() );

                int KILLL = moves[i].getScore();
                board.move_player(moves[i].getFig(), moves[i].getMove() );
                eval = MINIMAX( collect_nxt(), depth-1, false, KILLL );

                // UNDO PREVIOUS MOVE
                moves[i].setFig(COPY);
                board.move_player( moves[i].getFig(), moves[i].getFig().getBtn() );
                board.add_player( keep );

                if( eval < minEval )
                    MINMOVE = moves[i];
                minEval = min( minEval, eval );
            }
            return minEval;
        }
    }

    private int evaluate( boolean maximize, int kill ) throws FileNotFoundException
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
    private int max( int max, int val )
    {
        if( max > val )
            return max;
        return val;
    }
    private int min( int min, int val)
    {
        if( min < val)
            return min;
        return val;
    }

    private void AI_RANDOM( AI_MOVES ai_moves[] ) throws FileNotFoundException
    {
        AI_MOVES[] tmp = extract_best( ai_moves );
        AI_MOVES move;
        if( tmp[0] == null )
            move = ai_moves[0];
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

    public AI_MOVES[] protect_king_ai(  AI_MOVES ai_moves[] ) throws FileNotFoundException
    {
        if( isCheck() )
            return ai_moves;
        AI_MOVES tmp[]  = new AI_MOVES[1024];
        AI_MOVES copy[] = ai_moves;
        Abstract_Figure activeFIG;
        Button btn_before;
        int CNT =0;
        for( int i=0;i<ai_moves.length; i++ )
        {
            if( copy[i] == null )
                break;

            activeFIG               = copy[i].getFig();
            btn_before              = activeFIG.getBtn();
            Abstract_Figure keep    = board.get_figure( copy[i].getMove() );

            board.move_player( activeFIG, copy[i].getMove() );
            collect_next_moves( false, true, true );

            board.move_player( activeFIG, btn_before );

            board.add_player( keep );
            if( isCheck() ) {
                setIsCheck(false);
                continue;
            }

            tmp[CNT++] = copy[i];
        }
        criticalKINGMove = null;
        setIsCheck( false );
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

    private AI_MOVES[] CRITICAL_AI_MOVE( AI_MOVES ai_moves[])
    {
        int CNT             = 0;
        boolean freeForKing = true;
        AI_MOVES tmp[]      = new AI_MOVES[1024];

        if( nextMoves != null && ai_moves != null )
        {
            for(int i=0; i<ai_moves.length; i++)
            {
                if( ai_moves[i] == null )
                    break;

                /*      K I N G     */
                if( ai_moves[i].getFig() instanceof King )
                {
                    for (int j = 0; j < nextMoves.length; j++)
                    {
                        if (nextMoves[j] == null)
                            break;

                        if ( nextMoves[j].getId().equals(ai_moves[i].getMove().getId() ) )
                            freeForKing = false;
                    }
                    if( freeForKing )
                        tmp[CNT++] = ai_moves[i];
                    freeForKing = true;
                }

                /*      A L L    O T H E R S        */
                else {
                    if( criticalKINGMove == null )
                        break;
                    for (int j = 0; j < criticalKINGMove.length; j++)
                    {
                        if (criticalKINGMove[j] == null)
                            break;

                        if ( criticalKINGMove[j].getId().equals(ai_moves[i].getMove().getId()) )
                            tmp[CNT++] = ai_moves[i]; // VERY LIMITED CRITICAL MOVE
                    }
                }
            }
            System.out.println("");
        }
        return tmp;
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

    private boolean is_checkMate() throws FileNotFoundException {
        Abstract_Figure team[] = board.get_team( !activePlayer.isBlack() );
        Button tmp[]=null;
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

        int a =0;
        for(int i=0;i<team.length;i++)
        {
            possibleMoves   = null;
            activePlayer    = team[i];
            set_moves( false );

            for(int j=0;j<64;j++)
            {
                if( possibleMoves != null ) {
                    if (possibleMoves[j] == null)
                        break;
                    ai_moves[a] = new AI_MOVES();
                    ai_moves[a].setFig(team[i]);
                    ai_moves[a].setMove(possibleMoves[j]);
                    Abstract_Figure enemy = board.get_figure( possibleMoves[j] );
                    ai_moves[a].setScore( set_kill_score( team[i], enemy ) );
                    a++;
                }
                else
                    break;
            }
        }

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