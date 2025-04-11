package main;

import java.util.List;
import java.util.Map;

abstract class Board {
    public List<Node> nodes;
    public Map<Integer, List<Integer>> edges;

    public abstract void movePosition(Piece piece, Integer yutValue);

    public abstract void createNodes(); // 각 판 마다 동그라미 개수에 따라 노드 만들기

    public abstract void createBoard(); // 노드의 위치 정하기 -> ui 관련 함수라서 아직 안했습니다

    public abstract void createEdges(); // 각 판 마다 길 만들기

}