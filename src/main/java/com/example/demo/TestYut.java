package com.example.demo;

import java.util.Queue;
import java.util.LinkedList;

public class TestYut extends Yut {
    private final Queue<Integer> inputs;

    public TestYut(int... values) {
        this.inputs = new LinkedList<>();
        for (int v : values) {
            this.inputs.offer(v);
        }
    }

    @Override
    public Integer getResult() {
        while (!inputs.isEmpty()) {
            System.out.print("윷 값 입력 (빽도:-1, 도:1, 개:2, 걸:3, 윷:4, 모:5): ");
            int input = inputs.poll();
            if (input == -1 || (1 <= input && input <= 5)) {
                return input;
            } else {
                System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
            }
        }
        throw new IllegalStateException("올바른 입력값이 없습니다.");
    }
}
