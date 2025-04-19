import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.ArrayList;

/**
 * A Maze Drawing World.
 * 
 * Implemented Prim's Algorithm (Credit: Robert C Prim, 1957)
 * 
 * (For Algorithm Notes, see MazeGen, the code on which this was based)
 * 
 * ----- MAZE GAME 0.1.0 --------
 * Player is implemented - currently a square the walks around the maze.
 *  -- Can "catch" corners (only took a day to get this right!)
 *  
 * Goal -- A Random Maze Dungeon Game!
 * 
 * This version - Turn World Maze() into  Actor Maze (int blockSize, int blocksWide, int blocksHigh)
 *
 * ----- MAZE GAME 0.3.0 --------
 * 
 * Time to turn Maze into something other than a World...
 * 
 * @author Jordan Cohen
 * @version 1.0, Dec 2022
 */
public class Maze
{
    // Constants
    public static final int MODE = 0;
    // Hide all shaders for troubleshooting (or for other reasons??)
    public static final boolean DISABLE_SHADERS = false;

    // Help Learning by Visualizing
    public static final boolean SHOW_CELL_BORDERS = false; // help distinguish posts from walls and rooms
    public static final boolean DEMO_ALGORITHM = false; // show the algorithm as it works - this takes lots of time! Only for testing

    private int xOffset, yOffset;

    private int blockSize, blocksWide, blocksHigh, halfBlock;

    // Class Objects and Variables
    private Block[][] theGrid;
    private ShadeBlock[][] shadeGrid;
    private double[][] lightGrid;
    private ArrayList<MazeEntity> entities, lightEmitters;
    private ArrayList<ShadeBlock> shades;
    private Block startBlock, endBlock;
    private SuperWindow mazeFrame;

    private Player player;
    private int playerX, playerY;
    private Block playerBlock;
    private Traverser trav;

    /**
     * Constructor for objects of class Maze.
     * 
     */
    public Maze(int blockSize, int blocksWide, int blocksHigh, SuperWindow mazeFrame)
    {    
        this.blockSize = blockSize;
        this.blocksWide = blocksWide;
        this.blocksHigh = blocksHigh;
        halfBlock = blockSize / 2;

        this.mazeFrame = mazeFrame;

        this.xOffset = mazeFrame.getXOffset();
        this.yOffset = mazeFrame.getYOffset();

        // ---PREPARE THE GRID----
        // Init grid based on constants
        theGrid = new Block[blocksWide][blocksHigh];
        lightGrid = new double[blocksWide][blocksHigh];
        shadeGrid = new ShadeBlock[blocksWide][blocksHigh];

        entities = new ArrayList<MazeEntity>();

        lightEmitters = new ArrayList<MazeEntity>();

        shades = new ArrayList<ShadeBlock>();

        // Prepare the grid (build Posts, Walls and Rooms)
        prepareGrid();
        init();
        //pillarShape1();

        // Generate the maze, unless we are in demo mode, in which case
        // we will wait until the started () method (when the user clicks run)

    }

    /**
     * Generate the Maze. This includes setting up the start point for the algorithm, running the algorithm,
     * and placing a start and end blocks.
     */
    private void init(){
        // Start approximately in the middle.
        // You can start on any RoomBlock 
        int startX = blocksWide/2, startY = blocksWide / 2;
        startX = startX % 2 == 0? startX+1: startX;
        startY = startY % 2 == 0? startY+1: startY;
        // Time generation time
        long startTime = System.nanoTime();

        // Run the generation algorithm
        boolean success = prims(startX, startY);//prims(startX,startY);

        if (!success){
            System.out.println("Map Gen Failed!");
            return;
        }
        long duration = System.nanoTime() - startTime;

        // Report generation time if desired
        // System.out.println("Generated a Maze size " + blocksWide + " x " + blocksHigh + " in " + (duration/1000000.0) + " ms.");

        // Set start and end blocks
        startBlock = theGrid[1][1];
        endBlock = theGrid[blocksWide-2][blocksHigh-2];
        ((RoomBlock)startBlock).setStartBlock();
        ((RoomBlock)endBlock).setEndBlock();
        playerBlock = theGrid[startX][startY];
    }

    public Block getPlayerBlock (){
        return playerBlock;
    }

    public void updatePlayerPosition (Coordinate playerPosition, Maze m){
        playerX = playerPosition.getX();
        playerY = playerPosition.getY();
        playerBlock = m.getBlock(playerX, playerY);
    }

    private void pillarShape1 (){
        addPillarRectangle (blocksWide / 2 - 2, blocksHigh / 2 - 2, 5, 5);
        addPillarRectangle (4, 4, 4, 4);
        addPillarRectangle (blocksWide - 8, blocksHigh - 8, 4, 4);
        addPillarRectangle (blocksWide / 2, 1, 1, 12);
        addPillarRectangle (30, 10, 1, 20);
    }

    // Uses cell number,centers within cell based on m's size
    public void addMazeEntityCoord (MazeEntity m, int x, int y){
        insertMazeEntity(m, getXCoordinate(x) , getYCoordinate(y));
    }

    // Add to the Maze's SuperWindow via x and y raw coordinates (on MazeWindow, not world)
    public void addMazeEntity (MazeEntity m, int x, int y){

        int halfHeight = m.getImage().getHeight()/2;
        int halfWidth = m.getImage().getWidth()/2;
        insertMazeEntity(m, x, y );// x + halfHeight, y + halfWidth);
    }

    private void insertMazeEntity (MazeEntity m, int x, int y){
        entities.add(m);
        if (m.getLightStrength() > 0){
            lightEmitters.add(m);
        }
        mazeFrame.addObject(m, x, y);
        //mazeFrame.updatePositions();
    }

    public void removeMazeEntity(MazeEntity m){
        entities.remove(m);

        if (m.getLightStrength() > 0){
            lightEmitters.remove(m);
            refreshLight();
        }
        mazeFrame.removeObject(m);
    }

    public void updateLightGrid (int sourceX, int sourceY, int lightStrength, double lightIntensity){
        for (int y = 0; y < blocksHigh; y++){
            for (int x = 0; x < blocksWide; x++){
                double distFromSource = Math.hypot (x - sourceX, y - sourceY);
                // clamp light grid between existing light (previous applied this run,
                // as all was darkened between runs) and newly shined light, so it
                // ends up with the brightest value but not additive
                if (DISABLE_SHADERS){
                    shadeGrid[x][y].applyShade (1.0);
                }else {
                    lightGrid[x][y] = Math.max(lightGrid[x][y],Math.max(0, (lightStrength - distFromSource)/lightStrength) * lightIntensity);

                    shadeGrid[x][y].applyShade (lightGrid[x][y]);
                }
            }
        }

    }

    public void refreshLight (){

        for (int y = 0; y < blocksHigh; y++){
            for (int x = 0; x < blocksWide; x++){
                lightGrid[x][y] = 0;
            }
        }
        //System.out.println("Attempting to apply light from " + lightEmitters.size() + " sources.");
        for (MazeEntity light : lightEmitters){
            updateLightGrid(light.getMazeX(), light.getMazeY(), light.getLightStrength(), light.getLightIntensity());
        }
    }

    public void testShade () {
        for (int y = 0; y < blocksHigh; y++){
            for (int x = 0; x < blocksWide; x++){
                //System.out.println("Shading at " + x + ", " + y);
                //theGrid [x][y].applyShade ((double)x / blocksWide);       
            }
        }
    }

    /**
     * Prepare a Grid for Prim algorithm.
     * 
     * - Blocks around the outside are Posts'
     * - Remaining Blocks where both x and y are even are Posts
     * - Remaining Blocks where both x and y are odd are Rooms
     * - All Remaining Blocks are Walls
     * - (Posts never move, Walls may be removed, Rooms are open spaces)
     */
    private void prepareGrid () {
        // flood blocks
        for (int y = 0; y < blocksHigh; y++){
            for (int x = 0; x < blocksWide; x++){
                Block b;
                // Put a unmovable Post on every edge square as well as every every (even, even) square
                if (x == 0 || y == 0 || x == blocksWide - 1 || y == blocksHigh - 1 || y % 2 == 0 && x % 2 == 0){
                    b = new PostBlock(x, y);

                } else if (y % 2 == 1 && x % 2 == 1){ // where y and x are both odd, make a room
                    b = new RoomBlock(x, y);
                }
                else { // All remaining Blocks will be (removable) Wall Blocks
                    b = new WallBlock(x, y);
                }
                theGrid[x][y] = b;
                lightGrid[x][y] = 0.0;
                shadeGrid[x][y] = new ShadeBlock(x, y);
                shadeGrid[x][y].setBlack();
                shades.add(shadeGrid[x][y]);
                mazeFrame.addObject(theGrid[x][y], getXCoordinate(x), getYCoordinate(y));
                mazeFrame.addObject(shadeGrid[x][y], getXCoordinate(x), getYCoordinate(y));
            }
        }
    }

    private void addPillarRectangle (int startX, int startY, int width, int height){
        for (int y = startY; y < startY + height; y++){
            for (int x = startX; x < startX + width; x++){
                try{
                    mazeFrame.removeObject(theGrid[x][y]);
                    System.out.println("Make a Post at: " + x + ", " + y);
                    theGrid[x][y] = new PostBlock(x, y);
                    mazeFrame.addObject(theGrid[x][y], getXCoordinate(x), getYCoordinate(y));
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println(x + ", " + y + " is invalid. Watch your array bounds");
                }
            }
        }
    }

    /**
     * Mr. Cohen's Implementation of Prim's Algorithm for Maze Building on a 
     * Grid of Square Actor Blocks! 
     * 
     * Credit to:
     * 
     * The succinct explanation at: https://www.gamedeveloper.com/programming/algorithms-for-making-more-interesting-mazes
     * 
     * Check it out for a variety of ways to vary both the setup and execution of this algorithm (for example, with other starting
     * grids or other starting squares for the algorithm).
     */
    private boolean prims (int startX, int startY) {
        int x = startX;
        int y = startY;

        // Check for invalid states:
        if (theGrid.length % 2 == 0 || theGrid[0].length % 2 == 0 || startX > theGrid.length-1 || startY > theGrid[0].length || x < 1 || y < 1){
            System.out.println("Prim Algorithm: Invalid Parameters - Please read the notes!");
            //Greenfoot.stop();
            return false;
        }

        ArrayList<WallBlock> walls = new ArrayList<WallBlock>();

        walls.addAll(getRoomWalls(x, y));
        RoomBlock firstRoom = (RoomBlock)theGrid[x][y];

        firstRoom.visit();

        while (walls.size() > 0){
            // Choose a random wall

            // Each run of the algorithm, a wall block is chosen from the list. 
            // This can be done randomly (as in MODE == 0) or in a biased way to 
            // alter generation (as in MODE == 1)
            WallBlock procBlock;

            if (MODE == 0){ // Standard mode => Random
                int rand = Greenfoot.getRandomNumber(walls.size());
                procBlock = walls.get(rand);
            } else if (MODE == 1){ // Experimenting - different ways to choose the next block
                int rand = Greenfoot.getRandomNumber(10);
                int max = walls.size() - 1;
                int target;
                if (rand == 0){
                    target = 0;
                } else if (rand <= 2) {
                    target = 1;
                } else if (rand <= 5) {
                    target = 0;
                } else {
                    target = max;
                }
                target = Math.min (target, max);
                procBlock = walls.get(target);
            }

            ArrayList<RoomBlock> adjacentRooms = getWallAdjacentRooms(procBlock.getMazeX(), procBlock.getMazeY());

            if (adjacentRooms.size() == 2){
                int unvisitedCount = 0;
                RoomBlock unvisitedRoom = null;
                for (RoomBlock r : adjacentRooms){
                    if (!r.visited()){
                        unvisitedCount++;
                        unvisitedRoom = r;
                        //r.visit();
                    }
                }
                if (unvisitedRoom != null && unvisitedCount == 1){
                    unvisitedRoom.visit();
                    //path.add(unvisitedRoom);
                    walls.addAll(getRoomWalls(unvisitedRoom.getMazeX(), unvisitedRoom.getMazeY()));
                    procBlock.open();
                }
            }
            walls.remove(procBlock);
            if (DEMO_ALGORITHM){
                //repaint(); // This method will redraw the screen even before the act ends, causing one act to go on a LONG time while this generates
            }
        }

        return true;
    }

    public int getBlockSize(){
        return blockSize;
    }

    public Block[][] getGrid (){
        return theGrid;
    }

    private ArrayList<WallBlock> getRoomWalls (int x, int y){
        ArrayList<WallBlock> walls = new ArrayList<WallBlock>();
        if (theGrid[x-1][y] instanceof WallBlock){
            walls.add((WallBlock)theGrid[x - 1][y]);
        }
        if (theGrid[x+1][y] instanceof WallBlock){
            walls.add((WallBlock)theGrid[x + 1][y]);
        }
        if (theGrid[x][y-1] instanceof WallBlock){
            walls.add((WallBlock)theGrid[x][y - 1]);
        }
        if (theGrid[x][y+1] instanceof WallBlock){
            walls.add((WallBlock)theGrid[x][y + 1]);
        }
        return walls;
    }

    private ArrayList<RoomBlock> getWallAdjacentRooms (int x, int y){
        ArrayList<RoomBlock> rooms = new ArrayList<RoomBlock>();
        if (x > 1 && theGrid[x-1][y] instanceof RoomBlock){ // room to the left
            rooms.add ((RoomBlock)theGrid[x - 1][y]);
        }
        if (x <= blocksWide - 3 && theGrid[x+1][y] instanceof RoomBlock){ // room to the right
            rooms.add ((RoomBlock)theGrid[x + 1][y]);
        } 
        if (y > 1 && theGrid[x][y-1] instanceof RoomBlock){ // room above
            rooms.add ((RoomBlock)theGrid[x][y-1]);
        }
        if (y <= blocksHigh - 3 && theGrid[x][y+1] instanceof RoomBlock){
            rooms.add ((RoomBlock)theGrid[x][y+1]);
        }

        return rooms;
    }

    public Block getBlock (int x, int y){
        return theGrid[x][y];
    }

    public Block getStartBlock () {
        return startBlock;
    }

    public Block getEndBlock() {
        return endBlock;
    }

    public int getXCoordinate (int cellNumber){
        //return (cellNumber * blockSize) + X_OFFSET;
        return (cellNumber * blockSize) + halfBlock;
    }

    public int getXCell(int coordinate){
        return (coordinate - xOffset) % blockSize;
    }

    public int getYCoordinate (int cellNumber){
        // return (cellNumber * blockSize) + Y_OFFSET;
        return (cellNumber * blockSize) + halfBlock;
    }

    public int getYCell(int coordinate){
        return (coordinate - yOffset) % blockSize;
    }

    public int getXOffset () {
        return mazeFrame.getXOffset();
    }

    public int getYOffset() {
        return mazeFrame.getYOffset();
    }
}
