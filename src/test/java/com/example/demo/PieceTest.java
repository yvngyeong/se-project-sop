package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void initialPiece_hasCorrectDefaultValues() {
        Piece piece = new Piece(1, 2, 0);

        assertEquals(0, piece.getPosition());
        assertEquals(1, piece.getOwnerId());
        assertEquals(0, piece.getGroupId());
        assertFalse(piece.isFinished());
        //assertFalse(piece.hasMoved());
    }

    @Test
    void setPosition_marksHasMovedAndFinishCorrectly() {
        Piece piece = new Piece(1, 1, 0);

        piece.setPosition(5);
        //assertTrue(piece.hasMoved());
        assertFalse(piece.isFinished());

    }

    @Test
    void finishMethod_setsIsFinishedTrue() {
        Piece piece = new Piece(1, 1, 0);

        assertFalse(piece.isFinished());
        piece.finish();
        assertTrue(piece.isFinished());
    }

    @Test
    void grouping_setsSameGroupAndAddsToGroupList() {
        Piece p1 = new Piece(1, 1, 0);
        Piece p2 = new Piece(1, 1, 0);

        p1.grouping(p2);

        assertEquals(1, p1.getGroupId());
        assertEquals(1, p2.getGroupId());

        List<Piece> group1 = p1.getGroupedPieces();
        List<Piece> group2 = p2.getGroupedPieces();

        assertTrue(group1.contains(p2));
        assertTrue(group2.contains(p1));
    }

    @Test
    void pushAndPopPreviousPosition_behavesLikeStack() {
        Piece piece = new Piece(1, 1, 0);

        piece.pushPreviousPosition(3);
        piece.pushPreviousPosition(5);

        assertEquals(5, piece.popPreviousPosition());
        assertEquals(3, piece.popPreviousPosition());
        assertEquals(-1, piece.popPreviousPosition()); // 비었을 경우 -1 반환
    }
}
