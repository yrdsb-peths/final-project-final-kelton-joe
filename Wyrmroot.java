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
    // health
    public int maxHitpoints;
    public int hitpoints;
    
    // attack speed
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
    
    private double speed = 1.0;
    private double attackSpeed = 2000;
    
    public Wyrmroot(int hitpoints, int attack) {
        super(hitpoints, 1.0, attack, 2000);
        attackCooldown.mark();
    }
    
    protected void addedToWorld(World world) {
        double scale = 0.1;
        
        redBar = new RedBar(scale);
        world.addObject(redBar, getX(), getY());
        
        greenBar = new GreenBar(scale);
        world.addObject(greenBar, getX(), getY());
    }
    
    /**
     * Act - do whatever the Boss wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        if (hitpoints <= 0) return;
        
        dx = Hero.hero.getExactX() - getExactX();
        dy = Hero.hero.getExactY() - getExactY();
        
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
        if (frostbiteFreezeTimer.millisElapsed() >= 200) isFrozen = false;
        if (stunTimer.millisElapsed() >= 100) isStunned = false;
        
        if (isFrozen || isStunned) {
            setLocation(getExactX(), getExactY());
        }
        else if (isSlowed) {
            setLocation(getExactX() + (normalizedDx * speed * 0.8), getExactY() + (normalizedDy * speed * 0.8));
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
    
    @Override
    public void jester(int stun, int stunDamage) {
        if (stun > 0) {
            this.isStunned = true;
            removeHp(stunDamage, false, Color.MAGENTA, 30);
            stunTimer.mark();
        }
    }
    
    private void blueBite() {
        
    }
    
    private void purpleBite() {
        
    }
}
