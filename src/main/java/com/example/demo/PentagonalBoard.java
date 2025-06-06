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

    @Override
    public void movePosition(Piece myPiece, Integer yutValue) {

        int position = myPiece.getPosition();
        if (edges.get(position).isEmpty()) {
            // (그룹이 있으면 그룹도 함께 finish)
            if (myPiece.getGroupId() != -1) {
                for (Piece g : myPiece.getGroupedPieces()) {
                    if (!g.isFinished()) g.finish();
                }
            }
            if (!myPiece.isFinished()) {
                System.out.println("승리 (위치 " + position + ")");
                myPiece.finish();
            }
            return;
        }

        isCatched=false;
        isBackdo = false;

        // 백도로 0번 도착 후 다음 이동 → 완주 처리
        if (position == 0 && myPiece.isWaitingForFinish() && yutValue != -1) {
            System.out.println(" 백도 후 첫 이동 → 완주 처리");
            List<Piece> groupedCopy = new ArrayList<>(myPiece.getGroupedPieces()); // ✅ 백업 먼저
            myPiece.finish();
            myPiece.setWaitingForFinish(false);

            return;
        }

        // 빽도
        // 빽도 처리
        if (yutValue == -1) {
            if (myPiece.isFinished())
                return;

            int prev = myPiece.popPreviousPosition(); // 본인의 이전 위치
            nodes.get(position).remove(myPiece);

            if (prev != -1) {
                System.out.println("빽도");

                myPiece.setPosition(prev);
                if (prev == 0) {
                    myPiece.setJustArrived(true);  // ⬅ View에서 그릴 수 있도록 true
                    myPiece.setWaitingForFinish(true);

                    myPiece.pushPreviousPosition(1);

                }

                Node targetNode = nodes.get(prev);
                handleCaptureAndGroup(myPiece, targetNode);
                targetNode.add(myPiece);


                if (myPiece.getGroupId() != -1) {
                    for (Piece grouped : myPiece.getGroupedPieces()) {
                        if (grouped != myPiece && !grouped.isFinished()) {
                            nodes.get(grouped.getPosition()).remove(grouped);
                            grouped.setPosition(prev);     // ✅ leader와 동일한 prev 적용
                            nodes.get(prev).add(grouped);

                            if (prev == 0) {
                                grouped.setJustArrived(true);
                            }
                        }
                    }
                }


                return;
            } else {
                System.out.println("뒤로 갈 수 없음");
                myPiece.setWaitingForFinish(true);
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
                        myPiece.setJustArrived(true);
                        if (myPiece.getGroupId() != -1) {
                            for (Piece grouped : myPiece.getGroupedPieces()) {
                                if (grouped != myPiece && !grouped.isFinished()) {
                                    grouped.finish();
                                    nodes.get(grouped.getPosition()).remove(grouped); // 노드에서 제거도 필요
                                }
                            }


                        }
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
                    myPiece.pushPreviousPosition(prev2);
                    myPiece.pushPreviousPosition(prev1);
                    myPiece.setPosition(position);
                    nodes.get(position).add(myPiece);       //노드 정보도 갱신 필요
                }

            }}

        // 말 위치 등록
        myPiece.setPosition(position);
        nodes.get(position).add(myPiece);

        if (position == 0) {
            myPiece.setJustArrived(true);
            for (Piece grouped : myPiece.getGroupedPieces()) {
                if (grouped != myPiece && !grouped.isFinished()) {
                    grouped.setJustArrived(true);
                }
            }
        }
        if(position==36){
            myPiece.finish();
            for (Piece grouped : myPiece.getGroupedPieces()) {
                if (grouped != myPiece) {
                    grouped.setJustArrived(false);
                    grouped.finish();
                }
            }

        }

        // ✅ 잡기 & 그룹핑 통합 처리 (핸들 함수 호출)
        handleCaptureAndGroup(myPiece, nodes.get(position));

        // ✅ 그룹 이동 처리 (그룹핑 후 최신 상태 기준 + previous 동기화)
        for (Piece grouped : myPiece.getGroupedPieces()) {
            if (grouped != myPiece && !grouped.isFinished()) {
                nodes.get(grouped.getPosition()).remove(grouped);

                grouped.pushPreviousPosition(myPiece.peekPreviousPosition()); // ✅ 현재 위치를 이전 위치로 저장
                grouped.setPosition(position);
                nodes.get(position).add(grouped);

                if (position == 0) {
                    grouped.setJustArrived(true);
                }
                else if (position==36){
                    myPiece.finish();
                    grouped.setJustArrived(false);
                    grouped.finish();
                }
            }
        }


    }
    public boolean isCatched() {
        return isCatched;
    }

}
