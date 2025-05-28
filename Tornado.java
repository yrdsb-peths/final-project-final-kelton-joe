import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;

/**
 * Write a description of class Tornado here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tornado extends SmoothMover
{
    SimpleTimer tornadoTimer = new SimpleTimer();
    
    int damage;
    SimpleTimer attackTimer = new SimpleTimer();
    
    boolean isActive;
    int numCycles;
    
    GreenfootImage[] tornadoImage = new GreenfootImage[10];
    int tornadoIndex = 0;
    
    List<Enemy> enemiesPulled = new ArrayList<Enemy>();
    
    public Tornado(int damage) {
        for (int i = 0; i < tornadoImage.length; i++) {
            tornadoImage[i] = new GreenfootImage("tornado/tornado" + i + ".png");
            if (Hero.hero.vortexLvl > 0) tornadoImage[i].scale(90, 90);
            if (Hero.hero.vortexLvl > 1) tornadoImage[i].scale(125, 125);
            
            this.damage = damage;
            attackTimer.mark();
        }
    }
    
    public void act() {
        animateTornado();
        
        enemiesPulled = getIntersectingObjects(Enemy.class);
        
        for (Enemy enemy : enemiesPulled) {
            if (isActive && enemy != null) {
                enemy.isSlowed = true;
                enemy.slowDuration = 1200;
                enemy.frostbiteTimer.mark();
                
                enemy.dx = this.getExactX() - enemy.getExactX();
                enemy.dy = this.getExactY() - enemy.getExactY();
                
                if (Hero.hero.vortexLvl > 1) {
                    if (attackTimer.millisElapsed() > 600) {
                        enemy.removeHp(this.damage, false, Color.GREEN, 20);
                        attackTimer.mark();
                    }
                }
            }
        }
        
        if (tornadoIndex == tornadoImage.length) {
            GameWorld.gameWorld.removeObject(this);
        }
    }
    
    public void animateTornado() {
        if (tornadoTimer.millisElapsed() < 150) return;
        tornadoTimer.mark();
        
        if (tornadoIndex < 6) isActive = true;
        if (numCycles == 0) isActive = false;
        
        if (tornadoIndex < tornadoImage.length) {
            setImage(tornadoImage[tornadoIndex]);
            
            if (!isActive) tornadoIndex++;
            
            else if (numCycles > 0) {
                if (tornadoIndex == 5) tornadoIndex = 4;  
                else tornadoIndex = 5;
                numCycles--;
            }
        }
    }
}
