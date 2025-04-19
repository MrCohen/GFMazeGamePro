import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Projectile here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Projectile extends MazeMover
{
    protected double speed;
    protected double maxDistance;
    protected int damage;
    protected MazeMover shooter;
    private java.lang.Class targetType;

    public Projectile (Maze m, double dX, double dY, Class targetType, MazeMover shooter) {
        this.maze = m;
        this.dX = dX;
        this.dY = dY;
        this.targetType = targetType;
        this.shooter = shooter;
    }

    public static void init () {
        Grenade.init();
    }
    
    public void act () {
        applyMove();
        if (getWorld() == null){
            return;
        }
        checkTargetCollision();
    }

    protected void checkTargetCollision() {
        MazeMover mm = (MazeMover)getOneIntersectingObject(targetType);
        if (mm != null){
            if (mm instanceof Player){
                GameWorld.outputText ("Player was hit by " + shooter + " for " + damage + " damage.");
            }
            mm.hitMe (damage);
            maze.removeMazeEntity(this);
        }
    }

}
