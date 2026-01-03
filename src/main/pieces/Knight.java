package main.pieces;

import main.Echiquier;
import main.Piece;

public class Knight extends Piece {
    public Knight(int color) {
        super("knight", color, "♞", "♘");
    }

    @Override
    public boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier) {
        // bornes
        if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8) {
            return false;
        }

        int dx = endX - startX;
        int dy = endY - startY;
        dx = Math.abs(dx);
        dy = Math.abs(dy);

        return (dx == 1 && dy == 2) ^ (dx == 2 && dy == 1);
    }

    public Knight clone() {
        Knight nouveau = new Knight(this.color);
        return nouveau;
    }

    public void deplacementPossible(Echiquier e, int x, int y) {
        this.casePossible = new boolean[e.pieces.length][e.pieces[1].length];
        for (int i = 0; i < e.pieces.length; i++) {
            for (int j = 0; j < e.pieces[i].length; j++) {
                this.casePossible[i][j] = this.isMovementValid(x, y, j, i, e);
            }
        }
    }
}
