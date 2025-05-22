import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Enemy here.
 * 
 * @author Kelton and Joe
 * @version May 2025
 */
public class Enemy extends SmoothMover
{
    private double speed = 1.0;
    
    public int hitpoints;
    
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    
    public Enemy(int hitpoints) {
        setImage("images/balloon1.png");
        
        this.hitpoints = hitpoints;
        
        enemies.add(this);
        
        GreenfootImage enemy = getImage();
        enemy.scale(25, 25);
    }
    
    /**
     * Act - do whatever the Enemy wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        double dx = Hero.hero.getExactX() - getExactX();
        double dy = Hero.hero.getExactY() - getExactY();
        
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        
        double normalizedDx = dx / magnitude;
        double normalizedDy = dy / magnitude;
        
        setLocation(getExactX() + (normalizedDx * speed), getExactY() + (normalizedDy * speed));
    }
    
    public void removeHp(int damage) {
        hitpoints -= damage;
        if (hitpoints <= 0) {
            getWorld().removeObject(this);
            enemies.remove(this);
        }
    }
}
