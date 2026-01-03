package main.pieces;

import java.util.ArrayList;

import main.Echiquier;
import main.Piece;

public class Rook extends Piece {

    public Rook(int color) {
        super("rook", color, "♜", "♖");
    }

    /**
     * Retourne les pièces STRICTEMENT entre (startX,startY) et (endX,endY).
     * Si le mouvement n'est pas rectiligne, retourne une liste vide.
     */
    public ArrayList<Piece> getPathPieces(int startX, int startY, int endX, int endY, Echiquier echiquier) {
        ArrayList<Piece> between = new ArrayList<>();

        int dx = endX - startX;
        int dy = endY - startY;

        // Déplacement non rectiligne -> pas une tour
        if (dx != 0 && dy != 0) return between;

        // Même case
        if (dx == 0 && dy == 0) return between;

        // Mouvement vertical (même colonne)
        if (dx == 0) {
            int step = (dy > 0) ? 1 : -1;
            for (int y = startY + step; y != endY; y += step) {
                Piece t = echiquier.pieces[y][startX]; // row=y, col=x
                if (t != null) between.add(t);
            }
            return between;
        }

        // Mouvement horizontal (même ligne)
        // (dy == 0)
        int step = (dx > 0) ? 1 : -1;
        for (int x = startX + step; x != endX; x += step) {
            Piece t = echiquier.pieces[startY][x]; // row=y, col=x
            if (t != null) between.add(t);
        }
        return between;
    }

    @Override
    public boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier) {
        // bornes
        if (endX < 0 || endX >= Echiquier.SIZE || endY < 0 || endY >= Echiquier.SIZE) return false;

        int dx = endX - startX;
        int dy = endY - startY;

        // Doit être rectiligne et pas "immobile"
        if (!((dx == 0) ^ (dy == 0))) return false;

        // Destination alliée interdite (sécurité, même si re-vérifiée ailleurs)
        Piece dst = echiquier.pieces[endY][endX];
        if (dst != null && dst.color == this.color) return false;

        // Aucune pièce entre start et end
        if (!getPathPieces(startX, startY, endX, endY, echiquier).isEmpty()) return false;

        return true;
    }

    @Override
    protected String getSymbol() {
        return this.symbol[color];
    }

    public Rook clone() {
        Rook nouveau = new Rook(this.color);
        return nouveau;
    }

    @Override
    public void deplacementPossible(Echiquier e, int x, int y) {
        // casePossible[indexY][indexX]
        this.casePossible = new boolean[Echiquier.SIZE][Echiquier.SIZE];

        for (int yy = 0; yy < Echiquier.SIZE; yy++) {
            for (int xx = 0; xx < Echiquier.SIZE; xx++) {
                if (xx == x && yy == y) { // même case -> faux
                    this.casePossible[yy][xx] = false;
                    continue;
                }
                // on s'appuie sur isMovementValid qui gère rectiligne + chemin libre + allié en cible
                this.casePossible[yy][xx] = this.isMovementValid(x, y, xx, yy, e);
            }
        }
    }
}
