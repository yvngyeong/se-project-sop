package view;

import com.example.demo.*;
import listener.PieceClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PentagonalBoardView extends BoardView {

    private final Board board;
    private final int radius = 250;
    private final int nodeSize = 30;
    private final Map<Integer, Point> nodePositions = new HashMap<>();

    public PentagonalBoardView(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        setLayout(null);
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
        g2.setColor(Color.BLACK);
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
        drawEdge(g2, 5, 6);
        drawEdge(g2, 10, 11);
        drawEdge(g2, 15, 16);
        drawEdge(g2, 20, 21);
        drawEdge(g2, 25, 32);

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
        double[] angles = new double[5];
        for (int i = 0; i < 5; i++) {
            angles[i] = Math.toRadians(90 + i * 72);
        }

        Point[] originalCorners = new Point[5];
        for (int i = 0; i < 5; i++) {
            int x = (int) (cx + radius * Math.cos(angles[i]));
            int y = (int) (cy + radius * Math.sin(angles[i]));
            originalCorners[i] = new Point(x, y);
        }

        // 가장 왼쪽 꼭짓점 기준 회전
        int leftMostIdx = 0;
        for (int i = 1; i < 5; i++) {
            if (originalCorners[i].x < originalCorners[leftMostIdx].x) {
                leftMostIdx = i;
            }
        }

        Point[] corners = new Point[5];
        for (int i = 0; i < 5; i++) {
            corners[i] = originalCorners[(leftMostIdx + i) % 5];
        }

        // 0~29 노드 (외곽)
        int nodeIdx = 0;
        for (int i = 0; i < 5; i++) {
            Point start = corners[i];
            Point end = corners[(i + 1) % 5];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                int x = (int) (start.x * (1 - t) + end.x * t);
                int y = (int) (start.y * (1 - t) + end.y * t);
                nodePositions.put(nodeIdx++, new Point(x, 2 * cy - y)); // 좌우 대칭
            }
        }

        // 25번: 중심
        nodePositions.put(25, new Point(cx, cy));

        // 26~35: 대각선
        int diagonalIdx = 26;
        double rotate = Math.toRadians(-72);
        for (int i = 0; i < 5; i++) {
            Point corner = corners[i];
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0;
                int x = (int) (cx * (1 - t) + corner.x * t);
                int y = (int) (cy * (1 - t) + corner.y * t);
                y = 2 * cy - y; // 좌우 대칭

                int rotatedX = (int) (cx + (x - cx) * Math.cos(rotate) - (y - cy) * Math.sin(rotate));
                int rotatedY = (int) (cy + (x - cx) * Math.sin(rotate) + (y - cy) * Math.cos(rotate));

                nodePositions.put(diagonalIdx++, new Point(rotatedX, rotatedY));
            }
        }
    }

    private void drawNodes(Graphics2D g2) {
        //g2.setFont(new Font("Arial", Font.PLAIN, 12)); 노드 번호 표시를 위한 것. 삭제해도 됨

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
            // 노드 번호 표시를 위한 코드. 삭제해도 됨.
            String text = String.valueOf(node.getNodeID());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2.drawString(text, p.x - textWidth / 2, p.y + nodeSize / 2 + 12);

            */
        }
    }

    @Override
    public void refreshPieces(Map<Piece, PieceComponent> pieceComponentMap, List<Player> players) {
        // 모든 PieceComponent를 다시 위치시킴
        // 1. 기존에 이 BoardView에 올라와 있던 PieceComponent 전부 제거
        this.removeAll();

        // 2. 노드 위치 계산 보장 (paintComponent가 호출되지 않았을 수 있으므로)
        calculateNodePositions();

        // 3. 각 플레이어의 말 컴포넌트 배치
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (piece.getPosition()==0 && !piece.isFinished()) continue; // 보드에 없는 말은 무시

                Node node = board.getNodes().get(piece.getPosition());
                if (node == null) continue;

                Point nodePos = nodePositions.get(node.getNodeID());
                if (nodePos == null) continue;

                PieceComponent pieceComp = pieceComponentMap.get(piece);
                if (pieceComp == null) continue;

                Dimension size = pieceComp.getPreferredSize();
                int pieceX = nodePos.x - size.width / 2;
                int pieceY = nodePos.y - size.height / 2;

                pieceComp.setLocation(pieceX, pieceY);

                this.add(pieceComp); // 컴포넌트를 다시 boardView 위에 올림
            }
        }

        this.revalidate();
        this.repaint();
    }

}
