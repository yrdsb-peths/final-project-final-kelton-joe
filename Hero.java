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
    final int minDashCooldown = 800;
    int dashCooldown;
    
    // unique upgrades
    int frostbiteLvl;
    int scorchLvl;
    
    // facing direction
    String facing = "right";
    
    private final int xScale = 40;
    private final int yScale = 40;
    
    // idle animations
    GreenfootImage[] idleLeft = new GreenfootImage[4];
    GreenfootImage[] idleRight = new GreenfootImage[4];
    SimpleTimer idleAnimationTimer = new SimpleTimer();
    private int idleImageIndex = 0;
    SimpleTimer lastAttackTimer = new SimpleTimer();
    
    // death animations
    GreenfootImage[] death = new GreenfootImage[6];
    SimpleTimer deathAnimationTimer = new SimpleTimer();
    public boolean isDead = false;
    private int deathImageIndex = 0;
    
    // attack animations
    GreenfootImage[] heroLeft = new GreenfootImage[6];
    GreenfootImage[] heroRight = new GreenfootImage[6];
    SimpleTimer heroAnimationTimer = new SimpleTimer();
    private int heroImageIndex = 0;
    
    // hurt animations
    GreenfootImage[] hurtLeft = new GreenfootImage[4];
    GreenfootImage[] hurtRight = new GreenfootImage[4];
    SimpleTimer hurtAnimationTimer = new SimpleTimer();
    private int hurtImageIndex = 0;
    public boolean isHurt = false;
    
    // dash bar cooldown
    private final double dashBarScale = 0.2;
    private final int barX = 730;
    private final int barY = 40;
    RedBar redBar = new RedBar(dashBarScale);
    GreenBar greenBar = new GreenBar(dashBarScale);
    double cooldownPercent;
    
    /**
     * Constructor for Hero Class
     */
    public Hero() {
        for (int i = 0; i < idleLeft.length; i++) {
            idleRight[i] = new GreenfootImage("images/idle/idle" + i + ".png");
            idleRight[i].scale(xScale, yScale);
            
            idleLeft[i] = new GreenfootImage("images/idle/idle" + i + ".png");
            idleLeft[i].mirrorHorizontally();
            idleLeft[i].scale(xScale, yScale);
        }
        
        for (int i = 0; i < death.length; i++) {
            death[i] = new GreenfootImage("images/death/death" + i + ".png");
            death[i].scale(xScale, yScale);
        }
        
        for (int i = 0; i < heroLeft.length; i++) {
            heroRight[i] = new GreenfootImage("images/hero/hero" + i + ".png");
            heroRight[i].scale(xScale,yScale);
            
            heroLeft[i] = new GreenfootImage("images/hero/hero" + i + ".png");
            heroLeft[i].mirrorHorizontally();
            heroLeft[i].scale(xScale, yScale);
        }
        
        for (int i = 0; i < hurtLeft.length; i++) {
            hurtRight[i] = new GreenfootImage("images/hurt/hurt" + i + ".png");
            hurtRight[i].scale(xScale, yScale);
            
            hurtLeft[i] = new GreenfootImage("images/hurt/hurt" + i + ".png");
            hurtLeft[i].mirrorHorizontally();
            hurtLeft[i].scale(xScale, yScale);
        }
        
        idleAnimationTimer.mark();
        setImage(idleRight[0]);
        
        this.hero = this;
        
        // initializes default stats
        currentHp = 3;
        maxHp = 3;
        speed = 1.15;
        attackRange = 200;
        regenInterval = 5000;
        regenAmount = 1;
        attackSpeed = 1000.0;
        attack = 1.0;
        projectileSpeed = 1.0;
        critRate = 10.0;
        critDamage = 100.0;
        
        isDashing = false;
        dashLength = 300;
        dashMultiplier = 1.0;
        dashCooldown = 3000;
        
        frostbiteLvl = 0;
        scorchLvl = 0;
        
        attackCooldown.mark();
        regenCooldown.mark();
        
        dashTimer.mark();
        
        GameWorld.gameWorld.addObject(redBar, barX, barY);
        GameWorld.gameWorld.addObject(greenBar, barX, barY);
    }
    
    /**
     * Hero movements, attacks and health updates
     */
    public void act()
    {
        if (isDead) animateDeath();
        else {
            redBar.setPos(barX, barY);
            cooldownPercent = (double) dashTimer.millisElapsed() / (double) dashCooldown;
            cooldownPercent = Math.min(cooldownPercent, 1);
            cooldownPercent = Math.max(cooldownPercent, 0);
            greenBar.setPos(barX, barY, cooldownPercent);
            
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
                facing = "left";
            }
            
            if (Greenfoot.isKeyDown("d")) {
                this.setLocation(getExactX() + speed*dashMultiplier, getExactY());
                facing = "right";
            }
            
            // attack
            if (Greenfoot.isKeyDown("space")) {
                if (attackCooldown.millisElapsed() >= attackSpeed) {
                    attack();
                    attackCooldown.mark();
                    lastAttackTimer.mark();
                }
            }
            
            if (regenCooldown.millisElapsed() >= regenInterval) {
                currentHp = Math.min(regenAmount + currentHp, maxHp);
                GameWorld.healthBar.setValue(currentHp + "/" + maxHp + " hp");
                regenCooldown.mark();
            }
            // first check if hurt
            if (isHurt) animateHurt();
            
            // then check if idle
            else if (lastAttackTimer.millisElapsed() >= 3000) animateIdle();
            
            // otherwise animate attack
            else animateHero();
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
                                            damage, isCrit, frostbiteLvl, scorchLvl);
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
            case "dashCooldown":
                dashCooldown = Math.max(dashCooldown - (int) value, minDashCooldown);
                break;
        }
    }
    
    public void setStat(String uniqueTrait) {
        switch(uniqueTrait) {
            case "Frostbite":
                if (frostbiteLvl < 2) frostbiteLvl++;
                break;
            case "Scorch":
                if (scorchLvl < 2) scorchLvl++;
        }
    }
    
    public void animateIdle() {
        if (idleAnimationTimer.millisElapsed() < 100) return;
        idleAnimationTimer.mark();
        
        if (facing.equals("right")) {
            setImage(idleRight[idleImageIndex]);
            idleImageIndex = (idleImageIndex + 1) % idleRight.length;
        }
        else {
            setImage(idleLeft[idleImageIndex]);
            idleImageIndex = (idleImageIndex + 1) % idleLeft.length;
        }
    }
    
    public void animateDeath() {
        Enemy.removeAll();
        
        if (deathAnimationTimer.millisElapsed() < 250) return;
        deathAnimationTimer.mark();
        
        if (deathImageIndex < death.length) {
            setImage(death[deathImageIndex]);
            deathImageIndex++;
        }
        else {
            GameWorld.gameWorld.gameOver = true;
            
            EndScreen endScreen = new EndScreen();
            Greenfoot.setWorld(endScreen);
        }
    }
    
    public void animateHero() {
        if (heroAnimationTimer.millisElapsed() < 150) return;
        heroAnimationTimer.mark();
        
        if (facing.equals("right")) {
            setImage(heroRight[heroImageIndex]);
            heroImageIndex = (heroImageIndex + 1) % heroRight.length;
        }
        else {
            setImage(heroLeft[heroImageIndex]);
            heroImageIndex = (heroImageIndex + 1) % heroLeft.length;
        }
    }
    
    public void animateHurt() {
        if (hurtAnimationTimer.millisElapsed() < 150) return;
        hurtAnimationTimer.mark();
        
        if (hurtImageIndex < hurtLeft.length) {
            if (facing.equals("right")) setImage(hurtRight[hurtImageIndex]);
            else setImage(hurtLeft[hurtImageIndex]);
            hurtImageIndex++;
        }
        else {
            isHurt = false;
            hurtImageIndex = 0;
        }
    }
}
