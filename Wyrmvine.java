import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Wyrmvine here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Wyrmvine extends Enemy
{
    // state of the enemy
    private boolean isDead;
    private boolean facingRight;
    private boolean isAttacking;
    private int heading;
    private boolean isAnimating;
    private int attackSpeed;
    private boolean hasAttacked;
    private SimpleTimer idleTimer = new SimpleTimer();
    private SimpleTimer attackTimer = new SimpleTimer();
    
    // animation image storage
    private GreenfootImage[] attackLeft = new GreenfootImage[6];
    private GreenfootImage[] attackRight = new GreenfootImage[6];
    private GreenfootImage[] death = new GreenfootImage[6];
    private GreenfootImage[] idleLeft = new GreenfootImage[4];
    private GreenfootImage[] idleRight = new GreenfootImage[4];
    
    // animation indexes
    private int attackIndex;
    private int deathIndex;
    private int idleIndex;
    
    // animation timers
    private SimpleTimer attackAnimationTimer = new SimpleTimer();
    private SimpleTimer deathAnimationTimer = new SimpleTimer();
    private SimpleTimer idleAnimationTimer = new SimpleTimer();
    
    // size of the boss
    private final int scale = 40;
    
    // attack sound
    GreenfootSound chompSound = new GreenfootSound("chomp.mp3");
    
    /**
     * Constructor for the Wyrmvine class
     * 
     * @param hp: how much health to have for the enemy
     * @param attack: how much damage it deals
     */
    public Wyrmvine(int hp, int attack) {
        // second parameter is -1.0 to make it not move
        super(hp, -1.0, attack, 1000);
        
        // sets images for animations
        for (int i = 0; i < attackLeft.length; i++) {
            attackLeft[i] = new GreenfootImage("Wyrm/summon/wyrmAttack/wyrmAttack" + i + ".png");
            attackRight[i] = new GreenfootImage("Wyrm/summon/wyrmAttack/wyrmAttack" + i + ".png");
            attackRight[i].mirrorHorizontally();
            attackLeft[i].scale(scale, scale);
            attackRight[i].scale(scale, scale);
        }
        for (int i = 0; i < death.length; i++) {
            death[i] = new GreenfootImage("Wyrm/summon/wyrmDeath/wyrmDeath" + i + ".png");
            death[i].scale(scale, scale);
        }
        for (int i = 0; i < idleLeft.length; i++) {
            idleLeft[i] = new GreenfootImage("Wyrm/summon/wyrmIdle/wyrmIdle" + i + ".png");
            idleRight[i] = new GreenfootImage("Wyrm/summon/wyrmIdle/wyrmIdle" + i + ".png");
            idleRight[i].mirrorHorizontally();
            idleLeft[i].scale(scale, scale);
            idleRight[i].scale(scale, scale);
        }
        
        // sets instance variables
        deathIndex = 0;
        idleIndex = 0;
        attackIndex = 0;
        this.isAttacking = false;
        this.facingRight = false;
        this.isAnimating = false;
        this.attackSpeed = 1000;
        this.isDead = false;
        idleTimer.mark();
        attackTimer.mark();
        
        //sets default image
        setImage(idleLeft[0]);
    }
    
    public void act()
    {
        if (!isDead) {
            // cannot target tornado
            target = "hero";
            
            // tells itself that it died if health
            if (hitpoints <= 0) {
                isDead = true;
                return;
            }
            
            // idle animation
            if (!isAttacking && !isAnimating) animateIdle();
            
            // calculate distance from hero (used for rotation later)
            dx = Hero.hero.getExactX() - getExactX();
            dy = Hero.hero.getExactY() - getExactY();
                
            // facing right if the hero is more to the right
            facingRight = dx >= 0;
            
            // initiates attack
            if ((Math.sqrt(dx * dx + dy * dy) < 75 || isAttacking) && attackTimer.millisElapsed() > attackSpeed) {
                // animate attack
                animateAttack();
                
                // deals damage to player
                if (attackIndex == 4 && Math.sqrt(dx * dx + dy * dy) < 50 && !hasAttacked) {
                    // plays chomp sound
                    chompSound.play();
                    
                    // prevent dealing damage multiple times per attack cycle
                    hasAttacked = true;
                    attack();
                }
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
                    
                    if (hitpoints <= 0) {
                        isDead = true;
                        return;
                    }
                }
            }
            
            // update health bar
            changeBar();
            
            // remove status effects when timer ends
            if (frostbiteTimer.millisElapsed() >= slowDuration) isSlowed = false;
            if (frostbiteFreezeTimer.millisElapsed() >= 1500) isFrozen = false;
            if (stunTimer.millisElapsed() >= 800) isStunned = false;
            if (weakenTimer.millisElapsed() > weakenDuration) isWeakened = false;
            
            // tells itself that it died
            if (hitpoints <= 0) {
                setRotation(0);
                // remove health bars when dead
                GameWorld.gameWorld.removeObject(redBar);
                GameWorld.gameWorld.removeObject(greenBar);
                redBar = null;
                greenBar = null;
                
                this.isDead = true;
            }
        }
        if (hitpoints <= 0 || isDead) {
            // remove health bars when dead
            GameWorld.gameWorld.removeObject(redBar);
            GameWorld.gameWorld.removeObject(greenBar);
            redBar = null;
            greenBar = null;
                
            this.isDead = true;
            animateDeath();
        }
    }
    
    /**
     * Method for animating the death of the vine
     */
    private void animateDeath() {
        // breaks if the current animation is not finished
        if (deathAnimationTimer.millisElapsed() < 150) return;
        
        // marks animation timer
        deathAnimationTimer.mark();
        
        // continues playing animation if not finished
        if (deathIndex < death.length) {
            isAnimating = true;
            
            setImage(death[deathIndex]);
            deathIndex++;
        }
        // weakens the boss after it dies and removes itself from the world
        else  {
            Wyrmroot.vineRemaining--;
            Wyrmroot.vineDied = true;
            Wyrmroot.resMult += 0.15;
            GameWorld.gameWorld.removeObject(this);
            enemies.remove(this);
        }
    }
    
    /**
     * Method for animating the attack
     */
    private void animateAttack() {
        // breaks if current animation frame is not done playing
        if (attackAnimationTimer.millisElapsed() < 200) return;
        
        // marks animation timer
        attackAnimationTimer.mark();
        
        // continues playing animation
        if (attackIndex < attackLeft.length) {
            isAnimating = true;
            isAttacking = true;
            
            // checks facing direction
            if (facingRight) {
                facingRight = true;
                setImage(attackRight[attackIndex]);
            }
            else {
                facingRight = false;
                setImage(attackLeft[attackIndex]);
            }
            attackIndex++;
        }
        // resets variables used by 1 instance of bite
        else {
            isAnimating = false;
            isAttacking = false;
            hasAttacked = false;
            attackIndex = 0;
            attackTimer.mark();
            idleTimer.mark();
        }
    }
    
    /**
     * Method for animating the idle of the vine
     */
    private void animateIdle() {
        // breaks if the current animation is not finished
        if (idleAnimationTimer.millisElapsed() < 200) return;
        
        // marks animation timer
        idleAnimationTimer.mark();
        
        // checks facing direction
        if (facingRight) setImage(idleRight[idleIndex]);
        else setImage(idleLeft[idleIndex]);
        
        // increases animation index
        idleIndex = (idleIndex + 1) % idleLeft.length;
    }    
    
    /**
     * remove health method
     * 
     * @param damage: damage taken
     * @param isCrit: whether the attack is a crit
     * @param color: color for damage indicator
     * @param size: size of the damage indicator
     */
    @Override
    public void removeHp(int damage, boolean isCrit, Color color, int size) {
        // deals damage to itself
        hitpoints -= damage;
        
        // changes size and color if crit
        if (isCrit) {
            color = Color.ORANGE;
            size = 35;
        }
        
        // adds damage indicator
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, size, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
    }
}
