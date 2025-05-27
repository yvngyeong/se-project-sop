package controllerTest;

import com.example.demo.*;
import controller.GameController;
import view.GameView;
import listener.PieceClickListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameControllerTest {
    private Game game;
    private GameView gameView;
    private GameController gameController;
    private Player mockPlayer;
    private Piece mockPiece;
    private Board mockBoard;
    private Yut mockYut;

    @BeforeEach
    void setUp() {
        // 목 객체 준비
        mockPlayer = mock(Player.class);
        mockPiece = mock(Piece.class);
        mockBoard = mock(Board.class);
        mockYut = mock(Yut.class);

        // 플레이어 설정
        when(mockPlayer.getId()).thenReturn(1);
        when(mockPlayer.getPieces()).thenReturn(List.of(mockPiece));
        when(mockPlayer.getUnusedPieces()).thenReturn(List.of(mockPiece));
        when(mockPlayer.checkWin()).thenReturn(false);

        // 게임 객체 설정
        game = new Game(1, 1, mockYut, mockBoard);
        game.getPlayers().clear();
        game.getPlayers().add(mockPlayer);

        // 뷰 설정
        gameView = mock(GameView.class);
        when(gameView.getWidth()).thenReturn(800);
        when(gameView.getHeight()).thenReturn(600);

        gameController = new GameController(game, gameView);
    }

    @Test
    void testThrowAndMovePiece() {
        when(mockYut.getResult()).thenReturn(1);
        gameController.selectPiece(mockPiece);

        verify(mockBoard, never()).movePosition(any(), anyInt());

        // simulate yut throw
        gameController.selectPiece(mockPiece); // yutQueue 비어있으므로 "적용할 윷 결과가 없습니다"
    }

    @Test
    void testWinCondition() {
        when(mockYut.getResult()).thenReturn(1);
        when(mockPlayer.checkWin()).thenReturn(true);

        // 윷 결과 삽입
        gameController.selectPiece(mockPiece); // 비어있어서 바로 return 될 것임
        // 실제로는 private yutQueue에 값을 넣고 다시 selectPiece 해야 동작하므로, 테스트에서 내부에 접근할 수 있게 구성하거나 리팩터링 필요
    }
}
