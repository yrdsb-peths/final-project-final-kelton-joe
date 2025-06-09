import greenfoot.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Game World class
 * 
 * @author Kelton and Joe
 * @version May 2025
 */

public class GameWorld extends World {
    // static reference of itself (used by other classes)
    static GameWorld gameWorld;
    
    // enemies to spawn
    private int enemiesToSpawn;
    SimpleTimer spawnTimer = new SimpleTimer();
    private int spawnInterval;
    
    // bonus enemy stats
    private int bonusHp;
    private double bonusSpeed;
    private int bonusAttack;
    private int bonusAttackSpeed;
    private final int maxSpeedMultiplier = 5;
    
    // hero
    private Hero hero;
    private HeroArm heroArm;
    
    // wave
    public int wave;
    public int waveDifficulty;
    public int waveMultiplier;
    public final int easyReward = 2;
    private Label waveLabel;
    
    // game over
    static boolean gameOver;
    
    // hero hp bar
    public static Label healthBar;
    
    // upgrade manager
    public UpgradeManager upgradeManager;
    
    // waves per unique upgrade
    public static final int waveUnique = 5;
    
    // boss label timer and text
    Label bossText = new Label("Boss Wave", 90);
    Label boss;
    Label bossBarText;
    SimpleTimer labelTimer = new SimpleTimer();
    
    // max enemies that can be spawned at one time
    private final int maxEnemies = 8;
    
    /**
     * Constructor for the world
     */
    public GameWorld() {
        // size of the world is 800 by 600 pixels
        super(800, 600, 1);
        
        // init
        gameWorld = this;
        gameOver = false;
        heroArm = new HeroArm();
        
        // spawn actors
        hero = new Hero(heroArm);
        Hero.hero = hero;
        addObject(hero, 400, 300);
        addObject(heroArm, 0, 0);
        healthBar = new Label(Math.max(Hero.hero.currentHp, 0) + "/" + hero.maxHp + " hp", 40);
        addObject(healthBar, 710, 20);
        waveLabel = new Label("Wave 0", 50);
        addObject(waveLabel, 80, 20);
        
        // list of enemies
        Enemy.enemies = new ArrayList<Enemy>();
        
        // start original wave
        wave = 0;
        startWave();
        
        // give total rerolls
        UpgradeManager.numRerolls = 5;
        
        // manager upgrades (init ONLY once to prevent upgrades not removing)
        Upgrade.type = new ArrayList<String>(Arrays.asList(Upgrade.typeString)); 
        Upgrade.uniques = new ArrayList<String>(Arrays.asList(Upgrade.uniqueTraits));
        for (int i = 0; i < Upgrade.typeString.length; i++) {
            Upgrade.upgradeValues.put(Upgrade.typeString[i], Upgrade.value[i]);
        }
    }
    
    /**
     * Act method for game world
     * spawns new enemies every wave and generates bonus stats for them
     * also spawns upgrades when enemies are killed
     */
    public void act() {
        // sets background depending on wave
        if (wave % 20 == 0) setBackground("background/background7.png");
        else if (wave % 10 == 0) setBackground("background/background3.png");
        else setBackground("background/background6.png");
        
        // boss label
        if (labelTimer.millisElapsed() > 2000) {
            removeObject(bossText);
            removeObject(boss);
        }
        
        // spawn interval
        if (wave % 10 == 0) spawnInterval = 3000;
        else spawnInterval = 800 / waveMultiplier;
        
        // spawns enemies as long as there are more enemies to spawn
        if (enemiesToSpawn > 0 && wave > 0 && Enemy.enemies.size() < maxEnemies) {
            if (spawnTimer.millisElapsed() > spawnInterval) {
                // sets boss bar text
                if (wave % 20 == 0) bossBarText = new Label("Steelfin: Scourge of the Deep", 30);
                else if (wave % 10 == 0) bossBarText = new Label("Zarock: the All-Devouring", 30);
                
                if (wave % 10 == 0) {
                    // spawns the boss
                    spawnBoss();
                    addObject(bossBarText, 400, 20);
                } 
                
                else {
                    // randomly generate buffs based on wave number
                    int speedBound = Math.min(wave, maxSpeedMultiplier);
                    if (speedBound <= 0) speedBound = 1;  // fallback to 1
                    bonusSpeed = ((double) Greenfoot.getRandomNumber(speedBound)) / 10.0;
                    int hpBound = wave;
                    if (hpBound <= 0) hpBound = 1;
                    bonusHp = Greenfoot.getRandomNumber(hpBound);
                    int attackBound = (int) (wave * 0.5);
                    if (attackBound <= 0) attackBound = 1;
                    bonusAttack = Greenfoot.getRandomNumber(attackBound);
                    bonusAttackSpeed = Greenfoot.getRandomNumber(speedBound) * 10;
                    
                    // spawn enemy with buffs
                    Enemy enemy = new Enemy((wave + bonusHp) * waveMultiplier, bonusSpeed, bonusAttack * waveMultiplier, bonusAttackSpeed);
                    addObject(enemy, Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
                    
                    // reduces enemies to spawn and restarts timer
                    enemiesToSpawn--;
                    spawnTimer.mark();
                }
            }
        } 
        
        // generate upgrades if enemies are dead and hero is alive
        else if (Enemy.enemies.size() == 0 && upgradeManager == null) {
            if (!Hero.hero.isDead) {
                if (wave % waveUnique == 0) {
                    // additional reroll every 5 upgrades
                    UpgradeManager.numRerolls++;
                    
                    // spawn unique upgrade
                    upgradeManager = new UpgradeManager(2, this, true);
                } else {
                    // spawns 3 upgrades
                    upgradeManager = new UpgradeManager(easyReward + waveDifficulty, this, false);
                }
                
                // adds the upgrade manager to the world
                addObject(upgradeManager, 0, 0);
            }
        }
    }
    
    /**
     * Method for starting a new wave
     */
    public void startWave() {
        // increase wave number every time it is called
        wave++;
        
        // boss wave labels
        if (wave % 20 == 0) boss = new Label("Mechajaw", 50);
        else if (wave % 10 == 0) boss = new Label("The Wyrmroot", 50);
        
        // boss wave every 10 waves
        if (wave % 10 == 0) {
            labelTimer.mark();
            addObject(bossText, 400, 300);
            addObject(boss, 400, 375);
        }
        
        // more difficult scaling every 10 waves
        waveMultiplier = wave/10 + 1;
        
        // full heal hero
        hero.currentHp = hero.maxHp;
        GameWorld.healthBar.setValue(Math.max(Hero.hero.currentHp, 0) + "/" + hero.maxHp + " hp");
        
        // new wave label
        waveLabel.setValue("Wave " + wave);
        if (wave % 10 == 0) waveLabel.setValue(" ");
    
        // wave difficulty
        waveDifficulty = Greenfoot.getRandomNumber(3);
        
        // enemies to spawn
        enemiesToSpawn = (int) ((((wave * 0.5) + Greenfoot.getRandomNumber(2)) + 0.5) * waveMultiplier);
        
        // wave difficulty
        switch (waveDifficulty) {
            case 0:
                enemiesToSpawn = wave;
                bonusHp = 0;
                break;
            case 1:
                switch (Greenfoot.getRandomNumber(2)) {
                    case 0:
                        enemiesToSpawn++;
                        break;
                    case 1:
                        bonusHp++;
                        break;
                }
                break;
            case 2:
                switch (Greenfoot.getRandomNumber(2)) {
                    case 0:
                        enemiesToSpawn += 2;
                        break;
                    case 1:
                        bonusHp += 2;
                        break;
                }
                break;
        }
        
        // boss wave stuff
        if (wave % 10 == 0) enemiesToSpawn = 1;
        
        // marks spawn timer
        spawnTimer.mark();
    }
    
    /**
     * Method for spawning the boss
     */
    private void spawnBoss() {
        if (wave % 20 == 0) {
            // spawns the boss in the center of the world
            Shark shark = new Shark(200 * waveMultiplier, 2 * waveMultiplier);
            addObject(shark, 400, 300);
        }
        else {
            // adds the boss to the center of the world
            Wyrmroot wyrmroot = new Wyrmroot(150 * waveMultiplier, 7 * waveMultiplier);
            addObject(wyrmroot, 400, 300);
        }        
        
        // reduces enemies to spawn
        enemiesToSpawn--;
        spawnTimer.mark();
    }
    
    /**
     * Method for removing upgrades and upgrade manager
     */
    public void removeUpgrades() {
        removeObject(upgradeManager);
        upgradeManager = null;
    }
}
