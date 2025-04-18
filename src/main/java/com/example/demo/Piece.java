package com.example.demo;

import java.util.*;

public class Piece {
    private int position;
    private int ownerId;
    private boolean isFinished;
    private int groupId = 0;
    private static int NEXT_GID = 1;

    // 추가
    private Stack<Integer> posStack = new Stack<>();
    private List<Piece> groupPieces = new ArrayList<>();

    public Piece(int ownerId, int groupId) {
        this.position = 0;
        this.ownerId = ownerId;
        this.groupId = groupId;
        this.isFinished = false;
        this.groupPieces.add(this);

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void finish() {
        this.isFinished = true;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getGroupId() {
        return groupId;
    }

    // 추가
    public void grouping(Piece other) {
        // 이미 같은 그룹이면 무시
        if (this.groupId != 0 && this.groupId == other.groupId) return;

        // 1) 그룹 ID 결정
        int gid = (this.groupId != 0) ? this.groupId
                : (other.groupId != 0) ? other.groupId
                : NEXT_GID++;

        // 2) 모든 구성원 합집합
        Set<Piece> union = new HashSet<>();

        union.add(this);
        union.addAll(this.groupPieces);
        union.add(other);
        union.addAll(other.groupPieces);

        // 3) 모든 말에 동일 정보 세팅
        for (Piece p : union) {
            p.groupId = gid;
            p.groupPieces = new ArrayList<>(union);
        }
    }

    public List<Piece> getGroupedPieces() {
        return groupPieces;
    }

    public void pushPreviousPosition(int pos) {
        posStack.push(pos);
    }

    public int popPreviousPosition() {
        if (!posStack.isEmpty()) {
            return posStack.pop();
        }
        return -1;
    }
}
