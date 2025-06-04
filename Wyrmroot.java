import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Boss here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Wyrmroot extends Enemy
{
    // boss attack speed
    private double attackSpeed = 1300;
    private boolean facingRight = false;
    
    // animation
    GreenfootImage[] blueBiteLeft = new GreenfootImage[6];
    GreenfootImage[] purpleBiteLeft = new GreenfootImage[6];
    GreenfootImage[] runLeft = new GreenfootImage[6];
    GreenfootImage[] blueBiteRight = new GreenfootImage[6];
    GreenfootImage[] purpleBiteRight = new GreenfootImage[6];
    GreenfootImage[] runRight = new GreenfootImage[6];    
    GreenfootImage[] death = new GreenfootImage[6];
    
    // animation timers
    SimpleTimer blueBiteTimer = new SimpleTimer();
    SimpleTimer deathTimer = new SimpleTimer();
    SimpleTimer purpleBiteTimer = new SimpleTimer();
    SimpleTimer runTimer = new SimpleTimer();
    
    // animation indexes
    private int blueBiteIndex = 0;
    private int deathIndex = 0;
    private int purpleBiteIndex = 0;
    private int runIndex = 0;
    
    // boss conditions
    private boolean isAttacking;
    private boolean hasAttacked;
    public static boolean isDead = false;
    
    // location
    private double magnitude;
    private double nx, ny;
    private int heading;
    
    // which attack to use
    private double attackIndex = 0;
    
    // which phase
    private int phase;
    
    // summons and phase 2 related
    private int vineToSpawn;
    private boolean spawnedVine;
    public static int vineRemaining;
    public static boolean vineDied;
    public static double resMult;
    private boolean isDisabled;
    private SimpleTimer disableTimer = new SimpleTimer();
    
    // vine spawn sound
    GreenfootSound vineSound = new GreenfootSound("vine.mp3");
    
    // boss hp bar
    Label healthBar;
    Actor bossBarFrame = new Actor() {
        {
            GreenfootImage image = new GreenfootImage("bossbarframe.png");
            image.scale(500, 80);
            setImage(image);
        }
    };
    
    private final int scale = 90;
    
    /**
     * Wyrmroot constructor
     * 
     * @param hitpoints: hp of the boss
     * @param attack: attack of the boss
     */
    public Wyrmroot(int hitpoints, int attack) {
        // creates enemy
        super(hitpoints, 0.55, attack, 300);
        
        // sets images
        for (int i = 0; i < blueBiteRight.length; i++) {
            blueBiteLeft[i] = new GreenfootImage("Wyrm/blueBite/blueBite" + i + ".png");
            blueBiteRight[i] = new GreenfootImage("Wyrm/blueBite/blueBite" + i + ".png");
            blueBiteRight[i].mirrorHorizontally();
            
            blueBiteLeft[i].scale(scale, scale);
            blueBiteRight[i].scale(scale, scale);
        }
        for (int i = 0; i < death.length; i++) {
            death[i] = new GreenfootImage("Wyrm/death/death" + i + ".png");
            death[i].scale(scale, scale);
        }
        for (int i = 0; i < purpleBiteLeft.length; i++) {
            purpleBiteLeft[i] = new GreenfootImage("Wyrm/purpleBite/purpleBite" + i + ".png");
            purpleBiteRight[i] = new GreenfootImage("Wyrm/purpleBite/purpleBite" + i + ".png");
            purpleBiteRight[i].mirrorHorizontally();
            
            purpleBiteLeft[i].scale(scale, scale);
            purpleBiteRight[i].scale(scale, scale);
        }
        for (int i = 0; i < runLeft.length; i++) {
            runLeft[i] = new GreenfootImage("Wyrm/run/run" + i + ".png");
            runRight[i] = new GreenfootImage("Wyrm/run/run" + i + ".png");
            runRight[i].mirrorHorizontally();
            
            runLeft[i].scale(scale, scale);
            runRight[i].scale(scale, scale);
        }
        
        // marks attack cooldown
        attackCooldown.mark();
        
        // sets instance variables
        isDead = false;
        phase = 1;
        isDisabled = false;
        resMult = 1.0;
        vineRemaining = 0;
        vineToSpawn = 0;
    }
    
    /**
     * Act method for the boss
     */
    public void act()
    {
        if (!isDead) {
            // triggers phase 2
            if (hitpoints <= (maxHitpoints * 0.5) && phase == 1) {
                phase++;
                vineToSpawn = 4;
                resMult = 0.05;
                attackIndex = 1;
                hitpoints = maxHitpoints;
            }
            
            // stun and lose hp every time a vine dies
            if (vineDied) {
                hitpoints = Math.max(1, (int) (hitpoints - (maxHitpoints * 0.15)));
                isDisabled = true;
                disableTimer.mark();
                vineDied = false;
            }
            
            // less resistance when all vines are dead
            if (vineRemaining <= 0 && vineToSpawn <= 0 && phase == 2) {
                resMult = 2.0;
                attackIndex = 0;
                isAttacking = false;
                phase++;
            }
            
            // disable for 2s when vine dies 
            if (disableTimer.millisElapsed() >= 2000) isDisabled = false;
            
            // tornado targeting
            if (!isSlowed) target = "hero";
            else if (Hero.hero.vortexLvl > 0) target = "vortex";
            
            // hero targeting
            if (!target.equals("vortex")) {
                // calculate distance from hero
                dx = Hero.hero.getExactX() - getExactX();
                dy = Hero.hero.getExactY() - getExactY();
                
                // facing right if the hero is more to the right
                facingRight = dx >= 0;
                
                // animate run
                if (!isAttacking && !isDisabled) animateRun();
            }
            
            // deal scorch damage
            if (scorchTimer.millisElapsed() >= 1000) {
                if (burnTicks > 0) {
                    // burn sound effects
                    burnSounds[burnSoundIndex].play();
                    burnSoundIndex = (burnSoundIndex + 1) % burnSounds.length;
                    
                    // deal burn damage and reduce timer
                    removeHp((int)burnDamage, false, Color.RED, 25);
                    burnTicks--;
                    scorchTimer.mark();
                    
                    if (hitpoints <= 0) return;
                }
            }
            
            // remove status effects when timer ends
            if (frostbiteTimer.millisElapsed() >= slowDuration) isSlowed = false;
            if (frostbiteFreezeTimer.millisElapsed() >= 750) isFrozen = false;
            if (stunTimer.millisElapsed() >= stunDuration) isStunned = false;
            if (weakenTimer.millisElapsed() > weakenDuration) isWeakened = false;
            
            // slow, frozen, and stun movements
            if (!isAttacking && !isDisabled) {
                // movement calculation
                magnitude = Math.sqrt(dx * dx + dy * dy);
                nx = dx / magnitude;
                ny = dy / magnitude;
                
                // rotation
                heading = (int) Math.toDegrees(Math.atan2(dy, dx));
                
                // fix when mirrored
                if (dx < 0) heading -= 180;
                
                // set rotation
                this.setRotation(heading);
                
                if (isFrozen || isStunned) setLocation(getExactX(), getExactY());
                else if (isSlowed) setLocation(getExactX() + (nx * speed/3), getExactY() + (ny * speed/3));
                else setLocation(getExactX() + (nx * speed), getExactY() + (ny * speed));
            }
            
            // bite attacks
            if (attackCooldown.millisElapsed() >= attackSpeed && (magnitude < 75 || isAttacking)) { 
                if (attackIndex == 0 && vineToSpawn == 0) animateBlueBite();
                else animatePurpleBite();
            }
            
            // update health bar
            changeBar();
            
            // tells itself that it died
            if (hitpoints <= 0) {
                // reset rotation on death
                setRotation(0);
                
                // remove health bars when dead
                GameWorld.gameWorld.removeObject(redBar);
                GameWorld.gameWorld.removeObject(greenBar);
                redBar = null;
                greenBar = null;
                
                // removes boss bar text
                GameWorld.gameWorld.removeObject(bossBarFrame);
                GameWorld.gameWorld.removeObject(GameWorld.gameWorld.bossBarText);
                
                GameWorld.gameWorld.removeObject(healthBar);
                
                // boss is now dead
                isDead = true;
            }
        }
        
        // animate death when killed
        if (hitpoints <= 0) {
            // animates death
            animateDeath();
        }
    }
    
    @Override
    public void frostbite() {
        if (Hero.hero.frostbiteLvl > 0) {
            isSlowed = true;
            slowDuration = 800;
            frostbiteTimer.mark();
        }   
        if (Hero.hero.frostbiteLvl > 1) {
            isFrozen = true;
            frostbiteFreezeTimer.mark();
            
            freezeSounds[freezeSoundIndex].play();
            freezeSoundIndex = (freezeSoundIndex + 1) % freezeSounds.length;
        }
    }
    
    @Override
    public void changeBar() {
        if (redBar != null && greenBar != null) {
            redBar.setPos(400, 40);
            greenBar.setPos(400, 40, (double) hitpoints / (double) maxHitpoints);
            healthBar.setValue(Math.max(this.hitpoints, 0) + "/" + this.maxHitpoints);
        }
    }
    
    @Override
    protected void addedToWorld(World world) {
        world.addObject(bossBarFrame, 400, 60);
        
        redBar = new RedBar(0.0, 0.0, true);
        world.addObject(redBar, 400, 40);
        
        greenBar = new GreenBar(0.0, 0.0, true);
        world.addObject(greenBar, 400, 40);
        
        healthBar = new Label(this.hitpoints + "/" + this.maxHitpoints + " hp", 30);
        world.addObject(healthBar, 400, 60);
    }
    
    @Override
    public void jester() {
        if (Greenfoot.getRandomNumber(2) > 0 && Hero.hero.jesterLvl > 1) {
            stun(400);
            removeHp((int) (Hero.hero.attack * 1.5), false, Color.MAGENTA, 30);
        }
    }
    
    @Override
    public void removeHp(int damage, boolean isCrit, Color color, int size) {
        damage = (int) Math.max(damage * resMult, 0);
        hitpoints -= damage;
        
        if (isCrit) {
            color = Color.ORANGE;
            size = 35;
        }
        
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, size, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
    }
    
    private void animateBlueBite() {
        if (blueBiteTimer.millisElapsed() < 100) return;
        blueBiteTimer.mark();
        
        if (blueBiteIndex < blueBiteLeft.length) {
            isAttacking = true;
            
            if (facingRight) setImage(blueBiteRight[blueBiteIndex]);
            else setImage(blueBiteLeft[blueBiteIndex]);
            
            if (blueBiteIndex == 5 && !hasAttacked) {
                hasAttacked = true;
                BlueBite blueBite = new BlueBite((int) (this.attack * 0.4), isDodged);
                GameWorld.gameWorld.addObject(blueBite, (int) getExactX(), (int) getExactY());
            }
            
            blueBiteIndex++;
        }
        else {
            hasAttacked = false;
            blueBiteIndex = 0;
            attackIndex = 1;
            isAttacking = false;
        }
    }
    
    private void animatePurpleBite() {
        if (purpleBiteTimer.millisElapsed() < 150) return;
        purpleBiteTimer.mark();
        
        if (purpleBiteIndex < purpleBiteRight.length) {
            isAttacking = true;
            
            if (facingRight) setImage(purpleBiteRight[purpleBiteIndex]);
            else setImage(purpleBiteLeft[purpleBiteIndex]);
            
            if (purpleBiteIndex == 5)  {
                dx = Hero.hero.getExactX() - getExactX();
                dy = Hero.hero.getExactY() - getExactY();
                            
                if (Math.sqrt(dx * dx + dy * dy) < 70) {
                    attack();
                    hasAttacked = true;
                }
                            
                if (vineToSpawn > 0 && !spawnedVine) {
                    // creates a new vine
                    Wyrmvine vine = new Wyrmvine(GameWorld.gameWorld.waveMultiplier * 20, GameWorld.gameWorld.waveMultiplier * 3);
                    GameWorld.gameWorld.addObject(vine, (int) getExactX(), (int) getExactY());
                    
                    // play vine sound
                    vineSound.play();
                    
                    // changes variables
                    vineToSpawn--;
                    vineRemaining++;
                    isAttacking = false;
                    spawnedVine = true;
                    if (vineToSpawn <= 0) attackIndex = 0;
                }
            }
            purpleBiteIndex++;
        }
        
        else {
            purpleBiteIndex = 0;
                
            if (vineToSpawn > 0) attackIndex = 1;
            else attackIndex = 0;
                
            hasAttacked = false;
            isAttacking = false;
            spawnedVine = false;
                
            attackCooldown.mark();
        }
    }
    
    private void animateDeath() {
        if (deathTimer.millisElapsed() < 250) return;
        deathTimer.mark();
        
        if (deathIndex < death.length) {
            setImage(death[deathIndex]);
            deathIndex++;
        }
        else {
            GameWorld.gameWorld.removeObject(this);
            GameWorld.gameWorld.removeObject(GameWorld.gameWorld.boss);
            enemies.remove(this);
        }
    }
    
    private void animateRun() {
        if (runTimer.millisElapsed() < 80) return;
        runTimer.mark();
        
        if (runIndex < runLeft.length) {
            if (facingRight) setImage(runRight[runIndex]);
            else setImage(runLeft[runIndex]);
            runIndex++;
        }
        else runIndex = 0;
    }
}
