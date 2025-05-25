package view;

import com.example.demo.Piece;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import listener.PieceClickListener;

import java.util.List;

public class GroupedPieceComponentFX extends Region {
    private final List<Piece> pieces;
    private PieceClickListener listener;
    private Circle circle;
    private Text countText;

    public GroupedPieceComponentFX(List<Piece> pieces) {
        this.pieces = pieces;
        setPrefSize(40, 40);
        setMinSize(40, 40);
        setMaxSize(40, 40);
        setStyle("-fx-background-color: transparent;");

        drawGroup();
        setupClickHandler();
    }

    private void drawGroup() {
        getChildren().clear();

        if (pieces.isEmpty()) return;

        int ownerId = pieces.get(0).getOwnerId();
        Color color = switch (ownerId) {
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.ORANGE;
            case 4 -> Color.RED;
            default -> Color.GRAY;
        };

        circle = new Circle(20, 20, 15);
        circle.setFill(color);

        countText = new Text("x" + pieces.size());
        countText.setFont(Font.font("Arial", 12));
        countText.setFill(Color.WHITE);
        countText.setX(20 - 8);
        countText.setY(20 + 4);

        getChildren().addAll(circle, countText);
    }

    private void setupClickHandler() {
        setOnMouseClicked((MouseEvent e) -> {
            if (listener != null && !pieces.isEmpty()) {
                listener.onPieceClicked(pieces.get(0));
            }
        });
    }

    public void setClickListener(PieceClickListener listener) {
        this.listener = listener;
    }
}