import java.io.*;
import java.util.*;
import smile.neighbor.LSH;
import smile.neighbor.Neighbor;

public class MCTSKNNBot extends Bot {
	public static int keySz = 9 * 13 * 8 + 2 + 1;
	
	private double C;
	private static double INF = 1e6;
	
	private LSH<Integer> lsh;
	private int k;
	private double xWeight;
	private double yWeight;
	private double turnWeight;
	
	public static int[] playOkMoveToInGameMove = {3, 5, 6, 7, 4, 2, 1, 0};
	public static int[] inGameMoveToPlayOkMove = {7, 6, 5, 0, 4, 1, 2, 3};
	
	public MCTSKNNBot() {
		k = 2;
		xWeight = 20.0;
		yWeight = 20.0;
		turnWeight = 1000000.;
		init();
		C = .1;
	}
	
	public MCTSKNNBot(double C) {
		k = 1;
		xWeight = 20.0;
		yWeight = 20.0;
		turnWeight = 1000000.;
		init();
		this.C = C;
	}
	
	public void init() {
		//int n = 54757;
		int n = 5000;
		double[][] keys = new double[n][keySz];
		Integer[] vals = new Integer[n];
		
		int ct = 0;
		try {
			File f = new File("MCTS-KNN_Data.xlsx.csv");
			BufferedReader br = new BufferedReader(new FileReader(f));
	    	String line;
			while (ct < n && (line = br.readLine()) != null) {
			    String[] r = line.split("\"");
			    String[] config = r[1].split(",");
			    int value = Integer.parseInt(r[2].split(",")[1]);
			    
			    for (int i = 0; i < keySz; i++) {
			    	if (i >= keySz - 3 && config.length == keySz - 3) {
			    		/*if (i == keySz - 3) {
				    		keys[ct][i] = 4;
				    	} else if (i == keySz - 2) {
				    		keys[ct][i] = 6;
				    	} else if (i == keySz - 1) {
				    		keys[ct][i] = 1;
				    	}*/
			    	} else {
			    		keys[ct][i] = Integer.parseInt(config[i].trim());
			    	}
			    	if (i == keySz - 3) {
			    		keys[ct][i] *= yWeight;
			    	} else if (i == keySz - 2) {
			    		keys[ct][i] *= xWeight;
			    	} else if (i == keySz - 1) {
			    		keys[ct][i] = 2*turnWeight;
			    	}
			    }
			    vals[ct] = playOkMoveToInGameMove[value];
			    ct++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lsh = new LSH<Integer>(keys, vals, 100000., 1000000);
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
		int mi = selectChild(n, board.getPlayerTurn() == origTurn, board);
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
	
	public int selectChild(MCTNode n, boolean maximize, Board board) {
		int bi = 0;
		double[] preferences = kNNWeights(n.edges, board);
		double sign = maximize ? 1 : -1;
		double bestUCB = UCB(n.children.get(0), maximize) + sign * .05 * preferences[0];
		for (int i = 1; i < n.children.size(); i++) {
			double ucb = UCB(n.children.get(i), maximize) + sign * .05 * preferences[1];
			if (ucb > bestUCB) {
				bestUCB = ucb;
				bi = i;
			}
		}
		//System.out.println(bestUCB);
		return bi;
	}
	
	public int kNNMove(ArrayList<Move> moves, Board board) {
		int[] coefficients = new int[8];
		int[] mapping = new int[8];
		for (int i = 0; i < moves.size(); i++) {
			coefficients[moves.get(i).getType()] = 1;
			mapping[moves.get(i).getType()] = i;
		}
		double[] preferences = new double[8];
		Neighbor<double[], Integer>[] neighbors = lsh.search(board.toVector(xWeight, yWeight, turnWeight), k);
		for (int i = 0; i < k; i++) {
			preferences[neighbors[i].value] += (k - i);
		}
		int mi = mapping[0];
		double bestPreference = 0;
		for (int i = 0; i < 8; i++) {
			preferences[i] += Math.random() * k / 2.;
			double p = preferences[i] * coefficients[i];
			if (p > bestPreference) {
				bestPreference = p;
				mi = mapping[i];
			}
		}
		return mi;
		//return (int)(Math.random() * moves.size());
	}
	
	public double[] kNNWeights(ArrayList<Move> moves, Board board) {
		int[] coefficients = new int[8];
		int[] mapping = new int[8];
		for (int i = 0; i < moves.size(); i++) {
			coefficients[moves.get(i).getType()] = 1;
			mapping[moves.get(i).getType()] = i;
		}
		double[] preferences = new double[8];
		Neighbor<double[], Integer>[] neighbors = lsh.search(board.toVector(xWeight, yWeight, turnWeight), k);
		for (int i = 0; i < k; i++) {
			preferences[neighbors[i].value] += (k - i);
		}
		int mi = mapping[0];
		double bestPreference = 0;
		for (int i = 0; i < 8; i++) {
			preferences[i] += Math.random() * k / 2.;
			double p = preferences[i] * coefficients[i];
			if (p > bestPreference) {
				bestPreference = p;
				mi = mapping[i];
			}
		}
		double[] npreferences = new double[moves.size()];
		for (int i = 0; i < 8; i++) {
			npreferences[mapping[i]] = preferences[i];
		}
		return npreferences;
	}
	
	public int randomMove(ArrayList<Move> moves, Board board) {
		return (int)(Math.random() * moves.size());
	}
	
	public double rollout(Board board, boolean origTurn) {
		if (board.isGameOver()) {
			double delta = getDelta(board, origTurn);
			return delta;
		}
		ArrayList<Move> moves = board.validMoves();
		int mi = randomMove(moves, board);
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
