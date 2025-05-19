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

    private Integer selectedYut=null;  //사용자가 선택한 윷 결과
    private boolean randomYutButtonCreated = false;

    public GameController(Game game, GameView gameView) {
        System.out.println("GameController 초기화 시작");
        this.game = game;
        this.gameView = gameView;
        System.out.println("GameView 생성 완료");

        // ———— 초기 플레이어 정보 표시 ————
        gameView.updateCurrentPlayer(getCurrentPlayer().getId());

        // ———— 리스타트 콜백 등록 ————
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

        // ———— 말 컴포넌트 초기화 ————
        gameView.initPieceComponents(game.getPlayers(), piece -> selectPiece(piece));
    }


    private void processYutResult(int result){
        yutQueue.add(result);
        gameView.showYutResult(result);
        gameView.updateYutQueue(yutQueue);
        if (result == 4 || result == 5) {
            gameView.setStatus("윷/모! 한 번 더 던지세요.");
            return; //
        }

        isThrowing = false;

        if (!yutQueue.isEmpty()) {
            if (yutQueue.size() == 1) {
                // 자동 선택
                selectedYut = yutQueue.get(0);
                gameView.setStatus(gameView.getYutName(selectedYut) + "이 나왔습니다. 말을 선택하세요.");
            } else {
                // 두 개 이상일 때만 버튼 보여줌
                selectedYut = null;
                gameView.setStatus("적용할 윷 결과를 선택해주세요.");
                gameView.showYutResultButtons(yutQueue, selected -> {
                    selectedYut = selected;
                    gameView.setStatus(gameView.getYutName(selected) + "을 선택했습니다. 말을 클릭하세요.");
                });
            }
        } else {
            selectedPiece = null;
            nextTurn(); // 턴 넘김
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
            piece.setWaitingForFinish(false); // 다음 턴 시작할 때 리셋
        }


        gameView.updateCurrentPlayer(getCurrentPlayer().getId());
        gameView.setStatus("윷을 던져주세요.");
        //gameView.showThrowButtonAgain(game.getYut() instanceof TestYut);  //윷 던지기 버튼 다시 호출
        if (game.getYut() instanceof TestYut) {
            gameView.showThrowButtonAgain(true);
        }
    }

    // Piece 클릭 시 처리할 로직
    public void selectPiece(Piece piece) {


        if (isThrowing) {
            gameView.setStatus("먼저 윷을 던지세요.");
            return;
        }

        if (selectedYut == null) {
            gameView.setStatus("적용할 윷 결과를 먼저 선택하세요.");
            return;
        }

        //현재 플레이어가 클릭한 말의 주인인지 확인
        if(piece.getOwnerId()!=getCurrentPlayer().getId()){
            gameView.setStatus("자신의 말만 선택할 수 있습니다.");
            return;
        }

        selectedPiece = piece;

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
            if (currentPlayer.checkWin())
            {
                gameView.showGameOverDialog(currentPlayer.getId());
                return;
            }

            if (catched) {
                gameView.setStatus("상대 말을 잡았습니다! 한 번 더 던지세요.");
                isThrowing = true;
                selectedPiece = null;
                // 윷 던지기 버튼 다시 활성화
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

    //게임 다시 시작하는 함수
    public void restart() {
        gameView.dispose(); // 현재 게임 창 닫기
        new ServiceController(); // 처음부터 다시 시작
    }



}

