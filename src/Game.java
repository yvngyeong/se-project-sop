import java.util.List;
import java.util.Scanner;

public class Game {
    private List<Player> players;
    private Board board;
    private Yut yut;
    private int playerNum;
    private int pieceNum;

    public void start() {
        boolean gameOver = false;
        int currentPlayerIndex = 0;
        Scanner scanner = new Scanner(System.in);

        while (!gameOver) {
            Player currentPlayer = players.get(currentPlayerIndex);

            currentPlayer.throwYut(); //player의 필드 yutResult에 저장
            List<Integer> yutResult = currentPlayer.getYutResult();

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

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setYut(Yut yut) {
        this.yut = yut;
    }

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
    }
}
