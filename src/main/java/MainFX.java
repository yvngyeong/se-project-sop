import javafx.application.Application;
import javafx.stage.Stage;
import com.example.demo.*;
import controller.GameControllerFX;
import view.GameViewFX;
import view.ServiceViewFX;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        System.out.println("MainFX 시작됨");

        // 서비스 화면 보여주기
        ServiceViewFX serviceView = new ServiceViewFX(primaryStage);

        serviceView.show(view -> {
            // 사용자가 선택한 게임 설정 정보 가져오기
            int playerCount = view.getPlayerCount();
            int pieceCount = view.getPieceCount();
            Yut yut = view.getYutObject();
            Board board = view.getBoardObject();

            // Game은 내부에서 Player 리스트를 생성함
            Game game = new Game(playerCount, pieceCount, yut, board);

            // 게임 화면 생성 및 실행
            GameViewFX gameViewFX = new GameViewFX();
            gameViewFX.setBoardView(board);
            gameViewFX.start(primaryStage, game);

            // 컨트롤러 연결
            new GameControllerFX(game, gameViewFX);

        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}






