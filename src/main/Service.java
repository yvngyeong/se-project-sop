package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.TestYut;

public class Service {
    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();

        System.out.print("플레이어 수: ");
        int playerNum = scanner.nextInt();
        game.setPlayerNum(playerNum);

        System.out.print("각 플레이어의 말 개수: ");
        int pieceNum = scanner.nextInt();
        game.setPieceNum(pieceNum * playerNum); // 전체 말 개수로 설정

        // 플레이어 리스트 생성
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= playerNum; i++) {
            players.add(new Player(i, pieceNum)); // i를 id로 받아서 Player 추가
        }
        game.setPlayers(players);

        // 윷 선택
        Yut yut = null;
        while (true) {
            System.out.print("윷 종류(테스트용: 1, 게임용: 2): ");
            int yutType = scanner.nextInt();
            if (yutType == 1) {
                yut = new TestYut();
                break;
            } else if (yutType == 2) {
                yut = new RandomYut();
                break;
            } else {
                System.out.println("잘못된 윷 종류입니다. 다시 입력해주세요.");
            }
        }
        game.setYut(yut);

        // 보드판 선택
        Board board = null;
        while (true) {
            System.out.print("보드판 종류(사각형: 4, 오각형: 5, 육각형: 6): ");
            int boardType = scanner.nextInt();
            if (boardType == 4) {
                board = new TetragonalBoard();
                break;
            } else if (boardType == 5) {
                board = new PentagonalBoard();
                break;
            } else if (boardType == 6) {
                board = new HexagonalBoard();
                break;
            } else {
                System.out.println("잘못된 보드판 종류입니다. 다시 입력해주세요.");
            }
        }
        game.setBoard(board);

        // 게임 시작
        System.out.println("게임을 시작합니다!");
        game.start();
    }

    public void end() {
        // 게임 종료 로직 (필요 시 추가)
    }
}
