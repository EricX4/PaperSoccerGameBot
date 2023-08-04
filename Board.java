import java.util.ArrayList;
import java.util.Arrays;
import java.awt.*;

public class Board {
	// Animation-related fields:----
	protected double a_isPlayerTurn;
	protected double a_moveIndex;
	// ST stands for start time
	protected double ST_game;
	protected double ST_menu;
	protected double ST_gameEnd;

	// Fields:----
	protected Point[][] board;
	protected int[] ballPos;
	protected boolean isPlayerTurn;
	protected boolean tempPlayerTurn;
	// the current list of moves
	protected ArrayList<Move> moves;
	protected boolean playerWon;
	protected boolean gameOver;
	protected boolean cannotMove;
	
	protected boolean initPlayerTurn;
	
	// potential spot to insert field regarding board score
	// the current streak of moves in a single turn
	protected int currStreak;
	protected Bot bot;
	protected Bot player;
	protected Bot[] players;

	public Board(boolean isPlayerTurn) {
		initPlayerTurn = isPlayerTurn;
		
		board = new Point[9][13];

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 13; j++) {
				board[i][j] = new Point();
				if (i == 0 || j == 1 || i == 9 - 1 || j == 13 - 2) {
					board[i][j].setCanBounce(true);
				}
			}
		}

		// the expressions are for readability; they're optimized anyways by the
		// compiler so no need to evaluate them
		int centerX = (9 - 1) / 2;
		int centerY = (13 - 1) / 2;

		// manually editing canBounce for the points near the goals
		board[centerX][1].setCanBounce(false);
		board[centerX][13 - 2].setCanBounce(false);

		// set the position of the ball to the center
		ballPos = new int[2];
		ballPos[0] = centerX;
		ballPos[1] = centerY;

		this.isPlayerTurn = isPlayerTurn;
		this.a_isPlayerTurn = isPlayerTurn ? 1 : 0;
		tempPlayerTurn = isPlayerTurn;

		moves = new ArrayList<Move>();

		ST_game = 0;
		ST_menu = 0;
		ST_gameEnd = -1;

		a_moveIndex = 0;
		currStreak = 0;

		gameOver = false;
		cannotMove = false;

		player = new HumanPlayer(); // HumanPlayer normally
		bot = new ShortestPathBot(); // TESTING THE MINIMAX BOT --> BE SURE TO CHANGE BACK TO SHORTEST PATH BOT!!!

		players = new Bot[2];
		players[0] = player;
		players[1] = bot;
	}

	public void reset() {
		board = new Point[9][13];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 13; j++) {
				board[i][j] = new Point();
				if (i == 0 || j == 1 || i == 9 - 1 || j == 13 - 2) {
					board[i][j].setCanBounce(true);
				}
			}
		}
		// the expressions are for readability; they're optimized anyways by the
		// compiler so no need to evaluate them
		int centerX = (9 - 1) / 2;
		int centerY = (13 - 1) / 2;

		// manually editing canBounce for the points near the goals
		board[centerX][1].setCanBounce(false);
		board[centerX][13 - 2].setCanBounce(false);

		ballPos[0] = centerX;
		ballPos[1] = centerY;

		this.isPlayerTurn = initPlayerTurn;
		this.a_isPlayerTurn = isPlayerTurn ? 1 : 0;
		tempPlayerTurn = isPlayerTurn;

		moves.clear();

		ST_game = 0;
		ST_menu = 0;
		ST_gameEnd = -1;

		a_moveIndex = 0;
		currStreak = 0;

		gameOver = false;
		cannotMove = false;
	}
	
	public double[] toVector(double xWeight, double yWeight, double turnWeight) {
		double[] res = new double[KNNBot.keySz];
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 13; y++) {
				for (int i = 0; i < 8; i++) {
					res[8 * (y * 9 + x) + KNNBot.inGameMoveToPlayOkMove[i]] = board[x][y].connections[i] ? 1 : 0;
				}
			}
		}
		res[KNNBot.keySz - 3] = ballPos[1] * yWeight;
		res[KNNBot.keySz - 2] = ballPos[0] * xWeight;
		res[KNNBot.keySz - 1] = (isPlayerTurn ? 2 : 2) * turnWeight;
		return res;
	}

	// Returns the score of the board; this is based on the distance of the ball to
	// the players goal --> higher score: bot winning, lower score: player winning
	// <------ THIS CAN CHANGE DEPENDING ON BOARD-SCORING METHODS
	public double scoreBoard(boolean origTurn) {
		int pos = ballPos[1];
		double score = pos / 12.0;
		ArrayList<Move> moves = this.validMoves(ballPos[0], ballPos[1]);
		double bounces = .5;
		for (int i = 0; i < moves.size(); i++) {
			makeMove(moves.get(i));
			if (board[ballPos[0]][ballPos[1]].getCanBounce()) {
				if (ballPos[1] < pos) {
					if (isPlayerTurn != origTurn) {
						bounces += .4 * (1 - bounces);
					} else {
						bounces += 0.1 * (1 - bounces);
					}
				} else if (ballPos[1] == pos) {
					bounces += .2 * (1 - bounces);
				} else {
					if (isPlayerTurn != origTurn) {
						bounces += 0.1 * (1 - bounces);
					}
					bounces += 0.4 * (1 - bounces);
				}
			}
			revertMove();
		}
		score = (score + bounces) / 2.0;
		if (cannotMove) {			
			score = origTurn == isPlayerTurn ? -98 : 98;
		}
		if (ballPos[1] == 0) {
			score = -99;
		}
		if (ballPos[1] == 12) {
			score = 99;
		}
		return score;
	}

	// return a list of valid moves
	public ArrayList<Move> validMoves(int x, int y) {
		ArrayList<Move> result = new ArrayList<Move>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (isValidMove(x, y, i, j)) {
					result.add(new Move(x + i, y + j, x, y, isPlayerTurn, AnimationsConfig.MOVE_DELAY_BOT));
				}
			}
		}

		return result;
	}

	public ArrayList<Move> validMoves() {
		return validMoves(ballPos[0], ballPos[1]);
	}

	public boolean getPlayerTurn() {
		return isPlayerTurn;
	}

	public void setPlayerTurn(boolean turnState) {
		isPlayerTurn = turnState;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public boolean getPlayerWon() {
		return playerWon;
	}
	
	public boolean getCannotMove() {
		return cannotMove;
	}

	// checks if the bot can still move after its last move.
	public boolean canMoveBot() {
		return tempPlayerTurn == isPlayerTurn && !gameOver;
	}

	// Check whether a move is valid
	public boolean isValidMove(int x, int y, int dx, int dy) {
		// check if there's already an edge in the direction the ball wants to move in
		if (Math.abs(dx) > 1 || Math.abs(dy) > 1)
			return false;
		if (board[x][y].getConnection(dx, dy))
			return false;
		// -----
		// Invalidating any illegal moves that go along the vertical edges of the board:
		if ((x == 0 && dx <= 0) || (x == 8 && dx >= 0)) {
			return false;
		}

		// Invalidating any illegal moves that go along the horizontal edges of the
		// board:
		if (y == 1) {
			if ((x == 0 || x == 1) || (x == 2)) {
				if (dy <= 0) {
					return false; // check with dy
				}
			} else if ((x == 8 || x == 7) || (x == 6)) {
				if (dy <= 0) {
					return false; // check with dy
				}
			}
		} else if (y == 11) {
			if ((x == 0 || x == 1) || (x == 2)) {
				if (dy >= 0) {
					return false; // check with dy
				}
			} else if ((x == 8 || x == 7) || (x == 6)) {
				if (dy >= 0) {
					return false; // check with dy
				}
			}
		}

		// Invalidate moves that are in goal area
		if (y == 0 || y == 12) {
			return false;
		}

		// Check special cases with goal corner posts
		if (y == 1 && x == 3) {
			if (dx == -1 && dy == 0) {
				return false;
			} else if (dx == -1 && dy == -1) {
				return false;
			} else if (dx == 0 && dy == -1) {
				return false;
			}
		} else if (y == 1 && x == 5) {
			if (dx == 1 && dy == 0) {
				return false;
			} else if (dx == 0 && dy == -1) {
				return false;
			} else if (dx == 1 && dy == -1) {
				return false;
			}
		} else if (y == 11 && x == 3) {
			if (dx == -1 && dy == 0) {
				return false;
			} else if (dx == -1 && dy == 1) {
				return false;
			} else if (dx == 0 && dy == 1) {
				return false;
			}
		} else if (y == 11 && x == 5) {
			if (dx == 1 && dy == 0) {
				return false;
			} else if (dx == 0 && dy == 1) {
				return false;
			} else if (dx == 1 && dy == 1) {
				return false;
			}
		}
		return true;
	}

	// returns 0 if a position is not in the goal, 1 if a position is in the
	// player's goal, and 2 if a position is in the bot's goal
	public int checkGoal(int x, int y) {

		if ((x == 3) || (x == 4) || (x == 5)) {
			if (y == 0) {
				return 2;
			} else if (y == 12) {
				return 1;
			}
		}
		return 0;
	}

	public boolean isHumanPlayer(Bot b) {
		return b instanceof HumanPlayer;
	}

	public void click(int gridX, int gridY) {
		if (isHumanPlayer(players[isPlayerTurn ? 0 : 1])) {
			addMove(gridX - ballPos[0], gridY - ballPos[1], 0.);
		}
	}

	// returns whether or not the ball actually moved or not
	// adds a move to the current list of moves; if there is a delay, there will be
	// a delay before the move is executed
	public boolean addMove(int dx, int dy, double delay) {
		int x = ballPos[0] + dx;
		int y = ballPos[1] + dy;
		if (!isValidMove(ballPos[0], ballPos[1], dx, dy))
			return false;

		Move m = new Move(x, y, ballPos[0], ballPos[1], isPlayerTurn, delay);
		moves.add(m);
		if (delay == 0.) {
			executeMove(m);
		}

		return true;
	}

	public void addMove(Move m) {
		moves.add(m);
	}

	// actually execute the move
	public void executeMove(Move m) {
		m.setExecuted(true);
		int x = m.getX();
		int y = m.getY();
		int px = m.getPX();
		int py = m.getPY();
		int goal = checkGoal(x, y);

		if (validMoves(x, y).size() == 1 && goal == 0) {
			playerWon = !isPlayerTurn;
			gameOver = true;
			cannotMove = true;
			//System.out.println("cannot move anymore");
			ballPos[0] = x;
			ballPos[1] = y;
			return;
		}

		if (goal == 1 || goal == 2) {
			// TODO - game should end and display winner, then take user back to main
			if (goal == 2) {
				//System.out.println("Blue Player Wins");
			} else if (goal == 1) {
				//System.out.println("Red Player Wins");
			}
			playerWon = goal == 2;
			gameOver = true;
			ST_gameEnd = Main.time;
			ballPos[0] = x;
			ballPos[1] = y;
			return;
		}

		boolean botGenerateMoveFlag = false;

		if (!board[x][y].getCanBounce() && goal == 0) {
			isPlayerTurn = !isPlayerTurn;
			if (!isHumanPlayer(players[isPlayerTurn ? 0 : 1])) {
				botGenerateMoveFlag = true;
			}
			currStreak = 0;
		} else {
			currStreak++;
		}

		board[px][py].setConnection(x - px, y - py, true);
		board[x][y].setConnection(px - x, py - y, true);

		ballPos[0] = x;
		ballPos[1] = y;

		if (botGenerateMoveFlag && m.getDelay() == 0) {
			tempPlayerTurn = isPlayerTurn;
			if (isPlayerTurn) {
				flipBoard();
				addMoves(players[isPlayerTurn ? 0 : 1].generateMoves(this));
				flipBoard();
			} else {
				addMoves(players[isPlayerTurn ? 0 : 1].generateMoves(this));
			}
		}

		// TODO - check if game ends due to the fact that the player cannot move
	}

	public void makeMove(Move m) {
		moves.add(m);
		executeMove(m);
	}

	public void makeTurn(ArrayList<Move> turn) {
		for (int i = 0; i < turn.size(); i++) {
			addMove(turn.get(i));
			executeMove(turn.get(i));
		}
	}

	public void revertTurn(ArrayList<Move> turn) {
		for (int i = 0; i < turn.size(); i++) {
			revertMove();
		}
	}

	public void addMoves(ArrayList<Move> moves) {
		//System.out.println("start move for player " + (isPlayerTurn ? 1 : 2));
		for (int i = 0; i < moves.size(); i++) {
			Move m = moves.get(i);
			addMove(m);
			
			if (isPlayerTurn) {
				m.flip();
			}
			//System.out.print(m);
			if (isPlayerTurn) {
				m.flip();
			}
			
			if (m.getDelay() == 0) {
				executeMove(m);
			}
		}
	}

	public ArrayList<Move> getMoves() {
		return moves;
	}

	public void revertMove() {
		Move m = moves.get(moves.size() - 1);
		isPlayerTurn = m.getIsPlayerTurn();

		m.setExecuted(false);
		int x = m.getX();
		int y = m.getY();
		int px = m.getPX();
		int py = m.getPY();
		board[px][py].setConnection(x - px, y - py, false);
		board[x][y].setConnection(px - x, py - y, false);

		ballPos[0] = px;
		ballPos[1] = py;

		currStreak = 0;

		moves.remove(moves.size() - 1);

		gameOver = false;
		cannotMove = false;
		ST_gameEnd = -1;
	}

	public void flipBoard() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < (13 - 1) / 2; j++) {
				Point temp = board[i][j];
				board[i][j] = board[i][13 - j - 1];
				board[i][13 - j - 1] = temp;
			}
		}
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 13; j++) {
				board[i][j].flip();
			}
		}
		for (int i = 0; i < moves.size(); i++) {
			moves.get(i).flip();
		}
		ballPos[1] = 13 - ballPos[1] - 1;
	}

	public int[] getBallPos() {
		return ballPos;
	}

	public void renderBoard(Graphics2D g2d, double delta, int mouseX, int mouseY) {
		if (moves.size() == 0) {
			if (isPlayerTurn) {
				if (!isHumanPlayer(player)) {
					flipBoard();
					ArrayList<Move> initMoves = player.generateMoves(this);
					initMoves.get(0).setDelay(1200);
					addMoves(initMoves);
					flipBoard();
				}
			} else {
				ArrayList<Move> initMoves = bot.generateMoves(this);
				initMoves.get(0).setDelay(1200);
				addMoves(initMoves);
			}
		}

		double currST = ST_game;
		int gridCenterX = (9 - 1) / 2;
		int gridCenterY = (13 - 1) / 2;

		int w = GraphicsConfig.WINDOW_WIDTH;
		int h = GraphicsConfig.WINDOW_HEIGHT;
		int centerX = w / 2;
		int centerY = h / 2;
		int ss = GraphicsConfig.SQUARE_SIZE;

		int gridMouseX = (int) Math.round((mouseX - centerX) / (double) ss) + gridCenterX;
		int gridMouseY = (int) Math.round((mouseY - centerY) / (double) ss) + gridCenterY;
		if (isValidMove(ballPos[0], ballPos[1], gridMouseX - ballPos[0], gridMouseY - ballPos[1])
				&& isHumanPlayer(players[isPlayerTurn ? 0 : 1])) {
			board[gridMouseX][gridMouseY].setIsHover(true);
		}

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 13; j++) {
				board[i][j].updateAnimation(delta);
			}
		}

		double p;

		
		// drawing the grid:----

		Color gridColor = new Color(224, 224, 224);
		g2d.setColor(gridColor);
		g2d.setStroke(new BasicStroke(GraphicsConfig.GRID_LINE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		for (int i = 0; i < 9; i++) {
			p = AnimationFunctions.clamp((Main.time - currST) / AnimationsConfig.LINE_SWEEP_DURATION, 0, 1);
			p = AnimationFunctions.easeInOutCubic(p);
			g2d.drawLine((i - gridCenterX) * ss, h - centerY, (i - gridCenterX) * ss,
					(int) Math.round((1 - p) * h) - centerY);
			currST += AnimationsConfig.GRID_STAGGER;
		}
		currST = ST_game;
		for (int i = 0; i < 13; i++) {
			p = AnimationFunctions.clamp((Main.time - currST) / AnimationsConfig.LINE_SWEEP_DURATION, 0, 1);
			p = AnimationFunctions.easeInOutCubic(p);
			g2d.drawLine(-centerX, (i - gridCenterY) * ss, (int) Math.round((p) * w) - centerX, (i - gridCenterY) * ss);
			currST += AnimationsConfig.GRID_STAGGER;
		}

		// drawing the field border:----
		currST = ST_game + AnimationsConfig.DELAY_FIELD_FADEIN;
		p = AnimationFunctions.clamp((Main.time - currST) / AnimationsConfig.FIELD_FADEIN_DURATION, 0, 1);
		p = AnimationFunctions.easeInOutCubic(p);
		Color fieldBorderColor = new Color(48, 48, 48, (int) Math.round(255 * p));
		g2d.setColor(fieldBorderColor);
		g2d.setStroke(
				new BasicStroke(GraphicsConfig.FIELD_BORDER_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		int[] fieldXPts = { (0 - gridCenterX) * ss, (-1) * ss, (-1) * ss, (1) * ss, (1) * ss,
				(9 - 1 - gridCenterX) * ss, (9 - 1 - gridCenterX) * ss, (1) * ss, (1) * ss, (-1) * ss, (-1) * ss,
				(0 - gridCenterX) * ss };
		int[] fieldYPts = { (1 - gridCenterY) * ss, (1 - gridCenterY) * ss, (-gridCenterY) * ss, (-gridCenterY) * ss,
				(1 - gridCenterY) * ss, (1 - gridCenterY) * ss, (13 - 2 - gridCenterY) * ss,
				(13 - 2 - gridCenterY) * ss, (13 - 1 - gridCenterY) * ss, (13 - 1 - gridCenterY) * ss,
				(13 - 2 - gridCenterY) * ss, (13 - 2 - gridCenterY) * ss };
		g2d.drawPolygon(fieldXPts, fieldYPts, 12);

		
		// drawing the moves:----

		int lastMoveIndex = -1;
		for (int i = 0; i < moves.size(); i++) {
			Move m = moves.get(i);
			m.decrementDelay(delta);
			if (m.getDelay() == 0. && !m.getExecuted()) {
				executeMove(m);
				break;
			} else if (!m.getExecuted()) {
				break;
			}
			lastMoveIndex = i;
		}

		a_moveIndex = AnimationFunctions.lerp(Math.pow(2., -AnimationsConfig.MOVE_RUBBER_BAND_RATE * delta / 1000.),
				lastMoveIndex + 1, a_moveIndex);
		a_isPlayerTurn = AnimationFunctions.lerp(Math.pow(2., -AnimationsConfig.TURN_CHANGE_RATE * delta / 1000.),
				isPlayerTurn ? 1 : 0, a_isPlayerTurn);

		g2d.setColor(new Color(0, 0, 0));

		Color blue = new Color(66, 135, 245);
		Color red = new Color(240, 13, 48);

		int a_ballPosX = (ballPos[0] - gridCenterX) * ss;
		int a_ballPosY = (ballPos[1] - gridCenterY) * ss;
		if (a_moveIndex == 0) {
			a_ballPosX = 0;
			a_ballPosY = 0;
		}

		// draw the moves that were actually executed
		for (int i = 0; i <= lastMoveIndex; i++) {
			Move m = moves.get(i);
			Move lastMove = moves.get(lastMoveIndex);
			g2d.setColor(m.getIsPlayerTurn() ? blue : red);
			double trailWidth = AnimationFunctions.lerp(m.getRecency(), GraphicsConfig.INACTIVE_TRAIL_WIDTH,
					GraphicsConfig.ACTIVE_TRAIL_WIDTH);
			g2d.setStroke(new BasicStroke((float) trailWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			int px = (m.getPX() - gridCenterX) * ss;
			int py = (m.getPY() - gridCenterY) * ss;
			int x = (m.getX() - gridCenterX) * ss;
			int y = (m.getY() - gridCenterY) * ss;
			if (i > a_moveIndex - 1) {
				double amt = (double) i - (a_moveIndex - 1);
				if (amt <= 1) {
					a_ballPosX = (int) Math.round(AnimationFunctions.lerp(amt, x, px));
					a_ballPosY = (int) Math.round(AnimationFunctions.lerp(amt, y, py));
					// draw in a partial line
					g2d.drawLine(px, py, a_ballPosX, a_ballPosY);
				}
			} else {
				// draw in a line
				g2d.drawLine(px, py, x, y);
			}
			if (i < lastMoveIndex + 1 - currStreak) {
				m.decrementRecency(AnimationsConfig.MOVE_RECENCY_REACTION_RATE * delta / 1000.);
			}
		}

		// drawing in hover info:----
		Color pointHoverColor = new Color(224, 224, 224);
		g2d.setColor(pointHoverColor);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 13; j++) {
				p = board[i][j].getHoverState();
				if (p > 0) {
					int radius = (int) (GraphicsConfig.POINT_HOVER_RADIUS * p);
					int posX = (i - gridCenterX) * ss;
					int posY = (j - gridCenterY) * ss;
					g2d.fillOval(posX - radius, posY - radius, radius * 2, radius * 2);
				}
			}
		}

		// drawing the ball:----
		p = a_isPlayerTurn;
		Color ballColor = new Color((int) AnimationFunctions.lerp(p, 240, 66),
				(int) AnimationFunctions.lerp(p, 14, 135), (int) AnimationFunctions.lerp(p, 48, 245));

		currST = ST_game + AnimationsConfig.DELAY_BALL_FADEIN;
		p = AnimationFunctions.clamp((Main.time - currST) / AnimationsConfig.BALL_FADEIN_DURATION, 0, 1);
		p = AnimationFunctions.linear(p);
		int ballRadius = (int) Math.round(GraphicsConfig.BALL_RADIUS * p);

		g2d.setColor(ballColor);
		g2d.fillOval(a_ballPosX - ballRadius, a_ballPosY - ballRadius, ballRadius * 2, ballRadius * 2);

		// drawing the turn:----
		currST = ST_game + AnimationsConfig.DELAY_UI_FADEIN;
		p = AnimationFunctions.clamp((Main.time - currST) / AnimationsConfig.UI_FADEIN_DURATION, 0, 1);
		p = AnimationFunctions.linear(p);
		int blueHeight = (int) (a_isPlayerTurn * GraphicsConfig.TURN_BAR_HEIGHT * p);
		int redHeight = (int) ((1 - a_isPlayerTurn) * GraphicsConfig.TURN_BAR_HEIGHT * p);
		g2d.setColor(blue);
		g2d.fillRect((-gridCenterX) * ss, h - blueHeight - centerY, (9 - 1) * ss, blueHeight);
		g2d.setColor(red);
		g2d.fillRect((-gridCenterX) * ss, -centerY, (9 - 1) * ss, redHeight);

		// drawing the result of the game:----

	}
}