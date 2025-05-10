package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Piece {
    private int position;
    private int ownerId;
    private boolean isFinished;
    private static int groupCounter = 1; // 그룹 ID 자동 생성기 (공유)
    private int groupId = 0;             // 이 말의 그룹 ID (인스턴스별)
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
        if (this.groupId == 0 && otherPiece.groupId == 0) {
            // 둘 다 새 그룹
            this.groupId = groupCounter++;
            otherPiece.groupId = this.groupId;
        } else if (this.groupId == 0) {
            // this만 그룹 없음 → other에 맞춤
            this.groupId = otherPiece.groupId;
        } else if (otherPiece.groupId == 0) {
            // other만 그룹 없음 → this에 맞춤
            otherPiece.groupId = this.groupId;
        } else {
            // 둘 다 이미 그룹 있음 → 더 작은 groupId로 통일 (선택 사항)
            int unifiedId = Math.min(this.groupId, otherPiece.groupId);
            this.groupId = unifiedId;
            otherPiece.groupId = unifiedId;
        }

        // 병합: 서로의 그룹 멤버 리스트를 합침
        for (Piece p : otherPiece.groupPieces) {
            if (!this.groupPieces.contains(p)) {
                this.groupPieces.add(p);
            }
        }
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
        for (Piece p : new ArrayList<>(groupPieces)) {
            p.groupId = 0;
            p.groupPieces.clear();
        }
        groupId = 0;
        groupPieces.clear();
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





