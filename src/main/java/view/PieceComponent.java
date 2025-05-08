package view;

import com.example.demo.Piece;
import listener.PieceClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PieceComponent extends JComponent {
    private final Piece piece;
    private static final int DIAMETER = 30;
    private static final int PADDING = 4;   // 원이 GameView에서 잘려보여서 여유공간 용으로 추가
    private PieceClickListener listener;

    public PieceComponent(Piece piece, PieceClickListener listener) {
        this.piece = piece;
        int size = DIAMETER + PADDING*2;
        setPreferredSize(new Dimension(size, size));
        setSize(size, size);
        setOpaque(false); // 배경 투명 처리

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onPieceClicked(piece);
                }
            }
        });
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

    public void setClickListener(PieceClickListener listener) {
        this.listener = listener;
        for (MouseListener ml : getMouseListeners()) {
            removeMouseListener(ml);
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.onPieceClicked(piece);
                }
            }
        });
    }

    public PieceClickListener getListener() {
        return listener;
    }

    private void mouseClicked(){

    }
}

