package com.example.demo;
//5각형

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PentagonalBoard extends Board {

    public PentagonalBoard() {
        nodes = new ArrayList<>();
        edges = new HashMap<>();
    }

    @Override
    public void createNodes() {

        for (int i = 0; i < 37; i++)
            nodes.add(new Node(i));
    }

    @Override
    public void createEdges() {

        for (int i = 0; i < 36; i++) {
            edges.put(i, List.of(i + 1));
        }

        edges.put(27, List.of(25));
        edges.put(29, List.of(25));

        edges.put(31, List.of(30));
        edges.put(30, List.of(25));

        // 중심점 25에서 멈추지 않았을때
        edges.put(25, List.of(32));
        edges.put(33, List.of(20));

        // 중심점 25에서 멈췄을때
        edges.put(34, List.of(35));
        edges.put(35, List.of(36));

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

        if (position == 25) // 시작 위치가 25일때(25에서 딱 멈췄을때)
        {
            myPiece.pushPreviousPosition(position);
            position = 34;
            yutValue--;
        } else if (position == 5) // 시작 위치가 25일때(25에서 딱 멈췄을때)
        {
            myPiece.pushPreviousPosition(position);
            position = 26;
            yutValue--;
        } else if (position == 10) // 시작 위치가 25일때(25에서 딱 멈췄을때)
        {
            myPiece.pushPreviousPosition(position);
            position = 28;
            yutValue--;
        } else if (position == 15) // 시작 위치가 25일때(25에서 딱 멈췄을때)
        {
            myPiece.pushPreviousPosition(position);
            position = 31;
            yutValue--;
        }

        for (int i = 0; i < yutValue; i++) {
            List<Integer> nextPosition = edges.get(position);

            if (nextPosition == null || nextPosition.isEmpty()) {
                // 종점(시작점)을 통과하거나 이동할 곳이 없으면 승리 처리
                System.out.println("승리");
                myPiece.finish();
                position = 36; // 명시적으로 승리 위치 지정
                break;
            }

            myPiece.pushPreviousPosition(position);
            position = nextPosition.get(0);

            if (position == 36) {
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
                System.out.println("상대 팀 말 잡음!");
            } else if (opponentPiece.getOwnerId() == myPiece.getOwnerId()) // 같은 플레이어 말일때 -> 그룹핑
            {
                myPiece.grouping(opponentPiece);
                System.out.println("그룹핑함");// 테스트용으로 써본겁니다

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