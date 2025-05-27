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
        "health", // 0
        "attack", // 1
        "speed", // 2
        "attackSpeed", // 3 
        "attackRange", // 4
        "projectileSpeed", // 5
        "critRate", // 6
        "critDamage", // 7
        "regenInterval", // 8
        "regenAmount", // 9
        "crit", // 10
        "dashLength", // 11
        "dashMultiplier", // 12
        "projectile", // 13
        "dashCooldown" // 14
    };
    public static ArrayList<String> type;
    
    // values of upgrades
    private double[] value = {
        1.0, // hp
        1.0, // attack
        0.05, // speed
        -50.0, // attack speed
        10, // attack range
        0.1, // projectile speed
        3.0, //crit rate
        6.0, // crit damage
        -50.0, // hp regen interval
        1.0, // hp regen amount
        2.0, // both crit buff
        50.0, // dash length
        0.05, // dash mult
        5.0, // projectile
        50.0 // dash cooldown
    };
    
    private double theValue;
    
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
    
    private boolean isUnique;
    private String[] uniqueTraits = {
        "Frostbite",     // slows enemies
                         // upgraded: freezes enemies on hit (they can still attack)
        "Scorch",        // burn damage to enemies overtime
                         // upgraded: increases burn damage
        // "Sharpshot",     // projectile speed maxed and pierce through enemies
                         // upgraded: gives Hero maxed range
        "Vampire",       // Hero heals hp on hit
                         // upgraded: chance to increase max hp on hit
        //"Explosive Shot",// chance to shoot explosive arrows
                         // upgraded: guaranteed explosive arrows in larger area
        "Rogue",         // much increased speed and crit, but low range and hp
                         // upgraded: chance to fully dodge attacks, attack speed buff
        "Jester",        // chance to teleport enemies to random location on hit
                         // upgraded: higher teleport chance + stun (unable to move and attack) on teleport
        //"Stormcaller",   // chance to chain enemies on hit 
                         // upgraded: also chance to stun
        //"Thornsoul",     // chance to rebound damage
                         // upgraded: damage reduction + more rebound damage
        //"Spectral Veil", // chance to become immune to damage for a short time when hit
                         // upgraded: increases chance and duration
        //"Arcane Echo",   // attacks have a chance to repeat a second time but are weaker
                         // upgraded: full strength repeat + higher chance
        //"Blightroot",    // apply poison on hit
                         // upgraded: poison damage spreads and increases the longer it is active
        //"Soulforge",     // damage and speed buff on kill
                         // upgraded: longer duration, more max stacks, chance to get on attack
        //"Iron Will",     // hero takes reduced crowd control buffs
                         // upgraded: immune to crowd control + damage resistance
        //"Windborne"      // arrows have a chance to ricochet
                         // upgraded: also targets nearby enemies
    };
    private String uniqueTrait;
    
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
    public Upgrade(UpgradeManager upgradeManager, boolean isUnique) {
        this.upgradeManager = upgradeManager;
        this.isUnique = isUnique;
        
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
        if (isUnique) {
            uniqueUpgrade();
            return;
        }
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
                theValue = value[0];
                break;
            case 1:
                name = new Label("Attack Boost", 20);
                theValue = value[1];
                break;
            case 2:
                name = new Label("Speed Boost", 20);
                theValue = value[2];
                break;
            case 3:
                name = new Label("Attack Speed Boost", 20);
                theValue = value[3];
                break;
            case 4:
                name = new Label("Attack Range Boost", 20);
                theValue = value[4];
                break;
            case 5:
                name = new Label("Projectile Speed \nBoost", 20);
                theValue = value[5];
                break;
            case 6:
                name = new Label("Crit Rate Boost", 20);
                theValue = value[6];
                break;
            case 7:
                name = new Label("Crit Damage Boost", 20);
                theValue = value[7];
                break;
            case 8:
                name = new Label("Health Regen \nFrequency Boost", 20);
                theValue = value[8];
                break;
            case 9:
                name = new Label("Health Regen \nAmount Boost", 20);
                theValue = value[9];
                break;
            case 10:
                name = new Label("Full Crit Boost", 20);
                theValue = value[10];
                break;
            case 11:
                name = new Label("Dash Length Boost", 20);
                theValue = value[11];
                break;
            case 12:
                name = new Label("Dash Speed \nMultiplier Boost", 20);
                theValue = value[12];
                break;
            case 13:
                name = new Label("Weapon Mastery", 20);
                theValue = value[13];
                break;
            case 14:
                name = new Label("Dash Cooldown \nReduction", 20);
                theValue = value[14];
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
    
    private void uniqueUpgrade() {
        int trait = Greenfoot.getRandomNumber(uniqueTraits.length);
        
        uniqueTrait = uniqueTraits[trait];
        name = new Label(uniqueTraits[trait], 20);
        theRarity = new Label("Unique", 25);
        
        getImage().setColor(new greenfoot.Color(215, 0, 64));
        getImage().fill();
        
        GameWorld.gameWorld.addObject(name, getX(), getY() - 10);
        GameWorld.gameWorld.addObject(theRarity, getX(), getY() - 50);
    }
    
    /**
     * Select upgrade if clicked
     */
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            if (isUnique) {
                Hero.hero.setStat(uniqueTrait);
                upgradeManager.isSelected = true;
            } else {
                Hero.hero.setStat(theValue * (rarity + 1), type.get(num));
                upgradeManager.isSelected = true;
            }
        }
    }
}
