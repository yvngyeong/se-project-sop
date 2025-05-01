package view;

import com.example.demo.Piece;

import javax.swing.*;
import java.awt.*;

public class PieceView extends JComponent {
    private final Piece piece;
    private static final int DIAMETER = 30;

    public PieceView(Piece piece) {
        this.piece = piece;
        setPreferredSize(new Dimension(DIAMETER, DIAMETER));
        setSize(DIAMETER, DIAMETER);
        setOpaque(false); // 배경 투명 처리
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 색상 지정
        Color color = switch (piece.getOwnerId() % 4) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 원 그리기
        g2.setColor(color);
        g2.fillOval(0, 0, DIAMETER, DIAMETER);

        // 테두리
        g2.setColor(Color.BLACK);
        g2.drawOval(0, 0, DIAMETER, DIAMETER);

        // 말 주인 번호 표시
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        String text = "P" + piece.getOwnerId();
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2.drawString(text, (DIAMETER - textWidth) / 2, (DIAMETER + textHeight / 2) / 2);
    }
}
