package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Piece {
    private int position;
    private int ownerId;
    private boolean isFinished;
    private int groupId = 0;

    // 추가
    private Stack<Integer> posStack = new Stack<>();
    private List<Piece> groupPieces = new ArrayList<>();

    public Piece(int ownerId, int color, int groupId) {
        this.position = 0;
        this.ownerId = ownerId;
        this.groupId = groupId;
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
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getGroupId() {
        return groupId;
    }

    // 추가
    public void grouping(Piece otherPiece) {
        this.groupId = 1;
        otherPiece.groupId = 1;

        if (!this.groupPieces.contains(otherPiece)) {
            this.groupPieces.add(otherPiece);
        }

        if (!otherPiece.groupPieces.contains(this)) {
            otherPiece.groupPieces.add(this);
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
