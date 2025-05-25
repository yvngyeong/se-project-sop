package controller;

import com.example.demo.*;
import javafx.stage.Stage;
import view.GameViewFX;

import java.util.ArrayList;
import java.util.List;

public class GameControllerFX {
    private final Game game;
    private final GameViewFX gameView;
    private final List<Integer> yutQueue = new ArrayList<>();
    private boolean isThrowing = true;
    private int currentPlayerIndex = 0;
    private Piece selectedPiece = null;

    private Integer selectedYut = null;
    private boolean randomYutButtonCreated = false;

    public GameControllerFX(Stage primaryStage, Game game, GameViewFX gameView) {
        System.out.println("GameControllerFX 초기화 시작");
        this.game = game;
        this.gameView = gameView;
        System.out.println("GameViewFX 생성 완료");

        gameView.updateCurrentPlayer(getCurrentPlayer().getId());
        gameView.setStatus("윷을 던져주세요.");
        gameView.setPieceClickListener(piece -> selectPiece(piece));

        if (game.getYut() instanceof TestYut) {
            gameView.createYutButtons();
            gameView.setSelectThrowListener(result -> {
                if (!isThrowing) return;
                ((TestYut) game.getYut()).setNext(result);
                int realResult = game.getYut().getResult();
                processYutResult(realResult);
            });
        } else {
            if (!randomYutButtonCreated) {
                gameView.createRandomYutButtons();
                randomYutButtonCreated = true;
            }
            gameView.setThrowListener(() -> {
                if (!isThrowing) return;
                int result = game.getYut().getResult();
                processYutResult(result);
            });
        }

        gameView.initPieceComponents(game.getPlayers(), piece -> selectPiece(piece));
        gameView.updateBoardPieces(game.getPlayers());  // ✅ 말 표시
        gameView.updateUnusedPieces(game.getPlayers()); // ✅ 오른쪽 말 패널 표시
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

        if (!yutQueue.isEmpty()) {
            if (yutQueue.size() == 1) {
                selectedYut = yutQueue.get(0);
                gameView.setStatus(gameView.getYutName(selectedYut) + "이 나왔습니다. 말을 선택하세요.");
            } else {
                selectedYut = null;
                gameView.setStatus("적용할 윷 결과를 선택해주세요.");
                gameView.showYutResultButtons(yutQueue, selected -> {
                    selectedYut = selected;
                    gameView.setStatus(gameView.getYutName(selected) + "을 선택했습니다. 말을 클릭하세요.");
                });
            }
        } else {
            selectedPiece = null;
            nextTurn();
        }
    }

    private Player getCurrentPlayer() {
        return game.getPlayers().get(currentPlayerIndex);
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
        isThrowing = true;
        selectedPiece = null;

        for (Piece piece : getCurrentPlayer().getPieces()) {
            piece.setWaitingForFinish(false);
        }

        gameView.updateCurrentPlayer(getCurrentPlayer().getId());
        gameView.setStatus("윷을 던져주세요.");

        if (game.getYut() instanceof TestYut) {
            gameView.showThrowButtonAgain(true);
        }
    }

    public void selectPiece(Piece piece) {
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

        boolean hasPieceOnBoard = getCurrentPlayer().getPieces().stream()
                .anyMatch(p -> !p.isFinished() && p.getPosition() > 0);

        if (selectedYut == -1 && hasPieceOnBoard) {
            if (piece.getPosition() == 0 && !piece.isJustArrived()) {
                gameView.setStatus("이미 판위에 말이 있습니다.");
                return;
            }
        }

        selectedPiece = piece;
        System.out.println("🖱 클릭된 말: " + piece);


        if (!yutQueue.isEmpty()) {
            int yut = selectedYut;
            game.getBoard().movePosition(selectedPiece, yut);
            boolean catched = game.getBoard().isCatched();

            yutQueue.remove(selectedYut);
            if (yutQueue.isEmpty()) {
                gameView.hideYutResultButtons();
            }
            selectedYut = null;

            gameView.updateBoardPieces(game.getPlayers());
            gameView.updateUnusedPieces(game.getPlayers());
            gameView.updateYutQueue(yutQueue);

            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer.checkWin()) {
                gameView.showGameOverDialog(currentPlayer.getId());
                return;
            }

            if (catched) {
                gameView.setStatus("상대 말을 잡았습니다! 한 번 더 던지세요.");
                isThrowing = true;
                selectedPiece = null;
                if (game.getYut() instanceof TestYut) {
                    gameView.showThrowButtonAgain(true);
                }
                return;
            }

            if (!yutQueue.isEmpty()) {
                gameView.setStatus("이동할 윷 결과를 선택하세요.");
                gameView.showYutResultButtons(yutQueue, selected -> {
                    selectedYut = selected;
                    gameView.setStatus(gameView.getYutName(selected) + "(을)를 선택했습니다. 말을 클릭하세요.");
                });
            } else {
                selectedPiece = null;
                nextTurn();
            }
        } else {
            gameView.setStatus("적용할 윷 결과가 없습니다.");
        }
    }
}



