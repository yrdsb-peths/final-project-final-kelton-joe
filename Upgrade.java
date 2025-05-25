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
    // all types of upgrades
    public static String[] typeString = {
        "health", 
        "attack", 
        "speed", 
        "attackSpeed", 
        "attackRange",
        "projectileSpeed",
        "critRate", 
        "critDamage",
        "regenInterval",
        "regenAmount",
        "crit"
    };
    public static ArrayList<String> type;
    
    // values of upgrades
    private double[] value = {
        1.0, // attack
        1.0, // hp
        0.05, // speed
        -15.0, // attack speed
        3, // attack range
        0.1, // projectile speed
        3.0, //crit rate
        6.0, // crit damage
        -50.0, // hp regen interval
        1.0, // hp regen amount
        100.0, // full heal
        2.0 // both crit buff
    };
    
    // randomly generated number
    private int num;
    
    // rarity and probability for each rarity
    private int rarity;
    private int[] probability = { 
            // rarity number: chance name multiplier
        45, // 0: 45% common 1x
        70, // 1: 25% uncommon 2x
        85, // 2: 15% rare 3x
        95, // 3: 10% epic 4x
        98, // 4: 3% legendary 5x 
        99 // 5: 2% mythic 10x
    };
    
    private String[] uniqueTraits = {
        "Frostbite", // slows enemies
                    // upgraded - freezes enemies on hit
        "Scorch", // burn damage to enemies overtime
                    // upgraded - increases burn damage
        "Sharpshot", // projectile speed maxed and pierce through enemies
                    // upgraded - gives Hero maxed range
        "Vampire" // Hero gets temporary hp from kills
                    //upgraded - hp from kills has a chance to be permanent
    };
    
    // upgrade manager
    public UpgradeManager upgradeManager;
    
    // labels for name and rarity
    public Label name;
    public Label theRarity;
    
    /**
     * Upgrade Constructor
     * 
     * @param upgradeManager: manages the upgrades
     */
    public Upgrade(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
        
        // background for upgrades
        GreenfootImage rectangle = new GreenfootImage("rectangle.png");
        rectangle.scale(150, 150);
        setImage(rectangle);
        
        // converts string to arraylist
        type = new ArrayList<String>(Arrays.asList(typeString)); 
    }
    
    /**
     * Adds upgrade to the world
     * 
     * @param world: world the upgrade is added to
     */
    protected void addedToWorld(World world) {
        // randomly generate an upgrade type
        num = Greenfoot.getRandomNumber(type.size());
        
        // rarity with probability
        rarity = Greenfoot.getRandomNumber(100);
        for (int i = 0; i < probability.length; i++) {
            if (rarity <= probability[i]) {
                rarity = i;
                break;
            }
        }
        
        // increase mythic multiplier
        if (rarity == 5) rarity = 9;
        
        // make label for generated upgrade
        switch (num) {
            case 0:
                name = new Label("Health Boost", 20);
                break;
            case 1:
                name = new Label("Attack Boost", 20);
                break;
            case 2:
                name = new Label("Speed Boost", 20);
                break;
            case 3:
                name = new Label("Attack Speed Boost", 20);
                break;
            case 4:
                name = new Label("Attack Range Boost", 20);
                break;
            case 5:
                name = new Label("Projectile Speed \nBoost", 20);
                break;
            case 6:
                name = new Label("Crit Rate Boost", 20);
                break;
            case 7:
                name = new Label("Crit Damage Boost", 20);
                break;
            case 8:
                name = new Label("Health Regen \nFrequency Boost", 20);
                break;
            case 9:
                name = new Label("Health Regen \nAmount Boost", 20);
                break;
            case 10:
                name = new Label("Full Crit Boost", 20);
                break;
        }
        
        // rarity label
        switch (rarity) {
            case 0:
                theRarity = new Label("Common", 25);
                getImage().setColor(new greenfoot.Color(169, 169, 169));
                break;
            case 1:
                theRarity = new Label("Uncommon", 25);
                getImage().setColor(new greenfoot.Color(50, 205, 50));
                break;
            case 2:
                theRarity = new Label("Rare", 25);
                getImage().setColor(new greenfoot.Color(0, 150, 255));
                break;
            case 3:
                theRarity = new Label("Epic", 25);
                getImage().setColor(new greenfoot.Color(191, 64, 191));
                break;
            case 4:
                theRarity = new Label("Legendary", 25);
                getImage().setColor(new greenfoot.Color(255, 191, 0));
                break;
            case 9:
                theRarity = new Label("Mythic", 25);
                getImage().setColor(new greenfoot.Color(255, 117, 24));
                break;
        }
        getImage().fill();
        
        // adds the labels to the world
        GameWorld.gameWorld.addObject(name, getX(), getY() - 10);
        GameWorld.gameWorld.addObject(theRarity, getX(), getY() - 50);
    }
    
    /**
     * Select upgrade if clicked
     */
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            Hero.hero.setStat(value[num] * (rarity + 1), type.get(num));
            upgradeManager.isSelected = true;
        }
    }
}
