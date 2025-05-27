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
        List<Integer> cornerIndices = List.of(0, 5, 10, 15, 20);
        for (int i = 0; i < board.nodes.size(); i++) {
            if (cornerIndices.contains(i)) {
                assertInstanceOf(CornerNode.class, board.nodes.get(i));
            } else {
                assertInstanceOf(NormalNode.class, board.nodes.get(i));
            }
        }
    }

    //edge 생성 잘 되는지
    @Test
    void testAllEdgeCreation() {
        for (int i = 1; i <= 28; i++) {
            if (List.of(15, 20, 21, 22, 23, 24, 25, 27).contains(i)) continue;
            if (i - 1 == 0) {
                assertEquals(List.of(29), board.edges.get(0));
            } else {
                assertEquals(List.of(i), board.edges.get(i - 1));
            }
        }

        assertEquals(List.of(21), board.edges.get(22));
        assertEquals(List.of(20), board.edges.get(21));
        assertEquals(List.of(23), board.edges.get(24));
        assertEquals(List.of(20), board.edges.get(23));
        assertEquals(List.of(25), board.edges.get(20));
        assertEquals(List.of(15), board.edges.get(26));
    }

    //도, 개, 걸, 윷, 모 기본 검사 (출발점에서)
    @Test
    void testBasicMoveDoToMo() {
        for (int step = 1; step <= 5; step++) {
            piece = new Piece(1);
            board.movePosition(piece, step);
            assertEquals(step, piece.getPosition());
        }
    }

    // 5,10번 코너에서 출발할 때 중심점 쪽으로 이동하는지
    @Test
    void testCornerToCenterPath() {
        int[][] paths = {
                {5, 1, 22, 1, 21, 1, 20},
                {10, 1, 24, 1, 23, 1, 20},
        };
        for (int[] path : paths) {
            piece.setPosition(path[0]);
            for (int i = 1; i < path.length - 1; i += 2) {
                board.movePosition(piece, path[i]);
                assertEquals(path[i + 1], piece.getPosition());
            }
            assertEquals(20, piece.getPosition());
        }
    }

    //코너 노드 지나칠 떄
    @Test
    void testOverCorner() {
        int[][] paths = {{4, 2, 6}, {9, 2, 11}};
        for (int i = 0; i < paths.length; i++) {
            piece.setPosition(paths[i][0]);
            board.movePosition(piece, paths[i][1]);
            assertEquals(paths[i][2], piece.getPosition());
        }
    }

    //출발점에서 빽도 시 적용X
    @Test
    void testBackDoFromStart() {
        piece.setPosition(0);
        board.movePosition(piece, -1);
        assertEquals(0, piece.getPosition());
    }

    //기본 빽도
    @Test
    void testBackDoGeneral() {
        board.movePosition(piece,5);
        board.movePosition(piece, -1);
        assertEquals(4, piece.getPosition());
    }

    //코너노드 직후 노드에서 빽도
    @Test
    void testBaekdoOverCorner(){
        // 6 에서 빽도 시 -> 5
        board.movePosition(piece,4);
        board.movePosition(piece,2);
        board.movePosition(piece,-1);
        assertEquals(5,piece.getPosition());
        assertTrue(board.nodes.get(5).getOwnedPieces().contains(piece));
        //25에서 빽도 시 -> 20
        board.movePosition(piece,4);
        board.movePosition(piece,-1);
        assertEquals(20,piece.getPosition());
        //11에서 빽도 시 -> 10
        piece.setPosition(9);
        board.movePosition(piece,2);
        board.movePosition(piece,-1);
        assertEquals(10, piece.getPosition());
        //27에서 빽도 시 -> 20
        board.movePosition(piece,4);
        board.movePosition(piece,-1);
        assertEquals(20,piece.getPosition());
    }

    //21 -> 20 에서 빽도 시 21, 23 -> 20 에서 빽도 시 23
    @Test
    void testCornerAfterBackDo() {
        piece.setPosition(21);
        board.movePosition(piece, 1);
        board.movePosition(piece, -1);
        assertEquals(21, piece.getPosition());
        piece.setPosition(23);
        board.movePosition(piece,1);
        board.movePosition(piece,-1);
        assertEquals(23, piece.getPosition());
    }

    //도->빽도 에서는 도착 처리X, 거기서 도이상 던지면 도착 처리
    @Test
    void testBackDoToEnd(){
        board.movePosition(piece,1);
        board.movePosition(piece, -1);
        assertFalse(piece.isFinished());
        board.movePosition(piece,1);
        assertTrue(piece.isFinished());
    }

    //빽도의 빽도 시 다시 1번 노드 위치로
    @Test
    void testBaekdoOfBaekdo(){
        board.movePosition(piece,1);
        board.movePosition(piece,-1);
        board.movePosition(piece,-1);
        assertEquals(1,piece.getPosition());
    }


    //28에서 도 던지면 종료점 (도착 처리X), 19에서 도 던지면 종료점 (도칙 처리X)
    @Test
    void testArrivedLogic() {
        piece.setPosition(28);
        board.movePosition(piece, 1);
        assertTrue(piece.isJustArrived());
        assertFalse(piece.isFinished());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piece));

        piece.setPosition(19);
        board.movePosition(piece, 1);
        assertTrue(piece.isJustArrived());
        assertFalse(piece.isFinished());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piece));
    }

    //28에서 개면 통과, 19에서 개면 통과 처리
    @Test
    void testFinishedLogic(){
        piece.setPosition(28);
        board.movePosition(piece, 2);
        assertTrue(piece.isFinished());

        piece.setPosition(19);
        board.movePosition(piece, 2);
        assertTrue(piece.isFinished());
    }

    //기본 말 잡기
    @Test
    void testCapture(){
        Piece enemy = new Piece(2);
        enemy.setPosition(19);
        board.nodes.get(19).add(enemy);

        piece.setPosition(18);
        board.nodes.get(18).add(piece);
        board.movePosition(piece, 1);
        assertEquals(0, enemy.getPosition());
        assertEquals(19, piece.getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(enemy));
        assertTrue(board.nodes.get(19).getOwnedPieces().contains(piece));

    }

    //도착점에서 말 잡기
    @Test
    void testCaptureAtFinishNode() {
        Piece enemy = new Piece(2);
        enemy.setPosition(19);
        board.nodes.get(19).add(enemy);
        board.movePosition(enemy, 1);

        piece.setPosition(19);
        board.nodes.get(19).add(piece);
        board.movePosition(piece, 1);

        assertEquals(0, enemy.getPosition());
        assertEquals(0, piece.getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piece));
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(enemy));
        assertFalse(enemy.isJustArrived());
        assertTrue(piece.isJustArrived());
    }

    //그룹끼리 말 잡기
    @Test
    void testCaptureGrouping(){
        Piece ally = new Piece(1); // 같은 팀
        Piece enemy_1 = new Piece(2);
        Piece enemy_2 = new Piece(2);
        board.movePosition(piece,3);
        board.movePosition(enemy_1,2);
        board.movePosition(ally,3);
        board.movePosition(enemy_2,2);
        board.movePosition(piece,1);
        board.movePosition(enemy_1,2);
        assertEquals(0, piece.getPosition());
        assertEquals(0, ally.getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piece));
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(ally));
    }

    //말 업기 (Grouping) 2개, 함께 이동
    @Test
    void testGroupingWithAlly() {
        Piece ally = new Piece(1); // 같은 팀
        board.movePosition(piece,3);
        board.movePosition(ally,3);

        assertEquals(3, piece.getPosition());
        assertEquals(3, ally.getPosition());
        assertEquals(piece.getGroupId(),ally.getGroupId());

        board.movePosition(piece,3);
        assertEquals(6,piece.getPosition());
        assertEquals(6,ally.getPosition());
        assertEquals(piece.getPosition(),ally.getPosition());
    }

    //그룹핑 후 빽도
    @Test
    void testGroupingBakdo() {
        Piece ally = new Piece(1); // 같은 팀
        board.movePosition(piece,3);
        board.movePosition(ally,3);

        assertEquals(3, piece.getPosition());
        assertEquals(3, ally.getPosition());
        assertEquals(piece.getPosition(),ally.getPosition());
        assertEquals(piece.getGroupId(), ally.getGroupId());

        board.movePosition(piece,-1);
        assertEquals(2,piece.getPosition());
        assertEquals(2,ally.getPosition());
        assertEquals(piece.getPosition(),ally.getPosition());
        assertEquals(piece.getGroupId(), ally.getGroupId());
    }

    //그룹핑 - 마지막 노드 거치고 통과
    @Test
    void testGroupingArrived(){
        board.movePosition(piece,5);
        board.movePosition(piece,3);

        Piece ally = new Piece(1);
        board.movePosition(ally,5);
        board.movePosition(ally,3);

        assertEquals(piece.getGroupId(),ally.getGroupId());
        board.movePosition(piece,3);
        assertFalse(piece.isFinished());
        assertFalse(ally.isFinished());
        assertTrue(piece.isJustArrived());
        assertTrue(ally.isJustArrived());

        board.movePosition(piece,1);
        assertEquals(piece.getGroupId(),ally.getGroupId());
        assertTrue(piece.isFinished());
        assertTrue(ally.isFinished());
    }

    //그룹핑 바로 통과
    @Test
    void testGroupingFinished(){
        board.movePosition(piece,5);
        board.movePosition(piece,3);

        Piece ally = new Piece(1);
        board.movePosition(ally,5);
        board.movePosition(ally,3);

        assertEquals(piece.getGroupId(),ally.getGroupId());
        board.movePosition(piece,4);
        assertTrue(piece.isFinished());
        assertTrue(ally.isFinished());
    }

    //세개 그룹핑 후 같이 이동
    @Test
    void testThreeGrouping() {
        board.movePosition(piece,3);
        Piece ally_1 = new Piece(1); // 같은 팀
        board.movePosition(ally_1,3);
        Piece ally_2 = new Piece(1);
        board.movePosition(ally_2,3);

        assertEquals(3, piece.getPosition());
        assertEquals(3, ally_1.getPosition());
        assertEquals(piece.getGroupId(),ally_1.getGroupId());
        assertEquals(3,ally_2.getPosition());
        assertEquals(3, ally_2.getPosition());
        assertEquals(piece.getGroupId(),ally_2.getGroupId());

        board.movePosition(piece,3);
        assertEquals(6,piece.getPosition());
        assertEquals(6,ally_1.getPosition());
        assertEquals(piece.getPosition(),ally_1.getPosition());
        assertEquals(6,ally_2.getPosition());
        assertEquals(6, ally_2.getPosition());
        assertEquals(piece.getGroupId(),ally_2.getGroupId());
    }

    //세개 그룹핑 빽도
    @Test
    void testThreeGroupingBaekdo() {
        board.movePosition(piece,3);
        Piece ally_1 = new Piece(1); // 같은 팀
        board.movePosition(ally_1,3);
        Piece ally_2 = new Piece(1);
        board.movePosition(ally_2,3);

        assertEquals(3, piece.getPosition());
        assertEquals(3, ally_1.getPosition());
        assertEquals(piece.getGroupId(),ally_1.getGroupId());
        assertEquals(3,ally_2.getPosition());
        assertEquals(3, ally_2.getPosition());
        assertEquals(piece.getGroupId(),ally_2.getGroupId());

        board.movePosition(piece,-1);
        assertEquals(2,piece.getPosition());
        assertEquals(2,ally_1.getPosition());
        assertEquals(piece.getPosition(),ally_1.getPosition());
        assertEquals(2,ally_2.getPosition());
        assertEquals(2, ally_2.getPosition());
        assertEquals(piece.getGroupId(),ally_2.getGroupId());
    }

    //그룹핑의 그룹핑 후 같이 이동하는지 검사
    @Test
    void testDoubleGourping(){
        Piece ally_1 = new Piece(1);
        Piece ally_2 = new Piece(1);
        Piece ally_3 = new Piece(1);

        // piece와 ally_1을 같은 위치로
        board.movePosition(piece, 2);
        board.movePosition(ally_1, 2);
        assertEquals(piece.getGroupId(), ally_1.getGroupId());

        // ally_2와 ally_3도 다른 위치에서 같은 위치로 이동하여 그룹 형성
        board.movePosition(ally_2, 4);
        board.movePosition(ally_3, 4);
        assertEquals(ally_2.getGroupId(), ally_3.getGroupId());

        // piece가 ally_2와 같은 위치로 이동하여 모든 말이 하나의 그룹으로
        board.movePosition(piece, 2);
        assertEquals(piece.getGroupId(), ally_2.getGroupId());
        assertEquals(piece.getGroupId(), ally_3.getGroupId());
        assertEquals(piece.getGroupId(), ally_1.getGroupId());

        // group 전체가 함께 이동함 → 모든 말의 위치가 같아야 함
        board.movePosition(piece, 2); // group 전체 이동

        int expectedPos = piece.getPosition();
        for (Piece p : new Piece[]{ally_1, ally_2, ally_3}) {
            assertEquals(expectedPos, p.getPosition());
        }
    }

    //그룹핑의 그룹핑 - 마지막 노드 거치고 통과
    @Test
    void testDoubleGroupingArrivedLogic() {
        // 그룹 A: piece + ally_1
        board.movePosition(piece,5);
        board.movePosition(piece,3);
        Piece ally_1 = new Piece(1);
        board.movePosition(ally_1,5);
        board.movePosition(ally_1,3);
        assertEquals(piece.getGroupId(), ally_1.getGroupId());

        // 그룹 B: ally_2 + ally_3
        Piece ally_2 = new Piece(1);
        Piece ally_3 = new Piece(1);
        board.movePosition(ally_2, 5);
        board.movePosition(ally_3, 5);
        assertEquals(ally_2.getGroupId(), ally_3.getGroupId());

        // piece (and ally_1) + ally_2/3와 만나면서 그룹 통합
        board.movePosition(ally_2,3);
        int unifiedGroupId = piece.getGroupId();
        assertEquals(unifiedGroupId, ally_1.getGroupId());
        assertEquals(unifiedGroupId, ally_2.getGroupId());
        assertEquals(unifiedGroupId, ally_3.getGroupId());

        board.movePosition(piece,3);
        for (Piece p : new Piece[]{piece, ally_1, ally_2, ally_3}) {
            assertTrue(p.isJustArrived());
            assertFalse(p.isFinished());
            assertTrue(board.nodes.get(0).getOwnedPieces().contains(p));
        }

        // 다음 이동에서 모두 완주
        board.movePosition(piece, 1);
        for (Piece p : new Piece[]{piece, ally_1, ally_2, ally_3}) {
            assertTrue(p.isFinished());
        }
    }

    //그룹핑의 그룹핑 - 마지막 노드 안거치고 통과
    @Test
    void testDoubleGroupingFinishLogic(){
        // 그룹 A: piece + ally_1
        board.movePosition(piece,5);
        board.movePosition(piece,3);
        Piece ally_1 = new Piece(1);
        board.movePosition(ally_1,5);
        board.movePosition(ally_1,3);
        assertEquals(piece.getGroupId(), ally_1.getGroupId());

        // 그룹 B: ally_2 + ally_3
        Piece ally_2 = new Piece(1);
        Piece ally_3 = new Piece(1);
        board.movePosition(ally_2, 5);
        board.movePosition(ally_3, 5);
        assertEquals(ally_2.getGroupId(), ally_3.getGroupId());

        // piece (and ally_1) + ally_2/3와 만나면서 그룹 통합
        board.movePosition(ally_2,3);
        int unifiedGroupId = piece.getGroupId();
        assertEquals(unifiedGroupId, ally_1.getGroupId());
        assertEquals(unifiedGroupId, ally_2.getGroupId());
        assertEquals(unifiedGroupId, ally_3.getGroupId());

        //완주
        board.movePosition(piece,4);

        for (Piece p : new Piece[]{piece, ally_1, ally_2, ally_3}) {
            assertTrue(p.isFinished());
        }

    }
}


