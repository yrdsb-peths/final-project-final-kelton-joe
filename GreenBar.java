import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Green bar - used for health bars for enemies
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class GreenBar extends SmoothMover
{
    // image for the bar
    private GreenfootImage image;
    
    // how wide to originally start with
    private int startWidth;
    
    /**
     * Constructor for a green bar
     * 
     * @param xScale: horizontal scale
     * @param yScale: vertical scale
     * @param isBoss: whether the enemy the bar is for is a boss
     */
    public GreenBar(double xScale, double yScale, boolean isBoss) {
        // custom boss bar 
        if (isBoss) {
            image = new GreenfootImage("bossbarred.png");
            image.scale(480, 55);
            setImage(image);
        }
        // regular health bar
        else {
            image = new GreenfootImage("greenBar.png");
            setImage(image);
            image.scale((int)(image.getWidth() * xScale), (int)(image.getHeight() * yScale));
        }
        
        // sets start width to max size
        startWidth = image.getWidth();
    }
    
    /**
     * Changes the position and percentage of the green bar
     * 
     * @param x: new x location
     * @param y: new y location
     * @param percentage: new percentage of the bar
     */
    public void setPos(double x, double y, double percentage) {
        // removes if the percentage reaches 0
        if (percentage <= 0) return;
        
        // changes location
        setLocation(x - ((int)startWidth*(1-percentage)/2), y + 20);
        
        // new scale
        image.scale(Math.max(1, (int)(startWidth*percentage)), image.getHeight());
    }
}
