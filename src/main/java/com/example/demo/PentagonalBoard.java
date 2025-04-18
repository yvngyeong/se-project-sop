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

        /* ────── 1. 빽도(‑1) ────── */
        if (yutValue == -1) {
            if (myPiece.isFinished()) return;


            int prev = myPiece.popPreviousPosition();          // 대표 pop

            int cur = myPiece.getPosition();
            nodes.get(cur).remove(myPiece);

            int newPos = (prev != -1) ? prev : cur;            // 0이면 못 움직임
            if (prev != -1) System.out.println("빽도");
            else            System.out.println("뒤로 갈 수 없음");

            // ② 위치·노드 전원 동기화
            for (Piece g : myPiece.getGroupedPieces()) {
                nodes.get(g.getPosition()).remove(g);
                g.setPosition(newPos);
                if (!nodes.get(newPos).getOwnedPieces().contains(g))
                    nodes.get(newPos).add(g);
            }
            return;
        }

        /* ────── 2. 0 → 1 첫 출발 ────── */
        boolean firstMove = false;
        if (myPiece.getPosition() == 0 && myPiece.popPreviousPosition() == -1) {

            for (Piece g : myPiece.getGroupedPieces()) {
                nodes.get(0).remove(g);
                g.pushPreviousPosition(0);
                g.setPosition(1);
                if (!nodes.get(1).getOwnedPieces().contains(g))
                    nodes.get(1).add(g);
            }
            yutValue--;              // 한 칸 소비
            firstMove = true;        // 이후 스택 처리에 사용
        }

        /* ────── 3. 이동 전 push ────── */
        if (!firstMove) {
            for (Piece g : myPiece.getGroupedPieces())
                g.pushPreviousPosition(g.getPosition());
        }

        /* ────── 4. 현재 위치 변수 ────── */
        int position = myPiece.getPosition();
        nodes.get(position).remove(myPiece);

        /* ────── 5. 지름길 진입 ────── */
        if (position == 25) { myPiece.pushPreviousPosition(position); position = 34; yutValue--; }
        else if (position == 5)  { myPiece.pushPreviousPosition(position); position = 26; yutValue--; }
        else if (position == 10) { myPiece.pushPreviousPosition(position); position = 28; yutValue--; }
        else if (position == 15) { myPiece.pushPreviousPosition(position); position = 31; yutValue--; }

        /* ────── 6. 본 이동 루프 ────── */
        for (int i = 0; i < yutValue; i++) {
            List<Integer> next = edges.get(position);
            if (next == null || next.isEmpty()) {         // 도착
                myPiece.finish();
                position = 36;
                break;
            }

            for (Piece g : myPiece.getGroupedPieces())    // 전원 push
                g.pushPreviousPosition(position);

            position = next.get(0);

            if (position == 36) { myPiece.finish(); break; }
        }

        /* ────── 7. 잡기 & 그룹핑 ────── */
        Node nextNode = nodes.get(position);
        List<Piece> onNode = new ArrayList<>(nextNode.getOwnedPieces());

        for (Piece p : onNode) {
            if (p.getOwnerId() != myPiece.getOwnerId()) {          // 잡기
                nextNode.remove(p);
                p.setPosition(0);
                nodes.get(0).add(p);
                System.out.println("상대 팀 말 잡음!");
            } else if (p != myPiece) {                             // 그룹핑
                myPiece.grouping(p);
                System.out.println("그룹핑함");
            }
        }

        /* ────── 8. 최종 위치·노드 전원 동기화 ────── */
        for (Piece g : myPiece.getGroupedPieces()) {
            nodes.get(g.getPosition()).remove(g);
            g.setPosition(position);
            if (!nodes.get(position).getOwnedPieces().contains(g))
                nodes.get(position).add(g);
        }
        /* ★ 대표 말도 직접 갱신해 주기 ───────────── */
        myPiece.setPosition(position);
        if (!nodes.get(position).getOwnedPieces().contains(myPiece))
            nodes.get(position).add(myPiece);
    }

}