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
            slash[i].scale(125, 125);
            if (isCrit) slash[i].scale(160, 160);
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
                
                vampire(enemy);
                GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
                
                frostbite(enemy);
                scorch(enemy);
                jester(enemy);
                tornado(enemy);
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
    
     private void frostbite(Enemy enemy) {
        if (Hero.hero.frostbiteLvl > 0) enemy.frostbite();
    }
    
    private void scorch(Enemy enemy) {
        if (Hero.hero.scorchLvl > 0) enemy.scorch(damage * 0.5 * Hero.hero.scorchLvl);
    }
    
    private void vampire(Enemy enemy) {
        if (Hero.hero.vampireLvl == 1) {
            Hero.hero.currentHp = Math.min(Hero.hero.currentHp + 1, Hero.hero.maxHp);
            Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + (0.05 * Hero.hero.maxHp)), Hero.hero.maxHp);
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
        else if (Hero.hero.vampireLvl == 2) {
            if (enemy.hitpoints <= 0) {
                if (Greenfoot.getRandomNumber(2) == 1) {
                    Hero.hero.maxHp++;
                    Hero.hero.currentHp++;
                }
            }
            else {
                Hero.hero.currentHp = Math.min(Hero.hero.currentHp + 1, Hero.hero.maxHp);
                Hero.hero.currentHp = Math.min((int) (Hero.hero.currentHp + (0.15 * Hero.hero.maxHp)), Hero.hero.maxHp);
            }
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
    }
    
    private void jester(Enemy enemy) {
        if (Hero.hero.jesterLvl > 0) {
            if (Greenfoot.getRandomNumber(2) > 0) {
                enemy.setLocation(Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
            }
            if (Hero.hero.jesterLvl == 2) {
                enemy.jester(Greenfoot.getRandomNumber(2), Greenfoot.getRandomNumber(((int) (Hero.hero.attack / 3)) + 1));
            }
        }
    }
    
    private void tornado(Enemy enemy) {
        if (Hero.hero.vortexLvl > 0  && Greenfoot.getRandomNumber(100) <= Hero.hero.tornadoChance) {
            Tornado vortex = new Tornado((int) (this.damage * 0.25));
            
            vortex.numCycles = Hero.hero.vortexLvl * 5;
            vortex.tornadoIndex = 0;
            
            enemy.target = "vortex";
            
            GameWorld.gameWorld.addObject(vortex, (int) enemy.getExactX(), (int) enemy.getExactY());
        }
    }
}
