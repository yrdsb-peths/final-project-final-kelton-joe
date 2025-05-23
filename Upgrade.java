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
        "critDamage",
        "regenInterval",
        "regenAmount"
    };
    
    private double[] value = {
        1.0,
        1.0,
        0.25,
        -25.0,
        5,
        1.0,
        2.0,
        -50.0,
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
    }
    
    protected void addedToWorld(World world) {
        num = Greenfoot.getRandomNumber(8);
        name = new Label(type[num] + "\nUP", 25);
        GameWorld.gameWorld.addObject(name, getX(), getY() - 20);
    }
    
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            Hero.hero.setStat(value[num], type[num]);
            upgradeManager.isSelected = true;
        }
    }
}
