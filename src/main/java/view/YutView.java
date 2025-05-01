package view;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class YutView extends JPanel {
    // 색상 정의
    private final Color lightWood = new Color(191, 144, 58);  // 연한 갈색
    private final Color darkWood = new Color(139, 69, 19);    // 진한 갈색

    private int yutResult = 0;   // -1, 1, 2, 3, 4, 5
    private int lightCount = 0;  // 빽도:1, 도:1, 개:2, 걸:3, 윷:4, 모:0

    public YutView() {
        setBackground(Color.WHITE);
    }

    // Yut 결과값에 따라 lightCount 설정
    public void setYutResult(int result) {
        this.yutResult = result;

        switch (result) {
            case -1 -> lightCount = 1; // 빽도
            case 1, 2, 3, 4 -> lightCount = result; // 도 ~ 윷
            case 5 -> lightCount = 0; // 모
            default -> throw new IllegalArgumentException("YutResult는 -1,1,2,3,4,5 중 하나여야 합니다.");
        }

        repaint(); // 화면 갱신
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = 40;
        int height = 120;
        int startX = 50;
        int startY = 50;

        for (int i = 0; i < 4; i++) {
            int x = startX + i * (width + 20);
            Color barColor = (i < lightCount) ? lightWood : darkWood;

            g2.setColor(barColor);
            g2.fillRoundRect(x, startY, width, height, 20, 20);

            // X 표시 (yutResult == -1이고, 이 막대기가 연한 색이면)
            if (yutResult == -1 && i < lightCount) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(4)); // 두꺼운 선
                g2.drawLine(x + 10, startY + 10, x + width - 10, startY + height - 10); // \
                g2.drawLine(x + width - 10, startY + 10, x + 10, startY + height - 10); // /
            }
        }

        // 결과 텍스트 표시
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));

        String resultText = switch (yutResult) {
            case -1 -> "빽도";
            case 1 -> "도";
            case 2 -> "개";
            case 3 -> "걸";
            case 4 -> "윷";
            case 5 -> "모";
            default -> "";
        };

        int textY = startY + height + 40;
        int panelWidth = getWidth();
        int textWidth = g2.getFontMetrics().stringWidth(resultText);

        g2.drawString(resultText, (panelWidth - textWidth) / 2, textY);
    }
}
