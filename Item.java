import greenfoot.*;

/**
 * Base class for all pick-up / place-able objects.
 *
 *  •  Three visual forms
 *       - worldImage      – actor on the maze floor or a placed object
 *       - inventoryImage  – transparent icon for inventory / action-bar
 *       - itemSprite      – raw sprite with no padding
 *
 *  •  Behaviour flags are **instance** fields – no more shared static values.
 */
public abstract class Item extends MazeEntity implements iAction {

    /* ?? behaviour flags ??????????????????????????????????????????? */
    protected final boolean canBePlaced;
    protected final boolean canBeUsed;
    protected final boolean canStack;
    protected final int     stackSize;

    /* ?? sprites ??????????????????????????????????????????????????? */
    protected final GreenfootImage itemSprite;     // raw asset (no border)
    protected final GreenfootImage worldImage;     // border + background
    protected final GreenfootImage inventoryImage; // transparent, padded

    /* ?? meta data ????????????????????????????????????????????????? */
    protected String name;
    protected String description;
    protected int    quantity;

    /* ?? ctor ?????????????????????????????????????????????????????? */
    public Item(GreenfootImage sprite,  boolean canPlace, boolean canUse,  boolean canStack, int stackSize, String name, String description, boolean drawBorderInitially)
    {
        this.canBePlaced = canPlace;
        this.canBeUsed   = canUse;
        this.canStack    = canStack;
        this.stackSize   = stackSize;

        this.name        = name;
        this.description = description;
        this.quantity    = 1;

        this.itemSprite      = new GreenfootImage(sprite);
        this.worldImage      = buildWorldImage(sprite);     // green-bordered
        this.inventoryImage  = buildInventoryIcon(sprite);  // transparent

        this.image = drawBorderInitially ? worldImage : inventoryImage;
        setImage(this.image);
    }

    /* ??????????????????? static bootstrap for ALL items ??????????? */
    public static void init() {
        Torch.init();       // add other item types here later
    }

    /* ?? behaviour queries (instance safe) ????????????????????????? */
    public boolean canBePlaced() { return canBePlaced; }

    public boolean canBeUsed()   { return canBeUsed; }

    public boolean canStack()    { return canStack; }

    public int     getStackSize(){ return stackSize; }

    public int     getQuantity() { return quantity; }

    public boolean isWeapon()    { return false; }

    public String  getName()        { return name; }

    public String  getDescription() { return description; }

    /* ?? sprite accessors ?????????????????????????????????????????? */
    public GreenfootImage getIcon()            { return new GreenfootImage(inventoryImage); }

    public GreenfootImage getInventoryImage()  { return new GreenfootImage(inventoryImage); }

    public GreenfootImage getItemImage()       { return new GreenfootImage(itemSprite); }

    /*  Called by InventoryManager after the actor has been placed
     *  in a window.  Switch to the transparent icon.                */
    public void setInventoryImage() {
        this.image = inventoryImage;
        setImage(this.image);

    }

    /*  Variant kept for backward compatibility – lets caller supply
     *  a modified image (eg. power stamp).                           */
    public void setInventoryImage(GreenfootImage img) {
        inventoryImage.drawImage(img, 0, 0);
        this.image = inventoryImage;
        setImage(this.image);

    }

    /* ?? drag/drop helper ?????????????????????????????????????????? */
    public ActionBarButton getDropBar() {
        if (!isTouching(ActionBarButton.class)) return null;
        return (ActionBarButton) getOneIntersectingObject(ActionBarButton.class);
    }

    /* ?? WORLD vs INVENTORY image builders ????????????????????????? */
    private static final int ICON_SIZE = GameWorld.ITEM_SIZE;

    /** transparent, centred, no border */
    private GreenfootImage buildInventoryIcon(GreenfootImage src) {
        GreenfootImage img = new GreenfootImage(ICON_SIZE, ICON_SIZE);
        img.clear();                                          // transparent
        int x = (ICON_SIZE - src.getWidth())  / 2;
        int y = (ICON_SIZE - src.getHeight()) / 2;
        img.drawImage(src, x, y);
        return img;
    }

    /** green-bordered square for floor/placed view */
    private GreenfootImage buildWorldImage(GreenfootImage src) {
        GreenfootImage img = new GreenfootImage(src.getWidth() + 4,
                src.getHeight() + 4);
        img.setColor(backgroundColor);
        img.fill();
        img.setColor(borderColor);              // borderColor from MazeEntity
        img.drawRect(0, 0, img.getWidth() - 1, img.getHeight() - 1);
        img.drawRect(1, 1, img.getWidth() - 3, img.getHeight() - 3);
        img.drawImage(src, 2, 2);
        return img;
    }

    /* ??????????????????????????????????????????????????????????????
     *  Detach this Item from the maze and return a reference that
     *  InventoryManager can store/stack.  (The caller then does
     *      InventoryManager.addItem( item.pickUpItem() );
     *  or similar.)
     * ???????????????????????????????????????????????????????????? */
    public Item pickUpItem() {
        if (maze != null) {             // safety: only if we are in a maze
            maze.removeMazeEntity(this);
        }
        setInventoryImage();            // switch to icon graphic
        return this;
    }

    /* ?? optional power/quantity stamping utility ????????????????? */
    public static GreenfootImage stampPower(Item item, int value) {
        GreenfootImage img = item.getInventoryImage();
        img.setFont(new Font("Verdana", true, false, 14));
        img.setColor(Color.WHITE);
        img.fillRect(0, GameWorld.ITEM_SIZE - 15, GameWorld.ITEM_SIZE / 3, 15);
        img.setColor(Color.BLACK);
        img.drawRect(0, GameWorld.ITEM_SIZE - 15, GameWorld.ITEM_SIZE / 3, 15);
        img.setColor(Color.BLACK);
        img.drawString((value >= 0 ? "+" : "") + value,
            GameWorld.ITEM_SIZE - 12, GameWorld.ITEM_SIZE / 4 - 1);
        return img;
    }

    /* ?? equality / hashing ??????????????????????????????????????? */
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item i)) return false;
        return name.equals(i.name);
    }

    @Override public int hashCode() { return name.hashCode(); }

    /* ?? abstract API that concrete items must implement ?????????? */
    public abstract Item place();

    public abstract Item use();

    public abstract void attack(MazeEntity shooter, int direction, Class target);
}
