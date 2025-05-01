package view;

import com.example.demo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ServiceView extends JFrame {
    private int playerCount;
    private int pieceCount;
    private String selectedYut;
    private String selectedBoard;

    private final JButton startButton;

    public ServiceView() {
        setTitle("윷놀이 게임 설정");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 플레이어 수
        panel.add(createSelectionPanel("플레이어 수", new String[]{"2", "3", "4"}, e -> {
            playerCount = Integer.parseInt(e.getActionCommand());
        }));

        // 말 수
        panel.add(createSelectionPanel("각 플레이어 말 수", new String[]{"2", "3", "4", "5"}, e -> {
            pieceCount = Integer.parseInt(e.getActionCommand());
        }));

        // 윷 종류
        panel.add(createSelectionPanel("윷 종류", new String[]{"지정 윷", "랜덤 윷"}, e -> {
            selectedYut = e.getActionCommand();
        }));

        // 보드판 종류
        panel.add(createSelectionPanel("보드판 종류", new String[]{"4각형", "5각형", "6각형"}, e -> {
            selectedBoard = e.getActionCommand();
        }));

        // 시작 버튼
        startButton = new JButton("게임 시작");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        panel.add(buttonPanel);

        add(panel);
        setVisible(true);
    }

    // 선택 패널 생성 메서드
    private JPanel createSelectionPanel(String title, String[] options, ActionListener valueSetter) {
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel(title + ": "));

        ButtonGroup group = new ButtonGroup();
        List<JToggleButton> buttons = new ArrayList<>();

        // 선택된 버튼의 색상 설정 리스너
        ActionListener styledListener = e -> {
            for (JToggleButton btn : buttons) {
                if (btn.isSelected()) {
                    btn.setBackground(new Color(118, 140, 228, 165)); // 선택된 버튼 색 (진한 파란색)
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(null); // 기본 배경색
                    btn.setForeground(Color.BLACK);
                }
            }
            valueSetter.actionPerformed(e); // 실제 값 설정 콜백 호출
        };

        // 각 옵션에 대해 토글 버튼 생성
        for (String option : options) {
            JToggleButton toggleButton = new JToggleButton(option);
            toggleButton.setActionCommand(option);
            toggleButton.addActionListener(styledListener);

            toggleButton.setFocusPainted(false);
            toggleButton.setContentAreaFilled(true);
            toggleButton.setOpaque(true);

            group.add(toggleButton);
            buttons.add(toggleButton);
            selectionPanel.add(toggleButton);
        }

        return selectionPanel;
    }

    // 설정값 반환 메서드들
    public int getPlayerCount() {
        return playerCount;
    }

    public int getPieceCount() {
        return pieceCount;
    }

    public String getYutType() {
        return selectedYut;
    }

    public String getBoardType() {
        return selectedBoard;
    }

    // 윷 객체 반환
    public Yut getYutObject() {
        return switch (selectedYut) {
            case "지정 윷" -> new TestYut();
            case "랜덤 윷" -> new RandomYut();
            default -> null;
        };
    }

    // 보드 객체 반환
    public Board getBoardObject() {
        return switch (selectedBoard) {
            case "4각형" -> new TetragonalBoard();
            case "5각형" -> new PentagonalBoard();
            case "6각형" -> new HexagonalBoard();
            default -> null;
        };
    }

    // 시작 버튼 리스너 추가
    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }



}
