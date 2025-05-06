package com.example.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Board {
    public List<Node> nodes;
    public Map<Integer, List<Integer>> edges;

    protected boolean isCatched = false;
    protected boolean isBackdo = false;

    public abstract void movePosition(Piece piece, Integer yutValue);

    public abstract void createNodes(); // 각 판 마다 동그라미 개수에 따라 노드 만들기

    public abstract void createEdges(); // 각 판 마다 길 만들기

    public List<Node> getNodes() { return nodes; }

    public boolean isCatched() {return isCatched;}

    public HashMap<Integer, List<Integer>> getEdges() {
        return (HashMap<Integer, List<Integer>>) edges;
    }
}