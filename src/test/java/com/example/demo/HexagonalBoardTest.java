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
    private Piece piece;


    @BeforeEach
    void setUp() {
        board = new HexagonalBoard();
        board.createNodes();
        board.createEdges();
        piece = new Piece(1);
        piece.setPosition(0);
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
    }

    @Test
    void testMovePosition_NormalMove() {
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
    void testMovePosition_JustReachEnd_35() {
        // 35에서 윷값 1 → 0 → 아직 finish 아님
        piece.setPosition(35);
        board.nodes.get(35).add(piece);

        board.movePosition(piece, 1); // 딱 0 도달

        assertFalse(piece.isFinished(), "딱 0에 도달했을 때는 아직 승리 상태가 아님");
        assertEquals(0, piece.getPosition(), "위치는 0이지만 승리 상태 아님");
    }

    @Test
    void testMovePosition_OverEnd_35() {
        // 35에서 윷값 2 → 0을 넘음 → 승리 처리
        piece.setPosition(35);
        board.nodes.get(35).add(piece);

        board.movePosition(piece, 2); // 35을 '지나쳐서' 이동 불가 → 승리

        assertTrue(piece.isFinished(), "종점 이후로 이동하려 해서 승리 처리되어야 함");
        assertEquals(43, piece.getPosition(), "승리 위치는 43");
    }

    @Test
    void testMovePosition_Fail_29() {
        // 29에서 윷값 1 → 정확히 43 → 실패
        piece.setPosition(29);
        board.nodes.get(29).add(piece);

        board.movePosition(piece, 1);

        assertFalse(piece.isFinished(), "29 → 1칸 이동해 43 도달했지만 승리는 아님");
        assertEquals(0, piece.getPosition());
    }

    @Test
    void testMovePosition_Success_29() {
        // 29에서 윷값 2 → 43 넘어가서 승리 처리
        piece.setPosition(29);
        board.nodes.get(29).add(piece);

        board.movePosition(piece, 2); // 29 → 43 → 없음 → finish()

        assertTrue(piece.isFinished(), "29에서 윷값 2로 종점 이후 이동 시 승리");
        assertEquals(43, piece.getPosition());
    }

    //다른 팀 말 잡기
    @Test
    void testCaptureOpponent() {
        Piece enemy = new Piece(2); // 다른 팀
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
        Piece ally = new Piece(1); // 같은 팀
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
