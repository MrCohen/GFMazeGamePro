import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Medkit here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RepairKit extends Item
{
    private static GreenfootImage cachedItemImage, cachedMazeImage, cachedInventoryImage;
  
    protected Player player;
    private int healStrength;
    
    public RepairKit (Player p)
    {
        this(1, 12, p);
       
    }
    
    public RepairKit(int quantity, int healStrength, Player p)
    {
        super(cachedItemImage, true, false);
        name = "Repair Kit";
        description = "A Repair Kit, which will repair " + healStrength + " damage.";
        canBePlaced = false;
        canBeUsed = true;
        this.quantity = quantity;
        this.healStrength= healStrength;
        isPlaced = true;
        // default to  Item 
        setImage (new GreenfootImage(cachedItemImage));
        stackSize = 99;
        player = p;
    }
    
    public Item place(){
        // can NOT place so return null
        return null;
    }

    public Item use() {
        //setImage(itemImage);
        player.healMe (healStrength);
        return this;
    }
    

    public static void init () {
        //cachedMazeImage = new GreenfootImage ("maze_torch.png");
        cachedItemImage = new GreenfootImage ("item_repairkit.png");
        cachedInventoryImage = new GreenfootImage ("item_repairkit.png");
    }
    
    public void attack (MazeEntity shooter, int direction, Class target){
        
    }
}
