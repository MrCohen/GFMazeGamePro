import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Item here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class Item extends MazeEntity implements iAction
{
    //protected static GreenfootImage cachedItemImage, cachedMazeImage, cachedInventoryImage;
    protected GreenfootImage itemImage, mazeImage, inventoryImage;
    protected static boolean canBePlaced, canBeUsed, isPlaced, isOnGround, inInventory, canStack;
    protected static int stackSize;
    protected int quantity;
    protected String name, description;
    

    public abstract Item place();

    public abstract Item use();
    
    public abstract void attack (MazeEntity shooter, int direction, Class target);

    /**
     * An item can be isPlaced (active in Maze, doing something), isOneGround or neither (inInventory).
     * 
     * 
     * 
     */

    public Item (GreenfootImage itemImage, boolean isPlaced, boolean isOnGround){
        this.isOnGround = isOnGround;
        this.isPlaced = isPlaced;
        if (!isPlaced && !isOnGround){ // just one less parameter I guess..
            inInventory = true;
        }
        // Draw the World version of the item. 
        image = new GreenfootImage(itemImage.getWidth() + 4, itemImage.getHeight() + 4);
        image.setColor(backgroundColor);
        image.fill();
        image.setColor(borderColor);
        image.drawRect(0,0, image.getWidth() - 1, image.getHeight() - 1);
        image.drawRect(1,1, image.getWidth() - 3, image.getHeight() - 3);
        image.drawImage(itemImage, 2, 2);
        this.image = image;
        itemImage = image;
        setImage(image);
        quantity = 1;
        lightStrength = 0;
        lightIntensity = 0;
    }

    public static void init() {
        Torch.init();
    }

    public boolean canStack(){
        return canStack;
    }

    public boolean isOnGround() {
        return isOnGround;
    }
    
    public boolean canBePlaced() {
        return canBePlaced;
    }

    public int getQuantity () {
        return quantity;
    }

    public boolean isWeapon() {
        return false;  // any weapon would override this with true, be equipped to weapon slot when "used"
    }

    public GreenfootImage getIcon () {
        return new GreenfootImage (inventoryImage);
    }

    public int getStackSize () {
        if (!canBePlaced){
            return 1;
        } else {
            return stackSize;
        }
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public boolean canBeUsed() {
        return canBeUsed;
    }

    // When this is being dragged and is released, see if it is touching
    // an ActionBarButton
    public ActionBarButton getDropBar () {
        if (!isTouching(ActionBarButton.class)){
            return null;
        }
        ActionBarButton abb = (ActionBarButton)getOneIntersectingObject (ActionBarButton.class);
        return abb;
    }

    public Action createAction (String key){
        if (canBeUsed){
            return new Action (this, key);   
        } else {
            return new Action (this, "");
        }
    }

    public Item pickUpItem (){
        maze.removeMazeEntity(this);
        return this;
    }

    public GreenfootImage getMazeImage(){
        if (mazeImage == null){
            return itemImage;
        }
        return mazeImage;
    }

    public void setInventoryImage(GreenfootImage newImage){
        inventoryImage = new GreenfootImage(newImage);
        setInventoryImage();
    }

    public void setInventoryImage(){
        image = inventoryImage;
        setImage(image);
    }

    public GreenfootImage getInventoryImage(){
        if (inventoryImage == null){
            return itemImage;
        }
        return inventoryImage;
    }

    public GreenfootImage getItemImage(){
        return itemImage;
    }

    public static GreenfootImage stampPower (Item item, int value){
        Color c;
        String vString;
        if (item instanceof Torch){
            c = Color.BLACK;  
        } else {
            c = Color.BLACK;
        }
        GreenfootImage image = item.getInventoryImage();
        image.setFont(new Font ("Verdana", true, false, 14));
        image.setColor(Color.WHITE);
        image.fillRect (0, GameWorld.ITEM_SIZE - 15, GameWorld.ITEM_SIZE / 3, 15);
        image.setColor(Color.BLACK);
        image.drawRect(0, GameWorld.ITEM_SIZE - 15, GameWorld.ITEM_SIZE / 3, 15);
        image.setColor(c);
        if (value > 0){
            vString = "+" + value;
        } else {
            vString = "" + value;
        }
        image.drawString (vString, GameWorld.ITEM_SIZE - 12,  GameWorld.ITEM_SIZE / 4 -1  );
        return image;
        //item.setInventoryImage(image);
        //item.getImage().drawString(
    }

    /**
     * Items are equal if they are of the same type.
     */
    public boolean equals(Object other){
        if (other.getClass() != this.getClass()){
            System.out.println("not same class");
            return false;
        }
        iAction a = (iAction)other;
        if (a.getName() != this.getName()){
            System.out.println("other name: " + a.getName() + " this name: " + this.getName());
            return false;
        }
        return true;
    }
}
