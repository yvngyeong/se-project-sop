// ServiceViewFX.java
package view;

import com.example.demo.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class ServiceViewFX {

    private int playerCount = 2;
    private int pieceCount = 4;
    private String selectedYut = "지정 윷";
    private String selectedBoard = "4각형";

    private final Stage stage;

    public ServiceViewFX(Stage stage) {
        this.stage = stage;
    }

    public void show(Consumer<ServiceViewFX> onStart) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        root.getChildren().add(createSelection("플레이어 수", new String[]{"2", "3", "4"}, v -> playerCount = Integer.parseInt(v)));
        root.getChildren().add(createSelection("말 수", new String[]{"2", "3", "4", "5"}, v -> pieceCount = Integer.parseInt(v)));
        root.getChildren().add(createSelection("윷 종류", new String[]{"지정 윷", "랜덤 윷"}, v -> selectedYut = v));
        root.getChildren().add(createSelection("보드판", new String[]{"4각형", "5각형", "6각형"}, v -> selectedBoard = v));

        Button startBtn = new Button("게임 시작");
        startBtn.setOnAction(e -> onStart.accept(this));
        root.getChildren().add(startBtn);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("게임 설정");
        stage.show();
    }

    private HBox createSelection(String label, String[] options, Consumer<String> callback) {
        Label title = new Label(label + ": ");
        ToggleGroup group = new ToggleGroup();
        HBox box = new HBox(10, title);
        box.setAlignment(Pos.CENTER);

        for (String opt : options) {
            RadioButton rb = new RadioButton(opt);
            rb.setToggleGroup(group);
            if (opt.equals(options[0])) rb.setSelected(true); // 첫 항목 기본 선택
            rb.setOnAction(e -> callback.accept(opt));
            box.getChildren().add(rb);
        }
        return box;
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
}


