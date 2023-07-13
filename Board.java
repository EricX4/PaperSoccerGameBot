public class Board{

  //Fields:-----
  private int[][][] board = new int[13][9][8]; //define dimensions (x, y, z)
  private int numOfMoves;
  private int[] currentBallPosition; //x-coordinate is first element, y-coordinate is second element
  private int turn;

  //Constructs Board Object as a 3D array: (x coordinate, y coordinate, moves on compass rose)
  public Board(int[][][] board, int[] startingBallPosition) {
    this.board = board;
    this.currentBallPosition = startingBallPosition;
  } 

  //Getters and Setters:-----
  
  /**
  *  Returns current board position
  **/
  public int[][][] getBoard() 
  {
    return this.board;
  }

  /**
  *  Sets new board position data based on 3D array given
  **/
  public void setBoard(int[][][] board) {
    this.board = board;
  }

  /**
  * Returns the number of moves available for the ball to move at a specific time
  **/
  public int getNumOfMoves(){
    return numOfMoves;
  }

  public void setNumOfMoves(int numOfMoves) {
    this.numOfMoves = numOfMoves;
  }

  //Methods:-----

  /**
  *  Moves the currentBallPosition one spot to the left horizontally
  **/
  public void horizLeftMove() {
    this.currentBallPosition[0] = this.currentBallPosition[0]+1;
  }

  /**
  *  Moves the currentBallPosition one spot to the left horizontally and one spot up simultaneously in one move
  **/
  public void topLeftMove() {
    this.currentBallPosition[0] = this.currentBallPosition[0]+1;
  }

  public void upMove() {
    
  }

  public void topRightMove() {
    
  }

  public void horizRightMove() {
    
  }

  public void bottomMove() {
    
  }

  public void bottomRightMove() {
    
  }

  public void bottomLeftMove() {
    
  }

  // Returns true if bounce available, returns false otherwise
  public boolean checkBounce(){
    return false;
    
  }
  // Prints the board to the terminal in the form of dots and dashes
  public void printBoard(){
    
    for(int i = 0; i < 13; i++){
      for(int j = 0; j < 9; j++){
        for(int k = 0; k < 8; k++){
          
        }
      }
    }
  }

  /* Board win checker. 
  
  @param board : 3-d array of the board to be checked
  @return Returns 0 if the game not won yet, 1 if player 1 won, 2 if player 2 won
  */

  public int checkWin(){
    if (this.currentBallPosition[0] == 1 || this.currentBallPosition[0] == 13){
      if (this.currentBallPosition[1] == 4 || this.currentBallPosition[1] == 5 || this.currentBallPosition[1] == 6){
        return turn;
      }
    }
    return 0;
  } 
} 

  //returns a number between -1 and 1, where -1 represents a optimal position for player 1, and 1 represents a optimal position for player 2
  //temp - only scores based on y-coord of ball position
  public double scoreBoard(){
    bestMove = bestMove() //bestMove needs to be defined first <-- FIX
    return (((13-(double)currentBallPosition[1])/13*2)-1)
  }

  public ArrayList<Integer> bestMove(){
    moves = ArrayList<Integer>();
    return moves;
  }

  public void flipBoard(){
    pass; //FIX
  }

  public void lookahead() {
     
  }
}


  
