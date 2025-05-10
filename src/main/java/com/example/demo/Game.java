package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private List<Player> players;
    private Board board;
    private Yut yut;
    private int playerNum;
    private int pieceNum;

    // 생성자 추가
    public Game(int playerNum, int pieceNum, Yut yut, Board board) {
        this.playerNum = playerNum;
        this.pieceNum = pieceNum;
        this.yut = yut;
        this.board = board;
        this.players = new ArrayList<>();

        // 플레이어 생성
        // Game.java
        for (int i = 1; i <= playerNum; i++) {
            Player player = new Player(i, pieceNum);
            for (int j = 0; j < pieceNum; j++) {
                Piece piece = new Piece(i);
                player.addPiece(piece);     // 이 인스턴스만 사용해야 함
            }
            players.add(player);
        }

    }

    public void start() {
        boolean gameOver = false;
        int currentPlayerIndex = 0;
        Scanner scanner = new Scanner(System.in);

        while (!gameOver) {
            Player currentPlayer = players.get(currentPlayerIndex);

            List<Integer> yutResult = new ArrayList<>();
            while (true) {
                Integer yutValue = yut.getResult();
                yutResult.add(yutValue);
                if (yutValue == -1 || yutValue == 1 || yutValue == 2 || yutValue == 3) {
                    break;
                }
            }

            while (!yutResult.isEmpty()) {
                System.out.println("남은 윷 결과: " + yutResult);
                System.out.print("사용할 윷 값(도:1, 개:2, 걸:3, 윷:4, 모:5, 빽도: -1): ");
                int selectedYut = scanner.nextInt();

                yutResult.remove(Integer.valueOf(selectedYut));

                Piece selectedPiece = currentPlayer.selectPiece();
                board.movePosition(selectedPiece, selectedYut);
            }

            if (currentPlayer.checkWin()) {
                System.out.println("축하합니다! 플레이어 " + currentPlayer.getId() + "번 님이 승리했습니다!");
                gameOver = true;
            } else {
                currentPlayerIndex = (currentPlayerIndex + 1) % playerNum;
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Yut getYut() {
        return yut;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getPieceNum() {
        return pieceNum;
    }
}
