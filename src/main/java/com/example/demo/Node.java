package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    private int nodeID;
    private List<Piece> ownedPieces;

    public Node(int positionId) {
        this.nodeID = positionId;
        this.ownedPieces = new ArrayList<>();
    }

    public int getNodeID() {
        return nodeID;
    }

    public int checkOwner(Piece piece) {
        return piece.getOwnerId();
    }

    public List<Piece> getOwnedPieces() {
        return ownedPieces;
    }

    public void setOwnedPieces(List<Piece> ownedPieces) {
        this.ownedPieces = ownedPieces;
    }

    public void remove(Piece piece) // 노드에서 말 제거
    {
        ownedPieces.remove(piece);
    }

    public void add(Piece piece) // 노드에 말 추가
    {
        ownedPieces.add(piece);
    }

    public void clearPieces()  //게임 재시작할때 초기화
    {
        ownedPieces.clear();
    }

}