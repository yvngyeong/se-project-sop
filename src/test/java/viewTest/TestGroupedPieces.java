package viewTest;

import com.example.demo.Piece;
import com.example.demo.Player;
import com.example.demo.TetragonalBoard;
import view.BoardView;
import view.GroupedPieceComponent;
import view.PieceComponent;
import view.TetragonalBoardView;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class TestGroupedPieces {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 보드 및 보드 뷰 생성
            TetragonalBoard board = new TetragonalBoard();
            board.createNodes();
            TetragonalBoardView boardView = new TetragonalBoardView(board);
            boardView.setLayout(null);

            // 3. 프레임 구성
            JFrame frame = new JFrame("그루핑 테스트");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.add(boardView);

            // 4. 플레이어 및 말 생성 (같은 위치)
            Player player = new Player(1);
            Piece p1 = new Piece(1);
            p1.setPosition(10);
            Piece p2 = new Piece(1);
            p2.setPosition(10);
            player.getPieces().add(p1);
            player.getPieces().add(p2);

            // 5. 말 컴포넌트 생성 및 명시적 위치 설정
            Map<Piece, PieceComponent> pieceComponentMap = new HashMap<>();
            PieceComponent comp1 = new PieceComponent(p1, null);
            comp1.setBounds(150, 150, 40, 40);
            PieceComponent comp2 = new PieceComponent(p2, null);
            comp2.setBounds(150, 150, 40, 40);
            pieceComponentMap.put(p1, comp1);
            pieceComponentMap.put(p2, comp2);

            // 6. refreshPieces 내부에서 drawBoard() 호출 보장
            boardView.refreshPieces(pieceComponentMap, List.of(player));

            // 7. 프레임 표시
            frame.setVisible(true);
        });
    }
}
