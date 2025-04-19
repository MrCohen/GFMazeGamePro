import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Write a description of class ScrollingWindow here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ScrollingWindow extends SuperWindow
{
    public static final int CACHE_DISTANCE_FROM_EDGE = 3;
    
    
    
    private int totalWidth, totalHeight;
    
    
    private int allowedWidth, allowedHeight;
    private Coordinate cameraPosition;
    private Coordinate targetPosition;
    private Coordinate startPosition;
    private Actor target;
    private ArrayList<ActorContent> currentContents; 
    public ScrollingWindow (Actor target, Coordinate startPositions, int winWidth, int winHeight, int totalWidth, int totalHeight, int dragBarHeight, String title, boolean[] flags, Color borderColor, Color backgroundColor, int borderThickness){
    
        super (winWidth, winHeight, dragBarHeight, title, flags, borderColor,  backgroundColor, borderThickness);
        this.startPosition = startPosition;
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        allowedWidth = (int)(winWidth * 0.6);
        allowedHeight = (int)(winWidth * 0.6);
        this.target = target;
        
    }
        
    /**
     * Act - do whatever the ScrollingWindow wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        super.act();
        // check if target is passing edge and if so, adjust all objects
        
    }
}
