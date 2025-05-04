package view;

import com.example.demo.Piece;
import com.example.demo.Player;
import com.example.demo.Node;
import listener.PieceClickListener;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BoardView extends JPanel {
    protected Map<Node, JPanel> nodeToPanelMap = new HashMap<>();

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

    public abstract void refreshPieces(Map<Piece, PieceComponent> pieceComponentMap, List<Player> players);
}
