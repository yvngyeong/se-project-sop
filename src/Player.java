import java.util.List;

public class Player {
    private int id;
    private List<Piece> pieces;

    public Player(int id){
        this.id = id;
    }

    public Piece selectPiece(){

    }

    public int getId() {
        return id;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public boolean checkWin(){

    }
}
