package view;

import com.example.demo.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GroupedPieceComponent extends JComponent {
    private final List<Piece> pieces;

    public GroupedPieceComponent(List<Piece> pieces) {
        this.pieces = pieces;
        setPreferredSize(new Dimension(40, 40)); // 기본 크기 지정
        setToolTipText("그룹: " + pieces.size() + "개");
        setOpaque(false); // 배경 투명
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 대표 색상: 첫 번째 말의 ownerId를 기준으로 예시 설정
        int ownerId = pieces.get(0).getOwnerId();
        g.setColor(ownerId == 1 ? Color.BLUE : Color.RED);
        g.fillOval(5, 5, 30, 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("x" + pieces.size(), 12, 22);
    }
}


