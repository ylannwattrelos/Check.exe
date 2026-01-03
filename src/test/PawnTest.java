package test;

import main.*;
import main.pieces.Pawn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    Echiquier ech;

    @BeforeEach
    void setUp() {
        ech = new Echiquier();
    }

    @Test
    void white_single_step_forward_ok() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        assertTrue(ech.pieces[1][0].isMovementValid(0, 1, 0, 2, ech));
    }

    @Test
    void white_double_step_from_start_ok_if_clear() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        // a3 et a4 vides
        assertTrue(ech.pieces[1][0].isMovementValid(0, 1, 0, 3, ech));
    }

    @Test
    void white_double_step_blocked_intermediate_ko() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        ech.pieces[2][0] = new Pawn(Piece.BLACK); // bloque a3
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 0, 3, ech));
    }

    @Test
    void white_forward_into_occupied_ko() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        ech.pieces[2][0] = new Pawn(Piece.BLACK); // a3 occupé
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 0, 2, ech));
    }

    @Test
    void white_diagonal_capture_ok() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        ech.pieces[2][1] = new Pawn(Piece.BLACK); // b3
        assertTrue(ech.pieces[1][0].isMovementValid(0, 1, 1, 2, ech));
    }

    @Test
    void white_diagonal_no_target_ko() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 1, 2, ech)); // b3 vide
    }

    @Test
    void white_diagonal_same_color_ko() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        ech.pieces[2][1] = new Pawn(Piece.WHITE); // b3 blanc
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 1, 2, ech));
    }

    @Test
    void white_wrong_direction_ko() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 0, 0, ech)); // a1
    }

    @Test
    void white_sideways_ko() {
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 1, 1, ech)); // b2
    }

    @Test
    void black_single_step_forward_ok() {
        ech.pieces[6][0] = new Pawn(Piece.BLACK); // a7
        assertTrue(ech.pieces[6][0].isMovementValid(0, 6, 0, 5, ech)); // a6
    }

    @Test
    void black_double_step_from_start_ok_if_clear() {
        ech.pieces[6][0] = new Pawn(Piece.BLACK); // a7
        assertTrue(ech.pieces[6][0].isMovementValid(0, 6, 0, 4, ech)); // a5
    }

    @Test
    void black_double_blocked_intermediate_ko() {
        ech.pieces[6][0] = new Pawn(Piece.BLACK); // a7
        ech.pieces[5][0] = new Pawn(Piece.WHITE); // a6 bloque
        assertFalse(ech.pieces[6][0].isMovementValid(0, 6, 0, 4, ech)); // a5
    }

    @Test
    void black_diagonal_capture_ok() {
        ech.pieces[6][0] = new Pawn(Piece.BLACK); // a7
        ech.pieces[5][1] = new Pawn(Piece.WHITE); // b6
        assertTrue(ech.pieces[6][0].isMovementValid(0, 6, 1, 5, ech));
    }

    @Test
    void move_off_board_ko() {
        ech.pieces[7][7] = new Pawn(Piece.WHITE); // h8
        // Essai vers h9 (endY=8) hors limites
        assertFalse(ech.pieces[7][7].isMovementValid(7, 7, 7, 8, ech));
    }
}
