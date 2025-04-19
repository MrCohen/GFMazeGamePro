import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Enemy here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Enemy extends MazeMover
{
    protected abstract void drawEnemy();
    
    
    
    protected SuperStatBar statBar;
    
    public Enemy (Color c) {
        this.color = c;
        drawEnemy();
    }
    
    public void addedToWorld (World w){
             
    }
}
