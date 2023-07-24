import java.util.ArrayList;
import java.util.Arrays;

public class Point {
  //Animation related fields (prefixed by a_):----
  private double hoverState;
  private boolean isHover;

  //Fields:----
  private boolean[] connections;
  //determines whether or not the ball can bounce off the point
  private boolean canBounce;
  private boolean originalCanBounce;
  
  //shows how the displacements map to indices in the connections array
  private static int[][] displacementToIndex = {{0, 1, 2},{3, -1, 4},{5, 6, 7}};

  public Point() {
    canBounce = false;
    originalCanBounce = false;
    connections = new boolean[8];
    hoverState = 0.;
    isHover = false;
  }

  //Set the connection between points (x,y) and (x + dx, y + dy)
  public void setConnection(int dx, int dy, boolean connected) {
    int idx = displacementToIndex[dx + 1][dy + 1];
    if (idx == -1) return;
    connections[idx] = connected;
    canBounce = false;
    for (int i = 0; i < 8; i++) {
      canBounce = canBounce || connections[i];
    }
    canBounce = originalCanBounce || canBounce;
  }

  //Get the state of the connection between points (x,y) and (x + dx, y + dy)
  public boolean getConnection(int dx, int dy) {
    int idx = displacementToIndex[dx + 1][dy + 1];
    if (idx == -1) return true;
    return connections[idx];
  }

  public void setCanBounce(boolean c) {
    canBounce = c;
    originalCanBounce = c;
  }

  public boolean getCanBounce() {
    return canBounce;
  }

  public void setIsHover(boolean h) {
    isHover = h;
  }
  
  public double getHoverState() {
    return hoverState;
  }

  //Update the animation by a time step delta (in milliseconds)
  public void updateAnimation(double delta) {
    if (isHover) {
      hoverState = AnimationFunctions.clamp(hoverState + delta * AnimationsConfig.POINT_HOVER_RATE / 1000., 0, 1);
    } else {
      hoverState = AnimationFunctions.clamp(hoverState - delta * AnimationsConfig.POINT_HOVER_RATE / 1000., 0, 1);
    }
    isHover = false;
  }
}