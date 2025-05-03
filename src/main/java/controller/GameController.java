package controller;

import com.example.demo.Game;
import com.example.demo.Piece;
import view.GameView;
//import view.GameView;

public class GameController {
    private final Game game;
    private final GameView gameView;


    public GameController(Game game) {
        this.game = game;
        this.gameView = new GameView(game);
    }

    // 게임 시작 메서드
    public void run() {
        game.start();
    }

    // Piece 클릭 시 처리할 로직
    public void selectPiece(Piece piece) {
        System.out.println("선택된 말: " + piece);
        // 여기서 게임 모델에 piece 움직이는 로직 넣으면 됨
        // 예: game.movePiece(piece);
    }
}
