import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ActionBarButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ActionBarButton extends Actor
{
    private GreenfootImage image;
    private iAction action;
    private String key;
    private int id, quantity;
    private boolean selected;
    private boolean canBeUsed, canBePlaced, canAttack;

    private static Font keyFont = new Font ("Courier New", true, false, 13);
    private static Font quantityFont = new Font ("Verdana", true, false, 14);

    // make an empty action
    public ActionBarButton (String key, int id){
        this.id = id;
        selected = false;
        this.key = key;
        drawButton();
    }

    // make an action for an unstackable item or ability
    public ActionBarButton (iAction action, String key, int id){
        this (action, key, id, -1);
    }

    // TODO --> FIX: quantity is now part of Item / iAction
    public ActionBarButton (iAction action, String key, int id, int quantity){
        this.quantity = quantity;
        this.action = action;
        this.id = id;
        this.key = key;
        selected = false;
        canBeUsed = action.canBeUsed();
        canBePlaced = action.canBePlaced();
        canAttack = action.isWeapon();
        drawButton ();
    }

    /**
     * Drop an iAction onto this ActionBarButton and this method will either:
     * -- Add to the stack if they are the same Item
     * -- Replace the current iAction if they are different
     */
    public void dropAction (iAction newAction){
        if (action != null){
            System.out.println("Action: " + action);
            System.out.println("newAction: " + newAction);
            System.out.println("Result of .equals: " + action.equals(newAction));
        }
        if (action != null && action.equals(newAction)){
            quantity += newAction.getQuantity();
            System.out.println("added to quantity, now: " + quantity);
            drawButton ();
        } else {
            if (newAction.getQuantity() == 0){
                this.quantity = 1;
            }else {
                this.quantity = newAction.getQuantity();
            }
            this.action = newAction;
            canBeUsed = action.canBeUsed();
            canBePlaced = action.canBePlaced();
            canAttack = action.isWeapon();
            drawButton ();
        }
    }

    public void deselect () {
        drawButton();
        selected = false;
    }

    public void select () {
        drawButton();
        for (int i = 0; i < 3; i++){
            image.setColor (Color.YELLOW);
            image.drawRect (i, i, image.getWidth() - (i * 2), image.getHeight() - (i * 2));
        }
        selected = true;
    }

    public boolean isSelected (){
        return selected;
    }

    public int getQuantity(){
        return quantity ;
    }

    public void reduceQuantity (int amount){
        quantity = Math.max (quantity - amount, 0);
        drawButton();
    }

    public iAction getAction () {
        return action;
    }

    private void drawButton(){
        int stringX;
        if (action == null){
            drawEmptyButton();
            return;
        }
        GreenfootImage icon = action.getIcon();
        image = new GreenfootImage (icon.getWidth() + 2, icon.getHeight() + 2);
        if (canAttack){
            image.setColor(Color.RED);
            image.fill();
        } else if (canBeUsed){
            image.setColor(Color.PINK);
            image.fill();
        }else if (canBePlaced){
            image.setColor(Color.BLUE);
            image.fill();
        }
        image.drawImage (icon, 2, 2);
        // draw key over top 
        image.setColor (Color.LIGHT_GRAY);
        if (key.length() > 1){
            int wid = InventoryManager.getStringWidth(keyFont, key);
            stringX = (image.getWidth() - wid)/2;

            //image.fillRect (0, image.getHeight() - 16, wid, 16);
        } else {
            stringX = image.getWidth() - 10;
            image.fillRect (image.getWidth()-12, image.getHeight() - 16, 12, 16);
        }
        image.setFont (keyFont);
        image.setColor (Color.BLACK);
        //int stringX = key.length() == 1? image.getWidth()-10 : ;
        image.drawString (key, stringX, image.getHeight() - 3);

        if (quantity >= 0){
            image.setFont (quantityFont);
            String t = "" + quantity;
            image.drawString (t, 4, quantityFont.getSize() + 1);
        }

        setImage(image);
    }

    private void drawEmptyButton(){
        int stringX;
        image = new GreenfootImage (GameWorld.ITEM_SIZE + 2, GameWorld.ITEM_SIZE + 2);
        image.setColor (Color.DARK_GRAY);
        image.fill();
        // draw key over top 
        image.setColor (Color.LIGHT_GRAY);

        if (key.length() > 1){
            int wid = InventoryManager.getStringWidth(keyFont, key);
            stringX = (image.getWidth() - wid)/2;

            image.fillRect (2, image.getHeight() - 16, wid, 16);
        } else {
            stringX = image.getWidth() - 10;
            image.fillRect (image.getWidth()-12, image.getHeight() - 16, 12, 16);
        }

        image.setFont (keyFont);
        image.setColor (Color.BLACK);
        //int stringX = key.length() == 1? image.getWidth()-10 : (image.getWidth() - InventoryManager.getStringWidth(keyFont, key))/2;
        image.drawString (key, stringX, image.getHeight() - 3);
        setImage(image);
    }

    /**
     * Act - do whatever the ActionBarButton wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        // Add your action code here.
    }
}
