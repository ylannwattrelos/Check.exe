package main;

public class King extends Piece {

    public King(int color) {
        super("king", color, "♚", "♔");
    }

    @Override
    protected String getSymbol() {
        return this.symbol[color];
    }

    @Override
    public boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier) {
        // 1) bornes
        if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8)
            return false;

        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);

        // 2) mouvement classique du roi (1 case max dans chaque direction)
        if (dx == 0 && dy == 0)
            return false;
        if (dx <= 1 && dy <= 1) {
            Piece target = echiquier.pieces[endY][endX];
            if (target != null && target.color == this.color)
                return false;
            return true;
        }

        // 3) cas particulier : le roque (dx == 2, dy == 0)
        if (dy == 0 && dx == 2) {
            // le roi ne doit pas avoir bougé
            if (this.hasMoved)
                return false;

            int direction = (endX > startX) ? 1 : -1;
            int rookX = (direction == 1) ? 7 : 0; // tour sur h ou a
            Piece rook = echiquier.pieces[startY][rookX];

            // Vérifier la tour
            if (rook == null || !(rook instanceof Rook) || rook.color != this.color || rook.hasMoved)
                return false;

            // Vérifier que toutes les cases entre roi et tour sont vides
            for (int x = startX + direction; x != rookX; x += direction) {
                if (echiquier.pieces[startY][x] != null)
                    return false;
            }

            // (optionnel mais plus réaliste : vérifier que le roi ne traverse pas une case attaquée)
            return true;
        }

        return false;
    }


    public King clone() {
        King nouveau = new King(this.color);
        return nouveau;
    }


    @Override
    public void deplacementPossible(Echiquier e, int x, int y) {
        this.casePossible = new boolean[e.pieces.length][e.pieces[1].length];
        for (int i = 0; i < this.casePossible.length; i++) {
            for (int j = 0; j < this.casePossible[1].length; j++) {
                this.casePossible[i][j] = isMovementValid(x, y, j, i, e);
            }
        }
    }

}
