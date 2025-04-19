import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RoomBlock here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoomBlock extends Block
{

    private boolean visited;

    public RoomBlock (int mazeX, int mazeY){

        super(true, mazeX, mazeY);
        image = new GreenfootImage(blockSize, blockSize);
        visited = false;
        if (!GameWorld.RENDER_TILES){
            image.setColor(Color.WHITE);
            image.fill();
        }else{
            image.drawImage(new GreenfootImage("grassTile9.png"), 0, 0);
        }
        if (Maze.SHOW_CELL_BORDERS){
            image.setColor(Color.BLACK);
            image.drawRect(0,0, blockSize - 2, blockSize-2);
        }
        setImage(image);
    }

    public void visit(){
        Thread thread = Thread.currentThread();
        visited = true;
    }

    public boolean visited () {
        return visited;
    }

    /**
    public static void init () {
    cachedImages = new GreenfootImage[shades];
    cachedImages[0] = new GreenfootImage(blockSize, blockSize);
    cachedImages[0].setColor(Color.WHITE);
    cachedImages[0].fill();
    if (Maze.SHOW_CELL_BORDERS){
    cachedImages[0].setColor(Color.BLACK);
    cachedImages[0].drawRect(0,0, blockSize, blockSize);
    }
    updateShades(cachedImages);
    }*/

    public void setStartBlock (){
        image.setColor(Color.RED);
        image.fill();
        if (Maze.SHOW_CELL_BORDERS){
            image.setColor(Color.BLACK);
            image.drawRect(0,0, blockSize - 2, blockSize-2);
        }
        //updateShades(images);
        setImage(image);
    }

    public void setEndBlock (){
        image.setColor(Color.GREEN);
        image.fill();
        if (Maze.SHOW_CELL_BORDERS){
            image.setColor(Color.BLACK);
            image.drawRect(0,0, blockSize - 2, blockSize-2);
        }
        //updateShades(images);
        setImage(image);
    }

}
