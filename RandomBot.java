import java.util.Random;
import java.util.*;

public class RandomBot extends Bot {
	public RandomBot() {
		
	}
	
	//generate moves for one turn
	public ArrayList<Move> generateMoves(Board board) {
		ArrayList<Move> result = new ArrayList<Move>(); // List of moves the bot will make
		int[] ballPos = board.getBallPos();
		ArrayList<Move> validMoves = board.validMoves(ballPos[0], ballPos[1]);
		for (int i = 0; i < validMoves.size(); i++) {
			// System.out.println(validMoves.get(i));
		}
		// System.out.println(board.getMoves().size());

		int move = 0;
		while ((board.canMoveBot())) {
			Random rand = new Random();
			Move randomMove = validMoves.get(rand.nextInt(validMoves.size()));
			result.add(randomMove);
			board.addMove(randomMove);
			board.executeMove(result.get(move)); // Bot making the moves
			validMoves = board.validMoves(ballPos[0], ballPos[1]);
			move++;
		}
		for (int i = 0; i < move; i++) {
			board.revertMove();
		}
		// System.out.println(board.getMoves().size());
		return result;
	}
}