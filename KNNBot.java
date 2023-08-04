import java.io.*;
import java.util.*;
import smile.neighbor.LSH;
import smile.neighbor.Neighbor;

public class KNNBot extends Bot {
	public static int keySz = 9 * 13 * 8 + 2 + 1;
	
	private LSH<Integer> lsh;
	private int k;
	private double xWeight;
	private double yWeight;
	private double turnWeight;
	
	public static int[] playOkMoveToInGameMove = {3, 5, 6, 7, 4, 2, 1, 0};
	public static int[] inGameMoveToPlayOkMove = {7, 6, 5, 0, 4, 1, 2, 3};
	
	public KNNBot() {
		k = 1;
		xWeight = 20.0;
		yWeight = 20.0;
		turnWeight = 1000000.;
		init();
	}
	
	public KNNBot(int k, double xW, double yW, double tW) {
		this.k = k;
		this.xWeight = xW;
		this.yWeight = yW;
		this.turnWeight = tW;
		init();
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
	
	public void printBoardVec(double[] vec) {
		for (int y = 0; y < 13; y++) {
			for (int x = 0; x < 9; x++) {
				int sum = 0;
				for (int i = 0; i < 8; i++) {
					if (vec[8*(9 * y + x) + i] == 1) sum++;
				}
				System.out.print(sum + " ");
			}
			System.out.println();
		}
		System.out.println("(" + (vec[keySz - 2]/xWeight)+ ", " + (vec[keySz - 3]/yWeight) + "), and turn " + (vec[keySz - 1]/turnWeight));
		System.out.println("--------------------------");
	}
	
	public int kNNMove(ArrayList<Move> moves, Board board) {
		int[] coefficients = new int[8];
		int[] mapping = new int[8];
		for (int i = 0; i < moves.size(); i++) {
			coefficients[moves.get(i).getType()] = 1;
			mapping[moves.get(i).getType()] = i;
		}
		double[] preferences = new double[8];
		System.out.println("ONE ROUND");
		printBoardVec(board.toVector(xWeight, yWeight, turnWeight));
		Neighbor<double[], Integer>[] neighbors = lsh.search(board.toVector(xWeight, yWeight, turnWeight), k);
		for (int i = 0; i < k; i++) {
			preferences[neighbors[i].value] += (k - i);
		}
		
		printBoardVec(neighbors[0].key);
		System.out.println(neighbors[0].value);
		
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
	}
	
	public ArrayList<Move> generateMoves(Board board) {
		boolean origTurn = board.getPlayerTurn();
		
		ArrayList<Move> turn = new ArrayList<Move>();
		
		while (board.canMoveBot()) {
			ArrayList<Move> moves = board.validMoves();
			int mi = kNNMove(moves, board);
			Move m = moves.get(mi);
			board.makeMove(m);
			turn.add(m);
		}
		board.revertTurn(turn);
		
		
		return turn;
	}
}
