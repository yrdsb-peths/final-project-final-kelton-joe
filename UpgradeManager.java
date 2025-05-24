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
    private final int upgradeSpacing = 200;
    
    public boolean isSelected;
    
    public UpgradeManager(int upgrades, World world)
    {
        isSelected = false;
        
        double startIndex = -upgrades/2;
        double endIndex = upgrades/2;
        
        if (upgrades % 2 == 0) {
            startIndex += 0.5;
            endIndex -= 0.5;
        }
        
        for (double i = startIndex; i <= endIndex; i++) {
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
                GameWorld.gameWorld.removeObject(upgrade.theRarity);
                GameWorld.gameWorld.removeObject(upgrade);
            }
            
            GameWorld.gameWorld.removeUpgrades();
            
            GameWorld.gameWorld.startWave();
            
            GameWorld.gameWorld.removeObject(this);
            
        }
    }
}
