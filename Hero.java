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
    private double attack = 1.0;
    
    // attack freq in ms
    private double attackSpeed = 500.0;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // attack range
    private double attackRange = 75;
    
    // hero hp 
    public int health = 3;
    
    // movement speed
    private double speed = 2.0;
    
    // crit rate and damage
    int critRate = 5;
    int critDamage = 50;
    
    // position of the hero
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
        if (GameWorld.gameOver) {
            EndScreen endScreen = new EndScreen();
            Greenfoot.setWorld(endScreen);
        } else {
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
                    Attack();
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
    public void Attack() {
        if (isInRange() != null && isInRange().hitpoints > 0) isInRange().removeHp((int) attack);
    }
    
    public void setStat(double value, String stat) {
        switch (stat) {
            case "attack":
                attack += value;
                break;
            case "health":
                health += value;
                GameWorld.healthBar.setValue(health + " hp");
                break;
            case "attackSpeed":
                attackSpeed += value;
                break;
            case "attackRange":
                attackRange += value;
                break;
            case "speed":
                speed += value;
                break;
            case "critRate":
                critRate += value;
                break;
            case "critDamage":
                critDamage += value;
                break;
        }
    }
}
