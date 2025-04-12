package com.example.demo;
// HexagonalBoardTest.java

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
// Import the HexagonalBoard class from its package

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HexagonalBoardTest {
    private HexagonalBoard board;

    @BeforeEach
    void setUp() {
        board = new HexagonalBoard();
        board.createNodes();
        board.createEdges();
    }

    @Test
    void testCreateNodes() {
        assertNotNull(board.nodes, "노드 리스트가 null이 아닙니다.");
        assertEquals(44, board.nodes.size(), "노드의 개수는 44이어야 합니다.");
    }

    @Test
    void testCreateEdges() {
        assertNotNull(board.edges, "엣지 맵이 null이 아닙니다.");
        assertEquals(List.of(38), board.edges.get(37), "37번 노드의 엣지는 38이어야 합니다.");
        assertEquals(List.of(30), board.edges.get(38), "38번 노드의 엣지는 30이어야 합니다.");
        assertEquals(List.of(35), board.edges.get(43), "43번 노드의 엣지는 35이어야 합니다.");
    }

    @Test
    void testMovePosition_NormalMove() {
        Piece piece = new Piece(1, 1, 0); // 플레이어 1의 말 생성
        piece.setPosition(0); // 초기 위치 설정
        board.nodes.get(0).add(piece); // 노드에 말 추가

        board.movePosition(piece, 5); // 윷 값 5만큼 이동
        assertEquals(5, piece.getPosition(), "말의 위치는 5이어야 합니다.");
        assertTrue(board.nodes.get(5).getOwnedPieces().contains(piece), "5번 노드에 말이 있어야 합니다.");
        assertFalse(board.nodes.get(0).getOwnedPieces().contains(piece), "0번 노드에는 말이 없어야 합니다.");
    }

    @Test
    void testMovePosition_BackwardMove() {
        Piece piece = new Piece(1, 1, 0);
        piece.setPosition(5);
        piece.pushPreviousPosition(0); // ⭐ 빽도 시 되돌아갈 위치 설정

        board.nodes.get(5).add(piece);
        board.movePosition(piece, -1);

        assertEquals(0, piece.getPosition()); // ✅ 이제 조건이 충족될 것
    }

    @Test
    void testMovePosition_WinningMove() {
        Piece piece = new Piece(1, 1, 0);
        piece.setPosition(42);
        board.nodes.get(42).add(piece);

        board.movePosition(piece, 1);

        assertTrue(piece.isFinished());
        assertEquals(43, piece.getPosition());
    }

}
