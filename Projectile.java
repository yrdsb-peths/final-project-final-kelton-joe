import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.HashSet;

/**
 * Write a description of class Projectile here.
 * 
 * @author Kelton and Joe
 * @version May 2024
 */
public class Projectile extends SmoothMover
{
    private double nx, ny;
    private double speed;
    private double damage;
    private int durability;
    private boolean isCrit;
    private boolean isRemoved;
    
    private boolean isShrapnel;
    private double angleIncrement;
    private double shrapnelAngle;
    private double radians;
    private double dx, dy;
    private double shrapnelSpeed;
    private SimpleTimer shrapnelTimer = new SimpleTimer();
    private double shrapnelDamage;
    
    private boolean addHealth;
    
    private HashSet<Enemy> enemiesHit;
    
    GreenfootImage image;
    
    public Projectile(double nx, double ny, double speed, double damage, boolean isCrit, boolean isShrapnel) {
        if (Hero.hero.scorchLvl > 0) image = new GreenfootImage("scorchArrow.png");
        else if (Hero.hero.frostbiteLvl > 0) image = new GreenfootImage("frostArrow.png");
        else image = image = new GreenfootImage("arrow.png");
        
        setImage(image);
        image.scale((int)(image.getWidth() * 0.15), (int)(image.getHeight() * 0.15));
        
        double angle = Math.toDegrees(Math.atan2(ny, nx));
        setRotation((int)angle);
        
        this.nx = nx;
        this.ny = ny;
        this.speed = speed;
        this.damage = damage;
        this.isCrit = isCrit;
        this.isShrapnel = isShrapnel;
        if (isShrapnel) shrapnelTimer.mark();
        
        if (Hero.hero.sharpshotLvl == 2) this.durability = 5;
        else if (Hero.hero.sharpshotLvl == 1) this.durability = 3;
        else this.durability = 1;
        
        if (isShrapnel) {
            if (Hero.hero.shrapnelLvl == 1) this.durability = 1;
            else this.durability = 3;
        }
        
        isRemoved = false;
        
        enemiesHit = new HashSet<Enemy>();
    }
    
    public void act()
    {
        if (!isRemoved) {
            setLocation(getExactX() + nx*speed, getExactY() + ny*speed);
            
            Enemy enemy = (Enemy) getOneIntersectingObject(Enemy.class);
            
            if (enemy != null) {
                attack(enemy);
            } 
            else if (getX() <= 5 || getX() >= GameWorld.gameWorld.getWidth() - 5 ||
                getY() <= 5 || getY() >= GameWorld.gameWorld.getHeight() - 5) {
                GameWorld.gameWorld.removeObject(this);
                isRemoved = true;
            }
        }
        if (shrapnelTimer.millisElapsed() > 600 && isShrapnel) GameWorld.gameWorld.removeObject(this);
    }
    
    private void attack(Enemy enemy) {
        if (!enemiesHit.contains(enemy)) {
            enemyHit(enemy);
        }
    }
    
    private void enemyHit(Enemy enemy) {
        enemiesHit.add(enemy);
        
        enemy.removeHp((int)(damage + 0.5), isCrit, Color.GRAY, 20);
        
        if (!isShrapnel) {
            enemy.frostbite();
            enemy.scorch(damage);
            enemy.vampire();
            enemy.jester();
            enemy.tornado(damage);
            
            shrapnel();
        }
        
        durability--;
        if (durability == 0) {
            GameWorld.gameWorld.removeObject(this);
            isRemoved = true;
        }
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
                    shrapnelDamage = Hero.hero.shrapnelLvl == 1 ? (0.4 * damage) + 1.0 : (0.8 * damage) + 1.0;
                    
                    Projectile shrapnel = new Projectile(dx, dy, shrapnelSpeed, shrapnelDamage, isCrit, true);
                    
                    GameWorld.gameWorld.addObject(shrapnel, (int)getExactX(), (int)getExactY());
                }
            }
        }
    }
}
