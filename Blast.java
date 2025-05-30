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
    private int blastSize = 110;
    private final int projectilesize = 35;
    
    List<Enemy> enemies = new ArrayList<Enemy>();
    Set<Enemy> enemiesHitSet = new HashSet<>();
    
    private double dx, dy;
    private double speed;
    private boolean hasExploded;
    
    private boolean isShrapnel;
    private double angleIncrement;
    private double shrapnelAngle;
    private double radians;
    private double shrapnelSpeed;
    private SimpleTimer shrapnelTimer = new SimpleTimer();
    private double shrapnelDamage;
    
    public Blast(double dx, double dy, double speed, double damage, boolean isShrapnel) {
        if (isShrapnel) blastSize *= 0.5;
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
        this.isShrapnel = isShrapnel;
        
        if (isShrapnel) shrapnelTimer.mark();
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
            if (shrapnelTimer.millisElapsed() > 400 && isShrapnel) {
                blastIndex = 6;
                hasExploded = true;
            }
        }
        
        for (Enemy enemy : enemies) {
            if (enemy != null && blastIndex == 7 && !enemiesHitSet.contains(enemy)) {
                enemy.removeHp((int) this.damage, false, Color.BLUE, 25);
                
                enemiesHitSet.add(enemy);
    
                enemy.frostbite();
                enemy.scorch(damage);
                enemy.vampire();
                enemy.jester();
                enemy.tornado(damage);
                
                shrapnel();
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
    
    private void shrapnel() {
        if (Hero.hero.shrapnelLvl > 0 && Greenfoot.getRandomNumber(100) <= Hero.hero.shrapnelChance) {
            for (int i = 1; i < Hero.hero.numShrapnel + 2; i++) {
                if (!isShrapnel) {
                    angleIncrement = 360.0 / Hero.hero.numShrapnel;
                    shrapnelAngle = angleIncrement * i;
                    
                    radians = Math.toRadians(shrapnelAngle);
                    
                    dx = Math.cos(radians);
                    dy = Math.sin(radians);
                    
                    shrapnelSpeed = Hero.hero.shrapnelLvl == 1 ? 2.0 : 10.0;
                    shrapnelDamage = Hero.hero.shrapnelLvl == 1 ? 0.3 * damage : 0.6 * damage;
                    
                    Blast shrapnel = new Blast(dx, dy, shrapnelSpeed, shrapnelDamage, true);
                    
                    GameWorld.gameWorld.addObject(shrapnel, (int)getExactX(), (int)getExactY());
                }
            }
        }
    }
}
