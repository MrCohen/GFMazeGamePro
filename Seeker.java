import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.LinkedList;
/**
 * Write a description of class Seeker here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Seeker extends Enemy
{
    private LinkedList<Block> pathToPlayer;
    private GreenfootImage baseImage, sprintImage;
    private Coordinate direction; // consider this a poorly named Vector
    private Block currentBlock;
    private Coordinate destination; 
    private Block destBlock;
    private int mazeX, mazeY;
    private int shootingCooldown, shootingMaxCooldown;
    private double speed;
    private double baseSpeed;
    private int energy, maxEnergy;
    private int statBarHeight;
    private boolean moving, waiting, shooting;
    private boolean speedBoostActive;
    private int stagger;
    public Seeker () {
        super(Color.PINK);
        baseSpeed = Greenfoot.getRandomNumber (20) + 10;
        speed = baseSpeed;
        statBarHeight = image.getHeight() / 4;
        maxHp = 5;
        hp = maxHp;
        maxEnergy = 10;
        energy = maxEnergy;
        direction = new Coordinate (0,0);
        statBar = new SuperStatBar (new int[]{maxHp, maxEnergy},  new int[]{hp, energy}, null, this.getImage().getWidth()-2, statBarHeight, 0, new Color[]{new Color(0,255,33), new Color(255, 216, 0 )}, new Color[]{new Color(255,0,18), new Color(104,97,60)}, false, Color.DARK_GRAY, 0);
        // Choose a random frame 0-60 so all Seekers don't re-path on the same act
        stagger = Greenfoot.getRandomNumber(60);
        //new SuperStatBar  (maxHp,  hp, null, this.getImage().getWidth()-2, statBarHeight, 0, Color.GREEN, Color.RED, false);
        //refreshStatBar();
        moving = true;
        waiting = false;
        shooting = false;
        name = "Seeker";
        speedBoostActive = false;

        shootingMaxCooldown = 10;
        shootingCooldown = 10;
    }

    public void act () {
        if (!speedBoostActive && energy < maxEnergy && GameWorld.getActCount() % 40 == 0){
            energy++;
        }
        refreshStatBar();
        currentBlock = (Block) getOneObjectAtOffset(0,0,Block.class);
        if (GameWorld.getActCount() == 0){
            seekPlayer(4);
        }
        if (moving){
            boolean turned = doMove();

            // Check in my current direction for a straight shot at the player:
            if (GameWorld.getActCount() % 20 == 0 || turned){
                boolean foundPlayer = seekPlayer (4);
                if (foundPlayer){
                    // Change from moving state to shooting state
                    moving = false;
                    shooting = true;
                } 
            }
        } else if (shooting){
            doShoot();
        }
    }

    private boolean seekPlayer (int maxDistance) {
        Block b = null;
        int distance = 1;
        boolean found = false;
        while (!found && (direction.getX() != 0 || direction.getY() != 0)){
            //System.out.println("Looking offset at x: " + direction.getX() * blockSize * distance + ", y: " + direction.getY() * blockSize * distance);
            // check for the player every block until I hit an impassible Block
            b = (Block)getOneObjectAtOffset (direction.getX() * blockSize * distance, direction.getY() * blockSize * distance, Block.class);
            if (b.isBlocked()){
                return false; // exit with foundPlayer still false
            } else if (b.touchingPlayer()){
                found = true;
            }
            distance++;
            if (distance > maxDistance) return false;
        }
        return found;
    }

    private boolean doMove () {
        boolean turning = false;
        //System.out.println((GameWorld.getActCount() + stagger) % 90);
        // Retarget when no path, reached end of path, don't have a destination block OR once every 90 acts, staggered.x
        if (pathToPlayer == null || pathToPlayer.size()==0 || destBlock == null || (GameWorld.getActCount() + stagger) % 90 == 0){
            // get next destination
            getNextDestination();

        } 

        if (energy > 7 && !speedBoostActive){
            if (pathToPlayer != null && pathToPlayer.size() < 7){
                //System.out.println("Pre - Activating Speed Boost");

                //  System.out.println("Activating Speed Boost");
                setImage(sprintImage);
                speedBoostActive = true;
                speed = baseSpeed * 5;
            }
        }

        if (destBlock != null){
            // in case window moved (even thought it shouldn't)
            destination = new Coordinate (destBlock.getX(), destBlock.getY());
            Coordinate oldDirection = new Coordinate (direction.getX(), direction.getY());
            direction = getDirection (new Coordinate (getX(), getY()), destination);

            if (!oldDirection.equals(direction)){
                turning = true;
            }

            // get act time from the world to apply delta time based movement
            double dT = GameWorld.getDuration();
            // calculate this act's movement based on delta time
            dX = direction.getX() * speed * dT;
            dY = direction.getY() * speed * dT;
            // If I'm very close to my destination, less than one more step at speed, place me there instead and turn me towards my next destination
            if (Math.abs(getX() - destBlock.getX()) <= speed * dT && Math.abs(getY() - destBlock.getY()) <= speed * dT){
                if (speedBoostActive){
                    energy--;
                    if (energy == 0){
                        speedBoostActive = false;
                        setImage(baseImage);
                        speed = baseSpeed;
                    }
                }

                //System.out.println("set");
                setLocation (destBlock.getX(), destBlock.getY());
                destBlock = pathToPlayer.removeFirst();
                destination = new Coordinate (destBlock.getX(), destBlock.getY());
            } //else {  // incluuding this else makes the movement more accurate, including the "corner" step. Leaving it out
            // skips the corner and makes it look smoother, and I like that, so I'm leaving it out.
            applyMove();
        }
        return turning;
    }

    private void doShoot() {
        shootingCooldown--;
        if (shootingCooldown == 0){
            //System.out.println("Calling Shoot");
            shoot();
            shootingCooldown = shootingMaxCooldown;
        }
        if ((GameWorld.getActCount() + stagger) % 20 == 0){
            boolean foundPlayer = seekPlayer (4);
            if (!foundPlayer){
                // Change from moving state to shooting state
                moving = true;
                shooting = false;
            } 
        }
    }

    private void shoot () {
        if (direction.getY() == 1){
            // shoot downward
            int xOffset = Greenfoot.getRandomNumber(7) - 3;
            maze.addMazeEntity (new Bit(maze, 0, 1, Player.class, color, this), getX() - maze.getXOffset() + xOffset, getY() - maze.getYOffset()+halfMe);
            //System.out.println("Shooting Down");
        }
        if (direction.getY() == -1){
            // shoot upward
            int xOffset = Greenfoot.getRandomNumber(7) - 3;
            maze.addMazeEntity (new Bit(maze, 0, -1, Player.class, color, this), getX() - maze.getXOffset() + xOffset, getY() - maze.getYOffset()-halfMe);
            //System.out.println("Shooting Up");
        }
        if (direction.getX() == -1){
            // shoot leftward
            int yOffset = Greenfoot.getRandomNumber(7) - 3;
            maze.addMazeEntity (new Bit(maze, -1, 0, Player.class, color, this), getX() - maze.getXOffset() - halfMe, getY() - maze.getYOffset() + yOffset);
            //System.out.println("Shooting Left");
        }
        if (direction.getX() == 1){
            // shoot rightward
            int yOffset = Greenfoot.getRandomNumber(7) - 3;
            maze.addMazeEntity (new Bit(maze, 1, 0, Player.class, color, this), getX() - maze.getXOffset() + halfMe, getY() - maze.getYOffset() + yOffset);
            //System.out.println("Shooting Right");
        }
    }

    protected void refreshStatBar (){
        statBar.update(new int[]{hp, energy});
        getImage().drawImage (statBar.getImage(), 1, getImage().getHeight() - 1 - statBarHeight);
    }

    protected void drawEnemy(){

        baseImage = new GreenfootImage(blockSize - 8, blockSize - 8);
        sprintImage = new GreenfootImage (baseImage);

        baseImage.setColor(color);
        baseImage.fill();
        baseImage.setColor(Color.BLACK);
        baseImage.drawRect(0, 0, baseImage.getWidth()-1, baseImage.getHeight()-1);
        setImage(baseImage);
        image = new GreenfootImage(baseImage);

        sprintImage.setColor(new Color (255,44,203));
        sprintImage.fill();
        sprintImage.setColor(Color.BLACK);
        sprintImage.drawRect(0, 0, baseImage.getWidth()-1, baseImage.getHeight()-1);

    }
    private void getNextDestination (){
        //System.out.println("I'm a Seeker at " + currentBlock.getMazeX() + ", " + currentBlock.getMazeY() + " and I'm retargeting now");
        Traverser t = new Traverser (maze, currentBlock, maze.getPlayerBlock());
        //System.out.println(this + " at " + currentBlock.getMazeX() + ", " + currentBlock.getMazeY() + " getting path to player...");
        pathToPlayer = t.getPath();
        if (pathToPlayer.size() >= 2){
            // first block is the one I'm on, so discard it:
            pathToPlayer.removeFirst();
            // second block is the first one to point towards
            destBlock = pathToPlayer.removeFirst();

        }
    }

    private Coordinate getDirection (Block a, Block b){
        return getDirection (new Coordinate (a.getX(), a.getY()), new Coordinate (b.getX(), b.getY()));
    }

    private Coordinate getDirection (Coordinate start, Coordinate dest) {
        int xDelta = 0, yDelta = 0;
        if (start.getX() == dest.getX() && start.getY() == dest.getY()){ // No Direction - already on desired Block
            new Coordinate (0,0);
        }

        if (start.getX() > dest.getX()){
            xDelta = -1;
        } else if (start.getX() < dest.getX()){
            xDelta = 1;
        } 

        if (start.getY() > dest.getY()){
            yDelta = -1;
        } else if (start.getY() < dest.getY()){
            yDelta = 1;
        }
        return new Coordinate (xDelta, yDelta);
    } 
}

