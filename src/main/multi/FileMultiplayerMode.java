package main.multi;

import main.*;
import main.pieces.Pawn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class FileMultiplayerMode {

    private static final Scanner SC = Checkexe.sc; // réutilise le scanner global

    enum Command { NONE, QUIT, RESIGN, DRAW }

    public static void start() {
        StringBuilder content = new StringBuilder();
        content.append("\n  1) Héberger une partie\n");
        content.append("  2) Rejoindre une partie\n\n");
        System.out.println(TextUI.centerBox("Multijoueur", content.toString(), 48));
        int choice = getChoice(1, 2);

        System.out.print(TextUI.center("ID de partie (ex: 1234): ", 80));
        String gameId = safeNext().trim();

        // Mode: classique ou 960 ?
        System.out.println(TextUI.center("Mode de départ: 1) Classique  2) Random (Chess960)", 80));
        int mode = getChoice(1, 2);

        if (choice == 1) {
            System.out.print(TextUI.center("Ton nom (joueur blanc): ", 80));
            String myName = safeNext().trim();
            runAsHost(gameId, myName, mode == 2);
        } else {
            System.out.print(TextUI.center("Ton nom (joueur noir): ", 80));
            String myName = safeNext().trim();
            runAsJoin(gameId, myName);
        }
    }

    private static int getChoice(int min, int max) {
        int input = -1;
        while (input < min || input > max) {
            if (!SC.hasNextInt()) { SC.next(); continue; }
            input = SC.nextInt();
        }
        return input;
    }

    private static String safeNext() {
        while (!SC.hasNext()) { /* attend un token */ }
        return SC.next();
    }

    private static void runAsHost(String gameId, String whiteName, boolean is960) {
        FileGameSync sync = new FileGameSync(gameId);
        try {
            if (is960) {
                long seed = System.currentTimeMillis();
                appendInit960(sync, gameId, whiteName, seed);
            } else {
                sync.hostInit(whiteName);
            }
            // System.out.println(TextUI.center("Fichier créé: " + sync.getGameFile(), 80));
            System.out.println(TextUI.center("En attente du joueur noir (join)...", 80));
            waitForJoin(sync);
            runGameLoop(sync, "white", whiteName);
        } catch (IOException e) {
            System.out.println("Erreur hostInit: " + e.getMessage());
        }
    }

    private static void runAsJoin(String gameId, String blackName) {
        FileGameSync sync = new FileGameSync(gameId);
        try {
            File f = sync.getGameFile();
            if (!f.exists()) {
                System.out.println(TextUI.center("Partie introuvable: " + f, 80));
                return;
            }
            sync.join(blackName);
            System.out.println(TextUI.center("Rejoint la partie: " + f, 80));
            runGameLoop(sync, "black", blackName);
        } catch (IOException e) {
            System.out.println("Erreur join: " + e.getMessage());
        }
    }

    private static void appendInit960(FileGameSync sync, String gameId, String whiteName, long seed) throws IOException {
        // Write a custom init960 line at the top (overwrite file as with hostInit)
        String line = Event.init960(gameId, whiteName, seed).toJsonLine() + "\n";
        java.lang.reflect.Method m = null;
        try {
            m = FileGameSync.class.getDeclaredMethod("safeRewrite", String.class);
            m.setAccessible(true);
            m.invoke(sync, line);
        } catch (Exception e) {
            throw new IOException("init960 failed", e);
        }
    }

    private static void waitForJoin(FileGameSync sync) throws IOException {
        while (true) {
            if (sync.isEnded()) return; // si la partie est déjà finie (abandon)
            List<Event> all = sync.readAll();
            boolean hasJoin = false;
            for (Event e : all) if ("join".equals(e.type)) { hasJoin = true; break; }
            if (hasJoin) break;
            sleep(700);
        }
    }

    private static void runGameLoop(FileGameSync sync, String myColor, String myName) {
        myColor = Echiquier.normalizeOrientation(myColor);

        while (true) {
            try {
                // Fin externe ?
                FileGameSync.EndSummary end = sync.readEndSummary();
                if (end != null) {
                    endScreen(sync, myColor, end);
                    cleanupAndExit(sync);
                    return;
                }

                List<Event> moves = sync.readMoves();

                // Reconstruit le plateau
                Echiquier board = BoardRebuilder.rebuildFromMoves(moves);

                Checkexe.clearScreen();
                System.out.println(board.toStringPerspective(myColor));
                String toPlay = BoardRebuilder.nextToPlay(moves);
                // Check fin de partie (mat / stalemate) du point de vue de 'toPlay'
                int colorToPlay = "white".equals(toPlay) ? Piece.WHITE : Piece.BLACK;
                Joueur jsim = new Joueur("player", colorToPlay);
                if (Echiquier.checkChecked(board, colorToPlay) && Echiquier.checkMat(board, jsim)) {
                    String winner = colorToPlay == Piece.WHITE ? "black" : "white";
                    sync.appendEnd(winner, "mate", "checkmate");
                    FileGameSync.EndSummary end2 = sync.readEndSummary();
                    endScreen(sync, myColor, end2);
                    cleanupAndExit(sync);
                    return;
                }
                System.out.println(TextUI.center("Au trait: " + toPlay + " | Vous êtes: " + myColor , 80));
                System.out.println(TextUI.center("(saisissez: a1..h8  |  'resign' pour abandonner  |  'draw' pour proposer nulle  |  'quit' pour quitter)", 80));

                if (!toPlay.equals(myColor)) {
                    sleep(800);
                    continue;
                }

                // --- Mon tour ---
                Joueur joueurCourant = new Joueur(myName, "white".equals(myColor) ? Piece.WHITE : Piece.BLACK);

                // 1) Lecture départ (perspective + commandes)
                ReadResult rrFrom = readSquareOrCommand(joueurCourant.getName() + " ,entrez la case de départ (ex: a2): ", myColor);
                if (rrFrom.cmd != Command.NONE) {
                    handleCommand(sync, myColor, rrFrom.cmd);
                    continue;
                }
                int[] fromInt = rrFrom.xyInternal;

                // Vérifs de base
                if (fromInt == null || !Echiquier.inBounds(fromInt[0], fromInt[1])) continue;
                Piece p = board.pieces[fromInt[1]][fromInt[0]];
                if (p == null) { System.out.println("Aucune pièce sur cette case."); sleep(700); continue; }
                if (("white".equals(myColor) && p.color != Piece.WHITE) ||
                    ("black".equals(myColor) && p.color != Piece.BLACK)) {
                    System.out.println("Ce n'est pas votre pièce.");
                    sleep(700);
                    continue;
                }

                // 2) Aperçu en couleurs (perspective, sans deplacementPossible)
                System.out.println(board.toStringPerspective(fromInt[0], fromInt[1], myColor));

                // 3) Lecture arrivée (perspective + commandes)
                ReadResult rrTo = readSquareOrCommand(joueurCourant.getName() + " ,entrez la case d'arrivée (ex: a4): ", myColor);
                if (rrTo.cmd != Command.NONE) {
                    handleCommand(sync, myColor, rrTo.cmd);
                    continue;
                }
                int[] toInt = rrTo.xyInternal;

                // 4) Validation non-destructive
                if (!Echiquier.inBounds(toInt[0], toInt[1])) { System.out.println("Cible hors plateau."); sleep(700); continue; }
                Piece dst = board.pieces[toInt[1]][toInt[0]];
                if (dst != null && dst.color == p.color) { System.out.println("Case cible occupée par un allié."); sleep(700); continue; }
                if (!p.isMovementValid(fromInt[0], fromInt[1], toInt[0], toInt[1], board)) {
                    System.out.println(TextUI.center("Coup invalide.", 80));
                    sleep(700);
                    continue;
                }
                // 4bis) Interdit de se laisser en échec (simulation safe via Echiquier.stillChecked)
                if (Echiquier.stillChecked(board, fromInt, toInt, joueurCourant)) {
                    System.out.println(TextUI.center("Coup laisse votre roi en échec.", 80));
                    sleep(700);
                    continue;
                }

                // 5) Promotion éventuelle
                String promo = null;
                if (p instanceof Pawn) {
                    boolean reachLast = (p.color == Piece.WHITE && toInt[1] == 7) || (p.color == Piece.BLACK && toInt[1] == 0);
                    if (reachLast) {
                        System.out.print(TextUI.center("Promotion (Q/R/B/K, défaut Q): ", 80));
                        String in = safeNext().trim().toUpperCase();
                        promo = in.matches("[QRBK]") ? in : "Q";
                    }
                }

                // 6) Re-check du tour juste avant écriture
                int expectedNext = sync.lastSeq() + 1;
                if (!BoardRebuilder.nextToPlay(sync.readMoves()).equals(myColor)) {
                    continue; // l'adversaire a joué pendant la saisie
                }

                // 7) Append move (coords internes -> retransformées en notation standard)
                String fromStd = toSquareStd(fromInt[0], fromInt[1]); // NOTE: standard a1..h8 toujours
                String toStd   = toSquareStd(toInt[0], toInt[1]);

                Event mv = Event.move(expectedNext, myColor, fromStd, toStd, promo, System.currentTimeMillis());
                sync.appendMove(mv);

                sleep(400);

            } catch (IOException ex) {
                System.out.println(TextUI.center("Erreur I/O: " + ex.getMessage(), 80));
                sleep(1000);
            }
        }
    }

    // ===================== Helpers E/S & perspective =====================

    static class ReadResult {
        int[] xyInternal; // coordonnées internes
        Command cmd = Command.NONE;
    }

    private static ReadResult readSquareOrCommand(String prompt, String orientation) {
        ReadResult r = new ReadResult();
        System.out.print(prompt);
        while (!SC.hasNext()) {
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
        String token = SC.next().trim().toLowerCase();

        // commandes
        if ("q".equals(token) || "quit".equals(token) || "exit".equals(token)) { r.cmd = Command.NONE; return r; }
        if ("resign".equals(token) || "abandon".equals(token))                { r.cmd = Command.NONE; return r; }
        if ("draw".equals(token) || "nulle".equals(token))                    { r.cmd = Command.NONE; return r; }

        // coordonnée ?
        int[] xyUser = Echiquier.parseSquare(token);
        if (xyUser == null) {
            System.out.println("Entrée invalide (attendu: a1..h8, ou 'resign' / 'draw' / 'quit').");
            r.cmd = Command.NONE;
            r.xyInternal = null;
            return r;
        }
        r.xyInternal = Echiquier.userToInternal(xyUser, orientation);
        return r;
    }

    private static String toSquareStd(int x, int y) {
        char file = (char) ('a' + x);
        char rank = (char) ('1' + y);
        return "" + file + rank;
    }

    // ===================== Gestion fin de partie =====================

    private static void handleCommand(FileGameSync sync, String myColor, Command cmd) {
        try {
            switch (cmd) {
                case QUIT: {
                    sync.appendEnd("none", "abort", "quit by " + myColor);
                    FileGameSync.EndSummary end = sync.readEndSummary();
                    endScreen(sync, myColor, end);
                    cleanupAndExit(sync);
                    break;
                }
                case RESIGN: {
                    String winner = "white".equals(myColor) ? "black" : "white";
                    sync.appendEnd(winner, "resign", "resign by " + myColor);
                    FileGameSync.EndSummary end = sync.readEndSummary();
                    endScreen(sync, myColor, end);
                    cleanupAndExit(sync);
                    break;
                }
                case DRAW: {
                    sync.appendEnd("none", "draw", "draw offered/accepted");
                    FileGameSync.EndSummary end = sync.readEndSummary();
                    endScreen(sync, myColor, end);
                    cleanupAndExit(sync);
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur fin de partie: " + e.getMessage());
        }
    }

    private static void endScreen(FileGameSync sync, String myColor, FileGameSync.EndSummary end) {
        Checkexe.clearScreen();
        StringBuilder content = new StringBuilder();
        if (end == null) {
            content.append("\n  La partie a été clôturée.\n\n");
        } else {
            content.append("\n  Résultat: ")
                   .append(end.winner.equals("none") ? "égalité" : ("vainqueur: " + end.winner))
                   .append("\n");
            content.append("  Motif: ")
                   .append(end.by)
                   .append(end.reason.isEmpty() ? "" : (" — " + end.reason))
                   .append("\n\n");
        }
        content.append("  Fichier: ").append(sync.getGameFile().getName()).append("\n");
        System.out.println(TextUI.centerBox("FIN DE PARTIE", content.toString(), 56));
        sleep(1200);
    }

    private static void cleanupAndExit(FileGameSync sync) {
        // tenter la suppression (libérer l'ID)
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        sync.deleteGameFileSilently();
    }

    // ===================== Utils =====================

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
