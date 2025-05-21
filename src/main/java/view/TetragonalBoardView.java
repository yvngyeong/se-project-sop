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


public class TetragonalBoardView extends BoardView {
    private TetragonalBoard board;
    private Map<Integer, Point2D> nodePositions;

    public TetragonalBoardView(TetragonalBoard board) {
        this.board = board;
        this.nodePositions = new HashMap<>();

        setPrefSize(500, 500);
        setStyle("-fx-background-color: white;");

        setNodePositions();
        drawBoard();
    }

    private void setNodePositions() {
        int startX = 100;
        int startY = 100;
        int gap = 60;
        int diagonalGap = (int)(gap * 0.75f);
        int offsetX = 30; // 오른쪽으로 이동
        int offsetY = 30; // 아래쪽으로 이동

        int[][] nodeCoords = {
                {10, 0, 0}, {9, 1, 0}, {8, 2, 0}, {7, 3, 0}, {6, 4, 0}, {5, 5, 0},
                {4, 5, 1}, {3, 5, 2}, {2, 5, 3}, {1, 5, 4},
                {0, 5, 5}, {19, 4, 5}, {18, 3, 5}, {17, 2, 5}, {16, 1, 5}, {15, 0, 5},
                {14, 0, 4}, {13, 0, 3}, {12, 0, 2}, {11, 0, 1},
                {20, 2 * gap + offsetX, 2 * gap + offsetY},
                {21, 2 * gap + diagonalGap + offsetX, 2 * gap - diagonalGap + offsetY},
                {22, 2 * gap + 2 * diagonalGap + offsetX, 2 * gap - 2 * diagonalGap + offsetY},
                {23, 2 * gap - diagonalGap + offsetX, 2 * gap - diagonalGap + offsetY},
                {24, 2 * gap - 2 * diagonalGap + offsetX, 2 * gap - 2 * diagonalGap + offsetY},
                {25, 2 * gap - diagonalGap + offsetX, 2 * gap + diagonalGap + offsetY},
                {26, 2 * gap - 2 * diagonalGap + offsetX, 2 * gap + 2 * diagonalGap + offsetY},
                {27, 2 * gap + diagonalGap + offsetX, 2 * gap + diagonalGap + offsetY},
                {28, 2 * gap + 2 * diagonalGap + offsetX, 2 * gap + 2 * diagonalGap + offsetY},
        };

        for (int[] data : nodeCoords) {
            int id = data[0];
            Point2D pos = new Point2D(data[1] * gap + startX, data[2] * gap + startY);
            nodePositions.put(id, pos);
    }

    }


    private void drawBoard() {
        int[][] edges = {
                {10, 9}, {9, 8}, {8, 7}, {7, 6}, {6, 5}, {5, 4}, {4, 3}, {3, 2}, {2, 1}, {1, 0},
                {0, 19}, {19, 18}, {18, 17}, {17, 16}, {16, 15}, {15, 14}, {14, 13}, {13, 12}, {12, 11}, {11, 10},
                {5, 22}, {10, 24}, {24, 23}, {23, 20}, {20, 25}, {25, 26}, {20, 27}, {27, 28},
                {21, 22}, {21, 20}, {26, 15}, {28, 0}
        };

        for (int[] edge : edges) {
            Point2D from = nodePositions.get(edge[0]);
            Point2D to = nodePositions.get(edge[1]);
            if (from != null && to != null) {
                Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(2);
                getChildren().add(line);
            }
        }

        for (Node node : board.getNodes()) {
            Point2D p = nodePositions.get(node.getNodeID());
            if (p == null) continue;

            Circle circle = new Circle(p.getX(), p.getY(), 15);
            circle.setFill(Color.LIGHTGRAY);
            circle.setStroke(Color.BLACK);

            getChildren().add(circle);
        }
    }



    @Override
    public void refreshPieces(Map<Piece, PieceComponent> pieceComponentMap, List<Player> players) {
        // 말만 제거
        this.getChildren().removeIf(c -> c instanceof PieceComponent || c instanceof GroupedPieceComponent);


        Map<Integer, List<Piece>> positionMap = new HashMap<>();
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                System.out.println("확인용 로그 → pos: " + piece.getPosition() + ", finished: " + piece.isFinished() + ", justArrived: " + piece.isJustArrived());
                if (!piece.isFinished()) {
                    int pos = piece.getPosition();
                    positionMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(piece);
                }
            }
        }

        // 위치별 말 표시
        for (Map.Entry<Integer, List<Piece>> entry : positionMap.entrySet()) {
            int nodeId = entry.getKey();
            List<Piece> piecesAtSamePos = entry.getValue();

            // 0번 노드는 justArrived == true인 말만 표시
            if (nodeId == 0) {
                System.out.println("[디버깅] 0번에 있는 말들 (필터 전): " + piecesAtSamePos.size());

                piecesAtSamePos = piecesAtSamePos.stream()
                        .filter(Piece::isJustArrived)
                        .collect(Collectors.toList());

                System.out.println("[디버깅] 0번에 justArrived == true인 말들: " + piecesAtSamePos.size());

                if (piecesAtSamePos.isEmpty()) {
                    System.out.println("[디버깅] → 그릴 말 없음 → 리턴됨");
                    continue;
                }
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
