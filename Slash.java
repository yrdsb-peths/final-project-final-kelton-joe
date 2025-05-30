import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Write a description of class Slash here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Slash extends SmoothMover
{
    GreenfootImage[] slash = new GreenfootImage[7];
    
    SimpleTimer slashTimer = new SimpleTimer();
    int slashIndex = 0;
    
    double damage;
    boolean isCrit;
    
    List<Enemy> enemies = new ArrayList<Enemy>();
    Set<Enemy> enemiesHitSet = new HashSet<>();
    
    public Slash(double damage, boolean isCrit) {
        for (int i = 0; i < slash.length; i++) {
            slash[i] = new GreenfootImage("bloodslash/slash" + i + ".png");
            slash[i].scale(150, 150);
            if (isCrit) slash[i].scale(200, 200);
        }
        
        this.damage = damage;
        this.isCrit = isCrit;
    }
    
    public void act() {
        animateSlash();
        
        enemies = getIntersectingObjects(Enemy.class);
        
        for (Enemy enemy : enemies) {
            if (enemy != null && slashIndex == 3 && !enemiesHitSet.contains(enemy)) {
                if (isCrit) enemy.removeHp((int) this.damage, false, Color.RED, 35);
                else enemy.removeHp((int) this.damage, false, Color.RED, 25);
                
                enemiesHitSet.add(enemy);
                
                if ((int) Hero.hero.currentHp <= 0) Hero.hero.isDead = true;
                
                enemy.frostbite();
                enemy.scorch(damage);
                enemy.vampire();
                enemy.jester();
                enemy.tornado(damage);
            }
        }
        
        if (slashIndex == slash.length) {
            GameWorld.gameWorld.removeObject(this);
            enemiesHitSet.clear();
            
            if (Hero.hero.currentHp <= 0) {
                Hero.hero.isDead = true;
            }
            
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
    }
    
    public void animateSlash() {
        if (slashTimer.millisElapsed() < 60) return;
        slashTimer.mark();
        
        if (slashIndex < slash.length) {
            setImage(slash[slashIndex]);
            slashIndex++;
        }
    }
}
