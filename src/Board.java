import java.util.List;

abstract class Board {
    private List<Node> nodes;

    public abstract void movePosition(Piece piece, int yutValue);
}
