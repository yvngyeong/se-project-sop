package com.example.demo;
// HexagonalBoardTest.java

// Import the HexagonalBoard class from its package

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

        import static org.junit.jupiter.api.Assertions.*;
        import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class HexagonalBoardTest {
    private HexagonalBoard board;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        board = new HexagonalBoard();
        board.createNodes();
        board.createEdges();
        players = new ArrayList<>();

        // 말 개수 5개인 플레이어 2명 생성
        for (int i = 1; i <= 2; i++) {
            Player player = new Player(i, 5);
            players.add(player);
        }
    }

    // 일반 노드, 코너 노드 잘 생성되는 지 확인
    @Test
    void testNodeCreation() {
        assertEquals(44, board.nodes.size(), "노드의 개수는 44이어야 합니다.");
        List<Integer> cornerIndices = List.of(0, 5, 10, 15, 20, 25, 30);
        for (int i = 0; i < board.nodes.size(); i++) {
            if (cornerIndices.contains(i)) {
                assertInstanceOf(CornerNode.class, board.nodes.get(i));
            } else {
                assertInstanceOf(NormalNode.class, board.nodes.get(i));
            }
        }
    }

    @Test
    void testEdgeCreation() {
        // 일반 순차 연결 (i → i+1), 단 32 제외
        for (int i = 0; i < 43; i++) {
            if (List.of(32, 29, 38, 30, 34, 33, 36, 40, 42, 35).contains(i)) continue;
            if (i == 0) {
                assertEquals(List.of(43), board.edges.get(i)); // 0 → 43
            } else {
                assertEquals(List.of(i + 1), board.edges.get(i));
            }
        }

        // 예외 edge들 검증
        assertEquals(List.of(0), board.edges.get(29));
        assertEquals(List.of(30), board.edges.get(38));
        assertEquals(List.of(34), board.edges.get(30));
        assertEquals(List.of(33), board.edges.get(34));
        assertEquals(List.of(25), board.edges.get(33));
        assertEquals(List.of(35), board.edges.get(36));
        assertEquals(List.of(30), board.edges.get(40));
        assertEquals(List.of(30), board.edges.get(42));
        assertEquals(List.of(30), board.edges.get(32));
        assertEquals(List.of(0), board.edges.get(35));
        assertEquals(List.of(), board.edges.get(43)); // 완주 노드
    }

    @Test
    void testBasicMoveDoToMo() {
        for (int step = 1; step <= 5; step++) {
            Piece piece = players.get(0).getPieces().get(step-1);
            board.movePosition(piece, step);
            assertEquals(step, piece.getPosition());
            assertTrue(board.nodes.get(step).getOwnedPieces().contains(piece));
        }
    }

    // 코너 지나칠 때 잘 돌아가는지 확인
    @Test
    void testOverCorner(){
        Piece piece = players.get(0).getPieces().get(0);
        int[] nodeBeforeCorner = {4, 9, 14, 19};
        for(int i=0; i<nodeBeforeCorner.length; i++){
            piece.setPosition(nodeBeforeCorner[i]);
            board.movePosition(piece, 2);
            assertEquals(nodeBeforeCorner[i]+2, piece.getPosition());
        }
    }

    // 코너에 멈췄을 때 중심쪽으로 가는지 확인
    @Test
    void testCornerToCenter(){
        Piece piece = players.get(0).getPieces().get(0);

        int[][] paths = {
                {5, 37, 38, 30},
                {10, 39, 40, 30},
                {15, 41, 42, 30},
                {20, 31, 32, 30},
        };
        for (int[] path : paths) {
            piece.setPosition(path[0]);
            for (int i = 1; i < path.length; i ++) {
                board.movePosition(piece, 1);
                assertEquals(path[i], piece.getPosition());
                assertTrue(board.nodes.get(path[i]).getOwnedPieces().contains(piece));
            }
            assertEquals(30, piece.getPosition());
        }
    }

    //빽도 5번 노드에서 (5->4)
    @Test
    void testBackdo() {
        Piece piece = players.get(0).getPieces().get(0);
        piece.setPosition(5);
        piece.pushPreviousPosition(4);

        board.nodes.get(5).add(piece);
        board.movePosition(piece, -1);

        assertEquals(4, piece.getPosition());
        assertTrue(board.nodes.get(4).getOwnedPieces().contains(piece));
        assertFalse(board.nodes.get(5).getOwnedPieces().contains(piece), "5번 노드에는 말이 없어야 합니다.");
    }

    // 출발하지 않았을 때 빽도 적용하면 적용 안됨
    @Test
    void testBackdoFromStart() {
        Piece piece = players.get(0).getPieces().get(0);
        piece.setPosition(0);
        board.nodes.get(0).add(piece);

        board.movePosition(piece, -1);

        assertEquals(0, piece.getPosition()); // piece는 시작점에 위치
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piece));
        assertFalse(piece.isJustArrived()); // piece는 도착한게 아니라 출발 안한 상태
    }

    @Test
    // 코너 바로 다음 노드에서 빽도할 때 코너로 돌아감
    void testBackdoAfterCornerNode(){
        Piece piece = players.get(0).getPieces().get(0);
        piece.setPosition(9);
        board.nodes.get(9).add(piece);

        board.movePosition(piece, 2);
        board.movePosition(piece, -1);

        assertEquals(10, piece.getPosition());
        assertTrue(board.nodes.get(10).getOwnedPieces().contains(piece));
        assertFalse(board.nodes.get(39).getOwnedPieces().contains(piece));
    }

    // 도->빽도 나오고 다시 빽도 적용할시 도로 이동
    @Test
    void testBackdoAfterBackdo(){
        Piece piece = players.get(0).getPieces().get(0);
        piece.setPosition(0);
        board.nodes.get(0).add(piece);

        board.movePosition(piece, 1);
        board.movePosition(piece, -1);
        board.movePosition(piece, -1);

        assertEquals(1, piece.getPosition());   //도에서 빽도하고 빽도하면 다시 도자리로
        assertTrue(board.nodes.get(1).getOwnedPieces().contains(piece));
    }

    // 도착점(출발점) 도착 시 끝난게 아님
    @Test
    void testJustHasArrived() {
        Piece piece = players.get(0).getPieces().get(0);
        piece.setPosition(35);
        board.nodes.get(35).add(piece);

        Piece piece2 = players.get(0).getPieces().get(1);
        piece2.setPosition(29);
        board.nodes.get(29).add(piece2);


        board.movePosition(piece, 1); // 딱 0 도달
        board.movePosition(piece2, 1);


        assertFalse(piece.isFinished(), "딱 0에 도달했을 때는 아직 승리 상태가 아님");
        assertTrue(piece.isJustArrived());
        assertEquals(0, piece.getPosition(), "위치는 0이지만 승리 상태 아님");

        assertFalse(piece2.isFinished(), "딱 0에 도달했을 때는 아직 승리 상태가 아님");
        assertTrue(piece2.isJustArrived());
        assertEquals(0, piece2.getPosition(), "위치는 0이지만 승리 상태 아님");
    }

    // 도착점(출발점)을 지나치면 승리함
    @Test
    void testPassStartPoint() {
        Piece piece = players.get(0).getPieces().get(0);
        piece.setPosition(35);
        board.nodes.get(35).add(piece);

        Piece piece2 = players.get(0).getPieces().get(1);
        piece2.setPosition(29);
        board.nodes.get(29).add(piece2);


        board.movePosition(piece, 2);
        board.movePosition(piece2, 2);


        assertTrue(piece.isFinished(), "종점 이후로 이동하려 해서 승리 처리되어야 함");
        assertEquals(43, piece.getPosition(), "승리 위치는 43");

        assertTrue(piece2.isFinished(), "종점 이후로 이동하려 해서 승리 처리되어야 함");
        assertEquals(43, piece2.getPosition(), "승리 위치는 43");
    }

    // 다른 팀 말 잡기
    @Test
    void testCaptureOpponent() {
        Piece myPiece = players.get(0).getPieces().get(0);
        Piece opponent = players.get(1).getPieces().get(0);
        opponent.setPosition(3);
        board.nodes.get(3).add(opponent);

        board.movePosition(myPiece, 3);

        assertEquals(0, opponent.getPosition());
        assertTrue(board.nodes.get(3).getOwnedPieces().contains(myPiece));
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(opponent));
    }

    // 그룹이 그룹 잡기
    @Test
    void testGroupCaptureGroup(){
        // 플레이어 1의 그룹은 4에, 플레이어 2의 그룹은 2에 위치
        List<Piece> piecesOfP1 = players.get(0).getPieces();
        List<Piece> piecesOfP2 = players.get(1).getPieces();

        for(int i=0; i<2; i++){
            board.movePosition(piecesOfP1.get(i), 4);
            board.movePosition(piecesOfP2.get(i), 2);
        }

        board.movePosition(piecesOfP2.get(0), 2);

        assertEquals(0, piecesOfP1.get(0).getPosition());
        assertEquals(0, piecesOfP1.get(1).getPosition());
        assertEquals(4, piecesOfP2.get(0).getPosition());
        assertEquals(4, piecesOfP2.get(1).getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(piecesOfP1.get(0)));
    }

    // 도착점에서 말잡기
    @Test
    void testCaptureAtFinisihNode() {
        Piece myPiece = players.get(0).getPieces().get(0);
        Piece opponent = players.get(1).getPieces().get(0);
        myPiece.setPosition(35);
        board.nodes.get(35).add(myPiece);
        opponent.setPosition(29);
        board.nodes.get(29).add(opponent);

        board.movePosition(opponent, 1);
        board.movePosition(myPiece, 1);

        assertEquals(0, myPiece.getPosition());
        assertEquals(0, opponent.getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(myPiece));
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(opponent));
        assertTrue(myPiece.isJustArrived());
        assertFalse(opponent.isJustArrived());
    }

    //말 업기 (Grouping)
    @Test
    void testGrouping() {
        Piece ally = players.get(0).getPieces().get(0);
        ally.setPosition(3);
        board.nodes.get(3).add(ally);

        Piece piece = players.get(0).getPieces().get(1);
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
    }

    @Test
    void testBasicMoveWithGroup(){
        Piece ally = players.get(0).getPieces().get(0);
        board.movePosition(ally, 3);
        Piece piece = players.get(0).getPieces().get(1);
        board.movePosition(piece, 2);

        board.movePosition(piece, 1);
        board.movePosition(piece, 5);

        assertTrue(piece.getGroupedPieces().contains(ally));
        assertTrue(ally.getGroupedPieces().contains(piece));
        assertTrue(board.nodes.get(8).getOwnedPieces().contains(piece));
        assertTrue(board.nodes.get(8).getOwnedPieces().contains(ally));
    }

    @Test
    void testBackdoWithGroup(){
        Piece ally = players.get(0).getPieces().get(0);
        board.movePosition(ally, 3);
        Piece piece = players.get(0).getPieces().get(1);
        board.movePosition(piece, 2);

        board.movePosition(piece, 1);
        board.movePosition(piece, -1);

        assertTrue(piece.getGroupedPieces().contains(ally));
        assertTrue(ally.getGroupedPieces().contains(piece));
        assertTrue(board.nodes.get(2).getOwnedPieces().contains(piece));
        assertTrue(board.nodes.get(2).getOwnedPieces().contains(ally));
    }

    @Test
    void testFinishWhenGroupStoppedByStart(){
        Piece ally = players.get(0).getPieces().get(0);
        board.movePosition(ally, 5);
        board.movePosition(ally, 3);
        Piece piece = players.get(0).getPieces().get(1);
        board.movePosition(piece, 5);
        board.movePosition(piece, 3);

        board.movePosition(piece, 3);
        assertFalse(ally.isFinished(), "출발점에 도착했을 땐 끝난게 아님");
        assertFalse(piece.isFinished(), "출발점에 도착했을 땐 끝난게 아님");

        board.movePosition(ally, 2);
        assertTrue(piece.getGroupedPieces().contains(ally));
        assertTrue(ally.getGroupedPieces().contains(piece));
        assertTrue(piece.isFinished());
        assertTrue(ally.isFinished());
    }

    @Test
    void testFinishWhenGroupPassedStart(){
        Piece ally = players.get(0).getPieces().get(0);
        board.movePosition(ally, 5);
        board.movePosition(ally, 3);
        Piece piece = players.get(0).getPieces().get(1);
        board.movePosition(piece, 5);
        board.movePosition(piece, 3);

        board.movePosition(piece, 4);

        assertTrue(piece.getGroupedPieces().contains(ally));
        assertTrue(ally.getGroupedPieces().contains(piece));
        assertTrue(piece.isFinished());
        assertTrue(ally.isFinished());
    }

    @Test
    void testGroupingWithMany(){
        Player p1 = players.get(0);
        for(int i=0; i<3; i++){
            board.movePosition(p1.getPieces().get(i), 4);
        }

        board.movePosition(p1.getPieces().get(3), 4);

        assertEquals(4, p1.getPieces().get(0).getGroupedPieces().size());
    }

    // 그룹이 그룹을 만났을 때 그룹이 합쳐지는 것 확인
    @Test
    void testGroupingAndGrouping(){
        List<Piece> piecesOfP1 = players.get(0).getPieces();
        board.movePosition(piecesOfP1.get(0), 4);
        board.movePosition(piecesOfP1.get(1), 4);
        board.movePosition(piecesOfP1.get(2), 2);
        board.movePosition(piecesOfP1.get(3), 2);

        board.movePosition(piecesOfP1.get(2), 2);

        assertTrue(piecesOfP1.get(0).getGroupedPieces().contains(piecesOfP1.get(1)));
        assertTrue(piecesOfP1.get(0).getGroupedPieces().contains(piecesOfP1.get(2)));
        assertTrue(piecesOfP1.get(0).getGroupedPieces().contains(piecesOfP1.get(3)));
    }

    @Test
    void testDoubleGroupingAndMove(){
        List<Piece> piecesOfP1 = players.get(0).getPieces();
        board.movePosition(piecesOfP1.get(0), 4);
        board.movePosition(piecesOfP1.get(1), 4);
        board.movePosition(piecesOfP1.get(2), 2);
        board.movePosition(piecesOfP1.get(3), 2);

        board.movePosition(piecesOfP1.get(2), 2);
        board.movePosition(piecesOfP1.get(0), 4);

        assertEquals(8, piecesOfP1.get(0).getPosition());
        assertEquals(8, piecesOfP1.get(1).getPosition());
        assertEquals(8, piecesOfP1.get(2).getPosition());
        assertEquals(8, piecesOfP1.get(3).getPosition());
    }

    @Test
    void testDoubleGroupingAndBackdo(){
        List<Piece> piecesOfP1 = players.get(0).getPieces();
        board.movePosition(piecesOfP1.get(0), 4);
        board.movePosition(piecesOfP1.get(1), 4);
        board.movePosition(piecesOfP1.get(2), 2);
        board.movePosition(piecesOfP1.get(3), 2);

        board.movePosition(piecesOfP1.get(2), 2);
        board.movePosition(piecesOfP1.get(0), -1);

        assertEquals(3, piecesOfP1.get(0).getPosition());
        assertEquals(3, piecesOfP1.get(1).getPosition());
        assertEquals(3, piecesOfP1.get(2).getPosition());
        assertEquals(3, piecesOfP1.get(3).getPosition());
    }

    @Test
    void testFinishWhenDoubleGroupingStoppedByStart(){
        List<Piece> piecesOfP1 = players.get(0).getPieces();
        board.movePosition(piecesOfP1.get(0), 5);
        board.movePosition(piecesOfP1.get(1), 5);
        board.movePosition(piecesOfP1.get(2), 2);
        board.movePosition(piecesOfP1.get(3), 2);

        board.movePosition(piecesOfP1.get(2), 3);
        board.movePosition(piecesOfP1.get(1), 3);
        board.movePosition(piecesOfP1.get(0), 3);
        assertEquals(0, piecesOfP1.get(0).getPosition());
        assertFalse(piecesOfP1.get(0).isFinished(), "출발점에 도착했을 땐 끝난게 아님");

        board.movePosition(piecesOfP1.get(1), 2);
        assertTrue(piecesOfP1.get(0).isFinished());
        assertTrue(piecesOfP1.get(1).isFinished());
        assertTrue(piecesOfP1.get(2).isFinished());
        assertTrue(piecesOfP1.get(3).isFinished());
    }

    @Test
    void testFinishWhenDoubleGroupingPassedStart(){
        List<Piece> piecesOfP1 = players.get(0).getPieces();
        board.movePosition(piecesOfP1.get(0), 5);
        board.movePosition(piecesOfP1.get(1), 5);
        board.movePosition(piecesOfP1.get(2), 2);
        board.movePosition(piecesOfP1.get(3), 2);

        board.movePosition(piecesOfP1.get(2), 3);
        board.movePosition(piecesOfP1.get(1), 3);
        board.movePosition(piecesOfP1.get(0), 4);

        assertTrue(piecesOfP1.get(0).isFinished());
        assertTrue(piecesOfP1.get(1).isFinished());
        assertTrue(piecesOfP1.get(2).isFinished());
        assertTrue(piecesOfP1.get(3).isFinished());
    }
}

