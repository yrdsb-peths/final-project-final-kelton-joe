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
    
    private boolean addHealth;
    
    private HashSet<Enemy> enemiesHit;
    
    public Projectile(double nx, double ny, double speed, double damage, boolean isCrit) {
        GreenfootImage image = new GreenfootImage("arrow.png");
        setImage(image);
        image.scale((int)(image.getWidth() * 0.1), (int)(image.getHeight() * 0.1));
        
        double angle = Math.toDegrees(Math.atan2(ny, nx));
        setRotation((int)angle);
        
        this.nx = nx;
        this.ny = ny;
        this.speed = speed;
        this.damage = damage;
        this.isCrit = isCrit;
        
        if (Hero.hero.sharpshotLvl == 2) this.durability = 5;
        else if (Hero.hero.sharpshotLvl == 1) this.durability = 3;
        else this.durability = 1;
        
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
            } else if (getX() <= 5 || getX() >= GameWorld.gameWorld.getWidth() - 5 ||
                getY() <= 5 || getY() >= GameWorld.gameWorld.getHeight() - 5) {
                GameWorld.gameWorld.removeObject(this);
                isRemoved = true;
            }
        }
    }
    
    private void attack(Enemy enemy) {
        if (!enemiesHit.contains(enemy)) {
            enemyHit(enemy);
        }
    }
    
    private void enemyHit(Enemy enemy) {
        enemiesHit.add(enemy);
        
        enemy.removeHp((int)(damage + 0.5), isCrit, Color.GRAY, 20);
        frostbite(enemy);
        scorch(enemy);
        vampire(enemy);
        jester(enemy);
        
        durability--;
        if (durability == 0) {
            GameWorld.gameWorld.removeObject(this);
            isRemoved = true;
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
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
        else if (Hero.hero.vampireLvl == 2) {
            if (enemy.hitpoints <= 0) {
                if (Greenfoot.getRandomNumber(3) == 1) {
                    Hero.hero.maxHp++;
                    Hero.hero.currentHp++;
                }
                else Hero.hero.currentHp = Math.min(Hero.hero.currentHp + 1, Hero.hero.maxHp);
                GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
            }
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
}
