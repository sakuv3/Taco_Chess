package Taco_Chess.view;
import Taco_Chess.Figures.*;
import Taco_Chess.controller.BoardController;
import Taco_Chess.model.Board;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Dialog
{

    static final String URL = "src/Taco_Chess/images/";
    static String BEFORE1, BEFORE2;
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

    public static void GAMEOVER( boolean isBlack ) throws FileNotFoundException {
        Stage window    = new Stage();
        GridPane grid   = new GridPane();
        StackPane stack = new StackPane();
        FileInputStream fis = new FileInputStream("src/Taco_Chess/images/background/gameover.jpg");
        Image img = new Image(fis);
        ImageView bg = new ImageView(img);
        bg.setFitWidth(300);
        bg.setFitHeight(200);

        Button close    = new Button("Quit");
        Button newGame  = new Button("New Game");
        Button celeb    = new Button("Celebrate");

        close.setAlignment( Pos.BOTTOM_LEFT);
        close.setPrefHeight(25);
        close.setPrefWidth(100);
        close.setOnMouseEntered( e ->
        {
            BEFORE2 = close.getStyle();
            close.setStyle("-fx-background-color: linear-gradient(darkred,lightskyblue );");
        });
        close.setOnMouseExited( e -> {
            close.setStyle(BEFORE2);
        });
        close.setOnMouseClicked( e -> Platform.exit() );
        close.setStyle("-fx-background-color: linear-gradient(indianred, darksalmon)");

        newGame.setPrefWidth(100);
        newGame.setPrefHeight(25);
        newGame.setStyle("-fx-background-color: linear-gradient( cadetblue, lightgreen)");
        newGame.setAlignment( Pos.BOTTOM_RIGHT);
        newGame.setOnMouseEntered( e ->
        {
            BEFORE1 = newGame.getStyle();
            newGame.setStyle("-fx-background-color: linear-gradient(lightskyblue, greenyellow);");
        });
        newGame.setOnMouseExited( e -> {
            newGame.setStyle(BEFORE1);
        });
        newGame.setOnMouseClicked( e -> {
            try {
                board.reset_board();
                window.close();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        });

        grid.add( close, 0, 0);
        grid.add( newGame, 1, 0);
        grid.setAlignment( Pos.BOTTOM_CENTER );
        grid.setPadding( new Insets(130,0,0,0));
        VBox v = new VBox();
        String x;
        if( isBlack )
            x = "B L A C K  is the Winner";
        else
            x = "W H I T E   is the Winner";
        Label text = new Label(x);
        text.setAlignment(Pos.CENTER);
        text.setPadding( new Insets(10,0,0,40));
        text.setFont( Font.font("verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 20));
        text.setTextFill(Color.WHITESMOKE);
        text.setDisable(true);
        v.getChildren().addAll(text, grid);

        stack.getChildren().addAll( bg, v );
        Scene scene     = new Scene (stack, 300, 200 );

        window.initModality( Modality.APPLICATION_MODAL );
        window.initStyle(StageStyle.TRANSPARENT);
        window.setTitle("CHECKMATE");
        window.centerOnScreen();
        window.setScene(scene);
        window.show();
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
                    Image IMAGE    = new Image(FIS);
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


                    controller.collect_next_moves( newFig.isBlack(), false );// falls der neue Zug den Gegner in Schach setzt
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
