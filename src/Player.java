import java.util.List;

public class Player {
    private int id;
    private List<Piece> pieces;
    private List<Integer> yutResult;

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

    public List<Integer> getYutResult() {
        return yutResult;
    }

    public boolean checkWin(){

    }
}
