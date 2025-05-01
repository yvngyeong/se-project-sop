import com.example.demo.*;
import controller.GameController;
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

            Game game = new Game(playerCount, pieceCount, yut, board);
            serviceView.dispose();

            GameController gameController = new GameController(game);
            gameController.run();
        });
    }
}
