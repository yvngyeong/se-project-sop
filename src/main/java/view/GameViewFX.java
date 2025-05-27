package view;

import com.example.demo.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import listener.PieceClickListener;
import listener.SelectThrowListener;
import listener.ThrowListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameViewFX {
    private VBox root;
    private VBox rightPanel;
    private Label currentPlayerLabel;
    private Label yutQueueLabel;
    private Label statusLabel;
    private VBox yutButtonPanel;
    private ThrowListener throwListener;
    private SelectThrowListener selectThrowListener;
    private PieceClickListener pieceClickListener;
    private Map<Player, HBox> piecePanels = new HashMap<>();
    private BoardViewFX boardView;
    private StackPane boardPanel = new StackPane();
    private Map<Piece, PieceComponentFX> pieceComponentMap = new HashMap<>();
    private Runnable restartCallback;

    public void setRestartCallback(Runnable restartCallback) {
        this.restartCallback = restartCallback;
    }

    public void start(Stage stage, Game game) {
        root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(10));

        boardPanel.setPrefSize(500, 500);
        boardPanel.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
        boardPanel.setAlignment(Pos.CENTER);

        rightPanel = new VBox(10);
        rightPanel.setPrefWidth(250);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: gray;");

        for (Player player : game.getPlayers()) {
            Label playerLabel = new Label("플레이어 " + player.getId() + "의 남은 말:");
            HBox pieceBox = new HBox(5);
            piecePanels.put(player, pieceBox);
            rightPanel.getChildren().addAll(playerLabel, pieceBox);
        }

        currentPlayerLabel = new Label("현재 플레이어: ");
        yutQueueLabel = new Label("윷 결과: ");
        statusLabel = new Label("상태: 윷을 던져주세요");

        rightPanel.getChildren().addAll(currentPlayerLabel, yutQueueLabel, statusLabel);

        setBoardView(game.getBoard());
        mainLayout.getChildren().addAll(boardPanel, rightPanel);
        root.getChildren().add(mainLayout);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("JavaFX 윷놀이");
        stage.setScene(scene);
        stage.show();
    }

    public void setBoardView(Board board) {
        if (board instanceof TetragonalBoard tBoard) boardView = new TetragonalBoardViewFX(tBoard);
        else if (board instanceof PentagonalBoard pBoard) boardView = new PentagonalBoardViewFX(pBoard);
        else if (board instanceof HexagonalBoard hBoard) boardView = new HexagonalBoardViewFX(hBoard);
        else throw new IllegalArgumentException("알 수 없는 보드 타입입니다.");

        boardPanel.getChildren().clear();
        boardPanel.getChildren().add(boardView);
    }

    public void updateBoardPieces(List<Player> players) {
        if (boardView != null) boardView.refreshPieces(pieceComponentMap, players);
    }

    public void updateUnusedPieces(List<Player> players) {
        for (Player player : players) {
            HBox box = piecePanels.get(player);
            if (box != null) {
                box.getChildren().clear();
                for (Piece p : player.getUnusedPieces()) {
                    PieceComponentFX comp = pieceComponentMap.get(p);
                    if (comp != null) box.getChildren().add(comp);
                }
            }
        }
    }

    public void showGameOverDialog(int winnerId) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("게임 종료");
            alert.setHeaderText(null);
            alert.setContentText("🎉 플레이어 " + winnerId + "번이 승리했습니다!\n게임을 다시 시작하시겠습니까?");

            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK && restartCallback != null) restartCallback.run();
                else Platform.exit();
            });
        });
    }

    public void setPieceClickListener(PieceClickListener listener) {
        this.pieceClickListener = listener;
        for (PieceComponentFX comp : pieceComponentMap.values()) {
            comp.setClickListener(listener);
        }
    }

    public void initPieceComponents(List<Player> players, PieceClickListener pieceClickListener) {
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (!pieceComponentMap.containsKey(piece)) {
                    PieceComponentFX comp = new PieceComponentFX(piece, pieceClickListener);
                    pieceComponentMap.put(piece, comp);
                } else {
                    pieceComponentMap.get(piece).setClickListener(pieceClickListener);
                }
            }
        }
    }

    public void setThrowListener(ThrowListener listener) {
        this.throwListener = listener;
    }

    public void setSelectThrowListener(SelectThrowListener listener) {
        this.selectThrowListener = listener;
    }

    public void setStatus(String text) {
        Platform.runLater(() -> statusLabel.setText("상태: " + text));
    }

    public void updateCurrentPlayer(int playerId) {
        Platform.runLater(() -> currentPlayerLabel.setText("현재 플레이어: " + playerId + "번"));
    }

    public void updateYutQueue(List<Integer> yutQueue) {
        StringBuilder sb = new StringBuilder("윷 결과: ");
        for (int r : yutQueue) sb.append(getYutName(r)).append(" ");
        Platform.runLater(() -> yutQueueLabel.setText(sb.toString()));
    }

    public void showYutResult(int result) {
        Platform.runLater(() -> {
            Stage popup = new Stage();
            YutViewFX view = new YutViewFX();
            view.setYutResult(result);
            VBox box = new VBox(view);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(20));
            Scene scene = new Scene(box, 300, 250);
            popup.setScene(scene);
            popup.setTitle("윷 결과");
            popup.show();
            new Thread(() -> {
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                Platform.runLater(popup::close);
            }).start();
        });
    }

    public void showThrowButtonAgain(boolean isTestYut) {
        if (isTestYut) createYutButtons();
        else createRandomYutButtons();
    }

    public void createYutButtons() {
        hideYutResultButtons();
        yutButtonPanel = new VBox(5);
        ToggleGroup group = new ToggleGroup();

        String[] labels = {"빽도", "도", "개", "걸", "윷", "모"};
        int[] values = {-1, 1, 2, 3, 4, 5};

        for (int i = 0; i < labels.length; i++) {
            ToggleButton btn = new ToggleButton(labels[i]);
            int value = values[i];
            btn.setToggleGroup(group);
            btn.setUserData(value);
            btn.setOnAction(e -> {
                if (btn.isSelected() && selectThrowListener != null) {
                    selectThrowListener.onThrowSelected(value);
                }
                btn.setSelected(false);
            });
            yutButtonPanel.getChildren().add(btn);
        }
        rightPanel.getChildren().add(yutButtonPanel);
    }

    public void createRandomYutButtons() {
        hideYutResultButtons();
        Button throwButton = new Button("윷 던지기");
        throwButton.setOnAction(e -> {
            if (throwListener != null) throwListener.onThrow();
        });
        yutButtonPanel = new VBox(throwButton);
        rightPanel.getChildren().add(yutButtonPanel);
    }

    public void showYutResultButtons(List<Integer> yutQueue, SelectThrowListener listener) {
        hideYutResultButtons();
        VBox box = new VBox(5);
        for (Integer yut : yutQueue) {
            Button btn = new Button(getYutName(yut));
            btn.setOnAction(e -> listener.onThrowSelected(yut));
            box.getChildren().add(btn);
        }
        yutButtonPanel = box;
        rightPanel.getChildren().add(box);
    }

    public void hideYutResultButtons() {
        if (yutButtonPanel != null && rightPanel.getChildren().contains(yutButtonPanel)) {
            rightPanel.getChildren().remove(yutButtonPanel);
            yutButtonPanel = null;
            rightPanel.requestLayout();
        }
    }

    public String getYutName(int result) {
        return switch (result) {
            case -1 -> "빽도";
            case 1 -> "도";
            case 2 -> "개";
            case 3 -> "걸";
            case 4 -> "윷";
            case 5 -> "모";
            default -> "알수없음";
        };
    }
}