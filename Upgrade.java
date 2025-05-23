import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for hero upgrades
 * 
 * @author Joe and Kelton
 * @version May 2025
 */
public class Upgrade extends Actor
{
    public static String[] typeString = {
        "health", 
        "attack", 
        "speed", 
        "attackSpeed", 
        "attackRange",
        "critRate", 
        "critDamage",
        "regenInterval",
        "regenAmount"
    };
    
    public static ArrayList<String> type;
    
    private double[] value = {
        1.0,
        1.0,
        0.25,
        -25.0,
        5,
        3.0,
        6.0,
        -100.0,
        1.0
    };
    
    private int num;
    
    public UpgradeManager upgradeManager;
    
    public Label name;
    
    public Upgrade(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
        
        GreenfootImage rectangle = new GreenfootImage("rectangle.png");
        rectangle.scale(120, 140);
        setImage(rectangle);
        
        type = new ArrayList<String>(Arrays.asList(typeString)); 
    }
    
    protected void addedToWorld(World world) {
        num = Greenfoot.getRandomNumber(type.size());
        name = new Label(type.get(num) + "\nUP", 25);
        GameWorld.gameWorld.addObject(name, getX(), getY() - 20);
    }
    
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            Hero.hero.setStat(value[num], type.get(num));
            upgradeManager.isSelected = true;
        }
    }
}
