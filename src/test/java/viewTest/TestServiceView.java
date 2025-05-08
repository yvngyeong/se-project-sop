package viewTest;

import com.example.demo.*;
import org.junit.jupiter.api.*;
import view.ServiceView;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.SwingUtilities;
import javax.swing.JButton;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

class TestServiceView {

    private ServiceView view;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize the GUI on the Event Dispatch Thread
        SwingUtilities.invokeAndWait(() -> view = new ServiceView());
    }

    @AfterEach
    void tearDown() throws Exception {
        // Dispose of the GUI after each test
        SwingUtilities.invokeAndWait(() -> view.dispose());
    }

    @Test
    void testGetYutObjectMapping() throws Exception {
        // Use reflection to set the private field selectedYut
        Field yutField = ServiceView.class.getDeclaredField("selectedYut");
        yutField.setAccessible(true);

        yutField.set(view, "지정 윷");
        assertTrue(view.getYutObject() instanceof TestYut, "getYutObject should return TestYut for '지정 윷'");

        yutField.set(view, "랜덤 윷");
        assertTrue(view.getYutObject() instanceof RandomYut, "getYutObject should return RandomYut for '랜덤 윷'");

        yutField.set(view, "unknown");
        assertNull(view.getYutObject(), "getYutObject should return null for unknown yut type");
    }

    @Test
    void testGetBoardObjectMapping() throws Exception {
        Field boardField = ServiceView.class.getDeclaredField("selectedBoard");
        boardField.setAccessible(true);

        boardField.set(view, "4각형");
        assertTrue(view.getBoardObject() instanceof TetragonalBoard, "getBoardObject should return TetragonalBoard for '4각형'");

        boardField.set(view, "5각형");
        assertTrue(view.getBoardObject() instanceof PentagonalBoard, "getBoardObject should return PentagonalBoard for '5각형'");

        boardField.set(view, "6각형");
        assertTrue(view.getBoardObject() instanceof HexagonalBoard, "getBoardObject should return HexagonalBoard for '6각형'");

        boardField.set(view, "unknown");
        assertNull(view.getBoardObject(), "getBoardObject should return null for unknown board type");
    }

    @Test
    void testStartButtonListener() throws Exception {
        AtomicBoolean clicked = new AtomicBoolean(false);

        // Attach a listener to the start button
        SwingUtilities.invokeAndWait(() -> view.addStartButtonListener(e -> clicked.set(true)));

        // Access the private startButton field and simulate a click
        Field buttonField = ServiceView.class.getDeclaredField("startButton");
        buttonField.setAccessible(true);
        JButton startButton = (JButton) buttonField.get(view);

        SwingUtilities.invokeAndWait(startButton::doClick);

        assertTrue(clicked.get(), "Start button listener should be invoked on click");
    }
}
