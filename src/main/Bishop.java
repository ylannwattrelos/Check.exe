package main;

public class Bishop extends Piece {
    public Bishop(int color) {

        super("bishop", color, "♝", "♗");
    }

    @Override
    public boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier) {
        // bornes
        if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8)
            return false;

        int dx = endX - startX;
        int dy = endY - startY;

        // immobile
        if (dx == 0 && dy == 0) return false;

        // doit être strictement diagonal
        if (Math.abs(dx) != Math.abs(dy)) return false;

        // pas de pièce alliée en cible
        Piece target = echiquier.pieces[endY][endX];
        if (target != null && target.color == this.color) return false;

        // chemin libre
        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);
        int x = startX + stepX;
        int y = startY + stepY;
        while (x != endX || y != endY) {
            if (echiquier.pieces[y][x] != null) return false;
            x += stepX;
            y += stepY;
        }
        return true;
    }

    public Bishop clone() {
        Bishop nouveau = new Bishop(this.color);
        return nouveau;
    }

    public void deplacementPossible(Echiquier e, int x, int y) {
        this.casePossible = new boolean[e.pieces.length][e.pieces[0].length];
        for (int row = 0; row < e.pieces.length; row++) {
            for (int col = 0; col < e.pieces[row].length; col++) {
                if (row == y && col == x) { this.casePossible[row][col] = false; continue; }
                this.casePossible[row][col] = this.isMovementValid(x, y, col, row, e);
            }
        }
    }
}