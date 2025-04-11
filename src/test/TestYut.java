package test;

import java.util.Scanner;

import main.Yut;

public class TestYut extends Yut {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public Integer getResult() {
        int input;

        while (true) {
            System.out.print("윷 값 입력 (빽도:-1, 도:1, 개:2, 걸:3, 윷:4, 모:5): ");
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
