import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Wyrmvine here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Wyrmvine extends Enemy
{
    private boolean isDead;
    private boolean facingRight;
    private boolean isAttacking;
    private int heading;
    private boolean isAnimating;
    private int attackSpeed;
    private boolean hasAttacked;
    private SimpleTimer idleTimer = new SimpleTimer();
    private SimpleTimer attackTimer = new SimpleTimer();
    
    private GreenfootImage[] attackLeft = new GreenfootImage[6];
    private GreenfootImage[] attackRight = new GreenfootImage[6];
    private GreenfootImage[] death = new GreenfootImage[6];
    private GreenfootImage[] idleLeft = new GreenfootImage[4];
    private GreenfootImage[] idleRight = new GreenfootImage[4];
    
    private int attackIndex;
    private int deathIndex;
    private int idleIndex;
    
    private SimpleTimer attackAnimationTimer = new SimpleTimer();
    private SimpleTimer deathAnimationTimer = new SimpleTimer();
    private SimpleTimer idleAnimationTimer = new SimpleTimer();
    
    private final int scale = 40;
    
    public Wyrmvine(int hp, int attack) {
        super(hp, -1.0, attack, 1500);
        
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
        
        deathIndex = 0;
        idleIndex = 0;
        attackIndex = 0;
        this.isAttacking = false;
        this.facingRight = false;
        this.isAnimating = false;
        this.attackSpeed = 1500;
        this.isDead = false;
        idleTimer.mark();
        attackTimer.mark();
        
        setImage(idleLeft[0]);
    }
    
    public void act()
    {
        if (!isDead) {
            target = "hero";
            
            if (hitpoints <= 0) {
                isDead = true;
                return;
            }
            
            if (!isAttacking && !isAnimating) animateIdle();
            
            // calculate distance from hero (used for rotation later)
            dx = Hero.hero.getExactX() - getExactX();
            dy = Hero.hero.getExactY() - getExactY();
                
            // facing right if the hero is more to the right
            facingRight = dx >= 0;
            
            if ((Math.sqrt(dx * dx + dy * dy) < 75 || isAttacking) && attackTimer.millisElapsed() > attackSpeed) {
                animateAttack();
                if (attackIndex == 4 && Math.sqrt(dx * dx + dy * dy) < 50 && !hasAttacked) {
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
    
    private void animateDeath() {
        if (deathAnimationTimer.millisElapsed() < 150) return;
        deathAnimationTimer.mark();
        
        if (deathIndex < death.length) {
            isAnimating = true;
            
            setImage(death[deathIndex]);
            deathIndex++;
        }
        else  {
            Wyrmroot.vineRemaining--;
            Wyrmroot.vineDied = true;
            Wyrmroot.resMult += 0.15;
            GameWorld.gameWorld.removeObject(this);
            enemies.remove(this);
        }
    }
    
    private void animateAttack() {
        if (attackAnimationTimer.millisElapsed() < 200) return;
        attackAnimationTimer.mark();
        
        if (attackIndex < attackLeft.length) {
            isAnimating = true;
            isAttacking = true;
            
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
        else {
            isAnimating = false;
            isAttacking = false;
            hasAttacked = false;
            attackIndex = 0;
            attackTimer.mark();
            idleTimer.mark();
        }
    }
    
    private void animateIdle() {
        if (idleAnimationTimer.millisElapsed() < 200) return;
        idleAnimationTimer.mark();
        
        if (facingRight) setImage(idleRight[idleIndex]);
        else setImage(idleLeft[idleIndex]);
        idleIndex = (idleIndex + 1) % idleLeft.length;
    }    
    
    @Override
    public void removeHp(int damage, boolean isCrit, Color color, int size) {
        hitpoints -= damage;
        
        if (isCrit) {
            color = Color.ORANGE;
            size = 35;
        }
        
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, size, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
    }
}
