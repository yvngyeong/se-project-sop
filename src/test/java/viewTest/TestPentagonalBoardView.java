package viewTest;

import com.example.demo.PentagonalBoard;
import view.PentagonalBoardView;

import javax.swing.*;

public class TestPentagonalBoardView {

    public static void main(String[] args) {

        PentagonalBoard board = new PentagonalBoard();
        board.createNodes();
        board.createEdges();

        PentagonalBoardView boardView = new PentagonalBoardView(board);

        JFrame frame = new JFrame("Pentagonal Yutnori Board");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(boardView);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
