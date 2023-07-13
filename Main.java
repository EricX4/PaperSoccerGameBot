import java.security.PublicKey;

class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    System.out.println("Board Class Testing START");   
    //Place Code to Test Board Class Here:
    Board gameBoard = new Board(null, null);


    char[][] gb = new char[30][24];

    for(int i = 0; i < 30; i+= 3){
      for(int j = 0; j < 24; j += 3) {
        
      }
    System.out.println("Board Class Testing END");
    System.out.println("Point Class Testing START");
    //Place Code to Test Point Class Here:
    Point testPoint = new Point(i, i);
      
    System.out.println("Point Class Testing END");
    }
  }
}
