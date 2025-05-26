import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Class for the playable character
 * 
 * @author Kelton and Joe
 * @version May 2025
 * 
 * Uses smooth mover class for more accurate movements
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
    private final double maxAttackRange = 500;
    
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
    double critRate;
    double critDamage;
    
    // crit rate and damage as a percent
    private double critMultiplier;
    private boolean isCrit;
    
    // position of the hero
    int x, y;
    
    // damage dealt calculation variable
    double damageDealt;
    
    // dash
    boolean isDashing;
    SimpleTimer dashTimer = new SimpleTimer();
    double dashLength;
    double dashMultiplier;
    int dashCooldown;
    
    int frostbiteLvl;
    
    
    /**
     * Constructor for Hero Class
     */
    public Hero() {
        setImage("images/bee.png");
        GreenfootImage hero = getImage();
        hero.scale(25, 25);
        
        this.hero = this;
        
        // initializes default stats
        currentHp = 5;
        maxHp = 5;
        speed = 1.0;
        attackRange = 200;
        regenInterval = 10000;
        regenAmount = 1;
        attackSpeed = 1200.0;
        attack = 1.0;
        projectileSpeed = 1.5;
        critRate = 10.0;
        critDamage = 100.0;
        
        isDashing = false;
        dashLength = 500;
        dashMultiplier = 1.0;
        dashCooldown = 2000;
        
        frostbiteLvl = 0;
        
        attackCooldown.mark();
        regenCooldown.mark();
        
        dashTimer.mark();
    }
    
    /**
     * Hero movements, attacks and health updates
     */
    public void act()
    {
        if (GameWorld.gameOver) {
            EndScreen endScreen = new EndScreen();
            Greenfoot.setWorld(endScreen);
        } 
        else {
            // movement
            if (Greenfoot.isKeyDown("e")) {
                if (dashTimer.millisElapsed() >= dashCooldown) {
                    isDashing = true;
                    dashMultiplier = 2.5;
                    dashTimer.mark();
                }
            }
            if (dashTimer.millisElapsed() >= dashLength) {
                isDashing = false;
                dashMultiplier = 1.0;
            }
            
            if (Greenfoot.isKeyDown("w")) {
                this.setLocation(getExactX(), getExactY() - speed*dashMultiplier);
            }
            
            if (Greenfoot.isKeyDown("s")) {
                this.setLocation(getExactX(), getExactY() + speed*dashMultiplier);
            }
            
            if (Greenfoot.isKeyDown("a")) {
                this.setLocation(getExactX() - speed*dashMultiplier, getExactY());
            }
            
            if (Greenfoot.isKeyDown("d")) {
                this.setLocation(getExactX() + speed*dashMultiplier, getExactY());
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
     * Method to find the closest enemy in range
     * 
     * @return closest enemy in range of hero
     */
    public Enemy findClosestEnemy() {
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
        Enemy closestEnemy = findClosestEnemy();
        
        if (closestEnemy != null && closestEnemy.hitpoints > 0) {
            faceEnemy(closestEnemy);
            
            // crit generation
            if (Greenfoot.getRandomNumber(100) <= critRate) {
                isCrit = true;
                critMultiplier = 1.0 + (critDamage/100.0);
                damageDealt = attack * critMultiplier;
            }
            else {
              damageDealt = attack;
              isCrit = false;
            }
            
            fireProjectile(damageDealt, closestEnemy);

            //closestEnemy.removeHp((int) attack);
        }
    }
    
    /**
     * Face an enemy
     * 
     * @param enemy: enemy to face
     */
    private void faceEnemy(Enemy enemy) {
        double dx = enemy.getExactX() - getExactX();
        double dy = enemy.getExactY() - getExactY();
    
        // Calculate the angle in degrees
        double angle = Math.toDegrees(Math.atan2(dy, dx));
    
        // Rotate the hero's image to face the enemy
        setRotation((int) angle);
    }
    
    private void fireProjectile(double damage, Enemy enemy) {
        double dx = enemy.getExactX() - getExactX();
        double dy = enemy.getExactY() - getExactY();
        
        double magnitude = Math.sqrt(dx*dx + dy*dy);
        
        double normalizedX = dx / magnitude;
        double normalizedY = dy / magnitude;
        
        Projectile arrow = new Projectile(normalizedX, normalizedY, projectileSpeed,
                                            damage, isCrit, frostbiteLvl);
        GameWorld.gameWorld.addObject(arrow, (int)getExactX(), (int)getExactY());
    }
    
    /**
     * Changes a stat for the Hero
     * 
     * @param value: value of the stat to be changed by
     * @param stat: the stat to be changed
     */
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
                projectileSpeed = Math.min(projectileSpeed + value, maxProjectileSpeed);
                if (projectileSpeed == maxProjectileSpeed) {
                    Upgrade.type.remove("projectileSpeed");
                }
            case "speed":
                speed += value;
                break;
            case "critRate":
                critRate += value;
                while (critRate > 100.0) {
                    critRate--;
                    critDamage += 3;
                }
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
                while (critRate > 100.0) {
                    critRate--;
                    critDamage += 3;
                }
                critDamage += value * 2.0;
                break;
            case "dashLength":
                dashLength += value;
                break;
            case "dashMultiplier":
                dashMultiplier += value;
                break;
            case "projectile":
                projectileSpeed = Math.min(projectileSpeed + value/100.0, maxProjectileSpeed);
                if (projectileSpeed == maxProjectileSpeed) {
                    Upgrade.type.remove("projectileSpeed");
                }
                
                attackRange = Math.min(attackRange + value, maxAttackRange);
                if (attackRange == maxAttackRange) {
                    Upgrade.type.remove("attackRange");
                }
                
                attackSpeed = Math.max(attackSpeed - value*5.0, maxAttackSpeed);
                if (attackSpeed == maxAttackSpeed) {
                    Upgrade.type.remove("attackSpeed");
                }
                break;
        }
    }
    
    public void setStat(String uniqueTrait) {
        switch(uniqueTrait) {
            case "Frostbite":
                if (frostbiteLvl < 2) frostbiteLvl++;
                break;
        }
    }
}
