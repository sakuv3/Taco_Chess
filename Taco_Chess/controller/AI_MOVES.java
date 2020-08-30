package Taco_Chess.controller;

import Taco_Chess.Figures.Abstract_Figure;
import javafx.scene.control.Button;

public class AI_MOVES
{
    private Abstract_Figure fig;
    private Button move;
    private int score, evaluation;


    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public Abstract_Figure getFig() {
        return fig;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public Button getMove() {
        return move;
    }

    public void setFig(Abstract_Figure fig) {
        this.fig = fig;
    }

    public void setMove(Button move) {
        this.move = move;
    }
}
