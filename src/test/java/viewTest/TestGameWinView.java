package viewTest;

import com.example.demo.*;
import view.GameView;

import javax.swing.*;
import java.util.List;

public class TestGameWinView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 게임 생성
            Yut yut = new RandomYut();
            Board board = new TetragonalBoard();
            board.createNodes();
            board.createEdges();

            Game game = new Game(2, 3, yut, board); // 2명, 말 3개
            GameView gameView = new GameView(game);
            gameView.setVisible(true);

            // 2. 강제로 플레이어 1이 승리한 상태 만들기
            Player winner = game.getPlayers().get(0);
            for (Piece piece : winner.getPieces()) {
                piece.finish(); // 모든 말을 완주 상태로 만듦
            }

            // 3. 게임 종료 View 테스트
            gameView.showGameOverDialog(winner.getId());
        });
    }
}

