import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Write a description of class Collider here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Collider extends MazeMover
{
    // Turn on for troubleshooting
    private static final boolean HIGHLIGHTING = false;

    private GreenfootImage blankImage, highlightImage;
    private MazeEntity owner;
    private boolean follow;
    private int xOffset, yOffset, halfWidth, halfHeight;

    /**
     *  For Colliders the same size as the owner with no offset
     */
    public Collider (MazeEntity owner){

    }

    public Collider (MazeEntity owner, int width, int height, int xOffset, int yOffset, boolean follow){
        blankImage = new GreenfootImage (width, height);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.owner = owner;
        this.follow = follow;
        halfWidth = width / 2;
        halfHeight = height / 2;
        name = "Collider";
    }

    public void addedToWorld (World w){
        if (owner != null){
            setLocation (owner.getX() + xOffset, owner.getY() + yOffset);
        }
    }

    /**
     * Act - do whatever the Collider wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        if (follow){
            setLocation (owner.getX() + xOffset, owner.getY() + yOffset);
        }
        // Add your action code here.
    }

    public ArrayList<Collider> getTouching (){
        return (ArrayList<Collider>)getIntersectingObjects(Collider.class);
    }

    public Collider checkDirection (int dX, int dY){
        Collider c = null;
        if (dX > 0){ // check right
            c = (Collider)getOneObjectAtOffset(halfWidth + dX, 0, Collider.class);
            if (c == null){
                c = (Collider)getOneObjectAtOffset(halfWidth + dX, -halfHeight + 1, Collider.class);
            } 
            if (c == null){
                c = (Collider)getOneObjectAtOffset(halfWidth + dX, halfHeight - 1, Collider.class);
            }
            if (c != null) return c;
        }
        else if (dX < 0){
            c = (Collider)getOneObjectAtOffset(-halfWidth + dX, 0, Collider.class);
            if (c == null){
                c = (Collider)getOneObjectAtOffset(-halfWidth + dX, -halfHeight + 1, Collider.class);
            } 
            if (c == null){
                c = (Collider)getOneObjectAtOffset(-halfWidth + dX, halfHeight - 1, Collider.class);
            }
            if (c != null) return c;
        }
        if (dY > 0){ // check below
            c = (Collider)getOneObjectAtOffset(0,halfHeight + dY, Collider.class);
            if (c == null){
                c = (Collider)getOneObjectAtOffset(-halfWidth + 1,halfHeight + dY, Collider.class);
            }
            if (c == null){
                c = (Collider)getOneObjectAtOffset(halfWidth - 1,halfHeight + dY, Collider.class);
            }
            if (c != null) return c;
        }
        if (dY < 0){ // check below
            c = (Collider)getOneObjectAtOffset(0,-halfHeight + dY, Collider.class);
            if (c == null){
                c = (Collider)getOneObjectAtOffset(-halfWidth + 1,-halfHeight + dY, Collider.class);
            }
            if (c == null){
                c = (Collider)getOneObjectAtOffset(halfWidth - 1,-halfHeight + dY, Collider.class);
            }
            if (c != null) return c;
        }

        return c;
    }

    public static boolean checkTouching (Collider a, Collider b){
        if (a.getX() < b.getX() + b.getWidth() && a.getX() + a.getWidth() > b.getX() && a.getY() < b.getY() + b.getHeight() && a.getY() + a.getHeight()  > b.getY()){
            return true;
        }
        return false;
    }

    public int getWidth() {
        return getImage().getWidth();
    }

    public int getHeight () {
        return getImage().getWidth();
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public MazeEntity getOwner() {
        return owner;
    }
}
