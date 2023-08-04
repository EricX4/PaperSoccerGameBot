import java.util.ArrayList;
import java.awt.*;

class BatchBoard extends Board {
	private int numGames;

	public BatchBoard(boolean isPlayerTurn) {
		super(isPlayerTurn);

		super.player = new MCTSBot();
		super.bot = new MinimaxBot();

		super.players[0] = super.player;
		super.players[1] = super.bot;

		numGames = 20;
		boolean doFlip = true; //flip the second half of games so that the other player goes first
			//setting this to true allows a more balanced analysis of two bots

		int playerWins = 0;
		int botWins = 0;
		for (int i = 0; i < numGames; i++) {
			System.out.println("playing game no. " + (i + 1));
			super.isPlayerTurn = isPlayerTurn;
			if (i >= numGames/2 && doFlip) {
				super.isPlayerTurn = !isPlayerTurn;
			}
			while (!super.gameOver) {
				tempPlayerTurn = super.isPlayerTurn;
				if (super.isPlayerTurn) {
					flipBoard();
					super.addMoves(super.player.generateMoves(this));
					flipBoard();
				} else {
					super.addMoves(super.bot.generateMoves(this));
				}
			}
			if (cannotMove) {
				if (playerWon) playerWins++;
				else botWins++;
			} else {
				if (playerWon ^ tempPlayerTurn) playerWins++;
				else botWins++;
			}
			
			//System.out.println(super.moves);
			
			super.reset();
		}
		System.out.println("Player 1 (" + super.player.getClass().getSimpleName() + ") Wins: " + playerWins);
		System.out.println("Player 2 (" + super.bot.getClass().getSimpleName() + ") Wins: " + botWins);
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
	}

	// return a list of valid moves
	public ArrayList<Move> validMoves(int x, int y) {
		ArrayList<Move> result = new ArrayList<Move>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (super.isValidMove(x, y, i, j)) {
					result.add(new Move(x + i, y + j, x, y, super.isPlayerTurn, 0));
				}
			}
		}

		return result;
	}

	public void renderBoard(Graphics2D g2d, double delta, int mouseX, int mouseY) {
		if (false) {
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
}