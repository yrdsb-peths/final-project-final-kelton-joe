import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Bow class
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class HeroArm extends SmoothMover
{
    // image storage for the bow
    private GreenfootImage[] bowLeft = new GreenfootImage[7];
    private GreenfootImage[] bowRight = new GreenfootImage[7];
    
    // bow animation
    public boolean isAnimating;
    public SimpleTimer bowAnimationTimer = new SimpleTimer();
    public int bowImageIndex = 0;
    
    public HeroArm() {
        // sets images
        for (int i = 0; i < bowLeft.length; i++) {
            bowLeft[i] = new GreenfootImage("bow/bow" + i + ".png");
            bowLeft[i].mirrorVertically();
            
            bowRight[i] = new GreenfootImage("bow/bow" + i + ".png");
            
            bowLeft[i].scale(40, 40);
            bowRight[i].scale(40, 40);
        }
        
        // sets default image
        setImage(bowLeft[0]);
    }
    
    /**
     * Act method for the boss
     * mainly used to control the animation and rotation
     */
    public void act() { 
        // animates the bow and sets the rotation
        if (isAnimating) {
            bowAnimationTimer.mark();
            animateBow(Hero.hero.facing);
        }
        // animates the bow while attacking
        if (!Hero.hero.isAttacking) {
            if (Hero.hero.facing.equals("right")) setRotation(0);
            else setRotation(180);
        }
    }
    
    /**
     * Set position method for the bow
     * 
     * @param x: x location
     * @param y: y location
     * @param facing: direction of the hero (used for offsetting the image)
     */
    public void setPos(double x, double y, String facing) {
        if (facing.equals("right")) x += 3;
        else x -= 3;
        setLocation(x, y);
    }
    
    /**
     * Method for animating the bow
     * 
     * @param facing: direction of the bow
     */
    public void animateBow(String facing) {
        // each frame is calculated based on attack speed of the hero
        if (bowAnimationTimer.millisElapsed() < Hero.hero.attackSpeed / 20.0) return;
        
        // marks animation timer
        bowAnimationTimer.mark();
        
        // continues animation if not finished
        if (bowImageIndex < bowRight.length) {
            // checks facing 
            if (facing.equals("right")) setImage(bowRight[bowImageIndex]);
            else setImage(bowLeft[bowImageIndex]);
            
            // increases the image index
            bowImageIndex++;
        }
        // resets animation things
        else {
            isAnimating = false;
            bowImageIndex = 0;
            Hero.hero.isAttacking = false;
        }
    }
    
    /**
     * Face an enemy
     * 
     * @param enemy: enemy to face
     */
    public void faceEnemy(Enemy enemy) {
        double dx = enemy.getExactX() - getExactX();
        double dy = enemy.getExactY() - getExactY();
    
        // Calculate the angle in degrees
        double angle = Math.toDegrees(Math.atan2(dy, dx));
    
        // Rotate the hero's image to face the enemy
        setRotation((int) angle);
    }
}
