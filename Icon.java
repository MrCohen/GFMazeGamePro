import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Icon here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Icon extends Actor
{
    private static Color selectedColor = new Color (0, 255, 0, 120);
    protected GreenfootImage baseImage, selectedImage;
    private boolean selected;
    public Icon (GreenfootImage baseImage, GreenfootImage selectedImage){
        this.baseImage = baseImage;
        this.selectedImage = selectedImage;
        setImage(baseImage);
    }
    
    public Icon (GreenfootImage baseImage){
        this.baseImage = baseImage;
        this.selectedImage = new GreenfootImage(baseImage);
        selectedImage.setColor (selectedColor);
        selectedImage.fill();
        setImage(baseImage);
    }
    
    public Icon (){
        GreenfootImage temp = new GreenfootImage (GameWorld.ITEM_SIZE, GameWorld.ITEM_SIZE);
        temp.setColor(Color.DARK_GRAY);
        temp.fill();
        
    }
    
    
    public void select (){
        selected = true;
        setImage (selectedImage);
    }
    
    public void deselect () {
        selected = false;
        setImage (baseImage);
    }
    
    public boolean selected () {
        return selected;
    }
    
    
}
