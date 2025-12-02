package test;

import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KingTest {

    Echiquier ech;
    King king;

    @BeforeEach
    void setUp() {
        ech = new Echiquier();
        king = new King(Piece.WHITE); // roi blanc
        ech.pieces[4][4] = king; // e5
    }

    @Test
    void move_one_square_up_ok() {
        assertTrue(king.isMovementValid(4, 4, 4, 5, ech));
    }

    @Test
    void move_one_square_down_ok() {
        assertTrue(king.isMovementValid(4, 4, 4, 3, ech));
    }

    @Test
    void move_one_square_diagonal_ok() {
        assertTrue(king.isMovementValid(4, 4, 5, 5, ech));
    }

    @Test
    void move_two_squares_horizontal_ko() {
        assertFalse(king.isMovementValid(4, 4, 6, 4, ech));
    }

    @Test
    void move_two_squares_vertical_ko() {
        assertFalse(king.isMovementValid(4, 4, 4, 6, ech));
    }

    @Test
    void stay_in_place_ko() {
        assertFalse(king.isMovementValid(4, 4, 4, 4, ech));
    }

    @Test
    void capture_enemy_ok() {
        ech.pieces[5][4] = new Pawn(Piece.BLACK); // pièce noire devant le roi
        assertTrue(king.isMovementValid(4, 4, 4, 5, ech));
    }

    @Test
    void capture_ally_ko() {
        ech.pieces[5][4] = new Pawn(Piece.WHITE); // pièce blanche devant le roi
        assertFalse(king.isMovementValid(4, 4, 4, 5, ech));
    }

    @Test
    void move_off_board_ko() {
        King k = new King(Piece.WHITE);
        ech.pieces[0][0] = k; // a1
        assertFalse(k.isMovementValid(0, 0, -1, 0, ech)); // hors plateau
        assertFalse(k.isMovementValid(0, 0, 0, -1, ech));
    }

    // test lié au Roque
    @Test
    void castle_kingside_ok() {
        King k = new King(Piece.WHITE);
        Rook r = new Rook(Piece.WHITE);
        ech.pieces[0][4] = k; // roi e1
        ech.pieces[0][7] = r; // tour h1

        assertTrue(k.isMovementValid(4, 0, 6, 0, ech)); // petit roque
    }

    @Test
    void castle_queenside_ok() {
        King k = new King(Piece.WHITE);
        Rook r = new Rook(Piece.WHITE);
        ech.pieces[0][4] = k; // roi e1
        ech.pieces[0][0] = r; // tour a1

        assertTrue(k.isMovementValid(4, 0, 2, 0, ech)); // grand roque
    }

    @Test
    void castle_blocked_by_piece_ko() {
        King k = new King(Piece.WHITE);
        Rook r = new Rook(Piece.WHITE);
        Pawn p = new Pawn(Piece.WHITE);

        ech.pieces[0][4] = k; // roi e1
        ech.pieces[0][7] = r; // tour h1
        ech.pieces[0][5] = p; // pièce entre roi et tour

        assertFalse(k.isMovementValid(4, 0, 6, 0, ech)); // impossible
    }

    @Test
    void castle_rook_moved_ko() {
        King k = new King(Piece.WHITE);
        Rook r = new Rook(Piece.WHITE);
        ech.pieces[0][4] = k; // roi e1
        ech.pieces[0][7] = r; // tour h1

        r.hasMoved = true; // tour déjà déplacée

        assertFalse(k.isMovementValid(4, 0, 6, 0, ech));
    }

    @Test
    void castle_king_moved_ko() {
        King k = new King(Piece.WHITE);
        Rook r = new Rook(Piece.WHITE);
        ech.pieces[0][4] = k; // roi e1
        ech.pieces[0][7] = r; // tour h1

        k.hasMoved = true; // roi déjà déplacé

        assertFalse(k.isMovementValid(4, 0, 6, 0, ech));
    }
    }
