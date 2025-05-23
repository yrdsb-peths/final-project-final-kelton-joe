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
        "regenAmount",
        "fullHeal",
        "crit"
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
        1.0,
        100.0,
        1.0
    };
    
    private int num;
    
    // 0 = common, 1= uncommon, 2 = rare, 3 = epic, 4 = legendary, 5 = mythic
    private int rarity;
    
    public UpgradeManager upgradeManager;
    
    public Label name;
    public Label theRarity;
    
    public Upgrade(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
        
        // background for upgrades
        GreenfootImage rectangle = new GreenfootImage("rectangle.png");
        rectangle.scale(200, 200);
        setImage(rectangle);
        
        // converts string to arraylist
        type = new ArrayList<String>(Arrays.asList(typeString)); 
    }
    
    protected void addedToWorld(World world) {
        // randomly generate an upgrade
        num = Greenfoot.getRandomNumber(type.size());
        rarity = Greenfoot.getRandomNumber(5);
        
        // make label for generated upgrade
        switch (num) {
            case 0:
                name = new Label("Health Boost", 25);
                break;
            case 1:
                name = new Label("Attack Boost", 25);
                break;
            case 2:
                name = new Label("Speed Boost", 25);
                break;
            case 3:
                name = new Label("Attack Speed Boost", 25);
                break;
            case 4:
                name = new Label("Attack Range Boost", 25);
                break;
            case 5:
                name = new Label("Crit Rate Boost", 25);
                break;
            case 6:
                name = new Label("Crit Damage Boost", 25);
                break;
            case 7:
                name = new Label("Health Regen Cooldown \nBoost", 25);
                break;
            case 8:
                name = new Label("Health Regen Amount \nBoost", 25);
                break;
            case 9:
                name = new Label("Full Heal", 25);
                break;
            case 10:
                name = new Label("Full Crit Boost", 25);
                break;
        }
        
        // rarity label
        switch (rarity) {
            case 0:
                theRarity = new Label("Common", 30);
                break;
            case 1:
                theRarity = new Label("Uncommon", 30);
                break;
            case 2:
                theRarity = new Label("Rare", 30);
                break;
            case 3:
                theRarity = new Label("Epic", 30);
                break;
            case 4:
                theRarity = new Label("Legendary", 30);
                break;
            case 5:
                theRarity = new Label("Mythic", 30);
                break;
        }
        
        // adds the labels to the world
        GameWorld.gameWorld.addObject(name, getX(), getY() - 20);
        GameWorld.gameWorld.addObject(theRarity, getX(), getY() - 70);
    }
    
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            Hero.hero.setStat(value[num] * (rarity + 1), type.get(num));
            upgradeManager.isSelected = true;
        }
    }
}
