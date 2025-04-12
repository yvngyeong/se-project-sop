package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NormalNodeTest {

    @Test
    void testNormalNodeConstructor() {
        NormalNode node = new NormalNode(42);
        assertEquals(42, node.getNodeID(), "NormalNode는 Node의 생성자(super)를 통해 ID를 설정해야 합니다.");
    }

    @Test
    void testAddAndRemovePieceDelegatedToNode() {
        NormalNode node = new NormalNode(1);
        Piece piece = new Piece(1, 2, 0);

        node.add(piece); // Node의 add() 메서드 상속 사용
        assertTrue(node.getOwnedPieces().contains(piece), "Piece는 정상적으로 추가되어야 합니다.");

        node.remove(piece); // Node의 remove() 메서드 상속 사용
        assertFalse(node.getOwnedPieces().contains(piece), "Piece는 정상적으로 제거되어야 합니다.");
    }
}
