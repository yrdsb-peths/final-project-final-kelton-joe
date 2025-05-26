import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class DamageIndicator here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class DamageIndicator extends Actor
{
    Label damageIndicator;
    private final int lifeTime = 800;
    private int size;
    SimpleTimer lifeTimer = new SimpleTimer();
    
    public DamageIndicator(int damage, boolean isCrit) {
        if (isCrit) size = 30;
        else size = 20;
        
        this.damageIndicator = new Label(damage, size);
    }
    
    public void addToWorld(int x, int y) {
        GameWorld.gameWorld.addObject(this.damageIndicator, x, y);
        lifeTimer.mark();
    }
    
    public void act()
    {
        if (lifeTimer.millisElapsed() > lifeTime) {
            GameWorld.gameWorld.removeObject(this);
        }
    }
}
