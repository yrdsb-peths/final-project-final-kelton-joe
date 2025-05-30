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
    private double speed = 1.0;
    
    // health
    public int maxHitpoints;
    public int hitpoints;
    
    // attack
    private int attack = 1;
    
    // attack speed
    private int attackSpeed = 1200;
    private int maxAttackSpeed = 300;
    SimpleTimer attackCooldown = new SimpleTimer();
    
    // list of enemies
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    
    // health bars
    private RedBar redBar;
    private GreenBar greenBar;
    
    public boolean isSlowed;
    public int slowDuration;
    private boolean isFrozen;
    private SimpleTimer frostbiteFreezeTimer = new SimpleTimer();
    public SimpleTimer frostbiteTimer = new SimpleTimer();
    
    private GreenfootSound[] freezeSounds = {
        new GreenfootSound("freeze/freeze1.mp3"),
        new GreenfootSound("freeze/freeze2.mp3"),
        new GreenfootSound("freeze/freeze3.mp3")
    };
    private int freezeSoundIndex = 0;
    
    private int burnTicks;
    private double burnDamage;
    private SimpleTimer scorchTimer = new SimpleTimer();
    
    private GreenfootSound[] burnSounds = {
        new GreenfootSound("burn/burn1.mp3"),
        new GreenfootSound("burn/burn2.mp3"),
        new GreenfootSound("burn/burn3.mp3")
    };
    private int burnSoundIndex = 0;
    
    private boolean isDodged;
    
    private boolean isStunned;
    SimpleTimer stunTimer = new SimpleTimer();
    
    public String target = "hero";
    
    public double dx, dy;
    
    public Enemy(int hitpoints, double speed, int attack, int attackSpeed) {
        setImage("images/balloon1.png");
        
        for (GreenfootSound s : burnSounds) {
            s.setVolume(50);
        }
        
        // stat increases
        maxHitpoints = hitpoints;
        this.hitpoints = hitpoints;
        this.speed += speed;
        this.attack += attack;
        this.attackSpeed -= Math.min(attackSpeed, maxAttackSpeed);
        
        // add to list of all current enemies
        enemies.add(this);
        
        // enemy image
        GreenfootImage enemy = getImage();
        enemy.scale(25, 25);
        
        // marks attack cooldown timer
        attackCooldown.mark();
    }
    
    protected void addedToWorld(World world) {
        double scale = 0.05;
        
        redBar = new RedBar(scale);
        world.addObject(redBar, getX(), getY());
        
        greenBar = new GreenBar(scale);
        world.addObject(greenBar, getX(), getY());
    }
    
    public void act()
    {
        if (!isSlowed) target = "hero";
        else if (Hero.hero.vortexLvl > 0) target = "vortex";
        
        if (hitpoints <= 0) return;
        
        if (!target.equals("vortex")) {
            dx = Hero.hero.getExactX() - getExactX();
            dy = Hero.hero.getExactY() - getExactY();
        }
        
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        
        double normalizedDx = dx / magnitude;
        double normalizedDy = dy / magnitude;
        
        if (scorchTimer.millisElapsed() >= 1000) {
            if (burnTicks > 0) {
                burnSounds[burnSoundIndex].play();
                burnSoundIndex = (burnSoundIndex + 1) % burnSounds.length;
                
                removeHp((int)burnDamage, false, Color.RED, 25);
                burnTicks--;
                scorchTimer.mark();
                
                if (hitpoints <= 0) return;
            }
        }
        
        if (frostbiteTimer.millisElapsed() >= slowDuration) isSlowed = false;
        if (frostbiteFreezeTimer.millisElapsed() >= 1500) isFrozen = false;
        if (stunTimer.millisElapsed() >= 800) isStunned = false;
        
        if (isFrozen || isStunned) {
            setLocation(getExactX(), getExactY());
        }
        else if (isSlowed) {
            setLocation(getExactX() + (normalizedDx * speed/2), getExactY() + (normalizedDy * speed/2));
        }
        else {
            setLocation(getExactX() + (normalizedDx * speed), getExactY() + (normalizedDy * speed));
        }
        
        if (this.isTouching(Hero.class) && !isStunned) {
            if (attackCooldown.millisElapsed() >= attackSpeed) {
                attack();
                attackCooldown.mark();
            }
        }
        
        if (redBar != null && greenBar != null) {
            redBar.setPos(getX(), getY());
            greenBar.setPos(getX(), getY(), (double)hitpoints/(double)maxHitpoints);
        }
    }
    
    public void removeHp(int damage, boolean isCrit, Color color, int size) {
        hitpoints -= damage;
        
        if (isCrit) {
            color = Color.ORANGE;
            size = 35;
        }
        
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, size, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
        
        if (hitpoints <= 0) {
            GameWorld.gameWorld.removeObject(redBar);
            GameWorld.gameWorld.removeObject(greenBar);
            redBar = null;
            greenBar = null;
            
            GameWorld.gameWorld.removeObject(this);
            enemies.remove(this);
        }
    }
    
    public void attack() {
        if (Hero.hero.rogueLvl == 2) {
            if (Greenfoot.getRandomNumber(5) == 1) isDodged = true;
            else isDodged = false;
        }
        if (!isDodged || !Hero.hero.isImmune) Hero.hero.currentHp -= this.attack;
        GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        
        if (Greenfoot.getRandomNumber(100) <= Hero.hero.immuneChance && Hero.hero.spectralVeilLvl > 0) {
            if (!Hero.hero.isImmune) {
                Hero.hero.isImmune = true;
                Hero.hero.immunityTimer.mark();
            }
        }
        
        if (Hero.hero.currentHp <= 0) Hero.hero.isDead = true;
        else if (!isDodged && !Hero.hero.isImmune) Hero.hero.isHurt = true;
    }
    
    public void frostbite() {
        if (Hero.hero.frostbiteLvl > 0) {
            isSlowed = true;
            slowDuration = 4000;
            frostbiteTimer.mark();
        }   
        if (Hero.hero.frostbiteLvl > 1) {
            isFrozen = true;
            frostbiteFreezeTimer.mark();
            
            freezeSounds[freezeSoundIndex].play();
            freezeSoundIndex = (freezeSoundIndex + 1) % freezeSounds.length;
        }
    }

    
    public void scorch(double damage) {
        if (Hero.hero.scorchLvl > 0) {
            this.burnDamage = (int) ((damage * 0.5 * Hero.hero.scorchLvl) + 0.5);
            burnTicks = 3;
            scorchTimer.mark();
            
            burnSounds[burnSoundIndex].play();
            burnSoundIndex = (burnSoundIndex + 1) % burnSounds.length;
            
            removeHp((int)this.burnDamage, false, Color.RED, 25);
        }
    }
    
    public void vampire() {
        if (Hero.hero.vampireLvl == 1) {
            // increases current hp by 1 or 5% of max hp, whichever is more
            Hero.hero.currentHp = Math.min(Hero.hero.currentHp + 1, Hero.hero.maxHp);
            Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + (0.05 * Hero.hero.maxHp)), Hero.hero.maxHp);
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
        else if (Hero.hero.vampireLvl == 2) {
            // chance to grant additional hp on kill
            if (this.hitpoints <= 0) {
                if (Greenfoot.getRandomNumber(2) == 1) {
                    Hero.hero.maxHp++;
                    Hero.hero.currentHp++;
                }
            }
            // otherwise increase current hp by 1 or 15%, whicherver is more
            else {
                Hero.hero.currentHp = Math.min(Hero.hero.currentHp + 1, Hero.hero.maxHp);
                Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + (0.15 * Hero.hero.maxHp)), Hero.hero.maxHp);
            }
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
    }
    
    public void jester() {
        if (Hero.hero.jesterLvl > 0) {
            if (Greenfoot.getRandomNumber(2) > 0) {
                setLocation(Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
            }
            if (Greenfoot.getRandomNumber(2) > 0 && Hero.hero.jesterLvl > 1) {
                this.isStunned = true;
                removeHp(Greenfoot.getRandomNumber(((int) (Hero.hero.attack / 3)) + 1), false, Color.MAGENTA, 30);
                stunTimer.mark();
            }
        }
    }
    
    public void tornado(double damage) {
        if (Hero.hero.vortexLvl > 0  && Greenfoot.getRandomNumber(100) <= Hero.hero.tornadoChance) {
            Tornado vortex = new Tornado((int) damage);
            
            vortex.numCycles = Hero.hero.vortexLvl * 5;
            vortex.tornadoIndex = 0;
            
            this.target = "vortex";
            
            GameWorld.gameWorld.addObject(vortex, (int) this.getExactX(), (int) this.getExactY());
        }
    }
    
    public static void removeAll() {
        if (enemies.size() > 0) {
            GameWorld.gameWorld.removeObject(enemies.get(0).redBar);
            GameWorld.gameWorld.removeObject(enemies.get(0).greenBar);
            enemies.get(0).redBar = null;
            enemies.get(0).greenBar = null;
            GameWorld.gameWorld.removeObject(enemies.get(0));
            enemies.remove(enemies.get(0));
        }
    }
}
