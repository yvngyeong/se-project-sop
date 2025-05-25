import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import com.example.demo.*;
import controller.GameControllerFX;
import view.GameViewFX;
import view.ServiceViewFX;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        System.out.println("MainFX 시작됨");

        // 서비스 화면 보여주기
        ServiceViewFX serviceView = new ServiceViewFX();
        serviceView.start(primaryStage);

        serviceView.addStartButtonListener(() -> {
            // 사용자가 선택한 게임 설정 정보 가져오기
            int playerCount = serviceView.getPlayerCount();
            int pieceCount = serviceView.getPieceCount();
            Yut yut = serviceView.getYutObject();
            Board board = serviceView.getBoardObject();

            if (yut == null || board == null) {
                System.err.println("잘못된 설정값입니다.");
                return;
            }

            board.createNodes();
            board.createEdges();

            // Game 인스턴스 생성
            Game game = new Game(playerCount, pieceCount, yut, board);

            // GameViewFX 준비
            GameViewFX gameViewFX = new GameViewFX();
            gameViewFX.setBoardView(board);
            gameViewFX.start(primaryStage, game); // 여기서 Scene 설정

            // GameControllerFX 연결 (Stage 전달!)
            new GameControllerFX(primaryStage, game, gameViewFX);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
