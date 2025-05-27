package view;

import com.example.demo.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import listener.PieceClickListener;

import java.util.*;
import java.util.stream.Collectors;

public class PentagonalBoardViewFX extends BoardViewFX {

    private final Board board;
    private final int radius = 200;
    private final int nodeSize = 25;

    public PentagonalBoardViewFX(Board board) {
        this.board = board;
        setPrefSize(500, 500); // 전체 크기도 약간 줄임
        setStyle("-fx-background-color: white;");
        calculateNodePositions();
        drawEdges();
        drawBoard();
    }

    private void drawBoard() {
        getChildren().clear();
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

    private void calculateNodePositions() {
        nodePositions.clear();
        double cx = 250;
        double cy = 250;

        // 5개의 꼭짓점 각도 계산
        double[] angles = new double[5];
        for (int i = 0; i < 5; i++) {
            angles[i] = Math.toRadians(90 + i * 72);
        }

        // 원형 꼭짓점 좌표 계산
        Point2D[] originalCorners = new Point2D[5];
        for (int i = 0; i < 5; i++) {
            double x = cx + radius * Math.cos(angles[i]);
            double y = cy + radius * Math.sin(angles[i]);
            originalCorners[i] = new Point2D(x, y);
        }

        // 가장 왼쪽 꼭짓점 찾기
        int leftMostIdx = 0;
        for (int i = 1; i < 5; i++) {
            if (originalCorners[i].getX() < originalCorners[leftMostIdx].getX()) {
                leftMostIdx = i;
            }
        }

        // 꼭짓점 재정렬
        Point2D[] corners = new Point2D[5];
        for (int i = 0; i < 5; i++) {
            corners[i] = originalCorners[(leftMostIdx + i) % 5];
        }

        // 외곽 노드 좌표 계산
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

        // 중심 노드
        nodePositions.put(25, new Point2D(cx, cy));

        // 대각선 노드
        int diagonalIdx = 26;
        double rotate = Math.toRadians(-72);
        for (int i = 0; i < 5; i++) {
            Point2D corner = corners[i];
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0;
                double x = cx * (1 - t) + corner.getX() * t;
                double y = cy * (1 - t) + corner.getY() * t;
                y = 2 * cy - y;

                double dx = x - cx;
                double dy = y - cy;

                double rotatedX = cx + dx * Math.cos(rotate) - dy * Math.sin(rotate);
                double rotatedY = cy + dx * Math.sin(rotate) + dy * Math.cos(rotate);

                nodePositions.put(diagonalIdx++, new Point2D(rotatedX, rotatedY));
            }
        }
    }


    @Override
    public void refreshPieces(Map<Piece, PieceComponentFX> pieceComponentMap, List<Player> players) {
        getChildren().removeIf(c -> c instanceof PieceComponentFX || c instanceof GroupedPieceComponentFX);

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
            List<Piece> pieces = entry.getValue();

            if (nodeId == 0) {
                pieces = pieces.stream().filter(Piece::isJustArrived).collect(Collectors.toList());
                if (pieces.isEmpty()) continue;
            }

            Point2D pos = nodePositions.get(nodeId);
            if (pos == null) continue;

            if (pieces.size() == 1) {
                Piece piece = pieces.get(0);
                PieceComponentFX comp = pieceComponentMap.get(piece);
                comp.setLayoutX(pos.getX() - 20);
                comp.setLayoutY(pos.getY() - 20);
                getChildren().add(comp);
            } else {
                List<Piece> normalized = new ArrayList<>();
                for (Piece p : pieces) {
                    for (Piece key : pieceComponentMap.keySet()) {
                        if (key == p) {
                            normalized.add(key);
                            break;
                        }
                    }
                }

                if (normalized.isEmpty()) continue;

                Piece referencePiece = normalized.get(0);
                PieceComponentFX refComp = pieceComponentMap.get(referencePiece);
                PieceClickListener listener = refComp.getListener();

                GroupedPieceComponentFX groupComp = new GroupedPieceComponentFX(normalized);
                groupComp.setClickListener(listener);
                groupComp.setLayoutX(pos.getX() - 20);
                groupComp.setLayoutY(pos.getY() - 20);
                getChildren().add(groupComp);
            }
        }
    }
}
