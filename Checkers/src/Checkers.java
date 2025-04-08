import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Checkers extends JFrame {
    private static final int TILE_SIZE = 60;  // Size of each square
    private static final int BOARD_SIZE = 8;  // 8x8 Checkers Board

    private GamePiece[][] board;  // Updated to use GamePiece
    private GamePiece selectedPiece;  // Updated to use GamePiece
    private boolean isWhiteTurn = true;  // White pieces start
    private ArrayList<Move> moveHistory;
    private ArrayList<Move> originalMoveHistory; // Preserve original order

    private Image boardImage;
    private Image white;
    private Image black;
    private Image whiteKing;
    private Image blackKing;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Checkers().showMainMenu());
    }

    public Checkers() {
        board = new GamePiece[BOARD_SIZE][BOARD_SIZE];  // Updated to GamePiece
        moveHistory = new ArrayList<>();
        originalMoveHistory = new ArrayList<>(); // Initialize original move history
        loadImages();
    }


    private void recordMove(GamePiece piece, int newRow, int newCol) {
        Move move = new Move(piece.getRow(), piece.getCol(), newRow, newCol, piece.getType() == GamePiece.Type.WHITE);
        moveHistory.add(move);
        originalMoveHistory.add(move); // Record in the original history
    }



    private void viewSortedMoves() {
        if (moveHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No moves have been made yet.", "Move History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Sort moves by starting row (or any custom criteria)
        Collections.sort(moveHistory, Comparator.comparingInt(Move::getStartRow));

        // Build the message for display
        StringBuilder message = new StringBuilder("Move History (sorted by starting row):\n");
        for (Move move : moveHistory) {
            message.append(move).append("\n");
        }

        // Show the move history in a dialog box
        JOptionPane.showMessageDialog(this, message.toString(), "Sorted Moves", JOptionPane.INFORMATION_MESSAGE);
    }

    
    private void showMainMenu() {
        JFrame menuFrame = new JFrame("Checkers - Main Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 400);
        menuFrame.setLayout(new GridLayout(4, 1)); // Adjusted for 5 options

        JLabel title = new JLabel("Welcome to Checkers", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        menuFrame.add(title);

        JButton playButton = new JButton("Play Game");
        playButton.setFont(new Font("Arial", Font.PLAIN, 16));
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuFrame.dispose();
                startGame();
            }
        });
        menuFrame.add(playButton);

        JButton loadButton = new JButton("Load Game");
        loadButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menuFrame.dispose();
                loadGame();
            }
        });
        menuFrame.add(loadButton);

        JButton rulesButton = new JButton("Rules");
        rulesButton.setFont(new Font("Arial", Font.PLAIN, 16));
        rulesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRules();
            }
        });
        menuFrame.add(rulesButton);

        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }


    private void showRules() {
        String rules = """
                Checkers Rules:
                1. Players take turns moving their pieces diagonally.
                2. You can capture an opponent's piece by jumping over it.
                3. Pieces that reach the opponent's end become kings.
                4. The game ends when one player has no moves left.
                """;

        JOptionPane.showMessageDialog(this, rules, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
    }

    private void startGame() {
        startGame(false); // Default: New game
    }

    private void startGame(boolean fromLoad) {
        if (!fromLoad) {
            initializeBoard(); // Only initialize for a new game
        }

        setTitle("Checkers Game");
        setLayout(new BorderLayout()); // Use BorderLayout for layout management

        // Calculate the size of the game window
        Insets insets = getInsets();
        int boardWidth = BOARD_SIZE * TILE_SIZE;
        int boardHeight = BOARD_SIZE * TILE_SIZE;
        int buttonHeight = 50; // Height for the buttons
        int windowWidth = boardWidth + insets.left + insets.right + 15;
        int windowHeight = boardHeight + buttonHeight + insets.top + insets.bottom + 35;

        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add Mouse Listener for interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = getCord(e.getY());
                int col = getCord(e.getX());
                handleClick(row, col);
            }
        });

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3)); // Three buttons side by side

        // Save Game Button
        JButton saveButton = new JButton("Save Game");
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveGame();
                int result = JOptionPane.showConfirmDialog(
                    Checkers.this,
                    "Game saved successfully! Would you like to return to the main menu?",
                    "Save Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    dispose(); // Close the game window
                    SwingUtilities.invokeLater(() -> new Checkers().showMainMenu());
                }
            }
        });

        // Exit to Main Menu Button
        JButton exitButton = new JButton("Exit to Main Menu");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    Checkers.this,
                    "Are you sure you want to exit to the main menu? Unsaved progress will be lost.",
                    "Exit to Main Menu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    dispose(); // Close the game window
                    SwingUtilities.invokeLater(() -> new Checkers().showMainMenu());
                }
            }
        });

        // View Moves Button
        JButton viewMovesButton = new JButton("View Moves");
        viewMovesButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewMovesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMoveHistory();
            }
        });

        // Add buttons to the panel
        buttonPanel.add(saveButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(viewMovesButton);

        // Add components to the layout
        add(buttonPanel, BorderLayout.SOUTH); // Add buttons to the bottom

        // Ensure the board and UI are visible and repaint the game
        setVisible(true);
        revalidate(); // Ensure layout updates correctly
        repaint();    // Ensure pieces are rendered
    }

    private void showMoveHistory() {
        if (moveHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No moves have been made yet!", "Move History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a new frame to show moves
        JFrame moveHistoryFrame = new JFrame("Move History");
        moveHistoryFrame.setSize(400, 400);
        moveHistoryFrame.setLayout(new BorderLayout());

        // Text area to display moves
        JTextArea moveTextArea = new JTextArea();
        moveTextArea.setEditable(false);
        updateMoveHistoryDisplay(moveTextArea);

        JScrollPane scrollPane = new JScrollPane(moveTextArea);
        moveHistoryFrame.add(scrollPane, BorderLayout.CENTER);

        // Panel for sorting buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        // Button to sort by player
        JButton sortByPlayerButton = new JButton("Sort by Player");
        sortByPlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collections.sort(moveHistory, Comparator.comparing(Move::isWhiteMove)); // Sort by player
                updateMoveHistoryDisplay(moveTextArea); // Refresh the display
            }
        });

        // Button to reset sorting
        JButton resetSortButton = new JButton("Reset Sort");
        resetSortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveHistory.clear();
                moveHistory.addAll(originalMoveHistory); // Restore original order
                updateMoveHistoryDisplay(moveTextArea); // Refresh the display
            }
        });

        // Add buttons to the panel
        buttonPanel.add(sortByPlayerButton);
        buttonPanel.add(resetSortButton);

        moveHistoryFrame.add(buttonPanel, BorderLayout.SOUTH);
        moveHistoryFrame.setLocationRelativeTo(this);
        moveHistoryFrame.setVisible(true);
    }

    // Helper method to update the move history display
    private void updateMoveHistoryDisplay(JTextArea moveTextArea) {
        StringBuilder movesDisplay = new StringBuilder("Move History:\n");
        for (Move move : moveHistory) {
            movesDisplay.append(move).append("\n");
        }
        moveTextArea.setText(movesDisplay.toString());
    }




    private int getCord(int n) {
    	int cord = -1;
    	int temp = 0;
    	while(temp < n) {
    		temp += TILE_SIZE;
    		cord += 1;
    	}
    	if (cord >= 8){
    		cord = 7;
    	}
    	return cord;
    }
    
    private void loadImages() {
        try {
            // Load images
            boardImage = ImageIO.read(new File("boardImage.png"));
            white = ImageIO.read(new File("white.png"));
            black = ImageIO.read(new File("black.png"));
            whiteKing = ImageIO.read(new File("whiteKing.png"));
            blackKing = ImageIO.read(new File("blackKing.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeBoard() {
        board = new GamePiece[BOARD_SIZE][BOARD_SIZE]; // Ensure board is initialized
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 != 0) { // Only place pieces on dark squares
                    if (row < 3) {
                        board[row][col] = new WhitePiece(row, col);
                    } else if (row > 4) {
                        board[row][col] = new BlackPiece(row, col);
                    }
                } else {
                    board[row][col] = null; // Explicitly clear other squares
                }
            }
        }
    }



    private void initializeBoardFromLog(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            if (scanner.hasNextLine()) {
                isWhiteTurn = Boolean.parseBoolean(scanner.nextLine()); // Load the current turn
            }

            // Clear the board
            board = new GamePiece[BOARD_SIZE][BOARD_SIZE];

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                GamePiece.Type type = GamePiece.Type.valueOf(parts[2]);
                boolean isKing = Boolean.parseBoolean(parts[3]);

                // Place the piece on the board
                GamePiece piece = (type == GamePiece.Type.WHITE) ? new WhitePiece(row, col) : new BlackPiece(row, col);
                piece.setKing(isKing);
                board[row][col] = piece;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading the game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void handleClick(int row, int col) {
    	if (checkWin(true)) {
            endGame();
        }
        GamePiece clickedPiece = board[row][col];
        if (selectedPiece == null && clickedPiece != null && 
            (isWhiteTurn && clickedPiece.getType() == GamePiece.Type.WHITE || 
            !isWhiteTurn && clickedPiece.getType() == GamePiece.Type.BLACK)) {
            // Select the piece
            selectedPiece = clickedPiece;
            repaint();
        } else if (selectedPiece != null) {
            if (isValidMove(selectedPiece, row, col)) {
                movePiece(selectedPiece, row, col);
                if ((selectedPiece.getType() == GamePiece.Type.WHITE && selectedPiece.getRow() == BOARD_SIZE - 1) || 
                    (selectedPiece.getType() == GamePiece.Type.BLACK && selectedPiece.getRow() == 0)) {
                    selectedPiece.setKing(true);  // Set as king
                }
                isWhiteTurn = !isWhiteTurn;  // Switch turns
            } else if (isValidJump(selectedPiece, row, col)) {
                jumpPiece(selectedPiece, row, col);
                repaint();
                if ((selectedPiece.getType() == GamePiece.Type.WHITE && selectedPiece.getRow() == BOARD_SIZE - 1) || 
                    (selectedPiece.getType() == GamePiece.Type.BLACK && selectedPiece.getRow() == 0)) {
                    selectedPiece.setKing(true);  // Set as king
                }
                // Check if the piece can double jump
                if (canDoubleJump(selectedPiece)) {
                    if (selectedPiece.getJumpCount() == 0) {
                        selectedPiece.incrementJumpCount();
                        return;
                    } else {
                        int result = JOptionPane.showConfirmDialog(this, 
                                "Do you want to continue double jumping?", 
                                "Double Jump", 
                                JOptionPane.YES_NO_OPTION);

                        if (result == JOptionPane.YES_OPTION) {
                            // Continue jumping
                            return;
                        } else {
                            // No more jumping, switch turns
                            isWhiteTurn = !isWhiteTurn;
                            selectedPiece.resetJumpCount(); // Reset jump count for the piece
                        }
                    }
                } else {
                    // No double jump, end the turn
                    isWhiteTurn = !isWhiteTurn;
                    selectedPiece.resetJumpCount();  // Reset jump count for the piece
                }
            }
            selectedPiece = null; // Deselect the piece
            repaint();
            // Check if the game is over
        	if (checkWin(false)) {
                endGame();
            }
        }
    }

    private boolean checkWin(boolean check) {
        // Check if the current player has no valid moves
    	if (check) {
	        boolean hasValidMove = false;
	        // Check for valid moves for the current player
	        for (int row = 0; row < BOARD_SIZE; row++) {
	            for (int col = 0; col < BOARD_SIZE; col++) {
	                GamePiece piece = board[row][col];
	                if(isWhiteTurn) {
	                    if (piece != null && piece.getType() == GamePiece.Type.WHITE) {
	                        // Check if the piece has any valid move or jump
	                        if (hasValidMoves(piece)) {
	                            hasValidMove = true;
	                            break;
	                        }
	                    }
	                }
	                else {
	                    if (piece != null && piece.getType() == GamePiece.Type.BLACK) {
	                        // Check if the piece has any valid move or jump
	                        if (hasValidMoves(piece)) {
	                            hasValidMove = true;
	                            break;
	                        }
	                    }
	                }
	            }
	        }
	        // If no valid moves, the opponent wins
	        if (!hasValidMove) {
	        	isWhiteTurn=!isWhiteTurn;
	            return true;
	        }
    	}
        // Check if the opponent has any pieces left
        boolean opponentHasPieces = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                GamePiece piece = board[row][col];
                if (isWhiteTurn) {
                	if (piece != null && piece.getType() != GamePiece.Type.WHITE) {
                        opponentHasPieces = true;
                        break;
                    }
                }
                else {
                	if (piece != null && piece.getType() != GamePiece.Type.BLACK) {
                        opponentHasPieces = true;
                        break;
                    }
                }
            }
        }

        // If the opponent has no pieces, the current player wins
        if (!opponentHasPieces) {
        	
            return true;
        }

        return false;
    }

    private boolean hasValidMoves(GamePiece piece) {
        for (int rowOffset = -1; rowOffset <= 1; rowOffset += 2) {
            for (int colOffset = -1; colOffset <= 1; colOffset += 2) {
                int newRow = piece.getRow() + rowOffset;
                int newCol = piece.getCol() + colOffset;

                if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE) {
                    if (isValidMove(piece, newRow, newCol)) {
                        return true;
                    }
                }

                newRow = piece.getRow() + rowOffset * 2;
                newCol = piece.getCol() + colOffset * 2;

                if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE) {
                    if (isValidJump(piece, newRow, newCol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private void endGame() {
        String winner = isWhiteTurn ? "White" : "Black";
        int result = JOptionPane.showOptionDialog(this,
                winner + " wins! Would you like to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Play Again", "Exit"},
                "Play Again");

        if (result == JOptionPane.YES_OPTION) {
            dispose(); // Close the current game window
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new Checkers().showMainMenu(); // Restart by showing the main menu
                }
            });
        } else {
            System.exit(0); // Exit the application
        }
    }

    private boolean isValidMove(GamePiece piece, int row, int col) {
        int rowDiff = Math.abs(piece.getRow() - row);
        int colDiff = Math.abs(piece.getCol() - col);

        if (rowDiff == 1 && colDiff == 1 && board[row][col] == null) {
            if (piece.isKing()) {
                return true;
            }
            if (piece.getType() == GamePiece.Type.WHITE && row > piece.getRow()) {
                return true;
            }
            if (piece.getType() == GamePiece.Type.BLACK && row < piece.getRow()) {
                return true;
            }
        }
        return false;
    }




    private boolean isValidJump(GamePiece piece, int row, int col) {
        int rowDiff = Math.abs(piece.getRow() - row);
        int colDiff = Math.abs(piece.getCol() - col);
        int midRow = (piece.getRow() + row) / 2;
        int midCol = (piece.getCol() + col) / 2;

        if (rowDiff == 2 && colDiff == 2 && board[row][col] == null && board[midRow][midCol] != null) {
            GamePiece midPiece = board[midRow][midCol];

            if (midPiece.getType() != piece.getType()) {
                if (piece.isKing()) {
                    return true;
                }
                if (piece.getType() == GamePiece.Type.WHITE && row > piece.getRow()) {
                    return true;
                }
                if (piece.getType() == GamePiece.Type.BLACK && row < piece.getRow()) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean canDoubleJump(GamePiece selectedPiece) {
        // Check if the piece has any valid double jump options.
        for (int rowOffset = -2; rowOffset <= 2; rowOffset += 4) {
            for (int colOffset = -2; colOffset <= 2; colOffset += 4) {
                int row = selectedPiece.getRow() + rowOffset;
                int col = selectedPiece.getCol() + colOffset;

                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    if (isValidJump(selectedPiece, row, col)) {
                        return true;  // Double jump is possible
                    }
                }
            }
        }
        return false;
    }

    private void movePiece(GamePiece piece, int row, int col) {
        recordMove(piece, row, col);
        board[row][col] = piece;
        board[piece.getRow()][piece.getCol()] = null;
        piece.setRow(row);
        piece.setCol(col);
    }

    private void jumpPiece(GamePiece piece, int row, int col) {
        int midRow = (piece.getRow() + row) / 2;
        int midCol = (piece.getCol() + col) / 2;

        recordMove(piece, row, col);
        board[row][col] = piece;
        board[midRow][midCol] = null;
        board[piece.getRow()][piece.getCol()] = null;
        piece.setRow(row);
        piece.setCol(col);
        piece.incrementJumpCount(); // Increment jump count
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Get window insets (for top, bottom, left, right borders)
        Insets insets = getInsets();
        int xOffset = insets.left;
        int yOffset = insets.top;

        // Draw the board image
        g.drawImage(boardImage, xOffset, yOffset, BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE, this);

        // Draw the pieces
        drawPieces(g, xOffset, yOffset);
    }

    private void drawPieces(Graphics g, int xOffset, int yOffset) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                GamePiece piece = board[row][col];
                if (piece != null) {
                    if (selectedPiece != null && piece == selectedPiece) {
                        g.setColor(new Color(255, 215, 0));  // Yellow for selected piece
                        g.fillRect(col * TILE_SIZE + xOffset, row * TILE_SIZE + yOffset, TILE_SIZE + 1, TILE_SIZE + 1);
                    }

                    int xPos = col * TILE_SIZE + (TILE_SIZE - 32) / 2 + xOffset;
                    int yPos = row * TILE_SIZE + (TILE_SIZE - 32) / 2 + yOffset;

                    Image pieceImage = (piece.getType() == GamePiece.Type.WHITE)
                            ? (piece.isKing() ? whiteKing : white)
                            : (piece.isKing() ? blackKing : black);

                    g.drawImage(pieceImage, xPos, yPos, 32, 32, this);
                }
            }
        }
    }

    private void saveGame() {
        // Create a directory for storing game logs
        File logDir = new File("checkers_logs");
        if (!logDir.exists()) {
            logDir.mkdir(); // Create the directory if it doesn't exist
        }

        // Generate a unique file name using the current date and time
        String fileName = String.format("checkers_logs/%s.txt",
                java.time.LocalDateTime.now().toString().replace(":", "-").replace("T", "_"));

        try (PrintWriter writer = new PrintWriter(new File(fileName))) {
            writer.println(isWhiteTurn); // Save current turn
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    GamePiece piece = board[row][col];
                    if (piece != null) {
                        writer.printf("%d %d %s %b\n", row, col, piece.getType(), piece.isKing());
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Game saved successfully as:\n" + fileName,
                    "Save Game", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving the game: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser(new File("checkers_logs"));
        fileChooser.setDialogTitle("Select a Game Log to Load");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            initializeBoardFromLog(selectedFile.getPath()); // Load board state from the selected file
            startGame(true); // Start the game with the loaded state
        }
    }

    class Piece {
        private int row, col;
        private Type type;
        private boolean isKing;
        private int jumpCount; // Track the number of jumps made by this piece

        public Piece(int row, int col, Type type) {
            this.row = row;
            this.col = col;
            this.type = type;
            this.isKing = false;
            this.jumpCount = 0;  // Initialize with no jumps
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

        public Type getType() {
            return type;
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
            this.jumpCount++; // Increment jump count
        }

        public void resetJumpCount() {
            this.jumpCount = 0; // Reset the jump count after turn
        }

        public enum Type {
            WHITE, BLACK
        }
    }
    class Move {
        private int startRow, startCol, endRow, endCol;
        private boolean isWhiteMove;

        public Move(int startRow, int startCol, int endRow, int endCol, boolean isWhiteMove) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
            this.isWhiteMove = isWhiteMove;
        }

        public int getStartRow() {
            return startRow;
        }

        public boolean isWhiteMove() {
            return isWhiteMove;
        }

        @Override
        public String toString() {
            String player = isWhiteMove ? "White" : "Black";
            return String.format("%s: From (%d, %d) to (%d, %d)", player, startRow, startCol, endRow, endCol);
        }
    }

}