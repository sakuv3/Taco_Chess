package Taco_Chess;
import Taco_Chess.Figures.*;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Dialog
{

    static final String linuxURL = "/home/saku/IdeaProjects/Taco/src/Taco_Chess/images/";
    static final String type[] = {"queen.png", "horse.png", "rook.png", "bishop.png"};
    static Board board;

    public Dialog( Board board )
    {
        this.board = board;
    }

    public static void spawn_new_figure( Abstract_Figure activePlayer, Button btn, boolean isBlack ) throws FileNotFoundException {
        String PATH;
        Stage window    = new Stage();
        String figs[]   = new String[4];
        Button btns[]   = new Button[4];
        GridPane grid   = new GridPane();
        Rectangle rect  = new Rectangle();
        Scene scene     = new Scene(grid);
        ColumnConstraints column = new ColumnConstraints(100);
        RowConstraints row       = new RowConstraints(100);

        grid.getColumnConstraints().add(column);
        grid.getColumnConstraints().add(column);
        grid.getRowConstraints().add(row);
        grid.getRowConstraints().add(row);
        grid.setAlignment(Pos.CENTER);

        rect.setArcWidth(30);
        rect.setArcHeight(30);
        grid.setShape( rect );

        if( isBlack )
            PATH = linuxURL +"black/";
        else
            PATH = linuxURL +"white/";

        for(int i =0; i<4 ; i++)
        {
            final int j =i;

            figs[i] = PATH +type[i];
            btns[i] = new Button();
            btns[i].setPrefHeight(100);
            btns[i].setPrefWidth(100);
            btns[i].setStyle(" -fx-background-color: linear-gradient(lightgreen, cadetblue); ");
            btns[i].setGraphic( new ImageView(new Image(new FileInputStream( figs[i] ))) );

            btns[i].setOnMouseEntered( e -> { btns[j].setStyle(
                    "-fx-border-color: gold; " +
                    "-fx-background-color: linear-gradient(cadetblue, lightgreen);" );  });
            btns[i].setOnMouseExited( e ->  { btns[j].setStyle(
                    "-fx-border-color: #A39300;" +
                    "-fx-background-color: linear-gradient(lightgreen, cadetblue);" );  });

            btns[i].setOnMouseClicked( e ->
            {   // SPAWN NEW CHOSEN FIGURE
                Abstract_Figure newFig =null;
                try {
                    if( j ==0 )
                        newFig = new Queen();

                    else if( j==1 )
                        newFig = new Horse();

                    else if( j==2 )
                        newFig = new Rook();

                    else if( j==3 )
                        newFig = new Bishop();

                    newFig.setBlack(isBlack);
                    newFig.setCoordinates(activePlayer.getXCoord(), activePlayer.getYCoord());
                    board.move_player(newFig, btn);
                    window.close();
                }
                catch (FileNotFoundException fex )
                {
                    System.out.println("mayday");
                }
            });
        }
        grid.add(btns[0], 0, 0);
        grid.add(btns[1], 1, 0);
        grid.add(btns[2], 0, 1);
        grid.add(btns[3], 1, 1);

        // Calculate the center position of the parent Stage
        double x = board.getX() + board.getWidth()/2d;
        double y = board.getY() + board.getHeight()/2d;
        // Hide the pop-up stage before it is shown and becomes relocated
        window.setOnShowing( e -> window.hide() );
        // Relocate the pop-up Stage
        window.setOnShown( e -> {
            window.setX( x -window.getWidth() /2d );
            window.setY( y - window.getHeight() /2d );
            window.show();
        });
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setScene(scene);
        window.show();
    }
}
