import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Class for hero upgrades
 * 
 * @author Joe and Kelton
 * @version May 2025
 */
public class Upgrade extends Actor
{
    private String[] type = {
        "health", 
        "attack", 
        "speed", 
        "attackSpeed", 
        "attackRange",
        "critRate", 
        "critDamage"
    };
    
    private double[] value = {
        1.0,
        1.0,
        0.25,
        -25.0,
        5,
        1.0,
        2.0
    };
    
    public void act()
    {
         int num = Greenfoot.getRandomNumber(6);
         Hero.hero.setStat(value[num], type[num]);
    }
}
