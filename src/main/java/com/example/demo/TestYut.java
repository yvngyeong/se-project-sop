package com.example.demo;

import java.util.Queue;
import java.util.LinkedList;

public class TestYut extends Yut {
    private int nextValue;

    public TestYut(int... values) {
    }
    public void setNext(int value) {
        this.nextValue = value;
    }

    @Override
    public Integer getResult() {
        return nextValue;
    }
}
