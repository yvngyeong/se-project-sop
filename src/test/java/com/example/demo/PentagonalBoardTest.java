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

public class PentagonalBoardTest {
    private PentagonalBoard board;
    private Piece piece;

    @BeforeEach
    void setUp() {
        board = new PentagonalBoard();
        board.createNodes();
        board.createEdges();
        piece = new Piece(1,0);
        piece.setPosition(0);
    }

    @Test
    void testCreateNodes() {
        assertNotNull(board.nodes, "노드 리스트가 null이 아닙니다.");
        assertEquals(37, board.nodes.size(), "노드의 개수는 37이어야 합니다.");
    }

    @Test
    void testCreateEdges() {
        assertNotNull(board.edges, "엣지 맵이 null이 아닙니다.");
        assertEquals(List.of(26), board.edges.get(5), "5번 노드의 엣지는 26이어야 합니다.");
        assertEquals(List.of(28), board.edges.get(10), "10번 노드의 엣지는 28이어야 합니다.");
        assertEquals(List.of(31), board.edges.get(15), "15번 노드의 엣지는 31이어야 합니다.");
        assertEquals(List.of(25), board.edges.get(30), "30번 노드의 엣지는 25이어야 합니다.");
        assertEquals(List.of(0), board.edges.get(35), "35번 노드의 엣지는 0이어야 합니다.");
        assertEquals(List.of(0), board.edges.get(24), "24번 노드의 엣지는 0이어야 합니다.");
    }

    @Test
    void testMovePosition_NormalMove() {
        Piece piece = new Piece(1, 0); // 플레이어 1의 말 생성
        piece.setPosition(0); // 초기 위치 설정
        board.nodes.get(0).add(piece); // 노드에 말 추가

        board.movePosition(piece, 5); // 윷 값 5만큼 이동
        assertEquals(5, piece.getPosition(), "말의 위치는 5이어야 합니다.");
        assertTrue(board.nodes.get(5).getOwnedPieces().contains(piece), "5번 노드에 말이 있어야 합니다.");
    }

    //빽도 5번 노드에서 (5->4)
    @Test
    void testMovePosition_BackwardMove() {
        piece.setPosition(5);
        piece.pushPreviousPosition(4);

        board.nodes.get(5).add(piece);
        board.movePosition(piece, -1);

        assertEquals(4, piece.getPosition());
        assertTrue(board.nodes.get(4).getOwnedPieces().contains(piece));
        assertFalse(board.nodes.get(5).getOwnedPieces().contains(piece), "5번 노드에는 말이 없어야 합니다.");
    }

    @Test
    void testMovePosition_JustReachEnd() {
        // 35에서 윷값 1 → 0 → 아직 finish 아님
        piece.setPosition(35);
        board.movePosition(piece, 1); // 딱 0 도달
        assertFalse(piece.isFinished(), "딱 0에 도달했을 때는 아직 승리 상태가 아님");
        assertEquals(0, piece.getPosition(), "위치는 0이지만 승리 상태 아님");
    }

    @Test
    void testMovePosition_OverEnd() {
        // 24에서 윷값 2 → 0을 넘음 → 승리 처리
        Piece piece = new Piece(1, 0);
        piece.setPosition(24);
        board.movePosition(piece, 2); // 35을 '지나쳐서' 이동 불가 → 승리

        assertTrue(piece.isFinished(), "종점 이후로 이동하려 해서 승리 처리되어야 함");
        assertEquals(36, piece.getPosition(), "승리 위치는 36");
    }

    //빽도 시작위치에서=> 무효처리
    @Test
    void testNoBackDoFromStart() {
        board.movePosition(piece, -1); // 시작 지점이라 빽도 불가

        assertEquals(0, piece.getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piece));
    }

    //다른 팀 말 잡기
    @Test
    void testCaptureOpponent() {
        Piece enemy = new Piece(2,0); // 다른 팀
        enemy.setPosition(3);
        board.nodes.get(3).add(enemy);

        piece.setPosition(2);
        piece.pushPreviousPosition(1);
        board.nodes.get(2).add(piece);

        board.movePosition(piece, 1); // 2 → 3, 잡기

        assertEquals(0, enemy.getPosition());
        assertTrue(board.nodes.get(3).getOwnedPieces().contains(piece));
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(enemy));
    }

    //말 업기 (Grouping)
    @Test
    void testGroupingWithAlly() {
        Piece ally = new Piece(1,0); // 같은 팀
        ally.setPosition(3);
        board.nodes.get(3).add(ally);

        piece.setPosition(2);
        piece.pushPreviousPosition(1);
        board.nodes.get(2).add(piece);

        board.movePosition(piece, 1); // 2 → 3, 그룹핑

        assertEquals(3, piece.getPosition());
        assertEquals(3, ally.getPosition());

        // 그룹핑 확인
        assertTrue(piece.getGroupedPieces().contains(ally));
        assertTrue(ally.getGroupedPieces().contains(piece));
        assertTrue(board.nodes.get(3).getOwnedPieces().contains(piece));
        assertTrue(board.nodes.get(3).getOwnedPieces().contains(ally));
        assertTrue(board.nodes.get(3).getOwnedPieces().contains(ally));

    }
}
