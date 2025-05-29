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
    
    public static boolean isConfirmed;
    private boolean isUnique;
    
    public static int numRerolls;
    
    Label confirmLabel = new Label("Confirm", 30);
    
    private ArrayList<Upgrade> selectedUpgrades = new ArrayList<>();
    
    private Button confirmButton;
    private Button resetButton;
    
    UpgradeManager rerolledManager;
    
    /**
     * Constructor for upgradeManager
     * 
     * @param upgrades: number of upgrades to be generated
     * @param world: world to be put in
     * @param isUnique: generate unique upgrade or not
     */
    public UpgradeManager(int upgrades, World world, boolean isUnique)
    {
        // sets confirmed to false
        isConfirmed = false;
        
        // sets unique value
        this.isUnique = isUnique;
        
        // generates new upgrades
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
        
        
        // gives user 1 selection
        Upgrade.numSelections = 1;
        
        // adds a confirm button
        confirmButton = new Button("Confirm");
        GameWorld.gameWorld.addObject(confirmButton, 500, 450);
        
        // adds a reroll button
        resetButton = new Button("Rerolls");
        GameWorld.gameWorld.addObject(resetButton, 300, 450);
        
        // changes value of reroll button to currently remaining rerolls
        resetButton.image.drawString(resetButton.type + ": " + UpgradeManager.numRerolls, 50, 30);
        resetButton.setImage(resetButton.image);
    }
    
    /**
     * Act method for Upgrade Manager
     * removes all upgrades and starts the next wave when confirmed
     */
    public void act() {
        if (isConfirmed) {
            // gives stats for every upgrade in selected upgrades
            for (Upgrade upgrade : selectedUpgrades) {
                if (upgrade.isUnique) Hero.hero.setStat(upgrade.uniqueTrait);
                else Hero.hero.setStat(upgrade.theValue * (upgrade.rarity + 1), upgrade.type.get(upgrade.num));
            }
            
            // list of upgrades to remove
            List<Upgrade> upgrades = new ArrayList<>(GameWorld.gameWorld.getObjects(Upgrade.class));
            
            // removes all upgrades
            for (Upgrade upgrade : upgrades) {
                upgrade.upgradeManager = null;
                GameWorld.gameWorld.removeObject(upgrade.name);
                GameWorld.gameWorld.removeObject(upgrade.theRarity);
                GameWorld.gameWorld.removeObject(upgrade);
            }
            
            // removes upgrade manager in the world
            GameWorld.gameWorld.removeUpgrades();
            
            // removes itself and starts the next wave
            GameWorld.gameWorld.removeObject(this);
            GameWorld.gameWorld.startWave();
        }
    }
    
    /**
     * Function to add an upgrade into selected upgrades if it doesn't already contain it
     * 
     * @param upgrade: upgrade to be added
     */
    public void addSelectedUpgrade(Upgrade upgrade) {
        if (!selectedUpgrades.contains(upgrade)) {
            selectedUpgrades.add(upgrade);
        }
    }
    
    /**
     * Function to remove an upgrade from selected upgrades
     * 
     * @param upgrade: upgrade to be removed
     */
    public void removeSelectedUpgrade(Upgrade upgrade) {
        selectedUpgrades.remove(upgrade);
    }
    
    /**
     * Function to reroll all upgrades
     */
    public void rerollUpgrades() {
        // only applicable if the player still has rerolls and if currently in upgrade selection time
        if (numRerolls > 0 && GameWorld.gameWorld.upgradeManager != null) {
            // reduce number of remaining rerolls by 1
            numRerolls--;
            
            // list of current upgrades to be removed
            List<Upgrade> upgrades = new ArrayList<>(GameWorld.gameWorld.getObjects(Upgrade.class));
            
            // removes all current upgrades
            for (Upgrade upgrade : upgrades) {
                upgrade.upgradeManager = null;
                GameWorld.gameWorld.removeObject(upgrade.name);
                GameWorld.gameWorld.removeObject(upgrade.theRarity);
                GameWorld.gameWorld.removeObject(upgrade);
            }
            
            // removes current upgrade manager
            GameWorld.gameWorld.removeUpgrades();
            
            // same upgrade generation found in gameWorld
            if (GameWorld.gameWorld.wave % 5 == 0) {
                rerolledManager = new UpgradeManager(2, GameWorld.gameWorld, true);
            }
            else {
                rerolledManager = new UpgradeManager(GameWorld.gameWorld.easyReward + GameWorld.gameWorld.waveDifficulty, GameWorld.gameWorld, false);
            }
            
            // sets new upgrade manager for game world and adds it to the world
            GameWorld.gameWorld.upgradeManager = rerolledManager;
            GameWorld.gameWorld.addObject(rerolledManager, 0, 0);
        }
    }
}
