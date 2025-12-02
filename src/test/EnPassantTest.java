package test;

import org.junit.jupiter.api.Test;
import main.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class EnPassantTest{
    Echiquier ech;

    @BeforeEach
    void setUp() {
        ech = new Echiquier();
    }

    @Test
    void white_enPassant_ok() {
        Pawn whitePawn = new Pawn(Piece.WHITE);
        Pawn blackPawn = new Pawn(Piece.BLACK);

        // Pion blanc en e5 (x=4, y=4)
        ech.pieces[4][4] = whitePawn;

        // Pion noir vient de faire un double pas de d7 (x=3,y=6) à d5 (x=3,y=4)
        ech.pieces[4][3] = blackPawn;
        ech.lastFromX = 3; ech.lastFromY = 6;
        ech.lastToX   = 3; ech.lastToY   = 4;
        ech.lastMovedPiece = blackPawn;

        // Le blanc peut capturer en passant en d6 (x=3, y=5)
        assertTrue(whitePawn.isMovementValid(4, 4, 3, 5, ech));
    }

    @Test
    void white_enPassant_fail_if_not_lastMove() {
        Pawn whitePawn = new Pawn(Piece.WHITE);
        Pawn blackPawn = new Pawn(Piece.BLACK);

        ech.pieces[4][4] = whitePawn;   // blanc en e5
        ech.pieces[4][3] = blackPawn;   // noir en d5, mais pas via un double pas

        // Simuler que le dernier coup n’était pas ce pion noir
        ech.lastFromX = 0; ech.lastFromY = 1;
        ech.lastToX   = 0; ech.lastToY   = 2;
        ech.lastMovedPiece = new Pawn(Piece.BLACK);

        // Le blanc NE peut PAS capturer en passant
        assertFalse(whitePawn.isMovementValid(4, 4, 3, 5, ech));
    }

    @Test
    void black_enPassant_ok() {
        Pawn blackPawn = new Pawn(Piece.BLACK);
        Pawn whitePawn = new Pawn(Piece.WHITE);

        // Pion noir en d4 (x=3, y=3)
        ech.pieces[3][3] = blackPawn;

        // Pion blanc vient de faire un double pas de e2 (y=1,x=4) à e4 (y=3,x=4)
        ech.pieces[3][4] = whitePawn;
        ech.lastFromX = 4; ech.lastFromY = 1;
        ech.lastToX   = 4; ech.lastToY   = 3;
        ech.lastMovedPiece = whitePawn;

        // Le noir peut capturer en passant en e3 (y=2, x=4)
        assertTrue(blackPawn.isMovementValid(3, 3, 4, 2, ech));
    }

}
