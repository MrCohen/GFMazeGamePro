import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A Grenade that bounces off Walls and explodes after a setd.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Grenade extends Projectile
{
    private static final int SIZE = 12;

    private static final int EXPLOSION_FRAMES = 10;
    private static final double EXPLOSION_GROWTH = 1.24;

    private GreenfootImage[] explosionImages;

    private int countDown;
    public Grenade (Maze m, double dX, double dY, double speed, Class targetType, Color color, int angle, MazeMover shooter){
        super (m, dX, dY, targetType, shooter);
        damage = 3;
        maxDistance = 4;
        subjectToDrag = false; // going to try my own drag on speed instead
        destroyOnWall = false;

        // add some randomness to the angle
        int angleError = Greenfoot.getRandomNumber(15)-7;
        angle += angleError;
        setRotation (angle);
       // enableStaticRotation();
       
       this.speed = speed * GameWorld.BLOCK_SIZE; // now a parameter to allow hold-to-charge distance calculation
        //speed = GameWorld.BLOCK_SIZE * 6;
        System.out.println("Added grenade... parameter speed: " + speed + " calculated speed: " + this.speed);
        drag = 0.97;

        // will I need these?
        xDrag = true;
        yDrag = true;

        lightStrength = 3;
        lightIntensity = 0.35;

        countDown = 90;

        double radians = Math.toRadians(getPreciseRotation());
        dX = Math.cos(radians) * (speed * GameWorld.getDuration());
        dY = Math.sin(radians) * (speed * GameWorld.getDuration());

        this.color = color;

        image = new GreenfootImage (SIZE, SIZE);
        image.setColor (color);
        image.fillOval (0, 0, image.getWidth()-1, image.getHeight()-1);
        image.setColor (Color.BLACK);
        image.drawOval (0, 0, image.getWidth()-1, image.getHeight()-1);
        predrawExplosion();
        setImage(image);
    }

    /**
     * Act - do whatever the Grenade wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {

        countDown--;
        if (countDown == -12){
            maze.removeMazeEntity(this);
        }

        if (countDown <= 0)
        {
            // explode!
            //image.scale((int)(image.getWidth() * 1.18), (int)(image.getHeight() * 1.18));
            setImage(explosionImages[Math.min(-countDown, 9)]);
        }
        if (countDown == -6 || countDown == -11){
            // deal damage twice
            checkTargetCollision();

        }
        if (getWorld() == null){
            return;
        }
        // movement
        speed *= drag;
        double radians = Math.toRadians(getPreciseRotation());
        dX = Math.cos(radians) * (speed * GameWorld.getDuration());
        dY = Math.sin(radians) * (speed * GameWorld.getDuration());
        
        applyMove();
    }

    public void predrawExplosion () {
        explosionImages = new GreenfootImage [EXPLOSION_FRAMES];
        explosionImages[0] = new GreenfootImage(image);
        for (int i = 1; i < explosionImages.length; i++){
            int newSize = (int)(explosionImages[i-1].getWidth() * EXPLOSION_GROWTH);
            explosionImages[i] = new GreenfootImage (newSize, newSize);

            explosionImages[i].setColor (color);
            explosionImages[i].fillOval (0, 0, explosionImages[i].getWidth()-1, explosionImages[i].getHeight()-1);
            explosionImages[i].setColor (Color.BLACK);
            explosionImages[i].drawOval (0, 0, explosionImages[i].getWidth()-1, explosionImages[i].getHeight()-1);
        }

    }
}
