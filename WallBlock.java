import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class WallBlock here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class WallBlock extends Block
{
    // private static GreenfootImage[] cachedImages;

    public WallBlock(int mazeX, int mazeY){
        super(false, mazeX, mazeY);
        image = new GreenfootImage(blockSize, blockSize);
        if (!GameWorld.RENDER_TILES){
            image.setColor(Color.DARK_GRAY);
            image.fill();
        } else {
            image.drawImage(new GreenfootImage("brickTile0.png"), 0, 0);
        }
        if (Maze.SHOW_CELL_BORDERS){
            image.setColor(Color.BLACK);
            image.drawRect(0,0, blockSize - 2, blockSize-2);
        }
        setImage(image);

    }

    public void open () {
        passable = true; 
        if (!GameWorld.RENDER_TILES){
            image.setColor(Color.WHITE);
            image.fill();
        } else {
            image.drawImage(new GreenfootImage("grassTile9.png"), 0, 0);
        }
        if (Maze.SHOW_CELL_BORDERS){
            image.setColor(Color.BLACK);
            image.drawRect(0,0, blockSize - 2, blockSize-2);
        }
        // updateShades(images);
        setImage(image);
    }

    public boolean isPassable(){     
        return passable;
    }

}
