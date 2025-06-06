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
    // global instance of the hero
    public static Hero hero;
    
    // attack dmg in hp units
    public double attack;
    
    // attack speed
    public double attackSpeed;
    private double maxAttackSpeed = 300.0;
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
    int damageBonus;
    
    // dash
    boolean isDashing;
    SimpleTimer dashTimer = new SimpleTimer();
    double dashLength;
    double dashMultiplier;
    double dashSpeed;
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
    int shrapnelLvl;
    int burstLvl;
    int thunderLvl;
    
    // arcane echo
    int echoChance;
    double echoMult;
    private int echoWait = 250;
    boolean hasEchoed;
    SimpleTimer echoTimer = new SimpleTimer();
    
    // spectral veil
    public boolean isImmune;
    private boolean hasChanged;
    int immuneChance;
    int immuneDuration;
    SimpleTimer immunityTimer = new SimpleTimer();
    SpectralVeilActive indicator = new SpectralVeilActive();
    
    // violent vortex
    int tornadoChance;
    
    // shrapnel shot
    int numShrapnel;
    int shrapnelChance;
    
    // thunderstrike volley
    private int spread;
    private double randomness;
    private double newDirection;
    private double newVelocityX;
    private double newVelocityY;
    private SimpleTimer delay = new SimpleTimer();
    
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
    
    // whether hero is attacking
    public boolean isAttacking;
    
    // dash bar cooldown
    private final double dashBarScale = 0.2;
    private final int barX = 730;
    private final int barY = 40;
    RedBar redBar = new RedBar(dashBarScale, dashBarScale, false);
    GreenBar greenBar = new GreenBar(dashBarScale, dashBarScale, false);
    double cooldownPercent;
    
    // hero arm
    private HeroArm heroArm;
    
    // angle between hero and enemy (for bow targeting enemies)
    double dx, dy, angle;
    
    // controls
    public static String forward = "w";
    public static String backward = "s";
    public static String left = "a";
    public static String right = "d";
    public static String dash = "e";
    public static String skill = "space";
    
    // sharpshot upgrade arrow sound
    GreenfootSound[] arrowShoot = {
        new GreenfootSound("arrows/arrow/arrow1.mp3"),
        new GreenfootSound("arrows/arrow/arrow2.mp3"),
        new GreenfootSound("arrows/arrow/arrow3.mp3"),
        new GreenfootSound("arrows/arrow/arrow4.mp3"),
        new GreenfootSound("arrows/arrow/arrow5.mp3")
    };
    int arrowIndex = 0;
    
    // sharpshot upgrade arrow sound
    GreenfootSound[] sharpshotShoot = {
        new GreenfootSound("arrows/sharpshot/sharpshot1.mp3"),
        new GreenfootSound("arrows/sharpshot/sharpshot2.mp3"),
        new GreenfootSound("arrows/sharpshot/sharpshot3.mp3"),
        new GreenfootSound("arrows/sharpshot/sharpshot4.mp3"),
        new GreenfootSound("arrows/sharpshot/sharpshot5.mp3")
    };
    int sharpshotIndex = 0;
    
    // sounds
    GreenfootSound[] dripSounds = {
        new GreenfootSound("drip/drip1.mp3"),
        new GreenfootSound("drip/drip2.mp3"),
        new GreenfootSound("drip/drip3.mp3"),
        new GreenfootSound("drip/drip4.mp3"),
        new GreenfootSound("drip/drip5.mp3")
    };
    int dripIndex = 0;
    
    GreenfootSound slashSound = new GreenfootSound("slash.mp3");
    GreenfootSound dashSound = new GreenfootSound("dash.mp3");
    
    // burn
    private int burnTicks;
    private double burnDamage;
    private SimpleTimer burnTimer = new SimpleTimer();
    
    /**
     * Constructor for Hero Class
     * 
     * @param heroArm: the hero arm (bow) of the hero
     */
    public Hero(HeroArm heroArm) {
        // sets images
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
        
        // sets sounds
        for (GreenfootSound s : arrowShoot) {
            s.setVolume(80);
        }
        
        // marks animation timer
        idleAnimationTimer.mark();
        
        // sets default image
        setImage(idleRight[0]);
        
        // global instance of the hero
        this.hero = this;
        
        // change color of the dash bar
        redBar.getImage().setColor(new greenfoot.Color(137, 148, 153));
        redBar.getImage().fill();
        greenBar.getImage().setColor(new greenfoot.Color(15, 82, 186));
        greenBar.getImage().fill();
        
        // initializes default stats
        currentHp = 100;
        maxHp = 100;
        speed = 1.15;
        attackRange = 200;
        regenInterval = 5000;
        regenAmount = 5;
        attackSpeed = 1000.0;
        attack = 10.0;
        projectileSpeed = 1.0;
        critRate = 5.0;
        critDamage = 50.0;
        
        // dash
        isDashing = false;
        dashLength = 300;
        dashMultiplier = 1.0;
        dashSpeed = 2.5;
        dashCooldown = 3000;
        
        // marks cooldowns
        attackCooldown.mark();
        regenCooldown.mark();
        lastAttackTimer.mark();
        dashTimer.mark();
        
        // adds dash bars
        GameWorld.gameWorld.addObject(redBar, barX, barY);
        GameWorld.gameWorld.addObject(greenBar, barX, barY);
        
        // sets hero arm (bow)
        this.heroArm = heroArm;
    }
    
    /**
     * Hero movements, attacks and health updates
     */
    public void act()
    {
        // animates death
        if (isDead) {
            GameWorld.gameWorld.removeObject(heroArm);
            animateDeath();
        }
        else { 
            // burn damage
            if (burnTimer.millisElapsed() >= 1000) {
                if (burnTicks > 0) {
                    // burn damage
                    currentHp = Math.max(0, (int) (currentHp - burnDamage));
                    burnTicks--;
                    burnTimer.mark();
                    
                    // updates health bar after burn
                    GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
                }
            }
        
            // spectral veil immunity things
            if (spectralVeilLvl > 0) {
                if (immunityTimer.millisElapsed() >= immuneDuration && isImmune) {
                    isImmune = false;
                    hasChanged = false;
                    damageBonus -= 30;
                    GameWorld.gameWorld.removeObject(indicator);
                }
                else if (isImmune) {
                    if (!hasChanged)  {
                        damageBonus += 30;
                        hasChanged = true;
                    }
                    GameWorld.gameWorld.addObject(indicator, 50, 550);
                }
            }
            
            // dash bar
            redBar.setPos(barX, barY);
            cooldownPercent = (double) dashTimer.millisElapsed() / (double) dashCooldown;
            cooldownPercent = Math.min(cooldownPercent, 1);
            cooldownPercent = Math.max(cooldownPercent, 0);
            greenBar.setPos(barX, barY, cooldownPercent);
            
            // rogue final stats
            if (rogueLvl > 0) {
                maxHp = 30;
                GameWorld.healthBar.setValue(Math.max(Hero.hero.currentHp, 0) + "/" + Hero.hero.maxHp + " hp");
                attackRange = 110;
            }
            
            // movement
            if (Greenfoot.isKeyDown(dash)) {
                if (dashTimer.millisElapsed() >= dashCooldown) {
                    // play dash sound
                    dashSound.play();
                    
                    // increases speed while dashing
                    dashMultiplier = dashSpeed;
                    isDashing = true;
                    dashTimer.mark();
                }
            }
            
            // dash cooldown
            if (dashTimer.millisElapsed() >= dashLength) {
                isDashing = false;
                dashMultiplier = 1.0;
            }
            
            // movement
            if (Greenfoot.isKeyDown(forward)) this.setLocation(getExactX(), getExactY() - speed*dashMultiplier);
            if (Greenfoot.isKeyDown(backward)) this.setLocation(getExactX(), getExactY() + speed*dashMultiplier);
            
            // facing direction
            if (Greenfoot.isKeyDown(left)) {
                this.setLocation(getExactX() - speed*dashMultiplier, getExactY());
                facing = "left";
            }
            
            if (Greenfoot.isKeyDown(right)) {
                this.setLocation(getExactX() + speed*dashMultiplier, getExactY());
                facing = "right";
            }
            
            heroArm.setPos(getExactX(), getExactY(), facing);
            
            // attack
            if (Greenfoot.isKeyDown(skill)) {
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
                GameWorld.healthBar.setValue(Math.max(Hero.hero.currentHp, 0) + "/" + maxHp + " hp");
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
            // make bow face enemy
            heroArm.faceEnemy(closestEnemy);
            
            // crit generation
            if (Greenfoot.getRandomNumber(100) <= critRate) {
                isCrit = true;
                critMultiplier = 1.0 + (critDamage/100.0);
                damageDealt = attack * critMultiplier * (1 + damageBonus / 100.0);
            }
            else {
              damageDealt = attack * (1 + damageBonus / 100.0);
              isCrit = false;
            }
            
            // echo damage
            if (echo) damageDealt *= echoMult;
            
            // fire only if no blood pact
            if (bloodPactLvl == 0) fireProjectile(damageDealt, closestEnemy);
            else {
                // additional crit scaling
                if (isCrit) {
                    critMultiplier = 1.0 + (critDamage/100.0);
                    damageDealt = maxHp * 0.9 * critMultiplier;
                }
                // regular hp scaling
                else damageDealt = maxHp * 0.4;
                
                // creates slash
                Slash slash = new Slash(damageDealt, isCrit);
                
                // plays slash sound
                slashSound.play();
                
                // consumes health
                currentHp -= maxHp * 0.2;
                if (currentHp <= 0) isDead = true;
                
                // calculates rotation
                dx = closestEnemy.getExactX() - getExactX();
                dy = closestEnemy.getExactY() - getExactY();
                angle = Math.toDegrees(Math.atan2(dy, dx));
                slash.setRotation((int) angle - 90);
                
                // adds to world
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
        
        double nx = dx / magnitude;
        double ny = dy / magnitude;
        
        if (burstLvl == 0) {
            if (sharpshotLvl > 0) {
                sharpshotShoot[sharpshotIndex].play();
                sharpshotIndex = (sharpshotIndex + 1) % sharpshotShoot.length;
            } 
            else {
                arrowShoot[arrowIndex].play();
                arrowIndex = (arrowIndex + 1) % arrowShoot.length;
            }
            
            if (thunderLvl > 0) {
                for (int j = 0; j < thunderLvl * 5; j++) {
                    randomness = (Greenfoot.getRandomNumber(spread * 2) - spread);
                    newDirection = Math.atan2(ny, nx) + Math.toRadians(randomness);
                    newVelocityX = Math.cos(newDirection);
                    newVelocityY = Math.sin(newDirection);
                    
                    Projectile arrow = new Projectile(newVelocityX, newVelocityY, projectileSpeed, damage * 0.2, isCrit, false);
                    GameWorld.gameWorld.addObject(arrow, (int) getExactX(), (int) getExactY());
                }
            }
            else {
                Projectile arrow = new Projectile(nx, ny, projectileSpeed, damage, isCrit, false);
                GameWorld.gameWorld.addObject(arrow, (int) getExactX(), (int) getExactY());
            }
        }
        else if (burstLvl == 1) {
            // plays sound
            dripSounds[dripIndex].play();
            dripIndex = (dripIndex + 1) % dripSounds.length;
            
            // creates blast
            Blast burst = new Blast(nx, ny, projectileSpeed, (damage * 0.4) + 1, false);
            GameWorld.gameWorld.addObject(burst, (int) getExactX(), (int) getExactY());
        }
        else {
            // plays sound
            dripSounds[dripIndex].play();
            dripIndex = (dripIndex + 1) % dripSounds.length;
            
            // creates blast
            Blast burst = new Blast(nx, ny, projectileSpeed, (damage * 0.7) + 1, false);
            GameWorld.gameWorld.addObject(burst, (int) getExactX(), (int) getExactY());
        }
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
                break;
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
                dashSpeed += value;
                break;
            case "projectile":
                projectileSpeed = Math.min(projectileSpeed + value/100.0, maxProjectileSpeed);
                if (projectileSpeed == maxProjectileSpeed) {
                    Upgrade.type.remove("projectileSpeed");
                    Upgrade.type.remove("projectile");
                }
                
                attackRange = Math.min(attackRange + value, maxAttackRange);
                if (attackRange == maxAttackRange) {
                    Upgrade.type.remove("attackRange");
                    Upgrade.type.remove("projectile");
                }
                
                attackSpeed = Math.max(attackSpeed - value*5.0, maxAttackSpeed);
                if (attackSpeed == maxAttackSpeed) {
                    Upgrade.type.remove("attackSpeed");
                    Upgrade.type.remove("projectile");
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
                if (frostbiteLvl >= 2) Upgrade.uniques.remove("Frostbite");
                break;
            case "Scorch":
                if (scorchLvl < 2) scorchLvl++;
                if (scorchLvl >= 2) Upgrade.uniques.remove("Scorch");
                break;
            case "Sharpshot":
                switch (sharpshotLvl) {
                    case 0:
                        sharpshotLvl++;
                        critRate = Math.min(critRate + 30, 100);
                        projectileSpeed = maxProjectileSpeed;
                        Upgrade.type.remove("projectileSpeed");
                        Upgrade.uniques.remove("Blood Pact");
                        break;
                    case 1:
                        sharpshotLvl++;
                        critDamage += 100.0;
                        attackRange = maxAttackRange;
                        Upgrade.type.remove("attackRange");
                        Upgrade.uniques.remove("Sharpshot");
                        break;
                }
                break;
            case "Vampire":
                if (vampireLvl == 0) {
                    vampireLvl++;
                    speed -= 0.15;
                    maxHp -= 15;
                } 
                else if (vampireLvl == 1) {
                    vampireLvl++;
                    maxHp += 65;
                    Upgrade.uniques.remove("Vampire");
                }
                break;
            case "Rogue":
                if (rogueLvl < 2) {
                    rogueLvl++;
                    if (rogueLvl == 1) {
                        critRate = Math.min(critRate + 30.0, 100.0); 
                        attackSpeed = Math.max(attackSpeed - 500.0, maxAttackSpeed);
                        attack += 10.0;
                        critDamage += 80.0; 
                        speed += 0.5; 
                        dashCooldown = Math.max(dashCooldown - 1000, minDashCooldown); 
                        attackRange = 110;
                        currentHp = 30;
                        maxHp = 30;
                        GameWorld.healthBar.setValue(Math.max(Hero.hero.currentHp, 0) + "/" + Hero.hero.maxHp + " hp");
                        Upgrade.type.remove("health");
                        Upgrade.type.remove("regenInterval");
                        Upgrade.type.remove("regenAmount");
                        Upgrade.type.remove("projectile");
                        Upgrade.type.remove("attackRange");
                        Upgrade.uniques.remove("Blood Pact");
                    }
                    else {
                        critRate = 100.0;
                        attack += 25.0;
                        speed *= 1.3;
                        dashCooldown = minDashCooldown;
                        maxAttackSpeed = 150;
                        attackSpeed = maxAttackSpeed;
                        echoWait = 100;
                        Upgrade.type.remove("attackSpeed");
                        Upgrade.type.remove("dashCooldown");
                        Upgrade.uniques.remove("Rogue");
                    }
                }
                break;
            case "Jester":
                if (jesterLvl < 2) jesterLvl++;
                if (jesterLvl >= 2) Upgrade.uniques.remove("Jester");
                break;
            case "Arcane Echo":
                if (arcaneEchoLvl < 2) {
                    arcaneEchoLvl++;
                    if (arcaneEchoLvl == 1) {
                        echoChance = 35;
                        echoMult = 0.6;
                    }
                    else {
                        echoChance = 60;
                        echoMult = 1.2;
                        Upgrade.uniques.remove("Arcane Echo");
                    }
                }
                break;
            case "Spectral Veil":
                spectralVeilLvl = 1;
                immuneChance = 40;
                immuneDuration = 2500;
                Upgrade.uniques.remove("Spectral Veil");
                break;
            case "Violent Vortex":
                if (vortexLvl < 2) {
                    vortexLvl++;
                    tornadoChance = 15 * vortexLvl;
                }
                if (vortexLvl >= 2) Upgrade.uniques.remove("Violent Vortex");
                break;
            case "Blood Pact":
                bloodPactLvl = 1;
                maxHp += 35;
                critDamage += 30;
                attackRange = 200;
                
                Upgrade.type.remove("projectile"); 
                Upgrade.type.remove("projectileSpeed");
                Upgrade.type.remove("attackRange");
                Upgrade.uniques.remove("Sharpshot");
                Upgrade.uniques.remove("Blood Pact");
                break;
            case "Shrapnel Shot": 
                if (shrapnelLvl < 2) {
                    shrapnelLvl++;
                    numShrapnel = 3 * shrapnelLvl;
                    shrapnelChance = 30 * shrapnelLvl;
                }
                if (shrapnelLvl >= 2) Upgrade.uniques.remove("Shrapnel Shot");
                break;
            case "Hydro Burst":
                if (burstLvl < 2) {
                    burstLvl++;
                    if (burstLvl == 2) Upgrade.uniques.remove("Hydro Burst");
                }
                break;
            case "Thunderstrike \nVolley":
                thunderLvl++;
                if (thunderLvl == 1) {
                    spread = 45;
                    projectileSpeed = Math.min(maxProjectileSpeed, projectileSpeed + 2.0);
                    Upgrade.uniques.remove("Shrapnel Shot");
                }
                if (thunderLvl == 2) {
                    Upgrade.uniques.remove("Thunderstrike \nVolley");
                    spread = 30;
                }
                break;
        }
    }
    
    /**
     * Method for animating the hero when the user has not attacked for a while
     */
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
    
    /**
     * Method for animating the death of the hero
     */
    public void animateDeath() {
        Enemy.removeAll();
        GameWorld.gameWorld.removeObject(GameWorld.gameWorld.healthBar);
        GameWorld.gameWorld.removeObject(redBar);
        GameWorld.gameWorld.removeObject(greenBar);
        
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
    
    /**
     * Method for animating the hero (not the bow) when attacking
     */
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
    
    /**
     * Method for animating the hero when it is hurt
     */
    public void animateHurt() {
        if (hurtAnimationTimer.millisElapsed() < 100) return;
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
    
    /**
     * Method for applying a burn to the player
     * 
     * @param damage: damage per burn tick
     * @param burnTicks: number of times the burn lasts
     */
    public void burn(double damage, int burnTicks) {
        burnDamage = damage;
        this.burnTicks = burnTicks;
        burnTimer.mark();
        this.currentHp = Math.max(0, (int) (currentHp - damage));
    }
}
