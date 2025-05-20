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
        edges.put(31, List.of(32));
        edges.put(32, List.of(30));
        edges.put(35, List.of(0));
        // 0 → 다음 노드는 완료 노드
        edges.put(0, List.of(43));
        edges.put(43,List.of());  //43부터는 완주 처리

    }

    private void handleCaptureAndGroup(Piece myPiece, Node targetNode) {
        List<Piece> pieces = new ArrayList<>(targetNode.getOwnedPieces());

        for (Piece opponentPiece : pieces) {
            if (opponentPiece == myPiece) continue;
            if (opponentPiece.isFinished()) continue;

            boolean isSameTeam = opponentPiece.getOwnerId() == myPiece.getOwnerId();
            boolean isSamePosition = opponentPiece.getPosition() == myPiece.getPosition();

            if (!isSameTeam && isSamePosition) {
                if(myPiece.getPosition()==0&&!opponentPiece.isJustArrived()){
                    break;
                }
                targetNode.remove(opponentPiece);
                opponentPiece.setPosition(0);
                opponentPiece.clearPreviousPositions();
                opponentPiece.clearGroup();
                opponentPiece.setJustArrived(false);
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

        int position = myPiece.getPosition();
        if (edges.get(position).isEmpty()) {
            // (그룹이 있으면 그룹도 함께 finish)
            if (myPiece.getGroupId() == 1) {
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

        if (position == 0 && myPiece.isWaitingForFinish() && yutValue != -1) {
            System.out.println("\uD83C\uDF1F 백도 후 첫 이동 → 완주 처리");
            myPiece.finish();
            myPiece.setWaitingForFinish(false);
            return;
        }


        // 빽도
        if (yutValue == -1) {
            if (myPiece.isFinished())
                return;

            int prev = myPiece.popPreviousPosition(); // 본인의 이전 위치
            nodes.get(position).remove(myPiece);

            if (prev != -1) {
                System.out.println("빽도");

                myPiece.setPosition(prev);
                if (prev == 0) {
                    myPiece.setJustArrived(true);
                    myPiece.setWaitingForFinish(true);
                }

                Node targetNode = nodes.get(prev);
                handleCaptureAndGroup(myPiece, targetNode);
                targetNode.add(myPiece);

                if (myPiece.getGroupId() != 0) {
                    for (Piece grouped : myPiece.getGroupedPieces()) {
                        if (grouped != myPiece && !grouped.isFinished()) {
                            nodes.get(grouped.getPosition()).remove(grouped);

                            grouped.popPreviousPosition(); // ✅ 스택 정리만
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
        if (myPiece.getPosition() == 0 && (myPiece.popPreviousPosition() == -1)) {
            nodes.get(0).remove(myPiece);
            myPiece.setPosition(1); // 0 → 1
            myPiece.pushPreviousPosition(0);
            yutValue--; // 이미 1칸 이동했으므로 감소
        }

        position = myPiece.getPosition();
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
        } else if (position == 20) {
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
            if(position==34){
                int prev1 = myPiece.popPreviousPosition();
                int prev2 = myPiece.popPreviousPosition();

                if (prev2 == 32 || prev2 == 42) {
                    position = 36;
                } else {
                    position = nextPosition.get(0);  // 일반 30→34
                }
                myPiece.pushPreviousPosition(prev2);
                myPiece.pushPreviousPosition(prev1);

                myPiece.setPosition(position);
                nodes.get(position).add(myPiece);   //노드 정보도 갱신 필요
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
        if(position==43){
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
            }
        }


    }
    public boolean isCatched() {
        return isCatched;
    }

}
