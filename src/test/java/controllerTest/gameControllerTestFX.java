package controllerTest;

import com.example.demo.*;
        import controller.GameControllerFX;
import listener.PieceClickListener;
import listener.SelectThrowListener;
import listener.ThrowListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.GameViewFX;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameControllerFX가 GameViewFX(클래스)에 올바르게 리스너를 등록하고,
 * 그 리스너들이 호출될 때 컨트롤러가 적절히 반응하는지 검증하는 테스트 클래스
 */
public class gameControllerTestFX {

    // ─── 1. Stub 및 테스트 변수들 ──────────────────────────────────────────

    /**
     * GameViewFX 스텁: 리스너 등록 시 내부 필드에 저장하고,
     * 컨트롤러가 호출해야 하는 메서드들을 카운트한다.
     * 테스트에서는 직접 리스너 onXxx()를 호출하여 컨트롤러 동작을 시뮬레이션한다.
     */
    static class StubGameViewFX extends GameViewFX {
        // 리스너 저장용 필드
        PieceClickListener    storedPieceClickListener    = null;
        ThrowListener         storedThrowListener         = null;
        SelectThrowListener   storedSelectThrowListener   = null;

        // 컨트롤러가 호출해야 하는 메서드 카운터
        int createYutButtonsCount       = 0;
        int createRandomYutButtonsCount = 0;
        int showYutResultCount          = 0;
        int updateYutQueueCount         = 0;
        int setStatusCount              = 0;
        int showGameOverDialogCount     = 0;
        int showThrowButtonAgainCount   = 0;
        int updateCurrentPlayerCount    = 0;
        int updateUnusedPiecesCount     = 0;

        // GameViewFX가 기본 생성자를 가진다고 가정
        public StubGameViewFX() {
            super(); // 실제 GameViewFX에 매개변수가 없는 기본 생성자가 있어야 합니다.
        }

        @Override
        public void setRestartCallback(Runnable r) {
            // 무시
        }

        @Override
        public void setPieceClickListener(PieceClickListener l) {
            this.storedPieceClickListener = l;
        }

        @Override
        public void createYutButtons() {
            createYutButtonsCount++;
        }

        @Override
        public void createRandomYutButtons() {
            createRandomYutButtonsCount++;
        }

        @Override
        public void setThrowListener(ThrowListener l) {
            this.storedThrowListener = l;
        }

        @Override
        public void setSelectThrowListener(SelectThrowListener l) {
            this.storedSelectThrowListener = l;
        }

        @Override
        public void initPieceComponents(List<Player> players, PieceClickListener l) {
            // 무시
        }

        @Override
        public void updateUnusedPieces(List<Player> players) {
            updateUnusedPiecesCount++;
        }

        @Override
        public void updateCurrentPlayer(int playerId) {
            updateCurrentPlayerCount++;
        }

        @Override
        public void showYutResult(int result) {
            showYutResultCount++;
        }

        @Override
        public void updateYutQueue(List<Integer> queue) {
            updateYutQueueCount++;
        }

        @Override
        public void setStatus(String text) {
            setStatusCount++;
        }

        @Override
        public void showYutResultButtons(List<Integer> queue, SelectThrowListener l) {
            // 무시
        }

        @Override
        public void hideYutResultButtons() {
            // 무시
        }

        @Override
        public void updateBoardPieces(List<Player> players) {
            // 무시
        }

        @Override
        public void showGameOverDialog(int winnerId) {
            showGameOverDialogCount++;
        }

        @Override
        public void showThrowButtonAgain(boolean isTestYut) {
            showThrowButtonAgainCount++;
        }

    }

    /**
     * Game 스텁: players, Yut, Board만 반환
     */
    static class StubGame extends Game {
        private final List<Player> players;
        private final Yut yut;
        private final Board board;

        StubGame(List<Player> players, Yut yut, Board board) {
            super(0, 0, yut, board);
            this.players = players;
            this.yut     = yut;
            this.board   = board;
        }

        @Override
        public List<Player> getPlayers() {
            return players;
        }

        @Override
        public Yut getYut() {
            return yut;
        }

        @Override
        public Board getBoard() {
            return board;
        }
    }

    /**
     * Player 스텁: ID만 반환, hasPiecesOnBoard와 checkWin은 false 고정
     */
    static class StubPlayer extends Player {
        private final int id;

        StubPlayer(int id) {
            super(id, 0);
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public List<Piece> getPieces() {
            return new ArrayList<>();
        }

        @Override
        public List<Piece> getUnusedPieces() {
            return new ArrayList<>();
        }

        @Override
        public boolean hasPiecesOnBoard() {
            return false;
        }

        @Override
        public boolean checkWin() {
            return false;
        }
    }

    /**
     * TestYut 스텁: setNext(int) 시 지정된 결과를 getResult()로 반환
     */
    static class StubTestYut extends TestYut {
        private int nextValue;

        StubTestYut(int initial) {
            super(null);
            this.nextValue = initial;
        }

        @Override
        public void setNext(int next) {
            this.nextValue = next;
        }

        @Override
        public Integer getResult() {
            return nextValue;
        }
    }

    /**
     * Board 스텁: movePosition, isCatched만 최소 구현
     */
    static class StubBoard extends PentagonalBoard {
        @Override
        public void createNodes() { }

        @Override
        public void createEdges() { }

        @Override
        public boolean isCatched() {
            return false;
        }
    }

    // ─── 테스트에서 사용할 공통 변수들 ──────────────────────────────────────────────────────

    private StubGameViewFX view;
    private StubPlayer    player1, player2;
    private List<Player>  players;
    private StubTestYut   testYut;
    private StubBoard     board;
    private StubGame      testGame;
    private GameControllerFX controller;

    // ─── 테스트 준비 ─────────────────────────────────────────────────────────

    @BeforeEach
    void setUp() {
        // (1) Player 두 명 준비
        player1 = new StubPlayer(1);
        player2 = new StubPlayer(2);
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // (2) TestYut 모드를 초기값 1(도)로 설정
        testYut = new StubTestYut(1);
        board   = new StubBoard();

        // (3) Game 스텁 생성
        testGame = new StubGame(players, testYut, board);

        // (4) GameViewFX 스텁 생성 (Stage는 실제 UI가 아니라 null을 넣어도 무방)
        view = new StubGameViewFX();

        // (5) 컨트롤러 생성 (생성자 내부에서 리스너 등록, 버튼 생성, init 등 수행)
        controller = new GameControllerFX(/*stage*/ null, testGame, view);
    }

    // ─── 테스트 케이스 ──────────────────────────────────────────────────────────

    @Test
    void test_PieceClickListener() {
        // GameControllerFX 생성 시 setPieceClickListener(...)가 호출되어야 함
        assertNotNull(view.storedPieceClickListener,
                "GameControllerFX 생성자에서 반드시 setPieceClickListener(...)를 호출해야 한다");

        // 호출된 리스너를 직접 호출해 보아도 예외가 발생하지 않아야 함
        assertDoesNotThrow(
                () -> view.storedPieceClickListener.onPieceClicked(new Piece(1)),
                "PieceClickListener.onPieceClicked() 호출 시 예외가 발생하면 안 된다"
        );
    }

    @Test
    void test_TestYutMode_SelectThrowListener() {
        // TestYut 모드이므로 생성자에서 createYutButtons()와 setSelectThrowListener(...)가 호출됐어야 함
        assertEquals(1, view.createYutButtonsCount,
                "TestYut 모드: GameControllerFX 생성자에서 createYutButtons()를 1회 호출해야 한다");
        assertNotNull(view.storedSelectThrowListener,
                "TestYut 모드: GameControllerFX 생성자에서 setSelectThrowListener(...)를 호출해야 한다");

        // 리스너 호출 전 카운터 기록
        int beforeShow   = view.showYutResultCount;
        int beforeUpdate = view.updateYutQueueCount;

        // SelectThrowListener를 직접 호출 (예: '4' 결과를 선택했다고 가정)
        view.storedSelectThrowListener.onThrowSelected(4);

        // 호출 직후 showYutResult()와 updateYutQueue()가 각각 1회씩 실행되어야 함
        assertEquals(beforeShow + 1, view.showYutResultCount,
                "SelectThrowListener.onThrowSelected() 호출 시 showYutResult(...)가 호출되어야 한다");
        assertEquals(beforeUpdate + 1, view.updateYutQueueCount,
                "SelectThrowListener.onThrowSelected() 호출 시 updateYutQueue(...)가 호출되어야 한다");

        // processYutResult가 실행된 뒤 isThrowing=false가 되므로, 이제 PieceClickListener를 호출하면 setStatus()가 카운트되어야 함
        int beforeStatus = view.setStatusCount;
        view.storedPieceClickListener.onPieceClicked(new Piece(1));
        assertTrue(view.setStatusCount > beforeStatus,
                "processYutResult 이후, PieceClickListener.onPieceClicked() 호출 시 setStatus(...)가 호출되어야 한다");
    }

    @Test
    void test_RandomYutMode_ThrowListener() {
        // RandomYut 모드: TestYut이 아닌 일반 Yut 구현체를 사용
        Yut randomYut = new RandomYut() {
            @Override public Integer getResult() {
                return 2;  // 예: 개(2)
            }
        };

        // (1) RandomYut 모드용 Game/ControllerFX 준비
        Game randomGame         = new StubGame(players, randomYut, board);
        StubGameViewFX randomView = new StubGameViewFX();
        GameControllerFX randomController = new GameControllerFX(/*stage*/ null, randomGame, randomView);

        // (2) 생성자에서 createRandomYutButtons()와 setThrowListener(...)이 호출되었는지 확인
        assertEquals(1, randomView.createRandomYutButtonsCount,
                "RandomYut 모드: GameControllerFX 생성자에서 createRandomYutButtons()를 1회 호출해야 한다");
        assertNotNull(randomView.storedThrowListener,
                "RandomYut 모드: GameControllerFX 생성자에서 setThrowListener(...)를 호출해야 한다");

        // (3) 리스너 호출 전 카운터 기록
        int prevShow   = randomView.showYutResultCount;
        int prevUpdate = randomView.updateYutQueueCount;

        // ThrowListener를 직접 호출 (윷 던지기 버튼 클릭 시뮬레이션)
        randomView.storedThrowListener.onThrow();

        // 호출 직후 showYutResult()와 updateYutQueue()가 각각 1회씩 실행되어야 함
        assertEquals(prevShow + 1, randomView.showYutResultCount,
                "ThrowListener.onThrow() 호출 시 showYutResult(...)가 호출되어야 한다");
        assertEquals(prevUpdate + 1, randomView.updateYutQueueCount,
                "ThrowListener.onThrow() 호출 시 updateYutQueue(...)가 호출되어야 한다");
    }
}
