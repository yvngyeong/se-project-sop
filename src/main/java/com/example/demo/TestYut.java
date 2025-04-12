package com.example.demo;

import java.util.Scanner;

public class TestYut extends Yut {
    private final Scanner scanner;

    public TestYut(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Integer getResult() {
        int input;

        while (true) {
            System.out.print("윷 값 입력 (빽도:-1, 도:1, 개:2, 걸:3, 윷:4, 모:5): ");
            if (!scanner.hasNextInt()) {
                scanner.next(); // 소비
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                continue;
            }

            input = scanner.nextInt();

            // 유효 범위 확인
            if (input == -1 || (input >= 1 && input <= 5)) {
                break; // 유효한 입력 -> 반복 종료
            }

            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
        }

        return input;
    }
}
