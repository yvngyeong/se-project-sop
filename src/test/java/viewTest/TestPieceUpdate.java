package viewTest;

import com.example.demo.*;
import controller.GameController;
import view.GameView;

import java.util.ArrayList;
import java.util.List;

public class TestPieceUpdate {
    public static void main(String[] args) {
        // 게임 설정.. 바꿔도 다 가능
        Yut yut = new RandomYut();
        Board board = new PentagonalBoard();
        board.createNodes();
        board.createEdges();
        Game game = new Game(3, 3, yut, board);
        GameView gameView = new GameView(game);
        gameView.setVisible(true);
        GameController gameController = new GameController(game, gameView); // 던지기 버튼 누르면 팝업 뜸

        // 말 위치 이동
        Player player = game.getPlayers().get(0);
        Piece piece = player.getPieces().get(0);
        piece.setPosition(2);
        List<Piece> pieceList = new ArrayList<>();
        pieceList.add(piece);
        game.getBoard().getNodes().get(2).setOwnedPieces(pieceList);

        gameView.updateBoardPieces(game.getPlayers());  // 말 보드로 옮겨간거 확인
        gameView.updateUnusedPieces(game.getPlayers()); // 남은 말에서 사라진거 확인
    }
}
