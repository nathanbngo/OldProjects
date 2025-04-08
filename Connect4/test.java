public class test {

	// testing each check win method to check for all diagonals, horizontal, and
	// vertical wins
	public static void main(String[] args) {

		// creating board and setting to "-"
		String board[][] = new String[6][7];
		for (int x = 0; x < board[0].length; x++) {
			for (int y = 0; y < board.length; y++) {
				board[y][x] = "-";
			}
		}

		// diagonals (bottom left to top right)
		for (int y = 3; y < board.length; y++) {
			for (int x = 0; x < board[0].length - 3; x++) {

				board[y][x] = "0";
				board[y - 1][x + 1] = "0";
				board[y - 2][x + 2] = "0";
				board[y - 3][x + 3] = "0";

				// printing board
				print(board);

				// clear board
				clear(board);
				System.out.println();
			}
		}
		// diagonals (top left to bottom right)
		for (int y = 3; y < board.length; y++) {
			for (int x = 3; x < board[0].length; x++) {
				board[y][x] = "0";
				board[y - 1][x - 1] = "0";
				board[y - 2][x - 2] = "0";
				board[y - 3][x - 3] = "0";
				// printing board
				print(board);

				// clear board
				clear(board);
				System.out.println();
			}
		}

		// vertical
		for (int x = 0; x < board[0].length; x++) {
			for (int y = 0; y < board.length - 3; y++) {
				board[y][x] = "0";
				board[y + 1][x] = "0";
				board[y + 2][x] = "0";
				board[y + 3][x] = "0";

				// printing board
				print(board);

				// clear board
				clear(board);
				System.out.println();
			}
		}

		// horizontal
		for (int x = 0; x < board[0].length - 3; x++) {
			for (int y = 0; y < board.length; y++) {
				board[y][x] = "0";
				board[y][x + 1] = "0";
				board[y][x + 2] = "0";
				board[y][x + 3] = "0";

				// printing board
				print(board);

				// clear board
				clear(board);
				System.out.println();
			}
		}

	}

	// printing board
	public static void print(String[][] board) {
		for (int i = 0; i < board.length; i++) {
			System.out.println();
			for (int j = 0; j < board[0].length; j++) {
				System.out.print(board[i][j] + " ");
			}
		}
	}

	// clearing board
	public static void clear(String[][] board) {
		for (int k = 0; k < board[0].length; k++) {
			for (int l = 0; l < board.length; l++) {
				board[l][k] = "-";
			}
		}
	}

}
