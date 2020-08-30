package Taco_Chess.Figures;

public class Rook extends Abstract_Figure
{
    @Override
    boolean move() {
        return false;
    }

    public Rook(){};
    public Rook( Rook X )
    {
        super(X);
    }
}
