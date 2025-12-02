package test;

import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KnightTest {
    Echiquier ech;
    Knight knight;

    @BeforeEach
    void setUp() {
        ech = new Echiquier();
        knight = new Knight(Piece.WHITE); // cavalier blanc
        ech.pieces[4][4] = knight; // e5
    }

    @Test
    void stayInPlaceKo() {
        assertFalse(knight.isMovementValid(4, 4, 4, 4, ech));
    }

    @Test
    void moveLShapeOk() {
        int[][] moves = {
                { 6, 5 }, { 6, 3 }, { 2, 5 }, { 2, 3 }, // ±2 x ±1 y
                { 5, 6 }, { 3, 6 }, { 5, 2 }, { 3, 2 } // ±1 x ±2 y
        };
        for (int[] m : moves) {
            assertTrue(knight.isMovementValid(4, 4, m[0], m[1], ech));
        }
    }

    @Test
    void invalidMoveKo() {
        int[][] invalid = {
                { 4, 6 }, { 6, 4 }, { 3, 3 }, { 5, 5 } // pas en L
        };
        for (int[] m : invalid) {
            assertFalse(knight.isMovementValid(4, 4, m[0], m[1], ech));
        }
    }

    @Test
    void captureEnemyOk() {
        ech.pieces[6][5] = new Pawn(Piece.BLACK); // position en L
        assertTrue(knight.isMovementValid(4, 4, 6, 5, ech));
    }

    @Test
    void moveOffBoardKo() {
        Knight k = new Knight(Piece.WHITE);
        ech.pieces[0][0] = k; // a1
        int[][] offBoard = {
                { -2, 1 }, { -1, 2 }, { 2, -1 }, { 1, -2 }
        };
        for (int[] m : offBoard) {
            assertFalse(k.isMovementValid(0, 0, m[0], m[1], ech));
        }
    }
}
