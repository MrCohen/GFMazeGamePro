import greenfoot.*;

/** Torch – emits light only when PLACED; otherwise inert. */
public class Torch extends Item {

    private static GreenfootImage SPRITE;

    /** Load sprite once for all torches */
    public static void init() {
        SPRITE = new GreenfootImage("item_torch.png");   // replace with real asset
    }

    /* internal state */
    private final boolean isPlaced;
    private final boolean isOnGround;

    public Torch(boolean isPlaced, boolean isOnGround) {
        super(SPRITE,
              true,            // canBePlaced
              true,            // canBeUsed (e.g., right-click to place)
              true,            // canStack
              99,              // stackSize
              "Torch",
              "A wooden torch that lights the maze when placed.",
              /* draw green border initially? */
              isPlaced || isOnGround);

        this.isPlaced   = isPlaced;
        this.isOnGround = isOnGround;

        if (isPlaced) {            // only placed torches emit light
            lightStrength  = 6;
            lightIntensity = 0.75;
        } else {
            lightStrength  = 0;
            lightIntensity = 0;
        }
    }

    /* ?? iAction behaviour ????????????????????????????????????????? */
    @Override public Torch place() {
        /* consume one from inventory, spawn a PLACED torch actor */
        return new Torch(true, false);
    }

    @Override public Torch use() { return null; }   // not directly “used”
    @Override public void attack(MazeEntity shooter,int dir,Class target){}

    /* convenience getters */
    public boolean isPlaced()   { return isPlaced; }
    public boolean isOnGround() { return isOnGround; }
}
