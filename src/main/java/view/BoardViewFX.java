package view;

import com.example.demo.Piece;
import com.example.demo.Player;
import com.example.demo.Node;
import listener.PieceClickListener;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public abstract class BoardViewFX extends Pane {
    protected Map<Node, Pane> nodeToPanelMap = new HashMap<>();
    protected Map<Integer, Point2D> nodePositions = new HashMap<>();

    public void putNodePosition(int nodeId, Point2D pos) {
        this.nodePositions.put(nodeId, pos);
    }

    public void clearPieces() {
        this.getChildren().removeIf(c -> c instanceof PieceComponentFX || c instanceof GroupedPieceComponentFX);
    }

    public void addPieceComponentAt(PieceComponentFX pieceComponent, Node node) {
        Pane panel = nodeToPanelMap.get(node);
        if (panel != null) {
            panel.getChildren().add(pieceComponent);
        }
    }

    public void refreshPieces(Map<Piece, PieceComponentFX> pieceComponentMap, List<Player> players) {
        this.getChildren().removeIf(c -> c instanceof PieceComponentFX || c instanceof GroupedPieceComponentFX);

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

            if (piecesAtSamePos.size() == 1) {
                Piece piece = piecesAtSamePos.get(0);
                PieceComponentFX comp = pieceComponentMap.get(piece);
                if (comp == null) continue; // null 체크 추가

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
                PieceComponentFX refComp = pieceComponentMap.get(referencePiece);
                if (refComp == null) continue;

                GroupedPieceComponentFX groupComp = new GroupedPieceComponentFX(normalizedPieces);
                groupComp.setClickListener(refComp.getListener()); // 클릭 리스너 설정
                groupComp.setLayoutX(nodePos.getX() - 20);
                groupComp.setLayoutY(nodePos.getY() - 20);
                this.getChildren().add(groupComp);
            }
        }
    }

}
