package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.io.*;

class PlayerTest {

    Player player;

    @BeforeEach
    void setUp() {
        player = new Player(2, 3); // ID 2, 말 3개
    }

    //id값 일치 확인 & piece 말 개수만큼 생성되는지 확인 + piece랑 연동되는지
    @Test
    void testPlayerInitialization() {

        assertEquals(2, player.getId());
        List<Piece> pieces = player.getPieces();
        assertEquals(3, pieces.size());
        assertFalse(player.checkWin());

        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            assertEquals(2, p.getOwnerId()); // piece도 검사..
            assertEquals(0, p.getGroupId());
            assertEquals(0, p.getPosition());
            assertFalse(p.isFinished());
        }
    }

    //stack 잘 동작하는지 검사
    @Test
    void testPieceStackBehavior() {
        Piece p = player.getPieces().get(0);

        // 스택에 위치 넣고 꺼내기
        p.pushPreviousPosition(5);
        p.pushPreviousPosition(6);

        assertEquals(6, p.popPreviousPosition());
        assertEquals(5, p.popPreviousPosition());
        assertEquals(-1, p.popPreviousPosition()); // 스택 비었을 때
    }

    //말 그룹핑 검사
    @Test
    void testPieceGrouping() {
        Piece p1 = player.getPieces().get(0);
        Piece p2 = player.getPieces().get(1);

        assertNotEquals(1, p1.getGroupId());
        assertNotEquals(1, p2.getGroupId());

        p1.grouping(p2);

        assertEquals(1, p1.getGroupId());
        assertEquals(1, p2.getGroupId());

        assertTrue(p1.getGroupedPieces().contains(p2));
        assertTrue(p2.getGroupedPieces().contains(p1));
    }

    //모든 말이 끝나면, 승리로 바뀌는지 확인
    @Test
    void testCheckWin() {
        assertFalse(player.checkWin());

        for (Piece p : player.getPieces()) {
            p.finish();
        }

        assertTrue(player.checkWin());
        // 다시 호출해도 true가 유지되어야 함
        assertTrue(player.checkWin());
    }

    //완주한 말 개수로 checkWin 값 잘 나오는지 검사 (초기-> 일부만 도착-> 다 도착)
    @Test
    void testCheckWinPartialAndFull() {
        // 초기에 승리 상태는 false여야 함
        assertFalse(player.checkWin());

        // 말 2개만 도착한 상태
        player.getPieces().get(0).finish();
        player.getPieces().get(1).finish();

        // 아직 도착하지 않은 말이 있으므로 승리 아님
        assertFalse(player.checkWin());

        // 나머지 말도 도착 처리
        player.getPieces().get(2).finish();

        // 이제 승리 조건 충족
        assertTrue(player.checkWin());

        // 다시 호출해도 true 유지되어야 함
        assertTrue(player.checkWin());
    }

    //말 선택하는 거 잘 되는지 확인 - 모두 다 도착 안 한 경우
    @Test
    void testSelectPiece() {
        // 2번째 말을 고른다고 가정 (입력: "1\n")
        String simulatedInput = "1\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        Piece selected = player.selectPiece();
        assertEquals(player.getPieces().get(1), selected);

        // 원래의 System.in 복구
        System.setIn(System.in);
    }

    //말 선택하는 거 잘 되는지 확인 - 도착한 말 있고, 잘못된 입력
    @Test
    void testSelectPieceWithInvalidInputs() {
        // 말 0번은 도착한 것으로 처리
        player.getPieces().get(0).finish();

        // 입력 시나리오:
        // - "0" (도착한 말 → 실패)
        // - "5" (존재하지 않는 인덱스 → 실패)
        // - "abc" (숫자 아님 → 실패)
        // - "1" (정상 입력)
        String simulatedInput = "0\n5\nabc\n1\n";
        InputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        Piece selected = player.selectPiece();

        // 1번 말이 정상적으로 선택되었는지 확인
        assertEquals(player.getPieces().get(1), selected);

        // 입력 스트림 원상복구
        System.setIn(System.in);
    }

}
