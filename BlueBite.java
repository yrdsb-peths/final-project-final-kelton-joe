import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class BlueBite here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BlueBite extends Actor
{
    private int damage;
    private boolean isDodged;
    private final int size = 160;
    private boolean hasAttacked;
    
    GreenfootImage[] image = new GreenfootImage[30];
    SimpleTimer animationTimer = new SimpleTimer();
    private int index = 0;
    
    public BlueBite(int damage, boolean isDodged) {
        for (int i = 0; i < image.length; i++) {
            image[i] = new GreenfootImage("blueBite/bite" + i + ".png");
            image[i].scale(size, size);
        }
        
        this.damage = damage;
        this.isDodged = isDodged;
        hasAttacked = false;
        
        animationTimer.mark();
        index = 0;
    }
    
    public void act()
    {
        animateBite();
        
        if (this.isTouching(Hero.class) && !hasAttacked && index == 15) {
            hasAttacked = true;
            
            if (!Hero.hero.isImmune && !isDodged) {
                Hero.hero.currentHp -= this.damage;
                
                GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
                
                if (Hero.hero.currentHp <= 0) Hero.hero.isDead = true;
                else if (!isDodged && !Hero.hero.isImmune) Hero.hero.isHurt = true;
            }
        }
        
        if (index == image.length) GameWorld.gameWorld.removeObject(this);
    }
    
    public void animateBite() {
        if (animationTimer.millisElapsed() < 5) return;
        animationTimer.mark();
        
        if (index < image.length) {
            setImage(image[index]);
            index++;
        }
    }
}
