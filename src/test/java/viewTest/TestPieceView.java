package viewTest;

import com.example.demo.Piece;
import listener.PieceClickListener;
import view.PieceComponent;

import javax.swing.*;

public class TestPieceView {
    public static void main(String[] args) {

        Piece testPiece = new Piece(1);
        PieceClickListener testListener = null;

        PieceComponent pieceView = new PieceComponent(testPiece, testListener);

        pieceView.setBounds(50, 50, 30, 30);

        JFrame frame = new JFrame("PieceView 테스트");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200);
        frame.setLayout(null); // 위치를 수동으로 지정하려면 반드시 null 레이아웃
        frame.add(pieceView);
        frame.setVisible(true);
    }
}