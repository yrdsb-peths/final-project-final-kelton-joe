import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class HeroArm here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class HeroArm extends SmoothMover
{
    private GreenfootImage[] bowLeft = new GreenfootImage[7];
    private GreenfootImage[] bowRight = new GreenfootImage[7];
    
    private GreenfootImage bowImage;
    
    public boolean isAnimating;
    public SimpleTimer bowAnimationTimer = new SimpleTimer();
    
    public int bowImageIndex = 0;
    
    public HeroArm() {
        for (int i = 0; i < bowLeft.length; i++) {
            bowLeft[i] = new GreenfootImage("bow/bow" + i + ".png");
            //bowLeft[i].mirrorHorizontally();
            bowLeft[i].mirrorVertically();
            
            bowRight[i] = new GreenfootImage("bow/bow" + i + ".png");
            
            //bowLeft[i].scale((int)(bowLeft[i].getWidth() * 0.5), (int)(bowLeft[i].getHeight() * 0.5));
            //bowRight[i].scale((int)(bowRight[i].getWidth() * 0.5), (int)(bowRight[i].getHeight() * 0.5));
            bowLeft[i].scale(40, 40);
            bowRight[i].scale(40, 40);
        }
        
        this.bowImage = bowLeft[0];
        setImage(bowImage);
    }
    
    public void act() { 
        if (isAnimating) {
            bowAnimationTimer.mark();
            animateBow(Hero.hero.facing);
        }
        if (!Hero.hero.isAttacking) {
            if (Hero.hero.facing.equals("right")) setRotation(0);
            else setRotation(180);
        }
    }
    
    public void setPos(double x, double y, String facing) {
        if (facing.equals("right")) x += 5;
        else x -= 5;
        setLocation(x, y);
    }
    
    public void animateBow(String facing) {
        if (bowAnimationTimer.millisElapsed() < Hero.hero.attackSpeed / 12.0) return;
        bowAnimationTimer.mark();
        
        if (bowImageIndex < bowRight.length) {
            if (facing.equals("right")) setImage(bowRight[bowImageIndex]);
            else setImage(bowLeft[bowImageIndex]);
            bowImageIndex++;
        }
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
