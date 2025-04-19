import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A Torch Emits Light. It can be used by the player to extend the player's light while walking aroound,
 * or placed, to give a permanent light at that location. So far, it has been implemented to sit on the ground as 
 * an item, be placed in a bag or on an item slot and placed. Next up - equip to increase player lighting.
 * 
 * @author Jordan Cohen 
 * @version 0.6.1
 */
public class Torch extends Item implements iAction
{
    private static GreenfootImage cachedItemImage, cachedMazeImage, cachedInventoryImage;
    
    // This constructor is called when the object is "Placed" - returns
    // a new Torch of this type rather than the (boolean boolean) one... this 
    // wasn't intended... seems like two systems where one will do. Must improve!
    public Torch (){
        this (1);
    }

    // When placed
    public Torch (int quantity){
        super(cachedItemImage, true, false);
        sharedSetup();
        lightStrength = 4;
        this.quantity = quantity;
        isPlaced = true;
        //Item.stampPower(this, lightStrength);
        
        setImage (new GreenfootImage(cachedMazeImage));
       
    }

    // Initial Constructor
    public Torch (boolean isPlaced, boolean isInMaze){
        super(cachedItemImage, isPlaced, isInMaze);
        sharedSetup(); // setup commands applicable to all Torches
        //inventoryImage = Item.stampPower(this, 4);
        if (isPlaced){
            setImage(mazeImage);
            lightStrength = 4;
        } ///else if (isInMaze){
        //  setImage(itemImage);}
        else  if (!isInMaze){
            setImage(inventoryImage);
        }
    }

    private void sharedSetup (){
        name = "Torch";
        canBePlaced = true;
        canBeUsed = true;
        canStack = true;
        stackSize = 99;
        lightIntensity = 2;
        itemImage = new GreenfootImage(cachedItemImage);
        mazeImage = new GreenfootImage(cachedMazeImage);
        inventoryImage = new GreenfootImage(cachedInventoryImage);
        
    }
    
    public static void init () {
        cachedMazeImage = new GreenfootImage ("maze_torch.png");
        cachedItemImage = new GreenfootImage ("item_torch.png");
        cachedInventoryImage = new GreenfootImage ("inventory_torch.png");
    }

    public void attack (MazeEntity shooter, int direction, Class target){
        
    }
    
    public Item place(){
        
        return new Torch();
    }

    public Item use() {
        setImage(itemImage);
        return this;
    }
}
