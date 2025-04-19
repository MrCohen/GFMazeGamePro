import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Action here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Action extends Icon
{
    private final int KEY_SIZE = 16;
    private Item item;
    private String key;

    // an empty action
    public Action (String key){
        super ();
    }

    public Action (GreenfootImage baseImage, GreenfootImage selectedImage, String key) {
        super (baseImage, selectedImage);
        drawKey(baseImage);
        drawKey(selectedImage);
        this.key = key;
    }

    public Action (Item i, String key){
        super (i.getInventoryImage());
        drawKey(baseImage);
        drawKey(selectedImage);
        this.item = item;
        this.key = key;
    }

    public void use (){
        item.use();
    }

    private void drawKey (GreenfootImage canvas){
        canvas.setColor (Color.WHITE);
        canvas.fillRect (2, canvas.getHeight() - 2 - KEY_SIZE, KEY_SIZE, KEY_SIZE);

        if (item != null){
            canvas.setColor (Color.GREEN);
            if (item.canBePlaced()){

            }
            if (item.canBeUsed()){

            }
        } else {
            canvas.setColor (Color.BLACK);
            canvas.drawRect (2, canvas.getHeight() - 2 - KEY_SIZE, KEY_SIZE, KEY_SIZE);
            canvas.drawString (key, 4, canvas.getHeight() - 4);
        }
    }
    public void act () {
        System.out.println("There is an action!");
    }
}
