import com.example.demo.*;
import controller.GameController;
import controller.ServiceController;
import view.GameView;
import view.ServiceView;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("FlatLaf 적용 실패: " + e.getMessage());
        }

        new ServiceController();

    }
}
