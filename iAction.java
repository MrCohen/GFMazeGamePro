
/**
 * Write a description of class iAbility here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface iAction
{
    public boolean canBeUsed();
    public boolean canBePlaced();
    
    public iAction use();
    public iAction place();
    
    public boolean isWeapon();
    public boolean canStack();
    
    public int getStackSize();
    public int getQuantity();
    public greenfoot.GreenfootImage getIcon();
    
    public String getName();
    public String getDescription();
    
    public ActionBarButton getDropBar();
    
    public abstract boolean equals(Object other);
}





interface iCanBeUsed extends iAction
{
    
    
}

interface iCanBePlaced extends iAction
{
    
    
}

