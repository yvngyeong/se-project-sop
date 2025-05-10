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
                    myPiece.setJustArrived(true);
                }

                Node targetNode = nodes.get(prev);
                handleCaptureAndGroup(myPiece, targetNode);
                targetNode.add(myPiece);
                return;
            } else // 시작지점일때
            {
                System.out.println("뒤로 갈 수 없음");
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
        if (myPiece.isFinished())
            return;

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
                    myPiece.setJustArrived(false);
                    myPiece.finish();
                    break;
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
                myPiece.pushPreviousPosition(prev1);
                myPiece.setPosition(position);
                nodes.get(position).add(myPiece);   //노드 정보도 갱신 필요
            }

        }}

        // 잡기
        Node nextNode = nodes.get(position);
        List<Piece> pieces = new ArrayList<>(nextNode.getOwnedPieces());

        for (int i = 0; i < pieces.size(); i++) {

            Piece opponentPiece = pieces.get(i);

            if (opponentPiece.isFinished()) continue;

            if ((opponentPiece.getOwnerId() != myPiece.getOwnerId())&& opponentPiece.getPosition() !=0 ) {
                nextNode.remove(pieces.get(i));
                opponentPiece.setPosition(0);

                opponentPiece.clearPreviousPositions(); // Stack 비우기
                opponentPiece.clearGroup();             // 그룹 리스트 비우기


                nodes.get(0).add(opponentPiece);
                isCatched=true;

            } else if ((opponentPiece.getOwnerId() == myPiece.getOwnerId() )&& myPiece.getPosition() != 0) // 같은 플레이어 말일때 -> 그룹핑
            {
                myPiece.grouping(opponentPiece);

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

        if (!myPiece.isFinished() && !isBackdo&&edges.get(position).isEmpty())
        {
            System.out.println("승리 (위치 " + position + ")");
            if (myPiece.getGroupId() == 1) {
                for (Piece grouped : myPiece.getGroupedPieces()) {
                    grouped.finish();
                }
            }
            myPiece.finish();

        }

    }
    public boolean isCatched() {
        return isCatched;
    }

}
