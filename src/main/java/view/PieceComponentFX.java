package view;

import com.example.demo.Piece;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import listener.PieceClickListener;

public class PieceComponentFX extends StackPane {
    private final Piece piece;
    private PieceClickListener listener;

    public PieceComponentFX(Piece piece, PieceClickListener listener) {
        this.piece = piece;
        this.listener = listener;
        setPrefSize(38, 38);
        setMaxSize(38, 38);
        setMinSize(38, 38);
        setMouseTransparent(false);

        Circle circle = new Circle(15);
        circle.setFill(getColorByOwnerId(piece.getOwnerId()));
        circle.setStroke(Color.BLACK);

        Text text = new Text("P" + piece.getOwnerId());
        text.setFill(Color.WHITE);
        text.setFont(Font.font("SansSerif", 12));

        getChildren().addAll(circle, text);

        setOnMouseClicked((MouseEvent e) -> {
            if (listener != null) {
                listener.onPieceClicked(piece);
            }
        });
    }

    public void setClickListener(PieceClickListener listener) {
        this.listener = listener;
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