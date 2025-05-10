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

        edges.put(0,List.of(36));
        edges.put(36,List.of());  //36 -> 완주 처리

    }

    private void handleCaptureAndGroup(Piece myPiece, Node targetNode) {
        List<Piece> pieces = new ArrayList<>(targetNode.getOwnedPieces());

        for (Piece opponentPiece : pieces) {
            if (opponentPiece == myPiece) continue;
            if (opponentPiece.isFinished()) continue;

            boolean isSameTeam = opponentPiece.getOwnerId() == myPiece.getOwnerId();
            boolean isSamePosition = opponentPiece.getPosition() == myPiece.getPosition();

            if (!isSameTeam && isSamePosition) {
                targetNode.remove(opponentPiece);
                opponentPiece.setPosition(0);
                opponentPiece.clearPreviousPositions();
                opponentPiece.clearGroup();
                opponentPiece.setJustArrived(false); // ✅ 잡힌 말은 표시 안 되게
                nodes.get(0).add(opponentPiece);
                isCatched = true;
                System.out.println("상대 팀 말 잡음!");
            } else if (isSameTeam && isSamePosition) {
                if (myPiece.getPosition() == 0) {
                    if (myPiece.isJustArrived() && opponentPiece.isJustArrived()) {
                        myPiece.grouping(opponentPiece);
                        System.out.println("0번 노드 그룹핑 (둘 다 justArrived)");
                    }
                } else {
                    myPiece.grouping(opponentPiece);
                    System.out.println("그룹핑함");
                }
            }
        }
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
                System.out.println("빽도");

                myPiece.setPosition(prev);
                if (prev == 0) {
                    myPiece.setJustArrived(true);  // ⬅ View에서 그릴 수 있도록 true
                }

                Node targetNode = nodes.get(prev);
                handleCaptureAndGroup(myPiece, targetNode);  // ⬅ 잡기/그룹핑 처리
                targetNode.add(myPiece);
                return;  // 중복 방지
            } else // 시작지점일때
            {
                System.out.println("뒤로 갈 수 없음");
                nodes.get(position).add(myPiece);
                return;
            }
        }

        // 0에서 처음 출발할 경우 → 임시로 0 → 1 연결해 이동시키기

        if(!isBackdo){
            if (myPiece.getPosition() == 0 && (myPiece.popPreviousPosition() == -1) ) {
                nodes.get(0).remove(myPiece);
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
                if (nextPosition == null || nextPosition.isEmpty()) {
                    System.out.println("경로 없음 → 완주 처리");
                    myPiece.finish();
                    break;
                }

                int next = nextPosition.get(0);
                myPiece.pushPreviousPosition(position);
                position = next;

                if (position == 0) {
                    if (i == yutValue - 1) {
                        // 0번에서 정확히 멈춤
                        myPiece.setJustArrived(true);
                        System.out.println("0번 도착 → justArrived true");
                    } else {
                        // 0번 지나침 → 완주
                        System.out.println("0번 도착했지만 이동 남음 → 완주");
                        myPiece.setJustArrived(false);
                        myPiece.finish();
                        break;
                    }
                }
                if(position==32){
                    int prev1 = myPiece.popPreviousPosition();
                    int prev2 = myPiece.popPreviousPosition();

                    if (prev2 == 30) {
                        position = 34;
                    } else {
                        position = nextPosition.get(0);  // 일반 25->32
                    }
                    myPiece.pushPreviousPosition(prev1);
                    myPiece.setPosition(position);
                    nodes.get(position).add(myPiece);       //노드 정보도 갱신 필요
                }

                System.out.println("이동 후 말 위치: " + position);
            }}

        // 말 위치 등록
        myPiece.setPosition(position);
        nodes.get(position).add(myPiece);

        if (position == 0) {
            myPiece.setJustArrived(true);
        }

        // ✅ 잡기 & 그룹핑 통합 처리 (핸들 함수 호출)
        handleCaptureAndGroup(myPiece, nodes.get(position));

        // ✅ 그룹 이동 처리 (그룹핑 후 최신 상태 기준)
        for (Piece grouped : myPiece.getGroupedPieces()) {
            if (grouped != myPiece && !grouped.isFinished()) {
                nodes.get(grouped.getPosition()).remove(grouped);
                grouped.setPosition(position);
                nodes.get(position).add(grouped);

                if (position == 0) {
                    grouped.setJustArrived(true);  // ✅ 여기 중요
                }
            }
        }

    }
    public boolean isCatched() {
        return isCatched;
    }

}
