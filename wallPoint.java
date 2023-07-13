import java.util.ArrayList;

public Class wallPoint extends Point() {

  //Fields:-----
  //Wall: 0 = top, 1 = right, 2 = bottom, 3 = left
  private int wall;
  
  //Constructor:-----
  public wallPoint(wall) {
    super()
    wall=this.wall
    if (wall==0){
      validConnections = {3, 4, 5};
    }
    if (wall==1){
        validConnections = {5, 6, 7};
      }
    if (wall==2){
      validConnections = {7, 0, 1};
    }
    if (wall==3){
      validConnections = {1, 2, 3};
    }
  }
}