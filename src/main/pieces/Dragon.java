package main.pieces;
import main.*;

public class Dragon extends Piece {
    public Dragon(int color) {

        super("dragon", color, "★", "☆");
    }

    @Override
    public boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier) {
        // bornes
        if (endX < 0 || endX >= 8 || endY < 0 || endY >= 8)
            return false;

        int dx = endX - startX;
        int dy = endY - startY;


        // Empêcher de capturer une pièce alliée
        Piece target = echiquier.pieces[endY][endX];
        if (target != null && target.color == this.color) {
            return false;
        }

        // immobile
        if (dx == 0 && dy == 0) return false;
        
        if(Math.abs(dx) != Math.abs(dy)){
            return (Math.abs(dx) == 1 && Math.abs(dy) == 2) ^ (Math.abs(dx) == 2 && Math.abs(dy) == 1);
        }
        else{
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
    }

    

    public Dragon clone() {
        Dragon nouveau = new Dragon(this.color);
        return nouveau;
    }
    
    public void deplacementPossible(Echiquier e, int x, int y) {
        this.casePossible = new boolean[e.pieces.length][e.pieces[0].length];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                    this.casePossible[i][j] = this.isMovementValid(x, y, j, i, e);
            }
        }
    }

    

    //🐉
}
