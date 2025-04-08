import java.awt.*;

import javax.swing.JOptionPane;
import tapplet.TApplet;

public class board extends TApplet {

	// variables
	// creating new connect4 to get methods
	connect4 c = new connect4();

	// creating board
	static String[][] board;

	// coordinate of where player clicked
	int x, y;

	// tracks player turn, false = p1, true = p2
	boolean turn = false;

	// audio clips
	AudioClip win;
	AudioClip button;
	AudioClip background;

	// images
	Image p1;
	Image p2;
	Image gameOver;
	Image boardImage;

	// counting each players wins
	int p1Wins = 0;
	int p2Wins = 0;
	int draws = 0;

	// media tracker
	MediaTracker tracker;

	// main
	public static void main(String[] args) {
		// creating new board
		new board();

		// new connect4
		connect4 c = new connect4();

		// creating new board
		board = c.createBoard();
	}

	// init
	public void init() {

		// setting screen
		setSize(992, 856);

		Graphics offG = getScreenBuffer();

		// setting tracker
		tracker = new MediaTracker(this);

		// players pick color
		chooseColor();

		// setting gameover and boardimage
		gameOver = getImage(getCodeBase(), "gameover.png");
		boardImage = getImage(getCodeBase(), "board.png");

		// adding image to tracker
		tracker.addImage(gameOver, 0);
		tracker.addImage(boardImage, 0);
		tracker.addImage(p1, 0);
		tracker.addImage(p2, 0);

		while (tracker.checkAll(true) != true) {
		}

		// drawing board
		offG.drawImage(boardImage, 0, 0, this);

		// repating
		repaint();

		// setting sounds
		win = this.getAudioClip(this.getCodeBase(), "win.wav");
		button = this.getAudioClip(this.getCodeBase(), "button.wav");
		background = this.getAudioClip(this.getCodeBase(), "background.wav");

		// P1 goes first in the first game
		JOptionPane.showMessageDialog(null, "P1 goes first");

		// play background music
		background.play();
	}

	// when click
	public void mouseDown(int x, int y) {

		// initial values

		// where the coordinates relate back to board
		// ex. mx = 6, my = 5 is the bottom right corner spot of board

		// initial spot, and if player didn't click valid spot, value stays at -1
		int mx = -1;
		int my = -1;

		// where the piece needs to be drawn to fit on the board
		int px = 0;
		int py = 0;

		// any click within these x cords, relate back to spot on board

		// x=0
		if ((x >= 56 && x <= 120)) {
			px = 40;
			mx = 0;
		}
		// x=1
		else if ((x >= 192 && x <= 256)) {
			px = 176;
			mx = 1;
		}
		// x=2
		else if ((x >= 328 && x <= 392)) {
			px = 312;
			mx = 2;
		}
		// x=3
		else if ((x >= 464 && x <= 528)) {
			px = 448;
			mx = 3;
		}
		// x=4
		else if ((x >= 600 && x <= 664)) {
			px = 584;
			mx = 4;
		}
		// x=5
		else if ((x >= 736 && x <= 800)) {
			px = 720;
			mx = 5;
		}
		// x=6
		else if ((x >= 872 && x <= 936)) {
			px = 856;
			mx = 6;
		}

		// y=0
		if ((y >= 56 && y <= 120)) {
			py = 40;
			my = 0;
		}
		// y=1
		else if ((y >= 192 && y <= 256)) {
			py = 176;
			my = 1;
		}
		// y=2
		else if ((y >= 328 && y <= 392)) {
			py = 312;
			my = 2;
		}
		// y=3
		else if ((y >= 464 && y <= 528)) {
			py = 448;
			my = 3;
		}
		// y=4
		else if ((y >= 600 && y <= 664)) {
			py = 584;
			my = 4;
		}
		// y=5
		else if ((y >= 736 && y <= 800)) {
			py = 720;
			my = 5;
		}

		// printing what spot is clicked in console
		c.printSpot(mx, my);

		// if player clicked valid spot
		if (mx != -1 && my != -1) {

			// if valid to make move
			if (makeMove(mx, my)) {

				// if turn is false draws p1
				if (turn == false) {
					drawP1(px, py);
				}

				// else draw p2
				else {
					drawP2(px, py);
				}

				// checks win
				if (c.checkWin(board, turn)) {

					// game over by win
					gameOver(false);
				}

				// checls draw
				else if (c.checkDraw(board)) {

					// game over by draw
					gameOver(true);
				}
				turn = !turn;
			}
		}

		// repaint board
		repaint();

	}

	// draws p2 piece on board
	public void drawP2(int x, int y) {
		Graphics offG = getScreenBuffer();
		offG.drawImage(p2, x, y, this);
		repaint();

		// plays button sound
		button.play();
	}

	// draws p1 piece on board
	public void drawP1(int x, int y) {
		Graphics offG = getScreenBuffer();
		offG.drawImage(p1, x, y, this);
		repaint();

		// play button sound
		button.play();
	}

	// game over
	public void gameOver(boolean x) {

		// winner and loser variables
		String winner;
		String loser;

		// play win sound
		win.play();

		// if p1 wins
		if (turn == false) {
			winner = "P1";
			loser = "P2";

			// addds to win
			p1Wins++;
		}

		// if p2 wins
		else {
			winner = "P2";
			loser = "P1";

			// adds to win
			p2Wins++;
		}

		// different options when game ends
		Object[] options = { "Play Again", "Pick New Colors", "Leaderboard", "Exit" };

		// text to add to panel
		String text;

		// if game ended by win
		if (x == false) {

			// text set to winner text
			text = winner + " Wins!";
		}
		// else game is over by draw
		else {

			// text is set to draw
			text = "Draw!";

			// draws++
			draws++;
		}

		// loops until game is exited or new game is pressed
		while (true) {
			// creates panel with different options after game has ended
			int buttonOption = JOptionPane.showOptionDialog(null, text, "Game Over!", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, options, null);

			// if exit is clicked
			if (buttonOption == 3) {

				// closes program
				System.exit(0);
			}

			// if leaderboard is clicked
			else if (buttonOption == 2) {

				// show message with amount of wins each player has and amount of draws
				JOptionPane.showMessageDialog(null,
						"P1 wins: " + p1Wins + "\nP2 wins: " + p2Wins + "\nDraws: " + draws);
			}

			// if pick new color picked
			else if (buttonOption == 1) {

				// calls choosecolor method
				chooseColor();

				// removes previous p1 and p2 image pieces from tracker
				tracker.removeImage(p1);
				tracker.removeImage(p2);

				// adds new p1 and p2 piece to tracker
				tracker.addImage(p1, 0);
				tracker.addImage(p2, 0);

				while (tracker.checkAll(true) != true) {
				}

			}

			// else new game is picked
			else {

				// creates new game with new board, clears screen, then draws new board
				board = c.createBoard();
				Graphics g = getScreenBuffer();
				g.clearRect(0, 0, 992, 856);
				g.drawImage(boardImage, 0, 0, this);

				// says which player goes first, loser goes first in next game
				JOptionPane.showMessageDialog(null, loser + " goes first");
				repaint();

				// break loop to start new game
				break;

			}
		}
	}

	// make move, checking if move is valid, if valid then can make move, if not,
	// can't make move
	public boolean makeMove(int x, int y) {

		// if move is not valid
		if (!c.validMove(board, x, y)) {
			// can not make move, so returns false
			return false;
		}
		// move is valid
		else {

			// sets spot to board
			c.setBoard(board, x, y, turn);

			// prints board in console
			c.printBoard(board);

			// can make move, return true
			return true;
		}
	}

	// letting players choose color
	public void chooseColor() {
		// different color choices
		Object[] colors = { "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink" };

		// creates panel with different color options, and collects result
		int colorOption = JOptionPane.showOptionDialog(null, "Player 1 pick a color", "Pick a Color P1",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, colors, null);

		// setting different piece color based on what button user clicked
		if (colorOption == 0) {
			p1 = getImage(getCodeBase(), "red.png");
			System.out.println("P1 picked red");
		}

		else if (colorOption == 1) {
			p1 = getImage(getCodeBase(), "orange.png");
			System.out.println("P1 picked orange");
		}

		else if (colorOption == 2) {
			p1 = getImage(getCodeBase(), "yellow.png");
			System.out.println("P1 picked yellow");
		}

		else if (colorOption == 3) {
			p1 = getImage(getCodeBase(), "green.png");
			System.out.println("P1 picked green");
		}

		else if (colorOption == 4) {
			p1 = getImage(getCodeBase(), "blue.png");
			System.out.println("P1 picked blue");
		}

		else if (colorOption == 5) {
			p1 = getImage(getCodeBase(), "purple.png");
			System.out.println("P1 picked purple");
		}

		else {
			p1 = getImage(getCodeBase(), "pink.png");
			System.out.println("P1 picked pink");
		}

		// settings both color options the same
		int colorOption2 = colorOption;

		// while both color options are the same (so players can't pick the same color,
		// keeps looping until different color is picked)
		while (colorOption2 == colorOption) {

			// collecting result from which color button user clicked
			colorOption2 = JOptionPane.showOptionDialog(null, "Player 2 pick a color", "Pick a Color P2",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, colors, null);

			// if color is same as P1 color
			if (colorOption2 == colorOption) {

				// shows message that color is used already
				JOptionPane.showMessageDialog(null, "That color is already taken!");
			}
		}

		// setting different piece color based on what button user clicked
		if (colorOption2 == 0) {
			p2 = getImage(getCodeBase(), "red.png");
			System.out.println("P2 picked red");
		}

		else if (colorOption2 == 1) {
			p2 = getImage(getCodeBase(), "orange.png");
			System.out.println("P2 picked orange");
		}

		else if (colorOption2 == 2) {
			p2 = getImage(getCodeBase(), "yellow.png");
			System.out.println("P2 picked yellow");
		}

		else if (colorOption2 == 3) {
			p2 = getImage(getCodeBase(), "green.png");
			System.out.println("P2 picked green");
		}

		else if (colorOption2 == 4) {
			p2 = getImage(getCodeBase(), "blue.png");
			System.out.println("P2 picked blue");
		}

		else if (colorOption2 == 5) {
			p2 = getImage(getCodeBase(), "purple.png");
			System.out.println("P2 picked purple");
		}

		else {
			p2 = getImage(getCodeBase(), "pink.png");
			System.out.println("P2 picked pink");
		}
	}

}
