import com.example.demo.*;
import controller.GameController;
import view.GameView;
import view.ServiceView;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("FlatLaf 적용 실패: " + e.getMessage());
        }

        ServiceView serviceView = new ServiceView();

        serviceView.addStartButtonListener(e -> {
            int playerCount = serviceView.getPlayerCount();
            int pieceCount = serviceView.getPieceCount();
            Yut yut = serviceView.getYutObject();
            Board board = serviceView.getBoardObject();
            board.createNodes();
            board.createEdges();

            Game game = new Game(playerCount, pieceCount, yut, board);
            System.out.println("PlayerCount: " + playerCount);
            System.out.println("PieceCount: " + pieceCount);
            System.out.println("Yut: " + yut.getClass().getSimpleName());
            System.out.println("Board: " + board.getClass().getSimpleName());

            serviceView.dispose();

            GameView gameView = new GameView(game);
            gameView.setVisible(true);
            GameController gameController = new GameController(game, gameView);
//            gameController.run();
        });

    }
}
