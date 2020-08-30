package Taco_Chess.Figures;

public class Queen extends Abstract_Figure
{
    @Override
     boolean move() {
        return false;
    }
    public Queen(){};
    public Queen( Queen X )
    {
        super(X);
    }
}
