package Taco_Chess.Figures;

public class Pawn extends Abstract_Figure
{
    @Override
     boolean move()
    {
        return false;
    }
    public Pawn(){};
    public Pawn( Pawn X )
    {
        super(X);
    }
}
