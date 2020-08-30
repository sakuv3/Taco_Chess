package Taco_Chess.Figures;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;


public abstract class Abstract_Figure
{
    protected Button btn;
    protected int xCoord;
    protected int yCoord;
    protected boolean isBlack;
    protected ImageView imageView;

    public Abstract_Figure(){}
    public Abstract_Figure( Abstract_Figure x ){
        this.btn = x.btn;
        this.xCoord = x.xCoord;
        this.yCoord = x.yCoord;
        this.isBlack = x.isBlack;
        this.imageView = x.imageView;
    }
    public int getXCoord() {
        return this.xCoord; }

    public int getYCoord() {
        return this.yCoord; }

    public void setCoordinates( int x, int y )
    {
        this.xCoord = x;
        this.yCoord = y;
    }

    abstract boolean move();
    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setxCoord(int val) {
        this.xCoord = val; }

    public void setyCoord(int val) {
        this.yCoord = val; }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }
}
