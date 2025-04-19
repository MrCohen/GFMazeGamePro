import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Bit here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Bit extends Projectile
{
    private static final int SIZE = 5;
    private static final double SPEED = 4.0;
    
    //private GreenfootImage image;
    public Bit (Maze m, int dX, int dY, Class targetType, Color color, MazeMover shooter){
        super (m, dX, dY, targetType, shooter);
        damage = 1;
        maxDistance = -1; // no max distance
        subjectToDrag = false;
        destroyOnWall = true;
        xDrag = false;
        yDrag = false;
        this.dX = dX * SPEED;
        this.dY = dY * SPEED;
        
        lightStrength = 3;
        lightIntensity = 0.3;
        
        image = new GreenfootImage (SIZE, SIZE);
        image.setColor(color);
        image.fill();
        image.setColor(Color.BLACK);
        image.drawRect(0,0,SIZE-1, SIZE-1);
        setImage(image);
    }
    /**
     * Act - do whatever the Bit wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        super.act();
    }
}
