public class connect4 {

	// board
	String board[][] = new String[6][7];

	// "-" indicates an empty board space

	// creating a board
	public String[][] createBoard() {
		// filling board with "-"
		for (int x = 0; x < board[0].length; x++) {
			for (int y = 0; y < board.length; y++) {
				board[y][x] = "-";
			}
		}

		// return board
		return board;
	}

	// placing piece on board
	public void setBoard(String board[][], int x, int y, boolean turn) {
		// p1 piece
		if (turn == false) {
			board[y][x] = "0";
		}
		// p2 piece
		else {
			board[y][x] = "1";
		}
	}

	// checking if move is valid
	public boolean validMove(String board[][], int x, int y) {

		// if the board doesn't have "-", means spot is taken
		if (!board[y][x].equals("-")) {
			System.out.println("\nPiece Already There!");
			return false;
		}

		// if outside these cords, it is out of bounds
		if (y > 5 || x > 6 || y < 0 || x < 0) {
			System.out.println("\nOut Of Bounds!");
			return false;
		}

		// if not the bottom row
		if (y != 5) {
			// if piece under is empty, the move is invalid because there has to be a piece
			// underneath to be a valid move
			if (board[y + 1][x].equals("-")) {
				System.out.println("\nInvalid Spot!");
				return false;
			}
		}

		// if passes all conditions, move is valid
		return true;
	}

	// checks if win happened
	public boolean checkWin(String[][] board, boolean turn) {

		// piece checking for win
		String p;

		// checking which piece for win
		if (turn == false) {
			p = "0";
		} else {
			p = "1";
		}

		// checks for win horizontally
		for (int x = 0; x < board[0].length - 3; x++) {
			for (int y = 0; y < board.length; y++) {
				if (board[y][x].equals(p) && board[y][x + 1].equals(p) && board[y][x + 2].equals(p)
						&& board[y][x + 3].equals(p)) {
					return true;
				}
			}
		}
		// checks for win vertically
		for (int x = 0; x < board[0].length; x++) {
			for (int y = 0; y < board.length - 3; y++) {
				if (board[y][x].equals(p) && board[y + 1][x].equals(p) && board[y + 2][x].equals(p)
						&& board[y + 3][x].equals(p)) {
					return true;
				}
			}
		}
		// checks for win diagonal (top left to bottom right)
		for (int x = 3; x < board[0].length; x++) {
			for (int y = 3; y < board.length; y++) {
				if (board[y][x].equals(p) && board[y - 1][x - 1].equals(p) && board[y - 2][x - 2].equals(p)
						&& board[y - 3][x - 3].equals(p))
					return true;
			}
		}

		// checks for win diagonal (top right to bottom left)
		for (int x = 0; x < board[0].length - 3; x++) {
			for (int y = 3; y < board.length; y++) {
				if (board[y][x].equals(p) && board[y - 1][x + 1].equals(p) && board[y - 2][x + 2].equals(p)
						&& board[y - 3][x + 3].equals(p))
					return true;
			}
		}
		return false;
	}

	// checks for draws
	public boolean checkDraw(String[][] board) {
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {

				// if "-" is found, that means there is still a valid move and game is not over
				if (board[y][x].equals("-")) {
					return false;
				}
			}
		}
		// if not "-" is found, that means all spots are taken up and game is a draws
		return true;
	}

	// prints board in console
	public void printBoard(String[][] board) {
		// goes row by row and prints what piece is in that spot
		for (int x = 0; x < board.length; x++) {
			System.out.println();
			for (int y = 0; y < board[0].length; y++) {
				System.out.print(board[x][y] + " ");
			}
		}

		// prints new line
		System.out.println();
	}

	// printing where player clicked in console
	public void printSpot(int x, int y) {

		// -1 means player clicked invalid spot
		if (x == -1 || y == -1) {
			System.out.println("\nPlayer Clicked Invalid Spot");
		}
		// player clicked valid spot
		else {
			System.out.println("\nPlayer Clicked: " + x + "," + y);
		}
	}
}
