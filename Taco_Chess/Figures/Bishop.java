package Taco_Chess.Figures;

public class Bishop extends Abstract_Figure
{
    @Override
     boolean move() {
        return false;
    }

    public Bishop(){};
    public Bishop( Bishop X )
    {
        super(X);
    }
}
