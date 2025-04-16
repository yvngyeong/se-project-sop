package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class PieceTest {

    private Piece piece;

    @BeforeEach
    void setUp() {
        piece = new Piece(1); // ownerId = 1
    }

    @Test
    void testInitialization() {
        assertEquals(0, piece.getPosition());
        assertEquals(1, piece.getOwnerId());
        assertFalse(piece.isFinished());
        assertEquals(0, piece.getGroupId());
    }

    @Test
    void testPositionSetAndGet() {
        piece.setPosition(5);
        assertEquals(5, piece.getPosition());
    }

    @Test
    void testFinish() {
        assertFalse(piece.isFinished());
        piece.finish();
        assertTrue(piece.isFinished());
    }

    @Test
    void testGrouping() {
        Piece another = new Piece(1);

        piece.grouping(another);

        assertEquals(1, piece.getGroupId());
        assertEquals(1, another.getGroupId());

        List<Piece> group1 = piece.getGroupedPieces();
        List<Piece> group2 = another.getGroupedPieces();

        assertTrue(group1.contains(another));
        assertTrue(group2.contains(piece));
    }

    @Test
    void testPushAndPopPreviousPosition() {
        piece.pushPreviousPosition(3);
        piece.pushPreviousPosition(7);

        assertEquals(7, piece.popPreviousPosition());
        assertEquals(3, piece.popPreviousPosition());
        assertEquals(-1, piece.popPreviousPosition()); // 빈 스택
    }
}
