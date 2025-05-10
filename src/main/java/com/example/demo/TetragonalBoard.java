package com.example.demo;//4각형

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if(i==19) continue;
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

        edges.put(28,List.of(0));
        edges.put(19,List.of(0));

        // 0 → 다음은 승리조건으로 이동
        edges.put(0, List.of(29));
        edges.put(29, List.of()); // 29 이후는 없음 → 완주 처리

    }

    private void handleCaptureAndGroup(Piece myPiece, Node targetNode) {
        List<Piece> pieces = new ArrayList<>(targetNode.getOwnedPieces());

        for (Piece opponentPiece : pieces) {
            if (opponentPiece == myPiece) continue; // 자기 자신은 제외
            if (opponentPiece.isFinished()) continue; // 이미 완주한 말은 제외

            boolean isSameTeam = opponentPiece.getOwnerId() == myPiece.getOwnerId();
            boolean isSamePosition = opponentPiece.getPosition() == myPiece.getPosition();
            boolean isNotFinished = !opponentPiece.isFinished();

            // ✅ 1. 상대 팀이면 무조건 잡는다 (위치가 같고, 안 끝났으면)
            if (!isSameTeam && isSamePosition && isNotFinished) {
                targetNode.remove(opponentPiece);
                opponentPiece.setPosition(0);
                opponentPiece.clearPreviousPositions();
                opponentPiece.clearGroup();
                opponentPiece.setJustArrived(false);
                nodes.get(0).add(opponentPiece);
                isCatched = true;
                System.out.println("상대 팀 말 잡음!");
            }

            // ✅ 2. 같은 팀이면 그룹핑
            else if (isSameTeam && isSamePosition) {

                // 0번 노드인 경우: 동시에 도착한 경우만 그룹핑 허용
                if (myPiece.getPosition() == 0) {
                    if (myPiece.isJustArrived() && opponentPiece.isJustArrived()) {
                        myPiece.grouping(opponentPiece);
                        System.out.println("0번 노드에서 그룹핑함 (둘 다 방금 도착)");
                    }
                }

                // 0번 아닌 경우: 그냥 그룹핑
                else {
                    myPiece.grouping(opponentPiece);
                    System.out.println("그룹핑함");
                }
            }
        }
    }



    @Override
    public void movePosition(Piece myPiece, Integer yutValue) {

        isCatched = false;
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
                myPiece.setPosition(prev);            // ✅ 먼저 위치를 갱신해줘야 함!!
                if (prev == 0) {
                    myPiece.setJustArrived(true);
                }
                Node targetNode = nodes.get(prev);

                handleCaptureAndGroup(myPiece, targetNode); // ✅ 이제 정확한 위치 기반으로 잡기 검사 가능
                targetNode.add(myPiece);
            } else // 시작지점일때
            {
                System.out.println("뒤로 갈 수 없음");
                nodes.get(position).add(myPiece); // 그대로 다시 원위치// 테스트용으로 써본겁니다
            }

            return; // ⛔ 중복 방지용


        }

        // 0에서 처음 출발할 경우 → 임시로 0 → 1 연결해 이동시키기
        if(!isBackdo) {
            if (myPiece.getPosition() == 0 && (myPiece.popPreviousPosition() == -1)) {
                nodes.get(0).remove(myPiece);
                myPiece.setPosition(1); // 0 → 1
                myPiece.pushPreviousPosition(0);
                yutValue--; // 이미 1칸 이동했으므로 감소
            }

            position = myPiece.getPosition(); // 말의 현재 위치 가져옴
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
                System.out.println("현재 말 위치: " + position);
                List<Integer> nextPosition = edges.get(position);

                if (nextPosition == null || nextPosition.isEmpty()) {
                    System.out.println("경로 없음 → 완주 처리");
                    myPiece.finish();
                    break;
                }

                int next = nextPosition.get(0);

                myPiece.pushPreviousPosition(position);
                position = next;
                System.out.println("이동 후 말 위치: " + position);

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
            }
        }

        // 말 위치 등록
        myPiece.setPosition(position);
        nodes.get(position).add(myPiece);

        if (position == 0) {
            myPiece.setJustArrived(true);
        }

        // 먼저 그룹 전체를 같은 위치로 업데이트 (❗ 이게 핵심!)
        if (myPiece.getGroupId() == 1) {
            for (Piece grouped : myPiece.getGroupedPieces()) {
                grouped.setPosition(position);
            }
        }

        // ✅ 잡기 & 그룹핑 통합 처리 (핸들 함수 호출)
        handleCaptureAndGroup(myPiece, nodes.get(position));

       // ✅ 그룹 말 이동 동기화
        if (myPiece.getGroupId() == 1) {
            for (Piece grouped : myPiece.getGroupedPieces()) {
                if (grouped != myPiece) {
                    nodes.get(grouped.getPosition()).remove(grouped);
                    grouped.setPosition(position);
                    nodes.get(position).add(grouped);
                }
            }
        }

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

