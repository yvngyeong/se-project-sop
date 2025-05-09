package com.example.demo;

import view.PieceComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {
    private int id;
    private List<Piece> pieces;
    private boolean hasWon;

    public Player(int id, int pieceCount) {
        this.id = id;
        this.pieces = new ArrayList<>();
        this.hasWon = false;

        int color = (id - 1) % 4 + 1; // 플레이어 id에 따라 색상 1~4를 자동 배정

        for (int i = 0; i < pieceCount; i++) {
            pieces.add(new Piece(id)); // 정해진 color로 Piece 생성
        }
    }

    public Player(int id, List<Piece> pieces) {
        this.id = id;
        this.pieces = pieces;
    }

    public Player(int id) {
        this.id = id;
        this.pieces = new ArrayList<>();
    }


    public Piece selectPiece() {
        Scanner scanner = new Scanner(System.in);

        // 움직일 수 있는 말 목록 보여주기
        System.out.println("현재 이동 가능한 말 목록:");
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            if (!p.isFinished()) {
                System.out.println("[" + i + "] 위치: " + p.getPosition() + " (도착 안함)");
            }
        }

        int index = -1;
        while (true) {
            System.out.print("이동시킬 말의 번호를 입력하세요: ");
            try {
                index = Integer.parseInt(scanner.nextLine());
                if (index >= 0 && index < pieces.size() && !pieces.get(index).isFinished()) {
                    break;
                } else {
                    System.out.println("잘못된 선택입니다. 다시 입력해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }

        return pieces.get(index);
    }

    public boolean checkWin() {
        if (hasWon) {
            return true;
        }

        for (Piece p : pieces) {
            if (!p.isFinished()) {
                return false;
            }
        }

        hasWon = true;
        return true;
    }

    public int getId() {
        return id;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public List<Piece> getUnusedPieces(){
        List<Piece> unusedPieces = new ArrayList<>();
        for (Piece piece : getPieces()) {
            if(!piece.isFinished() && piece.getPosition()==0&& !piece.isJustArrived()){
                unusedPieces.add(piece);
            }
        }
        return  unusedPieces;
    }
}
