package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Piece {
    private int position;
    private int ownerId;
    private boolean isFinished;
    private int groupId = 0;
    private boolean justArrived = false; // ✅ 도착 표시용 필드 추가

    // 추가
    private Stack<Integer> posStack = new Stack<>();
    private List<Piece> groupPieces = new ArrayList<>();
    private boolean isWaitingForFinish = false;

    public Piece(int ownerId ) {
        this.position = 0;
        this.ownerId = ownerId;
        this.groupId = 0;
        this.isFinished = false;

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
        this.clearPreviousPositions();    // 이동 경로 제거
        this.clearGroup();
    }

    public void unfinish() {this.isFinished = false;}

    public int getOwnerId() {
        return ownerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public boolean isWaitingForFinish() {
        return isWaitingForFinish;
    }

    public void setWaitingForFinish(boolean waitingForFinish) {
        this.isWaitingForFinish = waitingForFinish;
    }

    // 추가
    public void grouping(Piece otherPiece) {
        this.groupId = 1;
        otherPiece.groupId = 1;

        System.out.println("✅ 그룹핑 → this.hash=" + this.hashCode() + ", other.hash=" + otherPiece.hashCode());

        if (!this.groupPieces.contains(otherPiece)) {
            this.groupPieces.add(otherPiece);
        }

        if (!otherPiece.groupPieces.contains(this)) {
            otherPiece.groupPieces.add(this);
        }

        System.out.println("   ↪️ 그룹 크기: " + this.groupPieces.size());
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

    public void clearPreviousPositions() {
        posStack.clear();
    }

    public void clearGroup() {
        groupPieces.clear();
        groupId = 0;
    }

    public int getPreviousPosition() {
        if (!posStack.isEmpty()) {
            return posStack.peek(); // 또는 직접 Stack 확인
        }
        return -1;
    }
    public boolean isJustArrived() {
        return justArrived;
    }

    public void setJustArrived(boolean justArrived) {
        this.justArrived = justArrived;
    }
}





