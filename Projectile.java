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
    
    public Projectile(double nx, double ny, double speed, double damage, boolean isCrit, boolean isShrapnel) {
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
        frostbite(enemy);
        scorch(enemy);
        vampire(enemy);
        jester(enemy);
        tornado(enemy);
        shrapnel();
        burst();
        
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
        if (Hero.hero.jesterLvl == 1) {
            enemy.jester(0, 0);
        }
        if (Hero.hero.jesterLvl == 2) {
            enemy.jester(Greenfoot.getRandomNumber(2), Greenfoot.getRandomNumber(((int) (Hero.hero.attack / 3)) + 1));
        }
    }
    
    private void tornado(Enemy enemy) {
        if (Hero.hero.vortexLvl > 0  && Greenfoot.getRandomNumber(100) <= Hero.hero.tornadoChance) {
            Tornado vortex = new Tornado((int) damage);
            
            vortex.numCycles = Hero.hero.vortexLvl * 5;
            vortex.tornadoIndex = 0;
            
            enemy.target = "vortex";
            
            GameWorld.gameWorld.addObject(vortex, (int) enemy.getExactX(), (int) enemy.getExactY());
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
                    shrapnelDamage = Hero.hero.shrapnelLvl == 1 ? 0.4 * damage : 0.8 * damage;
                    
                    Projectile shrapnel = new Projectile(dx, dy, shrapnelSpeed, shrapnelDamage, isCrit, true);
                    
                    GameWorld.gameWorld.addObject(shrapnel, (int)getExactX(), (int)getExactY());
                }
            }
        }
    }
    
    private void burst() {
        if (Hero.hero.burstLvl > 0 && Greenfoot.getRandomNumber(100) <= Hero.hero.burstChance) {
            Blast hydroBlast = new Blast(damage);
        }
    }
}
