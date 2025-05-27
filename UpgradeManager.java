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
    
    public boolean isConfirmed;
    private boolean isUnique;
    
    Label confirmLabel = new Label("Confirm", 30);
    
    private ArrayList<Upgrade> selectedUpgrades = new ArrayList<>();
    
    public UpgradeManager(int upgrades, World world, boolean isUnique)
    {
        isConfirmed = false;
        this.isUnique = isUnique;
        
        double startIndex = -upgrades/2;
        double endIndex = upgrades/2;
        
        if (upgrades % 2 == 0) {
            startIndex += 0.5;
            endIndex -= 0.5;
        }
        
        for (double i = startIndex; i <= endIndex; i++) {
            Upgrade upgrade = new Upgrade(this, isUnique);
            world.addObject(upgrade, world.getWidth()/2 + (int)(upgradeSpacing*i), world.getHeight()/2);
        }
        
        Upgrade.numSelections = 1;
        
        ConfirmButton confirmButton = new ConfirmButton();
        GameWorld.gameWorld.addObject(confirmButton, 400, 450);
    }
    
    public void act() {
        if (isConfirmed) {
            for (Upgrade upgrade : selectedUpgrades) {
                if (upgrade.isUnique) Hero.hero.setStat(upgrade.uniqueTrait);
                else Hero.hero.setStat(upgrade.theValue * (upgrade.rarity + 1), upgrade.type.get(upgrade.num));
            }
            
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
    
    public void addSelectedUpgrade(Upgrade upgrade) {
        if (!selectedUpgrades.contains(upgrade)) {
            selectedUpgrades.add(upgrade);
        }
    }
    
    public void removeSelectedUpgrade(Upgrade upgrade) {
        selectedUpgrades.remove(upgrade);
    }
}
