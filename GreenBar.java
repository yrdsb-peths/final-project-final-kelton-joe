import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class GreenBar here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GreenBar extends SmoothMover
{
    private GreenfootImage image;
    private int startWidth;
    
    public GreenBar(double scale) {
        image = new GreenfootImage("greenBar.png");
        setImage(image);
        image.scale((int)(image.getWidth() * scale), (int)(image.getHeight() * scale));
        
        startWidth = image.getWidth();
    }
    
    public void setPos(double x, double y, double percentage) {
        setLocation(x - ((int)startWidth*(1-percentage)/2), y + 20);
        image.scale((int)(startWidth*percentage), image.getHeight());
    }
}
