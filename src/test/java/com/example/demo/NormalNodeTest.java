package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

public class NormalNodeTest {
    CornerNode node;
    Piece piece;

    @BeforeEach
    void setUp() {
        node = new CornerNode(7); // ID 7 할당
        piece = new Piece(1, 1, 0);
    }

    @Test
    void checkedID() {
        assertEquals(7, node.getNodeID(), "nodeID는 생성자에서 설정한 값 7이어야 한다");
    }

    @Test
    void testAddAndRemovePiece() {
        node.add(piece);
        assertTrue(node.getOwnedPieces().contains(piece), "말이 추가되어 있어야 합니다.");

        node.remove(piece);
        assertFalse(node.getOwnedPieces().contains(piece), "말이 제거되어 있어야 합니다.");
    }

    @Test
    void testCheckedOwner() {
        node.add(piece);
        // checkOwner 테스트
        int ownerId = node.checkOwner(piece);
        assertEquals(1, ownerId, "말의 소유자 ID는 1이어야 합니다.");

    }
}
