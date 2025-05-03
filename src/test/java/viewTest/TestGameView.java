package viewTest;

import com.example.demo.*;
import view.GameView;

public class TestGameView {
    public static void main(String[] args) {
        Yut yut = new TestYut();
        Board board = new TetragonalBoard();
        board.createNodes();
        board.createEdges();
        Game game = new Game(3, 3, yut, board);
        GameView gameView = new GameView(game);
        gameView.setVisible(true);
    }
}
