package view;

import com.example.demo.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import listener.PieceClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PentagonalBoardView extends BoardView {

    private final Board board;
    private final int radius = 250;
    private final int nodeSize = 30;
    private final Map<Integer, Point2D> nodePositions = new HashMap<>();

    public PentagonalBoardView(Board board) {
        this.board = board;
        setPrefSize(600, 600);
        setStyle("-fx-background-color: white;");

        calculateNodePositions();
        drawEdges();
        drawNodes();
    }


    private void drawEdges() {
        for (int nodeId : board.getEdges().keySet()) {
            Point2D currentPos = nodePositions.get(nodeId);
            if (currentPos == null) continue;

            for (Integer neighborId : board.getEdges().get(nodeId)) {
                Point2D neighborPos = nodePositions.get(neighborId);
                if (neighborPos != null) {
                    Line line = new Line(currentPos.getX(), currentPos.getY(), neighborPos.getX(), neighborPos.getY());
                    line.setStroke(Color.BLACK);
                    line.setStrokeWidth(2);
                    getChildren().add(line);
                }
            }
        }

        drawEdge(5, 26);
        drawEdge(10, 28);
        drawEdge(15, 31);
        drawEdge(25, 34);
        drawEdge(0, 1);
        drawEdge(5, 6);
        drawEdge(10, 11);
        drawEdge(15, 16);
        drawEdge(20, 21);
        drawEdge(25, 32);
    }

    private void drawEdge(int fromId, int toId) {
        Point2D from = nodePositions.get(fromId);
        Point2D to = nodePositions.get(toId);
        if (from != null && to != null) {
            Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2);
            getChildren().add(line);
        }
    }



    private void calculateNodePositions() {
        nodePositions.clear();
        double cx = 300;
        double cy = 300;

        // 꼭짓점 각도 계산
        double[] angles = new double[5];
        for (int i = 0; i < 5; i++) {
            angles[i] = Math.toRadians(90 + i * 72);
        }

        Point2D[] originalCorners = new Point2D[5];
        for (int i = 0; i < 5; i++) {
            double x = cx + radius * Math.cos(angles[i]);
            double y = cy + radius * Math.sin(angles[i]);
            originalCorners[i] = new Point2D(x, y);
        }

        // 가장 왼쪽 꼭짓점 기준 회전
        int leftMostIdx = 0;
        for (int i = 1; i < 5; i++) {
            if (originalCorners[i].getX() < originalCorners[leftMostIdx].getX()) {
                leftMostIdx = i;
            }
        }


        Point2D[] corners = new Point2D[5];
        for (int i = 0; i < 5; i++) {
            corners[i] = originalCorners[(leftMostIdx + i) % 5];
        }

        int nodeIdx = 0;
        for (int i = 0; i < 5; i++) {
            Point2D start = corners[i];
            Point2D end = corners[(i + 1) % 5];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                double x = start.getX() * (1 - t) + end.getX() * t;
                double y = start.getY() * (1 - t) + end.getY() * t;
                nodePositions.put(nodeIdx++, new Point2D(x, 2 * cy - y));
            }
        }
        // 25번: 중심
        nodePositions.put(25, new Point2D(cx, cy));

        // 26~35: 대각선
        int diagonalIdx = 26;
        double rotate = Math.toRadians(-72);
        for (int i = 0; i < 5; i++) {
            Point2D corner = corners[i];
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0;
                double x = (int) (cx * (1 - t) + corner.getX() * t);
                double y = (int) (cy * (1 - t) + corner.getY() * t);
                y = 2 * cy - y; // 좌우 대칭

                double rotatedX = cx + (x - cx) * Math.cos(rotate) - (y - cy) * Math.sin(rotate);
                double rotatedY = cy + (x - cx) * Math.sin(rotate) + (y - cy) * Math.cos(rotate);

                nodePositions.put(diagonalIdx++, new Point2D(rotatedX, rotatedY));
            }
        }
    }

    private void drawNodes() {
        for (Node node : board.getNodes()) {
            Point2D p = nodePositions.get(node.getNodeID());
            if (p == null) continue;

            Circle outer = new Circle(p.getX(), p.getY(), nodeSize / 2.0);
            outer.setFill(Color.LIGHTGRAY);
            outer.setStroke(Color.BLACK);
            getChildren().add(outer);

            if (node instanceof CornerNode) {
                Circle inner = new Circle(p.getX(), p.getY(), (nodeSize - 10) / 2.0);
                inner.setStroke(Color.BLACK);
                inner.setFill(Color.TRANSPARENT);
                getChildren().add(inner);
            }
        }
    }

    @Override
    public void refreshPieces(Map<Piece, PieceComponent> pieceComponentMap, List<Player> players) {
        this.getChildren().removeIf(c -> c instanceof PieceComponent || c instanceof GroupedPieceComponent);

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

            if (nodeId == 0) {
                piecesAtSamePos = piecesAtSamePos.stream()
                        .filter(Piece::isJustArrived)
                        .collect(Collectors.toList());

                if (piecesAtSamePos.isEmpty()) continue;
            }

            Point2D nodePos = nodePositions.get(nodeId);
            if (nodePos == null) continue;

            if (piecesAtSamePos.size() == 1) {
                Piece piece = piecesAtSamePos.get(0);
                PieceComponent comp = pieceComponentMap.get(piece);
                comp.setLayoutX(nodePos.getX() - 20);
                comp.setLayoutY(nodePos.getY() - 20);
                this.getChildren().add(comp);
            } else {
                List<Piece> normalizedPieces = new ArrayList<>();
                for (Piece p : piecesAtSamePos) {
                    for (Piece key : pieceComponentMap.keySet()) {
                        if (key == p) {
                            normalizedPieces.add(key);
                            break;
                        }
                    }
                }

                if (normalizedPieces.isEmpty()) continue;

                Piece referencePiece = normalizedPieces.get(0);
                PieceComponent refComp = pieceComponentMap.get(referencePiece);
                PieceClickListener listener = refComp.getListener();

                GroupedPieceComponent groupComp = new GroupedPieceComponent(normalizedPieces);
                groupComp.setClickListener(listener);
                groupComp.setLayoutX(nodePos.getX() - 20);
                groupComp.setLayoutY(nodePos.getY() - 20);
                this.getChildren().add(groupComp);
            }
        }
    }
}
