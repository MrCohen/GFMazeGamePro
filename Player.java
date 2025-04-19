import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Write a description of class Player here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Player extends MazeMover
{

    private GameWorld gameWorld;

    //private static final double ACCEL = 0.15;
    //private static final double MAX_SPEED = Maze.BLOCK_SIZE / 8.0;
    //private static final double DRAG = 0.05;
    private static final double SIZE_RATIO = 0.66;
    private static final double SPEED_RATIO = 1.0/10.0;
    private static final double DRAG_RATIO = 1.0/120.0;

    private boolean grenades;
    private double accel, maxSpeed;
    private int energy, maxEnergy;
    private double grenCharge, grenChargePerAct, grenMaxCharge;

    private SuperStatBar playerStats;
    private SuperStatBar chargeBar;
    private GreenfootImage images[];
    private GreenfootImage faceImages[];

    /**
     * Constructor for class Player
     * 
     * Builds the Player-managing mostly static Player class
     */
    public Player () {
        int playerSize = (int)(blockSize * SIZE_RATIO);
        // ensure player size is even... works better.. not sure why
        playerSize = playerSize % 2 == 0 ? playerSize : playerSize - 1;

        color = Color.YELLOW;

        grenMaxCharge = 12.0; // If held for one second, max force will be 12 for grenades
        grenChargePerAct = grenMaxCharge / 60.0;
        grenCharge = 0;
        // draw image
        //if (!GameWorld.RENDER_TILES){

        image = new GreenfootImage(playerSize, playerSize);

        image.setColor(color);
        image.fill();
        image.setColor(Color.BLACK);
        image.drawRect(0, 0, image.getWidth()-1, image.getHeight()-1);
        setImage(image);

        // }
        // else {
        //image = new GreenfootImage("player.png");
        ////    setImage(image);
        // }

        // 0 - Right,  1 - Down,  2 - Left,  3 - Up,  4 - IDLE
        //images = new GreenfootImage[]{new GreenfootImage("player_lr.png"), new GreenfootImage("player_ud.png"), new GreenfootImage("player_lr.png"), new GreenfootImage("player_ud.png"), new GreenfootImage("player_idle.png") };

        // Right
        //images[0].drawImage(new GreenfootImage("face_right.png"), 8, 8);
        // Down
        //images[1].drawImage(new GreenfootImage("face_foreward.png"), 8, 8);
        // Left
        //images[2].drawImage(new GreenfootImage("face_left.png"), 8, 8);
        // Up
        //images[3].drawImage(new GreenfootImage("face_backward.png"), 8, 8);
        // Idle
        //images[4].drawImage(new GreenfootImage("face_foreward.png"), 8, 8);

        //faceImages = new GreenfootImage[]{new GreenfootImage("face_foreward.png"),new GreenfootImage("face_left.png"),new GreenfootImage("face_backward.png"), new GreenfootImage("face_right.png")};
        //image = images[0];
        setImage(image); // set idle image

        grenades = false;
        // prepare and cache
        dX = 0;
        dY = 0;
        //halfBlock = blockSize / 2;
        halfMe = playerSize / 2;

        // game state info
        maxHp = 50;
        hp = maxHp;
        maxEnergy = 100;
        energy = 100;

        SuperWindow actionBarWindow = GameWorld.getActionBarWindow();
        playerStats = new SuperStatBar (new int[]{maxHp, maxEnergy},  new int[]{hp, energy}, null, actionBarWindow.getWidth()/2, 46, 0, new Color[]{new Color(0,255,33), new Color(255, 216, 0 )}, new Color[]{new Color(255,0,18), new Color(104,97,60)}, false, Color.DARK_GRAY, 2);
        actionBarWindow.addObject(playerStats, actionBarWindow.getWidth() - playerStats.getImage().getWidth()/2-6, actionBarWindow.getHeight() - playerStats.getImage().getHeight()/2-5);

        chargeBar = new SuperStatBar ((int)grenMaxCharge,  (int)Math.ceil(grenMaxCharge - grenCharge), this, playerSize - 2, 4, playerSize/2-2, Color.MAGENTA, Color.BLUE, true, Color.BLACK, 1);
        maze.addMazeEntity(chargeBar, 0, 0);
        
        
        // Maze Mover
        lightStrength = 6;
        lightIntensity = 0.85;
        destroyOnWall = false;
        subjectToDrag = true;
        xDrag = false;
        yDrag = false;

        // Movement related
        maxSpeed = blockSize * SPEED_RATIO;
        accel = maxSpeed * 2;
        drag = 0.90; //blockSize * DRAG_RATIO;
        updateSize();
    }

    public void addedToWorld (World w){
        //Block b = maze.getStartBlock();
        //setLocation (b.getX(), b.getY());
        gameWorld = (GameWorld)w;
        //w.addObject(chargeBar, 0, 0); // will go to player's position minus offset
    }

    /**
     * Act - do whatever the Player wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        
        checkKeys();
        applyMove();

    }    

    @Override
    public void hitMe (int damage){
        super.hitMe(damage);
        playerStats.update(new int[]{hp, energy});
    }

    public void healMe (int amount){
        hitMe (-amount);
    }

    private void checkKeys(){
        // detect if changed on each axis (if not, apply drag on that axis)
        boolean xChanged = false, yChanged = false;
        xDrag = false;
        yDrag = false;
        // use actual delta time for movement --> speeds are per SECOND, dT is the fraction
        // of a second that has elapsed since the previous act.
        double dT = GameWorld.getDuration();
        if (Greenfoot.isKeyDown("a")){
            xChanged = true;
            dX -= accel * dT; 
            if (dX > 0){
                dX -= accel * dT;
            }
            dX = Math.max (-maxSpeed, dX);

        }
        if (Greenfoot.isKeyDown("d")){
            xChanged = true;
            dX += accel * dT;
            if (dX < 0){
                dX += accel * dT;
            }
            dX = Math.min (maxSpeed, dX);

        }
        if (Greenfoot.isKeyDown("w")){
            yChanged = true;
            dY -= accel * dT;
            if (dY > 0){
                dY -= accel * dT;
            }
            dY = Math.max (-maxSpeed, dY);
        } 
        if (Greenfoot.isKeyDown("s")){
            yChanged = true;
            dY += accel * dT;
            if (dY < 0){
                dY += accel * dT;
            }
            dY = Math.min (maxSpeed, dY);
        } 
        /**
        //image.clear();
        if (dX == 0 && dY == 0){

        image = images[4]; // idle

        }
        else if (Math.abs(dX) > Math.abs(dY)){
        if (dX > 0){ // rightward
        image = images[0];
        } else {
        image = images[2];
        }

        }
        else {
        if (dY > 0){
        image = images[1];
        } else {
        image = images[3];
        }
        }
        setImage(image);
         */
        if (!xChanged){
            xDrag = true;
        }
        if (!yChanged){
            yDrag = true;
        }

        if (Greenfoot.isKeyDown("e")){
            if (isTouching(Item.class)){
                Item i = (Item)getOneIntersectingObject(Item.class);
                if (i != null){
                    Item pickedUp = i.pickUpItem();
                    InventoryManager.addItem(pickedUp);
                    //System.out.println("I picked up: " + pickedUp);
                }
            }
        }

        String key = gameWorld.getKey();
        if (key != null){
            if (key.equals("down")){
                // shoot downward
                int xOffset = Greenfoot.getRandomNumber(7) - 3;
                if (grenades){
                    maze.addMazeEntity (new Grenade(maze, 0, 1, grenCharge, Enemy.class, color, 90, this), getX() - maze.getXOffset() + xOffset, getY() - maze.getYOffset()+halfMe);
                } else{
                    maze.addMazeEntity (new Bit(maze, 0, 1, Enemy.class, color, this), getX() - maze.getXOffset() + xOffset, getY() - maze.getYOffset()+halfMe);
                }
            }
            if (key.equals("up")){
                // shoot upward
                int xOffset = Greenfoot.getRandomNumber(7) - 3;
                if (grenades){
                    maze.addMazeEntity (new Grenade(maze, 0, -1, grenCharge, Enemy.class, color, 270, this), getX() - maze.getXOffset() + xOffset, getY() - maze.getYOffset()+halfMe);
                }else{
                    maze.addMazeEntity (new Bit(maze, 0, -1, Enemy.class, color, this), getX() - maze.getXOffset() + xOffset, getY() - maze.getYOffset()-halfMe);
                }
            }
            if (key.equals("left")){
                // shoot leftward
                int yOffset = Greenfoot.getRandomNumber(7) - 3;
                if (grenades){
                    maze.addMazeEntity (new Grenade(maze, -1, 0,grenCharge, Enemy.class, color, 180, this), getX() - maze.getXOffset() - halfMe, getY() - maze.getYOffset() + yOffset);
                } else{
                    maze.addMazeEntity (new Bit(maze, -1, 0, Enemy.class, color, this), getX() - maze.getXOffset() - halfMe, getY() - maze.getYOffset() + yOffset);
                }
            }
            if (key.equals("right")){
                // shoot rightward
                int yOffset = Greenfoot.getRandomNumber(7) - 3;
                if (grenades){
                    maze.addMazeEntity (new Grenade(maze, 1, 0,grenCharge, Enemy.class, color, 0, this), getX() - maze.getXOffset() + halfMe, getY() - maze.getYOffset() + yOffset);
                } else{
                    maze.addMazeEntity (new Bit(maze, 1, 0, Enemy.class, color, this), getX() - maze.getXOffset() + halfMe, getY() - maze.getYOffset() + yOffset);
                }
            }
            if (key.equals("g")||key.equals("G")){
                grenades = !grenades;
            }

            // Use Currently Selected Item
            if (key.equals("r")||key.equals("R")){
                // This was causing issues on Gallery --> Changed it to peek but why was 
                // it getting called? I wasn't pressing R or r ... ??
                if (InventoryManager.peekSelectedAction().canBeUsed()){
                    // use it??
                }
            }

            // Place Currently Selected Item
            if (key.equals("f")||key.equals("F")){
                System.out.println("getting qty");
                if (InventoryManager.peekSelectedAction()!= null && InventoryManager.peekSelectedAction().canBePlaced() && InventoryManager.getSelectedActionQuantity() > 0){
                    System.out.println("detected F");
                    MazeEntity me = (Item)InventoryManager.getSelectedAction().place();
                    maze.addMazeEntityCoord(me, mazeX, mazeY);

                }
            }

            // Check Action Bar keys
            for (int i = 0; i < InventoryManager.ACTION_KEYS.length; i++){

                String temp = InventoryManager.ACTION_KEYS[i];
                if (Character.isLetter(key.charAt(0))){
                    temp = temp.toLowerCase();
                }

                if (key.equals(temp)){
                    InventoryManager.selectAction(i);
                    break;
                }
            }

        }
        // Charge grenade speed/distance when holding an arrow
        // This has to go AFTER the getKey() checks because otherwise
        // there is a one-act gap between charging and shooting, which causes a reduced fire rate
        if (grenades && (Greenfoot.isKeyDown("up") || Greenfoot.isKeyDown("down") || Greenfoot.isKeyDown("left") || Greenfoot.isKeyDown("right"))){
            grenCharge = Math.min(grenCharge + grenChargePerAct, grenMaxCharge);
            // System.out.println(grenCharge);
            
        } else {
            //System.out.println("reduced");
            //grenCharge=Math.max(0,grenChargePerAct);
            grenCharge = 0;
            
        }
        chargeBar.update((int)(grenMaxCharge - grenCharge));
    }

}
