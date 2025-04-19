import greenfoot.*;  // (World, Actor, GreenfootImage, and Greenfoot)

/**
 * <p>A variation of an actor that maintains a precise location (using doubles for the co-ordinates
 * instead of ints).  This allows small precise movements (e.g. movements of 1 pixel or less)
 * that do not lose precision.</p>
 * 
 * <p>Modified by Jordan Cohen to include a precise rotation variable, as well as turn, setRotation 
 * and turnTowards methods.</p>
 * 
 * <p>To use this class, simply have all of the Actors that need to move smoothly inherit from this
 * class. This class adds new versions of move, turn and setLocation which take doubles. It also adds the
 * following methods to access the precise values:</p>
 * <ul>
 * <li><code>getPreciseX, getPreciseY -></code> Retrieves precise values</li>
 * <li><code>getPreciseRotation -></code> gets the precise angle</li>
 * <li><code>turnTowards (Actor) -></code> an added bonus - turn towards another Actor instead of an xy position</li>
 * </ul>
 * <p>Version 3.1 update - Now includes the option to enable static rotation, meaning the Actor will remain
 *    facing the same direction visually even while turning and moving as desired. Call the method enableStaticRotation() 
 *    to try this out</p>
 * 
 * 
 * @author Poul Henriksen
 * @author Michael Kolling
 * @author Neil Brown
 * 
 * @version 3.1.jc -- Modified by Jordan Cohen
 * 
 */
public abstract class SuperSmoothMover extends Actor
{
    private double exactX;
    private double exactY;
    private double rotation;
    private boolean staticRotation = false;

    /**
     * Move forward by the specified distance.
     * (Overrides the method in Actor).
     * 
     * @param distance  the distance to move in the current facing direction
     */
    @Override
    public void move(int distance)
    {
        move((double)distance);
    }

    /**
     * Move forward by the specified exact distance.
     * 
     * @param distance the precise distance to move in the current facing direction
     */
    public void move(double distance)
    {
        double radians;
        if (staticRotation){
            radians = Math.toRadians(rotation);
        } else {
            radians = Math.toRadians(getPreciseRotation());
        }
        double dx = Math.cos(radians) * distance;
        double dy = Math.sin(radians) * distance;
        setLocation(exactX + dx, exactY + dy);
    }

    /**
     * Static rotation means that the IMAGE WILL NOT ROTATE. The turn, turnTowards and move commands
     * will still work, and the Actor will move in the appropriate direction, but the image's facing angle
     * will not change. Note that the disableStaticRotation() method can be used to turn this off.
     */
    public void enableStaticRotation (){
        super.setRotation(0);
        staticRotation = true;
        rotation = 0.0;
    }
    
    public void disableStaticRotation (){
        super.setRotation((int)(rotation + 0.5));
        staticRotation = false;
    }
    
    /**
     * Set the internal rotation value to a new value.
     * 
     * @param rotation the precise new angle
     */
    public void setRotation (double rotation){
        this.rotation = rotation;
        if(!staticRotation)
            super.setRotation ((int)(rotation + 0.5));
    }

    /**
     * Set the internal rotation value to a new value. This will override the method from Actor.
     * 
     * @param rotation the new angle
     */
    @Override
    public void setRotation (int rotation){
        this.rotation = rotation;
        if(!staticRotation)
            super.setRotation(rotation);
    }

    /**
     * Set the internal rotation to face towards a given point. This will override the method from Actor.
     * 
     * @param x the x coordinate to face
     * @param y the y coordinate to face
     */
    @Override
    public void turnTowards (int x, int y){
        setRotation( Math.toDegrees(Math.atan2(y - getY() , x - getX())));
    }

    /**
     * A short-cut method that I (Jordan Cohen) always thought Greenfoot should have - use the
     * tuntToward method above to face another Actor instead of just a point. Keeps calling code
     * cleaner. 
     * 
     * @param a     The Actor to turn towards. 
     */
    public void turnTowards (Actor a){
        turnTowards (a.getX(), a.getY());
    }

    /**
     * Turn a specified number of degrees.
     * 
     * @param angle     the number of degrees to turn.
     */
    @Override
    public void turn (int angle){
        rotation += angle;
        if(!staticRotation)
            super.setRotation ((int)(rotation + 0.5));
    }

    /**
     * Turn a specified number of degrees with precision.
     * 
     * @param angle     the precise number of degrees to turn
     */
    public void turn (double angle){
        rotation += angle;
        if(!staticRotation)
            super.setRotation ((int)(rotation + 0.5));
    }

    /**
     * Set the location using exact coordinates.
     * 
     * @param x the new x location
     * @param y the new y location
     */
    public void setLocation(double x, double y) 
    {
        exactX = x;
        exactY = y;
        super.setLocation((int) (x + 0.5), (int) (y + 0.5));
    }

    /**
     * Set the location using integer coordinates.
     * (Overrides the method in Actor.)    
     * 
     * @param x the new x location
     * @param y the new y location
     */
    @Override
    public void setLocation(int x, int y) 
    {
        exactX = x;
        exactY = y;
        super.setLocation(x, y);
    }

    /**
     * Return the exact x-coordinate (as a double).
     * 
     * @return double   the exact x coordinate, as a double
     */
    public double getPreciseX() 
    {
        return exactX;
    }

    /**
     *   original name from SmoothMover, for compatibility. Same as above.
     *   
     *   @return double the exact x coordinate, as a double
     */
    public double getExactX(){
        return exactX;
    }

    /**
     * Return the exact y-coordinate (as a double).
     * 
     * @return double   the exact x coordinate, as a double
     */
    public double getPreciseY() 
    {
        return exactY;
    }

    /**
     *   original name from SmoothMover, for compatibility. Same as above.
     *   
     *   @return double the exact y coordinate, as a double
     */
    public double exactY(){
        return exactY;
    }

    public double getPreciseRotation (){
        return rotation;
    }
    
    @Override
    public int getRotation (){
      if (!staticRotation){
          return super.getRotation();
      } else {
          return (int)(rotation + 0.5);
      }
    }
    
}
