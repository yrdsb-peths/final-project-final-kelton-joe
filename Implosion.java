import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Implosion attack used by the shark boss in phase 3
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class Implosion extends Actor
{
    // stats
    private int damage;
    private boolean isDodged;
    private final int size = 120;
    private boolean hasAttacked;
    
    // image
    GreenfootImage[] image = new GreenfootImage[30];
    SimpleTimer animationTimer = new SimpleTimer();
    private int index = 0;
    
    public Implosion(int damage, boolean isDodged) {
        // sets images
        for (int i = 0; i < image.length; i++) {
            image[i] = new GreenfootImage("shark/implosion/implosion" + i + ".png");
            image[i].scale(size, size);
        }
        
        // sets instance variables
        this.damage = damage;
        this.isDodged = isDodged;
        this.hasAttacked = false;
        
        // reset animations
        animationTimer.mark();
        index = 0;
    }
    
    /**
     * Act method
     * deals damage if touching the hero class
     */
    public void act()
    {
        animateImplosion();
        
        // attacks the hero only once per animation
        if (this.isTouching(Hero.class) && !hasAttacked && index == 15) {
            // prevent attacking twice
            hasAttacked = true;
            
            // deals damage if hero is not immune 
            if (!Hero.hero.isImmune && !isDodged) {
                Hero.hero.currentHp -= this.damage;
                Hero.hero.burn(this.damage * 0.3, Greenfoot.getRandomNumber(3) + 2);
                
                // changes health bar
                GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
                
                // death or hurt animation (if applicable)
                if (Hero.hero.currentHp <= 0) Hero.hero.isDead = true;
                else if (!isDodged && !Hero.hero.isImmune) Hero.hero.isHurt = true;
            }
        }
        
        // removes attack once animaiton is finished
        if (index == image.length) GameWorld.gameWorld.removeObject(this);
    }
    
    /**
     * Method for animating the implosion attack
     */
    private void animateImplosion() {
        if (animationTimer.millisElapsed() < 5) return;
        animationTimer.mark();
        
        // increases index
        if (index < image.length) {
            setImage(image[index]);
            index++;
        }
    }
}
