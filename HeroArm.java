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
    
    public SimpleTimer animationTimer = new SimpleTimer();
    
    private int bowIndex;
    
    public HeroArm() {
        for (int i = 0; i < 6; i++) {
            bowLeft[i] = new GreenfootImage("bow/bow" + i + ".png");
            bowLeft[i].mirrorVertically();
            bowRight[i] = new GreenfootImage("bow/bow" + i + ".png");
            
            bowLeft[i].scale((int)(bowLeft[i].getWidth() * 0.5), (int)(bowLeft[i].getHeight() * 0.5));
            bowRight[i].scale((int)(bowRight[i].getWidth() * 0.5), (int)(bowRight[i].getHeight() * 0.5));
        }
        
        this.bowImage = bowRight[3];
        setImage(bowImage);
        
        animationTimer.mark();
    }
    
    public void act() {        
        bowImage = bowLeft[bowIndex];
        setImage(bowImage);
    }
    
    public void setPos(double x, double y) {
        setLocation(x, y + 5.0);
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
