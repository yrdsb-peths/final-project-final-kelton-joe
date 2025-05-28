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
    
    public static int numRerolls;
    
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
        
        Button confirmButton = new Button("Confirm");
        GameWorld.gameWorld.addObject(confirmButton, 500, 450);
        
        Button resetButton = new Button("Rerolls");
        GameWorld.gameWorld.addObject(resetButton, 300, 450);
        
        resetButton.image.drawString(resetButton.type + ": " + UpgradeManager.numRerolls, 50, 30);
        setImage(resetButton.image);
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
    
    public void rerollUpgrades() {
        if (numRerolls > 0) {
            numRerolls--;
            List<Upgrade> upgrades = new ArrayList<>(GameWorld.gameWorld.getObjects(Upgrade.class));
            
            for (Upgrade upgrade : upgrades) {
                upgrade.upgradeManager = null;
                GameWorld.gameWorld.removeObject(upgrade.name);
                GameWorld.gameWorld.removeObject(upgrade.theRarity);
                GameWorld.gameWorld.removeObject(upgrade);
            }
            
            GameWorld.gameWorld.removeUpgrades();
            
            if (GameWorld.gameWorld.wave % 5 == 0) GameWorld.gameWorld.upgradeManager = new UpgradeManager(2, GameWorld.gameWorld, true);
            else GameWorld.gameWorld.upgradeManager = new UpgradeManager(GameWorld.gameWorld.easyReward + GameWorld.gameWorld.waveDifficulty, GameWorld.gameWorld, false);
        }
    }
}
