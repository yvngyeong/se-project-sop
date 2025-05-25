package view;

import com.example.demo.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import listener.PieceClickListener;

import java.util.*;
import java.util.stream.Collectors;

public class TetragonalBoardViewFX extends BoardViewFX {
    private final TetragonalBoard board;

    public TetragonalBoardViewFX(TetragonalBoard board) {
        this.board = board;
        setPrefSize(500, 500);
        setStyle("-fx-background-color: white;");


        setNodePositions();
        drawEdges();
        drawBoard();
    }

    private void setNodePositions() {
        int startX = 100;
        int startY = 100;
        int gap = 60;
        int diagonalGap = (int)(gap * 0.75f);
        int offsetX = 30;
        int offsetY = 30;

        putNodePosition(10, new Point2D(startX + 0 * gap, startY + 0 * gap));
        putNodePosition(9,  new Point2D(startX + 1 * gap, startY + 0 * gap));
        putNodePosition(8,  new Point2D(startX + 2 * gap, startY + 0 * gap));
        putNodePosition(7,  new Point2D(startX + 3 * gap, startY + 0 * gap));
        putNodePosition(6,  new Point2D(startX + 4 * gap, startY + 0 * gap));
        putNodePosition(5,  new Point2D(startX + 5 * gap, startY + 0 * gap));

        putNodePosition(4,  new Point2D(startX + 5 * gap, startY + 1 * gap));
        putNodePosition(3,  new Point2D(startX + 5 * gap, startY + 2 * gap));
        putNodePosition(2,  new Point2D(startX + 5 * gap, startY + 3 * gap));
        putNodePosition(1,  new Point2D(startX + 5 * gap, startY + 4 * gap));

        putNodePosition(0,  new Point2D(startX + 5 * gap, startY + 5 * gap));
        putNodePosition(19, new Point2D(startX + 4 * gap, startY + 5 * gap));
        putNodePosition(18, new Point2D(startX + 3 * gap, startY + 5 * gap));
        putNodePosition(17, new Point2D(startX + 2 * gap, startY + 5 * gap));
        putNodePosition(16, new Point2D(startX + 1 * gap, startY + 5 * gap));
        putNodePosition(15, new Point2D(startX + 0 * gap, startY + 5 * gap));

        putNodePosition(14, new Point2D(startX + 0 * gap, startY + 4 * gap));
        putNodePosition(13, new Point2D(startX + 0 * gap, startY + 3 * gap));
        putNodePosition(12, new Point2D(startX + 0 * gap, startY + 2 * gap));
        putNodePosition(11, new Point2D(startX + 0 * gap, startY + 1 * gap));

        putNodePosition(20, new Point2D(startX + 2 * gap + offsetX, startY + 2 * gap + offsetY));
        putNodePosition(21, new Point2D(startX + 2 * gap + diagonalGap + offsetX, startY + 2 * gap - diagonalGap + offsetY));
        putNodePosition(22, new Point2D(startX + 2 * gap + 2 * diagonalGap + offsetX, startY + 2 * gap - 2 * diagonalGap + offsetY));
        putNodePosition(23, new Point2D(startX + 2 * gap - diagonalGap + offsetX, startY + 2 * gap - diagonalGap + offsetY));
        putNodePosition(24, new Point2D(startX + 2 * gap - 2 * diagonalGap + offsetX, startY + 2 * gap - 2 * diagonalGap + offsetY));
        putNodePosition(25, new Point2D(startX + 2 * gap - diagonalGap + offsetX, startY + 2 * gap + diagonalGap + offsetY));
        putNodePosition(26, new Point2D(startX + 2 * gap - 2 * diagonalGap + offsetX, startY + 2 * gap + 2 * diagonalGap + offsetY));
        putNodePosition(27, new Point2D(startX + 2 * gap + diagonalGap + offsetX, startY + 2 * gap + diagonalGap + offsetY));
        putNodePosition(28, new Point2D(startX + 2 * gap + 2 * diagonalGap + offsetX, startY + 2 * gap + 2 * diagonalGap + offsetY));
    }

    private void drawBoard() {
        for (Node node : board.getNodes()) {
            Point2D p = nodePositions.get(node.getNodeID());
            if (p == null) continue;

            boolean isCorner = node instanceof CornerNode;
            Circle outer = new Circle(p.getX(), p.getY(), 15);
            outer.setFill(Color.LIGHTGRAY);
            outer.setStroke(Color.BLACK);
            getChildren().add(outer);

            if (isCorner) {
                Circle inner = new Circle(p.getX(), p.getY(), 10);
                inner.setFill(Color.TRANSPARENT);
                inner.setStroke(Color.BLACK);
                getChildren().add(inner);
            }
        }
    }

    private void drawEdges() {
        int[][] edges = {
                {10,9},{9,8},{8,7},{7,6},{6,5},{5,4},{4,3},{3,2},{2,1},{1,0},
                {0,19},{19,18},{18,17},{17,16},{16,15},{15,14},{14,13},{13,12},{12,11},{11,10},
                {5,22},{10,24},{24,23},{23,20},{20,25},{25,26},{20,27},{27,28},
                {21,22},{21,20},{26,15},{28,0}
        };

        for (int[] edge : edges) drawEdge(edge[0], edge[1]);
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