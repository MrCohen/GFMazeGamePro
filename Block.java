import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Block here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Block extends Actor
{
    // mark each shaded block to see it's opacity, for testing lighting

    protected boolean passable;
    protected GreenfootImage image;

    protected int mazeX, mazeY;

    protected static int blockSize;

    /**
     * Prepare -> For now just set size
     */
    public static void init (int size) {
        blockSize = size;

    }

    public Block (boolean passable, int mazeX, int mazeY){
        //image = new Image (blockSize, blockSize);
        this.passable = passable;
        this.mazeX = mazeX;
        this.mazeY = mazeY;

    }

    public void highlight () {
        if (!GameWorld.RENDER_TILES){
            image.setColor(Color.PINK);
            image.fill();
        } else {
            image.drawImage(new GreenfootImage("dirtTile9.png"), 0, 0);
        }
        if (Maze.SHOW_CELL_BORDERS){
            image.setColor(Color.BLACK);
            image.drawRect(0,0, blockSize - 2, blockSize-2);
        }
        // updateShades(images);
        setImage(image);

    }

    public void removeHighlight () {
        image.setColor(Color.LIGHT_GRAY);
        image.fill();
        if (Maze.SHOW_CELL_BORDERS){
            image.setColor(Color.BLACK);
            image.drawRect(0,0, blockSize - 2, blockSize-2);
        }
        //updateShades(image);
        setImage(image);
    }

    public boolean touchingPlayer(){
        return this.isTouching(Player.class);
    }

    public int getMazeX () {
        return mazeX;
    }

    public int getMazeY () {
        return mazeY;
    }

    public static int getSize () {
        return blockSize;
    }

    public boolean isPassable(){
        return passable;
    }

    public boolean isBlocked() {
        return !passable;
    }

    public String toString () {
        return  (passable ? " passable at: " : "blocked at:") + mazeX + ", " + mazeY ;
    }

}
