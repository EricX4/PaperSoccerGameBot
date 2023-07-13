import java.util.ArrayList;
import java.util.Arrays;

public class Point {

  //Fields:-----
  private int x;
  private int y;
  private boolean placed;
  private ArrayList<Integer> connections;
  private ArrayList<Integer> validConnections;

  //Constructor:-----
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
    placed = false;
    this.validConnections = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
  }

  //Getters and Setters:-----
  public boolean getPlaced() {
    return placed;
  }

  public ArrayList<Integer> getConnections() {
    return connections;
  }
  
  /**
   * Moves the Point one spot to the left horizontally
   */
  public void horizLeftMove() {
    this.x = this.x - 1;
  }

  /**
   * Moves the Point one spot to the left horizontally and one spot up simultaneously in one move
   */
  public void topLeftMove() {
	this.x = this.x - 1;
    this.y = this.y + 1;
  }

  /**
   * Moves the Point one spot to the up vertically
   */
  public void upMove() {
    this.y = this.y + 1;
  }

  /**
   * Moves the Point one spot to the right horizontally and one spot up simultaneously in one move
   */
  public void topRightMove() {
    this.x = this.x + 1;
    this.y = this.y + 1;
  }

  /**
   * Moves the Point one spot to the right horizontally
   */
  public void horizRightMove() {
    this.x = this.x + 1;
  }

  /**
   * Moves the Point one spot down vertically
   */
  public void bottomMove() {
    this.y = this.y - 1;
  }

  /**
   * Moves the Point one spot to the right horizontally and one spot down simultaneously in one move
   */
  public void bottomRightMove() {
    this.x = this.x + 1;
    this.y = this.y - 1;
  }

  /**
   * Moves the Point one spot to the left horizontally and one spot down simultaneously in one move
   */
  public void bottomLeftMove() {
    this.x = this.x - 1;
    this.y = this.y - 1;
  }

  //Methods:-----
  public void place(){
    placed = true;
  }

  public void addConnection(int connection){
    connections.add(connection);
  }

  public boolean isValidConnection(int connection){
    if ((validConnections.contains(connection)) && (!(connections.contains(connections)))) {
      connections.add(connection);
    }
  return true; //FIX
  }
}
