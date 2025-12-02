package main;

public abstract class Piece {

    // attr
    public String[] symbol;
    public String name;
    public int color;
    public boolean[][] casePossible;

    public boolean hasMoved = false; // pour le roi et tour

    public static final int BLACK = 1;
    public static final int WHITE = 0;

    public Piece(String name, int color, String s1, String s2) {
        this.name = name;
        this.color = color;
        // colors attr
        this.symbol = new String[2];
        this.symbol[0] = s1;
        this.symbol[1] = s2;
    }

    public Piece(String name, int color, String[] symbol, boolean[][] casePossible) {
        this.name = name;
        this.color = color;
        this.symbol = symbol;
        this.casePossible = casePossible;
    }

    public abstract boolean isMovementValid(int startX, int startY, int endX, int endY, Echiquier echiquier);

    protected String getSymbol() {
        return this.symbol[color];
    }

    public abstract Piece clone();

    public abstract void deplacementPossible(Echiquier e, int x, int y);

    public boolean estBloque(Echiquier e, int x, int y) {
        this.deplacementPossible(e, x, y);
        for (int i = 0; i < this.casePossible.length; i++) {
            for (int j = 0; j < this.casePossible[i].length; j++) {
                if (this.casePossible[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // private String getProperSymbolFrom(String name, String color) {
    // switch (name.toLowerCase()) {
    // case "king":
    // return color.equalsIgnoreCase("white") ? "♚" : "♔";
    // case "queen":
    // return color.equalsIgnoreCase("white") ? "♛" : "♕";
    // case "rook":
    // return color.equalsIgnoreCase("white") ? "♜" : "♖";
    // case "bishop":
    // return color.equalsIgnoreCase("white") ? "♝" : "♗";
    // case "knight":
    // return color.equalsIgnoreCase("white") ? "♞" : "♘";
    // case "pawn":
    // return color.equalsIgnoreCase("white") ? "♟" : "♙";
    // default:
    // return "?";
    // }
    // }
}
