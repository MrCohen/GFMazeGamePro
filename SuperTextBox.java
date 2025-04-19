import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A flexible single- or multi-line Text Box.
 * 
 * <h2>Version 1.0 - Multi-Line!</h2>
 * 
 * <h3>Notes</h3>
 * <ul>
 *  <li> Height is set automatically to fit. See below for a detailed description of how it's calculated. The easiest way to 
 *       get the height in order to position it is to have it build itself, and then call getHeight() on the created object.</li>
 *  <li> Border Thickness is optional --> Set it to zero (0) to create a box with no border</li>
 *  <li> If you turn on centered, the processing cost goes up dramatically upon updates. This is really only an issue if you plan
 *       to publish to the Greenfoot Gallery. If you are, please consider not using centered, or updating very sparingly. </li>
 * </ul>
 * 
 * <p> To understand <b>height</b> examine the following equation:</p>
 * <code>height = (padding * 2) + (vSpace * (numLines)) + (fontSize * numLines) + (2 * borderThickness);</code>
 * 
 * @author Jordan Cohen
 * @version 1.0 (November 30, 2021)
 */
public class SuperTextBox extends Actor
{

    private GreenfootImage image;
    private String[] text;
    private boolean centered;
    private boolean bordered;
    private Color backColor;
    private Color foreColor;
    private Color borderColor;
    private Font font;
    private int[] centeredXs;
    private int numLines;
    private int vSpace;
    private int width, height;
    private int padding;
    private int fontSize;
    private int borderThickness;

    /**
     *  Simple Constructor - One line text box
     *  
     *  @param line     The line of text to display
     *  @param font     The font to display
     *  @param width    The desired width of the text box in pixels
     */
    public SuperTextBox (String line, Font font, int width){
        this (new String[]{line}, font, width);
    }

    /**
     *  Simple Constructor - Multi-line text box
     *  
     *  @param line     The line of text to display
     *  @param font     The font to display
     *  @param width    The desired width of the text box in pixels
     */
    public SuperTextBox (String[] text, Font font, int width){
        this (text, Color.BLACK, Color.WHITE, font, false, width, 2, new Color (255, 255, 204));
    }

    
    /**
     *  Simple Constructor - Multi-line text box
     *  
     *  @param line     The line of text to display
     *  @param font     The font to display
     *  @param width    The desired width of the text box in pixels
     *  @param centered Set to true to center the text (performance cost)
     */
    public SuperTextBox (String[] text, Font font, int width, boolean centered){
        this (text, Color.BLACK, Color.WHITE, font, centered, width, 2, new Color (255, 255, 204));
    }

    /**
     * The detailed constructor for ONE-LINE text boxes.
     * 
     * @param line          A single String, which will be the starting text for this one-line text box
     * @param backColor     The background colour of the text box
     * @param foreColor     The text colour of the text box
     * @param font          The (Greenfoot) Font to display
     * @param centered      Should the text be centered? (Note - this has a performance cost, especially for the Greenfoot
     *                      Gallery and doubly so for accessing the Gallery via mobile device)
     * @param width         The width, in pixels, for this text box
     * @param borderThickness   The thickness of the border. Setting this to zero will make a borderless text box
     * @param borderColor   The color of the border
     */
    public SuperTextBox(String line, Color backColor, Color foreColor, Font font, boolean centered, int width, int borderThickness, Color borderColor){
        this(new String[]{line}, backColor, foreColor, font, centered, width, borderThickness, borderColor);
    }

    /**
     * Primary Constructor.
     * 
     * @param text          The array of text. Number of lines will be equal to the size of this array.
     * @param backColor     The background colour of the text box
     * @param foreColor     The text colour of the text box
     * @param font          The (Greenfoot) Font to display
     * @param centered      Should the text be centered? (Note - this has a performance cost, especially for the Greenfoot
     *                      Gallery and doubly so for accessing the Gallery via mobile device)
     * @param width         The width, in pixels, for this text box
     * @param borderThickness   The thickness of the border. Setting this to zero will make a borderless text box
     * @param borderColor   The color of the border
     */
    public SuperTextBox(String[] text, Color backColor, Color foreColor, Font font, boolean centered, int width, int borderThickness, Color borderColor){
        this.numLines = text.length;
  
        centeredXs = new int[numLines];
        
        this.backColor = backColor;
        this.foreColor = foreColor;
        this.borderColor = borderColor;
        this.font = font;
        this.centered = centered;
        this.borderThickness = borderThickness;
        if (borderThickness > 0)
            bordered = true;
        else
            bordered = false;

        this.width = width;
        fontSize = font.getSize();

        // Spacing is a factor of font size - Font sizes typically describe the max height of 
        // the characters in that font. I.e. 24pt font is 24 pixels tall
        vSpace = fontSize / 4; //6
        this.padding = (int)(fontSize / 3.0); // 8

        // System.out.println("Pad: " + padding  + " vSpace " + vSpace + " fontSize: " + fontSize);

        // Padding top and bottom, vSpace between rows and bottom to make up for extra space on top
        height = (padding * 2) + (vSpace * (numLines)) + (fontSize * numLines) + (2 * borderThickness);

        //System.out.println("Creating image with a height of " + height);
        image = generateImage(width, height);

        update (text);

        setImage(image);

    }

    
    
    private GreenfootImage generateImage (int width, int vSpace, int padding, int borderThickness, int fontSize,  int numLines){
        height = (padding * 2) + (vSpace * (numLines)) + (fontSize * numLines) + (2 * borderThickness);
        return generateImage (width, height);
    }
    
    private GreenfootImage generateImage (int width, int height){
        
        return new GreenfootImage (width, height);
    }
    
    /**
     * Simplest Update - Intended primarily for text boxes with only one line. 
     * 
     * For text boxes with one line, this will insert the text at the BOTTOM.
     * 
     * For text boxes with just a single line, this will replace the existing text and, if
     * centering is turned on, recenter. For more precise control, use update (String[]) or
     * updateLine(String, boolean).
     * 
     * @param text the line of text to add. 
     */
    public void update (String textLine){
        if (text.length == 1){
            update (new String[] {textLine}); 
        } else { 
            updateLine (textLine, true);
        }
    }

    /**
     * Update with a whole new array of text. 
     * 
     * Note that if you have centering turned on this has significant CPU cost because it will
     * call the getStringWidth method as many times as there are Strings in String[] text.
     * For example, if there are four lines of text, it would call the method four times,
     * which in the best case is 12-16ms (on a fast local computer) which will almost certainly
     * slow your acts below 60 fps. On the Greenfoot Gallery, the time for this method to run
     * is about 10x slower, or about 120-160ms, which is enough for perceptible lag. 
     * 
     * It is better to call updateLine for centered text, as 
     * it maintains the previously calculated values for the existing lines and only calls
     * the getStringWidth method once for the new text inserted.
     * 
     * If you're not centering your text, you can ignore the above.
     */
    public void update (String[] text)
    {
        this.text = text;
        if (centered){
            for (int i = 0; i < text.length; i++){
                // The drawing position for the left of the String, not the actual center
                centeredXs[i] = (width  - getStringWidth(font, text[i]))/2;
            }
        }
        update();
    }

    public void update (){

        int xPos, yPos;

        image.setColor(backColor);
        image.fill();

        // ===== DRAW BORDER =======

        if (bordered){
            image.setColor (borderColor);
            for (int i = 0; i < borderThickness; i++){
                image.drawRect (0 + i, 0 + i, width - 1 - (i * 2), height - 1 - (i*2));
            }
        }

        // ====== DRAW TEXT ========
        image.setColor(foreColor);
        // Check for empty Strings and return from this method if any Strings are empty
        // which will cause this not to draw text (and not to crash on null Strings).
        // This will happen if it's called from the constructor (when still empty) or if
        // the class managing this does not populate the array completely.
        for (String s : text){
            if (s == null){
                return;
            }
        }

        image.setFont(font);
        for (int i = 0; i < numLines; i++){
            yPos = borderThickness + padding + ((i + 1) * fontSize) + (i * vSpace);
            if (centered){
                xPos = centeredXs[i];
            } else{
                xPos = padding + borderThickness;
            }
            // System.out.println("Pad: " + padding  + " vSpace " + vSpace + " fontSize: " + fontSize);
            // System.out.println("Drawing String " + i + " at " +yPos);
            image.drawString(text[i], xPos , yPos);
        }

    }

    /**
     * Update the Multi Line Text box by adding a new line. This can add a line
     * at the top or bottom, and will move the other lines accordingly.
     * 
     * @param textIn    The new String to add to the display
     * @param fromBottom    Should the line go at the bottom (scroll up), or false to insert it at the top (scroll down)
     */
    public void updateLine (String textIn, boolean fromBottom){
        // Bump values in the arrays in the appropriate direction:
        for (int i = 0; i < text.length - 1; i++){
            if (fromBottom){ // inserting to bottom, move text up (text[0] is displayed at the top)
                // move the text
                text[i] = text[i + 1];
                // moved the cached size value to avoid recalculation
                centeredXs[i] = centeredXs[i+1];
            } else { // inserting at the top, move everything else down
                text[text.length-(i+1)] = text[text.length-i-2];
                centeredXs[text.length-(i+1)] = centeredXs[text.length-i-2];
            }
        }
        // Insert the new value, and if centered, calculate it's position:
        if (fromBottom){
            text[text.length - 1] = textIn;
            if (centered){
                centeredXs[text.length - 1] = (width  - getStringWidth(font, text[text.length - 1]))/2;
            }
        } else {
            text[0] = textIn;
            if (centered){
                centeredXs[0] = (width  - getStringWidth(font, text[0]))/2;
            }
        }
        update();

    }

    /**
     * Set or change Colors.
     * 
     * @param backColor     The Background colour
     * @param foreColor     The text colour
     * @param borderColor   the border colour
     */
    public void setColors (Color backColor, Color foreColor, Color borderColor){
        this.backColor = backColor;
        this.foreColor = foreColor;
        this.borderColor = borderColor;
        update();
    }
    
    public void forceHeight (int height){
        this.height = height;
    }
    
    public void toggleCentered (){
        centered = !centered;
        update();
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
        if(text.length() == 0){
            return 0;
        }
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
