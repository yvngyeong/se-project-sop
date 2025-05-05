package controller;

import com.example.demo.Board;
import com.example.demo.Game;
import com.example.demo.Yut;
import view.GameView;
import view.ServiceView;

public class ServiceController {
    public ServiceController() {
        ServiceView serviceView = new ServiceView();

        serviceView.addStartButtonListener(e -> {
            int playerCount = serviceView.getPlayerCount();
            int pieceCount = serviceView.getPieceCount();
            Yut yut = serviceView.getYutObject();
            Board board = serviceView.getBoardObject();
            board.createNodes();
            board.createEdges();

            Game game = new Game(playerCount, pieceCount, yut, board);

            serviceView.dispose();

            GameView gameView = new GameView(game);
            gameView.setVisible(true);
            new GameController(game, gameView);
        });
    }
}
