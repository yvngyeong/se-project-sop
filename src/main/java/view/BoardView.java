package view;

import com.example.demo.Piece;
import com.example.demo.Player;
import com.example.demo.Node;
import listener.PieceClickListener;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.awt.Rectangle;
import java.awt.Point;



public abstract class BoardView extends JPanel {
    protected Map<Node, JPanel> nodeToPanelMap = new HashMap<>();
    protected Map<Integer, Point> nodePositions = new HashMap<>();

    public void putNodePosition(int nodeId, Point pos) {
        this.nodePositions.put(nodeId, pos);
    }



    public void clearPieces() {
        for (JPanel nodePanel : nodeToPanelMap.values()) {
            nodePanel.removeAll();
            nodePanel.revalidate();
            nodePanel.repaint();
        }
    }


    public void addPieceComponentAt(PieceComponent pieceComponent, Node node) {
        JPanel panel = nodeToPanelMap.get(node);
        if (panel != null) {
            panel.add(pieceComponent);
            panel.revalidate();
            panel.repaint();
        }
    }

    public void refreshPieces(Map<Piece, PieceComponent> pieceComponentMap, List<Player> players) {
        this.removeAll();

        // 1. 위치 index → 해당 위치에 있는 Piece 리스트
        Map<Integer, List<Piece>> positionMap = new HashMap<>();
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (!piece.isFinished()) {
                    int pos = piece.getPosition();
                    positionMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(piece);
                }
            }
        }

        // 2. 위치별로 처리
        for (Map.Entry<Integer, List<Piece>> entry : positionMap.entrySet()) {
            List<Piece> piecesAtSamePos = entry.getValue();

            JComponent comp;
            if (piecesAtSamePos.size() == 1) {
                Piece piece = piecesAtSamePos.get(0);
                comp = pieceComponentMap.get(piece);
            } else {
                // 그룹 컴포넌트로 교체
                comp = new GroupedPieceComponent(piecesAtSamePos);

                // 좌표는 첫 번째 말 기준
                Piece referencePiece = piecesAtSamePos.get(0);
                PieceComponent refComp = pieceComponentMap.get(referencePiece);
                Rectangle bounds = refComp.getBounds(); // 기존 좌표 얻기
                comp.setBounds(bounds);
            }

            // 단독 말은 기존 좌표 유지, 그룹은 첫 말 좌표 복사
            if (comp.getParent() != this) {
                this.add(comp);
            }
        }

        this.revalidate();
        this.repaint();
    }

}
