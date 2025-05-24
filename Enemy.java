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
    // speed (default 1.5)
    private double speed = 0.75;
    
    // health
    public int hitpoints;
    
    // attack
    private int attack = 1;
    
    // attack speed
    private int attackSpeed = 1000;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // list of enemies
    public static ArrayList<Enemy> enemies;
    
    public Enemy(int hitpoints) {
        setImage("images/balloon1.png");
        
        this.hitpoints = hitpoints;
        
        enemies.add(this);
        
        GreenfootImage enemy = getImage();
        enemy.scale(25, 25);
        
        attackCooldown.mark();
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
        
        if (this.isTouching(Hero.class)) {
            if (attackCooldown.millisElapsed() >= attackSpeed) {
                attack();
                attackCooldown.mark();
            }
        }
    }
    
    public void removeHp(int damage) {
        hitpoints -= damage;
        if (hitpoints <= 0) {
            getWorld().removeObject(this);
            enemies.remove(this);
        }
    }
    
    public void attack() {
        Hero.hero.currentHp-= this.attack;
        GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        
        if (Hero.hero.currentHp <= 0) GameWorld.gameOver = true;
    }
}
