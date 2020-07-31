package Taco_Chess.Figures;

import javafx.scene.control.Button;

public abstract class Abstract_Figure
{
    private boolean isBlack = false;
    protected int xCoord    = -1;
    protected int yCoord    = -1;
    private Button btn      = null;

    abstract boolean move();

    public int getXCoord() {
        return this.xCoord; }

    public int getYCoord() {
        return this.yCoord; }

    public void setxCoord(int val) {
        this.xCoord = val; }

    public void setyCoord(int val) {
        this.yCoord = val; }

    public void setCoordinates( int x, int y )
    {
        this.xCoord = x;
        this.yCoord = y;
    }

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
