import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ShadeBlock here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ShadeBlock extends Actor
{
    protected static final boolean SHOW_SHADES = false;
    
    private  GreenfootImage[] images;
    private static GreenfootImage[] cachedImages;
    private double lightPercent;

    public static void init (){
        cachedImages = new GreenfootImage[GameWorld.SHADES];
        GreenfootImage baseImage = new GreenfootImage(Block.getSize(), Block.getSize());
        
        // cache to simplify loop below:
        double quotient = 255.0/cachedImages.length;
        
        for (int i = 0; i < cachedImages.length; i++){

            int opacity = 255 - (int)(i * (quotient));
            //System.out.println(" o: " + opacity + " ");
            Color shade = new Color (0, 0, 0, opacity);
            cachedImages[i] = new GreenfootImage (baseImage);
            cachedImages[i].setColor(shade);
            cachedImages[i].fill();//Rect(0,0,cachedImages[i].getWidth()-2, cachedImages[i].getHeight()-2);
            // Troubleshooting --> Show Opacities on Blocks
            if (SHOW_SHADES){
                cachedImages[i].setColor(Color.RED);
                cachedImages[i].setFont (new Font("Courier New", false, false, 8));
                cachedImages[i].drawString (Integer.toString(opacity), 3, 12 );
            }
        }
    }

    protected GreenfootImage[] cloneCache (GreenfootImage[] source){
        GreenfootImage[] temp = new GreenfootImage[source.length];
        for (int i = 0; i < source.length; i++){
            temp[i] = new GreenfootImage (source[i]);
        }
        return temp;
    }
    
    public ShadeBlock (int mazeX, int mazeY){
        //super (false, mazeX, mazeY);
        
        //  OLD:
        
        //images = cloneCache (cachedImages);
        //setImage(images[images.length-1]);
        
        // BETTER:
        setImage(cachedImages[cachedImages.length-1]);
        
    }

    public void setBlack (){
        // OLD
        //setImage(images[0]);
        
        // NEW
        setImage (cachedImages[0]);
    }
    
    public void applyShade (double lightPercent){
        lightPercent = Math.min (lightPercent, 1);
        this.lightPercent = lightPercent;
        
        int index = (int)((cachedImages.length - 1) * (lightPercent));
        //System.out.println(this + " applying shade from index = " + index);
        setImage(cachedImages[index]);
    }
}
