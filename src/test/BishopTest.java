package test;

import main.*;
import main.pieces.Bishop;
import main.pieces.Pawn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BishopTest {
    Echiquier ech;
    Bishop bishop;

    @BeforeEach
    void setUp() {
        ech = new Echiquier();
        bishop = new Bishop(Piece.WHITE); // fou blanc
        ech.pieces[4][4] = bishop; // e5
    }

    @Test
    void stayInPlaceKo() {
        assertFalse(bishop.isMovementValid(4, 4, 4, 4, ech));
    }

    @Test
    void moveDiagonallyUpRightOk() {
        assertTrue(bishop.isMovementValid(4, 4, 6, 6, ech)); // g7
    }

    @Test
    void moveDiagonallyUpLeftOk() {
        assertTrue(bishop.isMovementValid(4, 4, 2, 6, ech)); // c7
    }

    @Test
    void moveDiagonallyDownRightOk() {
        assertTrue(bishop.isMovementValid(4, 4, 6, 2, ech)); // g3
    }

    @Test
    void moveDiagonallyDownLeftOk() {
        assertTrue(bishop.isMovementValid(4, 4, 2, 2, ech)); // c3
    }

    @Test
    void moveNonDiagonalKo() {
        assertFalse(bishop.isMovementValid(4, 4, 4, 6, ech)); // vertical
        assertFalse(bishop.isMovementValid(4, 4, 6, 4, ech)); // horizontal
    }

    @Test
    void captureEnemyOk() {
        ech.pieces[6][6] = new Pawn(Piece.BLACK); // g7
        assertTrue(bishop.isMovementValid(4, 4, 6, 6, ech));
    }

    @Test
    void blockedByPieceKo() {
        ech.pieces[5][5] = new Pawn(Piece.WHITE); // f6 bloque diagonale
        assertFalse(bishop.isMovementValid(4, 4, 6, 6, ech));
    }

    @Test
    void moveOffBoardKo() {
        Bishop b = new Bishop(Piece.WHITE);
        ech.pieces[0][0] = b; // a1
        assertFalse(b.isMovementValid(0, 0, -1, 1, ech));
        assertFalse(b.isMovementValid(0, 0, 1, -1, ech));
    }
}