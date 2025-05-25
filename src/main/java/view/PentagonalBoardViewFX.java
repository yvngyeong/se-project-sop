package view;

import com.example.demo.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import listener.PieceClickListener;

import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;

public class PentagonalBoardViewFX extends BoardViewFX {
    private final Board board;
    private final int radius = 200;
    private final int nodeSize = 25;
    private final Map<Integer, Point> nodePositions = new HashMap<>();

    public PentagonalBoardViewFX(Board board) {
        this.board = board;
        setPrefSize(500, 500); // 전체 크기도 약간 줄임
        setStyle("-fx-background-color: white;");
        calculateNodePositions();
        drawBoard();
    }

    private void drawBoard() {
        getChildren().clear();
        drawEdges();
        drawNodes();
    }

    private void drawEdges() {
        for (int nodeId : board.getEdges().keySet()) {
            Point currentPos = nodePositions.get(nodeId);
            if (currentPos == null) continue;

            for (Integer neighborId : board.getEdges().get(nodeId)) {
                Point neighborPos = nodePositions.get(neighborId);
                if (neighborPos != null) {
                    javafx.scene.shape.Line line = new javafx.scene.shape.Line(
                            currentPos.x, currentPos.y, neighborPos.x, neighborPos.y);
                    line.setStroke(Color.BLACK);
                    line.setStrokeWidth(2);
                    getChildren().add(line);
                }
            }
        }

        drawExtraEdge(5, 26);
        drawExtraEdge(10, 28);
        drawExtraEdge(15, 31);
        drawExtraEdge(25, 34);
        drawExtraEdge(0, 1);
        drawExtraEdge(5, 6);
        drawExtraEdge(10, 11);
        drawExtraEdge(15, 16);
        drawExtraEdge(20, 21);
        drawExtraEdge(25, 32);
    }

    private void drawExtraEdge(int fromId, int toId) {
        Point from = nodePositions.get(fromId);
        Point to = nodePositions.get(toId);
        if (from != null && to != null) {
            javafx.scene.shape.Line line = new javafx.scene.shape.Line(from.x, from.y, to.x, to.y);
            line.setStroke(Color.BLACK);
            line.setStrokeWidth(2);
            getChildren().add(line);
        }
    }

    private void drawNodes() {
        for (Node node : board.getNodes()) {
            Point p = nodePositions.get(node.getNodeID());
            if (p == null) continue;

            Circle circle = new Circle(p.x, p.y, nodeSize / 2.0);
            circle.setFill(Color.LIGHTGRAY);
            circle.setStroke(Color.BLACK);

            getChildren().add(circle);

            if (node instanceof CornerNode) {
                Circle inner = new Circle(p.x, p.y, (nodeSize - 10) / 2.0);
                inner.setFill(Color.TRANSPARENT);
                inner.setStroke(Color.BLACK);
                getChildren().add(inner);
            }
        }
    }

    private void calculateNodePositions() {
        nodePositions.clear();
        int cx = 250;
        int cy = 250;

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

        int nodeIdx = 0;
        for (int i = 0; i < 5; i++) {
            Point start = corners[i];
            Point end = corners[(i + 1) % 5];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                int x = (int) (start.x * (1 - t) + end.x * t);
                int y = (int) (start.y * (1 - t) + end.y * t);
                nodePositions.put(nodeIdx++, new Point(x, 2 * cy - y));
            }
        }

        nodePositions.put(25, new Point(cx, cy));

        int diagonalIdx = 26;
        double rotate = Math.toRadians(-72);
        for (int i = 0; i < 5; i++) {
            Point corner = corners[i];
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0;
                int x = (int) (cx * (1 - t) + corner.x * t);
                int y = (int) (cy * (1 - t) + corner.y * t);
                y = 2 * cy - y;

                int rotatedX = (int) (cx + (x - cx) * Math.cos(rotate) - (y - cy) * Math.sin(rotate));
                int rotatedY = (int) (cy + (x - cx) * Math.sin(rotate) + (y - cy) * Math.cos(rotate));

                nodePositions.put(diagonalIdx++, new Point(rotatedX, rotatedY));
            }
        }
    }

    @Override
    public void refreshPieces(Map<Piece, PieceComponentFX> pieceComponentMap, List<Player> players) {
        getChildren().removeIf(node -> node instanceof PieceComponentFX || node instanceof GroupedPieceComponentFX);

        Map<Integer, List<Piece>> positionMap = new HashMap<>();
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (!piece.isFinished()) {
                    positionMap.computeIfAbsent(piece.getPosition(), k -> new ArrayList<>()).add(piece);
                }
            }
        }

        for (Map.Entry<Integer, List<Piece>> entry : positionMap.entrySet()) {
            int nodeId = entry.getKey();
            List<Piece> pieces = entry.getValue();

            if (nodeId == 0) {
                pieces = pieces.stream().filter(Piece::isJustArrived).collect(Collectors.toList());
                if (pieces.isEmpty()) continue;
            }

            Point pos = nodePositions.get(nodeId);
            if (pos == null) continue;

            if (pieces.size() == 1) {
                Piece piece = pieces.get(0);
                PieceComponentFX comp = pieceComponentMap.get(piece);
                if (comp != null) {
                    comp.setLayoutX(pos.x - 20);
                    comp.setLayoutY(pos.y - 20);
                    getChildren().add(comp);
                }
            } else {
                List<Piece> actualRefs = pieces.stream().map(p -> pieceComponentMap.keySet().stream().filter(k -> k == p).findFirst().orElse(null)).filter(Objects::nonNull).toList();
                if (actualRefs.isEmpty()) continue;

                Piece reference = actualRefs.get(0);
                PieceClickListener listener = pieceComponentMap.get(reference).getListener();

                GroupedPieceComponentFX group = new GroupedPieceComponentFX(actualRefs);
                group.setClickListener(listener);
                group.setLayoutX(pos.x - 20);
                group.setLayoutY(pos.y - 20);
                getChildren().add(group);
            }
        }
    }
}
