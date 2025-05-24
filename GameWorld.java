import greenfoot.*;
import java.util.ArrayList;

public class GameWorld extends World {
    static GameWorld gameWorld;
    
    SimpleTimer spawnTimer = new SimpleTimer();
    
    private int enemiesToSpawn;
    
    private int bonusHp;
    private double bonusSpeed;
    private int bonusAttack;
    private int bonusAttackSpeed;
    
    private final int spawnInterval = 1000;
    
    private Hero hero;
    
    public int wave;
    private int waveMultiplier;
    private final int maxWaveMultiplier = 8;
    
    private int waveDifficulty;
    
    private final int easyReward = 2;
    
    private Label waveLabel;
    
    static boolean gameOver;
    
    public static Label healthBar;
    
    private UpgradeManager upgradeManager;
    
    /**
     * Constructor for the world
     */
    public GameWorld() {
        // size of the world is 800 by 600 pixels
        super(800, 600, 1);
        
        gameWorld = this;
        
        gameOver = false;
        
        hero = new Hero();
        addObject(hero, 400, 300);
        
        healthBar = new Label(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp", 40);
        addObject(healthBar, 740, 20);
        
        waveLabel = new Label("Wave 0", 50);
        addObject(waveLabel, 80, 20);
        
        Enemy.enemies = new ArrayList<Enemy>();
        
        wave = 0;
        startWave();
    }
    
    public void act() {
        if (enemiesToSpawn > 0) {
            if (spawnTimer.millisElapsed() > spawnInterval) {
                // sets wave multiplier
                waveMultiplier = Math.min(wave, maxWaveMultiplier);
                
                // randomly generate buffs based on wave multiplier
                bonusSpeed = ((double) Greenfoot.getRandomNumber(waveMultiplier)) / 10.0;
                bonusHp = Greenfoot.getRandomNumber(waveMultiplier);
                bonusAttack = Greenfoot.getRandomNumber(waveMultiplier);
                bonusAttackSpeed = Greenfoot.getRandomNumber(waveMultiplier) * 10;
                
                // spawn enemy with buffs
                Enemy enemy = new Enemy(wave + bonusHp, bonusSpeed, bonusAttack, bonusAttackSpeed);
                addObject(enemy, Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
                
                enemiesToSpawn--;
                spawnTimer.mark();
            }
        } 
        else if (Enemy.enemies.size() == 0 && upgradeManager == null) {
            // spawns 3 upgrades
            upgradeManager = new UpgradeManager(easyReward + waveDifficulty, this);
            addObject(upgradeManager, 0, 0);
        }
    }
    
    public void startWave() {
        // increase wave number every time it is called
        wave++;
        
        Hero.hero.currentHp = Hero.hero.maxHp;
        GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        
        waveLabel.setValue("Wave " + wave);
        
        enemiesToSpawn = wave + Greenfoot.getRandomNumber(2);
        
        waveDifficulty = Greenfoot.getRandomNumber(3);
        
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
        spawnTimer.mark();
    }
    
    public void removeUpgrades() {
        upgradeManager = null;
    }
}
