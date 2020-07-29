package Taco_Chess;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{

    static BoardController controller;
    static Board board;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        board       = new Board();
        controller  = new BoardController();
        controller.init( board );

        board.setTitle("Taco_Chess");
        board.centerOnScreen();
        board.setResizable(false);
        board.setFullScreen(false);
        board.setAlwaysOnTop(false);
        board.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
