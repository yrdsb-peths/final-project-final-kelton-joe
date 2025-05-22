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
    public static Hero hero;
    
    // hero stats
    // attack dmg in hp units
    private int attack = 1;
    
    // attack freq in ms
    private int attackSpeed = 500;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // attack range
    private final int attackRange = 75;
    
    // hero hp 
    public int health = 3;
    
    // movement speed
    private double speed = 2.0;
    
    double critRate = 5.0;
    double critDamage = 50.0;
    
    /**
     * Movement code for the hero
     */
    int x, y;
    
    public Hero() {
        setImage("images/bee.png");
        GreenfootImage hero = getImage();
        hero.scale(25, 25);
        
        this.hero = this;
        
        attackCooldown.mark();
    }
    
    public void act()
    {
        
        if (!MyWorld.gameOver) {
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
                if (attackCooldown.millisElapsed() >= attackSpeed) {
                    Attack(attack);
                    attackCooldown.mark();
                }
            }
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
