public class BlackPiece extends GamePiece {
    public BlackPiece(int row, int col) {
        super(row, col);
    }

    @Override
    public Type getType() {
        return Type.BLACK;
    }

    @Override
    public String getDescription() {
        return "Black Piece at (" + row + ", " + col + ")";
    }
}
