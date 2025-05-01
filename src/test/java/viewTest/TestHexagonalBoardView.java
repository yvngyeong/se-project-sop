package viewTest;

import com.example.demo.HexagonalBoard;
import view.HexagonalBoardView;

import javax.swing.*;

public class TestHexagonalBoardView {

    public static void main(String[] args) {

        HexagonalBoard board = new HexagonalBoard();
        board.createNodes();
        board.createEdges();

        HexagonalBoardView boardView = new HexagonalBoardView(board);

        JFrame frame = new JFrame("Hexagonal Yutnori Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(boardView);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
