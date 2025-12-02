package test;

import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QueenTest {
    Echiquier ech;
    Queen queen;

    @BeforeEach
    void setUp() {
        ech = new Echiquier();
        queen = new Queen(Piece.WHITE); // reine blanche
        ech.pieces[4][4] = queen; // e5
    }

    @Test
    void stayInPlaceKo() {
        assertFalse(queen.isMovementValid(4, 4, 4, 4, ech));
    }

    @Test
    void moveHorizontallyOk() {
        assertTrue(queen.isMovementValid(4, 4, 4, 7, ech)); // e5 → e8
        assertTrue(queen.isMovementValid(4, 4, 4, 1, ech)); // e5 → e2
        assertTrue(queen.isMovementValid(4, 4, 7, 4, ech)); // e5 → h5
        assertTrue(queen.isMovementValid(4, 4, 1, 4, ech)); // e5 → b5
    }

    @Test
    void moveDiagonallyOk() {
        assertTrue(queen.isMovementValid(4, 4, 7, 7, ech)); // e5 → h8
        assertTrue(queen.isMovementValid(4, 4, 1, 1, ech)); // e5 → b2
        assertTrue(queen.isMovementValid(4, 4, 7, 1, ech)); // e5 → h2
        assertTrue(queen.isMovementValid(4, 4, 1, 7, ech)); // e5 → b8
    }

    @Test
    void invalidMoveKo() {
        assertFalse(queen.isMovementValid(4, 4, 5, 6, ech)); // pas droit ni diagonale
        assertFalse(queen.isMovementValid(4, 4, 3, 6, ech));
    }

    @Test
    void captureEnemyOk() {
        ech.pieces[4][7] = new Pawn(Piece.BLACK); // h5
        assertTrue(queen.isMovementValid(4, 4, 4, 7, ech));
    }

    @Test
    void blockedByPieceKo() {
        ech.pieces[5][5] = new Pawn(Piece.WHITE); // f6 bloque diagonale
        assertFalse(queen.isMovementValid(4, 4, 7, 7, ech));
    }

    @Test
    void moveOffBoardKo() {
        Queen q = new Queen(Piece.WHITE);
        ech.pieces[0][0] = q; // a1
        assertFalse(q.isMovementValid(0, 0, -1, 0, ech)); // hors plateau
        assertFalse(q.isMovementValid(0, 0, 0, -1, ech));
    }
}
