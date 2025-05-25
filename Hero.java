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
    
    // attack dmg in hp units
    private double attack;
    
    // attack speed
    private double attackSpeed;
    private final double maxAttackSpeed = 100.0;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // attack range
    private double attackRange;
    private final double maxAttackRange = 200;
    
    // projectile speed
    private double projectileSpeed;
    private final double maxProjectileSpeed = 10.0;
    
    // health
    public int currentHp;
    public int maxHp;

    // natural health regeneration
    public int regenInterval;
    public final int minRegenInterval = 1000;
    public int regenAmount;
    SimpleTimer regenCooldown = new SimpleTimer();
    
    // movement speed
    private double speed;
    
    // crit rate and damage
    double critRate = 5;
    double critDamage = 20;
    
    // position of the hero
    int x, y;
    
    public Hero() {
        setImage("images/bee.png");
        GreenfootImage hero = getImage();
        hero.scale(25, 25);
        
        this.hero = this;
        
        currentHp = 5;
        maxHp = 5;
        speed = 1.0;
        attackRange = 600;
        regenInterval = 10000;
        regenAmount = 1;
        attackSpeed = 600.0;
        attack = 1.0;
        projectileSpeed = 2.0;
        
        attackCooldown.mark();
        regenCooldown.mark();
    }
    
    public void act()
    {
        if (GameWorld.gameOver) {
            EndScreen endScreen = new EndScreen();
            Greenfoot.setWorld(endScreen);
        } 
        else {
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
                    attack();
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
    public void attack() {
        Enemy closestEnemy = isInRange();
        
        if (closestEnemy != null && closestEnemy.hitpoints > 0) {
            faceEnemy(closestEnemy);
            
            fireProjectile(closestEnemy);
            //closestEnemy.removeHp((int) attack);
        }
    }
    
    private void faceEnemy(Enemy enemy) {
        double dx = enemy.getExactX() - getExactX();
        double dy = enemy.getExactY() - getExactY();
    
        // Calculate the angle in degrees
        double angle = Math.toDegrees(Math.atan2(dy, dx));
    
        // Rotate the hero's image to face the enemy
        setRotation((int) angle);
    }
    
    private void fireProjectile(Enemy enemy) {
        double dx = enemy.getExactX() - getExactX();
        double dy = enemy.getExactY() - getExactY();
        
        double magnitude = Math.sqrt(dx*dx + dy*dy);
        
        double normalizedX = dx / magnitude;
        double normalizedY = dy / magnitude;
        
        Projectile arrow = new Projectile(normalizedX, normalizedY, projectileSpeed, attack);
        GameWorld.gameWorld.addObject(arrow, (int)getExactX(), (int)getExactY());
    }
    
    public void setStat(double value, String stat) {
        switch (stat) {
            case "attack":
                attack += value;
                break;
            case "health":
                maxHp += value;
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
            case "projectileSpeed":
                projectileSpeed = Math.min(attackRange + value, maxProjectileSpeed);
                if (attackRange == maxProjectileSpeed) {
                    Upgrade.type.remove("projectileSpeed");
                }
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
            case "crit":
                critRate += value;
                critDamage += value * 2;
                break;
        }
    }
}
