package controller;

import com.example.demo.Game;
import com.example.demo.Piece;
import com.example.demo.Player;
import view.GameView;
import listener.PieceClickListener;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final Game game;
    private final GameView gameView;


    public GameController(Game game, GameView gameView) {
        System.out.println("GameController 초기화 시작");
        this.game = game;
        this.gameView = gameView;
        System.out.println("GameView 생성 완료");

        gameView.setPieceClickListener(new PieceClickListener() {
            @Override
            public void onPieceClicked(Piece piece) {
                selectPiece(piece);
            }
        });

        gameView.setThrowListener(() -> {
            // 지정 윷, 랜덤 윷 따라 다르게 구현해야됨
            int result = game.getYut().getResult();
            gameView.showYutResult(result);
        });

        System.out.println("GameView 표시 설정 완료");
    }

    // 게임 시작하고 돌아가는 메서드
    public void run() {
        boolean gameOver = false;
        int currentPlayerIndex = 0;

        while(!gameOver){
            Player currentPlayer = game.getPlayers().get(currentPlayerIndex);
            gameView.updateCurrentPlayer(currentPlayer.getId());
        }

        List<Integer> yutResult = new ArrayList<>();
        // 모, 윷 아닐때까지 클릭으로 던지는 로직
        gameView.updateYutQueue(yutResult);
    }

    // Piece 클릭 시 처리할 로직
    public void selectPiece(Piece piece) {
        // 여기서 게임 모델에 piece 움직이는 로직 넣으면 됨
        // 예: game.getBoard.movePosition(piece, yutValue);
        gameView.updateBoardPieces(game.getPlayers()); //움직인 말 반영
        gameView.updateUnusedPieces(game.getPlayers()); //남은 말 반영
    }
}
