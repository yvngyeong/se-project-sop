package controller;

import com.example.demo.*;
import javafx.stage.Stage;
import listener.PieceClickListener;
import view.GameViewFX;
import view.ServiceViewFX;

import java.util.ArrayList;
import java.util.List;

public class GameControllerFX {
    private final Stage stage;
    private final Game game;
    private final GameViewFX gameView;
    private final List<Integer> yutQueue = new ArrayList<>();
    private boolean isThrowing = true;
    private int currentPlayerIndex = 0;
    private Piece selectedPiece = null;
    private Integer selectedYut = null;

    public GameControllerFX(Stage stage, Game game, GameViewFX gameView) {
        this.stage = stage;
        this.game = game;
        this.gameView = gameView;

        gameView.setRestartCallback(this::restart);
        gameView.setPieceClickListener(this::selectPiece);

        if (game.getYut() instanceof TestYut) {
            gameView.createYutButtons();
            gameView.setSelectThrowListener(result -> {
                if (!isThrowing) return;
                ((TestYut) game.getYut()).setNext(result);
                processYutResult(game.getYut().getResult());
            });
        } else {
            gameView.createRandomYutButtons();
            gameView.setThrowListener(() -> {
                if (!isThrowing) return;
                processYutResult(game.getYut().getResult());
            });
        }

        gameView.initPieceComponents(game.getPlayers(), this::selectPiece);
        gameView.updateUnusedPieces(game.getPlayers());
        gameView.updateCurrentPlayer(getCurrentPlayer().getId());
        gameView.setStatus("윷을 던져주세요.");
    }

    private void processYutResult(int result) {
        yutQueue.add(result);
        gameView.showYutResult(result);
        gameView.updateYutQueue(yutQueue);

        if (result == 4 || result == 5) {
            gameView.setStatus("윷/모! 한 번 더 던지세요.");
            return;
        }

        isThrowing = false;

        if (yutQueue.size() == 1) {
            selectedYut = yutQueue.get(0);
            if(selectedYut==-1 && !getCurrentPlayer().hasPiecesOnBoard()){
                yutQueue.clear();
                gameView.updateYutQueue(yutQueue);
                nextTurn();
            }else{
                gameView.setStatus(gameView.getYutName(selectedYut) + " 나왔습니다. \n말을 선택하세요.");
            }
        } else {
            selectedYut = null;
            gameView.setStatus("적용할 윷 결과를 선택해주세요.");
            gameView.showYutResultButtons(yutQueue, selected -> {
                selectedYut = selected;
                gameView.setStatus(gameView.getYutName(selected) + "(을)를 선택했습니다.\n말을 클릭하세요.");
            });
        }
    }

    private Player getCurrentPlayer() {
        return game.getPlayers().get(currentPlayerIndex);
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
        isThrowing = true;
        selectedPiece = null;
        selectedYut = null;
        yutQueue.clear();

        for (Piece piece : getCurrentPlayer().getPieces()) {
            piece.setWaitingForFinish(false);
        }

        gameView.updateCurrentPlayer(getCurrentPlayer().getId());
        gameView.setStatus("윷을 던져주세요.");
        gameView.showThrowButtonAgain(game.getYut() instanceof TestYut);
    }

    private void selectPiece(Piece piece) {
        if (isThrowing) {
            gameView.setStatus("먼저 윷을 던지세요.");
            return;
        }

        if (selectedYut == null) {
            gameView.setStatus("적용할 윷 결과를 먼저 선택하세요.");
            return;
        }

        if (piece.getOwnerId() != getCurrentPlayer().getId()) {
            gameView.setStatus("자신의 말만 선택할 수 있습니다.");
            return;
        }

        if(selectedYut==-1&&getCurrentPlayer().hasPiecesOnBoard()){
            if(piece.getPosition()==0&&!piece.isJustArrived()){
                gameView.setStatus("이미 판위에 말이 있습니다.");
                return;
            }
        }

        selectedPiece = piece;
        game.getBoard().movePosition(selectedPiece, selectedYut);
        boolean catched = game.getBoard().isCatched();

        yutQueue.remove(selectedYut);
        selectedYut = null;

        gameView.hideYutResultButtons();
        gameView.updateBoardPieces(game.getPlayers());
        gameView.updateUnusedPieces(game.getPlayers());
        gameView.updateYutQueue(yutQueue);

        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.checkWin()) {
            gameView.showGameOverDialog(currentPlayer.getId());
            return;
        }

        if (catched) {
            gameView.setStatus("상대 말을 잡았습니다! \n한 번 더 던지세요.");
            isThrowing = true;
            selectedPiece = null;
            gameView.showThrowButtonAgain(game.getYut() instanceof TestYut);
            return;
        }

        if (!yutQueue.isEmpty()) {
            gameView.setStatus("이동할 윷 결과를 선택하세요.");
            gameView.showYutResultButtons(yutQueue, selected -> {
                selectedYut = selected;
                gameView.setStatus(gameView.getYutName(selected) + "(을)를 선택했습니다. \n말을 클릭하세요.");
            });
        } else {
            selectedPiece = null;
            nextTurn();
        }
    }

    public void restart() {
        ServiceViewFX serviceView = new ServiceViewFX();
        serviceView.start(stage);

        serviceView.addStartButtonListener(() -> {
            Yut yut = serviceView.getYutObject();
            Board board = serviceView.getBoardObject();

            if (yut == null || board == null) {
                System.err.println("설정이 올바르지 않습니다.");
                return;
            }

            board.createNodes();
            board.createEdges();

            Game game = new Game(serviceView.getPlayerCount(), serviceView.getPieceCount(), yut, board);
            GameViewFX newGameView = new GameViewFX();
            newGameView.start(stage,game);
            new GameControllerFX(stage, game, newGameView);
        });
    }
}