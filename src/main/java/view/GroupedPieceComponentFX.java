package view;

import com.example.demo.Piece;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import listener.PieceClickListener;

import java.util.List;

public class GroupedPieceComponentFX extends StackPane {
    private final List<Piece> pieces;
    private PieceClickListener listener;

    public GroupedPieceComponentFX(List<Piece> pieces) {
        this.pieces = pieces;

        setPrefSize(40, 40);

        // 말 색상 설정
        Circle circle = new Circle(20);
        circle.setFill(getColorByOwnerId(pieces.get(0).getOwnerId()));

        // "xN" 텍스트 표시
        Label label = new Label("x" + pieces.size());
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        getChildren().addAll(circle, label);

        // 클릭 이벤트 처리: 대표 말 1개 기준
        this.setOnMouseClicked((MouseEvent e) -> {
            if (listener != null && !pieces.isEmpty()) {
                listener.onPieceClicked(pieces.get(0));
            }
        });

        // 툴팁 비슷하게 사용자 정보 추가 가능
        this.setUserData("그룹: " + pieces.size() + "개");
    }

    public void setClickListener(PieceClickListener listener) {
        this.listener = listener;
    }

    public PieceClickListener getListener() {
        return listener;
    }

    private Color getColorByOwnerId(int ownerId) {
        return switch (ownerId) {
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.ORANGE;
            case 4 -> Color.RED;
            default -> Color.GRAY;
        };
    }
}
