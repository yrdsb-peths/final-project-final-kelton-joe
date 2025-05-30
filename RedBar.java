import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RedBar here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RedBar extends SmoothMover
{
    private GreenfootImage image;
    
    public RedBar(double xScale, double yScale) {
        image = new GreenfootImage("redBar.png");
        setImage(image);
        image.scale((int)(image.getWidth() * xScale), (int)(image.getHeight() * yScale));
    }
    
    public void setPos(double x, double y) {
        setLocation(x, y + 20);
    }
}
