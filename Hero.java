import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Class for the playable character
 * 
 * @author Kelton and Joe
 * @version May 2025
 */
public class Hero extends Actor
{
    int attack;
    int health;
    int def;
    int speed = 3;
    
    double critRate = 5.0;
    double critDamage = 50.0;
    
    /**
     * Movement code for the hero
     */
    int x, y;
    
    public void act()
    {
        
        if (Greenfoot.isKeyDown("w")) {
            this.setLocation(getX(), getY() - speed);
        }
        if (Greenfoot.isKeyDown("s")) {
            this.setLocation(getX(), getY() + speed);
        }y++;
        if (Greenfoot.isKeyDown("a")) {
            this.setLocation(getX() - speed, getY());
        }
        if (Greenfoot.isKeyDown("d")) {
            this.setLocation(getX() + speed, getY());
        }
    }
}
