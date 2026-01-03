package main.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.*;
import main.pieces.Bishop;
import main.pieces.King;
import main.pieces.Knight;
import main.pieces.Pawn;
import main.pieces.Queen;
import main.pieces.Rook;

public class PositionerChess960 implements Positioner {

    private final Random rng;

    /** Aléatoire système */
    public PositionerChess960() { this.rng = new Random(); }
    /** Pour tests (reproductible) */
    public PositionerChess960(long seed) { this.rng = new Random(seed); }

    @Override
    public void position(Echiquier e) {
        int[] back = generateBackRank(); // indices 0..7 -> code de pièce
        // place rangée blanche selon back[]
        for (int file = 0; file < 8; file++) placeWhite(back[file], file, e);
        // pions
        for (int i = 0; i < 8; i++) {
            e.addPiece(new Pawn(Piece.WHITE), i, 1);
            e.addPiece(new Pawn(Piece.BLACK), i, 6);
        }
        // rangée noire = miroir types de back[] (même colonnes)
        for (int file = 0; file < 8; file++) placeBlack(back[file], file, e);
    }

    /** Codes: 0=Rook,1=Knight,2=Bishop,3=Queen,4=King */
    private void placeWhite(int code, int file, Echiquier e) {
        switch (code) {
            case 0: e.addPiece(new Rook(Piece.WHITE), file, 0); break;
            case 1: e.addPiece(new Knight(Piece.WHITE), file, 0); break;
            case 2: e.addPiece(new Bishop(Piece.WHITE), file, 0); break;
            case 3: e.addPiece(new Queen(Piece.WHITE), file, 0); break;
            case 4: e.addPiece(new King(Piece.WHITE), file, 0); break;
            default: break;
        }
    }

    private void placeBlack(int code, int file, Echiquier e) {
        switch (code) {
            case 0: e.addPiece(new Rook(Piece.BLACK), file, 7); break;
            case 1: e.addPiece(new Knight(Piece.BLACK), file, 7); break;
            case 2: e.addPiece(new Bishop(Piece.BLACK), file, 7); break;
            case 3: e.addPiece(new Queen(Piece.BLACK), file, 7); break;
            case 4: e.addPiece(new King(Piece.BLACK), file, 7); break;
            default: break;
        }
    }

    /** Génère une rangée arrière Chess960 valide. */
    private int[] generateBackRank() {
        int[] board = new int[8]; // -1 = vide
        for (int i = 0; i < 8; i++) board[i] = -1;

        // 1) Fous sur couleurs opposées
        int[] dark = {0,2,4,6};
        int[] light = {1,3,5,7};
        int b1 = dark[rng.nextInt(dark.length)];
        int b2 = light[rng.nextInt(light.length)];
        board[b1] = 2; // Bishop
        board[b2] = 2;

        // 2) Reine sur une case restante
        List<Integer> rem = remaining(board);
        int qIdx = rng.nextInt(rem.size());
        board[rem.get(qIdx)] = 3; // Queen

        // 3) Deux cavaliers parmi les restantes
        rem = remaining(board);
        int k1Index = rng.nextInt(rem.size());
        int k1Pos = rem.remove(k1Index);
        int k2Index = rng.nextInt(rem.size());
        int k2Pos = rem.remove(k2Index);
        board[k1Pos] = 1; // Knight
        board[k2Pos] = 1;

        // 4) R, K, R sur les 3 dernières colonnes dans l'ordre R-K-R (roi au centre)
        rem = remaining(board);
        rem.sort(Integer::compareTo);
        board[rem.get(0)] = 0; // Rook
        board[rem.get(1)] = 4; // King
        board[rem.get(2)] = 0; // Rook

        return board;
    }

    private static List<Integer> remaining(int[] board) {
        ArrayList<Integer> r = new ArrayList<>();
        for (int i = 0; i < board.length; i++) if (board[i] == -1) r.add(i);
        return r;
    }
}
