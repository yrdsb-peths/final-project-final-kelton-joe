import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Tornado class for the Violent Vortex Unique upgrade
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class Tornado extends SmoothMover
{
    // duration of tornado
    SimpleTimer tornadoTimer = new SimpleTimer();
    
    // damage
    int damage;
    
    // active and how many cycles
    boolean isActive;
    int numCycles;
    
    // image of tornado
    GreenfootImage[] tornadoImage = new GreenfootImage[10];
    int tornadoIndex = 0;
    
    // list of enemies pulled
    List<Enemy> enemiesPulled = new ArrayList<Enemy>();
    
    // attack timers for each enemy
    Map<Enemy, SimpleTimer> enemyTimers = new HashMap<>();
    
    /**
     * Constructor for tornado
     * creates a new tornado and generates the images based on violent vortex level
     * 
     * @param damage: damage the tornado deals (if vortex lv 2)
     */
    public Tornado(int damage) {
        for (int i = 0; i < tornadoImage.length; i++) {
            // sets images
            tornadoImage[i] = new GreenfootImage("tornado/tornado" + i + ".png");
            if (Hero.hero.vortexLvl > 0) tornadoImage[i].scale(90, 90);
            // bigger size for level 2 vortex
            if (Hero.hero.vortexLvl > 1) tornadoImage[i].scale(125, 125);
            
            // sets damage
            this.damage = damage;
            
            // animation
            this.numCycles = 5 + (Hero.hero.vortexLvl * 5);
            this.tornadoIndex = 0;
        }
    }
    
    /**
     * Act method for tornado
     */
    public void act() {
        // animate
        animateTornado();
        
        // pull all enemies touching it
        enemiesPulled = getIntersectingObjects(Enemy.class);
        
        for (Enemy enemy : enemiesPulled) {
            if (isActive && enemy != null) {
                // slows enemies
                enemy.isSlowed = true;
                enemy.slowDuration = 1200;
                enemy.frostbiteTimer.mark();
                
                // makes enemies chase the tornado
                enemy.dx = this.getExactX() - enemy.getExactX();
                enemy.dy = this.getExactY() - enemy.getExactY();
                
                // sets attack intervals
                int interval = (Hero.hero.vortexLvl > 1) ? 400 : 800;
                
                // creates timer for attacking enemies
                SimpleTimer timer = enemyTimers.get(enemy);
                if (timer == null) {
                    timer = new SimpleTimer();
                    enemyTimers.put(enemy, timer);
                }
                
                // deals damage when off cooldown
                if (timer.millisElapsed() > interval) {
                    int damageDealt = (Hero.hero.vortexLvl > 1) ? this.damage : (int)(this.damage * 0.5);
                    enemy.removeHp(damageDealt, false, Color.GREEN, 20);
                    timer.mark();
                }
            }
        }
        
        // removes if the animation is complete 
        if (tornadoIndex == tornadoImage.length) {
            GameWorld.gameWorld.removeObject(this);
            for (Enemy enemy : enemiesPulled) enemy.isPulled = false;
        }
    }
    
    /**
     * Method for animating the tornado for a certain amount of cycles
     */
    public void animateTornado() {
        if (tornadoTimer.millisElapsed() < 150) return;
        tornadoTimer.mark();
        
        // can have damage and slow if tornado isn't disappearing
        if (tornadoIndex < 6) isActive = true;
        if (numCycles == 0) isActive = false;
        
        // animates tornado using index
        if (tornadoIndex < tornadoImage.length) {
            setImage(tornadoImage[tornadoIndex]);
            
            if (!isActive) tornadoIndex++;
            
            // repeats middle section if there are cycles left
            else if (numCycles > 0) {
                if (tornadoIndex == 5) tornadoIndex = 4;  
                else tornadoIndex = 5;
                numCycles--;
            }
        }
    }
}
