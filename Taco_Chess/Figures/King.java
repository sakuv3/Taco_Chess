package Taco_Chess.Figures;

public class King extends Abstract_Figure
{
    @Override
     boolean move() {
        return false;
    }
    public King(){};
    public King( King X )
    {
        super(X);
    }
}
