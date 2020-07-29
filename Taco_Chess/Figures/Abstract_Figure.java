package Taco_Chess.Figures;

public abstract class Abstract_Figure
{
    public boolean isBlack = false;

    protected int xCoord = -1;
    protected int yCoord = -1;

    abstract boolean move();

    public int getXCoord() {
        return this.xCoord; }

    public int getyCoord() {
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
}
