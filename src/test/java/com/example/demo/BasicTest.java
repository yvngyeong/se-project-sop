package com.example.demo;

import java.io.*;
        import java.util.Scanner;

import org.junit.jupiter.api.*;

        import static org.junit.jupiter.api.Assertions.*;

public class BasicTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream fakeOut;

    @BeforeEach
    void setup() {
        fakeOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(fakeOut));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testPromptForPlayerCountAppears() {
        String simulatedInput = String.join("\n",
                "1", // playerNum
                "1", // pieceNum
                "1", // yutType (TestYut)
                "4", // boardType
                "1"  // getResult 윷 값
        ) + "\n";

        ByteArrayInputStream fakeIn = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(fakeIn);

        com.example.demo.Service service = new com.example.demo.Service(testScanner);
        service.startGame();

        String output = fakeOut.toString();

        assertTrue(output.contains("플레이어 수"), "플레이어 수 프롬프트가 출력되어야 합니다.");
        assertTrue(output.contains("윷 값 입력"), "윷 값 입력 메시지가 출력되어야 합니다.");
        assertTrue(output.contains("게임을 시작합니다!"), "게임 시작 메시지가 출력되어야 합니다.");
    }
}
