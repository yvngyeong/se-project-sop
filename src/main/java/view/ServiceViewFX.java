package view;

import com.example.demo.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ServiceViewFX {
    private int playerCount;
    private int pieceCount;
    private String selectedYut;
    private String selectedBoard;

    private final Button startButton = new Button("게임 시작");
    private final VBox root = new VBox(15);
    private final List<ToggleButton> allToggleButtons = new ArrayList<>();

    public void start(Stage stage) {
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        root.getChildren().addAll(
                createSelectionBox("플레이어 수", new String[]{"2", "3", "4"}, value -> playerCount = Integer.parseInt(value)),
                createSelectionBox("각 플레이어 말 수", new String[]{"2", "3", "4", "5"}, value -> pieceCount = Integer.parseInt(value)),
                createSelectionBox("윷 종류", new String[]{"지정 윷", "랜덤 윷"}, value -> selectedYut = value),
                createSelectionBox("보드판 종류", new String[]{"4각형", "5각형", "6각형"}, value -> selectedBoard = value)
        );

        HBox buttonBox = new HBox(startButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.getChildren().add(buttonBox);

        Scene scene = new Scene(root, 450, 400);
        stage.setTitle("윷놀이 게임 설정");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSelectionBox(String title, String[] options, ValueConsumer consumer) {
        VBox vbox = new VBox(5);
        Label label = new Label(title + ":");

        ToggleGroup group = new ToggleGroup();
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        for (String option : options) {
            ToggleButton button = new ToggleButton(option);
            button.setUserData(option);
            button.setToggleGroup(group);
            button.setOnAction(e -> {
                highlightSelected(group);
                consumer.accept(option);
            });

            styleToggleButton(button);
            buttonRow.getChildren().add(button);
            allToggleButtons.add(button);
        }

        vbox.getChildren().addAll(label, buttonRow);
        return vbox;
    }

    private void highlightSelected(ToggleGroup group) {
        for (ToggleButton btn : allToggleButtons) {
            if (btn.getToggleGroup() == group) {
                if (btn.isSelected()) {
                    btn.setStyle("-fx-background-color: #768ce4; -fx-text-fill: white;");
                } else {
                    btn.setStyle("");
                }
            }
        }
    }

    private void styleToggleButton(ToggleButton btn) {
        btn.setMinWidth(60);
        btn.setFocusTraversable(false);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getPieceCount() {
        return pieceCount;
    }

    public String getYutType() {
        return selectedYut;
    }

    public String getBoardType() {
        return selectedBoard;
    }

    public Yut getYutObject() {
        return switch (selectedYut) {
            case "지정 윷" -> new TestYut();
            case "랜덤 윷" -> new RandomYut();
            default -> null;
        };
    }

    public Board getBoardObject() {
        return switch (selectedBoard) {
            case "4각형" -> new TetragonalBoard();
            case "5각형" -> new PentagonalBoard();
            case "6각형" -> new HexagonalBoard();
            default -> null;
        };
    }

    public void addStartButtonListener(Runnable listener) {
        startButton.setOnAction(e -> listener.run());
    }

    private interface ValueConsumer {
        void accept(String value);
    }
}