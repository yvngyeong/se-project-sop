package view;

import com.example.demo.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import listener.PieceClickListener;

import java.util.*;
import java.util.stream.Collectors;

public class HexagonalBoardViewFX extends BoardViewFX {

    private final Board board;
    private final int radius = 200;
    private final int nodeSize = 30;

    public HexagonalBoardViewFX(Board board) {
        this.board = board;
        setPrefSize(600, 600);
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

        drawEdge(0, 1);
        drawEdge(5, 37);
        drawEdge(10, 39);
        drawEdge(15, 41);
        drawEdge(20, 31);
        drawEdge(30, 32);
        drawEdge(30, 36);
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

        double[] angles = new double[6];
        for (int i = 0; i < 6; i++) {
            angles[i] = Math.toRadians(90 + i * 60);
        }

        Point2D[] originalCorners = new Point2D[6];
        for (int i = 0; i < 6; i++) {
            double x = cx + radius * Math.cos(angles[i]);
            double y = cy + radius * Math.sin(angles[i]);
            double rotatedX = cx - (y - cy);
            double rotatedY = cy + (x - cx);
            originalCorners[i] = new Point2D(rotatedX, rotatedY);
        }

        int nodeIdx = 0;
        for (int i = 0; i < 6; i++) {
            Point2D start = originalCorners[i];
            Point2D end = originalCorners[(i + 1) % 6];
            for (int j = 0; j < 5; j++) {
                double t = j / 5.0;
                double x = start.getX() * (1 - t) + end.getX() * t;
                double y = start.getY() * (1 - t) + end.getY() * t;
                nodePositions.put(nodeIdx++, new Point2D(x, 2 * cy - y));
            }
        }

        nodePositions.put(30, new Point2D(cx, cy));

        int diagonalIdx = 31;
        double rotate = Math.toRadians(-60);
        for (int i = 0; i < 6; i++) {
            Point2D corner = originalCorners[i];
            for (int j = 1; j <= 2; j++) {
                double t = j / 3.0;
                double x = corner.getX() * (1 - t) + cx * t;
                double y = corner.getY() * (1 - t) + cy * t;
                y = 2 * cy - y;

                double rotatedX = cx + (x - cx) * Math.cos(rotate) - (y - cy) * Math.sin(rotate);
                double rotatedY = cy + (x - cx) * Math.sin(rotate) + (y - cy) * Math.cos(rotate);

                rotatedX = 2 * cx - rotatedX;
                rotatedY = 2 * cy - rotatedY;

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


