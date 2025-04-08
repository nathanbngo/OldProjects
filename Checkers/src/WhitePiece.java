public class WhitePiece extends GamePiece {
    public WhitePiece(int row, int col) {
        super(row, col);
    }

    @Override
    public Type getType() {
        return Type.WHITE;
    }

    @Override
    public String getDescription() {
        return "White Piece at (" + row + ", " + col + ")";
    }
}
