import java.util.List;

public class Game {
    private List<Player> players;
    private Board board;
    private Yut yut;
    private int playerNum;
    private int pieceNum;

    public void start() {

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
