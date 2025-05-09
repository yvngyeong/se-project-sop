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
            if (i == 0 || i == 5 || i == 10 || i == 15 || i == 20 || i==25) {
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
        edges.put(25,List.of(32));


        edges.put(27, List.of(26));
        edges.put(26, List.of(25));
        edges.put(29, List.of(28));
        edges.put(28, List.of(25));

        edges.put(31, List.of(30));
        edges.put(30, List.of(25));

        edges.put(33, List.of(20));

        edges.put(24,List.of(0));
        edges.put(35, List.of(0));
        edges.put(0,List.of());

    }

    @Override
    public void movePosition(Piece myPiece, Integer yutValue) {
        isCatched=false;
        isBackdo = false;
        int position = myPiece.getPosition();
        // 빽도
        if (yutValue == -1) {
            if (myPiece.isFinished())
                return;

            int prev = myPiece.popPreviousPosition(); // 말이 지나온 경로 중 가장 최근 위치

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
            isBackdo = true;

        }

        // 0에서 처음 출발할 경우 → 임시로 0 → 1 연결해 이동시키기

        if(!isBackdo){
            if (myPiece.getPosition() == 0 && (myPiece.popPreviousPosition() == -1) ) {
                myPiece.setPosition(1); // 0 → 1
                myPiece.pushPreviousPosition(0);
                yutValue--; // 이미 1칸 이동했으므로 감소
            }

            if (myPiece.isFinished())
                return;

            position = myPiece.getPosition();
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
                position = 27;
                yutValue--;
            } else if (position == 10) // 시작 위치가 25일때(25에서 딱 멈췄을때)
            {
                myPiece.pushPreviousPosition(position);
                position = 29;
                yutValue--;
            } else if (position == 15) // 시작 위치가 25일때(25에서 딱 멈췄을때)
            {
                myPiece.pushPreviousPosition(position);
                position = 31;
                yutValue--;
            }

            for (int i = 0; i < yutValue; i++) {
                List<Integer> nextPosition = edges.get(position);
                if ((nextPosition == null || nextPosition.isEmpty())) {
                    if (position == 0) {
                        // 0번은 그냥 도달만 함
                        break;
                    }

                    // 종점(시작점)을 통과하거나 이동할 곳이 없으면 승리 처리
                    System.out.println("승리");
                    if (myPiece.getGroupId() == 1) {
                        for (Piece grouped : myPiece.getGroupedPieces()) {
                            grouped.finish();
                        }
                    }
                    myPiece.finish();
                    position = 36; // 명시적으로 승리 위치 지정
                    break;
                }
                myPiece.pushPreviousPosition(position);
                // 🎯 25로 가려는 순간 & 이전이 15,30,31인 경우 → 강제로 34로 분기
                if (nextPosition.size() == 1 && nextPosition.get(0) == 25) {
                    int prev = myPiece.getPreviousPosition(); // peek
                    if (position == 30 || position == 31 || position == 15) {
                        i++;
                        position = 34; // 🔥 강제 분기!
                        continue; // 다음 루프 진행
                    }
                }
                // 25 → 34로 가는 예외 케이스
                if (position == 25) {
                    int prev = myPiece.getPreviousPosition(); // 새로 메서드 만들거나 스택 확인
                    // 특수 라인(15, 30, 31) 또는 yutValue == 0일 때만 34로
                    if (prev == 15 || prev == 30 || prev == 31 || yutValue - i == 0) {
                        position = 34;
                    } else {
                        position = 32;
                    }
                } else {
                    position = nextPosition.get(0);
                }

                if (position == 36) {
                    System.out.println("승리");
                    myPiece.finish();
                    break;
                }
            }}

        // 잡기
        Node nextNode = nodes.get(position);
        List<Piece> pieces = new ArrayList<>(nextNode.getOwnedPieces());

        for (int i = 0; i < pieces.size(); i++) {
            Piece opponentPiece = pieces.get(i);

            if (opponentPiece.isFinished()) continue;

            if ((opponentPiece.getOwnerId() != myPiece.getOwnerId())&& opponentPiece.getPosition() !=0) {

                nextNode.remove(pieces.get(i));
                opponentPiece.setPosition(0);

                opponentPiece.clearPreviousPositions(); // Stack 비우기
                opponentPiece.clearGroup();             // 그룹 리스트 비우기

                nodes.get(0).add(opponentPiece);
                isCatched=true;
                System.out.println("상대 팀 말 잡음!");
            } else if ((opponentPiece.getOwnerId() == myPiece.getOwnerId())&& myPiece.getPosition() != 0) // 같은 플레이어 말일때 -> 그룹핑
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
                    nodes.get(grouped.getPosition()).remove(grouped);
                    grouped.setPosition(position);
                    nodes.get(position).add(grouped);
                }
            }
        }

    }
    public boolean isCatched() {
        return isCatched;
    }

}
