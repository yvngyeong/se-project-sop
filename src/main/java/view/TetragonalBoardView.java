package view;

import com.example.demo.*;
import listener.PieceClickListener;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;


public class TetragonalBoardView extends BoardView {
    private TetragonalBoard board;
    private Map<Integer, Point> nodePositions;

    public TetragonalBoardView(TetragonalBoard board) {
        this.board = board;
        this.nodePositions = new HashMap<>();

        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.WHITE);

        setNodePositions();
        setLayout(null);
    }

    private void setNodePositions() {
        int startX = 100;
        int startY = 100;
        int gap = 60;

        // 바깥 테두리 노드 배치 (위쪽 줄)
        nodePositions.put(10, new Point(startX + 0 * gap, startY + 0 * gap));
        nodePositions.put(9,  new Point(startX + 1 * gap, startY + 0 * gap));
        nodePositions.put(8,  new Point(startX + 2 * gap, startY + 0 * gap));
        nodePositions.put(7,  new Point(startX + 3 * gap, startY + 0 * gap));
        nodePositions.put(6,  new Point(startX + 4 * gap, startY + 0 * gap));
        nodePositions.put(5,  new Point(startX + 5 * gap, startY + 0 * gap));

        // 오른쪽 세로줄
        nodePositions.put(4,  new Point(startX + 5 * gap, startY + 1 * gap));
        nodePositions.put(3,  new Point(startX + 5 * gap, startY + 2 * gap));
        nodePositions.put(2,  new Point(startX + 5 * gap, startY + 3 * gap));
        nodePositions.put(1,  new Point(startX + 5 * gap, startY + 4 * gap));

        // 아래쪽 줄
        nodePositions.put(0,  new Point(startX + 5 * gap, startY + 5 * gap)); // 출발점
        nodePositions.put(19, new Point(startX + 4 * gap, startY + 5 * gap));
        nodePositions.put(18, new Point(startX + 3 * gap, startY + 5 * gap));
        nodePositions.put(17, new Point(startX + 2 * gap, startY + 5 * gap));
        nodePositions.put(16, new Point(startX + 1 * gap, startY + 5 * gap));
        nodePositions.put(15, new Point(startX + 0 * gap, startY + 5 * gap));

        // 왼쪽 세로줄
        nodePositions.put(14, new Point(startX + 0 * gap, startY + 4 * gap));
        nodePositions.put(13, new Point(startX + 0 * gap, startY + 3 * gap));
        nodePositions.put(12, new Point(startX + 0 * gap, startY + 2 * gap));
        nodePositions.put(11, new Point(startX + 0 * gap, startY + 1 * gap));

        int diagonalGap = (int)(gap * 0.75f);
        int offsetX = 30; // 오른쪽으로 이동
        int offsetY = 30; // 아래쪽으로 이동

        // 중심
        nodePositions.put(20, new Point(startX + 2 * gap + offsetX, startY + 2 * gap + offsetY));

        // 대각선 위쪽
        nodePositions.put(21, new Point(startX + 2 * gap + diagonalGap + offsetX, startY + 2 * gap - diagonalGap + offsetY));
        nodePositions.put(22, new Point(startX + 2 * gap + 2 * diagonalGap + offsetX, startY + 2 * gap - 2 * diagonalGap + offsetY));
        nodePositions.put(23, new Point(startX + 2 * gap - diagonalGap + offsetX, startY + 2 * gap - diagonalGap + offsetY));
        nodePositions.put(24, new Point(startX + 2 * gap - 2 * diagonalGap + offsetX, startY + 2 * gap - 2 * diagonalGap + offsetY));

        // 대각선 아래쪽
        nodePositions.put(25, new Point(startX + 2 * gap - diagonalGap + offsetX, startY + 2 * gap + diagonalGap + offsetY));
        nodePositions.put(26, new Point(startX + 2 * gap - 2 * diagonalGap + offsetX, startY + 2 * gap + 2 * diagonalGap + offsetY));
        nodePositions.put(27, new Point(startX + 2 * gap + diagonalGap + offsetX, startY + 2 * gap + diagonalGap + offsetY));
        nodePositions.put(28, new Point(startX + 2 * gap + 2 * diagonalGap + offsetX, startY + 2 * gap + 2 * diagonalGap + offsetY));

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawEdges((Graphics2D) g); // 선 그리기 호출
        drawBoard((Graphics2D) g);

    }

    private void drawBoard(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Node node : board.getNodes()) {
            Point p = nodePositions.get(node.getNodeID());
            if (p == null) continue;

            boolean isCorner = (node instanceof CornerNode);

            if (isCorner) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(p.x - 15, p.y - 15, 30, 30);
                g2.setColor(Color.BLACK);
                g2.drawOval(p.x - 15, p.y - 15, 30, 30); // 바깥 원
                g2.drawOval(p.x - 10, p.y - 10, 20, 20); // 안쪽 원
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(p.x - 15, p.y - 15, 30, 30);
                g2.setColor(Color.BLACK);
                g2.drawOval(p.x - 15, p.y - 15, 30, 30);
            }

            // 노드 번호 및 말 개수 표시
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            //String label = node.getNodeID() + " (" + node.getOwnedPieces().size() + ")";   노드 번호 + 말 갯수 표시
            //String label = String.valueOf(node.getNodeID()); //노드 번호만 표시
            //g2.drawString(label, p.x - 15, p.y + 30);
        }
    }


    private void drawEdges(Graphics2D g2) {
        g2.setColor(Color.BLACK); // 선 색상
        g2.setStroke(new BasicStroke(2)); // 선 굵기 통일 (2픽셀)

        // 바깥 사각형 프레임
        drawEdge(g2, 10, 9);
        drawEdge(g2, 9, 8);
        drawEdge(g2, 8, 7);
        drawEdge(g2, 7, 6);
        drawEdge(g2, 6, 5);
        drawEdge(g2, 5, 4);
        drawEdge(g2, 4, 3);
        drawEdge(g2, 3, 2);
        drawEdge(g2, 2, 1);
        drawEdge(g2, 1, 0);
        drawEdge(g2, 0, 19);
        drawEdge(g2, 19, 18);
        drawEdge(g2, 18, 17);
        drawEdge(g2, 17, 16);
        drawEdge(g2, 16, 15);
        drawEdge(g2, 15, 14);
        drawEdge(g2, 14, 13);
        drawEdge(g2, 13, 12);
        drawEdge(g2, 12, 11);
        drawEdge(g2, 11, 10);

        // 대각선 및 중심 연결
        drawEdge(g2, 5, 22);
        drawEdge(g2, 10, 24);
        drawEdge(g2, 24, 23);
        drawEdge(g2, 23, 20);
        drawEdge(g2, 20, 25);
        drawEdge(g2, 25, 26);
        drawEdge(g2, 20, 27);
        drawEdge(g2, 27, 28);
        drawEdge(g2, 21, 22);
        drawEdge(g2, 21, 20);
        drawEdge(g2, 26, 15);
        drawEdge(g2, 28, 0);



    }

    private void drawEdge(Graphics2D g2, int fromId, int toId) {
        Point from = nodePositions.get(fromId);
        Point to = nodePositions.get(toId);
        if (from != null && to != null) {
            g2.drawLine(from.x, from.y, to.x, to.y);
        }
    }

    @Override
    public void refreshPieces(Map<Piece, PieceComponent> pieceComponentMap, List<Player> players) {
        // 말만 제거
        Component[] comps = this.getComponents();
        for (Component c : comps) {
            if (c instanceof PieceComponent || c instanceof GroupedPieceComponent) {
                this.remove(c);
            }
        }

        Map<Integer, List<Piece>> positionMap = new HashMap<>();
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (!piece.isFinished()) {
                    int pos = piece.getPosition();
                    positionMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(piece);
                }
            }
        }

        for (Map.Entry<Integer, List<Piece>> entry : positionMap.entrySet()) {
            int nodeId = entry.getKey();
            List<Piece> piecesAtSamePos = entry.getValue();

            Point nodePos = nodePositions.get(nodeId);
            if (nodePos == null) continue;

            if (piecesAtSamePos.size() == 1) {
                Piece piece = piecesAtSamePos.get(0);
                PieceComponent comp = pieceComponentMap.get(piece);
                comp.setBounds(nodePos.x - 20, nodePos.y - 20, 40, 40);
                this.add(comp);
            } else {
                // 핵심: pieceComponentMap의 key와 동일한 인스턴스를 사용한 리스트 만들기
                List<Piece> normalizedPieces = new ArrayList<>();
                for (Piece p : piecesAtSamePos) {
                    for (Piece key : pieceComponentMap.keySet()) {
                        if (key == p) { // 인스턴스 동일성 비교
                            normalizedPieces.add(key);
                            break;
                        }
                    }
                }

                // 대표 piece에서 리스너 추출
                Piece referencePiece = normalizedPieces.get(0);
                PieceComponent refComp = pieceComponentMap.get(referencePiece);
                PieceClickListener listener = refComp.getListener();

                GroupedPieceComponent groupComp = new GroupedPieceComponent(normalizedPieces);
                groupComp.setClickListener(listener);
                groupComp.setBounds(nodePos.x - 20, nodePos.y - 20, 40, 40);
                this.add(groupComp);
            }
        }

        this.revalidate();
        this.repaint(); // board 다시 그리기

    }



}

