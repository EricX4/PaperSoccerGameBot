import java.util.Random;
import java.util.*;

public class ShortestPathBot extends Bot {

  private long time = 0;
  private int turns = 0;
	public ShortestPathBot() {
		
	}
	
	//generate moves for one turn
	public ArrayList<Move> generateMoves(Board board) {
    long start = System.nanoTime(); // Start Time Counter Per Move
    boolean flag = true;
		ArrayList<Move> result = new ArrayList<Move>(); //List of moves the bot will make
    int[] ballPos = board.getBallPos();
    ArrayList<Move> validMoves = board.validMoves(ballPos[0], ballPos[1]); // gets valid moves given current ball position
 //   for (int i = 0; i < validMoves.size(); i++) {
  //    System.out.println(validMoves.get(i)); //prints out valid moves
 //  }

    int move = 0;
    int smallestDistance = 600; //temporary max to find the shortest distance
    Move bestMove = null;
    while (board.canMoveBot()){ //while it is the bot's turn and not player's turn
      flag = false;
      for(int i = 0; i < validMoves.size(); i++){ //iterates through validMoves arraylist and searches for shortest distance
        if(validMoves.get(i).getY() > validMoves.get(i).getPY()) {
          int dist = Math.abs(validMoves.get(i).getX() - 4);
          if(dist < smallestDistance) {
            smallestDistance = dist;
            bestMove = validMoves.get(i);
            flag=true; 
          }
        }
      }
      if (!(flag)){
        Random rand = new Random();
        Move randomMove = validMoves.get(rand.nextInt(validMoves.size()));
        result.add(randomMove);
        board.addMove(randomMove);
        board.executeMove(result.get(move)); //Bot making the moves 
        validMoves = board.validMoves(ballPos[0], ballPos[1]);
        move++;
      } else {
        result.add(bestMove); //adds the bestMove to the result arraylist (the series of moves the bot will make)
        board.addMove(bestMove); //adds the bestMove to the board 
        board.executeMove(bestMove);
        validMoves = board.validMoves(ballPos[0], ballPos[1]); // 
        move++;
      }

//      board.setPlayerTurn(false);
    }

    
    
    for (int i = 0; i < move; i++) {
      board.revertMove(); //reverts all the moves made
      // System.out.println("LMAOOOOOO");
    }
    long end = System.nanoTime();
    turns+=1;
    time+=(Math.pow(10, -9)*(end-start));
    //System.out.println("AVERAGE TIME TAKEN AFTER " + turns + " TURNS = "+(time/turns));
    
		return result;
	}
}