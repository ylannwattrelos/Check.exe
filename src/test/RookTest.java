package test;

import main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RookTest {
    Echiquier ech;
        
    
    @BeforeEach
    void setUp() {
        ech = new Echiquier();
    }

    @Test
    void white_single_step_forward_ok() {
        ech.pieces[1][0] = new Rook(Piece.WHITE);
        assertTrue(ech.pieces[1][0].isMovementValid(0, 1, 0, 2, ech));
    } 

    @Test
    void white_single_step_diagonal_false() {
        ech.pieces[1][0] = new Rook(Piece.WHITE);
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 1, 2, ech));
    }

    @Test
    void white_diagonal_capture_false() {
        ech.pieces[1][0] = new Rook(Piece.WHITE); 
        ech.pieces[2][1] = new Pawn(Piece.BLACK); 
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 1, 2, ech));
    }

    @Test
    void white_vertical_capture_ok() { //vers le haut pour les blancs
        ech.pieces[1][0] = new Rook(Piece.WHITE); 
        ech.pieces[4][0] = new Pawn(Piece.BLACK); 
        assertTrue(ech.pieces[1][0].isMovementValid(0, 1, 0, 4,ech));

    }
 
    @Test
    void white_vertical_capture_ok_b() { //vers le bas pour les blancs
        ech.pieces[3][0] = new Rook(Piece.WHITE); 
        ech.pieces[1][0] = new Pawn(Piece.BLACK); 
        assertTrue(ech.pieces[3][0].isMovementValid(0, 3, 0, 1,ech));

    }

    @Test
    void white_horizontal_step() {
        ech.pieces[1][0] = new Rook(Piece.WHITE); 
        assertTrue(ech.pieces[1][0].isMovementValid(0, 1, 3, 1,ech));  
    }

    @Test
    void white_horizontal_capture() {
        ech.pieces[1][0] = new Rook(Piece.WHITE); 
        ech.pieces[1][3] = new Pawn(Piece.BLACK); 
        assertTrue(ech.pieces[1][0].isMovementValid(0, 1, 3, 1,ech));  
    }

    @Test
    void white_horizontal_blocked() {
        ech.pieces[1][0] = new Rook(Piece.WHITE); 
        ech.pieces[1][3] = new Pawn(Piece.WHITE); 
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 4, 1,ech));  
    }

    @Test
    void white_horizontal_backstep() {
        ech.pieces[4][0] = new Rook(Piece.WHITE); 
        assertTrue(ech.pieces[4][0].isMovementValid(0, 4, 0, 1,ech));  
    }

    @Test
    void white_dont_moove(){
        ech.pieces[1][0] = new Rook(Piece.WHITE); 
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 0, 1,ech));  

    }

    @Test
    void black_dont_moove(){
        ech.pieces[1][0] = new Rook(Piece.BLACK); 
        assertFalse(ech.pieces[1][0].isMovementValid(0, 1, 0, 1,ech));  
    }

     @Test
    void black_backstep(){
        ech.pieces[6][0] = new Rook(Piece.BLACK); 
        assertTrue(ech.pieces[6][0].isMovementValid(0, 6, 0, 7,ech));  
    } 

    @Test
    void black_single_step_forward_ok(){
        ech.pieces[6][0] = new Rook(Piece.BLACK); 
        assertTrue(ech.pieces[6][0].isMovementValid(0, 6, 0, 5,ech));  

    }

    @Test
    void black_horizontal_moove() {
        ech.pieces[6][0] = new Rook(Piece.BLACK); 
        assertTrue(ech.pieces[6][0].isMovementValid(0, 6, 1, 6,ech));  
    }
}
