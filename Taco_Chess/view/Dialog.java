package Taco_Chess.view;
import Taco_Chess.Figures.*;
import Taco_Chess.controller.BoardController;
import Taco_Chess.model.Board;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Dialog
{

    static final String URL = "src/Taco_Chess/images/";
    static final String type[] = {"queen.png", "horse.png", "rook.png", "bishop.png"};
    static private BoardController controller;
    static Board board;
    static View view;

    public Dialog( Board board, View view, BoardController controller )
    {
        this.controller = controller;
        this.board = board;
        this.view = view;
    }

    public static void spawn_new_figure(Abstract_Figure activePlayer, Button btn, boolean isBlack) throws FileNotFoundException {
        String PATH;
        board.getChessBoard().setDisable( true );
        String figs[]   = new String[4];
        Button btns[]   = new Button[4];
        GridPane grid   = new GridPane();
        ColumnConstraints column = new ColumnConstraints(100);
        RowConstraints row       = new RowConstraints(100);

        // gibt den auswählbaren figuren ne abgerundete Form
        Rectangle rect = new Rectangle(100, 100);
        rect.setArcHeight(10);
        rect.setArcWidth(10);

        grid.getColumnConstraints().add(column);
        grid.getColumnConstraints().add(column);
        grid.getRowConstraints().add(row);
        grid.getRowConstraints().add(row);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxHeight(200);
        grid.setMaxWidth(200);

        if( isBlack )
            PATH = URL +"black/";
        else
            PATH = URL +"white/";

        for(int i =0; i<4 ; i++)
        {
            final int j =i;
            final String URL = PATH +type[i];
            btns[i] = new Button();
            btns[i].setMaxHeight(100);
            btns[i].setMaxWidth(100);
            btns[i].setStyle(" -fx-background-color: linear-gradient(lightgreen, cadetblue); ");
            btns[i].setGraphic( new ImageView(new Image(new FileInputStream( URL ))) );
            btns[i].setShape(rect);
            btns[i].setOnMouseEntered( e -> { btns[j].setStyle(
                    "-fx-border-color: gold; " +
                    "-fx-background-color: linear-gradient(cadetblue, lightgreen);" );  });
            btns[i].setOnMouseExited( e ->  { btns[j].setStyle(
                    "-fx-border-color: #A39300;" +
                    "-fx-background-color: linear-gradient(lightgreen, cadetblue);" );  });

            btns[i].setOnMouseClicked( e ->
            {   // SPAWN NEW CHOSEN FIGURE
                FileInputStream FIS  = null;
                try {
                    FIS = new FileInputStream( URL );
                Image IMAGE          = new Image(FIS);
                ImageView IMG  = new ImageView( IMAGE );
                Abstract_Figure newFig =null;

                    if( j ==0 )
                        newFig = new Queen();

                    else if( j==1 )
                        newFig = new Horse();

                    else if( j==2 )
                        newFig = new Rook();

                    else if( j==3 )
                        newFig = new Bishop();


                    newFig.setImageView( IMG );
                    newFig.setBtn( btn );
                    view.update( newFig, btn, false );
                    int x = board.get_xCoord_btn( btn );
                    int y = board.get_yCoord_btn( btn );
                    board.set_figure( newFig, x, y, isBlack);
                    view.update_score( null );


                    controller.check_for_mate( newFig.isBlack() );// falls der neue Zug den Gegner in Schach setzt
                    if( controller.isCheck() ) // ja hat er
                    {
                        controller.setIsCheck(true);
                        System.out.println("CHECK");
                    }

                    // remove the chosable 4 figures
                    view.getStackPane().getChildren().remove( grid );
                    board.getChessBoard().setDisable( false );
                }
                catch (FileNotFoundException fileNotFoundException)
                {
                    System.out.println("mayday");
                }
            });
        }
        grid.add(btns[0], 0, 0);
        grid.add(btns[1], 1, 0);
        grid.add(btns[2], 0, 1);
        grid.add(btns[3], 1, 1);

        // Shows the 4 figures to choose from
        view.getStackPane().getChildren().addAll( grid );
    }
}
