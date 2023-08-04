import java.util.*;

public class MCTSBot extends Bot {
	private double C;
	private static double INF = 1e6;
	
	public MCTSBot() {
		C = .1;
	}
	
	public MCTSBot(double C) {
		this.C = C;
	}
	
	public ArrayList<Move> generateMoves(Board board) {
		ArrayList<Move> ret = new ArrayList<Move>();
		boolean origTurn = board.getPlayerTurn();
		MCTNode root = MCT(board, 100, origTurn);
		return recoverTurn(root, board, origTurn);
	}
	
	//returns the root of a monte carlo tree search
	public MCTNode MCT(Board board, int numIter, boolean origTurn) {
		MCTNode root = new MCTNode(board.validMoves(), null, origTurn);
		for (int i = 0; i < numIter; i++) {
			descend(root, board, origTurn, 10);
		}
		return root;
	}
	
	public double descend(MCTNode n, Board board, boolean origTurn, int numSim) {
		if (n.endpt) {
			double delta = getDelta(board, origTurn);
			//System.out.println(delta);
			return numSim * delta;
		}
		int mi = selectChild(n, board.getPlayerTurn() == origTurn);
		board.makeMove(n.edges.get(mi));
		double ret = 0;
		if (n.children.get(mi) == null) {
			MCTNode c = new MCTNode(board.validMoves(), n, board.getPlayerTurn());
			n.children.set(mi, c);
			if (board.isGameOver()) {
				c.endpt = true;
				ret = descend(c, board, origTurn, numSim);
			} else {
				for (int i = 0; i < numSim; i++)
					ret += rollout(board, origTurn);
			}
			c.w += ret;
			c.n += numSim;
		} else {
			ret = descend(n.children.get(mi), board, origTurn, numSim);
		}
		board.revertMove();
		n.w += ret;
		n.n += numSim;
		return ret;
	}
	
	public double getDelta(Board board, boolean origTurn) {
		if (board.getCannotMove()) {
			return board.getPlayerTurn() == origTurn ? 0 : .1;
		}
		return !board.getPlayerWon() ? 1 : 0;
	}
	
	public int selectChild(MCTNode n, boolean maximize) {
		int bi = 0;
		double bestUCB = UCB(n.children.get(0), maximize);
		for (int i = 1; i < n.children.size(); i++) {
			double ucb = UCB(n.children.get(i), maximize);
			if (ucb > bestUCB) {
				bestUCB = ucb;
				bi = i;
			}
		}
		//System.out.println(bestUCB);
		return bi;
	}
	
	public int kNNMove(ArrayList<Move> moves, Board board) {
		return (int)(Math.random() * moves.size());
	}
	
	public double rollout(Board board, boolean origTurn) {
		if (board.isGameOver()) {
			double delta = getDelta(board, origTurn);
			return delta;
		}
		ArrayList<Move> moves = board.validMoves();
		int mi = kNNMove(moves, board);
		board.makeMove(moves.get(mi));
		double delta = rollout(board, origTurn);
		board.revertMove();
		return delta;
	}
	
	public ArrayList<Move> recoverTurn(MCTNode n, Board board, boolean origTurn) {
		ArrayList<Move> turn = new ArrayList<Move>();
		while (n != null && !n.endpt && n.turn == origTurn) {
			int bi = 0;
			for (int i = 1; i < n.children.size(); i++) {
				MCTNode c = n.children.get(i);
				MCTNode best = n.children.get(bi);
				if (best == null && c != null) {
					bi = i;
				} else if (c != null && c.w * best.n > best.w * c.n) {
					bi = i;
				}
			}
			Move m = n.edges.get(bi);
			board.makeMove(m);
			turn.add(m);
			n = n.children.get(bi);
		}
		//if the turn is still not over, just use the kNNMove heuristic
		while (!board.isGameOver() && board.getPlayerTurn() == origTurn) {
			ArrayList<Move> moves = board.validMoves();
			int mi = kNNMove(moves, board);
			Move m = moves.get(mi);
			board.makeMove(m);
			turn.add(m);
		}
		board.revertTurn(turn);
		return turn;
	}
	
	public double UCB(MCTNode n, boolean maximize) {
		if (n == null) return INF;
		if (n.n == 0) return INF;
		double wins = n.w;
		if (!maximize) wins = n.n - wins;
		return wins / n.n + C * Math.sqrt(2 * Math.log(n.parent.n) / n.n);
	}
}
