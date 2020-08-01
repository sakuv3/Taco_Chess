package Taco_Chess;

import Taco_Chess.Figures.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class View
{
    static Stage mainStage;
    static private Board board;
    static private StackPane stackPane;
    static private BorderPane borderPane;
    static private HBox top;
    static private HBox bottom;
    static private BoardController controller;

    static final String linuxURL = "/home/saku/IdeaProjects/Taco/src/Taco_Chess/images/";
    static private FileInputStream FIS;
    static private ImageView IMAGEVIEW;
    static private Image IMAGE;

    static private Circle circles[];
    static private Rectangle rect[];


    public View(){};
    public View( Stage mainStage, Board board ) throws IOException
    {
        controller      = new BoardController();
        this.mainStage  = mainStage;
        this.board      = board;
    }

    public void init(double width, double height ) throws IOException
    {
        init_panes( width, height );
        draw_figures();

        // coloring active players position
        rect = new Rectangle[2];
        // current BTN
        rect[0] = new Rectangle(100, 100 );
        rect[0].setFill( Color.ORANGE);
        rect[0].setOpacity(0.25);
        rect[0].setDisable(true);

        // destination BTN
        rect[1] = new Rectangle( 100, 100 );
        rect[1].setFill( Color.GREEN);
        rect[1].setOpacity(0.25);
        rect[1].setDisable(true);
    }

    private void init_panes( double width, double height ) throws IOException
    {
        // Wallpaper
        FIS         = new FileInputStream(linuxURL +"nature.jpg");
        IMAGE       = new Image(FIS);
        IMAGEVIEW   = new ImageView(IMAGE);
        IMAGEVIEW.setFitWidth( width );
        IMAGEVIEW.setFitHeight( height );

        // Actual GUI -> top - right - bottom - left
        init_borderPane( width, height );

        // Bottom - Field
        stackPane   = new StackPane();
        stackPane.setMaxWidth( width );
        stackPane.setMaxHeight( height );
        stackPane.getChildren().addAll( IMAGEVIEW, borderPane );

        // set the one and only scene for the chess gui
        Scene scene = new Scene( stackPane );
        scene.getStylesheets().add(getClass().getResource("Board.css").toExternalForm());
        mainStage.setScene( scene );
    }

    private void init_borderPane( double width, double height ) throws FileNotFoundException {
        borderPane = new BorderPane();
        borderPane.setPrefWidth( width );
        borderPane.setPrefHeight( height );
        borderPane.setCenter( board.getChessBoard() );

        FIS         = new FileInputStream(linuxURL +"players/pharaoh.png");
        IMAGE       = new Image(FIS);
        ImageView xx[]   = new ImageView[2];
        xx[0] = new ImageView(IMAGE);
        xx[0].setFitWidth( 90 );
        xx[0].setFitHeight( 90 );

        FIS         = new FileInputStream(linuxURL +"players/lion.png");
        IMAGE       = new Image(FIS);
        xx[1] = new ImageView(IMAGE);
        xx[1].setFitWidth( 90 );
        xx[1].setFitHeight( 90 );

        top = new HBox();
        top.setOpacity(0.7);
        top.setPrefWidth(800);
        top.setPrefHeight(100);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setMaxWidth(Region.USE_PREF_SIZE);
        top.setMaxHeight(Region.USE_PREF_SIZE);
        BorderPane.setAlignment(top, Pos.TOP_CENTER);
        top.setPadding( new Insets(0, 0,0,0));
        top.setStyle(" -fx-background-color: linear-gradient( #cbe3a8, #77944e); ");
        top.getChildren().add( xx[0] );

        bottom = new HBox();
        bottom.setOpacity(0.7);
        bottom.setPrefWidth(800);
        bottom.setPrefHeight(100);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setMaxWidth(Region.USE_PREF_SIZE);
        bottom.setMaxHeight(Region.USE_PREF_SIZE);
        BorderPane.setAlignment(bottom, Pos.BOTTOM_CENTER);
        bottom.setStyle(" -fx-background-color: linear-gradient( #cbe3a8, #77944e); ");;
        bottom.getChildren().add( xx[1] );

        borderPane.setTop( top );
        borderPane.setBottom( bottom );

    }
    public void draw_figures( ) throws FileNotFoundException
    {
        String PATH;
        Abstract_Figure[] activeFigs = board.get_all_figures();

        for( int i=0; i<activeFigs.length; i++ )
        { /* jede Figur behält ihr zugehöriges Image */
            PATH    = get_figure_path( activeFigs[i] );
            FIS     = new FileInputStream( PATH );
            IMAGE   = new Image( FIS );
            IMAGEVIEW = new ImageView( IMAGE );

            activeFigs[i].setImageView( IMAGEVIEW );
            activeFigs[i].getBtn().setGraphic( IMAGEVIEW );
        }
    }

    public void update( Abstract_Figure oldPlayer, Button dest )
    {
        clear_possible_circles();
        oldPlayer.getBtn().setGraphic( null );
        dest.setGraphic( oldPlayer.getImageView() );
    }
    public void add_killed_figure( String type, boolean isBlack ) throws FileNotFoundException {

        FileInputStream FIS = new FileInputStream(linuxURL +"black/pawn.png");
        FileInputStream FOS = new FileInputStream(linuxURL +"white/pawn.png");
        Image image1 = new Image(FIS);
        Image image2 = new Image(FOS);
        ImageView black[] = new ImageView[16];
        ImageView white[] = new ImageView[16];


        HBox top = new HBox();
        top.setOpacity(0.75);
        top.setSpacing(0);
        top.setPrefHeight(100);
        top.setPrefWidth(800);
        top.setMaxHeight(Region.USE_PREF_SIZE);
        top.setMaxWidth(Region.USE_PREF_SIZE);
        top.setAlignment(Pos.BOTTOM_LEFT);
        top.setStyle( "-fx-background-color: linear-gradient(#bdd49b, #94b26a)");
        BorderPane.setMargin( top, new Insets(0,0,0,100));

        HBox bottom = new HBox();
        bottom.setSpacing(0);
        bottom.setOpacity(0.75);
        bottom.setPrefHeight(100);
        bottom.setPrefWidth(800);
        bottom.setMaxHeight(Region.USE_PREF_SIZE);
        bottom.setMaxWidth(Region.USE_PREF_SIZE);
        bottom.setAlignment(Pos.TOP_LEFT);
        bottom.setStyle( "-fx-background-color: linear-gradient(#bdd49b, #94b26a)");
        BorderPane.setMargin( bottom, new Insets(0,0,0,100));

        for(int i=0; i<16;i++) {
            white[i] = new ImageView(image2);
            white[i].setFitHeight(25);
            white[i].setFitWidth(25);
            top.getChildren().add( white[i] );

            black[i] = new ImageView(image1);
            black[i].setFitHeight(25);
            black[i].setFitWidth(25);
            bottom.getChildren().add( black[i] );
        }
        borderPane.setTop( top );
        borderPane.setBottom( bottom );
    }
    public void draw_possible_circles( int x, int y ) throws FileNotFoundException
    {
        circles                 = controller.getCircles();
        Button possibleMoves[]  = controller.getPossibleMoves();

        // mark players current-field with color
        set_active_field( 0, x, y );

        for( int i=0; i<64; i++ )
        {
            if( possibleMoves[i] != null )
            {
                x = board.get_xCoord_btn( possibleMoves[i] );
                y = board.get_yCoord_btn( possibleMoves[i] );
                board.getChessBoard().add( circles[i], x, y );
            }
            else
                break;
        }
    }

    public void set_active_field( int i, int x, int y )
    {
        board.getChessBoard().add( rect[i], x, y );
    }
    public void clear_active_fields()
    {
        if( rect != null )
        {
            board.getChessBoard().getChildren().remove( rect[0]);
            board.getChessBoard().getChildren().remove( rect[1]);
        }
    }

    public void clear_possible_circles()
    {
        Button possibleMoves[] = controller.getPossibleMoves();
        if( possibleMoves != null )
        {
            for(int i=0; i<64; i++)
            {
                if( possibleMoves[i] != null )
                    board.getChessBoard().getChildren().remove(circles[i]);
                else
                    break;
            }
            circles = null;
        }
    }

    public String get_figure_path( Abstract_Figure figure )
    {
        String PATH;
        if( figure.isBlack() )
            PATH = linuxURL + "black/";
        else
            PATH = linuxURL +"white/";

        if( figure instanceof King)
            PATH = PATH + "king.png";
        else if( figure instanceof Queen)
            PATH = PATH +"queen.png";
        else if( figure instanceof Rook)
            PATH = PATH + "rook.png";
        else if( figure instanceof Horse)
            PATH = PATH + "horse.png";
        else if( figure instanceof Bishop )
            PATH = PATH + "bishop.png";
        else if( figure instanceof Pawn )
            PATH = PATH + "pawn.png";
        return PATH;
    }

    public static StackPane getStackPane() {
        return stackPane;
    }
}