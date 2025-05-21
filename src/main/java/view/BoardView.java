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


public abstract class BoardView extends Pane {
    protected Map<Node, Pane> nodeToPanelMap = new HashMap<>();
    protected Map<Integer, Point2D> nodePositions = new HashMap<>();

    public void putNodePosition(int nodeId, Point2D pos) {
        this.nodePositions.put(nodeId, pos);
    }



    public void clearPieces() {
        for (Pane nodePanel : nodeToPanelMap.values()) {

            nodePanel.getChildren().clear();

        }
    }


    public void addPieceComponentAt(PieceComponent pieceComponent, Node node) {
        Pane panel = nodeToPanelMap.get(node);
        if (panel != null) {

            panel.getChildren().add(pieceComponent);
        }
    }


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

            Point2D nodePos = nodePositions.get(nodeId);
            if (nodePos == null) continue;

            if (piecesAtSamePos.size() == 1) {
                Piece piece = piecesAtSamePos.get(0);
                PieceComponent comp = pieceComponentMap.get(piece);
                comp.setLayoutX(nodePos.getX() - 20);
                comp.setLayoutY(nodePos.getY() - 20);
                this.getChildren().add(comp);

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
                groupComp.setLayoutX(nodePos.getX() - 20);
                groupComp.setLayoutY(nodePos.getY() - 20);
                this.getChildren().add(groupComp);
            }
        }
    }
}

