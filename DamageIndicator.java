import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DamageIndicator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DamageIndicator extends Label
{
    private final int lifeTime = 250;
    SimpleTimer lifeTimer = new SimpleTimer();
    
    public DamageIndicator(int damage, int size, Color color) {
        super(damage, size);
        this.setLineColor(color);
        this.setFillColor(color);
        lifeTimer.mark();
    }
    
    public void act()
    {
        setLocation(getX() + Greenfoot.getRandomNumber(3) - 1, getY() - 1);
        
        if (lifeTimer.millisElapsed() > lifeTime) {
            GameWorld.gameWorld.removeObject(this);
        }
    }
}