package com.example.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Board {
    public List<Node> nodes;
    public Map<Integer, List<Integer>> edges;

    protected boolean isCatched = false;
    protected boolean isBackdo = false;

    protected void handleCaptureAndGroup(Piece myPiece, Node targetNode) {
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

    public abstract void movePosition(Piece piece, Integer yutValue);

    public abstract void createNodes(); // 각 판 마다 동그라미 개수에 따라 노드 만들기

    public abstract void createEdges(); // 각 판 마다 길 만들기

    public List<Node> getNodes() { return nodes; }

    public boolean isCatched() {return isCatched;}

    public HashMap<Integer, List<Integer>> getEdges() {
        return (HashMap<Integer, List<Integer>>) edges;
    }
}