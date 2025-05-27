package view;

import com.example.demo.*;
import listener.PieceClickListener;
import listener.SelectThrowListener;
import listener.ThrowListener;

import javax.swing.*;
import java.awt.*;
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
    private SelectThrowListener selectThrowListener;
    private JLabel statusLabel;

    private JPanel yutButtonPanel; // 윷 버튼 모음 패널

    private Runnable restartCallback;

    public void setRestartCallback(Runnable restartCallback) {
        this.restartCallback = restartCallback;
    }

    private Component yutStrut;




    public GameView(Game game) {
        setTitle("윷놀이 게임");
        setSize(800, 600);
        setResizable(false);
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

        // 상태 표시 라벨 (윷 던져주세요, 윷이나 모가 나오면 -> 윷을 한번 더 던져주세요, 말 이동 중)
        statusLabel = new JLabel("윷을 던져주세요.");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(statusLabel);
        rightPanel.add(Box.createVerticalStrut(10));

    }

    public void setPieceClickListener(PieceClickListener listener) {
        this.pieceClickListener = listener;
        for (PieceComponent comp : pieceComponentMap.values()) {
            comp.setClickListener(listener);
        }

        for (Component c : boardPanel.getComponents()) {
            if (c instanceof GroupedPieceComponent gpc) {
                gpc.setClickListener(listener);
            }
        }


    }

    public void setThrowListener(ThrowListener listener) {
        this.throwListener = listener;
    }
    public void setSelectThrowListener(SelectThrowListener listener){
        this.selectThrowListener=listener;
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
        StringBuilder sb = new StringBuilder();
        sb.append("윷 결과: ");

        for (int result : yutResults) {
            sb.append(getYutName(result)).append(" ");
        }

        yutQueueLabel.setText(sb.toString().trim());
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
            if (restartCallback != null)
            {
                restartCallback.run(); // 여기서 GameController.restart() 실행됨
            }
        } else {
            // 아니오: 프로그램 종료
            System.out.println("게임 종료");
            System.exit(0);
        }
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    // 필요할 때 호출
    public void createYutButtons() {

        if (yutStrut != null) {
            rightPanel.remove(yutStrut);
        }
        // (2) 이전 버튼 패널도 지우기
        if (yutButtonPanel != null) {
            rightPanel.remove(yutButtonPanel);
        }
        yutStrut = Box.createVerticalStrut(10);

        yutButtonPanel = new JPanel();
        yutButtonPanel.setLayout(new GridLayout(2, 3, 5, 5));
        yutButtonPanel.setMaximumSize(new Dimension(250, 100));

        String[] labels = {"빽도", "도", "개", "걸", "윷", "모"};
        int[] values = {-1, 1, 2, 3, 4, 5};

        for (int i = 0; i < labels.length; i++) {
            JButton btn = new JButton(labels[i]);
            int value = values[i];
            btn.addActionListener(e -> {
                if (selectThrowListener != null) {
                    selectThrowListener.onThrowSelected(value);
                }
            });
            yutButtonPanel.add(btn);
        }
        rightPanel.add(yutStrut);
        rightPanel.add(yutButtonPanel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void createRandomYutButtons(){

        if (yutStrut != null) rightPanel.remove(yutStrut);
        if (yutButtonPanel != null) rightPanel.remove(yutButtonPanel);

        yutStrut = Box.createVerticalGlue();

        // 윷 던지기 버튼
        throwButton = new JButton("윷 던지기");
        throwButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(250, 50));
        throwButton.setPreferredSize(new Dimension(230, 50));
        buttonPanel.add(throwButton);

        throwButton.addActionListener(e -> {
            if (throwListener != null) {
                throwListener.onThrow();
            }
        });

        rightPanel.add(yutStrut);
        rightPanel.add(buttonPanel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }
    public String getYutName(int result) {
        return switch (result) {
            case -1 -> "빽도";
            case 1 -> "도";
            case 2 -> "개";
            case 3 -> "걸";
            case 4 -> "윷";
            case 5 -> "모";
            default -> "알수없음";
        };
    }
    public void showYutResultButtons(List<Integer> yutQueue, SelectThrowListener listener) {

        if (yutStrut != null) {
            rightPanel.remove(yutStrut);
        }

        if (yutButtonPanel != null) {
            rightPanel.remove(yutButtonPanel);
        }

        yutStrut = Box.createVerticalStrut(10);

        yutButtonPanel = new JPanel(new FlowLayout());
        yutButtonPanel.setMaximumSize(new Dimension(250, 50));

        for (Integer yut : yutQueue) {
            JButton btn = new JButton(getYutName(yut));
            btn.addActionListener(e -> {
                if (listener != null) {
                    listener.onThrowSelected(yut);
                }
            });
            yutButtonPanel.add(btn);
        }
        rightPanel.add(yutStrut);
        rightPanel.add(yutButtonPanel);
        rightPanel.revalidate();
        rightPanel.repaint();
    }
    public void showThrowButtonAgain(boolean isTestYut) {

        if (yutStrut != null) {
            rightPanel.remove(yutStrut);
        }

        if (yutButtonPanel != null) {
            rightPanel.remove(yutButtonPanel);
            yutButtonPanel = null;  ///?????
        }

        if (isTestYut) {
            createYutButtons(); // 지정 윷
        } else {
            createRandomYutButtons(); // 랜덤 윷
        }

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void hideYutResultButtons() {
        if (yutButtonPanel != null) {
            rightPanel.remove(yutButtonPanel);
            yutButtonPanel = null;
            rightPanel.revalidate();
            rightPanel.repaint();
        }
    }
    public void initPieceComponents(List<Player> players, PieceClickListener pieceClickListener) {
        for (Player player : players) {
            for (Piece piece : player.getPieces()) {
                if (!pieceComponentMap.containsKey(piece)) { // 기존에 없을 때만 새로 생성
                    PieceComponent pieceComp = new PieceComponent(piece, pieceClickListener);
                    pieceComponentMap.put(piece, pieceComp);
                } else {
                    // 이미 존재하는 경우, 리스너만 다시 설정
                    pieceComponentMap.get(piece).setClickListener(pieceClickListener);
                }
            }
        }
    }}
