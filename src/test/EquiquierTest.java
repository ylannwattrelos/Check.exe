package test;

import main.*;
import main.pieces.Pawn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EchiquierTest {

    Echiquier ech;

    @BeforeEach
    void setUp() {
        ech = new Echiquier();
        ech.pieces[1][0] = new Pawn(Piece.WHITE); // a2
        ech.pieces[6][0] = new Pawn(Piece.BLACK); // a7
        ech.pieces[3][4] = new Pawn(Piece.WHITE); // e4
    }

    @Test
    void getPiece_returns_correct_square() {
        assertSame(ech.pieces[1][0], ech.getPiece('a', 2));
        assertNull(ech.getPiece('h', 8)); // vide
    }

    @Test
    void toString_contains_headers_and_symbols() {
        String s = ech.toString();
        assertTrue(s.contains("a b c d e f g h"));
        assertTrue(s.contains("♙")); // pion blanc
        assertTrue(s.contains("♟")); // pion noir
    }


    // void askAPiece_reads_coords_and_returns_piece() {
    //     // place une pièce ciblée
    //     ech.pieces[3][4] = new Pawn(Piece.WHITE); // e4

    //     // Scanner qui simule la saisie "e\n4\n"
    //     Scanner sc = new Scanner("e\n4\n");
    //     //Piece p = ech.askAPiece(sc, 8, 8);

    //     assertNotNull(p);
    //     assertEquals(Piece.WHITE, p.color);
    //     // c'est bien la pièce de e4
    //     assertSame(ech.pieces[3][4], p);
    // }
}
