package controllerTest;

public class controllerTest {
}

package controller;

import com.example.demo.Board;
import com.example.demo.Game;
import com.example.demo.Piece;
import com.example.demo.Player;
import com.example.demo.Yut;
import listener.PieceClickListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.GameView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mockito 없이 최소한의 스텁으로 작성한 GameController 단위 테스트
 */
class GameControllerSimpleTest {

    // ─── 간단 Stub 클래스들 ──────────────────────────────────────────────────

    // GameView의 동작을 최소화해서 호출 카운트만 기록
    static class StubGameView implements GameView {
        int updateCurrentPlayerCount = 0;
        int showYutResultCount = 0;
        int updateYutQueueCount = 0;
        int setStatusCount = 0;

        @Override
        public void updateCurrentPlayer(int playerId) {
            updateCurrentPlayerCount++;
        }
        @Override public void setRestartCallback(Runnable r) { }
        @Override public void setPieceClickListener(PieceClickListener l) { }
        @Override public void createYutButtons() { }
        @Override public void createRandomYutButtons() { }
        @Override public void setThrowListener(Runnable r) { }
        @Override public void setSelectThrowListener(YutListener l) { }
        @Override public void initPieceComponents(List<Player> players, PieceClickListener l) { }
        @Override public void showYutResult(int result) {
            showYutResultCount++;
        }
        @Override public void updateYutQueue(List<Integer> queue) {
            updateYutQueueCount++;
        }
        @Override public void setStatus(String text) {
            setStatusCount++;
        }
        @Override public void showYutResultButtons(List<Integer> queue, YutListener l) { }
        @Override public void hideYutResultButtons() { }
        @Override public void updateBoardPieces(List<Player> players) { }
        @Override public void updateUnusedPieces(List<Player> players) { }
        @Override public void showGameOverDialog(int winnerId) { }
        @Override public void showThrowButtonAgain(boolean isTestYut) { }

        // (인터페이스 메서드 생략: 불필요한 부분은 빈 구현)
    }

    // Game의 최소 스텁: 플레이어 리스트와 Yut 결과만 반환
    static class StubGame extends Game {
        private final List<Player> players;
        private final Yut yut;
        private final Board board;

        StubGame(List<Player> players, Yut yut, Board board) {
            super(0, 0, yut, board);
            this.players = players;
            this.yut = yut;
            this.board = board;
        }
        @Override public List<Player> getPlayers() {
            return players;
        }
        @Override public Yut getYut() {
            return yut;
        }
        @Override public Board getBoard() {
            return board;
        }
    }

    // Player의 최소 스텁: ID와 빈 리스트만 제공
    static class StubPlayer extends Player {
        private final int id;
        private final List<Piece> pieces = new ArrayList<>();

        StubPlayer(int id) {
            super(id, 0);
            this.id = id;
        }
        @Override public int getId() {
            return id;
        }
        @Override public List<Piece> getPieces() {
            return pieces;
        }
        @Override public List<Piece> getUnusedPieces() {
            return pieces;
        }
        @Override public boolean hasPiecesOnBoard() {
            return false;
        }
        @Override public boolean checkWin() {
            return false;
        }
    }

    // Yut의 최소 스텁: 고정된 값만 리턴
    static class StubYut extends Yut {
        private final int next;

        StubYut(int next) {
            super(null);
            this.next = next;
        }
        @Override public int getResult() {
            return next;
        }
    }

    // Board의 최소 스텁 (movePosition, isCatched 등을 빈 구현)
    static class StubBoard extends Board {
        @Override public void createNodes() { }
        @Override public void createEdges() { }
        @Override public void movePosition(Piece p, int steps) { }
        @Override public boolean isCatched() {
            return false;
        }
    }

    // ─── 테스트 변수들 ──────────────────────────────────────────────────

    private StubGameView view;
    private StubPlayer player1, player2;
    private List<Player> players;
    private StubYut yut;
    private StubBoard board;
    private StubGame game;
    private GameController controller;

    @BeforeEach
    void setUp() {
        // 플레이어 두 명 준비
        player1 = new StubPlayer(1);
        player2 = new StubPlayer(2);
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Yut 결과를 항상 “1”로 고정 (ex: 도)
        yut = new StubYut(1);
        board = new StubBoard();

        // Game 스텁 생성
        game = new StubGame(players, yut, board);

        // 뷰 스텁 생성
        view = new StubGameView();

        // 컨트롤러 생성 (여기서 updateCurrentPlayer 한 번 호출)
        controller = new GameController(game, view);
    }

    @Test
    void 초기화_시_updateCurrentPlayer_한번_호출() {
        assertEquals(1, view.updateCurrentPlayerCount,
                "컨트롤러 생성 시 한 번만 updateCurrentPlayer가 호출되어야 한다");
    }

    @Test
    void 윷던지면_showYutResult와_updateYutQueue_호출() {
        // yut.getResult() == 1 이므로 processYutResult(1)를 직접 호출
        controller.processYutResult(1);

        // showYutResult 와 updateYutQueue가 각각 1번씩 호출되어야 함
        assertEquals(1, view.showYutResultCount, "윷 결과를 화면에 보여줘야 한다");
        assertEquals(1, view.updateYutQueueCount, "윷 Queue를 갱신해야 한다");
    }

    @Test
    void firstYut이_빽도인_경우_바로_턴넘기기() {
        // yut 결과를 -1(빽도)로 바꾼 새로운 컨트롤러 준비
        StubYut backdoYut = new StubYut(-1);
        game = new StubGame(players, backdoYut, board);
        controller = new GameController(game, view);

        // 첫 번째 빽도 처리
        controller.processYutResult(-1);

        // showYutResult, updateYutQueue 호출 확인
        assertEquals(1, view.showYutResultCount);
        assertEquals(1, view.updateYutQueueCount);

        // 바로 다음 턴으로 넘어가므로 updateCurrentPlayerCount는 총 2회 (생성 시 1회 + 여기서 1회)
        assertEquals(2, view.updateCurrentPlayerCount,
                "빽도일 때 즉시 턴이 넘어가야 한다");
    }
}
