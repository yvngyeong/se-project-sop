package com.example.demo;//6각형

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HexagonalBoard extends Board {

    public HexagonalBoard() {
        nodes = new ArrayList<>();
        edges = new HashMap<>();
    }

    @Override
    public void createNodes() {
        for (int i = 0; i < 44; i++) {
            if (i == 0 || i == 5 || i == 10 || i == 15 || i == 30) {
                nodes.add(new CornerNode(i));  // 예: CornerNode
            } else {
                nodes.add(new NormalNode(i));  // NormalNode
            }
        }
    }

    @Override
    public void createEdges() {

        for (int i = 0; i < 43; i++) {
            edges.put(i, List.of(i + 1));
        }

        edges.put(37, List.of(38));
        edges.put(38, List.of(30));
        edges.put(30, List.of(34));
        edges.put(34, List.of(33));
        edges.put(33, List.of(25));
        edges.put(36, List.of(35));
        edges.put(35, List.of(43));
        edges.put(39, List.of(40));
        edges.put(40, List.of(30));
        edges.put(41, List.of(42));
        edges.put(42, List.of(30));
        edges.put(43, List.of(35));

    }

    @Override
    public void createBoard() {

    }

    @Override
    public void movePosition(Piece myPiece, Integer yutValue) {

        if (myPiece.isFinished())
            return;

        int position = myPiece.getPosition();
        Node currentNode = nodes.get(position);
        currentNode.remove(myPiece);

        if (yutValue == -1) {
            int prev = myPiece.popPreviousPosition();
            if (prev != -1) {
                position = prev;
                System.out.println("빽도");
            } else {
                System.out.println("뒤로 갈 수 없음");
            }
            myPiece.setPosition(position);
            nodes.get(position).add(myPiece);
            return;
        }

        if (position == 5) {
            myPiece.pushPreviousPosition(position);
            position = 37;
            yutValue--;
        } else if (position == 10) {
            myPiece.pushPreviousPosition(position);
            position = 39;
            yutValue--;
        } else if (position == 15) {
            myPiece.pushPreviousPosition(position);
            position = 41;
            yutValue--;
        } else if (position == 30) {
            myPiece.pushPreviousPosition(position);
            position = 36;
            yutValue--;
        }

        for (int i = 0; i < yutValue; i++) {
            List<Integer> nextPosition = edges.get(position);

            if (nextPosition == null || nextPosition.isEmpty()) {
                // 종점(시작점)을 통과하거나 이동할 곳이 없으면 승리 처리
                System.out.println("승리");
                myPiece.finish();
                position = 43; // 명시적으로 승리 위치 지정
                break;
            }

            myPiece.pushPreviousPosition(position);
            position = nextPosition.get(0);

            if (position == 43) {
                System.out.println("승리");
                myPiece.finish();
                break;
            }
        }

        // 잡기
        Node nextNode = nodes.get(position);
        List<Piece> pieces = new ArrayList<>(nextNode.getOwnedPieces());

        for (int i = 0; i < pieces.size(); i++) {
            Piece opponentPiece = pieces.get(i);
            if (opponentPiece.getOwnerId() != myPiece.getOwnerId()) {
                nextNode.remove(pieces.get(i));
                opponentPiece.setPosition(0);
                nodes.get(0).add(opponentPiece);

            } else if (opponentPiece.getOwnerId() == myPiece.getOwnerId()) // 같은 플레이어 말일때 -> 그룹핑
            {
                myPiece.grouping(opponentPiece);

            }
        }

        myPiece.setPosition(position);
        nodes.get(position).add(myPiece);

        if (myPiece.getGroupId() == 1) {
            for (Piece grouped : myPiece.getGroupedPieces()) {
                if (grouped != myPiece) {
                    grouped.setPosition(position);
                    nodes.get(position).add(grouped);
                }
            }
        }

    }

}