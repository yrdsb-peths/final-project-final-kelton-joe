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
    public double attack;
    
    // attack speed
    public double attackSpeed;
    private final double maxAttackSpeed = 200.0;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // attack range
    private double attackRange;
    private final double maxAttackRange = 600;
    
    // projectile speed
    public double projectileSpeed;
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
    public boolean isCrit;
    
    // position of the hero
    int x, y;
    
    // damage dealt calculation variable
    double damageDealt;
    
    // dash
    boolean isDashing;
    SimpleTimer dashTimer = new SimpleTimer();
    double dashLength;
    double dashMultiplier;
    final int minDashCooldown = 600;
    int dashCooldown;
    
    // unique upgrades
    int frostbiteLvl;
    int scorchLvl;
    int vampireLvl;
    int rogueLvl;
    int jesterLvl;
    int sharpshotLvl;
    int arcaneEchoLvl;
    int spectralVeilLvl;
    int vortexLvl;
    int bloodPactLvl;
    
    // arcane echo
    int echoChance;
    double echoMult;
    private final int echoWait = 250;
    boolean hasEchoed;
    SimpleTimer echoTimer = new SimpleTimer();
    
    // spectral veil
    public boolean isImmune;
    int immuneChance;
    int immuneDuration;
    SimpleTimer immunityTimer = new SimpleTimer();
    SpectralVeilActive indicator = new SpectralVeilActive();
    
    // violent vortex
    int tornadoChance;
    
    // facing direction
    String facing = "right";
    
    public final int xScale = 40;
    public final int yScale = 40;
    
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
    
    public boolean isAttacking;
    
    // dash bar cooldown
    private final double dashBarScale = 0.2;
    private final int barX = 730;
    private final int barY = 40;
    RedBar redBar = new RedBar(dashBarScale);
    GreenBar greenBar = new GreenBar(dashBarScale);
    double cooldownPercent;
    
    private HeroArm heroArm;
    
    double dx, dy, angle;
    
    /**
     * Constructor for Hero Class
     */
    public Hero(HeroArm heroArm) {
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
        
        redBar.getImage().setColor(new greenfoot.Color(137, 148, 153));
        redBar.getImage().fill();
        greenBar.getImage().setColor(new greenfoot.Color(15, 82, 186));
        greenBar.getImage().fill();
        
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
        sharpshotLvl = 0;
        
        attackCooldown.mark();
        regenCooldown.mark();
        lastAttackTimer.mark();
        dashTimer.mark();
        
        GameWorld.gameWorld.addObject(redBar, barX, barY);
        GameWorld.gameWorld.addObject(greenBar, barX, barY);
        
        this.heroArm = heroArm;
    }
    
    /**
     * Hero movements, attacks and health updates
     */
    public void act()
    {
        if (isDead) {
            GameWorld.gameWorld.removeObject(heroArm);
            animateDeath();
        }
        else { 
            if (immunityTimer.millisElapsed() >= immuneDuration) {
                isImmune = false;
                GameWorld.gameWorld.removeObject(indicator);
            }
            
            else {
                GameWorld.gameWorld.addObject(indicator, 50, 550);
            }
            
            // dash bar
            redBar.setPos(barX, barY);
            cooldownPercent = (double) dashTimer.millisElapsed() / (double) dashCooldown;
            cooldownPercent = Math.min(cooldownPercent, 1);
            cooldownPercent = Math.max(cooldownPercent, 0);
            greenBar.setPos(barX, barY, cooldownPercent);
            
            if (rogueLvl > 0) {
                maxHp = 3;
                GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
            }
            
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
            
            heroArm.setPos(getExactX(), getExactY(), facing);
            
            // attack
            if (Greenfoot.isKeyDown("space")) {
                if (attackCooldown.millisElapsed() >= attackSpeed) {
                    attack(false);
                    attackCooldown.mark();
                }
            }
            
            // Check for arcane echo delayed attack
            if (!hasEchoed && echoTimer.millisElapsed() >= echoWait) {
                attack(true);          // echo attack
                hasEchoed = true;      // prevent repeat
            }
            
            if (regenCooldown.millisElapsed() >= regenInterval) {
                currentHp = Math.min(regenAmount + currentHp, maxHp);
                GameWorld.healthBar.setValue(currentHp + "/" + maxHp + " hp");
                regenCooldown.mark();
            }
            
            // check if hurt
            if (isHurt) animateHurt();
            
            // then check if idle
            else if (lastAttackTimer.millisElapsed() >= 3000) {
                animateIdle();
                GameWorld.gameWorld.removeObject(heroArm);
            }
            
            // animate attack
            else {
                if (isAttacking) {
                    GameWorld.gameWorld.addObject(heroArm, (int) getExactX(), (int) getExactY());
                    heroArm.animateBow(facing);
                }
                animateHero();
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
    public void attack(boolean echo) {
        Enemy closestEnemy = findClosestEnemy();
        
        if (closestEnemy != null && closestEnemy.hitpoints > 0) {
            heroArm.faceEnemy(closestEnemy);
            
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
            
            if (echo) damageDealt *= echoMult;
            
            if (bloodPactLvl == 0) fireProjectile(damageDealt, closestEnemy);
            else {
                if (isCrit) {
                    critMultiplier = 1.0 + (critDamage/100.0);
                    damageDealt = maxHp * 0.9 * critMultiplier;
                }
                else damageDealt = maxHp * 0.4;
                
                Slash slash = new Slash(damageDealt, isCrit);
                
                dx = closestEnemy.getExactX() - getExactX();
                dy = closestEnemy.getExactY() - getExactY();
                angle = Math.toDegrees(Math.atan2(dy, dx));
                
                slash.setRotation((int) angle - 90);
                
                GameWorld.gameWorld.addObject(slash, (int) getExactX(), (int) getExactY());
            }
            
            lastAttackTimer.mark();

            if (!echo && arcaneEchoLvl > 0) {
                if (Greenfoot.getRandomNumber(100) < echoChance) {
                    echoTimer.mark();       // start the timer
                    hasEchoed = false;      // allow echo to happen
                } else {
                    hasEchoed = true;       // no echo will happen
                }
            }
        }
    }
    
    private void fireProjectile(double damage, Enemy enemy) {
        isAttacking = true;
        
        double dx = enemy.getExactX() - getExactX();
        double dy = enemy.getExactY() - getExactY();
        
        double magnitude = Math.sqrt(dx*dx + dy*dy);
        
        double normalizedX = dx / magnitude;
        double normalizedY = dy / magnitude;
        
        Projectile arrow = new Projectile(normalizedX, normalizedY, projectileSpeed, damage, isCrit);
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
                if (dashCooldown == minDashCooldown) {
                    Upgrade.type.remove("dashCooldown");
                }
                break;
        }
    }
    
    public void setStat(String uniqueTrait) {
        switch(uniqueTrait) {
            case "Frostbite":
                if (frostbiteLvl < 2) frostbiteLvl++;
                else Upgrade.uniques.remove("Frostbite");
                break;
            case "Scorch":
                if (scorchLvl < 2) {
                    scorchLvl++;
                }
                else Upgrade.uniques.remove("Scorch");
                break;
            case "Sharpshot":
                switch (sharpshotLvl) {
                    case 0:
                        sharpshotLvl++;
                        projectileSpeed = maxProjectileSpeed;
                        Upgrade.type.remove("projectileSpeed");
                            break;
                    case 1:
                        sharpshotLvl++;
                        attackRange = maxAttackRange;
                        Upgrade.type.remove("attackRange");
                        Upgrade.uniques.remove("Sharpshot");
                        break;
                }
                break;
            case "Vampire":
                if (vampireLvl == 0) {
                    vampireLvl++;
                    speed -= 0.1;
                    maxHp += 3;
                } 
                else if (vampireLvl == 1) {
                    vampireLvl++;
                    maxHp += 3;
                }
                else Upgrade.uniques.remove("Vampire");
                break;
            case "Rogue":
                if (rogueLvl < 2) {
                    rogueLvl++;
                    if (rogueLvl == 1) {
                        critRate = Math.min(critRate + 30.0, 100.0); 
                        attackSpeed = Math.max(attackSpeed - 300.0, maxAttackSpeed);
                        critDamage += 60.0; 
                        speed += 0.5; 
                        dashCooldown = Math.max(dashCooldown - 1000, minDashCooldown); 
                        attackRange = 80;
                        currentHp = 3;
                        maxHp = 3;
                        GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
                        Upgrade.type.remove("health");
                        Upgrade.type.remove("attackRange");
                    }
                    else {
                        critRate = 100.0;
                        critDamage += 100.0;
                        speed *= 1.5;
                        dashCooldown = minDashCooldown;
                        attackSpeed = maxAttackSpeed;
                        Upgrade.type.remove("attackSpeed");
                        Upgrade.type.remove("dashCooldown");
                    }
                }
                else Upgrade.uniques.remove("Rogue");
                break;
            case "Jester":
                if (jesterLvl < 2) jesterLvl++;
                else Upgrade.uniques.remove("Jester");
                break;
            case "Arcane Echo":
                if (arcaneEchoLvl < 2) {
                    arcaneEchoLvl++;
                    if (arcaneEchoLvl == 1) {
                        echoChance = 35;
                        echoMult = 0.6;
                    }
                    else {
                        echoChance = 100;
                        echoMult = 1.2;
                    }
                }
                else Upgrade.uniques.remove("Arcane Echo");
                break;
            case "Spectral Veil":
                if (spectralVeilLvl < 1) {
                    spectralVeilLvl++;
                    immuneChance = 40;
                    immuneDuration = 1200;
                }
                else Upgrade.uniques.remove("Spectral Veil");
                break;
            case "Violent Vortex":
                if (vortexLvl < 2) {
                    vortexLvl++;
                    tornadoChance = 10 * vortexLvl;
                }
                else Upgrade.uniques.remove("Violent Vortex");
                break;
            case "Blood Pact":
                if (bloodPactLvl < 2) {
                    bloodPactLvl++;
                    maxHp += 3;
                    critDamage += 30;
                    attackRange = 150;
                    Upgrade.type.remove("attackRange");
                }
                else Upgrade.uniques.remove("Blood Pact");
                break;
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
