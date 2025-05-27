package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

public class PentagonalBoardTest {

    private PentagonalBoard board;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        board = new PentagonalBoard();
        board.createNodes();
        board.createEdges();

        players = new ArrayList<>();
        // 플레이어 2명, 각 5개의 말 생성
        for (int i = 1; i <= 2; i++) {
            players.add(new Player(i, 5));
        }
    }

    @Test
    void testNodeCreation() {
        assertEquals(37, board.nodes.size());
        List<Integer> cornerIdx = List.of(0, 5, 10, 15, 20, 25);
        for (int i = 0; i < board.nodes.size(); i++) {
            if (cornerIdx.contains(i)) {
                assertInstanceOf(CornerNode.class, board.nodes.get(i));
            } else {
                assertInstanceOf(NormalNode.class, board.nodes.get(i));
            }
        }
    }

    @Test
    void testAllEdgeCreation() {
        // 기본 연결 (i → i+1) 검증, 예외 인덱스는 아래에서 별도 검사
        List<Integer> overrides = List.of(0, 24, 25, 26, 27, 28, 29, 30, 31, 33, 35, 36);
        for (int i = 1; i < 36; i++) {
            if (overrides.contains(i)) continue;
            assertEquals(List.of(i + 1), board.edges.get(i));
        }

        // 예외 엣지 검증
        assertEquals(List.of(36),      board.edges.get(0));
        assertEquals(List.of(0),       board.edges.get(24));
        assertEquals(List.of(32),      board.edges.get(25));
        assertEquals(List.of(25),      board.edges.get(26));
        assertEquals(List.of(26),      board.edges.get(27));
        assertEquals(List.of(25),      board.edges.get(28));
        assertEquals(List.of(28),      board.edges.get(29));
        assertEquals(List.of(25),      board.edges.get(30));
        assertEquals(List.of(30),      board.edges.get(31));
        assertEquals(List.of(20),      board.edges.get(33));
        assertEquals(List.of(0),       board.edges.get(35));
        assertEquals(List.of(),        board.edges.get(36));
    }

    @Test
    void testBasicMoveDoToMo() {
        // 도(1)부터 모(5)까지 각각 한 번에 이동
        for (int steps = 1; steps <= 5; steps++) {
            Piece p = players.get(0).getPieces().get(steps - 1);
            board.movePosition(p, steps);
            assertEquals(steps, p.getPosition());
            assertTrue(board.nodes.get(steps).getOwnedPieces().contains(p));
        }
    }

    @Test
    void testCornerShortcutOnce() {
        // 코너 노드에서 1칸 이동할 때 지름길로 가는지
        Piece p = players.get(0).getPieces().get(0);

        // 5번 코너 → 27
        p.setPosition(5);
        board.nodes.get(5).add(p);
        board.movePosition(p, 1);
        assertEquals(27, p.getPosition());

        // 10번 코너 → 29
        p.setPosition(10);
        board.nodes.get(10).add(p);
        board.movePosition(p, 1);
        assertEquals(29, p.getPosition());

        // 15번 코너 → 31
        p.setPosition(15);
        board.nodes.get(15).add(p);
        board.movePosition(p, 1);
        assertEquals(31, p.getPosition());

        // 25번 코너 → 34
        p.setPosition(25);
        board.nodes.get(25).add(p);
        board.movePosition(p, 1);
        assertEquals(34, p.getPosition());
    }

    @Test
    void testOverCorner() {
        // 코너 바로 전 노드에서 여러 칸 이동 시 정상 overshoot
        Piece p = players.get(0).getPieces().get(0);
        p.setPosition(4);
        board.nodes.get(4).add(p);
        board.movePosition(p, 2);
        assertEquals(6, p.getPosition());

        p.setPosition(9);
        board.nodes.get(9).add(p);
        board.movePosition(p, 2);
        assertEquals(11, p.getPosition());
    }

    //맨 처음에 빽도 나오면, 적용X
    @Test
    void testBackDoFromStart() {
        Piece p = players.get(0).getPieces().get(0);
        p.setPosition(0);
        board.nodes.get(0).add(p);
        board.movePosition(p, -1);
        assertEquals(0, p.getPosition());
    }

    //기본 빽도
    @Test
    void testBackDoGeneral() {
        Piece p = players.get(0).getPieces().get(0);
        board.movePosition(p, 5);  // 0→1→...→5
        board.movePosition(p, -1);
        assertEquals(4, p.getPosition());
    }

    //코너 노드 직후 빽도
    @Test
    void testBackDoOverShortcut() {
        Piece p = players.get(0).getPieces().get(0);
        // 5→27로 이동 후 빽도 → 5
        p.setPosition(5);
        board.nodes.get(5).add(p);
        board.movePosition(p, 1); // →27
        assertEquals(27,p.getPosition());
        board.movePosition(p, -1);
        assertEquals(5, p.getPosition());

        // 10→29 후 빽도 → 10
        p.setPosition(10);
        board.nodes.get(10).add(p);
        board.movePosition(p, 1); // →29
        assertEquals(29,p.getPosition());
        board.movePosition(p, -1);
        assertEquals(10, p.getPosition());

        // 15→31 후 빽도 → 15
        p.setPosition(15);
        board.nodes.get(15).add(p);
        board.movePosition(p, 1); // →31
        assertEquals(31,p.getPosition());
        board.movePosition(p, -1);
        assertEquals(15, p.getPosition());

        //30->25->34 후 빽도, 빽도
        p.setPosition(30);
        board.nodes.get(30).add(p);
        board.movePosition(p, 2); // →34
        assertEquals(34, p.getPosition());
        board.movePosition(p, -1);
        assertEquals(25, p.getPosition());
        board.movePosition(p, -1);
        assertEquals(30, p.getPosition());

        //28->25->32 후 빽도, 빽도
        p.setPosition(28);
        board.nodes.get(28).add(p);
        board.movePosition(p, 2); // →32
        assertEquals(32, p.getPosition());
        board.movePosition(p, -1);
        assertEquals(25, p.getPosition());
        board.movePosition(p, -1);
        assertEquals(28, p.getPosition());

    }

    @Test
    void testBackDoToFinishLogic() {
        Piece p = players.get(0).getPieces().get(0);
        // 도(1)→빽도→다시 도(1) 하면 완주 처리
        board.movePosition(p, 1);
        board.movePosition(p, -1);
        assertFalse(p.isFinished()); //0번 노드로 돌아왔을 때는 완주 아님
        board.movePosition(p, 1);
        assertTrue(p.isFinished()); //도 나오면 완주 처리
    }

    //빽도의 빽도 (도->빽도->빽도 = 도)
    @Test
    void testBaekdoOfBaekdo() {
        Piece p = players.get(0).getPieces().get(0);
        board.movePosition(p, 1);
        board.movePosition(p, -1);
        board.movePosition(p, -1);
        assertEquals(1, p.getPosition());
    }

    //완주 처리
    @Test
    void testJustArrivedVsFinished() {
        Piece p1 = players.get(0).getPieces().get(0);
        p1.setPosition(35);
        board.nodes.get(35).add(p1);

        // 정확히 0에 도착 → justArrived, 아직 finished 아님
        board.movePosition(p1, 1);
        assertTrue(p1.isJustArrived());
        assertFalse(p1.isFinished());
        assertEquals(0, p1.getPosition());

        // 하나 더 지나칠 경우 → finished
        p1.setJustArrived(false);
        board.movePosition(p1, 2);
        assertTrue(p1.isFinished());
    }

    //말 잡기
    @Test
    void testCaptureAtFinishNode() {
        Piece me  = players.get(0).getPieces().get(0);
        Piece opp = players.get(1).getPieces().get(0);

        // 상대를 35에 놓고 1칸 → 0으로
        opp.setPosition(35);
        board.nodes.get(35).add(opp);
        board.movePosition(opp, 1);

        // 이제 내가 35→1칸
        me.setPosition(35);
        board.nodes.get(35).add(me);
        board.movePosition(me, 1);

        assertEquals(0, opp.getPosition());
        assertEquals(0, me.getPosition());
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(me));
        assertTrue(board.nodes.get(0).getOwnedPieces().contains(opp));
        assertFalse(opp.isJustArrived());
        assertTrue(me.isJustArrived());
    }

    //말 2개 그룹핑 후, 이동되는지 확인
    @Test
    void testGroupingWithAllyAndMove() {
        Piece p1 = players.get(0).getPieces().get(0);
        Piece p2 = players.get(0).getPieces().get(1);

        board.movePosition(p1, 3);
        board.movePosition(p2, 3);

        assertEquals(p1.getGroupId(), p2.getGroupId()); //그룹ID 일치 확인

        board.movePosition(p1, 2);
        assertEquals(p1.getPosition(), p2.getPosition()); //말 위치 일치 확인
    }

    //말 3개 그룹핑 후, 빽도되는지 확인, 완주(0번 노드 거치기) 확인
    @Test
    void testGroupingBackdo() {
        Piece p1 = players.get(0).getPieces().get(0);
        Piece p2 = players.get(0).getPieces().get(1);
        Piece p3 = players.get(0).getPieces().get(2);

        board.movePosition(p1, 2);
        board.movePosition(p2, 2);
        board.movePosition(p3, 2);
        for (Piece p : List.of(p1, p2, p3)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        //빽도
        board.movePosition(p1, -1);
        assertEquals(p1.getPosition(), p2.getPosition());
        assertEquals(p1.getPosition(), p3.getPosition());

        //이동
        board.movePosition(p1, 4);
        for (Piece p : List.of(p1, p2, p3)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        //완주
        board.movePosition(p1, 3);
        board.movePosition(p1, 3); //0번 노드
        board.movePosition(p1, 1);

        for (Piece p : List.of(p1, p2, p3)) {
            assertTrue(p.isFinished());
        }
    }

    //말 4개 그룹핑 후, 빽도되는지 확인, 완주(0번 노드 지나치기) 확인
    @Test
    void testGroupingBackdo2() {
        Piece p1 = players.get(0).getPieces().get(0);
        Piece p2 = players.get(0).getPieces().get(1);
        Piece p3 = players.get(0).getPieces().get(2);
        Piece p4 = players.get(0).getPieces().get(3);

        board.movePosition(p1, 2);
        board.movePosition(p2, 2);
        board.movePosition(p3, 2);
        board.movePosition(p4, 2);
        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        //빽도
        board.movePosition(p1, -1);
        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        //이동
        board.movePosition(p1, 4);
        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        //완주
        board.movePosition(p1, 3);
        board.movePosition(p1, 4);

        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertTrue(p.isFinished());
        }
    }

    //그룹핑의 그룹핑(2+2). 이동, 빽도, 완주(0번 노드 거치기) 검사
    @Test
    void testMultipleGroupingAndFinish() {
        // 세 개 그룹핑 후 이동/빽도/완주까지
        Piece p1 = players.get(0).getPieces().get(0);
        Piece p2 = players.get(0).getPieces().get(1);
        Piece p3 = players.get(0).getPieces().get(2);
        Piece p4 = players.get(0).getPieces().get(3);

        board.movePosition(p1, 1);
        board.movePosition(p2, 1);
        assertEquals(p1.getGroupId(), p2.getGroupId());

        board.movePosition(p3, 4);
        board.movePosition(p4, 4);
        assertEquals(p3.getGroupId(), p4.getGroupId());

        // 이동
        board.movePosition(p1, 3);
        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertEquals(p1.getPosition(), p.getPosition());
            assertEquals(p1.getGroupId(), p.getGroupId());
        }

        board.movePosition(p1, 11);
        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }
        board.movePosition(p1, 3);
        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        // 빽도
        board.movePosition(p1, -1);
        for (Piece p : List.of(p1, p2, p3, p4)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        // 완주 (0번 노드 거쳐서)
        board.movePosition(p1, 4); //0번 노드
        board.movePosition(p1, 3);
        for (Piece p : List.of(p1, p2, p3)) {
            assertTrue(p.isFinished());
        }
    }

    //그룹핑의 그룹핑(3+2 =5). 이동, 빽도, 완주(0번 노드 지나치기) 검사
    @Test
    void testMultipleGroupingAndFinish2() {
        // 세 개 그룹핑 후 이동/빽도/완주까지
        Piece p1 = players.get(0).getPieces().get(0);
        Piece p2 = players.get(0).getPieces().get(1);
        Piece p3 = players.get(0).getPieces().get(2);
        Piece p4 = players.get(0).getPieces().get(3);
        Piece p5 = players.get(0).getPieces().get(4);

        board.movePosition(p1, 4);
        board.movePosition(p2, 4);
        board.movePosition(p3, 4);
        assertEquals(p1.getGroupId(), p2.getGroupId());
        assertEquals(p1.getGroupId(), p3.getGroupId());

        board.movePosition(p4, 2);
        board.movePosition(p5, 2);
        assertEquals(p4.getGroupId(), p5.getGroupId());

        // 이동
        board.movePosition(p4, 2);
        for (Piece p : List.of(p1, p2, p3, p4, p5)) {
            assertEquals(p1.getPosition(), p.getPosition());
            assertEquals(p1.getGroupId(), p.getGroupId());
        }

        board.movePosition(p1, 6);
        for (Piece p : List.of(p1, p2, p3, p4, p5)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }
        board.movePosition(p1, 4);
        for (Piece p : List.of(p1, p2, p3, p4, p5)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        // 빽도
        board.movePosition(p1, -1);
        for (Piece p : List.of(p1, p2, p3, p4, p5)) {
            assertEquals(p1.getPosition(), p.getPosition());
        }

        // 완주 (0번 노드 지나쳐서)
        board.movePosition(p1, 5);
        for (Piece p : List.of(p1, p2, p3)) {
            assertTrue(p.isFinished());
        }
    }
}
