package Taco_Chess;
import javafx.application.Application;
import javafx.scene.control.Button;
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