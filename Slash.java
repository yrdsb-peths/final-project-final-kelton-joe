import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Blood Slash Unique Upgrade
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class Slash extends SmoothMover
{
    // images
    GreenfootImage[] slash = new GreenfootImage[7];
    
    // timer between animations
    SimpleTimer slashTimer = new SimpleTimer();
    int slashIndex = 0;
    
    // damage
    private double damage;
    private boolean isCrit;
    
    // enemies hit
    List<Enemy> enemies = new ArrayList<Enemy>();
    Set<Enemy> enemiesHitSet = new HashSet<>();
    
    // self heal
    double selfHeal;
    double kills;
    
    /**
     * Constructor for slash
     * 
     * @param damage: damage dealt by attack
     * @param isCrit: whether it is a critical hit
     */
    public Slash(double damage, boolean isCrit) {
        // sets images
        for (int i = 0; i < slash.length; i++) {
            slash[i] = new GreenfootImage("bloodslash/slash" + i + ".png");
            slash[i].scale(150, 150);
            if (isCrit) slash[i].scale(200, 200);
        }
        
        // sets instance variables
        this.damage = damage;
        this.isCrit = isCrit;
    }
    
    /**
     * Act method for Slash
     */
    public void act() {
        // animate
        animateSlash();
        
        // get enemies hit
        enemies = getIntersectingObjects(Enemy.class);
        
        // deals damage to enemies 
        for (Enemy enemy : enemies) {
            if (enemy != null && slashIndex == 3 && !enemiesHitSet.contains(enemy)) {
                if (isCrit) enemy.removeHp((int) this.damage, false, Color.RED, 35);
                else enemy.removeHp((int) this.damage, false, Color.RED, 25);
                
                // kills give extra healing
                if (enemy.hitpoints <= 0) selfHeal += Hero.hero.maxHp * 0.05;
                
                // dont hit the same enemy
                enemiesHitSet.add(enemy);
                
                // self heal on hit
                selfHeal += Hero.hero.maxHp * 0.02;
                
                // apply other effects from other uniques
                enemy.frostbite();
                enemy.scorch(damage);
                enemy.vampire();
                enemy.jester();
                enemy.tornado(damage);
            }
        }
        
        // removes slash once animation ends
        if (slashIndex == slash.length) {
            GameWorld.gameWorld.removeObject(this);
            enemiesHitSet.clear();
            
            // heals hero based on kills and enemies hit
            Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + selfHeal), Hero.hero.maxHp);
            
            // resets self heal
            selfHeal = 0;
            
            // updates health bar
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
    }
    
    /**
     * Animate slash method
     */
    public void animateSlash() {
        // breaks if animation frame is not finished
        if (slashTimer.millisElapsed() < 60) return;
        
        // marks animation timer
        slashTimer.mark();
        
        // continues animation if not finished
        if (slashIndex < slash.length) {
            setImage(slash[slashIndex]);
            slashIndex++;
        }
    }
}
