package com.example.demo;//4각형

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TetragonalBoard extends Board {

    public TetragonalBoard() {
        nodes = new ArrayList<>();
        edges = new HashMap<>();
    }

    @Override
    public void createNodes() {
        for (int i = 0; i < 30; i++) {
            if (i == 0 || i == 5 || i == 10 || i == 15 || i == 20 ) {
                nodes.add(new CornerNode(i));  // 예: CornerNode
            } else {
                nodes.add(new NormalNode(i));  // NormalNode
            }
        }
    }

    @Override
    public void createEdges() {

        for (int i = 0; i < 29; i++) // 지름길 아닐때는 그냥 +1
        {
            edges.put(i, List.of(i + 1));
        }

        // 지름길 -> 위의 결과보다 더 빠른 순서로 저장

        edges.put(22, List.of(21));
        edges.put(21, List.of(20));

        edges.put(24, List.of(23));
        edges.put(23, List.of(20));

        // 딱 중심점에서 멈추지 않았을때
        edges.put(26, List.of(15));
        edges.put(20, List.of(25));

        // 0 → 다음 노드 없음 → 이동 불가 → 승리 조건 발생
        edges.put(0, List.of()); // 또는 edges.remove(0)


    }

    @Override
    public void createBoard() {

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
        if (myPiece.getPosition() == 0 && (myPiece.popPreviousPosition() == -1)) {
            nodes.get(0).remove(myPiece);
            myPiece.setPosition(1); // 0 → 1
            myPiece.pushPreviousPosition(0);
            yutValue--; // 이미 1칸 이동했으므로 감소
        }

        int position = myPiece.getPosition(); // 말의 현재 위치 가져옴
        Node currentNode = nodes.get(position); // 말이 현재 위치해 있는 노드 가져옴
        currentNode.remove(myPiece); // 현재 위치해 있는 노드에서 말 삭제



        if (position == 5) // 시작 위치가 5일때(5에서 딱 멈췄을때)
        {
            myPiece.pushPreviousPosition(position);
            position = 22;
            yutValue--;
        }
        if (position == 10) // 시작 위치가 10일때(10에서 딱 멈췄을때)
        {
            myPiece.pushPreviousPosition(position);
            position = 24;
            yutValue--;
        }
        if (position == 20) // 시작 위치가 20일때(20에서 딱 멈췄을때)
        {
            myPiece.pushPreviousPosition(position);
            position = 27;
            yutValue--;
        }

        for (int i = 0; i < yutValue; i++) {
            List<Integer> nextPosition = edges.get(position);

            if (nextPosition == null || nextPosition.isEmpty()) {
                // 종점(시작점)을 통과하거나 이동할 곳이 없으면 승리 처리
                System.out.println("도착");
                myPiece.finish();
                position = 29; // 명시적으로 승리 위치 지정
                break;
            }

            myPiece.pushPreviousPosition(position);
            position = nextPosition.get(0);

        }

        // 잡기 & 그룹핑
        Node nextNode = nodes.get(position); // 말이 도착할 위치에 다른 말이 있는지 알기 위해 ..
        List<Piece> pieces = new ArrayList<>(nextNode.getOwnedPieces()); // 말이 도착할 위치에 있는 모든 말들의 리스트

        for (int i = 0; i < pieces.size(); i++) {
            Piece opponentPiece = pieces.get(i);
            if (opponentPiece.getOwnerId() != myPiece.getOwnerId()) // 같은 플레이어의 말이 아닐때 -> 잡기
            {
                nextNode.remove(pieces.get(i));
                opponentPiece.setPosition(0);
                nodes.get(0).add(opponentPiece);
                System.out.println("상대 팀 말 잡음! "); // 테스트용으로 써본겁니다
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
