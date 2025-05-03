package view;

import com.example.demo.Game;
import com.example.demo.Piece;
import com.example.demo.Player;
import com.example.demo.TetragonalBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameView extends JFrame {
    private JPanel boardPanel;
    private JPanel rightPanel;
    private JLabel currentPlayerLabel;
    private JLabel yutQueueLabel;
    private JButton throwButton;
    private Map<Player, JPanel> piecePanels = new HashMap<>(); // 남은 말 표시용

    public GameView(Game game) {
        setTitle("윷놀이 게임");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 보드 패널
        boardPanel = new BoardView(game.getBoard());
        add(boardPanel, BorderLayout.CENTER);

        // 우측 패널
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(250, 0));
        add(rightPanel, BorderLayout.EAST);
        rightPanel.add(Box.createVerticalStrut(30));

        // 각 플레이어의 말 표시
        for (Player player : game.getPlayers()) {
            JLabel playerLabel = new JLabel("플레이어" + player.getId() + "의 남은 말:");
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            rightPanel.add(playerLabel);

            JPanel piecePanel = new JPanel();
            piecePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            piecePanel.setMaximumSize(new Dimension(250, 50));

            for (Piece piece : player.getUnusedPieces()) {
                PieceComponent pieceComp = new PieceComponent(piece); // 플레이어 별로 말 만듦
                piecePanel.add(pieceComp);
            }

            piecePanels.put(player, piecePanel);

            rightPanel.add(Box.createVerticalStrut(10));
            rightPanel.add(piecePanel);
            rightPanel.add(Box.createVerticalStrut(10));
        }

        // 현재 플레이어 / 윷결과
        currentPlayerLabel = new JLabel("현재 플레이어: ");
        yutQueueLabel = new JLabel("윷 결과: ");

        currentPlayerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        yutQueueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(Box.createVerticalStrut(200));
        rightPanel.add(currentPlayerLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(yutQueueLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // 윷 던지기 버튼
        throwButton = new JButton("윷 던지기");
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(250, 50));
        throwButton.setPreferredSize(new Dimension(230, 50));
        buttonPanel.add(throwButton);

        rightPanel.add(Box.createVerticalGlue()); // 아래로 밀기
        rightPanel.add(buttonPanel);

        setVisible(true);
    }

    public void addThrowListener(ActionListener listener) {
        throwButton.addActionListener(listener);
    }

    public void addPieceSelectListener(PieceSelectListener listener) {
        // 우측 패널 말 클릭 이벤트 연결
        for (Component c : rightPanel.getComponents()) {
            c.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    int index = rightPanel.getComponentZOrder(c);
                    listener.onPieceSelected(index);
                }
            });
        }
    }

//    public void updateBoard(List<Player> players) {
//        boardPanel.removeAll();
//        rightPanel.removeAll();
//
//        for (Player player : players) {
//            for (Piece piece : player.getPieces()) {
//                JLabel label = new JLabel("● P" + player.getId());
//                if (piece.isOnBoard()) {
//                    boardPanel.add(label);
//                } else {
//                    rightPanel.add(label);
//                }
//            }
//        }
//
//        boardPanel.revalidate();
//        boardPanel.repaint();
//        rightPanel.revalidate();
//        rightPanel.repaint();
//    }

    public void updateYutQueue(List<Integer> yutResults) {
        yutQueueLabel.setText("윷 결과: " + yutResults.toString());
    }

    public void updateCurrentPlayer(int playerId) {
        currentPlayerLabel.setText("현재 플레이어: " + playerId + "번");
    }

    public void showYutResult(int result) {
        JDialog dialog = new JDialog(this, "윷 결과", true);
        dialog.setSize(200, 100);
        dialog.add(new JLabel("결과: " + result, SwingConstants.CENTER));
        dialog.setLocationRelativeTo(this);
        new Timer(1500, e -> dialog.dispose()).start();
        dialog.setVisible(true);
    }

    public void showWinner(int playerId) {
        JOptionPane.showMessageDialog(this, "플레이어 " + playerId + " 승리!");
    }

    public interface PieceSelectListener {
        void onPieceSelected(int index);
    }
}