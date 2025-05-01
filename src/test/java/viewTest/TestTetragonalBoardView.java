package viewTest;

import com.example.demo.TetragonalBoard;
import view.TetragonalBoardView;

import javax.swing.*;

public class TestTetragonalBoardView {

    public static void main(String[] args) {
        TetragonalBoard board = new TetragonalBoard();
        board.createNodes();
        board.createEdges();

        JFrame frame = new JFrame("Tetragonal Board View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TetragonalBoardView(board));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
