package main;

import java.util.Scanner;

import main.pieces.Bishop;
import main.pieces.King;
import main.pieces.Knight;
import main.pieces.Pawn;
import main.pieces.Queen;
import main.pieces.Rook;

public class Echiquier {

    public static final int SIZE = 8;
    public Piece[][] pieces;
    public final Scanner sc;

    // Pour suivre le dernier coup (utile pour la prise en passant)
    public int lastFromX = -1, lastFromY = -1;
    public int lastToX = -1, lastToY = -1;
    public Piece lastMovedPiece = null;

    public Echiquier() {
        this.pieces = new Piece[SIZE][SIZE];
        this.sc = new Scanner(System.in);
    }

    // ===================== Affichages =====================

    @Override
    public String toString() {
        String[][] cells = new String[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Piece p = pieces[y][x];
                cells[y][x] = (p != null) ? p.getSymbol() : ".";
            }
        }
        String[] files = new String[] {"a","b","c","d","e","f","g","h"};
        return TextUI.drawBoard(cells, files, null, true, true);
    }

    /**
     * Affichage avec surlignage à partir d’une case interne (sélection). SANS
     * casePossible.
     */
    public String toString(int selX, int selY) {
        if (!inBounds(selX, selY))
            return toString();
        Piece sel = this.pieces[selY][selX];
        if (sel == null)
            return toString();

        boolean[][] possible = new boolean[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Piece at = pieces[row][col];
                if (at != null && at.color == sel.color) {
                    possible[row][col] = false;
                } else {
                    possible[row][col] = sel.isMovementValid(selX, selY, col, row, this);
                }
            }
        }

        String[][] cells = new String[SIZE][SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Piece at = pieces[y][x];
                if (possible[y][x]) {
                    String mark = "x"; // middle dot keeps width stable
                    if (at == null) cells[y][x] = "\u001b[32m" + mark + "\u001b[0m"; // green dot
                    else cells[y][x] = "\u001b[31m" + at.getSymbol() + "\u001b[0m"; // red capture
                } else {
                    cells[y][x] = (at != null) ? at.getSymbol() : ".";
                }
            }
        }
        String[] files = new String[] {"a","b","c","d","e","f","g","h"};
        return TextUI.drawBoard(cells, files, null, true, true);
    }

    // ===================== NOUVEAU : AFFICHAGE EN PERSPECTIVE
    // =====================

    public static String normalizeOrientation(String o) {
        return "black".equalsIgnoreCase(o) ? "black" : "white";
    }

    public Echiquier copy() {
        Echiquier nouveau = new Echiquier();

        nouveau.pieces = new Piece[this.pieces.length][this.pieces[0].length];
        for (int y = 0; y < this.pieces[0].length; y++) {
            nouveau.pieces[y] = this.pieces[y].clone();
        }
        for(int x = 0 ; x < 8 ; x++){
            for(int y = 0 ; y < 8 ; y++){
                if(this.pieces[x][y] != null){        
                    if(this.pieces[x][y].name.equals("king")){
                        this.pieces[x][y] = this.pieces[x][y].clone();
                    }       
                    if(this.pieces[x][y].name.equals("rook")){
                        this.pieces[x][y] = this.pieces[x][y].clone();
                    }       
                }
            }
        }
        return nouveau;
    }

    /**
     * Convertit des coords "utilisateur" (perspective du joueur) vers coords
     * internes.
     */
    public static int[] userToInternal(int[] xyUser, String orientation) {
        if (xyUser == null)
            return null;
        orientation = normalizeOrientation(orientation);
        int x = xyUser[0], y = xyUser[1];
        if ("black".equals(orientation)) {
            return new int[] { 7 - x, 7 - y };
        }
        return new int[] { x, y };
    }

    /**
     * Convertit des coords internes vers coords "utilisateur" (perspective du
     * joueur).
     */
    public static int[] internalToUser(int[] xyInt, String orientation) {
        if (xyInt == null)
            return null;
        orientation = normalizeOrientation(orientation);
        int x = xyInt[0], y = xyInt[1];
        if ("black".equals(orientation)) {
            return new int[] { 7 - x, 7 - y };
        }
        return new int[] { x, y };
    }

    /** Affiche le plateau selon la perspective (white: normal, black: inversé). */
    public String toStringPerspective(String orientation) {
        orientation = normalizeOrientation(orientation);
        String[][] cells = new String[SIZE][SIZE];
        for (int dispY = 7; dispY >= 0; dispY--) {
            for (int dispX = 0; dispX < 8; dispX++) {
                int row = "black".equals(orientation) ? (7 - dispY) : dispY;
                int col = "black".equals(orientation) ? (7 - dispX) : dispX;
                Piece at = pieces[row][col];
                cells[dispY][dispX] = (at != null) ? at.getSymbol() : ".";
            }
        }
        String[] files = new String[] {"a","b","c","d","e","f","g","h"};
        return TextUI.drawBoard(cells, files, null, true, true);
    }

    /** Affiche avec surlignage selon perspective. */
    public String toStringPerspective(int selX, int selY, String orientation) {
        orientation = normalizeOrientation(orientation);
        Piece sel = inBounds(selX, selY) ? this.pieces[selY][selX] : null;
        if (sel == null)
            return toStringPerspective(orientation);

        boolean[][] possible = new boolean[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Piece at = pieces[row][col];
                if (at != null && at.color == sel.color) {
                    possible[row][col] = false;
                } else {
                    possible[row][col] = sel.isMovementValid(selX, selY, col, row, this);
                }
            }
        }

        String[][] cells = new String[SIZE][SIZE];
        for (int dispY = 7; dispY >= 0; dispY--) {
            for (int dispX = 0; dispX < 8; dispX++) {
                int row = "black".equals(orientation) ? (7 - dispY) : dispY;
                int col = "black".equals(orientation) ? (7 - dispX) : dispX;
                Piece at = pieces[row][col];
                if (possible[row][col]) {
                    String mark = "·";
                    if (at == null) cells[dispY][dispX] = "\u001b[32m" + mark + "\u001b[0m";
                    else cells[dispY][dispX] = "\u001b[31m" + at.getSymbol() + "\u001b[0m";
                } else {
                    cells[dispY][dispX] = (at != null) ? at.getSymbol() : ".";
                }
            }
        }
        String[] files = new String[] {"a","b","c","d","e","f","g","h"};
        return TextUI.drawBoard(cells, files, null, true, true);
    }

    // ===================== Utils =====================

    public static boolean inBounds(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    public static int[] parseSquare(String s) {
        if (s == null)
            return null;
        s = s.trim().toLowerCase();
        if (!s.matches("^[a-h][1-8]$"))
            return null;
        int x = s.charAt(0) - 'a';
        int y = s.charAt(1) - '1';
        return new int[] { x, y };
    }

    public String askSquare(String prompt) {
        while (true) {
            System.out.print(prompt);
            // Attendre calmement qu'une entrée arrive, sans réimprimer le prompt
            while (!sc.hasNext()) {
                try { Thread.sleep(150); } catch (InterruptedException ignored) {}
            }
            String token = sc.next().trim();
            if(token.equals("quitter")){
                System.exit(0);
            }
            if(token.equals("autoechec")){
                autoEchec();
            }
            int[] xy = parseSquare(token);
            if (xy != null)
                return token.toLowerCase();
            System.out.println("Coordonnées invalides. Format attendu : a1..h8.");
        }
    }

    private void autoEchec(){
        moveAPiece(this, new int[]{4, 1}, new int[]{4, 3}, true);
        moveAPiece(this, new int[]{3, 0}, new int[]{5, 2}, true);
        moveAPiece(this, new int[]{5, 0}, new int[]{2, 3}, true);
        System.out.println(this);
    }

    // Accès aux pièces

    public void addPiece(Piece piece, int x, int y) {
        if (inBounds(x, y))
            pieces[y][x] = piece;
    }

    public Piece getPiece(char file, int rank) {
        int x = Character.toLowerCase(file) - 'a';
        int y = rank - 1;
        if (!inBounds(x, y))
            return null;
        return pieces[y][x];
    }

    public static Piece getPiece(Echiquier ech, String square) {
        int[] xy = parseSquare(square);
        if (xy == null)
            return null;
        return ech.pieces[xy[1]][xy[0]];
    }

    // public Echiquier copy(){
    // Echiquier nouveau = new Echiquier();
    // // copie profonde de la matrice (mais pas des objets Piece eux-mêmes)
    // for (int r = 0; r < SIZE; r++) {
    // for (int c = 0; c < SIZE; c++) {
    // nouveau.pieces[r][c] = this.pieces[r][c];
    // }
    // }
    // return nouveau;
    // }

    // --- Promotion utilitaire ---
    public void promotePawn(Pawn pawn, int x, int y) {
        System.out.print("Promotion ! Choisissez une pièce (Q = Dame, R = Tour, B = Fou, K = Cavalier) : ");
        String choix = sc.nextLine().trim().toUpperCase();
        Piece newPiece;

        switch (choix) {
            case "R":
                newPiece = new Rook(pawn.getColor());
                break;
            case "B":
                newPiece = new Bishop(pawn.getColor());
                break;
            case "K":
                newPiece = new Knight(pawn.getColor());
                break;
            case "Q":
            default:
                newPiece = new Queen(pawn.getColor());
                break;
        }
        pieces[y][x] = newPiece;
    }

    // ===================== Déplacements =====================

    /** Version sécurisée (bornes, alliés, validité) */
    public static boolean moveAPiece(Echiquier ech, int[] from, int[] to, boolean silent) {
        int fromX = from[0], fromY = from[1], toX = to[0], toY = to[1];

        if (!inBounds(fromX, fromY) || !inBounds(toX, toY))
            return false;

        Piece piece = ech.pieces[fromY][fromX];
        if (piece == null) {
            if(!silent) System.out.println("Aucune pièce en " + (char) ('a' + fromX) + (fromY + 1));
            return false;
        }

        // Empêcher de capturer une pièce alliée
        Piece target = ech.pieces[toY][toX];
        if (target != null && target.color == piece.color) {
            if(!silent) System.out.println("Case d'arrivée occupée par une pièce alliée.");
            return false;
        }

        if (!piece.isMovementValid(fromX, fromY, toX, toY, ech)) {
            if(!silent) System.out.println("Mouvement invalide pour " + piece.name);
            return false;
        }

        // Cas spécial : ROQUE
        if (piece instanceof King && Math.abs(toX - fromX) == 2 && fromY == toY) {
            int direction = (toX > fromX) ? 1 : -1;
            int rookStartX = (direction == 1) ? 7 : 0;
            int rookEndX = (direction == 1) ? toX - 1 : toX + 1;

            Piece rook = ech.pieces[fromY][rookStartX];
            if (rook != null && rook instanceof Rook) {
                // Déplacer la tour
                ech.pieces[fromY][rookEndX] = rook;
                rook.hasMoved = true;
                ech.pieces[fromY][rookStartX] = null;
            }
        }
        
        boolean isPriseEnPassant = false;

        if (piece instanceof Pawn && Math.abs(toX - fromX) == 1 && ech.pieces[toY][toX] == null) {
        // C'est potentiellement une prise en passant
            if (ech.lastMovedPiece instanceof Pawn && 
                Math.abs(ech.lastFromY - ech.lastToY) == 2 &&
                ech.lastToY == fromY && ech.lastToX == toX) {
                isPriseEnPassant = true;
            }
        }

        ech.pieces[fromY][fromX] = null;
        ech.pieces[toY][toX] = piece;
        piece.hasMoved = true;

        if (isPriseEnPassant) {
            ech.pieces[fromY][toX] = null; // Supprimer le pion pris en passant
        }

        ech.lastMovedPiece = piece;
        ech.lastFromX = fromX;
        ech.lastFromY = fromY;
        ech.lastToX = toX;
        ech.lastToY = toY;

        if (piece instanceof Pawn) {
            Pawn pawn = (Pawn) piece;
            boolean lastRank = (pawn.color == Piece.WHITE && toY == 7) || (pawn.color == Piece.BLACK && toY == 0);
            if (lastRank) {
                ech.promotePawn(pawn, toX, toY);
            }
        }
        return true;
    }

    // ===================== Saisie mouvement (conserve doAMove original, mais safe)
    // =====================

    public int[] getCoordonnéesDépartValide(Joueur j, String msg) {

        while (true) {
            String coordonée;
        int[] a;
        int x, y;
        coordonée = askSquare(j.getName() + " ," + msg);
        a = parseSquare(coordonée);
        if (a == null) { System.out.println("Coordonnées invalides."); continue; }
        x = a[0];
        y = a[1];
        if (this.pieces[y][x] == null || this.pieces[y][x].color != j.color) {
            System.out.println("Aucune pièce à vous sur cette case.");
            continue;
        }
        if (this.pieces[y][x].estBloque(this, x, y)) {
            System.out.println("Cette pièce ne peut pas bouger. Choisissez une autre pièce.");
        }
        while (!inBounds(x, y) || this.pieces[y][x] == null || this.pieces[y][x].color != j.color
                || this.pieces[y][x].estBloque(this, x, y)) {
                     
            coordonée = askSquare(j.getName() + " ,la case choisie est invalide, " + msg);
            a = parseSquare(coordonée);
            if (a == null) { System.out.println("Coordonnées invalides."); continue; }
            x = a[0];
            y = a[1];
        }
        this.pieces[y][x].deplacementPossible(this, x, y);

            // Optionnel : calculer le masque une fois (utile si tu gardes estBloque())
            // try {
            //     pc.deplacementPossible(this, x, y);
            //     System.out.println("Cette pièce est bloquée? : " + pc.estBloque(this, x, y));

            //     if (pc.estBloque(this, x, y)) {
            //         System.out.println("Cette pièce est bloquée.");
            //         continue;
            //     }
            // } catch (Exception ignored) {
            //     // si certaines pièces n'implémentent pas estBloque(), on ignore
            // }
            return a;
        }

        
    }

    // String coordonée;
    // int[] a;
    // int x, y;
    // coordonée = askSquare(j.getName() + " ," + msg);
    // a = parseSquare(coordonée);
    // x = a[0];
    // y = a[1];
    // while (!inBounds(x, y) || this.pieces[y][x] == null ||
    // this.pieces[y][x].color != j.color
    // || this.pieces[y][x].estBloque(this, x, y)) {
    // coordonée = askSquare(j.getName() + " ,la case choisie est invalide, " +
    // msg);
    // a = parseSquare(coordonée);
    // x = a[0];
    // y = a[1];
    // }
    // this.pieces[y][x].deplacementPossible(this, x, y);
    // return a;
    // }

    public int[] getCoordonnéesArrivéValide(Joueur j, int x, int y, String msg) {
        Piece p = this.pieces[y][x];
        while (true) {
            String coordonée = askSquare(j.getName() + " ," + msg);
            int[] a = parseSquare(coordonée);
            if (a == null)
                continue;
            int xB = a[0], yB = a[1];
            if (!inBounds(xB, yB))
                continue;

            Piece dst = this.pieces[yB][xB];
            if (dst != null && p != null && dst.color == p.color) {
                System.out.println("Case ciblée occupée par un allié.");
                continue;
            }

            // On valide avec isMovementValid plutôt que p.casePossible[..]
            if (p != null && p.isMovementValid(x, y, xB, yB, this)) {
                return a;
            }
            System.out.println("Coup invalide pour cette pièce.");
        }
    }

    /** Variante plus générique si besoin */
    public int[] getCoordonnéesArrivéeValide2(Joueur j, String msg, int[] from) {
        while (true) {
            String token = askSquare(j.getName() + " ," + msg);
            int[] xy = parseSquare(token);
            if (xy == null || !inBounds(xy[0], xy[1])) {
                System.out.println("Coordonnées invalides.");
                continue;
            }


            // on interdit juste d'atterrir sur un allié (le reste sera validé par
            // isMovementValid/moveAPiece)


            Piece src = this.pieces[from[1]][from[0]];
            Piece dst = this.pieces[xy[1]][xy[0]];
            if (dst != null && src != null && dst.color == src.color) {
                System.out.println("Case ciblée occupée par un allié.");
                continue;
            }
            return xy;
        }
    }

    /**
     * Conserve la signature et le flux original, avec affichage surligné sans NPE.
     */
    public boolean doAMove(Joueur joueur) {
        int[] from;
        int[] to;
        do {
            from = this.getCoordonnéesDépartValide(joueur, "entrez la case de départ (ex: a2): ");
            // Affiche avec surlignage en coordonnées internes (mode classique non inversé)
            System.out.println(this.toString(from[0], from[1]));
            to = this.getCoordonnéesArrivéValide(joueur, from[0], from[1], "entrez la case d'arrivée (ex: a4): ");
            // tant que le roi reste en échec après simulation, on redemande
        } while (stillChecked(this, from, to, joueur));
        return moveAPiece(this, from, to, false);
    }

            
        //     from = this.getCoordonnéesDépartValide(joueur, "entrez la case de départ (ex: a2): ");
        //     Piece p = this.pieces[from[1]][from[0]];
            
        //     // IMPORTANT : on calcule ses déplacements possibles maintenant
        //     p.deplacementPossible(this, from[0], from[1]);
                   // } ca
            
        //     // Affichage du plateau en mettant en couleur les cases possibles (vert = libre, rouge = capture)
        //     System.out.println(this.toString(from[0], from[1]));
            
        //     to = this.getCoordonnéesArrivéValide(joueur, from[0], from[1], "entrez la case d'arrivée (ex: a4): ");
            
            
        //     // quand le mouvement laisse le joueur sans echec, alors on effectue l'action pour de vrai
        // } while (stillChecked(this, from, to, joueur));
        // return moveAPiece(this, from, to);
        // }

    // from = this.getCoordonnéesDépartValide(joueur, "entrez la case de départ (ex:
    // a2): ");
    // Piece p = this.pieces[from[1]][from[0]];

    // // IMPORTANT : on calcule ses déplacements possibles maintenant
    // p.deplacementPossible(this, from[0], from[1]);

    // // Affichage du plateau en mettant en couleur les cases possibles (vert =
    // libre, rouge = capture)
    // System.out.println(this.toString(from[0], from[1]));

    // to = this.getCoordonnéesArrivéValide(joueur, from[0], from[1], "entrez la
    // case d'arrivée (ex: a4): ");

    // // quand le mouvement laisse le joueur sans echec, alors on effectue l'action
    // pour de vrai
    // } while (stillChecked(this, from, to, joueur));
    // return moveAPiece(this, from, to);
    // }

    public static boolean stillChecked(Echiquier ech, int[] from, int[] to, Joueur joueur) { // renvoie true si le
                                                                                             // joueur est toujours en
                                                                                             // echec à l'issue du
                                                                                             // déplacement
        Echiquier ech2 = ech.copy();
        // silent simulation on a copy
        // Piece piece = getPiece(ech, getSquare(from));
        // System.out.println(getSquare(from));
        // boolean val = false;
        // if (piece != null) {
        //     val = piece.hasMoved;
        //     System.out.println("la pièce a bougé ?" + val);
        // }
        moveAPiece(ech2, from, to, true);
        // if (piece != null) {
        //     piece.hasMoved = val;
        //     System.out.println("et là normalement c'est pareil qu'au dessu : " + piece.hasMoved);
        // }
        return Echiquier.checkChecked(ech2, joueur.color);
    }

    public static String getSquare(int[] coords) {
        String square;
        char x = (char) (coords[0] + 'a');
        square = x + "" + (coords[1] + 1);

        return square;

    }

    // ===================== Démo & Check =====================

    public static void testerQuelquesMovements(Echiquier ech) {
        System.out.println("Blanc a2 -> a4 (2 cases) : " + ech.pieces[1][0].isMovementValid(0, 1, 0, 3, ech));
        System.out.println("Blanc a2 -> a3 (1 case) : " + ech.pieces[1][0].isMovementValid(0, 1, 0, 2, ech));
        System.out.println("Noir  a7 -> a5 (2 cases) : " + ech.pieces[6][0].isMovementValid(0, 6, 0, 4, ech));
        System.out.println("Noir  a7 -> a6 (1 case) : " + ech.pieces[6][0].isMovementValid(0, 6, 0, 5, ech));
    }

    public static void main(String[] args) {
        Echiquier ech = new Echiquier();
        ech.pieces[1][0] = new Pawn(Pawn.WHITE);
        ech.pieces[0][1] = new Pawn(Pawn.WHITE);
        ech.pieces[0][0] = new King(King.WHITE);
        ech.pieces[0][7] = new Rook(Rook.WHITE); // h1
        ech.pieces[4][1] = new King(King.BLACK);
        ech.pieces[7][5] = new Bishop(Bishop.BLACK); // h3
        ech.pieces[5][3] = new Knight(Knight.WHITE); // d3

        System.out.println(ech);
        Joueur j1 = new Joueur("Alice", 1);
        while (true) {
            if (Echiquier.checkChecked(ech, 1)) {
                System.out.println("ATTENTION vous êtes en echec");
                if (Echiquier.checkMat(ech, j1))
                    ;
            }
            ech.doAMove(j1);
            System.out.println(ech);
        }
    }

    public static boolean checkChecked(Echiquier ech, int joueur_color) { // vérifie si le joueur est mis en échec
        int king_x = -1;
        int king_y = -1;

        // Trouver le roi du joueur (utiliser [y][x])
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = ech.pieces[y][x];
                if (piece != null && piece.color == joueur_color && "king".equals(piece.name)) {
                    king_x = x;
                    king_y = y;
                    // System.out.println("Roi trouvé en " + x + "," + y);
                }
            }
        }
        if (king_x == -1 || king_y == -1) {
            // pas de roi trouvé → on considère "pas en échec" pour éviter faux positifs
            return false;
        }

        // Une pièce adverse peut-elle atteindre la case du roi ?
        for (int y2 = 0; y2 < 8; y2++) {
            for (int x2 = 0; x2 < 8; x2++) {
                Piece piece = ech.pieces[y2][x2];
                if (piece != null && piece.color != joueur_color) {
                    try {
                        piece.deplacementPossible(ech, x2, y2);
                        if (piece.casePossible != null && piece.casePossible[king_y][king_x]) {
                            return true;
                        }
                    } catch (Exception ignored) {
                        // si certaines pièces n’implémentent pas correctement, on ignore
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkMat(Echiquier ech, Joueur joueur) {
        for (int x = 0; x < ech.pieces.length; x++) {
            for (int y = 0; y < ech.pieces.length; y++) {
                if (ech.pieces[y][x] != null) {
                    if (ech.pieces[y][x].color == joueur.color) {
                        for (int i = 0; i < ech.pieces.length; i++) {
                            for (int j = 0; j < ech.pieces[0].length; j++) {
                                ech.pieces[y][x].deplacementPossible(ech, x, y);
                                if (ech.pieces[y][x].casePossible[j][i]) {
                                    if (!Echiquier.stillChecked(ech, new int[] { x, y }, new int[] { i, j }, joueur)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
