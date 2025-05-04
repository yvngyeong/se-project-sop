package viewTest;

import com.example.demo.*;
import view.GameView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TestCaptureView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 게임 환경 구성
            Yut yut = new RandomYut();
            Board board = new TetragonalBoard(); // 보드 종류는 자유롭게
            board.createNodes();
            board.createEdges();

            Game game = new Game(2, 3, yut, board); // 2명, 각 3개의 말
            GameView gameView = new GameView(game);
            gameView.setVisible(true);

            // 2. 테스트를 위한 말 상태 구성
            Player player1 = game.getPlayers().get(0);
            Player player2 = game.getPlayers().get(1);

            Piece p1 = player1.getPieces().get(0); // 플레이어1의 말
            Piece p2 = player2.getPieces().get(0); // 플레이어2의 말

            // 둘 다 같은 위치에 있다고 가정 (p2가 잡힘)
            p1.setPosition(5);
            p2.setPosition(5);

            // 보드 노드에 말 등록
            Node node = board.getNodes().get(5);
            List<Piece> nodePieces = new ArrayList<>();
            nodePieces.add(p1);
            nodePieces.add(p2);
            node.setOwnedPieces(nodePieces);

            // 잡기 상황 시뮬레이션 (p2가 잡힘)
            node.remove(p2);
            p2.setPosition(0); // 잡혀서 다시 시작점으로 이동
            board.getNodes().get(0).add(p2);

            // 3. View 갱신
            gameView.refreshAfterCapture(game.getPlayers());
        });
    }
}

