import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
/**
 * A Maze Solver.
 * 
 * This can be set up to visually solve the maze (I.e. highlight the path, or materialize as an Actor and walk from start to end)
 * or highlight the solve path, or simply return the solve path.
 * 
 * 0.5.3 --
 * -- Making more modular, allow specified start and goal cells.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Traverser extends Actor
{
    private static final boolean DRAW_TRAIL = true;
    public static final boolean ACT = false;

    private boolean drawTrail, doAct;

    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    private GreenfootImage image;

    // cache for simplicity and performance
    private int halfBlock, halfMe;
    private Maze maze;
    private LinkedList<Coordinate> solvePath;
    private Block[][] grid;
    private boolean[][] visited;

    private Block startBlock, endBlock;
    private int mazeX, mazeY;
    private int direction, xDir, yDir;
    // private ArrayList<Block> path;
    private boolean finished;

    private Block nextBlock;
    private LinkedList<Block> movePath;
    private int moveSpeed;

    public Traverser (Maze m, Block start, Block end){
        this(m);
        startBlock = start;
        endBlock = end;
        drawTrail = false;
        doAct = false;
    }

    public Traverser (Maze m) {
        doAct = ACT;
        drawTrail = DRAW_TRAIL;
        this.maze = m;
        this.grid = m.getGrid();
        int blockSize = grid[0][0].getSize();
        //playerSize = playerSize % 2 == 0 ? playerSize : playerSize - 1;
        if (ACT){
            image = new GreenfootImage(blockSize, blockSize);
            image.setColor(Color.GREEN);
            image.fill();
            image.setTransparency(120);
        }
        else{
            image = new GreenfootImage(1,1);
        }
        setImage(image);
        finished = false;
        halfBlock = blockSize / 2;
        halfMe = getImage().getWidth()/2;
        direction = 0; // right

        // Prepare the Boolean Array - Visited means Visited OR Blocked
        visited = new boolean[grid.length][grid[0].length];
        for (int y = 0; y < grid[0].length; y++){
            for (int x = 0; x < grid.length; x++){
                if (grid[x][y].isPassable()){
                    visited[x][y] = false;
                } else {
                    visited[x][y] = true;
                }
            }
        }
    }

    public LinkedList<Block> getPath (){
        if (startBlock == null){
            startBlock = maze.getStartBlock();
        }
        if (endBlock == null){
            endBlock = maze.getEndBlock();
        }
        mazeX = startBlock.getMazeX();
        mazeY = startBlock.getMazeY();
        //System.out.println("Attempting to solve from : " + startBlock + " to " + endBlock);
        solvePath = solve();
        return getSolvePath();
    }

    /**
     * DONT add Traverser to World unless 
     * 
     */
    public void addedToWorld (World w){

        startBlock = maze.getStartBlock();
        endBlock = maze.getEndBlock();

        mazeX = startBlock.getMazeX();
        mazeY = startBlock.getMazeY();
        
        // Avoid repeating when re-added to World
        if (solvePath == null){
            solvePath = solve();

            GameWorld gw = (GameWorld)w;
            gw.setSolvePath(solvePath);
            //System.out.println("solve path length: " + solvePath.size());
            if (drawTrail){
                for (Coordinate c : solvePath){
                    //System.out.println(c);
                    if (grid[c.getX()][c.getY()] != startBlock && grid[c.getX()][c.getY()] != endBlock){
                        grid[c.getX()][c.getY()].highlight();
                    }
                }

            }
            if (doAct){
                moveSpeed = 3;
                movePath = (LinkedList<Block>)getSolvePath();
                nextBlock = movePath.remove();
                setLocation (nextBlock.getX(), nextBlock.getY());
                nextBlock = movePath.remove();
                turnTowards(nextBlock.getX(), nextBlock.getY());
            }
        }
    }

    public void act () {
        if (ACT){

            if (Math.hypot(getX() - nextBlock.getX(), getY() - nextBlock.getY()) < moveSpeed){
                setLocation (nextBlock.getX(), nextBlock.getY());
                if (!movePath.isEmpty()){
                    nextBlock = movePath.remove();
                    turnTowards(nextBlock.getX(), nextBlock.getY());
                } else {

                    getWorld().removeObject(this);
                    return;
                }
            }
            else {
                move(moveSpeed);
            }
        }
    }

    /**
     * Solve using a Breadth-first search
     */
    private LinkedList<Coordinate> solve () {
        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = new Coordinate (mazeX, mazeY);
        //System.out.println("Starting at: " + start);
        nextToVisit.add(start);
        //System.out.println("Looking for end at : " + endBlock.getMazeX() + ", " + endBlock.getMazeY()); 

        while (!nextToVisit.isEmpty()){
            Coordinate cur = nextToVisit.remove();
            // System.out.println(visited[cur.getX()][cur.getY()] + "===>" + cur);
            //System.out.print("Step! cur = " + cur + ": ");
            if (visited[cur.getX()][cur.getY()] == true){ // blocked or visited are both true
                //System.out.println("Blocked or Visited");
                // System.out.println("BEEN HERE Tick--");
                continue;
            }
            if (cur.getX() == endBlock.getMazeX() && cur.getY() == endBlock.getMazeY()){
                // found the end!
                // BACKTRACK method
                // System.out.println("Found THE END Tick--");
                return backtrack (cur);
            }
            if (isDeadEnd(cur.getX(),cur.getY())){

                visited[cur.getX()][cur.getY()] = true;
                // System.out.println("Dead End Tick--");
                continue;
            }

            for (int[] direction : DIRECTIONS){
                Coordinate coordinate = new Coordinate (cur, cur.getX() + direction[0], cur.getY() + direction[1]);
                nextToVisit.add(coordinate);
                visited[cur.getX()][cur.getY()] = true;
            }
            // System.out.println("End Tick--");
        }
        return null;
    }

    private LinkedList<Coordinate> backtrack (Coordinate cur){
        //System.out.print("BACKTRACK SOLUTION: ");
        LinkedList<Coordinate> path = new LinkedList<Coordinate>();
        Coordinate iter = cur;
        while (iter != null){
            path.add(iter);
            iter = iter.getParent();
        }

        return path;
    }

    public LinkedList<Block> getSolvePath (){
        LinkedList<Block> path = new LinkedList<Block>();

        for (Coordinate c : solvePath){
            path.addFirst(grid[c.getX()][c.getY()]);
        }

        return path;
    }

    private boolean isDeadEnd (int x, int y){
        return visited[x-1][y] == true && visited[x+1][y] == true && visited[x][y - 1] == true && visited[x][y+1] == true; 
    }

    private void turnRight () {
        direction++;
        direction = direction % 4; // 4 becomes 0 again
        if (direction == 0){ // turn to right
            xDir = 1;
            yDir = 0;
        } else if (direction == 1){ // turn to down
            xDir = 0;
            yDir = 1;
        } else if (direction == 2){ // turn to left
            xDir = -1;
            yDir = 0;
        } else if (direction == 3){ // turn to up
            xDir = 0;
            yDir = -1;
        }
    }  

    private int round (double val){
        return (int)(val + 0.5);
    }

    private int absCeil (double val){
        return (int)(val >= 0 ? Math.ceil(val) : Math.floor(val));
    }

}
