package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomYutTest {

    @Test
    void getResult_returnsOnlyValidValues() {
        RandomYut randomYut = new RandomYut();
        Set<Integer> validValues = Set.of(-1, 1, 2, 3, 4, 5);

        for (int i = 0; i < 1000; i++) {
            int result = randomYut.getResult();
            assertTrue(validValues.contains(result), "유효하지 않은 결과: " + result);
        }
    }

    @Test
    void getResult_eventuallyReturnsAllValidValues() {
        RandomYut randomYut = new RandomYut();
        Set<Integer> expected = Set.of(-1, 1, 2, 3, 4, 5);
        Set<Integer> actual = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            int result = randomYut.getResult();
            actual.add(result);
            if (actual.containsAll(expected)) {
                break; // 전부 모이면 더 돌릴 필요 없음
            }
        }

        for (int value : expected) {
            assertTrue(actual.contains(value), value + " 값이 한 번도 나오지 않았습니다.");
        }
    }

    @Test
    void getResult_checkDistributionOfValidValues() {
        RandomYut randomYut = new RandomYut();
        Map<Integer, Integer> countMap = new HashMap<>();
        int iterations = 10000;

        // -1, 1, 2, 3, 4, 5를 미리 카운트 맵에 초기화
        for (int val : new int[]{-1, 1, 2, 3, 4, 5}) {
            countMap.put(val, 0);
        }

        // 10000번 실행
        for (int i = 0; i < iterations; i++) {
            int result = randomYut.getResult();
            countMap.put(result, countMap.getOrDefault(result, 0) + 1);
        }

        // 퍼센트 출력 + 허용 오차 내에 있는지 assert 확인
        for (int val : countMap.keySet()) {
            int count = countMap.get(val);
            double percentage = (count / (double) iterations) * 100;

            System.out.printf("%2d: %5d회 (%.2f%%)\n", val, count, percentage);

            switch (val) {
                case -1 -> assertTrue(percentage >= 3 && percentage <= 7, "-1(빽도) 비율이 예상 범위를 벗어났습니다.");
                case 1, 2, 3 -> assertTrue(percentage >= 20 && percentage <= 30, val + "(도,개,걸) 비율이 예상 범위를 벗어났습니다.");
                case 4, 5 -> assertTrue(percentage >= 7 && percentage <= 13, val + "(윷,모) 비율이 예상 범위를 벗어났습니다.");
            }
        }
    }
}
