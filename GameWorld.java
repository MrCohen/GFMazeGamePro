import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.ArrayList;
import java.util.LinkedList;
import java.text.DecimalFormat;
/**
 * The Game World. 
 * 
 * (Prior versions had the Maze as a World, it is now a standalone class).
 * 
 * ( earlier notes available in 0.6.7 and before )
 * 
 * --0.5.3--
 * * Finally in a good place with multiple windows moving, keeping the top window and it's contents on top
 *   and, trickiest of all, having newly spawned objects be added to the paint order without too much extra processing
 *   (I did this by creating pAddObject in SuperWindow, which is used instead of addObject when objects are getting
 *    reinserted by the Paint Order functions. pAddObject is simplified and only adds and positions the object,
 *    does not add it to the contents list (because it should already be there). 
 *    When the addObject method is called externally, the new Actor is added to contents, and the display order is refreshed
 *    but ONLY fater the SuperWindow's init has been called. So, when initially adding objects it won't redraw repeatedly.)
 * * I feel very smart today because that was a LOT to manage!
 * * 
 * 
 * --0.6.0--
 * * Superwindow is actually done this time. More bugs were found and squashed. More efficiencies applied. 
 * * I can drag minimized windows now without crashing!
 * * FPS counter has been improved to be as efficient as I can come up with.
 * * Enemies now adjust their targets if the gameWindow is moved (which it won't be, because it's locked, but still...)
 * * Enemies now move towards player properly, still don't retarget until they reach their initial destination, though.
 * * 
 * * V0.6 Goals
 * * - Game Logic! Maybe a bit ... not abstracting anything yet, though.
 * * - Items are tricky ... Invetory and actions and ... oh my
 * 
 * --0.6.6--
 * * Inventory System is half-baked! That's better than raw
 * * Next step is to allow items from inventory to be dragged 
 *   to the action bar and ...
 * * Allow action bar items to be... placed, used, attacked with
 * 
 * --0.6.7--
 *  * Dealing with issues related to quantities - not diminishing,
 *  * currently using place() to generate a new Torch ... is there a better way??
 * 
 * -- 0.6.8 --
 *  * Time to implement dragging from inventory to Action Bar
 * 
 * -- 0.7.0 --
 *  * Dragging works! Can drag from inventory, if you hit an action bar it puts 
 *    the action there. If you drop it on the way, it goes back to inventory!
 *  * Next steps -- If Ability is already on bar, stack in there instead of making duplicate
 *  *            -- Ability to drag from Ability Bar back to Bag
 *  *            -- Ability to drag to move around Ability Bar
 *  *            -- Right click to "Use" or "Place" item from Ability Bar
 *  *            -- AFTER THAT --> EQUIP!
 * 
 * -- 0.7.1 --
 *  * Need to redo the Torch / Item classes --> Right now "picking up" involves creating a new object and its not working right
 * 
 * -- 0.9.1 --
 *  * Picking up again, June 2023, Because Matthew wants to work on this together over the summer
 *  * Added "charged" shooting for Grenades, determines distance that they will fly! 
 * 
 * @author Jordan Cohen
 * @version 0.9.1, Jan 23, 2023
 */
public class GameWorld extends World
{
    // Number of shades for shadows
    public static final int SHADES = 256;
    public static final boolean RENDER_TILES = false;

    public static final int BLOCK_SIZE = 32;
    public static final int ITEM_SIZE = 48;

    private static MouseInfo mouse;
    private static Maze maze;
    private static SuperWindow mazeWindow, gameWindow, inventoryWindow, actionBarWindow;
    private static Icon inventoryIcon;
    private static SuperTextBox textBox; 
    private static SuperDisplayLabel scoreLabel;

    private static String key;

    private static LinkedList<Coordinate> solution;

    private static LinkedList<Long> actTimes;

    private Traverser trav;
    private Player player;

    private static double lastActDuration;
    private static long lastAct, elaspsed, last60Duration;
    private static long startLoad, loadTime;
    private static long startGen, genTime;
    private static long startSolve, solveTime;
    private static double loadMs, genMs, solveMs, initMs, shaderMs;
    private static int playerX, playerY;
    private static int actCount;
    private static Block playerBlock;
    private static int invWindowX, invWindowY;

    private static boolean isDraggingAction;
    private static iAction draggedAction;
    private static Actor am;

    /**
     * Constructor for objects of class GameWorld.
     * 
     */
    public GameWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(1440, 900, 1, false); 
        Greenfoot.setSpeed(50);
        // start the timer to see how long loading takes...
        startLoad = System.nanoTime();

        
        
        // re-initialize the SuperWindow, setting managed to false
        SuperWindow.reset();
        actCount = 0;
        lastAct = 0;

        isDraggingAction = false; 

        int mazeBlock = BLOCK_SIZE;
        int mazeWidth = (996 / mazeBlock);
        mazeWidth = mazeWidth % 2 == 1 ? mazeWidth : mazeWidth-1;
        int mazeHeight = (800 / mazeBlock);
        mazeHeight = mazeHeight % 2 == 1 ? mazeHeight : mazeHeight-1;

        // Setup Paint order for SuperWindow
        ArrayList<Class<?>> po = new ArrayList<Class<?>>();
        po.add(ShadeBlock.class);
        po.add(Projectile.class);
        po.add(SuperStatBar.class);
        po.add(Player.class);
        po.add(Enemy.class);
        po.add(Item.class);
        po.add(Block.class);
        // mazeWindow.(po);
        SuperWindow.setStaticPaintOrder(po);

        // Player start details
        playerX = 13;
        playerY = 11;

        // Init Blocks
        Block.init(mazeBlock);

        long endInit = System.nanoTime();
        
        initMs = (endInit - startLoad)/1000000.0;
        long loadShaderStart = System.nanoTime();
        ShadeBlock.init();
        long loadShaderDuration = System.nanoTime() - loadShaderStart;
        shaderMs = loadShaderDuration / 1000000.0; 

        long uiStart = System.nanoTime();
        scoreLabel = new SuperDisplayLabel ();
        addObject(scoreLabel, 640, scoreLabel.getImage().getHeight() / 2);

        int textBoxHeight = 126;
        mazeWindow = new SuperWindow (mazeWidth * mazeBlock, mazeHeight * mazeBlock, 0, "Maze", new boolean[]{false, true, false, false, false});//{true, false, true, true, false});

        textBox = new SuperTextBox (new String[]{"", "", "", "", "", "", "","", ""}, 
            Color.BLACK, Color.WHITE, new Font ("Courier New", true, false, 18), false,  getWidth() - mazeWindow.getTotalWidth() , 2, new Color (255,204,0));

        gameWindow = new SuperWindow (getWidth() - mazeWindow.getTotalWidth()-4, getHeight() - scoreLabel.getImage().getHeight() - textBox.getImage().getHeight(), 0, "Game Window", new boolean[]{false, true, false, false, false});
        inventoryWindow = new SuperWindow (260, 260, 18, "Inventory", new boolean[]{false, false, true, true, false});
        actionBarWindow = new SuperWindow (mazeWidth * mazeBlock, getHeight() - scoreLabel.getImage().getHeight() - mazeWindow.getTotalHeight(), 0, "Action Bar", new boolean[] {false, true, false, false, false});

        addObject(mazeWindow, getWidth() - mazeWindow.getImage().getWidth()/2, getHeight() - mazeWindow.getImage().getHeight()/2);

        gameWindow.setFramedImage(new GreenfootImage("paper background.png"));
        actionBarWindow.setFramedImage (new GreenfootImage("actionbar_back.png"));

        addObject(gameWindow, gameWindow.getImage().getWidth()/2, scoreLabel.getImage().getHeight() + gameWindow.getTotalHeight()/2);

        addObject(actionBarWindow, getWidth() - actionBarWindow.getImage().getWidth()/2, scoreLabel.getImage().getHeight() + actionBarWindow.getTotalHeight() / 2);

        inventoryIcon = new Icon (new GreenfootImage("icon_inventory.png"), new GreenfootImage( "icons_inventory.png"));
        gameWindow.addObject(inventoryIcon, 76, 76);

        inventoryWindow.setFramedImage(new GreenfootImage("background_leather.png"));

        InventoryManager.init(inventoryWindow, actionBarWindow);
        invWindowX = 152;
        invWindowY = 350;

        
        
        startGen = System.nanoTime();
        maze = new Maze (mazeBlock,mazeWidth, mazeHeight,  mazeWindow);
        MazeEntity.init(maze);
        genTime = System.nanoTime() - startGen;
        genMs = (genTime / 1000000.0);

        player = new Player();
        maze.addMazeEntityCoord(player, playerX, playerY);

        startSolve = System.nanoTime();
        trav = new Traverser(maze);
        mazeWindow.addObject(trav, 0, 0);
        solveTime = System.nanoTime() - startSolve;
        solveMs = solveTime / 1000000.0;

        Torch testTorch = new Torch (false, true);
        // testing adding an item to the world...
        maze.addMazeEntityCoord(testTorch, 3 , 3 );

        // testing adding an action to action bar...
        InventoryManager.addAction (testTorch, 0, 3);

        loadTime = System.nanoTime() - startLoad;
        loadMs = loadTime / 1000000.0;
        /**
        textBox = new SuperTextBox (new String[]{"Welcome To The Maze!",
        "...",
        "Load Shaders:    " + loadShaderMs + "ms",
        "Gen Maze in:     " + genMs + "ms",
        "Solve Maze in:   " + solveMs + "ms",
        "Total Load Time: " + loadMs + "ms",
        "...",
        "Good Luck!"}, 
        Color.BLACK, Color.WHITE, new Font ("Courier New", true, false, 18), false,  getWidth() - mazeWindow.getTotalWidth() , 2, new Color (255,204,0));
         */
        textBox.update(new String[]{"Welcome To The Maze!",
                "...",
                "First Init:      " + leftPad(initMs + "ms", 15, ' '),
                "Load Shaders:    " + leftPad(shaderMs + "ms",15, ' '),
                "Gen Maze in:     " + leftPad(genMs + "ms",15, ' '),
                "Solve Maze in:   " + leftPad(solveMs + "ms",15, ' '),
                "Total Load Time: " + leftPad(loadMs + "ms",15, ' '),
                "...",
                "Good Luck!"});

        addObject (textBox, textBox.getImage().getWidth()/2, getHeight() - textBox.getImage().getHeight()/2);

        // scoreLabel.update("Welcome... To the Machine!", true);
        maze.updateLightGrid(playerX, playerY, 6,0.75);
        //maze.testShade();

        actTimes = new LinkedList<Long>();

        // Setup Window Order for SuperWindow and Initialize it
        ArrayList<SuperWindow> windows = new ArrayList<SuperWindow>();
        windows.add(inventoryWindow);
        windows.add(gameWindow);
        windows.add(mazeWindow);

        // This must be called after setStaticPaintOrder (or set individual paint orders by SuperWindow and set 
        SuperWindow.initWindowManager(windows);

        // Four corner based spawning
        for (int i = 0; i < 6; i++){
            int corner = Greenfoot.getRandomNumber (4);
            int sX, sY;

            if (corner == 0){ // top left
                sX = (Greenfoot.getRandomNumber(3) * 2) + 2;
                sY = (Greenfoot.getRandomNumber(3) * 2) + 2;
            } else if (corner  == 1){ // top right
                sX = mazeWidth - ((Greenfoot.getRandomNumber(3) * 2) + 2);
                sY = (Greenfoot.getRandomNumber(3) * 2) + 2;
            } else if (corner == 2){ // bottom left
                sX = (Greenfoot.getRandomNumber(3) * 2) + 2;
                sY = mazeHeight - ((Greenfoot.getRandomNumber(2) * 2)+2);
            } else {
                sX = mazeWidth - ((Greenfoot.getRandomNumber(3) * 2) + 2);
                sY = mazeHeight - ((Greenfoot.getRandomNumber(2) * 2) + 2);
            }

            // Ensure odd numbers (guranteed rooms)
            sX = (sX % 2 == 1 ? sX : sX + 1);           
            sY = (sY % 2 == 1 ? sY : sY + 1);

            Seeker s = new Seeker();
            maze.addMazeEntityCoord(s, sX, sY);
        }

    }

    public void setSolvePath (LinkedList<Coordinate> solution){
        this.solution = solution;
    }

    public void act () {
        updateState();
        checkInput();
    }

    public static void outputText (String text){
        textBox.update (text);
    }

    public static int getActCount(){
        return actCount;
    }

    private void checkInput() {
        if (Greenfoot.mouseClicked(inventoryIcon)){
            toggleInventory();
        }

        if (mouse != null){
            if (Greenfoot.mouseDragged(null) && !isDraggingAction){
                am = mouse.getActor();
                if (am instanceof iAction){
                    isDraggingAction = true;
                    draggedAction = (iAction)am;
                    InventoryManager.removeItem((Item)am);
                    addObject(am, mouse.getX(), mouse.getY());
                } else {
                    //System.out.println("am: " + am + " is not an iAction");
                }
            }
        }
        if (isDraggingAction){
            if (Greenfoot.mouseDragEnded(null)){
                //System.out.println("Mouse Actor at Drag's end: " + mouse.getActor());
                iAction acm = (iAction)am;
                ActionBarButton destination = acm.getDropBar();
                if (destination != null){
                    removeObject(am);
                    destination.dropAction (acm);
                    isDraggingAction = false;
                } else {

                    if (acm instanceof Item){
                        System.out.println("FAIL --> Adding " + am + " back to pack.");
                        removeObject(am);
                        InventoryManager.addItem((Item)acm);
                    }
                }

                am = null;
                isDraggingAction = false;
            }
            else {
                // TODO --> Improve this with cached coordinates to avoid weirdness when dragging mouse off screen
                if (mouse != null){
                    am.setLocation (mouse.getX(), mouse.getY());
                }
            }
        }

        if (key != null){
            if (key.equals("i") || key.equals("I")){
                toggleInventory();
            }
        }
    }

    private void toggleInventory () {
        if (inventoryWindow.getWorld() == null){
            addObject(inventoryWindow, invWindowX, invWindowY);
            inventoryIcon.select();
        } else {
            invWindowX = inventoryWindow.getX();
            invWindowY = inventoryWindow.getY();
            inventoryWindow.removeWindow();
            removeObject(inventoryWindow);
            inventoryIcon.deselect();
        }
    }

    public MouseInfo getMouseInfo () {
        return mouse;
    }

    public static SuperWindow getActionBarWindow() {
        return actionBarWindow;
    }

    public static SuperWindow getGameWindow() {
        return gameWindow;
    }

    public String getKey(){
        return key;
    }

    public void started () {
        // Avoid FPS issues when pausing simulation
        actCount = 0;
    }

    private void updateState(){
        actCount++;
        long current = System.nanoTime();

        // First act only
        if (actCount == 1){
            actTimes.clear();
            lastActDuration = 1/60.0; // default frame time for first act
            lastAct = System.nanoTime();
            last60Duration = 0;
            //return;
        }

        mouse = Greenfoot.getMouseInfo();
        key = Greenfoot.getKey();

        long duration = current - lastAct;
        actTimes.add(0, duration);
        last60Duration += duration;
        if (actTimes.size() == 60){
            last60Duration -= actTimes.removeLast();

        }
        if (actCount % 60 == 0){
            double averageDuration = last60Duration / actTimes.size();
            double fps = 1/(averageDuration/1000000000.0);
            scoreLabel.update("FPS: " + roundToTwo(fps) + " Enemies Left: " + getObjects(Enemy.class).size());
        }

        lastActDuration = (duration)/1000000000.0;
        lastAct = current;

    }

    public static double roundToTwo (double value){
        int val100 = (int)(value * 100);
        return val100 / 100.0;

    }

    public static double getDuration () {
        if(lastActDuration > 0.1){ // if game was paused, avoids high values cause by long elapsed time
            return 0.1;
        }
        return lastActDuration > 0 ? lastActDuration : 1/60.0;
    }

    /**
     * Useful method to add specified padding (usually spaces) to the left
     * of a String. This is most commonly used to make numbers of various lengths line up.
     * 
     * Before:
     * load maze:     4.5678ms
     * load things:   46.9130ms
     * 
     * After:
     * load maze:      4.5678ms
     * load things:   46.9130ms
     * 
     */
    public static String leftPad (String s, int digits, char pad){
        while (s.length() < digits){
            s = pad + s;
        }
        return s;
    }

    // Did not crash on gallery, but also did not work ...
    public static double roundTo (double value, int decPlaces){
        String decForm = "#.";
        for (int i = 0; i < decPlaces; i++){
            decForm += "0";
        }
        DecimalFormat df = new DecimalFormat(decForm);
        return Double.valueOf(df.format(value));
    }
}
