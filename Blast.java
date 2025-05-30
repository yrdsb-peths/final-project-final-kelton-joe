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
    GreenfootImage[] blast = new GreenfootImage[11];
    
    SimpleTimer blastTimer = new SimpleTimer();
    int blastIndex = 0;
    
    double damage;
    private final int blastSize = 110;
    private final int projectilesize = 35;
    
    List<Enemy> enemies = new ArrayList<Enemy>();
    Set<Enemy> enemiesHitSet = new HashSet<>();
    
    private double dx, dy;
    private double speed;
    private boolean hasExploded;
    
    public Blast(double dx, double dy, double speed, double damage) {
        for (int i = 0; i < blast.length; i++) {
            blast[i] = new GreenfootImage("blast/blast" + i + ".png");
            blast[i].scale(projectilesize, projectilesize);
        }
        blast[6].scale(blastSize, blastSize);
        blast[7].scale(blastSize, blastSize);
        blast[8].scale(blastSize, blastSize);
        blast[9].scale(blastSize, blastSize);
        blast[10].scale(blastSize, blastSize);
        
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        setRotation((int) angle);
        
        this.dx = dx;
        this.dy = dy;
        this.speed = speed;
        this.damage = damage;
    }
    
    public void act()
    {
        animateBlast();
        
        enemies = getIntersectingObjects(Enemy.class);
        
        if (!hasExploded) {
            setLocation(getExactX() + dx*speed, getExactY() + dy*speed);
            if (!enemies.isEmpty()) {
                blastIndex = 6;
                hasExploded = true;
            }
        }
        
        for (Enemy enemy : enemies) {
            if (enemy != null && blastIndex == 7 && !enemiesHitSet.contains(enemy)) {
                enemy.removeHp((int) this.damage, false, Color.BLUE, 30);
                
                enemiesHitSet.add(enemy);
    
                enemy.frostbite();
                enemy.scorch(damage);
                enemy.vampire();
                enemy.jester();
                enemy.tornado(damage);
            }
        }
        
        if (blastIndex == blast.length) {
            GameWorld.gameWorld.removeObject(this);
            enemiesHitSet.clear();
        }
    }
    
    public void animateBlast() {
        if (blastTimer.millisElapsed() < 100) return;
        blastTimer.mark();
        
        if (blastIndex < blast.length) {
            setImage(blast[blastIndex]);
            blastIndex++;
            if (!hasExploded && blastIndex == 5) blastIndex = 0;
        }
        
        if (blastIndex == 6) hasExploded = true;
    }
}
