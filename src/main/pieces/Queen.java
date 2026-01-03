package main.pieces;

import main.Echiquier;
import main.Piece;

public class Queen extends Piece {

    public Queen(int color) {
        super("Queen", color, "♛", "♕");

    }

    @Override
    public boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier) {

        if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8) {
            return false;
        }

        int dx = endX - startX;
        int dy = endY - startY;

        // reste sur place
        if (dx == 0 && dy == 0)
            return false;

        // mouvement valide (ligne ou diagonale)
        if (!(Math.abs(dx) == Math.abs(dy) || dx == 0 || dy == 0)) {
            return false;
        }

        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);

        int x = startX + stepX;
        int y = startY + stepY;

        // vérifier chaque case intermédiaire
        while (x != endX || y != endY) {
            if (echiquier.pieces[y][x] != null) {
                return false; // une pièce bloque le chemin
            }
            x += stepX;
            y += stepY;
        }

        Piece target = echiquier.pieces[endY][endX];
        if (target != null && target.color == this.color) {
            return false;
        }

        return true;
    }

    public Queen clone() {
        Queen nouveau = new Queen(this.color);
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
