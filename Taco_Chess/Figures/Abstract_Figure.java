package Taco_Chess.Figures;

public abstract class Abstract_Figure
{
    private boolean isBlack     = false;
    private boolean isValid     = false;
    protected int xCoord = -1;
    protected int yCoord = -1;

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

    public boolean isBlack() {
        return isBlack;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
