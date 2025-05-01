package com.example.demo;//6각형


//6각형
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
        for (int i = 0; i < 44; i++)
            if ((i % 5 == 0) && (i<=30) ) // 5의 배수는 CornerNode로 생성
                nodes.add(new CornerNode(i));
            else // 나머지는 NormalNode로 생성
                nodes.add(new NormalNode(i));
    }

    @Override
    public void createEdges() {

        for (int i = 0; i < 43; i++) {
            if(i==32) continue;
            edges.put(i, List.of(i + 1));
        }
        edges.put(29, List.of(0));
        edges.put(37, List.of(38));
        edges.put(38, List.of(30));
        edges.put(30, List.of(34));
        edges.put(34, List.of(33));
        edges.put(33, List.of(25));
        edges.put(36, List.of(35));
        edges.put(39, List.of(40));
        edges.put(40, List.of(30));
        edges.put(41, List.of(42));
        edges.put(42, List.of(30));
        edges.put(35, List.of(0));
        // 0 → 다음 노드 없음 → 이동 불가 → 승리 조건 발생
        edges.put(0, List.of()); // 또는 edges.remove(0)

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
            myPiece.setPosition(1); // 0 → 1
            myPiece.pushPreviousPosition(0);
            yutValue--; // 이미 1칸 이동했으므로 감소
        }
        if (myPiece.isFinished())
            return;

        int position = myPiece.getPosition();
        Node currentNode = nodes.get(position);
        currentNode.remove(myPiece);


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
                // 시작점(0)에 도달했지만 그것이 처음 도착이면 종료 아님
                // 종점(시작점)을 통과하거나 이동할 곳이 없으면 승리 처리
                System.out.println("승리");
                myPiece.finish();
                position = 43; // 명시적으로 승리 위치 지정
                break;
            }

            myPiece.pushPreviousPosition(position);
            position = nextPosition.get(0);

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