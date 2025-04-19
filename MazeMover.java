import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MazeMover here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MazeMover extends MazeEntity
{
    protected double dX, dY;
    // cache for simplicity and performance

    protected boolean subjectToDrag, xDrag, yDrag;
    protected boolean destroyOnWall;

    protected int hp, maxHp;

    protected double drag;

    
    public void hitMe (int damage){
        //System.out.println(this + " got hit for " + damage);
        hp = Math.max (0, hp - damage);
        if (hp == 0){
            GameWorld.outputText (this + " was defeated!");
            maze.removeMazeEntity(this);
        }
    }

    /**
     * Apply Movement based on current dX / dY
     * 
     * No idea why round works for right and down but absCeil 
     * (directional round, away from zero) works for up and left.
     */
    protected void applyMove(){

        if (subjectToDrag){
            if (xDrag){
                if (dX > 0){
                    dX = Math.max (0, dX * drag);
                } else {
                    dX = Math.min (0, dX * drag);
                }
                if (Math.abs(dX) < 0.005){
                    dX = 0;
                } 
            }

            if (yDrag){
                if (dY > 0){
                    dY = Math.max (0, dY * drag);
                } else {
                    dY = Math.min (0, dY * drag);
                }
                if (Math.abs(dY) < 0.005){
                    dY = 0;
                }
            }
        }

        Block b;// = null;

        // moving right
        if (dX > 0){
            b = (Block)getOneObjectAtOffset(round(halfMe + dX)+1,halfMe-1, Block.class);
            //System.out.println("while trying to move right - " + b);
            if (b == null  || b.isPassable()){

                b = (Block)getOneObjectAtOffset(round(halfMe + dX)+1,-halfMe+1, Block.class);
            }
            if (b != null && !b.isPassable()){
                if (destroyOnWall){
                    //getWorld().removeObject(this);
                    maze.removeMazeEntity(this);
                    return;
                }
                
                if (this instanceof Grenade){
                    //dX = -dX;
                    setRotation (180 - getPreciseRotation());
                    // return;
                    dX = 0;
                } else {
                    setLocation (b.getX() - halfBlock - halfMe, getPreciseY());
                    dX = 0;
                }
            }   
        }

        //  moving left
        else if (dX < 0){
            b = (Block)getOneObjectAtOffset(absCeil(-halfMe + dX)-1,halfMe-1, Block.class);
            if (b == null || b.isPassable()){
                b = (Block)getOneObjectAtOffset(absCeil(-halfMe + dX)-1,-halfMe+1, Block.class);
            }
            if (b != null && !b.isPassable()){
                if (destroyOnWall){
                    maze.removeMazeEntity(this);
                    return;
                }
                
                if (this instanceof Grenade){
                    setRotation (180 - getPreciseRotation());
                    //return;
                    dX = 0;
                } else {
                    setLocation (b.getX() + halfBlock + halfMe, getPreciseY());
                    dX = 0;
                }
            }
        }

        // apply horizontal movement
        setLocation(getPreciseX() + dX, getPreciseY());

        //  Moving Down
        if (dY > 0){
            b = (Block)getOneObjectAtOffset(halfMe-1, round(halfMe + dY)+1, Block.class);
            if (b == null ||  b.isPassable()){
                b = (Block)getOneObjectAtOffset(-halfMe+1, round(halfMe + dY)+1, Block.class);
            }
            if (b != null && !b.isPassable()){
                if (destroyOnWall){
                    maze.removeMazeEntity(this);
                    return;
                }
                
                if (this instanceof Grenade){
                    setRotation (360 - getPreciseRotation());
                    dY = 0;
                   //return;
                } else {
                    setLocation (getPreciseX(), b.getY() - halfBlock - halfMe);
                    dY = 0;
                }
            }
        }

        // Moving Up
        else if (dY < 0){
            b = (Block)getOneObjectAtOffset(halfMe-1, absCeil(-halfMe + dY)-1, Block.class);
            if (b == null || b.isPassable()){
                b = (Block)getOneObjectAtOffset(-halfMe+1,absCeil(-halfMe + dY)-1, Block.class);
            }
            if (b != null && !b.isPassable()){
                if (destroyOnWall){
                    maze.removeMazeEntity(this);
                    return;
                }
               
                
                if (this instanceof Grenade){
                    setRotation (360 - getPreciseRotation());
                    dY = 0;
                    //return;
                } else {
                     setLocation (getPreciseX(), b.getY() + halfBlock + halfMe);
                    dY = 0;
                }
            }
        }

        // apply vertical movement
        setLocation(getPreciseX(), getPreciseY() + dY);
        
        updatePosition();
    }

    public int round (double val){
        return (int)(val + 0.5);
    }

    public int absCeil (double val){
        return (int)(val >= 0 ? Math.ceil(val) : Math.floor(val));
    }
}
