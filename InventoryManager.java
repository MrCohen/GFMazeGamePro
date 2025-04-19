import java.util.ArrayList;
import java.util.Arrays;
import greenfoot.*;
/**
 * The Inventory Manager manages the Inventory Window and the Action Bar.
 * 
 * The Inventory Manager contains ArrayList<Item> items which is a list of all Items currently in Inventory
 *                            and ActionBarButton[] actions which is the Actions currently on the Bar
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class InventoryManager  
{
    private static Color frameColor = new Color (163, 129, 78);
    private static Color clearColor = new Color(0,0,0,0);
    private static ArrayList<Item> items;
    private static ActionBarButton[] actions;

    private static SuperWindow inventoryWindow;
    private static SuperWindow actionBarWindow;
    private static GreenfootImage invBaseImage, abBaseImage;
    public static final String[] ACTION_KEYS = {"1", "2", "3", "4","5","6","Weapon","Action"};
    private static int actionSelected;

    public static void init (SuperWindow inv, SuperWindow ab){
        actions = new ActionBarButton[ACTION_KEYS.length];
        for (int i = 0; i < actions.length; i++){
            actions[i] = new ActionBarButton(ACTION_KEYS[i], i);
        }

        actionSelected = 0;
        inventoryWindow = inv;
        actionBarWindow = ab;
        items = new ArrayList<Item>();
        invBaseImage = new GreenfootImage (inventoryWindow.getFramedImage());
        GreenfootImage temp = drawInventory ( 56, 56, 3, 3, 18);
        inventoryWindow.setFramedImage(temp);

        abBaseImage = new GreenfootImage (actionBarWindow.getFramedImage());
        GreenfootImage temp2 = setupActionBar (48, 48, ACTION_KEYS);  
        actionBarWindow.setFramedImage (temp2);
    }

    public static void selectAction (int index){
        actions[actionSelected].deselect();
        actionSelected = index;
        actions[actionSelected].select();

    }

    public static int getSelectedActionQuantity() {
        if (actions[actionSelected] == null){
            return 0;
        } 
        return actions[actionSelected].getQuantity();
    }

    public static iAction peekSelectedAction () {
        if (actions[actionSelected] == null){
            return null;
        }
        iAction a = actions[actionSelected].getAction();

        return a;
    }

    public static iAction getSelectedAction () {
        if (actions[actionSelected] == null){
            return null;
        }
        iAction a = actions[actionSelected].getAction();
        if (a.canStack() && actions[actionSelected].getQuantity() > 0){

            actions[actionSelected].reduceQuantity(1);
            // If I used the last object in a stack, or there was only 1...
            if (actions[actionSelected].getQuantity() == 0){
                if (a instanceof Item){
                    removeItem ((Item)a);
                }
            }
        }

        return a;
    }

    public static void reduceSelectionQuantity (int amount){
        if (actions[actionSelected] == null){
            return;
        }
        actions[actionSelected].reduceQuantity(amount);
    }

    public static void addAction (iAction ia, int index, int quantity){
        //System.out.println("Adding " + ia + " at " + index);
        if (actions[index] != null && actions[index].getWorld() != null){
            actionBarWindow.removeObject(actions[index]);
        }
        actions[index] = new ActionBarButton(ia, ACTION_KEYS[index], index, quantity);

        GreenfootImage temp2 = setupActionBar (48, 48, ACTION_KEYS);  
        actionBarWindow.setFramedImage (temp2);

    }

    public static void addItem(Item newItem){
        items.add(newItem);

        GreenfootImage temp = drawInventory (56, 56, 3, 3, 18);
        inventoryWindow.setFramedImage(temp);
    }

    public static void removeItem (Item remItem){

        if (items.contains(remItem)){           
            items.remove(remItem);         
        }
        inventoryWindow.removeObject(remItem);
        GreenfootImage temp = drawInventory (56, 56, 3, 3, 18);
        inventoryWindow.setFramedImage(temp);
    }

    private static GreenfootImage setupActionBar (int itemWidth, int itemHeight, String[] keys){
        GreenfootImage newCanvas = new GreenfootImage(abBaseImage);
        newCanvas.setColor (Color.BLACK);
        //System.out.println(Arrays.toString(actions));
        for (int i = 0; i < keys.length; i++){
            // place action bar buttons
            actionBarWindow.addObject(actions[i], 36 + (i * (itemWidth + 12)), actionBarWindow.getHeight() /2);
        }

        return newCanvas;
    }

    private static GreenfootImage drawInventory (int itemWidth, int itemHeight, int itemsWide, int itemsHigh,int innerMargins){
        // save the background of the cavnas so I can retrieve it later

        GreenfootImage newCanvas = new GreenfootImage(invBaseImage);

        // draw frames for where the items will be placed, and place the items within that frame
        int totalWidth = newCanvas.getWidth() - 4; // assume border size 2
        int totalHeight = newCanvas.getHeight() - 4; 

        int outerMarginX = (totalWidth - (itemsWide * itemWidth) - ((itemsWide-1) * innerMargins))/2;
        int outerMarginY = (totalHeight - (itemsHigh * itemHeight) - ((itemsHigh-1) * innerMargins))/2;
        //System.out.println("OuterMarginX = " + outerMarginX);
        int index = 0;
        for (int y = 0; y < itemsHigh; y++){

            for (int x = 0; x < itemsWide; x++){
                newCanvas.setColor(frameColor);
                newCanvas.fillRect(outerMarginX + (x * itemWidth) + (x  * innerMargins), outerMarginY + (y * itemWidth) + (y * innerMargins), itemWidth, itemHeight);
                if (items.size() > index){

                    Item i = items.get(index);
                    //System.out.println("Trying to place: " + i);
                    i.setInventoryImage();
                    inventoryWindow.addObject(i, outerMarginX + (x * itemWidth) + (x  * innerMargins) + (itemWidth / 2), outerMarginY + (y * itemWidth) + (y * innerMargins) + (itemWidth / 2));
                    index++;
                }

            }
        }
        return newCanvas;
    }

    /**
     * <h3>Mr. Cohen's Text Centering Algorithm</h3>
     * 
     * <p>Get the Width of a String, if it was printed out using the drawString command in a particular
     * Font.</p>
     * <p>There is a performance cost to this, although it is more significant on the Gallery, and 
     * especially on the Gallery when browsed on a mobile device. It is appropriate to call this in the 
     * constructor, and in most cases it is ideal NOT to call it from an act method, especially
     * every act.</p>
     * 
     * <p>In cases where values are pre-determined, it may be ideal to cache the values (save them) so 
     * you don't have to run this repeatedly on the same text. If you do this in the World constructor,
     * there is no performance cost while running.</p>
     * 
     * <h3>Performance & Compatibility:</h3>
     * <ul>
     *  <li> Locally, performance should be sufficient on any moderate computer (average call 0.1-0.2ms on my laptop)</li>
     *  <li> To be compatible with Greenfoot Gallery, removed use of getAwtImage() and replaced with getColorAt() on a GreenfootImage</li>
     *  <li> On Gallery, performance is about 10x slower than locally (4ms on Gallery via Computer). For reference, an act() should be
     *       less than 16.6ms to maintain 60 frames/acts per second. </li>
     *  <li> HUGE performance drop on Gallery via Mobile devices - not sure why, going to ignore for now. (Average update duration 34ms, more
     *       than 2 optimal acts)</li>
     * </ul>
     * 
     * @param font the GreenFoot.Font which is being used to draw text
     * @param text the actual text to be drawn
     * @return int  the width of the String text as draw in Font font, in pixels.
     * 
     * @since June 2021
     * @version December 2021 - Even more Efficiency Improvement - sub 0.06ms per update on setSpeed(100)!
     */
    public static int getStringWidth (Font font, String text){

        // Dividing font size by 1.2 should work for even the widest fonts, as fonts are
        // taller than wide. For example, a 24 point font is usually 24 points tall 
        // height varies by character but even a w or m should be less than 20 wide
        // 24 / 1.2 = 20
        int maxWidth = (int)(text.length() * (font.getSize()/1.20));//1000; 
        int fontSize = font.getSize();
        int marginOfError = fontSize / 6; // how many pixels can be skipped scanning vertically for pixels?
        int checkX;

        GreenfootImage temp = new GreenfootImage (maxWidth, fontSize);
        temp.setFont(font);
        temp.drawString (text, 0, fontSize);

        //int testValue = 1000;
        boolean running = true;

        checkX = maxWidth - 1;
        while(running){
            boolean found = false;
            for (int i = fontSize - 1; i >= 0 && !found; i-=marginOfError){

                if (temp.getColorAt(checkX, i).getAlpha() != 0){
                    // This lets me only look at every other pixel on the first run - check back one to the right
                    // when I find a pixel to see if I passed the first pixel or not. This should almost half the 
                    // total calls to getColorAt().
                    if (temp.getColorAt(checkX + 1, i).getAlpha() != 0){
                        checkX++;
                        if (temp.getColorAt(checkX + 1, i).getAlpha() != 0){
                            checkX++;
                        }
                    }
                    found = true;
                }
            }
            if (found){
                return checkX;
            }
            checkX-=3; // shift 3 pixels at a time in my search - above code will make sure I don't miss anything
            if (checkX <= marginOfError)
                running = false;
        }
        return 0;

    }
}
