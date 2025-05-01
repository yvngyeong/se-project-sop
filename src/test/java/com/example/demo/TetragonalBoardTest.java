package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public class TetragonalBoardTest {

    private TetragonalBoard board;
    private Piece piece;

    @BeforeEach
    void setUp() {
        board = new TetragonalBoard();
        board.createNodes();
        board.createEdges();

        piece = new Piece(1); // ownerId = 1, color = 1, groupID = 0
        piece.setPosition(0);
    }

    //Node 30개 생성되는지 & 코너노드랑 일반노드랑 구분되어서 잘 생성되는지
    @Test
    void testNodeCreation() {
        assertEquals(30, board.nodes.size());

        // 코너 노드 위치 인덱스
        List<Integer> cornerIndices = List.of(0, 5, 10, 15, 20);

        for (int i = 0; i < board.nodes.size(); i++) {
            if (cornerIndices.contains(i)) {
                assertInstanceOf(CornerNode.class, board.nodes.get(i), "Index " + i + " should be CornerNode");
            } else {
                assertInstanceOf(NormalNode.class, board.nodes.get(i), "Index " + i + " should be NormalNode");
            }
        }
    }

    //edge 생성 잘 되는지
    @Test
    void testAllEdgeCreation() {
        // 기본 직선 경로 (0~28까지 i → i+1)
        for (int i = 1; i <= 28; i++) {
            if (i == 20 ||i==21 || i==22 || i == 23 || i==24 || i == 25 || i == 15 || i == 27) continue; // 지름길에서 사용되는 목적지 노드는 검사 제외
            if (i - 1 == 0) {
                assertEquals(List.of(), board.edges.get(0), "edge from 0 should be empty (finish point)");
            } else {
                assertEquals(List.of(i), board.edges.get(i - 1), "edge from " + (i - 1) + " should go to " + i);
            }
        }

        // 특수 지름길 검사
        assertEquals(List.of(21), board.edges.get(22));
        assertEquals(List.of(20), board.edges.get(21));
        assertEquals(List.of(23), board.edges.get(24));
        assertEquals(List.of(20), board.edges.get(23));
        assertEquals(List.of(25), board.edges.get(20));
        assertEquals(List.of(15), board.edges.get(26));
    }

    //시작 위치에서 모 검사 (0->5)
    @Test
    void testMovePosition_NormalMove() {
        piece.setPosition(0); // 초기 위치 설정
        board.nodes.get(0).add(piece); // 노드에 말 추가

        board.movePosition(piece, 5); // 윷 값 5만큼 이동
        assertEquals(5, piece.getPosition(), "말의 위치는 5이어야 합니다.");
        assertTrue(board.nodes.get(5).getOwnedPieces().contains(piece), "5번 노드에 말이 있어야 합니다.");
        assertTrue(!board.nodes.get(0).getOwnedPieces().contains(piece), "0번 노드에는 말이 없어야 합니다.");
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

    //빽도 시작위치에서=> 무효처리
    @Test
    void testNoBackDoFromStart() {
        board.movePosition(piece, -1); // 시작 지점이라 빽도 불가

        assertEquals(0, piece.getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piece));
    }

    //코너노드 직후 노드에서 빽도

    //코너노드에서 멈출 경우, 지름길로 빠짐 (5->22)
    @Test
    void testShortcutFromCorner5() {
        piece.setPosition(5);
        piece.pushPreviousPosition(4);
        board.nodes.get(5).add(piece);

        board.movePosition(piece, 1); // 5 → 22

        assertEquals(22, piece.getPosition());
        assertTrue(board.nodes.get(22).getOwnedPieces().contains(piece));
        assertFalse(board.nodes.get(5).getOwnedPieces().contains(piece), "5번 노드에는 말이 없어야 합니다.");
    }

    //코너 노드에서 꺾는 거랑 지나칠 떄

    //종료 지점 도착 = 완주 조건이 아닐 때 (28->29)
    @Test
    void testNotFinishedCondition() {
        piece.setPosition(28);
        piece.pushPreviousPosition(27);
        board.nodes.get(28).add(piece);

        board.movePosition(piece, 1); // 28 → 29 (종착)

        assertFalse(piece.isFinished());
        assertEquals(29, piece.getPosition());
        assertTrue(board.nodes.get(29).getOwnedPieces().contains(piece));
    }

    //완주 (종료지점 29->30~) - 종료지점에서 도
    @Test
    void testFinishedCondition() {
        piece.setPosition(29);
        piece.pushPreviousPosition(28);
        board.nodes.get(29).add(piece);

        board.movePosition(piece, 1);

        assertTrue(piece.isFinished());
    }

    //완주 - 종료지점 29에서 개 이상 나오면??
    @Test
    void testFinishedConditionAnother() {
        piece.setPosition(29);
        piece.pushPreviousPosition(28);
        board.nodes.get(29).add(piece);

        board.movePosition(piece, 2);

        assertTrue(piece.isFinished());
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
