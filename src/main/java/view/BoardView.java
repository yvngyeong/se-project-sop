package view;

import com.example.demo.Board;
import com.example.demo.HexagonalBoard;
import com.example.demo.PentagonalBoard;
import com.example.demo.TetragonalBoard;

import javax.swing.*;
import java.awt.*;

public class BoardView extends JPanel {
    public BoardView(Board board) {
        setLayout(new BorderLayout());

        JPanel innerView = new JPanel();

        if (board instanceof TetragonalBoard) {
            innerView = new TetragonalBoardView((TetragonalBoard) board);
        } else if (board instanceof PentagonalBoard) {
            innerView = new PentagonalBoardView((PentagonalBoard) board);
        } else if (board instanceof HexagonalBoard) {
            innerView = new HexagonalBoardView((HexagonalBoard) board);
        }

        add(innerView, BorderLayout.CENTER);
    }
}
