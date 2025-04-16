package com.example.demo;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestYutTest {

    @Test
    void getResult_acceptsNegativeOneAsBackDo() {
        TestYut testYut = new TestYut(-1); // 빽도
        assertEquals(-1, testYut.getResult());
    }

    @Test
    void getResult_Do() {
        TestYut testYut = new TestYut(1); // 도
        assertEquals(1, testYut.getResult());
    }
    @Test
    void getResult_Gae() {
        TestYut testYut = new TestYut(2); // 개
        assertEquals(2, testYut.getResult());
    }
    @Test
    void getResult_Girl() {
        TestYut testYut = new TestYut(3); // 걸
        assertEquals(3, testYut.getResult());
    }
    @Test
    void getResult_Yut() {
        TestYut testYut = new TestYut(4); // 윷
        assertEquals(4, testYut.getResult());
    }
    @Test
    void getResult_Mo() {
        TestYut testYut = new TestYut(5); // 모
        assertEquals(5, testYut.getResult());
    }
    @Test
    void getResult_skipsInvalidThenReturnsValid() {
        TestYut testYut = new TestYut(10, 0, 3); // 잘못된 입력 뒤에 3
        assertEquals(3, testYut.getResult());
    }



}
