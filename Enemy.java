import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Enemy here.
 * 
 * @author Kelton and Joe
 * @version May 2025
 */
public class Enemy extends SmoothMover
{
    private Hero hero;
    
    private double speed = 1.0;
    
    public Enemy(Hero hero) {
        this.hero = hero;
    }
    
    /**
     * Act - do whatever the Enemy wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        double dx = hero.getExactX() - getExactX();
        double dy = hero.getExactY() - getExactY();
        
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        
        double normalizedDx = dx / magnitude;
        double normalizedDy = dy / magnitude;
        
        setLocation(getExactX() + (normalizedDx * speed), getExactY() + (normalizedDy * speed));
        
    }
}
