import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Damage Indicators to show how much damage attacks do
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class DamageIndicator extends Label
{
    // duration of damage indicator
    private final int lifeTime = 250;
    SimpleTimer lifeTimer = new SimpleTimer();
    
    /**
     * Constructor for damage indicator
     * 
     * @param damage: the amount of damage
     * @param size: the size of the damage text
     * @param color: the color of the damage text
     */
    public DamageIndicator(int damage, int size, Color color) {
        super(damage, size);
        this.setLineColor(color);
        this.setFillColor(color);
        lifeTimer.mark();
    }
    
    /**
     * Act method
     * removes itself after displaying damage for its lifetime
     */
    public void act()
    {
        // fspawns around the enemy
        setLocation(getX() + Greenfoot.getRandomNumber(3) - 1, getY() - 1);
        
        if (lifeTimer.millisElapsed() > lifeTime) {
            GameWorld.gameWorld.removeObject(this);
        }
    }
}