package viewTest;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;
import view.YutView;

public class YutViewTest {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("윷놀이 게임");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 300);
        frame.setLocationRelativeTo(null);

        YutView yutView = new YutView();
        frame.add(yutView);
        frame.setVisible(true);

        yutView.setYutResult(-1); // 여기서 값 바꾸면 테스트 가능
    }
}
