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
    private final double maxAttackSpeed = 100.0;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // attack range
    private double attackRange = 75;
    private final double maxAttackRange = 200;
    
    // hero hp 
    public int currentHp = 3;
    public int maxHp = 3;
    
    public int regenInterval = 15000;
    public final int minRegenInterval = 1000;
    public int regenAmount = 1;
    SimpleTimer regenCooldown = new SimpleTimer();
    
    // movement speed
    private double speed = 2.0;
    
    // crit rate and damage
    double critRate = 5;
    double critDamage = 50;
    
    // position of the hero
    int x, y;
    
    public Hero() {
        setImage("images/bee.png");
        GreenfootImage hero = getImage();
        hero.scale(25, 25);
        
        this.hero = this;
        
        attackCooldown.mark();
        regenCooldown.mark();
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
            if (regenCooldown.millisElapsed() >= regenInterval) {
                currentHp = Math.min(regenAmount + currentHp, maxHp);
                GameWorld.healthBar.setValue(currentHp + "/" + maxHp + " hp");
                regenCooldown.mark();
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
                maxHp += value;
                currentHp += value;
                GameWorld.healthBar.setValue(currentHp + "/" + maxHp + " hp");
                break;
            case "attackSpeed":
                attackSpeed = Math.max(attackSpeed + value, maxAttackSpeed);
                if (attackSpeed == maxAttackSpeed) {
                    Upgrade.type.remove("attackSpeed");
                }
                break;
            case "attackRange":
                attackRange = Math.min(attackRange + value, maxAttackRange);
                if (attackRange == maxAttackRange) {
                    Upgrade.type.remove("attackRange");
                }
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
            case "regenInterval":
                regenInterval = Math.max(regenInterval + (int) value, minRegenInterval);
                if (regenInterval == minRegenInterval) {
                    Upgrade.type.remove("regenInterval");
                }
                break;
            case "regenAmount":
                regenAmount += value;
                break;
        }
    }
}
