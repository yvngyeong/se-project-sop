package controller;

import com.example.demo.Game;
//import view.GameView;

public class GameController {
    private final Game game;
 //  private final GameView gameview;


    public GameController(Game game /* , GameView gameView*/) {
        this.game = game;
    //    this.gameview = gameView;
    }

    // 게임 시작 메서드
    public void run() {
        game.start();
    }
}
