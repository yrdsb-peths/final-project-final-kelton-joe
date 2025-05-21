import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Class for the playable character
 * 
 * @author Kelton and Joe
 * @version May 2025
 */
public class Hero extends SmoothMover
{
    int attack;
    int health;
    int def;
    double speed = 2.0;
    
    double critRate = 5.0;
    double critDamage = 50.0;
    
    /**
     * Movement code for the hero
     */
    int x, y;
    
    
    public Hero() {
        setImage("images/bee.png");
        GreenfootImage hero = getImage();
        hero.scale(25, 25);
    }
    
    public void act()
    {
        
        if (Greenfoot.isKeyDown("w")) {
            this.setLocation(getExactX(), getExactY() - speed);
        }
        if (Greenfoot.isKeyDown("s")) {
            this.setLocation(getExactX(), getExactY() + speed);
        }y++;
        if (Greenfoot.isKeyDown("a")) {
            this.setLocation(getExactX() - speed, getExactY());
        }
        if (Greenfoot.isKeyDown("d")) {
            this.setLocation(getExactX() + speed, getExactY());
        }
    }
}
