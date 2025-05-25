package view;

import com.example.demo.Piece;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import listener.PieceClickListener;

public class PieceComponentFX extends StackPane {
    private static final double RADIUS = 15;
    private PieceClickListener listener;
    private final Piece piece;

    public PieceComponentFX(Piece piece, PieceClickListener listener) {
        this.piece = piece;
        this.listener = listener;

        setPrefSize(RADIUS * 2 + 8, RADIUS * 2 + 8);

        Circle circle = new Circle(RADIUS);
        circle.setFill(getColorByOwnerId(piece.getOwnerId()));
        circle.setStroke(Color.BLACK);

        Label label = new Label("P" + piece.getOwnerId());
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");

        getChildren().addAll(circle, label);

        //  클릭 이벤트는 이 메서드에서 따로 처리하게 위임
        setClickListener(listener);
    }

    public void setClickListener(PieceClickListener listener) {
        this.listener = listener;

        //  클릭 이벤트 핸들러를 다시 등록
        this.setOnMouseClicked((MouseEvent e) -> {
            if (this.listener != null) {
                this.listener.onPieceClicked(piece);
            }
        });
    }

    public PieceClickListener getListener() {
        return listener;
    }

    private Color getColorByOwnerId(int ownerId) {
        return switch (ownerId % 4) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }
}
