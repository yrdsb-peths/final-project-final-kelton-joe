import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Enemy class
 * 
 * @author Kelton and Joe
 * @version May 2025
 * 
 * Uses smooth mover for more precise movements
 */
public class Enemy extends SmoothMover
{
    // speed
    double speed = 1.0;
    
    // health
    public int maxHitpoints;
    public int hitpoints;
    
    // attack
    int attack = 1;
    
    // attack speed
    private int attackSpeed = 1200;
    private int maxAttackSpeed = 300;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // list of enemies
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    
    // health bars
    public RedBar redBar;
    public GreenBar greenBar;
    
    // slow and freeze
    public boolean isSlowed;
    public int slowDuration;
    boolean isFrozen;
    SimpleTimer frostbiteFreezeTimer = new SimpleTimer();
    public SimpleTimer frostbiteTimer = new SimpleTimer();
    GreenfootSound[] freezeSounds = {
        new GreenfootSound("freeze/freeze1.mp3"),
        new GreenfootSound("freeze/freeze2.mp3"),
        new GreenfootSound("freeze/freeze3.mp3")
    };
    int freezeSoundIndex = 0;
    
    // burn
    int burnTicks;
    double burnDamage;
    SimpleTimer scorchTimer = new SimpleTimer();
    GreenfootSound[] burnSounds = {
        new GreenfootSound("burn/burn1.mp3"),
        new GreenfootSound("burn/burn2.mp3"),
        new GreenfootSound("burn/burn3.mp3")
    };
    int burnSoundIndex = 0;
    
    // dodge and stun
    boolean isDodged;
    boolean isStunned;
    int stunDuration;
    SimpleTimer stunTimer = new SimpleTimer();
    
    // weaken
    boolean isWeakened;
    int weakenAmount;
    int weakenDuration;
    SimpleTimer weakenTimer = new SimpleTimer();
    
    // targeting
    public String target = "hero";
    public double dx, dy;
    
    // spectral veil sounds
    GreenfootSound veilSound = new GreenfootSound("veil.mp3");
    
    public boolean isPulled;
    
    /**
     * Constructor for enemy class
     * 
     * @param hitpoints: how much hp to give the enemy
     * @param speed: how fast the enemy to be
     * @param attack: how much damage it deals
     * @param attackSpeed: bonus attack speed for the enemy
     */
    public Enemy(int hitpoints, double speed, int attack, int attackSpeed) {
        // sets image
        setImage("images/balloon1.png");
        
        // lowers burn volume
        for (GreenfootSound s : burnSounds) {
            s.setVolume(50);
        }
        
        // stat increases
        maxHitpoints = hitpoints * 10;
        this.hitpoints = hitpoints * 10;
        this.speed += speed; 
        this.attack += (attack + 0.5) * 8;
        this.attackSpeed -= Math.min(attackSpeed, maxAttackSpeed);
        
        // add to list of all current enemies
        enemies.add(this);
        
        // enemy image
        GreenfootImage enemy = getImage();
        enemy.scale(25, 25);
        
        // marks attack cooldown timer
        attackCooldown.mark();
    }
    
    /**
     * Method for adding an hp bar to an enemy
     * 
     * @param world: world to add the enemy to
     */
    protected void addedToWorld(World world) {
        // scale of health bar
        double scale = 0.05;
        
        // red bar
        redBar = new RedBar(scale, scale, false);
        world.addObject(redBar, getX(), getY());
        
        // green bar
        greenBar = new GreenBar(scale, scale, false);
        world.addObject(greenBar, getX(), getY());
    }
    
    /**
     * Act method for enemies
     */
    public void act()
    {   
        // targeting
        if (!isPulled) target = "hero";
        else target = "vortex";
        
        // if hp is less than 0 enemy is dead
        if (hitpoints <= 0) return;
        
        // hero targeting
        if (!target.equals("vortex")) {
            dx = Hero.hero.getExactX() - getExactX();
            dy = Hero.hero.getExactY() - getExactY();
        }
        
        // distance from targets
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        double normalizedDx = dx / magnitude;
        double normalizedDy = dy / magnitude;
        
        // burn damage
        if (scorchTimer.millisElapsed() >= 1000) {
            if (burnTicks > 0) {
                // burn sound
                burnSounds[burnSoundIndex].play();
                burnSoundIndex = (burnSoundIndex + 1) % burnSounds.length;
                
                // burn damage
                removeHp((int)burnDamage, false, Color.RED, 25);
                burnTicks--;
                scorchTimer.mark();
                
                // burn death
                if (hitpoints <= 0) return;
            }
        }
        
        // status effects
        if (weakenTimer.millisElapsed() > weakenDuration) isWeakened = false;
        if (frostbiteTimer.millisElapsed() >= slowDuration) isSlowed = false;
        if (frostbiteFreezeTimer.millisElapsed() >= 1500) isFrozen = false;
        if (stunTimer.millisElapsed() >= stunDuration) isStunned = false;
        
        // movement based status effects
        if (isFrozen || isStunned) setLocation(getExactX(), getExactY());
        else if (isSlowed) setLocation(getExactX() + (normalizedDx * speed/2), getExactY() + (normalizedDy * speed/2));
        else setLocation(getExactX() + (normalizedDx * speed), getExactY() + (normalizedDy * speed));
        
        // deals damage to hero
        if (this.isTouching(Hero.class) && !isStunned && !Hero.hero.isImmune) {
            if (attackCooldown.millisElapsed() >= attackSpeed) {
                attack();
                attackCooldown.mark();
            }
        }
        
        // updates hp bar
        changeBar();
    }
    
    /**
     * Method for removing health from the enemy
     * 
     * @param damage: damage it takes
     * @param isCrit: whether the attack was crit (used for damage indicator)
     * @param color: color of damage indicator
     * @param size: size of damage indicator
     */
    public void removeHp(int damage, boolean isCrit, Color color, int size) {
        // remove hp
        hitpoints -= damage;
        
        // crit indicator
        if (isCrit) {
            color = Color.ORANGE;
            size = 35;
        }
        
        // creates indicator
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, size, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
        
        // removes itself and hp bar if it died
        if (hitpoints <= 0) {
            // removers health bars
            GameWorld.gameWorld.removeObject(redBar);
            GameWorld.gameWorld.removeObject(greenBar);
            redBar = null;
            greenBar = null;
            
            // removes itself 
            GameWorld.gameWorld.removeObject(this);
            enemies.remove(this);
        }
    }
    
    /**
     * Attack method for hero
     */
    public void attack() {
        // rogue dodge
        if (Hero.hero.rogueLvl == 2) {
            if (Greenfoot.getRandomNumber(5) == 1) isDodged = true;
            else isDodged = false;
        }
        
        // deal damage if hero is not immune
        if (!isDodged || !Hero.hero.isImmune) Hero.hero.currentHp -= this.attack * (1.0 - weakenAmount/100.0);
        GameWorld.healthBar.setValue(Math.max(Hero.hero.currentHp, 0) + "/" + Hero.hero.maxHp + " hp");
        
        // chance to activate spectral veil on hitting hero
        if (Greenfoot.getRandomNumber(100) <= Hero.hero.immuneChance && Hero.hero.spectralVeilLvl > 0 && !Hero.hero.isImmune) {
            // spectral veil sound
            veilSound.play();
            
            Hero.hero.isImmune = true;
            Hero.hero.immunityTimer.mark();
        }
        
        // hero hurt and death animations
        if (Hero.hero.currentHp <= 0) Hero.hero.isDead = true;
        else if (!isDodged && !Hero.hero.isImmune) Hero.hero.isHurt = true;
    }
    
    /**
     * frostbite (slow/freeze status effect)
     */
    public void frostbite() {
        // slow for 4s when hero's frostbite level is 1
        if (Hero.hero.frostbiteLvl > 0) {
            isSlowed = true;
            slowDuration = 4000;
            frostbiteTimer.mark();
        }   
        // freeze if hero's frostbite level is 2
        if (Hero.hero.frostbiteLvl > 1) {
            isFrozen = true;
            frostbiteFreezeTimer.mark();
            
            // freeze sound
            freezeSounds[freezeSoundIndex].play();
            freezeSoundIndex = (freezeSoundIndex + 1) % freezeSounds.length;
        }
    }

    /**
     * Initializes burn
     * 
     * @param damage: damage per burn
     */
    public void scorch(double damage) {
        if (Hero.hero.scorchLvl > 0) {
            // damage and duration
            this.burnDamage = (int) ((damage * 0.5 * Hero.hero.scorchLvl) + 0.5);
            burnTicks = 3;
            scorchTimer.mark();
            
            // burn sounds
            burnSounds[burnSoundIndex].play();
            burnSoundIndex = (burnSoundIndex + 1) % burnSounds.length;
            
            // removes hp from burn
            removeHp((int)this.burnDamage, false, Color.RED, 25);
        }
    }
    
    /**
     * Vampire method 
     * heals the hero when this is called
     */
    public void vampire() {
        if (Hero.hero.vampireLvl == 1) {
            // increases current hp by 4% of max hp
            Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + (0.04 * Hero.hero.maxHp)), Hero.hero.maxHp);
            // updates health bar
            GameWorld.healthBar.setValue(Math.max(Hero.hero.currentHp, 0) + "/" + Hero.hero.maxHp + " hp");
        }
        else if (Hero.hero.vampireLvl >= 2) {
            // chance to grant additional hp on kill (50%)
            if (this.hitpoints <= 0) {
                if (Greenfoot.getRandomNumber(2) == 1) {
                    // heals for 10% of max hp 
                    Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + (0.1 * Hero.hero.maxHp)), Hero.hero.maxHp);
                    
                    // increases max hp by 5
                    Hero.hero.maxHp += Greenfoot.getRandomNumber(7) + 2;
                }
            }
            // otherwise increase current hp by 15%
            else Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + (0.15 * Hero.hero.maxHp)), Hero.hero.maxHp);
            
            // updates health bar
            GameWorld.healthBar.setValue(Math.max(Hero.hero.currentHp, 0) + "/" + Hero.hero.maxHp + " hp");
        }
    }
    
    /**
     * Jester method 
     */
    public void jester() {
        if (Hero.hero.jesterLvl > 0) {
            if (Greenfoot.getRandomNumber(2) > 0) {
                // sets random location
                setLocation(Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
                
                if (Hero.hero.jesterLvl > 1) {
                    // stuns
                    stun(1200);
                    
                    // deals jester damage
                    removeHp((int) (Hero.hero.attack * 1.5), false, Color.MAGENTA, 30);
                }
            }
        }
    }
    
    /**
     * Method for creating a tornado
     * 
     * @param damage: damage the tornado deals
     */
    public void tornado(double damage) {
        // generates a tornado if lucky
        if (Hero.hero.vortexLvl > 0  && Greenfoot.getRandomNumber(100) <= Hero.hero.tornadoChance) {
            // creates a new vortex
            Tornado vortex = new Tornado((int) damage);
            
            // changes target to the vortex
            this.target = "vortex";
            this.isPulled = true;
            
            // adds the vortex to the world
            GameWorld.gameWorld.addObject(vortex, (int) this.getExactX(), (int) this.getExactY());
        }
    }
    
    /**
     * Weaken method
     * 
     * @param weakenAmount: amount to weaken enemy by
     * @param weakenDuration: weaken duration
     */
    public void weaken(int weakenAmount, int weakenDuration) {
        // makes this enemy weakened
        this.isWeakened = true;
        this.weakenAmount = weakenAmount;
        this.weakenDuration = weakenDuration;
        weakenTimer.mark();
    }
    
    /**
     * Stun method
     * 
     * @param duration: how long to stun enemies for
     */
    public void stun(int duration) {
        // makes this enemy weakened
        this.isStunned = true;
        this.stunDuration = duration;
        stunTimer.mark();
    }
    
    /**
     * Method for removing all enemies
     */
    public static void removeAll() {
        // continously removes enemies until the entire of list in enemies is blank
        if (enemies.size() > 0) {
            GameWorld.gameWorld.removeObject(enemies.get(0).redBar);
            GameWorld.gameWorld.removeObject(enemies.get(0).greenBar);
            enemies.get(0).redBar = null;
            enemies.get(0).greenBar = null;
            GameWorld.gameWorld.removeObject(enemies.get(0));
            enemies.remove(enemies.get(0));
        }
    }
    
    /**
     * Method for updating the health bar
     */
    public void changeBar() {
        // changes green bar based on current hp
        if (redBar != null && greenBar != null) {
            redBar.setPos(getX(), getY());
            greenBar.setPos(getX(), getY(), (double) hitpoints / (double) maxHitpoints);
        }
    }
}
