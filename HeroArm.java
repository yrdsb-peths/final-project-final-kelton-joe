import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class HeroArm here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class HeroArm extends SmoothMover
{
    private GreenfootImage[] bowLeft = new GreenfootImage[6];
    private GreenfootImage[] bowRight = new GreenfootImage[6];
    
    private GreenfootImage bowImage;
    
    public SimpleTimer bowAnimationTimer = new SimpleTimer();
    
    public int bowImageIndex = 0;
    
    public HeroArm() {
        for (int i = 0; i < 6; i++) {
            bowLeft[i] = new GreenfootImage("bow/bow" + i + ".png");
            bowLeft[i].mirrorHorizontally();
            
            bowRight[i] = new GreenfootImage("bow/bow" + i + ".png");
            
            //bowLeft[i].scale((int)(bowLeft[i].getWidth() * 0.5), (int)(bowLeft[i].getHeight() * 0.5));
            //bowRight[i].scale((int)(bowRight[i].getWidth() * 0.5), (int)(bowRight[i].getHeight() * 0.5));
            bowLeft[i].scale(200, 200);
            bowRight[i].scale(200,200);
        }
        
        this.bowImage = bowRight[3];
        setImage(bowImage);
    }
    
    public void act() { 
        bowAnimationTimer.mark();
    }
    
    public void setPos(double x, double y, String facing) {
        if (facing.equals("right")) x += 5;
        else x -= 5;
        setLocation(x, y);
    }
    
    public void animateBow(String facing) {
        if (bowAnimationTimer.millisElapsed() < 150) return;
        bowAnimationTimer.mark();
        
        if (bowImageIndex < bowRight.length) {
            if (facing.equals("right")) setImage(bowRight[bowImageIndex]);
            else setImage(bowLeft[bowImageIndex]);
            bowImageIndex++;
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
