import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

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
    
    private int frostbiteLvl;
    private int scorchLvl;
    private int vampireLvl;
    private boolean addHealth;
    
    public Projectile(double nx, double ny, double speed, double damage, boolean isCrit,
                        int frostbiteLvl, int scorchLvl, int vampireLvl) {
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
        this.frostbiteLvl = frostbiteLvl;
        this.scorchLvl = scorchLvl;
        this.vampireLvl = vampireLvl;
        
        this. durability = 1;
        
        isRemoved = false;
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
        enemy.removeHp((int)damage, isCrit, Color.GRAY);
        frostbite(enemy);
        scorch(enemy);
        vampire();
        
        durability--;
        if (durability == 0) {
            GameWorld.gameWorld.removeObject(this);
            isRemoved = true;
        }
    }
    
    private void frostbite(Enemy enemy) {
        if (frostbiteLvl > 0) enemy.frostbite();
    }
    
    private void scorch(Enemy enemy) {
        if (scorchLvl > 0) enemy.scorch(damage * 0.5);
    }
    
    private void vampire() {
        if (vampireLvl == 1) {
            Hero.hero.currentHp = Math.min(Hero.hero.currentHp + 1, Hero.hero.maxHp);
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
        else if (vampireLvl == 2) {
            if (Greenfoot.getRandomNumber(5) == 1) addHealth = true;
            else addHealth = false;
            
            if (addHealth) {
                Hero.hero.maxHp++;
                Hero.hero.currentHp++;
            }
            else Hero.hero.currentHp = Math.min(Hero.hero.currentHp + 1, Hero.hero.maxHp);
            GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        }
    }
}
