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
public class Blast extends Actor
{
    GreenfootImage[] blast = new GreenfootImage[11];
    
    SimpleTimer blastTimer = new SimpleTimer();
    int blastIndex = 0;
    
    double damage;
    
    List<Enemy> enemies = new ArrayList<Enemy>();
    Set<Enemy> enemiesHitSet = new HashSet<>();
    
    public Blast(double damage) {
        for (int i = 0; i < blast.length; i++) {
            blast[i] = new GreenfootImage("blast/blast" + i + ".png");
            if (Hero.hero.burstLvl == 1) blast[i].scale(50, 50);
            else blast[i].scale(80, 80);
        }
        
        this.damage = damage;
    }
    
    public void act()
    {
        animateBlast();
        
        enemies = getIntersectingObjects(Enemy.class);
        
        for (Enemy enemy : enemies) {
            if (enemy != null && blastIndex == 3 && !enemiesHitSet.contains(enemy)) {
                enemy.removeHp((int) this.damage, false, Color.BLUE, 30);
                
                enemiesHitSet.add(enemy);
                
                vampire(enemy);
                GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
    
                frostbite(enemy);
                scorch(enemy);
                jester(enemy);
                tornado(enemy);
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
