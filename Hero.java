import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Class for the playable character
 * 
 * @author Kelton and Joe
 * @version May 2025
 */
public class Hero extends SmoothMover
{
    // hero stats
    // attack dmg in hp units
    private int attack = 1;
    // attack freq in ms
    private int attackSpeed = 1000;
    // attack range
    private final int attackRange = 50;
    // hero hp 
    private int health = 3;
    // movement speed
    private double speed = 2.0;
    
    /**
     * Movement code for the hero
     */
    int x, y;
    
    public Hero() {
        setImage("images/bee.png");
        GreenfootImage hero = getImage();
        hero.scale(25, 25);
    }
    
    public void act()
    {
        // movement
        if (Greenfoot.isKeyDown("w")) {
            this.setLocation(getExactX(), getExactY() - speed);
        }
        
        if (Greenfoot.isKeyDown("s")) {
            this.setLocation(getExactX(), getExactY() + speed);
        }
        
        if (Greenfoot.isKeyDown("a")) {
            this.setLocation(getExactX() - speed, getExactY());
        }
        
        if (Greenfoot.isKeyDown("d")) {
            this.setLocation(getExactX() + speed, getExactY());
        }
        
        // attack
        if (Greenfoot.isKeyDown("space")) {
            Attack(attack);
        }
    }
    
    /**
     * finds the closest enemy in range of the Hero
     */
    public Enemy isInRange() {
        Enemy closestEnemy = null;
        double smallestDistance = attackRange;
        
        for (int i = 0; i < Enemy.enemies.size(); i++) {
            double dx = Enemy.enemies.get(i).getExactX() - getExactX();
            double dy = Enemy.enemies.get(i).getExactY() - getExactY();
            
            double distance = Math.sqrt(dx*dx + dy*dy);
            
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestEnemy = Enemy.enemies.get(i);
            }
        }
        return closestEnemy;
    }
    
    /**
     * attacks the closest enemy in range 
     */
    public void Attack(int atk) {
        if (isInRange() != null && isInRange().hitpoints > 0) isInRange().removeHp(atk);
    }
}
