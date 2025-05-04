package view;

import com.example.demo.*;
import listener.PieceClickListener;
import listener.ThrowListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameView extends JFrame {
    private BoardView boardPanel;
    private JPanel rightPanel;
    private JLabel currentPlayerLabel;
    private JLabel yutQueueLabel;
    private JButton throwButton;
    private Map<Player, JPanel> piecePanels = new HashMap<>(); // 남은 말 표시용
    private Map<Piece, PieceComponent> pieceComponentMap = new HashMap<>(); // ⬅️ 말 컴포넌트 저장
    private PieceClickListener pieceClickListener;
    private ThrowListener throwListener;

    public GameView(Game game) {
        setTitle("윷놀이 게임");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 보드 패널
        if (game.getBoard() instanceof TetragonalBoard) {
            boardPanel = new TetragonalBoardView((TetragonalBoard) game.getBoard());
        } else if (game.getBoard() instanceof PentagonalBoard) {
            boardPanel = new PentagonalBoardView((PentagonalBoard) game.getBoard());
        } else {
            boardPanel = new HexagonalBoardView((HexagonalBoard) game.getBoard());
        }
        add(boardPanel, BorderLayout.CENTER);

        // 우측 패널
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(250, 0));
        add(rightPanel, BorderLayout.EAST);
        rightPanel.add(Box.createVerticalStrut(20));

        // 각 플레이어의 말 표시
        for (Player player : game.getPlayers()) {
            JLabel playerLabel = new JLabel("플레이어" + player.getId() + "의 남은 말:");
            System.out.println("디버깅: "+player.getId());
            playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            rightPanel.add(playerLabel);

            JPanel piecePanel = new JPanel();
            piecePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            piecePanel.setMaximumSize(new Dimension(250, 50));

            for (Piece piece : player.getUnusedPieces()) {
                PieceComponent pieceComp = new PieceComponent(piece, pieceClickListener); // 플레이어 별로 말 만듦
                pieceComponentMap.put(piece, pieceComp); // ⬅️ 저장
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

        rightPanel.add(Box.createVerticalGlue()); // 아래로 밀기
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

        initListeners(); // throwButton에 이벤트 처리 되도록
    }

    public void setPieceClickListener(PieceClickListener listener) {
        this.pieceClickListener = listener;
    }

    public void setThrowListener(ThrowListener listener) {
        this.throwListener = listener;
    }

    private void initListeners() {
        throwButton.addActionListener(e -> {
            if (throwListener != null) {
                throwListener.onThrow();
            }
        });
    }

    public void updateUnusedPieces(List<Player> players) {
        for (Player player : players) {
            JPanel panel = piecePanels.get(player);
            if (panel != null) {
                panel.removeAll();
                for (Piece piece : player.getUnusedPieces()) {
                    PieceComponent pieceComp = pieceComponentMap.get(piece); // 기존 컴포넌트 재사용
                    panel.add(pieceComp);
                }
                panel.revalidate();
                panel.repaint();
            }
        }
    }

    public void updateYutQueue(List<Integer> yutResults) {
        yutQueueLabel.setText("윷 결과: " + yutResults.toString());
    }

    public void updateCurrentPlayer(int playerId) {
        currentPlayerLabel.setText("현재 플레이어: " + playerId + "번");
    }

    // GameView 클래스 내
    public void updateBoardPieces(List<Player> players) {
        boardPanel.refreshPieces(pieceComponentMap, players); // 재사용되는 컴포넌트 활용
    }

    public void showYutResult(int result) {
        YutView yutView = new YutView();
        yutView.setYutResult(result);
        yutView.setSize(300, 250); // 원하는 크기
        yutView.setOpaque(false); // 배경 투명

        // 위치는 가운데쯤 (조절 가능)
        int x = (getWidth() - yutView.getWidth()) / 2;
        int y = (getHeight() - yutView.getHeight()) / 2;
        yutView.setLocation(x, y);

        JLayeredPane layeredPane = getLayeredPane();
        layeredPane.add(yutView, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();

        // 1.5초 후 제거
        Timer timer = new Timer(1000, e -> {
            layeredPane.remove(yutView);
            layeredPane.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void refreshAfterCapture(List<Player> players) {
        updateBoardPieces(players);      // 현재 말 배치 다시 그림
        updateUnusedPieces(players);     // 잡힌 말이 오른쪽에 다시 보이게
    }

    public void showGameOverDialog(int winnerId) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "🎉 플레이어 " + winnerId + "번이 승리했습니다!\n게임을 다시 시작하시겠습니까?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            // 예: 게임 재시작 (여기선 프로그램 재시작 처리로 대체)
            System.out.println("게임 재시작");
            System.exit(0); // TODO: 실제로는 게임 초기화 로직으로 교체해야 함
        } else {
            // 아니오: 프로그램 종료
            System.out.println("게임 종료");
            System.exit(0);
        }
    }



}