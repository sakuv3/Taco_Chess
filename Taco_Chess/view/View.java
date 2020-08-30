package Taco_Chess.view;

import Taco_Chess.Figures.*;
import Taco_Chess.Main;
import Taco_Chess.controller.BoardController;
import Taco_Chess.model.Board;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class View
{
    private Main main;
    private static Scene scene;
    static private Stage mainStage;
    static private Board board;
    static private VBox vbox;
    static private StackPane stackPane;
    static private BoardController controller;
    static final private String URL = "src/Taco_Chess/images/";

    /* FAQ Box */
    static private VBox FAQBox;
    /* Top and Bottom Panes for Players Images and killed figures */
    static private HBox hTop;
    static private HBox hBottom;
    static private VBox vTop;
    static private VBox vBottom;
    static private HBox bottomFigureBox;
    static private HBox topFigureBox;
    static private int blackValue=0, whiteValue=0;
    static private Label topLabelCNT, bottomLabelCNT;

    static private Abstract_Figure deadWhite[];
    static private Abstract_Figure deadBlack[];
    static private FileInputStream FIS;
    static private ImageView BACKGROUND, FIGS;
    static private Image players[];
    static private Image IMAGE;

    /* used to graphically indicate possible moves and current players position */
    static private Circle circles[];
    static private Rectangle rect[];
    static private String COLOR = "-fx-border-color: deepskyblue";
    static private String Color_before =null;
    static private String WHITE = " -fx-border-color: #cbe3a8";
    static private String BLACK = " -fx-border-color: #77944e";
    static private Button HOVER;

    static int cnt =0;
    public View(){};
    public View( Stage mainStage, Board board ) throws IOException
    {
        main            = new Main();
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
        rect[0].setFill( Color.DEEPSKYBLUE);
        rect[0].setOpacity(0.6);
        rect[0].setDisable(true);

        // destination BTN
        rect[1] = new Rectangle( 100, 100 );
        rect[1].setFill( Color.AQUA);
        rect[1].setOpacity(0.45);
        rect[1].setDisable(true);

        // killed figures
        deadWhite = new Abstract_Figure[20];
        deadBlack = new Abstract_Figure[20];

        // team values
        blackValue = total_team_value( true );
        whiteValue = total_team_value( false );
        topLabelCNT     = new Label();
        bottomLabelCNT  = new Label();
        topLabelCNT.setTextFill(Color.SKYBLUE);
        bottomLabelCNT.setTextFill(Color.SKYBLUE);
        topLabelCNT.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 20));
        bottomLabelCNT.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 20));

    }
    private void init_panes( double width, double height ) throws IOException
    {
        // Wallpaper
        FIS         = new FileInputStream(URL +"background/wood3.jpg");
        IMAGE       = new Image(FIS);

        BACKGROUND   = new ImageView(IMAGE);
        BACKGROUND.setFitWidth( width );
        BACKGROUND.setFitHeight( height );

        // BorderPane-> ( top-> (HBox-> (PlayerImage, VBox-> (Label, Hbox-> (deadFigure1, deadFigure2, etc..)))))
        // BorderPane-> ( bottom-> (HBox-> (PlayerImage, VBox-> (Label, Hbox-> (deadFigure1, deadFigure2, etc..)))))
        init_GUI( width, height );

        // Bottom - Field
        stackPane   = new StackPane();
        stackPane.setMaxWidth( width );
        stackPane.setMaxHeight( height );
        stackPane.getChildren().addAll( BACKGROUND, vbox  );

        // set the one and only scene for the chess gui
        scene = new Scene( stackPane );
        scene.getStylesheets().add(getClass().getResource("Board.css").toExternalForm());
        mainStage.setScene( scene );
    }
    public void show_faq( boolean selected )
    {

        if( selected ) {
            // Create First TitledPane.
            TitledPane first = new TitledPane();
            first.setText("Is the white team better than black?");
            first.setContent(new Label("yes mate"));
            first.setExpanded(false);

            // Create Second TitledPane.
            TitledPane second = new TitledPane();
            second.setText("What is a good chess opening?");
            second.setExpanded(false);

            VBox content2 = new VBox();
            TitledPane c21 = new TitledPane();
            c21.setText("Cicilian Defence");
            c21.setContent(new Label("just play defensive mate"));
            c21.setExpanded(false);
            TitledPane c22 = new TitledPane();
            c22.setText("Hungarian Attack");
            c22.setContent(new Label("kill everyone as soon as possible mate"));
            c22.setExpanded(false);

            content2.getChildren().addAll(c21, c22);

            second.setContent(content2);

            // Create Root Pane.
            FAQBox = new VBox();
            FAQBox.setMaxWidth(300);
            FAQBox.setPadding(new Insets(30, 0, 0, 0));
            FAQBox.getChildren().addAll(first, second);

            stackPane.getChildren().add( FAQBox );
        }
        else
            stackPane.getChildren().remove( FAQBox );


    }

    private void init_GUI( double width, double height ) throws FileNotFoundException {
        vbox = new VBox();
        vbox.setPrefWidth( width );
        vbox.setPrefHeight( height );

        topFigureBox    = new HBox();
        bottomFigureBox = new HBox();
        players         = new Image[2];
        Rectangle r[]   = new Rectangle[2];


        /* Player 1 */
        Label name1 = new Label("Black Pharaoh");
        FIS         = new FileInputStream(URL +"players/pharaoh.png");
        players[0]  = new Image(FIS);
        r[0]        = new Rectangle();
        r[0].setHeight(90);
        r[0].setWidth(90);
        r[0].setArcHeight(30);
        r[0].setArcWidth(30);
        r[0].setFill( new ImagePattern(players[0]) );

        vTop = new VBox();
        vTop.setSpacing(10);
        vTop.setPrefWidth(700);
        vTop.setPrefHeight(100);
        vTop.setMaxWidth(Region.USE_PREF_SIZE);
        vTop.setMaxHeight(Region.USE_PREF_SIZE);
        name1.setTextFill(Color.BLACK);
        name1.setPadding( new Insets(10,0,0,5));
        name1.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 20));
        vTop.getChildren().addAll( name1, topFigureBox );

        // Black Players TOP BOX for storing IMAGE and killed white figures
        hTop = new HBox();
        hTop.setOpacity(0.9);
        hTop.setPrefWidth(800);
        hTop.setPrefHeight(100);
        hTop.setAlignment(Pos.CENTER_LEFT);
        hTop.setMaxWidth(Region.USE_PREF_SIZE);
        hTop.setMaxHeight(Region.USE_PREF_SIZE);
        hTop.setPadding( new Insets(0, 0,0,5));
        hTop.setStyle(" -fx-background-color: linear-gradient( #d77822, #3f230a); ");
        hTop.getChildren().addAll( r[0], vTop );

        /* Player 2 */
        Label name2 = new Label("White Lion");
        FIS         = new FileInputStream(URL +"players/lion.png");
        players[1]  = new Image(FIS);
        r[1]        = new Rectangle();
        r[1].setHeight(90);
        r[1].setWidth(90);
        r[1].setArcHeight(30);
        r[1].setArcWidth(30);
        r[1].setFill( new ImagePattern(players[1]) );

        vBottom = new VBox();
        vBottom.setSpacing(10);
        vBottom.setPrefWidth(700);
        vBottom.setPrefHeight(100);
        vBottom.setMaxWidth(Region.USE_PREF_SIZE);
        vBottom.setMaxHeight(Region.USE_PREF_SIZE);
        name2.setTextFill(Color.WHITESMOKE);
        name2.setPadding(new Insets(10,0,0,5));
        name2.setFont(Font.font("verdana", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 20));
        vBottom.getChildren().addAll( name2, bottomFigureBox );

        // White Players BOTTOM BOX for storing IMAGE and killed black figures (box in a box) lol
        hBottom = new HBox();
        hBottom.setOpacity(0.9);
        hBottom.setPrefWidth(800);
        hBottom.setPrefHeight(100);
        hBottom.setAlignment(Pos.CENTER_LEFT);
        hBottom.setMaxWidth(Region.USE_PREF_SIZE);
        hBottom.setMaxHeight(Region.USE_PREF_SIZE);
        hBottom.setPadding( new Insets(0, 0,0,5));
        hBottom.setStyle(" -fx-background-color: linear-gradient( #3f230a, #d77822); ");;
        hBottom.getChildren().addAll( r[1], vBottom );


        Menu game           = new Menu("Game");
        MenuItem newGame    = new MenuItem("New Game");
        MenuItem quit       = new MenuItem("Quit");
        newGame.setOnAction( (ActionEvent e) ->  main.restart() );
        quit.setOnAction( (ActionEvent e) -> Platform.exit() );
        game.getItems().addAll( newGame, quit );

        Menu settings       = new Menu("Settings");
        Menu wallhack       = new Menu("Wallhack Mode");
        ToggleGroup tg      = new ToggleGroup();
        RadioMenuItem whON  = new RadioMenuItem("ON");
        RadioMenuItem whOFF = new RadioMenuItem("OFF");
        whON.setToggleGroup(tg);
        whOFF.setToggleGroup(tg);
        whON.setOnAction( (ActionEvent e) -> controller.setWallhackMode(true));
        whOFF.setOnAction( (ActionEvent e) -> controller.setWallhackMode(false));
        wallhack.getItems().addAll( whON, whOFF);

        Menu style = new Menu("Style");
        RadioMenuItem brown = new RadioMenuItem("Brown-White");
        RadioMenuItem green = new RadioMenuItem("Green-Lime");
        ToggleGroup ts = new ToggleGroup();
        brown.setToggleGroup(ts);
        green.setToggleGroup(ts);

        brown.setOnAction( (ActionEvent e) -> {
            scene.getStylesheets().clear();
            scene.getStylesheets().add( getClass().getResource("Board.css").toExternalForm());
        });
        green.setOnAction( (ActionEvent e) ->
        {
            try {
                stackPane.getChildren().removeAll( BACKGROUND, vbox );
                FIS = new FileInputStream(URL + "background/grass.jpg");
                IMAGE = new Image(FIS);
                BACKGROUND = new ImageView(IMAGE);
                BACKGROUND.setFitWidth(width);
                BACKGROUND.setFitHeight(height);
                stackPane.getChildren().addAll( BACKGROUND, vbox );
                hBottom.setStyle(" -fx-background-color: linear-gradient( #77944e, #bdd49b); ");
                hTop.setStyle(" -fx-background-color: linear-gradient( #bdd49b, #77944e); ");
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("Green.css").toExternalForm());
            }
            catch (FileNotFoundException fileNotFoundException)
            { fileNotFoundException.printStackTrace(); }
        });
        style.getItems().addAll( brown, green);

        settings.getItems().addAll(wallhack, style);

        Menu faq          = new Menu("FAQ");
        RadioMenuItem xxx = new RadioMenuItem("FAQS");

        xxx.setOnAction( (ActionEvent e ) -> show_faq( xxx.isSelected() ) );
        faq.getItems().add( xxx );

        MenuBar bar     = new MenuBar();
        bar.getMenus().addAll( game, settings, faq );

        vbox.getChildren().addAll( bar, hTop, board.getChessBoard(), hBottom );
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
            FIGS = new ImageView( IMAGE );

            activeFigs[i].setImageView( FIGS );
            activeFigs[i].getBtn().setGraphic( FIGS );
        }
    }
    public void update( Abstract_Figure oldPlayer, Button dest, boolean mark ) throws FileNotFoundException
    {
        Abstract_Figure killed = board.get_figure( dest );
        if( killed != null && killed.isBlack() != oldPlayer.isBlack() )
            add_killed_figure( killed );

        if( mark ) {
            // mark players destination field with color
            int x = board.get_xCoord_btn(dest);
            int y = board.get_yCoord_btn(dest);
            mark_active_field(1, x, y);
        }
        oldPlayer.getBtn().setGraphic(null);
        move_animation(oldPlayer, dest);
        dest.setGraphic( oldPlayer.getImageView() );
        clear_possible_circles();
        clear_next_moves();
        controller.setCOLOR( null );
    }

    private void move_animation(Abstract_Figure oldPlayer, Button dest)
    {
        double xold = oldPlayer.getBtn().getLayoutX();
        double yold = oldPlayer.getBtn().getLayoutY();
        double xnew = dest.getLayoutX();
        double ynew = dest.getLayoutY();
        double DIFFX = xnew - xold;
        double DIFFY = ynew - yold;

        if( xnew > xold )
            DIFFX = - DIFFX;
        else
            DIFFX = Math.sqrt( Math.pow(DIFFX, 2) );
        if( ynew > yold )
            DIFFY = - DIFFY;
        else
            DIFFY = Math.sqrt( Math.pow(DIFFY, 2) );
        ImageView img = oldPlayer.getImageView();
        img.setTranslateX( DIFFX );
        img.setTranslateY( DIFFY );

        TranslateTransition tt = new TranslateTransition();
        tt.setNode( img );
        tt.setToX( dest.getTranslateX() );
        tt.setToY( dest.getTranslateY() );
        tt.setDuration( Duration.seconds(1) );
        tt.setAutoReverse(true);
        tt.play();
    }
    public void draw_possible_circles( Abstract_Figure activePlayer) throws FileNotFoundException
    {
        int x = activePlayer.getXCoord();
        int y = activePlayer.getYCoord();

        circles                 = controller.getCircles();
        Button possibleMoves[]  = controller.getPossibleMoves();

        // mark players current-field with color
        mark_active_field( 0, x, y );

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

    public static void setHOVER(Button btn) {
        HOVER = btn;
    }

    public void draw_next_moves( boolean isBlack )
    {
        Button critical[] =  controller.getNextMoves();
        if( isBlack )
            COLOR = "-fx-background-color: slategrey; -fx-border-color: black";
        else
            COLOR = "-fx-background-color: darkslateblue; -fx-border-color: navajowhite";

        Color_before = COLOR;
        boolean hovered = false;
        if( critical != null )
        {
            for (int k = 0; k < 64; k++)
            {
                if (critical[k] == null)
                    break;
                critical[k].setStyle( COLOR );

                if( critical[k].equals( HOVER ))
                    hovered = true;
            }
        }
        if( hovered == false )
        {
            if ( HOVER != null ) {
                Color_before = null;
                HOVER = null;
            }
        }
    }
    public void clear_next_moves( )
    {
        Button critical[] =  controller.getNextMoves();
        COLOR = "-fx-border-color: null";
        if( critical != null )
        {
           // controller.setCOLOR( COLOR );
            for (int k = 0; k < 64; k++)
            {
                if (critical[k] == null)
                    break;

                critical[k].setStyle( null );
            }
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
        }
    }
    public void mark_active_field( int i, int x, int y )
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

    public void add_killed_figure( Abstract_Figure killed ) throws FileNotFoundException
    {
        FIS                     = new FileInputStream( get_figure_path( killed ) );
        IMAGE                   = new Image(FIS);
        ImageView deadIMG       = new ImageView(IMAGE);
        Abstract_Figure deadFig = get_new_dead( killed );
        deadIMG.setFitHeight(25);
        deadIMG.setFitWidth(25);
        deadFig.setImageView( deadIMG );
        deadFig.setBlack( killed.isBlack() );

        inSORT_killed_figure( deadFig );
        update_score( killed );
    }

    // sort killed figures
    private void inSORT_killed_figure( Abstract_Figure dead )
    {
        Abstract_Figure deadFIGS[];
        HBox box;

        if( dead.isBlack() ) {

            deadFIGS = deadBlack;
            box = bottomFigureBox;
        }
        else {
            deadFIGS = deadWhite;
            box = topFigureBox;
        }

        // remove all figs
        int i=0;
        while( deadFIGS[i] != null )
            box.getChildren().remove( deadFIGS[i++].getImageView() );

        // add new dead figure at end of array
        for(int j=0;j<16;j++)
            if( deadFIGS[j] == null ) {
                deadFIGS[j] = dead;
                break;
            }

        i=0;
        Abstract_Figure tmp; // insertionsort
        while( deadFIGS[i] != null )
        {
            int j = i;
            tmp = deadFIGS[i];
            while( j > 0 && board.get_type( deadFIGS[j-1] ) > board.get_type( tmp )  )
            {
                deadFIGS[j] = deadFIGS[j-1];
                j--;
            }
            deadFIGS[j] = tmp;
            i++;
        }

        i =0;   // re-adding the updated and sorted dead figures
        while( deadFIGS[i] != null )
            box.getChildren().add( deadFIGS[i++].getImageView() );

    }

    public void update_score( Abstract_Figure killed )
    {
        topFigureBox.getChildren().remove( topLabelCNT );
        bottomFigureBox.getChildren().remove( bottomLabelCNT );

        if( killed != null ) {
            if (killed.isBlack())
                blackValue = total_team_value(true) - get_credits(killed);
            else
                whiteValue = total_team_value(false) - get_credits(killed);
        }
        else
        {
            blackValue = total_team_value( true );
            whiteValue = total_team_value( false );
        }

        // make difference Positiv
        int DIFF = blackValue - whiteValue;
        int SCORE = (int) Math.sqrt( Math.pow( DIFF, 2) );

        if( DIFF > 0 )
        {   // black still has bigger score
            topLabelCNT.setText( "   +" +SCORE );
            topFigureBox.getChildren().add( topLabelCNT );
        }
        else if( DIFF < 0 )
        {   // white has bigger score
            bottomLabelCNT.setText( "   +" +SCORE );
            bottomFigureBox.getChildren().add( bottomLabelCNT );
        }
    }

    public int total_team_value( boolean isBlack )
    {
        int CNT=0;
        Abstract_Figure figs[] = board.get_team( isBlack );

        for(int i=0;i<figs.length;i++)
            CNT += get_credits( figs[i] );
        return CNT;
    }
    public int get_credits( Abstract_Figure figure )
    {
        int type = board.get_type( figure );
        if( type == 4 )
            return 9;   // queen
        else if( type == 3 )
            return 4;   // rook
        else if( type == 2)
            return 3;   // horse
        else if( type == 1)
            return 2;    // bishop
        else if( type == 0 )
            return 1;   // pawn

        return -1;      // king cant be killed
    }

    public Abstract_Figure get_new_dead( Abstract_Figure killed )
    {
        int type = board.get_type( killed );

        if( type == 0 )
            return new Pawn();
        if( type == 1 )
            return new Bishop();
        if( type == 2 )
            return new Horse();
        if( type == 3 )
            return new Rook();
        if (type == 4 )
            return new Queen();

        return null;
    }

    private String get_figure_path( Abstract_Figure figure )
    {
        String PATH;
        int type = board.get_type( figure );

        if( figure.isBlack() )
            PATH = URL + "black/";
        else
            PATH = URL +"white/";

        if( type == 0 )
            PATH = PATH + "pawn.png";
        else if( type == 1)
            PATH = PATH +"bishop.png";
        else if( type == 2 )
            PATH = PATH + "horse.png";
        else if( type == 3 )
            PATH = PATH + "rook.png";
        else if( type == 4 )
            PATH = PATH + "queen.png";
        else if( type == 5 )
            PATH = PATH + "king.png";
        return PATH;
    }

    public static String getColor_before() {
        return Color_before;
    }
    public static void setColor_before(String color_before) {
        Color_before = color_before;
    }

    public static StackPane getStackPane() {
        return stackPane;
    }
}
