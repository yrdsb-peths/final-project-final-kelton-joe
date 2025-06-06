import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Wyrmroot Boss
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
    
    GreenfootSound chompSound = new GreenfootSound("loudChomp.mp3");
    
    // boss hp bar
    Label healthBar;
    Actor bossBarFrame = new Actor() {
        {
            GreenfootImage image = new GreenfootImage("bossbarframe.png");
            image.scale(500, 80);
            setImage(image);
        }
    };
    
    // size of the boss
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
     * Controls movement, vine spawning, and death
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
                else if (phase == 2 && vineRemaining > 0) setLocation(getExactX() + (nx * 0.7), getExactY() + (ny * 0.7));
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
                
                // removes health bar
                GameWorld.gameWorld.removeObject(healthBar);
                
                // boss is now dead
                isDead = true;
            }
        }
        
        // animate death when killed
        if (hitpoints <= 0) animateDeath();
    }
    
    /**
     * Frostbite method for the Wyrmroot class
     * slow lasts shorter compared to regular enemies
     */
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
    
    /**
     * Method to updating health bars
     * health bar does not change locations 
     * there is also an added label for health remaining
     */
    @Override
    public void changeBar() {
        if (redBar != null && greenBar != null) {
            redBar.setPos(400, 40);
            greenBar.setPos(400, 40, (double) hitpoints / (double) maxHitpoints);
            healthBar.setValue(Math.max(this.hitpoints, 0) + "/" + this.maxHitpoints);
        }
    }
    
    /**
     * Method to spawn in health bars when boss is added to the world
     */
    @Override
    protected void addedToWorld(World world) {
        // adds boss bar frame
        world.addObject(bossBarFrame, 400, 60);
        
        // adds hp bars
        redBar = new RedBar(0.0, 0.0, true);
        world.addObject(redBar, 400, 40);
        greenBar = new GreenBar(0.0, 0.0, true);
        world.addObject(greenBar, 400, 40);
        
        // adds health label
        healthBar = new Label(this.hitpoints + "/" + this.maxHitpoints + " hp", 30);
        world.addObject(healthBar, 400, 60);
    }
    
    /**
     * Boss is resistant to stun and immune to teleports
     */
    @Override
    public void jester() {
        if (Greenfoot.getRandomNumber(2) > 0 && Hero.hero.jesterLvl > 1) {
            stun(400);
            removeHp((int) (Hero.hero.attack * 1.5), false, Color.MAGENTA, 30);
        }
    }
    
    /**
     * Adjusted remove health function for damage calculations based on resistance multiplier
     */
    @Override
    public void removeHp(int damage, boolean isCrit, Color color, int size) {
        damage = (int) Math.max(damage * resMult, 0);
        hitpoints -= damage;
        
        // crit damage indicator
        if (isCrit) {
            color = Color.ORANGE;
            size = 35;
        }
        
        // adds damage indicator
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, size, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
    }
    
    /**
     * Method for animating the blue bite attack 
     */
    private void animateBlueBite() {
        // breaks if frame is not finished
        if (blueBiteTimer.millisElapsed() < 100) return;
        
        chompSound.play();
        
        // marks animation timer
        blueBiteTimer.mark();
        
        // continues animation if it is not finished
        if (blueBiteIndex < blueBiteLeft.length) {
            // boss is attacking while blue bite is animating
            isAttacking = true;
            
            // checks facing direction and sets images accordingly
            if (facingRight) setImage(blueBiteRight[blueBiteIndex]);
            else setImage(blueBiteLeft[blueBiteIndex]);
            
            // summons the swirl attack on specific index and if the boss has not already summoned it in the cycle
            if (blueBiteIndex == 5 && !hasAttacked) {
                // prevent creating more blue bites
                hasAttacked = true;
                
                // summons blue bite 
                BlueBite blueBite = new BlueBite((int) (this.attack * 0.4), isDodged);
                
                // adds blue bite to the world
                GameWorld.gameWorld.addObject(blueBite, (int) getExactX(), (int) getExactY());
            }
            
            // next animation
            blueBiteIndex++;
        }
        else {
            // resets variables for this bite instance
            hasAttacked = false;
            blueBiteIndex = 0;
            attackIndex = 1;
            isAttacking = false;
        }
    }
    
    /**
     * Method for animating the purple bite attack
     */
    private void animatePurpleBite() {
        // breaks if the current frame is not finished
        if (purpleBiteTimer.millisElapsed() < 150) return;
        
        chompSound.play();
        
        // marks animation timer
        purpleBiteTimer.mark();
        
        // continues if animation is not finished
        if (purpleBiteIndex < purpleBiteRight.length) {
            // is attacking while animating
            isAttacking = true;
            
            // checks facing direction
            if (facingRight) setImage(purpleBiteRight[purpleBiteIndex]);
            else setImage(purpleBiteLeft[purpleBiteIndex]);
            
            // attacks only on the 5th animation index
            if (purpleBiteIndex == 5)  {
                // calculates distance from hero
                dx = Hero.hero.getExactX() - getExactX();
                dy = Hero.hero.getExactY() - getExactY();
                
                // only deals damage to hero if it is close enough
                if (Math.sqrt(dx * dx + dy * dy) < 80) {
                    attack();
                    hasAttacked = true;
                }
                
                // spawns 1 vine per bite if not all of them (4) have been spawned
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
                    
                    // uses blue bite instead if no more vines to spawn
                    if (vineToSpawn <= 0) attackIndex = 0;
                }
            }
            
            // increases animation index
            purpleBiteIndex++;
        }
        
        else {
            // resets variables used in 1 loop of the animation
            purpleBiteIndex = 0;
            if (vineToSpawn > 0) attackIndex = 1;
            else attackIndex = 0;
            hasAttacked = false;
            isAttacking = false;
            spawnedVine = false;
            attackCooldown.mark();
        }
    }
    
    /**
     * Method for animating the death of the Wyrmroot
     */
    private void animateDeath() {
        // breaks if the animation frame is not finished
        if (deathTimer.millisElapsed() < 250) return;
        
        // marks animation timer
        deathTimer.mark();
        
        // continues animation if not finished
        if (deathIndex < death.length) {
            setImage(death[deathIndex]);
            deathIndex++;
        }
        // removes itself from the world once animation is done
        else {
            GameWorld.gameWorld.removeObject(this);
            enemies.remove(this);
        }
    }
    
    /**
     * Method for animating the movement of the Wyrmroot
     */
    private void animateRun() {
        // breaks if the current frame is not finished
        if (runTimer.millisElapsed() < 80) return;
        
        // marks animation timer
        runTimer.mark();
        
        // continues playing animation
        if (runIndex < runLeft.length) {
            if (facingRight) setImage(runRight[runIndex]);
            else setImage(runLeft[runIndex]);
            runIndex++;
        }
        
        // otherwise reset the animation
        else runIndex = 0;
    }
}
