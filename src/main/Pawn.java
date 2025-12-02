package main;

public class Pawn extends Piece {

    public Pawn(int color) {

        super("pawn", color, "♟", "♙");
    }

    @Override
    public boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier) {
        // bornes
        if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8)
            return false;

        int dx = endX - startX;
        int dy = endY - startY;

        // blancs montent (Y+), noirs descendent (Y-)
        int direction = (color == WHITE) ? 1 : -1;

        Piece target = echiquier.pieces[endY][endX];

        // 1. avance d'une case
        if (dx == 0 && dy == direction && target == null)
            return true;

        // 2. double pas depuis la rangée de départ
        int startRow = (color == WHITE) ? 1 : 6; // blancs: rang 2 (y=1), noirs: rang 7 (y=6)
        if (dx == 0 && dy == 2 * direction && startY == startRow) {
            int intermediateY = startY + direction;
            if (echiquier.pieces[intermediateY][startX] == null && target == null)
                return true;
        }

        // 3. capture diagonale
        if (Math.abs(dx) == 1 && dy == direction && target != null && target.color != this.color)
            return true;

        // 4. Prise en passant
        if (Math.abs(dx) == 1 && dy == direction && target == null) {
            if (echiquier.lastMovedPiece instanceof Pawn) {
                // Le pion adverse doit avoir avancé de deux cases au dernier coup
                if (Math.abs(echiquier.lastFromY - echiquier.lastToY) == 2
                        && echiquier.lastToY == startY // il est bien à côté du nôtre
                        && echiquier.lastToX == endX) { // colonne où on veut aller
                    return true;
                }
            }
        }

        return false;

    }

    public boolean canPromote(int endY) {
        if ((color == WHITE && endY == 7) || (color == BLACK && endY == 0)) {
            return true;
        }
        return false;
    }

    public int getColor() {
        return this.color;
    }

    public Pawn clone() {
        Pawn nouveau = new Pawn(this.color);
        return nouveau;
    }

    public void deplacementPossible(Echiquier e, int x, int y) {
        this.casePossible = new boolean[e.pieces.length][e.pieces[0].length];
        for (int i = 0; i < e.pieces.length; i++) {
            for (int j = 0; j < e.pieces[i].length; j++) {
                this.casePossible[i][j] = this.isMovementValid(x, y, j, i, e);
            }
        }
    }

}
