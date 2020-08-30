package Taco_Chess.Figures;

public class Horse extends Abstract_Figure
{
    @Override
     boolean move() {
        return false;
    }

    public Horse(){};
    public Horse( Horse X )
    {
        super(X);
    }
}
