import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.ArrayList;

/**
 * Write a description of class UpgradeManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class UpgradeManager extends Actor
{
    private final int upgradeSpacing = 150;
    
    public boolean isSelected;
    
    public UpgradeManager(int upgrades, World world)
    {
        isSelected = false;
        
        for (double i = -upgrades/2; i <= upgrades/2; i++) {
            Upgrade upgrade = new Upgrade(this);
            world.addObject(upgrade, world.getWidth()/2 + (int)(upgradeSpacing*i), world.getHeight()/2);
        }
    }
    
    public void act() {
        if (isSelected) {
            List<Upgrade> upgrades = new ArrayList<>(GameWorld.gameWorld.getObjects(Upgrade.class));
            for (Upgrade upgrade : upgrades) {
                upgrade.upgradeManager = null;
                GameWorld.gameWorld.removeObject(upgrade.name);
                GameWorld.gameWorld.removeObject(upgrade);
            }
            
            GameWorld.gameWorld.removeUpgrades();
            
            GameWorld.gameWorld.startWave();
            
            GameWorld.gameWorld.removeObject(this);
            
        }
    }
}
