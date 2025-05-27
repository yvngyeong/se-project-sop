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

public abstract class BoardViewFX extends Pane {
    protected final Map<Node, Pane> nodeToPanelMap = new HashMap<>();
    protected final Map<Integer, Point2D> nodePositions = new HashMap<>();

    public void putNodePosition(int nodeId, Point2D pos) {
        this.nodePositions.put(nodeId, pos);
    }

    public void clearPieces() {
        getChildren().removeIf(c -> c instanceof PieceComponentFX || c instanceof GroupedPieceComponentFX);
    }

    public void addPieceComponentAt(PieceComponentFX pieceComponent, Node node) {
        Point2D pos = nodePositions.get(node.getNodeID());
        if (pos != null) {
            pieceComponent.setLayoutX(pos.getX() - 20);
            pieceComponent.setLayoutY(pos.getY() - 20);
            getChildren().add(pieceComponent);
        }
    }

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
            List<Piece> piecesAtSamePos = entry.getValue();

            Point2D nodePos = nodePositions.get(nodeId);
            if (nodePos == null) continue;

            if (nodeId == 0) {
                piecesAtSamePos = piecesAtSamePos.stream()
                        .filter(Piece::isJustArrived)
                        .collect(Collectors.toList());
                if (piecesAtSamePos.isEmpty()) continue;
            }

            if (piecesAtSamePos.size() == 1) {
                Piece piece = piecesAtSamePos.get(0);
                PieceComponentFX comp = pieceComponentMap.get(piece);
                if (comp != null) {
                    comp.setLayoutX(nodePos.getX() - 20);
                    comp.setLayoutY(nodePos.getY() - 20);
                    getChildren().add(comp);
                }
            } else {
                List<Piece> normalized = new ArrayList<>();
                for (Piece p : piecesAtSamePos) {
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
                if (refComp == null) continue;
                PieceClickListener listener = refComp.getListener();

                GroupedPieceComponentFX groupComp = new GroupedPieceComponentFX(normalized);
                groupComp.setClickListener(listener);
                groupComp.setLayoutX(nodePos.getX() - 20);
                groupComp.setLayoutY(nodePos.getY() - 20);
                getChildren().add(groupComp);
            }
        }
    }
}