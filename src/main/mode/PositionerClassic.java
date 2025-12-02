package main.mode;

import main.*;

public class PositionerClassic implements Positioner {

    @Override
    public void position(Echiquier e) {
        // Pions
        for (int i = 0; i < 8; i++) {
            e.addPiece(new Pawn(Piece.WHITE), i, 1);
            e.addPiece(new Pawn(Piece.BLACK), i, 6);
        }
        // Rangée blanche
        e.addPiece(new Rook(Piece.WHITE),   0, 0);
        e.addPiece(new Knight(Piece.WHITE), 1, 0);
        e.addPiece(new Bishop(Piece.WHITE), 2, 0);
        e.addPiece(new Queen(Piece.WHITE),  3, 0);
        e.addPiece(new King(Piece.WHITE),   4, 0);
        e.addPiece(new Bishop(Piece.WHITE), 5, 0);
        e.addPiece(new Knight(Piece.WHITE), 6, 0);
        e.addPiece(new Rook(Piece.WHITE),   7, 0);

        // Rangée noire
        e.addPiece(new Rook(Piece.BLACK),   0, 7);
        e.addPiece(new Knight(Piece.BLACK), 1, 7);
        e.addPiece(new Bishop(Piece.BLACK), 2, 7);
        e.addPiece(new Queen(Piece.BLACK),  3, 7);
        e.addPiece(new King(Piece.BLACK),   4, 7);
        e.addPiece(new Bishop(Piece.BLACK), 5, 7);
        e.addPiece(new Knight(Piece.BLACK), 6, 7);
        e.addPiece(new Rook(Piece.BLACK),   7, 7);
    }
}
