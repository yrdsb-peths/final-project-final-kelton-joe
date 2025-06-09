import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.HashSet;

/**
 * Write a description of class Projectile here.
 * 
 * @author Kelton and Joe
 * @version May 2024
 */
public class Projectile extends SmoothMover
{
    // movement
    private double nx, ny;
    private double speed;
    
    // damage dealt
    private double damage;
    
    // number of times it can hit enemies
    private int durability;
    
    // whether it is a critical attack
    private boolean isCrit;
    
    // whether it is removed from the world
    private boolean isRemoved;
    
    // shrapnels cannot inherit other effects (freeze, burn, etc)
    private boolean isShrapnel;
    
    // used for shrapnel angle calculations
    private double angleIncrement;
    private double shrapnelAngle;
    private double radians;
    private double dx, dy;
    private double shrapnelSpeed;
    private SimpleTimer shrapnelTimer = new SimpleTimer();
    private double shrapnelDamage;
    
    // enemies hit by the projectile
    private HashSet<Enemy> enemiesHit;
    
    // timer for maximum duration of thunderstrike volley arrows
    private SimpleTimer stormTimer = new SimpleTimer();
    
    public Projectile(double nx, double ny, double speed, double damage, boolean isCrit, boolean isShrapnel) {
        // sets image based on effects
        if (Hero.hero.scorchLvl > 0) setImage(new GreenfootImage("scorchArrow.png"));
        else if (Hero.hero.frostbiteLvl > 0) setImage(new GreenfootImage("frostArrow.png"));
        else setImage(new GreenfootImage("arrow.png"));
        
        // scales image
        getImage().scale((int)(getImage().getWidth() * 0.15), (int)(getImage().getHeight() * 0.15));
        
        // changes angle/rotation
        double angle = Math.toDegrees(Math.atan2(ny, nx));
        setRotation((int)angle);
        
        // sets instance variables
        this.nx = nx;
        this.ny = ny;
        this.speed = speed;
        this.damage = damage;
        this.isCrit = isCrit;
        this.isShrapnel = isShrapnel;
        
        // marks timers if applicable
        if (isShrapnel) shrapnelTimer.mark();
        
        // default durability
        this.durability = 1;
        
        // sharpshot durability increase
        if (Hero.hero.sharpshotLvl == 2) this.durability += 4;
        else if (Hero.hero.sharpshotLvl == 1) this.durability += 2;
        
        // thunderstrike volley durability increase
        if (Hero.hero.thunderLvl > 1) this.durability++;
        
        // shrapnel durability increase
        if (isShrapnel && Hero.hero.shrapnelLvl > 1) this.durability += 2;
        if (Hero.hero.thunderLvl > 0) stormTimer.mark();
        
        // not removed upon spawn
        isRemoved = false;
        
        // enemies hit is originally empty
        enemiesHit = new HashSet<Enemy>();
    }
    
    /**
     * Act method for projectiles (arrows)
     * Controls movement and hitting enemies
     */
    public void act()
    {
        if (!isRemoved) {
            // movement
            setLocation(getExactX() + nx*speed, getExactY() + ny*speed);
            
            // finds enemy to hit
            Enemy enemy = (Enemy) getOneIntersectingObject(Enemy.class);
            
            if (enemy != null) attack(enemy);
            // removes itself if it goes out of the world
            else if (getX() <= 5 || getX() >= GameWorld.gameWorld.getWidth() - 5 ||
                getY() <= 5 || getY() >= GameWorld.gameWorld.getHeight() - 5) {
                GameWorld.gameWorld.removeObject(this);
                isRemoved = true;
            }
        }
        // removes itself if it is a special type of arrow and exceeds timers
        if (shrapnelTimer.millisElapsed() > 800 && isShrapnel) GameWorld.gameWorld.removeObject(this);
        if (stormTimer.millisElapsed() > 800 && Hero.hero.thunderLvl > 0) GameWorld.gameWorld.removeObject(this);
    }
    
    /**
     * Method for attacking an enemy
     * 
     * @param enemy: enemy to attack
     */
    private void attack(Enemy enemy) {
        // adds enemies to the list of hit enemies (prevents them from being hit twice
        if (!enemiesHit.contains(enemy)) enemyHit(enemy);
    }
    
    /**
     * Method for dealing damage to the enemy
     */
    private void enemyHit(Enemy enemy) {
        // enemies cannot be hit twice by the same arrow
        enemiesHit.add(enemy);
        
        // deals damage
        enemy.removeHp((int)(damage + 0.5), isCrit, Color.GRAY, 20);
        
        // status effects if it is not a shrapnel
        if (!isShrapnel) {
            enemy.frostbite();
            enemy.scorch(damage);
            enemy.vampire();
            enemy.jester();
            enemy.tornado(damage);
            
            // chance to stun enemies on hit 
            if (Greenfoot.getRandomNumber(100) < Hero.hero.thunderLvl * 3) enemy.stun(300 * Hero.hero.thunderLvl);
            
            // shrapnel
            shrapnel(enemy);
        }
        
        // reduces durability
        durability--;
        
        // if no durability, remove itself from the world
        if (durability == 0) {
            GameWorld.gameWorld.removeObject(this);
            isRemoved = true;
        }
    }
    
    /**
     * Method for spawning shrapnels at the location of an enemy
     * 
     * @param enemy: enemy hit by initial attack
     */
    private void shrapnel(Enemy enemy) {
        // only a chance to spawn shrapnels
        if (Hero.hero.shrapnelLvl > 0 && Greenfoot.getRandomNumber(100) <= Hero.hero.shrapnelChance) {
            for (int i = 1; i < Hero.hero.numShrapnel + 2; i++) {
                // shrapnels cannot create more shrapnels (unfortunately)
                if (!isShrapnel) {
                    // calculates angles
                    angleIncrement = 360.0 / Hero.hero.numShrapnel;
                    shrapnelAngle = angleIncrement * i;
                    radians = Math.toRadians(shrapnelAngle);
                    dx = Math.cos(radians);
                    dy = Math.sin(radians);
                    
                    // calculates speed and damage dealt based on shrapnel level
                    shrapnelSpeed = Hero.hero.shrapnelLvl == 1 ? 2.0 : 10.0;
                    shrapnelDamage = Hero.hero.shrapnelLvl == 1 ? (0.4 * damage) + 1.0 : (0.8 * damage) + 1.0;
                    
                    // creates shrapnel
                    Projectile shrapnel = new Projectile(dx, dy, shrapnelSpeed, shrapnelDamage, isCrit, true);
                    
                    // shrapnel cannot hit original enemy
                    shrapnel.enemiesHit.add(enemy);
                    
                    // adds shrapnel to the world
                    GameWorld.gameWorld.addObject(shrapnel, (int)getExactX(), (int)getExactY());
                }
            }
        }
    }
}
