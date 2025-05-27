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
    
    private boolean isSlowed;
    private boolean isFrozen;
    private SimpleTimer frostbiteFreezeTimer = new SimpleTimer();
    private SimpleTimer frostbiteTimer = new SimpleTimer();
    
    private int burnTicks;
    private double burnDamage;
    private SimpleTimer scorchTimer = new SimpleTimer();
    
    private boolean isDodged;
    
    private boolean isStunned;
    SimpleTimer stunTimer = new SimpleTimer();
    
    public Enemy(int hitpoints, double speed, int attack, int attackSpeed) {
        setImage("images/balloon1.png");
        
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
        if (hitpoints <= 0) return;
        
        double dx = Hero.hero.getExactX() - getExactX();
        double dy = Hero.hero.getExactY() - getExactY();
        
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        
        double normalizedDx = dx / magnitude;
        double normalizedDy = dy / magnitude;
        
        if (scorchTimer.millisElapsed() >= 1000) {
            if (burnTicks > 0) {
                removeHp((int)burnDamage, false, Color.RED);
                burnTicks--;
                scorchTimer.mark();
                
                if (hitpoints <= 0) return;
            }
        }
        
        if (frostbiteTimer.millisElapsed() >= 5000) isSlowed = false;
        if (frostbiteFreezeTimer.millisElapsed() >= 1500) isFrozen = false;
        if (stunTimer.millisElapsed() >= 800) isStunned = false;
        
        if (isSlowed) {
            setLocation(getExactX() + (normalizedDx * speed/2), getExactY() + (normalizedDy * speed/2));
        } 
        else if (isFrozen || isStunned) {
            setLocation(getExactX(), getExactY());
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
    
    public void removeHp(int damage, boolean isCrit, Color color) {
        hitpoints -= damage;
        
        if (isCrit) color = Color.ORANGE;
        
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, isCrit, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
        
        if (hitpoints <= 0) {
            GameWorld.gameWorld.removeObject(redBar);
            GameWorld.gameWorld.removeObject(greenBar);
            redBar = null;
            greenBar = null;
            
            getWorld().removeObject(this);
            enemies.remove(this);
        }
    }
    
    public void attack() {
        if (Hero.hero.rogueLvl == 2) {
            if (Greenfoot.getRandomNumber(5) == 1) isDodged = true;
            else isDodged = false;
        }
        if (!isDodged) Hero.hero.currentHp -= this.attack;
        GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        
        if (Hero.hero.currentHp <= 0) Hero.hero.isDead = true;
        else if (!isDodged) Hero.hero.isHurt = true;
    }
    
    public void frostbite() {
        if (Hero.hero.frostbiteLvl == 1) {
            isSlowed = true;
            frostbiteTimer.mark();
        }   
        else if (Hero.hero.frostbiteLvl == 2) {
            isFrozen = true;
            frostbiteFreezeTimer.mark();
        }
    }
    
    public void scorch(double burnDamage) {
        this.burnDamage = burnDamage;
        burnTicks = 3;
        scorchTimer.mark();
    }
    
    public void jester(int stun) {
        if (stun > 0) {
            this.isStunned = true;
            stunTimer.mark();
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
