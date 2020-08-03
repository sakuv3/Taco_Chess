package Taco_Chess;

import Taco_Chess.Figures.Abstract_Figure;
import Taco_Chess.Figures.King;
import javafx.scene.control.Button;

import java.io.FileNotFoundException;

public class MoveInfo
{
    static Button btn;
    static Board board;
    static Abstract_Figure enemy;
    static BoardController controller;
    static private boolean CHECK_FOR_MATE, IS_CHECK;

    public MoveInfo(){};
    public MoveInfo(Board board, BoardController controller )
    {
        btn                 = null;
        enemy               = null;
        this.board          = board;
        this.controller     = controller;
    }

    public static void set_Check_For_Mate(boolean checkForMate) {
        CHECK_FOR_MATE = checkForMate;
    }

    // returns true if move is valid ( for rook and bishop  only )
    public boolean move_is_valid(int x, int y, boolean playerIsBlack )
    {
        if( CHECK_FOR_MATE )
        {
            if( move_is_check(x,y, playerIsBlack) )
                controller.setIsCheck(true);

            enemy = board.get_figure(board.get_button(x, y));
            if (enemy != null)
            {
                if( enemy instanceof King ) {
                    controller.add_valid_move(board.get_button(x, y));
                    return true;
                }

                if (enemy.isBlack() == playerIsBlack)  // no further than enemy
                    controller.add_valid_move(board.get_button(x, y));
                return false;
            }
            else    // free field
                controller.add_valid_move(board.get_button(x, y));
            return true;
        }

        else if( IS_CHECK )
        {
            Button CRITICALS[] = controller.getCriticalKINGMove();
            System.out.print("");
        }
        else
        {
            enemy = board.get_figure(board.get_button(x, y));
            if (enemy != null)
            {
                if (enemy.isBlack() != playerIsBlack)  // no further than enemy
                    controller.add_valid_move(board.get_button(x, y));
                return false;
            } else    // free field
                controller.add_valid_move(board.get_button(x, y));
            return true;

        }
        return false;
    }

    public boolean move_is_check( int x, int y, boolean isBlack )
    {
        Button king = board.get_king_btn( !isBlack );
        Button check = board.get_button(x,y);

        if( check != null && king != null )
            if( king.getId().equals( check.getId() ))
            {
                controller.setIsCheck( true );
                return true;
            }

        return false;
    }

    public void pawn( int x, int y, boolean isBlack ) throws FileNotFoundException
    {

        int x1, x2;
        int y1, y2;

        if( isBlack )
        {
            x1 = x -1;
            x2 = x +1;
            y1 = y +1;
            y2 = y +2;
        }
        else
        {
            x1 = x +1;
            x2 = x -1;
            y1 = y -1;
            y2 = y -2;
        }

        if( CHECK_FOR_MATE )
        {    // pawn could kill
            if (board.get_figure(board.get_button(x1, y1)) == null)
                move_is_valid(x1, y1, isBlack); // kill left
            else if( board.get_figure(board.get_button(x1,y1)).isBlack() == isBlack )
                move_is_valid(x1,y1,isBlack);   // king could kill this one

            if (board.get_figure(board.get_button(x2, y1)) == null)
                move_is_valid(x2, y1, isBlack); // kill right
            else if( board.get_figure(board.get_button(x2,y1)).isBlack() == isBlack )
                move_is_valid(x2,y1,isBlack);   // king could kill this one
        }

        else    /* NORMAL */
        {
            Abstract_Figure tmp = board.get_figure(( board.get_button(x, y1)));
            // 1 up - down
            if (tmp == null) {
                if (move_is_valid(x, y1, isBlack)) {
                    tmp = board.get_figure(board.get_button(x, y2));
                    // black can jump 2 down from top
                    if (y == 1 && isBlack && tmp == null)
                        move_is_valid(x, y2, isBlack);

                        // white can jump 2 up from bottom
                    else if (y == 6 && !isBlack && tmp == null)
                        move_is_valid(x, y2, isBlack);
                }
            }

            // pawn can kill
            if (board.get_figure(board.get_button(x1, y1)) != null)
                move_is_valid(x1, y1, isBlack); // kill left

            if (board.get_figure(board.get_button(x2, y1)) != null)
                move_is_valid(x2, y1, isBlack); // kill right

        }
    }

    public void king( int x, int y, boolean playerIsBlack ) throws FileNotFoundException
    {
        int x1 = x+1;
        int x2 = x-1;
        int y1 = y+1;
        int y2 = y-1;

        //UP
        if( y1 >=0 && y1 <8 )
            move_is_valid(x, y1, playerIsBlack);
        //DOWN
        if( y2 >=0 && y2 <8 )
            move_is_valid(x, y2, playerIsBlack);
        //RIGHT
        if( x1 >=0 && x1 <8 )
            move_is_valid(x1, y, playerIsBlack);
        //LEFT
        if( x2 >=0 && x2 <8 )
            move_is_valid(x2, y, playerIsBlack);
        //UP-LEFT
        if( x2 >=0 && x2 <8 && y1 >=0 && y1 <8)
            move_is_valid(x2, y1, playerIsBlack);
        //DOWN-LEFT
        if( x2 >=0 && x2 <8 && y2 >=0 && y2 <8)
            move_is_valid(x2, y2, playerIsBlack);
        //UP-RIGHT
        if( x1 >=0 && x1 <8 && y1 >=0 && y1 <8)
            move_is_valid(x1, y1, playerIsBlack);
        //DOWN-RIGHT
        if( x1 >=0 && x1 <8 && y2 >=0 && y2 <8)
            move_is_valid(x1, y2, playerIsBlack);
    }

    public void queen( int x, int y, boolean playerIsBlack ) throws FileNotFoundException
    {
        // yes, the almighty queen has combined power of rook and pawn
        rook(x,y, playerIsBlack);
        bishop(x,y, playerIsBlack);
    }

    public void rook( int x, int y, boolean playerIsBlack ) throws FileNotFoundException
    {
        for(int i=1; i<8; i++)
        {   //RIGHT
            if( x+i > 7 )
                break;

            if( CHECK_FOR_MATE )
            {
                if (move_is_check(x + i, y, playerIsBlack)) {   // add CRITICAL fields - all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x + j, y);
                        controller.add_critical_king_move(btn);
                    }
                }
            }

            if( !move_is_valid(x+i, y, playerIsBlack) )
                break;
        }
        for(int i=1; i<8; i++)
        {   //LEFT
            if( x-i < 0 )
                break;

            if( CHECK_FOR_MATE )
            {
                if (move_is_check(x - i, y, playerIsBlack)) {   // add CRITICAL fields - all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x - j, y);
                        controller.add_critical_king_move(btn);
                    }
                }
            }
            if( !move_is_valid(x-i, y, playerIsBlack) )
                break;
        }
        for(int i=1; i<8; i++)
        {   //UP
            if( y+i > 7 )
                break;

            if( CHECK_FOR_MATE )
            {
                if (move_is_check(x , y+i, playerIsBlack)) {   // add CRITICAL fields - all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x , y+j);
                        controller.add_critical_king_move(btn);
                    }
                }
            }
            if( !move_is_valid(x, y+i, playerIsBlack) )
                break;
        }
        for(int i=1; i<8; i++)
        {   //DOWN
            if( y-i < 0 )
                break;

            if( CHECK_FOR_MATE )
            {
                if (move_is_check(x, y-i, playerIsBlack)) {   // add CRITICAL fields - all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x, y -j);
                        controller.add_critical_king_move(btn);
                    }
                }
            }
            if( !move_is_valid(x, y-i, playerIsBlack) )
                break;
        }
    }

    public void bishop( int x, int y, boolean playerIsBlack ) throws FileNotFoundException
    {
        for(int i=1; i<8; i++)
        {   //DOWN-RIGHT
            if( x+i >7 || y+i >7 )
                break;

            if( CHECK_FOR_MATE ) {
                if (move_is_check(x + i, y + i, playerIsBlack)) {   // add CRITICAL fields - all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x + j, y + j);
                        controller.add_critical_king_move(btn);
                    }
                }
            }

            if( !move_is_valid(x+i, y+i, playerIsBlack) )
                break;
        }

        for(int i=1; i<8; i++)
        {   //DOWN-LEFT
            if( x-i <0 || y+i >7 )
                break;

            if( CHECK_FOR_MATE ) {
                if (move_is_check(x - i, y + i, playerIsBlack)) {   // all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x - j, y + j);
                        controller.add_critical_king_move(btn);
                    }
                }
            }

            if( !move_is_valid(x-i, y+i, playerIsBlack) )
                break;
        }

        for(int i=1; i<8; i++)
        {   //UP-RIGHT
            if( x+i >7 || y-i <0 )
                break;


            if( CHECK_FOR_MATE ) {
                if (move_is_check(x + i, y - i, playerIsBlack)) {   // all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x + j, y - j);
                        controller.add_critical_king_move(btn);
                    }
                }
            }

            if( !move_is_valid(x+i, y-i, playerIsBlack) )
                break;
        }

        for(int i=1; i<8; i++)
        {   //UP_LEFT
            if( x-i <0 || y-i <0 )
                break;


            if( CHECK_FOR_MATE ) {
                if (move_is_check(x - i, y - i, playerIsBlack)) {   // all the way back
                    for (int j = 0; j < i; j++) {
                        btn = board.get_button(x - j, y - j);
                        controller.add_critical_king_move(btn);
                    }
                }
            }

            if( !move_is_valid(x-i, y-i, playerIsBlack) )
                break;
        }
    }

    public void horse( int x, int y, boolean playerIsBlack ) throws FileNotFoundException
    {
        int horseMovesX[] = new int[8];
        int horseMovesY[] = new int[8];

        horseMovesX[0] = x+1;
        horseMovesY[0] = y+2;

        horseMovesX[1] = x+2;
        horseMovesY[1] = y+1;

        horseMovesX[2] = x+2;
        horseMovesY[2] = y-1;

        horseMovesX[3] = x+1;
        horseMovesY[3] = y-2;

        horseMovesX[4] = x-1;
        horseMovesY[4] = y-2;

        horseMovesX[5] = x-2;
        horseMovesY[5] = y-1;

        horseMovesX[6] = x-2;
        horseMovesY[6] = y+1;

        horseMovesX[7] = x-1;
        horseMovesY[7] = y+2;

        for(int i=0;i<8;i++)
        {
            int xCord = horseMovesX[i];
            int yCord = horseMovesY[i];

            if( xCord <0 || xCord >7 || yCord <0 || yCord >7 )
                continue;

            if( y == 2 )
                System.out.print("");
            if( CHECK_FOR_MATE )
            {
                if (move_is_check(xCord, yCord, playerIsBlack)) {
                    // either the horse gets killed or the king moves, or it simply check-mate
                    btn = board.get_button(xCord, yCord);
                    controller.add_critical_figure_move(btn);
                    btn = board.get_button(x,y);
                    controller.add_critical_king_move(btn);
                }
            }

            enemy = board.get_figure( board.get_button(xCord, yCord) );
            if( enemy != null )
                if ( enemy.isBlack() == playerIsBlack )
                    continue;   // no team-kill

            controller.add_valid_move( board.get_button(xCord, yCord) );
        }
    }

}
