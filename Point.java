import java.util.ArrayList;

public Class Point {

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
    this.validConnections = {0, 1, 2, 3, 4, 5, 6, 7};
  }

  //Getters and Setters:-----
  public boolean getPlaced() {
    return placed;
  }

  public ArrayList<Integer> getConnections() {
    return connections;
  }

  //Methods:-----
  public void place(){
    placed = true;
  }

  public void addConnection(int connection){
    connections.add(connection);
  }

  public boolean isValidConnection(int connection){
    if (validConnections.contains(connection))&&(!(connections.contains(connections)))
      connections.add(connection);
    }
  }
  
}