package main;

import java.util.ArrayList;
import java.util.List;

abstract class Node {
    private int nodeID;
    private List<Piece> ownedPieces;

    // ui용 위치
    private int x;
    private int y;

    public Node(int positionId) {
        this.nodeID = nodeID;
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
}