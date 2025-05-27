package com.example.demo;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestYutTest {

    @Test
    void getResult_acceptsNegativeOneAsBackDo() {
        TestYut testYut = new TestYut(); // 빽도
        testYut.setNext(-1);
        assertEquals(-1, testYut.getResult());
    }

    @Test
    void getResult_Do() {
        TestYut testYut = new TestYut();
        testYut.setNext(1);// 도
        assertEquals(1, testYut.getResult());
    }
    @Test
    void getResult_Gae() {
        TestYut testYut = new TestYut(); // 개
        testYut.setNext(2);
        assertEquals(2, testYut.getResult());
    }
    @Test
    void getResult_Girl() {
        TestYut testYut = new TestYut(); // 걸
        testYut.setNext(3);
        assertEquals(3, testYut.getResult());
    }
    @Test
    void getResult_Yut() {
        TestYut testYut = new TestYut(); // 윷
        testYut.setNext(4);
        assertEquals(4, testYut.getResult());
    }
    @Test
    void getResult_Mo() {
        TestYut testYut = new TestYut(); // 모
        testYut.setNext(5);
        assertEquals(5, testYut.getResult());
    }



}
