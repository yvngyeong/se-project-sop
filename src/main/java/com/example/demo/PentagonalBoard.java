package com.example.demo;//5각형

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
        for (int i = 0; i < 37; i++) {
            if (i == 0 || i == 5 || i == 10 || i == 15 || i == 30) {
                nodes.add(new CornerNode(i));  // 예: CornerNode
            } else {
                nodes.add(new NormalNode(i));  // NormalNode
            }
        }
    }

    @Override
    public void createEdges() {

        for (int i = 0; i < 36; i++) {
            edges.put(i, List.of(i + 1));
        }

        edges.put(5, List.of(26));
        edges.put(10,List.of(28));
        edges.put(15,List.of(31));

        edges.put(27, List.of(25));
        edges.put(29, List.of(25));

        edges.put(31, List.of(30));
        edges.put(30, List.of(25));

        edges.put(25, List.of(34));
        edges.put(33, List.of(20));

        edges.put(24,List.of(0));
        edges.put(35, List.of(0));
        edges.put(0,List.of());

    }

    @Override
    public void movePosition(Piece myPiece, Integer yutValue) {
        // 빽도
        if (yutValue == -1) {
            if (myPiece.isFinished())
                return;

            int prev = myPiece.popPreviousPosition(); // 말이 지나온 경로 중 가장 최근 위치
            int position = myPiece.getPosition();
            nodes.get(position).remove(myPiece);

            if (prev != -1) // 뒤로 갈 수 있을 때
            {
                position = prev;
                System.out.println("빽도"); // 테스트용으로 써본겁니다
            } else // 시작지점일때
            {
                System.out.println("뒤로 갈 수 없음"); // 테스트용으로 써본겁니다
            }
            myPiece.setPosition(position);
            nodes.get(position).add(myPiece);
            return;
        }

        // 0에서 처음 출발할 경우 → 임시로 0 → 1 연결해 이동시키기
        if (myPiece.getPosition() == 0 && (myPiece.popPreviousPosition() == -1) ) {
            myPiece.setPosition(1); // 0 → 1
            myPiece.pushPreviousPosition(0);
            yutValue--; // 이미 1칸 이동했으므로 감소
        }

        if (myPiece.isFinished())
            return;

        int position = myPiece.getPosition();
        Node currentNode = nodes.get(position);
        currentNode.remove(myPiece);


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