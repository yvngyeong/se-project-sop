package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class YutViewFX extends Pane {
    private final Canvas canvas;
    private int yutResult = 0;
    private int lightCount = 0;

    private final Color lightWood = Color.rgb(191, 144, 58);
    private final Color darkWood = Color.rgb(139, 69, 19);

    public YutViewFX() {
        canvas = new Canvas(300, 250);
        this.getChildren().add(canvas);
        draw();
    }

    public void setYutResult(int result) {
        this.yutResult = result;
        this.lightCount = switch (result) {
            case -1 -> 1;
            case 1, 2, 3, 4 -> result;
            case 5 -> 0;
            default -> throw new IllegalArgumentException("YutResult는 -1,1,2,3,4,5 중 하나여야 합니다.");
        };
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double width = 40;
        double height = 120;
        double startX = 50;
        double startY = 50;

        for (int i = 0; i < 4; i++) {
            double x = startX + i * (width + 20);
            Color barColor = (i < lightCount) ? lightWood : darkWood;

            gc.setFill(barColor);
            gc.fillRoundRect(x, startY, width, height, 20, 20);

            if (yutResult == -1 && i < lightCount) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(4);
                gc.strokeLine(x + 10, startY + 10, x + width - 10, startY + height - 10);
                gc.strokeLine(x + width - 10, startY + 10, x + 10, startY + height - 10);
            }
        }

        // 결과 텍스트 표시
        String resultText = switch (yutResult) {
            case -1 -> "빽도";
            case 1 -> "도";
            case 2 -> "개";
            case 3 -> "걸";
            case 4 -> "윷";
            case 5 -> "모";
            default -> "";
        };

        gc.setFont(Font.font("SansSerif", 20));
        gc.setFill(Color.BLACK);

        Text textNode = new Text(resultText);
        textNode.setFont(Font.font("SansSerif", 20));
        double textWidth = textNode.getLayoutBounds().getWidth();

        gc.fillText(resultText, (canvas.getWidth() - textWidth) / 2, startY + height + 40);
    }
}

