package Taco_Chess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application
{
    static Board board;
    static Button fields[][];
    BoardController controller;

    @Override
    public void start(Stage mainStage) throws Exception
    {
        board       = new Board( );
        fields      = new Button[8][8];
        controller  = new BoardController();

        GridPane root = FXMLLoader.load(getClass().getResource("Board.fxml"));

        // create 64 fields, each equipping with sensors
        for( int y=0;y<8;y++)
        {
            for(int x=0;x<8;x++)
            {
                final int xVal = x;
                final int yVal = y;
                fields[x][y] = new Button();
                fields[x][y].setPrefWidth(75);
                fields[x][y].setPrefHeight(75);
                fields[x][y].setId( Integer.toString(x) + Integer.toString(y) );
                fields[x][y].setOnMouseEntered( enter -> controller.buttonEnter( fields[xVal][yVal]) );
                fields[x][y].setOnMouseExited(  exit -> controller.buttonExit( fields[xVal][yVal]) );
                fields[x][y].setOnMouseClicked( clicked -> controller.handleButtonMove( fields[xVal][yVal]) );
                root.add(fields[x][y], x, y);
            }
        }
        board.init( fields );
        controller.init( board, fields );

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("Board.css").toExternalForm());
        mainStage.setScene(scene);

        mainStage.setTitle("Taco_Chess");
        mainStage.centerOnScreen();
        mainStage.setResizable(false);
        mainStage.setFullScreen(false);
        mainStage.setAlwaysOnTop(false);
        mainStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}