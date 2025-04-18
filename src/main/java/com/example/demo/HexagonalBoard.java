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
            if (i % 5 == 0) // 5의 배수는 CornerNode로 생성
                nodes.add(new CornerNode(i));
            else // 나머지는 NormalNode로 생성
                nodes.add(new NormalNode(i));
    }

    @Override
    public void createEdges() {

        for (int i = 0; i < 43; i++) {
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

        /* ────── 1. 빽도(‑1) 처리 ────── */
        if (yutValue == -1) {
            if (myPiece.isFinished()) return;


            int prev = myPiece.popPreviousPosition();        // 대표 pop
            int cur  = myPiece.getPosition();
            int pos  = (prev != -1) ? prev : cur;

            // (B) 위치·노드 전원 동기화
            for (Piece g : myPiece.getGroupedPieces()) {
                nodes.get(g.getPosition()).remove(g);
                g.setPosition(pos);
                if (!nodes.get(pos).getOwnedPieces().contains(g))
                    nodes.get(pos).add(g);
            }
            System.out.println(prev != -1 ? "빽도" : "뒤로 갈 수 없음");
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
            yutValue--;
            firstMove = true;
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
        if (position == 5)  { myPiece.pushPreviousPosition(position); position = 37; yutValue--; }
        else if (position == 10){ myPiece.pushPreviousPosition(position); position = 39; yutValue--; }
        else if (position == 15){ myPiece.pushPreviousPosition(position); position = 41; yutValue--; }
        else if (position == 30){ myPiece.pushPreviousPosition(position); position = 36; yutValue--; }

        /* ────── 6. 본 이동 루프 ────── */
        for (int i = 0; i < yutValue; i++) {
            List<Integer> next = edges.get(position);
            if (next == null || next.isEmpty()) {                 // 도착
                myPiece.finish();
                position = 43;
                break;
            }
            for (Piece g : myPiece.getGroupedPieces())
                g.pushPreviousPosition(position);                 // 전원 push

            position = next.get(0);
            if (position == 43) { myPiece.finish(); break; }
        }

        /* ────── 7. 잡기 & 그룹핑 ────── */
        Node nextNode = nodes.get(position);
        for (Piece p : new ArrayList<>(nextNode.getOwnedPieces())) {
            if (p.getOwnerId() != myPiece.getOwnerId()) {         // 잡기
                nextNode.remove(p);
                p.setPosition(0);
                nodes.get(0).add(p);
            } else if (p != myPiece) {                            // 그룹핑
                myPiece.grouping(p);
            }
        }

        /* ────── 8. 최종 위치·노드 전원 동기화 ────── */
        for (Piece g : myPiece.getGroupedPieces()) {
            nodes.get(g.getPosition()).remove(g);
            g.setPosition(position);
            if (!nodes.get(position).getOwnedPieces().contains(g))
                nodes.get(position).add(g);
        }
    }
}