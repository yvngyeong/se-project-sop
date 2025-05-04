package viewTest;

import com.example.demo.*;
import controller.GameController;
import view.GameView;

public class TestThrowYut {
    public static void main(String[] args) {
        // 게임 설정
        Yut yut = new RandomYut();
        Board board = new TetragonalBoard();
        board.createNodes();
        board.createEdges();
        Game game = new Game(3, 3, yut, board);
        GameView gameView = new GameView(game);
        gameView.setVisible(true);

        GameController gameController = new GameController(game, gameView); // 던지기 버튼 누르면 팝업 뜸
    }
}
