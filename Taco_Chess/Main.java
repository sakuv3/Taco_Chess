package Taco_Chess;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Board brett = new Board();

        Scene brettScene = new Scene( brett );

        primaryStage.setTitle("Taco_Chess");
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);
        primaryStage.setAlwaysOnTop(false);
        primaryStage.setScene( brettScene );
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
