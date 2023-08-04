//Stores parameters about the graphics that can be edited before runtime
public class GraphicsConfig {
  //dimensions of the window in terms of pixels
  public static final int WINDOW_WIDTH = 360;
  public static final int WINDOW_HEIGHT = 500;

  public static final int SQUARE_SIZE = 30;
  public static final float GRID_LINE_WIDTH = 1.3f;
  public static final float FIELD_BORDER_WIDTH = 2f;
  public static final int BALL_RADIUS = 5;
  public static final float ACTIVE_TRAIL_WIDTH = 2.8f;
  public static final float INACTIVE_TRAIL_WIDTH = 1.5f;
  public static final int POINT_HOVER_RADIUS = 4;

  public static final int TURN_BAR_HEIGHT = 7;
  
  public static final int HEADER1 = 20;
  public static final int BODY = 10;

  public static final double FRAME_LENGTH = 16; //number of milliseconds per frame
  public static final double MAX_FRAME_LENGTH = 200; //anything below 5 fps slows down the game

  //dummy private constructor to prevent instantiation
  private GraphicsConfig() {}
}