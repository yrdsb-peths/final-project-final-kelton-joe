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
    
    private int hitpoints;
    
    public Enemy(Hero hero) {
        setImage("images/balloon1.png");
        
        this.hero = hero;
        hitpoints = 3;
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
    
    public void removeHp(int hitpoints) {
        this.hitpoints -= hitpoints;
    }
}
