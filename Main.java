import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Main {
  private static Board board;

  private static int mouseX;
  private static int mouseY;

  //the current time elapsed since running the game, in milliseconds
  public static double time;

  //these variables represent actual unix time in ms
  public static long then;
  public static long now;
  
  public static void main(String[] args) {
    then = -1;
    board = new Board(true);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  private static void createAndShowGUI() {
    // Create the main frame
    JFrame frame = new JFrame("Paper Soccer Bot");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Create the canvas for drawing
    final Canvas canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(GraphicsConfig.WINDOW_WIDTH, GraphicsConfig.WINDOW_HEIGHT));
    canvas.setBackground(Color.WHITE);

    // Add mouse click listener to the canvas
    canvas.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int gridCenterX = (9 - 1) / 2;
        int gridCenterY = (13 - 1) / 2;

        int w = GraphicsConfig.WINDOW_WIDTH;
        int h = GraphicsConfig.WINDOW_HEIGHT;
        int centerX = w/2;
        int centerY = h/2;
        int ss = GraphicsConfig.SQUARE_SIZE;
        
        int gridX = (int)Math.round((x - centerX)/(double)ss) + gridCenterX;
        int gridY = (int)Math.round((y - centerY)/(double)ss) + gridCenterY;

        board.click(gridX, gridY);
      }
    });

    canvas.addMouseMotionListener(new MouseAdapter() {
      public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
      }
    });

    // Add keyboard listener to the canvas
    canvas.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        System.out.println("Key pressed: " + KeyEvent.getKeyText(keyCode));
      }
    });

    // Set focusable and request focus for keyboard events
    canvas.setFocusable(true);
    canvas.requestFocus();

    // Add the canvas to the frame
    frame.getContentPane().add(canvas, BorderLayout.CENTER);
    frame.setVisible(true);

    frame.pack();

    // Start the animation loop
    startAnimationLoop(canvas);
  }

  private static void startAnimationLoop(final Canvas canvas) {
      // Start a Timer to trigger the animation loop
      Timer timer = new Timer((int)GraphicsConfig.FRAME_LENGTH, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // Repaint the canvas
          canvas.repaint();
        }
      });
      timer.start();
  }

  static class Canvas extends JPanel {
    private Font secularFont;
    private Font secularHeader1;
    private Font secularBody;

    public Canvas() {
      Font secularFont;
      try {
        secularFont = Font.createFont(Font.TRUETYPE_FONT, new File("SecularOne-Regular.ttf"));
        secularHeader1 = secularFont.deriveFont(Font.PLAIN, GraphicsConfig.HEADER1);
        secularBody = secularFont.deriveFont(Font.PLAIN, GraphicsConfig.BODY);
      } catch (IOException | FontFormatException e) {
        e.printStackTrace();
        return;
      }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      
      // Perform drawing operations here
      // Use the provided Graphics object to draw on the canvas
      Graphics2D g2d = (Graphics2D) g;

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2d.setColor(new Color(255, 255, 255));
      g2d.fillRect(0, 0, GraphicsConfig.WINDOW_WIDTH, GraphicsConfig.WINDOW_HEIGHT);

      now = System.currentTimeMillis();
      if (then == -1) then = now;
      double delta = Math.min(now - then, GraphicsConfig.MAX_FRAME_LENGTH);
      then = now;
      time += delta;
      
      g2d.setFont(secularHeader1);
      
      int centerX = GraphicsConfig.WINDOW_WIDTH/2;
      int centerY = GraphicsConfig.WINDOW_HEIGHT/2;
      g2d.translate(centerX, centerY);
      
      board.renderBoard(g2d, delta, mouseX, mouseY);
      
      g2d.translate(-centerX, -centerY);
    }
  }
}
