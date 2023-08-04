class Move {
  private int x;
  private int y;
  //previous x and y
  private int px;
  private int py;

  //this is used for the bot so it doesn't just do all the moves at once - each move has a delay before it is actually executed
  //for the player this is always 0 milliseconds
  private double delay;
  private boolean executed;

  //recent moves are thicker than older moves - this keeps track of how recent a move is
  private double recency;

  boolean isPlayerTurn;
  
  //shows how the displacements map to indices in the connections array
  private static int[][] displacementToIndex = {{0, 1, 2}, {3, -1, 4},{5, 6, 7}};

  public Move(int x, int y, int px, int py, boolean isPlayerTurn, double delay) {
    this.x = x;
    this.y = y;
    this.px = px;
    this.py = py;
    this.isPlayerTurn = isPlayerTurn;
    this.delay = delay;
    executed = false;

    //initially recency is 1
    recency = 1;
  }
  
  public void flip() {
	y = 13 - y - 1;
	py = 13 - py - 1;
  }
  
  public int getType() {
	  int dx = x - px;
	  int dy = y - py;
	  return displacementToIndex[dx + 1][dy + 1];
  }

  public int getX() {
    return x;
  }
  public int getY() {
    return y;
  }
  public int getPX() {
    return px;
  }
  public int getPY() {
    return py;
  }

  //amt is in milliseconds
  public void decrementDelay(double amt) {
    delay = Math.max(delay - amt, 0);
  }
  public double getDelay() {
    return delay;
  }
  
  public void setDelay(double d) {
	  delay = d;
  }
  
  public void decrementRecency(double amt) {
    recency = Math.max(recency - amt, 0);
  }
  public double getRecency() {
    return recency;
  }
  
  public boolean getExecuted() {
    return executed;
  }
  public void setExecuted(boolean e) {
    executed = e;
  }
  public boolean getIsPlayerTurn() {
    return isPlayerTurn;
  }

  public String toString() {
    return "Move: " + "(" + px + ", " + py + ") -------------> " + "(" + x + ", " + y + ") ------------- \n";
  }
}