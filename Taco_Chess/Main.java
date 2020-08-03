package Taco_Chess;
import Taco_Chess.controller.BoardController;
import Taco_Chess.model.Board;
import Taco_Chess.view.View;
import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application
{
    static private View view;
    static private Board model;
    static private BoardController controller;

    @Override
    public void start(Stage mainStage) throws Exception
    {
        model           = new Board( );
        view            = new View( mainStage, model );
        controller      = new BoardController();
        view.init(800, 1000);
        controller.init( model, view );

        mainStage.setTitle("Taco_Chess");
        mainStage.setAlwaysOnTop(false);
        mainStage.setFullScreen(false);
        mainStage.setResizable(false);
        mainStage.centerOnScreen();
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}