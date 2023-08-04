import java.util.*;

public class MinimaxBot extends Bot {
	private static double INFSCORE = 100.0;
	private int strength;
	
	public MinimaxBot(int strength) {
		this.strength = strength;
	}
	
	public MinimaxBot() {
		strength = 2;
	}

	ArrayList<ArrayList<Move>> listOfTurns = new ArrayList<ArrayList<Move>>();
	ArrayList<Double> listOfScores = new ArrayList<Double>();
	private boolean originalTurn;

	// Generate moves for one turn for the Minimax Bot
	public ArrayList<Move> generateMoves(Board board) { // TODO: implement with minimax2() method
		ArrayList<Move> result = new ArrayList<Move>(); // List of moves the bot will make
		originalTurn = board.getPlayerTurn();
		minimax4(board, originalTurn, strength, -INFSCORE, INFSCORE);
		result = bestMaxTurn;
		for (int i = 0; i < result.size(); i++) {
			//System.out.println(result.get(i));
		}
		return result;
	} // GOOD FOR NOW

	ArrayList<Move> bestMaxTurn = new ArrayList<Move>();
	ArrayList<Move> bestMinTurn = new ArrayList<Move>();

	public double minimax4(Board board, boolean isPlayer, int currentDepth, double alpha, double beta) {
		// System.out.println("Minimax Called");
		if (board.isGameOver() || currentDepth == 0) {
			//System.out.println("depth = 0 reached");
			return board.scoreBoard(originalTurn);
		} else {
			moveSeries2.clear();
			availableTurns2.clear();
			findTurns2(board, isPlayer);
			if (isPlayer == originalTurn) {
				// System.out.println("Is Player");
				double currentMax = -INFSCORE;
				ArrayList<ArrayList<Move>> turns = (ArrayList<ArrayList<Move>>) availableTurns2.clone();
				ArrayList<Move> maxTurn = new ArrayList<Move>();
				for (ArrayList<Move> t : turns) {
					// System.out.println("Move Counter");
					// System.out.println(t);
					board.makeTurn(t);
					double score = minimax4(board, !isPlayer, currentDepth - 1, alpha, beta);
					if (currentMax < score) {
						currentMax = score;
						maxTurn = t; // since it's recursive, the last change of gloval variable will be th biggest
											// turn that we need
					}
					board.revertTurn(t);
					
					alpha = Math.max(alpha, score);
					if (beta <= alpha) break;
				}
				bestMaxTurn = maxTurn;
				return currentMax;
			} else {
				// System.out.println("Is Bot Player");
				double currentMin = INFSCORE;
				ArrayList<ArrayList<Move>> turns = (ArrayList<ArrayList<Move>>) availableTurns2.clone();
				for (ArrayList<Move> t : turns) {
					board.makeTurn(t);
					double score = minimax4(board, !isPlayer, currentDepth - 1, alpha, beta); // score from the recursive call of
																					// minimax
					if (currentMin > score) {
						currentMin = score;
						bestMinTurn = t;
					}
					board.revertTurn(t);
					
					beta = Math.min(beta, score);
					if (beta <= alpha) break;
				}
				return currentMin;
			}
		}
	} // GOOD FOR NOW


	ArrayList<ArrayList<Move>> availableTurns2 = new ArrayList<ArrayList<Move>>();
	ArrayList<Move> moveSeries2 = new ArrayList<Move>();

	public void findTurns2(Board board, boolean isPlayer) { // Modifies the ****FIX availableTurns class-bound list of
															// possible turns to make
		if (board.isGameOver() || board.getPlayerTurn() != isPlayer) {
			availableTurns2.add((ArrayList<Move>) moveSeries2.clone()); // adds the moveSeries (turn to the list of
																		// turns (availableTurns)
		} else {
			ArrayList<Move> availableMoves = board.validMoves();
			for (Move m : availableMoves) {
				// System.out.println("FindTurn Counter");
				board.addMove(m);
				board.executeMove(m);
				moveSeries2.add(m);
				findTurns2(board, isPlayer);
				moveSeries2.remove(moveSeries2.size() - 1);
				board.revertMove();
			}
		}
	}

}