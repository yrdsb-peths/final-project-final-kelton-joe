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
    public static double[] value = {
        13.0, // hp 
        6.0, // attack
        0.07, // speed
        -25.0, // attack speed
        15, // attack range
        0.15, // projectile speed
        3.0, //crit rate
        6.0, // crit damage
        -400.0, // hp regen interval
        10.0, // hp regen amount
        2.0, // both crit buff
        40.0, // dash length
        0.35, // dash mult
        5.0, // projectile
        70.0 // dash cooldown
    };
    
    // how much to buff a stat by
    public double theValue;
    
    // randomly generated number
    public int num;
    
    // rarity and probability for each rarity
    public int rarity;
    private int[] probability = { 
            // rarity number: chance name multiplier (0 to 99)
        34, // 0: 45% common 1x
        69, // 1: 25% uncommon 2x
        84, // 2: 15% rare 3x
        94, // 3: 10% epic 4x
        98, // 4: 4% legendary 5x 
        99 // 5: 1% mythic 10x
    };
    
    // unique upgrades
    public boolean isUnique;
    public static String[] uniqueTraits = {
        "Frostbite",            // slows enemies (B) (A)
                                // upgraded: freezes enemies on hit (they can still attack) (S) (S)
        "Scorch",               // burn damage to enemies overtime (A) (S)
                                // upgraded: increases burn damage (S) (SS)
        "Sharpshot",            // pierce through enemies, max projectile speed, more crit chance (S) (S)
                                // upgraded: max range, more piercing (B) (B)
        "Vampire",              // Hero heals hp on hit (A with BP, D without) (SS with BP, D without)
                                // upgraded: chance to increase max hp on hit (SS) (SS)
        "Rogue",                // much increased speed and crit, but low range and hp (B) (S)
                                // upgraded: chance to fully dodge attacks, attack speed buff (SS) (SS)
        "Jester",               // chance to teleport enemies to random location on hit (D) (D)
                                // upgraded: higher teleport chance + stun (unable to move and attack) on teleport (B) (C)
        "Spectral Veil",        // chance to become immune to damage for a short time when hit (B) (D)
        "Arcane Echo",          // attacks have a chance to repeat a second time but are weaker (A with others, D itself) (C with others, D itself)
                                // upgraded: higher chance + stronger repeat damage (SS) (B)
        "Violent Vortex",       // grants vortex skill on hit: pull nearby enemies and slows them (A) (A)
                                // upgraded: vortex also deals damage and becomes bigger (S) (S)
        "Blood Pact",           // converts arrows to "blood pact" attack, consuming health and dealing additonal crit damage (A) (S)
        "Shrapnel Shot",        // after projectiles hit enemies, chance to fire shrapnels with lower speed, damage, and range (S) (A)
                                // upgraded: shrapnels have max speed and range, and can additionally pierce (SS) (SS)
        "Hydro Burst",          // projectiles become blasts (A) (A)
                                // upgraded: weakens enemies by 30% and deals double damage to enemies under 30% hp (S) (S)
        "Thunderstrike \nVolley"  // fire a volley of arrows with a chance to spawn that deal partial damage (B) (A)
                                // upgraded: more arrows that can pierce slightly and have higher stun chance (SS) (SS)
    };
    public String uniqueTrait;
    public static ArrayList<String> uniques;
    
    // upgrade values
    public static java.util.HashMap<String, Double> upgradeValues = new java.util.HashMap<>();
    
    // upgrade manager
    public UpgradeManager upgradeManager;
    
    // labels for name and rarity
    public Label name;
    public Label theRarity;
    
    // mouse location and hovering stuff
    private boolean isHovered;
    private int mouseX;
    private int mouseY;
    
    // selection stuff
    private boolean isSelected;
    public static int numSelections = 1;
    
    /**
     * Upgrade Constructor
     * 
     * @param upgradeManager: manages the upgrades
     * @param isUnique: whether the upgrade is unique
     */
    public Upgrade(UpgradeManager upgradeManager, boolean isUnique) {
        // sets instance variable values
        this.upgradeManager = upgradeManager;
        this.isUnique = isUnique;
        
        // background for upgrades
        GreenfootImage rectangle = new GreenfootImage("rectangle.png");
        rectangle.scale(150, 150);
        setImage(rectangle);
    }
    
    /**
     * Adds upgrade to the world
     * 
     * @param world: world to add the upgrade to
     */
    protected void addedToWorld(World world) {
        if (isUnique) {
            uniqueUpgrade();
            return;
        }
        // randomly generate an upgrade type
        num = Greenfoot.getRandomNumber(type.size());
        String upgradeType = type.get(num);
        theValue = upgradeValues.get(upgradeType);
        
        // rarity with probability
        rarity = Greenfoot.getRandomNumber(100);
        for (int i = 0; i < probability.length; i++) {
            if (rarity <= probability[i]) {
                rarity = i;
                break;
            }
        }
        
        // increase mythic multiplier to (9+1) = 10x
        if (rarity == 5) rarity = 9;
        
        // make name label for generated upgrade
        switch (upgradeType) {
            case "health":
                name = new Label("Health Boost", 20);
                theValue = value[0];
                break;
            case "attack":
                name = new Label("Attack Boost", 20);
                theValue = value[1];
                break;
            case "speed":
                name = new Label("Speed Boost", 20);
                theValue = value[2];
                break;
            case "attackSpeed":
                name = new Label("Attack Speed Boost", 20);
                theValue = value[3];
                break;
            case "attackRange":
                name = new Label("Attack Range Boost", 20);
                theValue = value[4];
                break;
            case "projectileSpeed":
                name = new Label("Projectile Speed \nBoost", 20);
                theValue = value[5];
                break;
            case "critRate":
                name = new Label("Crit Rate Boost", 20);
                theValue = value[6];
                break;
            case "critDamage":
                name = new Label("Crit Damage Boost", 20);
                theValue = value[7];
                break;
            case "regenInterval":
                name = new Label("Health Regen \nFrequency Boost", 20);
                theValue = value[8];
                break;
            case "regenAmount":
                name = new Label("Health Regen \nAmount Boost", 20);
                theValue = value[9];
                break;
            case "crit":
                name = new Label("Full Crit Boost", 20);
                theValue = value[10];
                break;
            case "dashLength":
                name = new Label("Dash Length Boost", 20);
                theValue = value[11];
                break;
            case "dashMultiplier":
                name = new Label("Dash Speed \nMultiplier Boost", 20);
                theValue = value[12];
                break;
            case "projectile":
                name = new Label("Weapon Mastery", 20);
                theValue = value[13];
                break;
            case "dashCooldown":
                name = new Label("Dash Cooldown \nReduction", 20);
                theValue = value[14];
                break;
        }
        
        // make rarity label
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
     * Method for unique upgrades
     */
    private void uniqueUpgrade() {
        // randomly generate an unique upgrade
        int trait = Greenfoot.getRandomNumber(uniques.size());
        uniqueTrait = uniques.get(trait);
        
        // name and rarity label
        name = new Label(uniques.get(trait), 20);
        theRarity = new Label("Unique", 25);
        
        // background color
        getImage().setColor(new greenfoot.Color(215, 0, 64));
        getImage().fill();
        
        // adds labels to world
        getWorld().addObject(name, getX(), getY() - 10);
        getWorld().addObject(theRarity, getX(), getY() - 50);
    }
    
    /**
     * Checks if the mouse is hovering over the upgrade
     * 
     * @return is hovering or not
     */
    public Boolean isMouseOver() {
        mouseX = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getX() : -1;
        mouseY = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getY() : -1;
        
        return mouseX >= getX() - getImage().getWidth() / 2
                      && mouseX <= getX() + getImage().getWidth() / 2
                      && mouseY >= getY() - getImage().getHeight() / 2
                      && mouseY <= getY() + getImage().getHeight() / 2;
    }
    
    /**
     * Act method
     * 
     * Selects upgrade if clicked 
     * Removes upgrade from selected upgrades if it was already selected
     */
    public void act() {
        // checks mouse hovering for lighter background
        if (isMouseOver() && !isHovered) {
            getImage().setTransparency(128);
            isHovered = true;
        }
        // otherwise don't make lighter background
        else if (!isMouseOver() && isHovered) {
            getImage().setTransparency(255);
            isHovered = false;
        }
        
        // if the rectangle or labels are clicked add/remove the upgrade from list of selected upgrades
        if (Greenfoot.mouseClicked(this) || Greenfoot.mouseClicked(name) || Greenfoot.mouseClicked(theRarity)) {
            if (!isSelected && numSelections > 0) {
                isSelected = true;
                numSelections--;
                upgradeManager.addSelectedUpgrade(this);
            }
            else if (isSelected) {
                isSelected = false;
                numSelections++;
                upgradeManager.removeSelectedUpgrade(this);
            }
        }
        
        // make lighter background if selected
        if (isSelected) getImage().setTransparency(128);
    }
}
