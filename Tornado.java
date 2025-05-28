import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

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
    
    public Tornado(int damage) {
        for (int i = 0; i < tornadoImage.length; i++) {
            tornadoImage[i] = new GreenfootImage("tornado/tornado" + i + ".png");
            tornadoImage[i].scale(90, 90);
            
            this.damage = damage;
        }
    }
    
    public void act() {
        animateTornado();
        
        Enemy enemy = (Enemy) getOneIntersectingObject(Enemy.class);
        
        if (isActive && enemy != null) {
            enemy.isSlowed = true;
            enemy.slowDuration = 1200;
            enemy.frostbiteTimer.mark();
            
            enemy.dx = this.getExactX() - enemy.getExactX();
            enemy.dy = this.getExactY() - enemy.getExactY();
            
            if (Hero.hero.vortexLvl > 1) {
                if (attackTimer.millisElapsed() > 800) {
                    enemy.removeHp(this.damage, false, Color.GREEN, 20);
                    attackTimer.mark();
                }
            }
        }
        
        if (tornadoIndex == tornadoImage.length) {
            GameWorld.gameWorld.removeObject(this);
            if (enemy != null) enemy.target = "hero";
        }
    }
    
    public void animateTornado() {
        if (tornadoTimer.millisElapsed() < 150) return;
        tornadoTimer.mark();
        
        if (tornadoIndex > 3 && tornadoIndex < 6) isActive = true;
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
