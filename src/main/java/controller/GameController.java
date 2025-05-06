package controller;

import com.example.demo.*;
import view.GameView;
import listener.PieceClickListener;


import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final Game game;
    private final GameView gameView;
    private final List<Integer> yutQueue = new ArrayList<>(); //던진 결과 저장
    private boolean isThrowing = true; // 현재 던지는 중인지 여부
    private int currentPlayerIndex=0;
    private Piece selectedPiece = null;


    public GameController(Game game, GameView gameView) {
        System.out.println("GameController 초기화 시작");
        this.game = game;
        this.gameView = gameView;
        System.out.println("GameView 생성 완료");
        gameView.setRestartCallback(this::restart);


        gameView.setPieceClickListener(new PieceClickListener() {
            @Override
            public void onPieceClicked(Piece piece) {
                selectPiece(piece);
            }
        });

        // 윷 던지기 버튼 눌렸을 때
        if (game.getYut() instanceof TestYut) {
            // 지정 윷: 버튼 선택값을 직접 넘김
            gameView.createYutButtons(); //  여기서 버튼 만들기
            gameView.setSelectThrowListener(result -> {
                if (!isThrowing) return;

                ((TestYut) game.getYut()).setNext(result);
                int realResult = game.getYut().getResult();

                yutQueue.add(realResult);
                gameView.showYutResult(realResult);
                gameView.updateYutQueue(yutQueue);


                if (realResult == 4 || realResult == 5) {
                    gameView.setStatus("윷/모! 한 번 더 던지세요.");
                } else {
                    isThrowing = false;
                    gameView.setStatus("이동할 말을 선택하세요.");

                }

            });
        } else {
            gameView.createRandomYutButtons();
            // 랜덤 윷: 기존처럼 버튼 한 개
            gameView.setThrowListener(() -> {
                if (!isThrowing) return;

                int result = game.getYut().getResult();
                yutQueue.add(result);
                gameView.showYutResult(result);
                gameView.updateYutQueue(yutQueue);


                if (result == 4 || result == 5) {
                    gameView.setStatus("윷/모! 한 번 더 던지세요.");
                } else {
                    isThrowing = false;
                    gameView.setStatus("이동할 말을 선택하세요.");

                }
            });
        }
        gameView.updateCurrentPlayer(getCurrentPlayer().getId());
        gameView.setStatus("윷을 던져주세요.");

    }
    private Player getCurrentPlayer() {
        return game.getPlayers().get(currentPlayerIndex);
    }
    private void nextTurn() {

        currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
        isThrowing = true;
        selectedPiece = null;

        gameView.updateCurrentPlayer(getCurrentPlayer().getId());
        gameView.setStatus("윷을 던져주세요.");
    }

    // Piece 클릭 시 처리할 로직
    public void selectPiece(Piece piece) {


        if (isThrowing) {
            gameView.setStatus("먼저 윷을 던지세요.");
            return;
        }
        selectedPiece = piece;

        if (!yutQueue.isEmpty()) {
            int yut = yutQueue.remove(0);
            game.getBoard().movePosition(selectedPiece, yut);
            boolean catched = game.getBoard().isCatched();

            gameView.updateBoardPieces(game.getPlayers());
            gameView.updateUnusedPieces(game.getPlayers());
            gameView.updateYutQueue(yutQueue);

            Player currentPlayer = getCurrentPlayer();
            if (currentPlayer.checkWin())
            {
                gameView.showGameOverDialog(currentPlayer.getId());
                return;
            }

            if (catched) {
                gameView.setStatus("상대 말을 잡았습니다! 한 번 더 던지세요.");
                isThrowing = true;
                selectedPiece = null;
                return;
            }

            if (!yutQueue.isEmpty()) {
                gameView.setStatus("이동할 말을 선택하세요.");
            } else {
                selectedPiece = null;
                nextTurn();
            }
        } else {
            gameView.setStatus("적용할 윷 결과가 없습니다.");
        }

    }

    //게임 다시 시작하는 함수
    public void restart() {
        gameView.dispose(); // 현재 게임 창 닫기
        new ServiceController(); // 처음부터 다시 시작
    }



}

