package view;

import com.example.demo.Board;
import com.example.demo.CornerNode;
import com.example.demo.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HexagonalBoardView extends JPanel {

    private final Board board;
    private final int radius = 250; // 외곽 육각형 반지름
    private final int nodeSize = 30; // 모든 노드의 크기 동일
    private final Map<Integer, Point> nodePositions = new HashMap<>();

    public HexagonalBoardView(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        calculateNodePositions();
        drawEdges(g2);
        drawNodes(g2);
    }

    private void drawEdges(Graphics2D g2) {
        g2.setColor(Color.BLACK); // 선 색상
        g2.setStroke(new BasicStroke(2)); // 선 굵기

        // 일반적인 edge들
        for (int nodeId : board.getEdges().keySet()) {
            Point currentPos = nodePositions.get(nodeId);
            if (currentPos == null) continue;

            for (Integer neighborId : board.getEdges().get(nodeId)) {
                Point neighborPos = nodePositions.get(neighborId);
                if (neighborPos != null) {
                    g2.drawLine(currentPos.x, currentPos.y, neighborPos.x, neighborPos.y);
                }
            }
        }
        // 추가 edge들
        drawEdge(g2, 0, 1);
        drawEdge(g2, 5, 37);
        drawEdge(g2, 10, 39);
        drawEdge(g2, 15, 41);
        drawEdge(g2, 20, 31);
        drawEdge(g2, 30, 32);
        drawEdge(g2, 30, 36);
    }

    private void drawEdge(Graphics2D g2, int fromId, int toId) {
        Point from = nodePositions.get(fromId);
        Point to = nodePositions.get(toId);
        if (from != null && to != null) {
            g2.drawLine(from.x, from.y, to.x, to.y);
        }
    }

    private void calculateNodePositions() {
        nodePositions.clear();
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // 꼭짓점 각도 계산
        double[] angles = new double[6];
        for (int i = 0; i < 6; i++) {
            angles[i] = Math.toRadians(90 + i * 60);
        }

        Point[] originalCorners = new Point[6];
        for (int i = 0; i < 6; i++) {
            int x = (int) (cx + radius * Math.cos(angles[i]));
            int y = (int) (cy + radius * Math.sin(angles[i]));
            int rotatedX = cx - (y - cy);
            int rotatedY = cy + (x - cx);
            originalCorners[i] = new Point(rotatedX, rotatedY);
        }

        // 0~29 노드 (외곽)
        int nodeIdx = 0;
        for (int i = 0; i < 6; i++) {
            Point start = originalCorners[i];
            Point end = originalCorners[(i + 1) % 6];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                int x = (int) (start.x * (1 - t) + end.x * t);
                int y = (int) (start.y * (1 - t) + end.y * t);
                nodePositions.put(nodeIdx++, new Point(x, 2 * cy - y)); // 좌우 대칭
            }
        }

        // 30번: 중심
        nodePositions.put(30, new Point(cx, cy));

        // 31~35: 대각선
        int diagonalIdx = 31;
        double rotate = Math.toRadians(-60);
        for (int i = 0; i < 6; i++) {
            Point corner = originalCorners[i];
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0;
                int x = (int) (corner.x * (1 - t) + cx * t);
                int y = (int) (corner.y * (1 - t) + cy * t);
                y = 2 * cy - y; // 좌우 대칭

                int rotatedX = (int) (cx + (x - cx) * Math.cos(rotate) - (y - cy) * Math.sin(rotate));
                int rotatedY = (int) (cy + (x - cx) * Math.sin(rotate) + (y - cy) * Math.cos(rotate));

                rotatedX = 2 * cx - rotatedX;
                rotatedY = 2 * cy - rotatedY;

                nodePositions.put(diagonalIdx++, new Point(rotatedX, rotatedY));
            }
        }
    }

    private void drawNodes(Graphics2D g2) {
        for (Node node : board.getNodes()) {
            Point p = nodePositions.get(node.getNodeID());
            if (p == null) continue;

            g2.setColor(Color.LIGHT_GRAY);
            g2.fill(new Ellipse2D.Double(p.x - nodeSize / 2.0, p.y - nodeSize / 2.0, nodeSize, nodeSize));

            g2.setColor(Color.BLACK);
            g2.draw(new Ellipse2D.Double(p.x - nodeSize / 2.0, p.y - nodeSize / 2.0, nodeSize, nodeSize));

            if (node instanceof CornerNode) {
                int innerSize = nodeSize - 10;
                g2.draw(new Ellipse2D.Double(p.x - innerSize / 2.0, p.y - innerSize / 2.0, innerSize, innerSize));
            }

            /*
            // 노드 번호 표시. 없애도 됨.
            String text = String.valueOf(node.getNodeID());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2.drawString(text, p.x - textWidth / 2, p.y + nodeSize / 2 + 12);
            */
        }
    }
}
