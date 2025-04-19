/**
 * Write a description of class Coordinate here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Coordinate {
    int x, y;
    private Coordinate parent;
    private boolean isStart;

    public Coordinate (int x, int y){
        isStart = true;
        this.parent = null;
        this.x = x;
        this.y = y;
    }

    public Coordinate (Coordinate parent, int x, int y){
        isStart = false;
        this.parent = parent;    
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Coordinate getParent (){
        return parent;
    }

    public boolean isStart () {
        return isStart;
    }

    public String toString () {
        return "(" + x + ", " + y + ")"; //<-" + parent;
    }
    
    @Override
    public boolean equals (Object other){
        if (other == this){
            return true;
        }
        if (!(other instanceof Coordinate)){
            return false;
        }
        Coordinate c = (Coordinate)other;
        return (x == c.getX() && y == c.getY());
    }
    
}
