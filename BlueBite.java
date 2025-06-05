import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Blue bite from Wyrm boss attack
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BlueBite extends Actor
{
    // stats
    private int damage;
    private boolean isDodged;
    private final int size = 200;
    private boolean hasAttacked;
    
    // image
    GreenfootImage[] image = new GreenfootImage[30];
    SimpleTimer animationTimer = new SimpleTimer();
    private int index = 0;
    
    /**
     * Constructor for blue bite
     * 
     * @param damage: damage it deals
     * @param isDodged: whether this attack is dodged
     */
    public BlueBite(int damage, boolean isDodged) {
        // sets images
        for (int i = 0; i < image.length; i++) {
            image[i] = new GreenfootImage("blueBite/bite" + i + ".png");
            image[i].scale(size, size);
        }
        
        // sets instance variables
        this.damage = damage;
        this.isDodged = isDodged;
        hasAttacked = false;
        
        // resets animations
        animationTimer.mark();
        index = 0;
    }
    
    /**
     * Act method
     * deals damage if touching the hero class
     */
    public void act()
    {
        // animates the attack
        animateBite();
        
        // attacks the hero only once per animation
        if (this.isTouching(Hero.class) && !hasAttacked && index == 15) {
            // prevent attacking twice
            hasAttacked = true;
            
            // deals damage if hero is not immune 
            if (!Hero.hero.isImmune && !isDodged) {
                Hero.hero.currentHp -= this.damage;
                
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
     * Method for animating the bite attack
     */
    public void animateBite() {
        if (animationTimer.millisElapsed() < 5) return;
        animationTimer.mark();
        
        // increases index
        if (index < image.length) {
            setImage(image[index]);
            index++;
        }
    }
}
