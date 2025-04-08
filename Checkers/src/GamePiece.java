public abstract class GamePiece extends GameComponent {
    protected int row, col;
    protected boolean isKing;
    protected int jumpCount;

    public enum Type {
        WHITE, BLACK
    }

    public GamePiece(int row, int col) {
        this.row = row;
        this.col = col;
        this.isKing = false;
        this.jumpCount = 0;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean isKing) {
        this.isKing = isKing;
    }

    public int getJumpCount() {
        return jumpCount;
    }

    public void incrementJumpCount() {
        this.jumpCount++;
    }

    public void resetJumpCount() {
        this.jumpCount = 0;
    }

    // Abstract method for piece type
    public abstract Type getType();
}
