package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CornerNodeTest {

    @Test
    void testConstructorAndGetNodeID() {
        CornerNode node = new CornerNode(7); // ID 7 할당
        assertEquals(7, node.getNodeID(), "nodeID는 생성자에서 설정한 값이어야 합니다.");
    }

    @Test
    void testAddAndRemovePiece() {
        CornerNode node = new CornerNode(1);
        Piece piece = new Piece(1, 1, 0); // 생성자에 맞는 값 넣기

        node.add(piece);
        assertTrue(node.getOwnedPieces().contains(piece), "말이 추가되어 있어야 합니다.");

        node.remove(piece);
        assertFalse(node.getOwnedPieces().contains(piece), "말이 제거되어 있어야 합니다.");
    }
}
