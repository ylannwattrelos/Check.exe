package main.multi;

import main.*;

public final class BoardRebuilder {
    private BoardRebuilder(){}

    public static Echiquier rebuildFromMoves(java.util.List<Event> moves) {
        Echiquier e;
        if (!moves.isEmpty() && "init960".equals(moves.get(0).type)) {
            // Extract seed from white name field "name#seed" if present
            String whiteField = moves.get(0).to == null ? "" : moves.get(0).to;
            long seed = System.currentTimeMillis();
            int idx = whiteField.lastIndexOf('#');
            if (idx > 0) {
                try { seed = Long.parseLong(whiteField.substring(idx + 1)); } catch (Exception ignored) {}
            }
            main.mode.PositionerChess960 pos = new main.mode.PositionerChess960(seed);
            e = new Echiquier();
            pos.position(e);
        } else {
            e = BoardSetup.freshBoard();
        }
        for (Event mv : moves) applyMove(e, mv);
        return e;
    }

    public static boolean applyMove(Echiquier e, Event mv) {
        int[] from = Echiquier.parseSquare(mv.from);
        int[] to   = Echiquier.parseSquare(mv.to);
        if (from == null || to == null) return false;

        int fx = from[0], fy = from[1], tx = to[0], ty = to[1];
        Piece piece = e.pieces[fy][fx];
        if (piece == null) return false;

        // couleur cohérente
        String by = mv.by;
        int expectedColor = "white".equals(by) ? Piece.WHITE : Piece.BLACK;
        if (piece.color != expectedColor) return false;

        // case d'arrivée pas alliée
        Piece target = e.pieces[ty][tx];
        if (target != null && target.color == piece.color) return false;

        if (!piece.isMovementValid(fx, fy, tx, ty, e)) return false;
        // Interdit de jouer un coup qui laisse son roi en échec
        if (Echiquier.stillChecked(e, from, to, new Joueur("remote", expectedColor))) return false;

        // Déplace
        e.pieces[fy][fx] = null;
        e.pieces[ty][tx] = piece;

        // Promotion (non interactive)
        if (piece instanceof Pawn) {
            Pawn pawn = (Pawn) piece;
            boolean lastRank = (pawn.color == Piece.WHITE && ty == 7) || (pawn.color == Piece.BLACK && ty == 0);
            if (lastRank) {
                String code = (mv.promo == null) ? "Q" : mv.promo;
                Piece newPiece;
                switch (code) {
                    case "R": newPiece = new Rook(pawn.getColor()); break;
                    case "B": newPiece = new Bishop(pawn.getColor()); break;
                    case "K": newPiece = new Knight(pawn.getColor()); break;
                    case "Q":
                    default:  newPiece = new Queen(pawn.getColor()); break;
                }
                e.pieces[ty][tx] = newPiece;
            }
        }
        return true;
    }

    public static String nextToPlay(java.util.List<Event> moves) {
        // départ: white joue, donc si nombre de moves pair => white; impair => black
        return (moves.size() % 2 == 0) ? "white" : "black";
    }
}
