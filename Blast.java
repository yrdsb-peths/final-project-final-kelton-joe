import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Hydro Burst unique upgrade
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class Blast extends SmoothMover
{
    // animation images of the blast
    GreenfootImage[] blast = new GreenfootImage[11];
    
    // timer and indexfor blast animation
    SimpleTimer blastTimer = new SimpleTimer();
    int blastIndex = 0;
    
    // damage it deals
    double damage;
    
    // blast size
    private int blastSize = 90;
    private final int projectilesize = 35;
    
    // enemies hit by blast
    List<Enemy> enemies = new ArrayList<Enemy>();
    Set<Enemy> enemiesHitSet = new HashSet<>();
    
    // speed of blast
    private double dx, dy;
    private double speed;
    
    // whether the blast has exploded
    private boolean hasExploded;
    
    // shrapnel
    private boolean isShrapnel;
    private double angleIncrement;
    private double shrapnelAngle;
    private double radians;
    private double shrapnelSpeed;
    private SimpleTimer shrapnelTimer = new SimpleTimer();
    private double shrapnelDamage;
    
    /**
     * Constructor is blast 
     * 
     * @param dx: normalized change in x
     * @param dy: normalized change in y
     * @param speed: how fast it goes
     * @param damage: damage dealt
     * @param isShrapnel: whether it is a shrapnel or not
     */
    public Blast(double dx, double dy, double speed, double damage, boolean isShrapnel) {
        // shrapnels have smaller blasts
        if (isShrapnel) blastSize *= 0.5;
        
        // creates image for blast animation
        for (int i = 0; i < blast.length; i++) {
            blast[i] = new GreenfootImage("blast/blast" + i + ".png");
            blast[i].scale(projectilesize, projectilesize);
        }
        // blast is bigger than projectile
        blast[6].scale(blastSize, blastSize);
        blast[7].scale(blastSize, blastSize);
        blast[8].scale(blastSize, blastSize);
        blast[9].scale(blastSize, blastSize);
        blast[10].scale(blastSize, blastSize);
        
        // gets angle for the direction of blast projectile
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        setRotation((int) angle);
        
        // sets instance variables
        this.dx = dx;
        this.dy = dy;
        this.speed = speed;
        this.damage = damage;
        this.isShrapnel = isShrapnel;
        
        // marks shrapnel life timer
        if (isShrapnel) shrapnelTimer.mark();
    }
    
    /**
     * Act method for shrapnel
     * 
     * hits all enemies if the blast occurs
     * otherwise keep travelling
     */
    public void act()
    {
        // animate
        animateBlast();
        
        // get all enemies touching the blast
        enemies = getIntersectingObjects(Enemy.class);
        
        if (!hasExploded) {
            // move if not touching enemy
            setLocation(getExactX() + dx*speed, getExactY() + dy*speed);
            
            // explode if touches enemy or shrapnel timer expires
            if (!enemies.isEmpty()) {
                blastIndex = 6;
                hasExploded = true;
            }
            if (shrapnelTimer.millisElapsed() > 400 && isShrapnel) {
                blastIndex = 6;
                hasExploded = true;
            }
        }
        
        // deals damage to all enemies touching the blast
        for (Enemy enemy : enemies) {
            if (enemy != null && blastIndex == 7 && !enemiesHitSet.contains(enemy)) {
                enemy.removeHp((int) this.damage, false, Color.BLUE, 25);
                
                // dont damage enemies already hit
                enemiesHitSet.add(enemy);
    
                // shrapnels cannot have these effects :sob:
                if (!isShrapnel) {
                    enemy.frostbite();
                    enemy.scorch(damage);
                    enemy.vampire();
                    enemy.jester();
                    enemy.tornado(damage);
                }
                
                // create shrapnels
                shrapnel();
            }
        }
        
        // remove if it goes out of screen
        if (getX() <= 5 || getX() >= GameWorld.gameWorld.getWidth() - 5 || getY() <= 5 || getY() >= GameWorld.gameWorld.getHeight() - 5) {
            GameWorld.gameWorld.removeObject(this);
        }
        
        // remove itself if the blast animation finishes
        if (blastIndex == blast.length) {
            GameWorld.gameWorld.removeObject(this);
            enemiesHitSet.clear();
        }
    }
    
    /**
     * Method for animating the blast
     */
    public void animateBlast() {
        // change frames every 100ms
        if (blastTimer.millisElapsed() < 100) return;
        blastTimer.mark();
        
        // changes to the next image
        if (blastIndex < blast.length) {
            setImage(blast[blastIndex]);
            blastIndex++;
            
            // if it does not explode, keep showing the projectile
            if (!hasExploded && blastIndex == 5) blastIndex = 0;
        }
        
        // if it hits an enemy and index goes to 6, it explodes
        if (blastIndex == 6) hasExploded = true;
    }
    
    /**
     * Shrapnel method for blast
     */
    private void shrapnel() {
        // checks hero shrapnel level and chance
        if (Hero.hero.shrapnelLvl > 0 && Greenfoot.getRandomNumber(100) <= Hero.hero.shrapnelChance) {
            // generates shrapnels
            for (int i = 1; i < Hero.hero.numShrapnel + 2; i++) {
                if (!isShrapnel) {
                    // angle calculation
                    angleIncrement = 360.0 / Hero.hero.numShrapnel;
                    shrapnelAngle = angleIncrement * i;
                    radians = Math.toRadians(shrapnelAngle);
                    dx = Math.cos(radians);
                    dy = Math.sin(radians);
                    
                    // speed and damage calculation depending on shrapnel level
                    shrapnelSpeed = Hero.hero.shrapnelLvl == 1 ? 2.0 : 10.0;
                    shrapnelDamage = Hero.hero.shrapnelLvl == 1 ? (0.3 * damage) + 1.0 : (0.6 * damage) + 1.0;
                    
                    // creates blast shrapnel 
                    Blast shrapnel = new Blast(dx, dy, shrapnelSpeed, shrapnelDamage, true);
                    
                    // adds shrapnel to the world
                    GameWorld.gameWorld.addObject(shrapnel, (int)getExactX(), (int)getExactY());
                }
            }
        }
    }
}
