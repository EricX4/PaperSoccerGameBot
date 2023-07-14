import java.util.ArrayList;
import java.util.Arrays;

public class Board {

	// Fields:-----
	private Point[][] board = new Point[37][25]; // define dimensions (x, y)
	private int numOfMoves;
	private int[] ballPosition = new int[2]; // x-coordinate is first element, y-coordinate is second element
	private int turn; // Which player's turn it is

	// Constructs Board Object as a 2D array: (x coordinate, y coordinate, moves on
	// compass rose)
	public Board(Point[][] board, int[] startingBallPosition) {
		this.board = board;
		for (int i = 0; i < 37; i++) {
			for (int j = 0; j < 25; j++) {
				this.board[i][j] = new Point(i, j);
			}
		}
		this.ballPosition = startingBallPosition;
	}

	// Getters and Setters:-----

	/**
	 * Returns current board position
	 * 
	 * @return
	 */
	public Point[][] getBoard() {
		return this.board;
	}

	/**
	 * Sets new board position data based on 3D array given
	 * 
	 * @param board
	 */
	public void setBoard(Point[][] board) {
		this.board = board;
	}

	/**
	 * Returns the number of moves available for the ball to move at a specific time
	 **/
	public int getNumOfMoves() {
		return numOfMoves;
	}

	public void setNumOfMoves(int numOfMoves) {
		this.numOfMoves = numOfMoves;
	}

	// Methods:-----

	/**
	 * Moves the currentBallPosition one spot to the left horizontally
	 */
	public void horizLeftMove() {
		this.ballPosition[0] -= 1;
	}

	/**
	 * Moves the currentBallPosition one spot to the left horizontally and one spot
	 * up simultaneously in one move
	 */
	public void topLeftMove() {
		this.ballPosition[0] -= 1;
		this.ballPosition[1] += 1;
	}

	public void upMove() {
		this.ballPosition[1] += 1;
	}

	public void topRightMove() {
		this.ballPosition[0] += 1;
		this.ballPosition[1] += 1;
	}

	public void horizRightMove() {
		this.ballPosition[0] += 1;
	}

	public void bottomMove() {
		this.ballPosition[1] -= 1;

	}

	public void bottomRightMove() {
		this.ballPosition[0] += 1;
		this.ballPosition[1] -= 1;
	}

	public void bottomLeftMove() {
		this.ballPosition[0] -= 1;
		this.ballPosition[1] -= 1;
	}

	// Returns true if bounce available, returns false otherwise
	public boolean checkBounce() {
		return false;

	}

	// Prints the board to the terminal in the form of dots and dashes
	public void printBoard() {
		for (int i = 0; i < 37; i++) {
			for (int j = 0; j < 25; j++) {
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}

	public void addConnection(int connection, int x, int y) {
		if (connection == 4) {
			this.board[x + 1][y].changeDisplay("|");
			this.board[x + 2][y].changeDisplay("|");
		} else if (connection == 1) {
			this.board[x - 1][y + 1].changeDisplay("/");
			this.board[x - 2][y + 2].changeDisplay("/");
		} else if (connection == 3) {
			this.board[x + 1][y + 1].changeDisplay("\\");
			this.board[x + 2][y + 2].changeDisplay("\\");
		} else if (connection == 2) {
			this.board[x][y + 1].changeDisplay("_");
			this.board[x][y + 2].changeDisplay("_");
		} else if (connection == 0) {
			this.board[x - 1][y].changeDisplay("|");
			this.board[x - 2][y].changeDisplay("|");
		}
	}

	/*
	 * Board win checker.
	 * 
	 * @param board : 3-d array of the board to be checked
	 * 
	 * @return Returns 0 if the game not won yet, 1 if player 1 won, 2 if player 2
	 * won
	 */

	// NEED TO CHANGE THIS
	public int checkWin() {
		if (this.ballPosition[0] == 1 || this.ballPosition[0] == 13) {
			if (this.ballPosition[1] == 4 || this.ballPosition[1] == 5 || this.ballPosition[1] == 6) {
				return turn;
			}
		}
		return 0;
	}

	// returns a number between -1 and 1, where -1 represents a optimal position for
	// player 1, and 1 represents a optimal position for player 2
	// temp - only scores based on y-coord of ball position
	public double scoreBoard() {
		ArrayList<Integer> bestMove = bestMove();
		return (((13 - (double) ballPosition[1]) / 13 * 2) - 1);
	}

	public ArrayList<Integer> bestMove() {
		ArrayList<Integer> moves = new ArrayList<Integer>();
		return moves;
	}

	public void flipBoard() {
		// TODO
	}

	public void lookahead() {
		// TODO
	}
}
