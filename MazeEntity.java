import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MazeEntity here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MazeEntity extends SuperSmoothMover
{
    protected static Color backgroundColor = new Color (240,123,123);
    protected static Color borderColor = (Color.GREEN);

    protected static Maze maze;
    protected static int blockSize, halfBlock;

    protected int halfMe;
    protected int mazeX, mazeY, halfWidth, halfHeight;

    protected Color color, secondaryColor;
    private Block location;
    protected GreenfootImage image;
    //private int width, height;
    protected int lightStrength;
    protected double lightIntensity;
    protected String name;

    protected Coordinate mazeLocation;

    public static void init (Maze m){
        maze = m;
        blockSize = m.getBlockSize();
        halfBlock = blockSize / 2;
        Item.init();
    }

    public MazeEntity (){
        lightStrength = 0;
        lightIntensity = 1.0;
        name = "";
    }

    protected void updateSize() {
        if (image == null) {
            image = getImage(); // fallback to Greenfoot's image
        }
        halfWidth = image.getWidth()/2;
        halfHeight = image.getHeight()/2;

    }

    public int getHalfWidth() {
        return halfWidth;
    }

    public int getHalfHeight() {
        return halfHeight;
    }

    public void addedToWorld (World w){
        updatePosition();
    }

    protected void updatePosition () {
        updateSize();
        Block b = (Block)getOneObjectAtOffset(0,0, Block.class);
        if (b != null){
            int tempX = b.getMazeX();
            int tempY = b.getMazeY();
            if (tempX != mazeX || tempY != mazeY){
                mazeX = tempX;
                mazeY = tempY;
                //maze.updateLightGrid (mazeX, mazeY, lightStrength);
                if (lightStrength > 0){
                    maze.refreshLight();
                }
                if (this instanceof Player){
                    maze.updatePlayerPosition (new Coordinate(mazeX, mazeY), maze);
                }
            }

        }

    }

    /**
     * Act - do whatever the MazeEntity wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
    }

    public boolean light(){
        return lightStrength > 0;
    }

    public int getLightStrength() {
        return lightStrength;
    }

    public double getLightIntensity() {
        return lightIntensity;
    }

    public int getMazeX () {
        return mazeX;
    }

    public int getMazeY () {
        return mazeY;
    }
}
