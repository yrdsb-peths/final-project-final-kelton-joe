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
    private HeroArm heroArm;
    
    public int wave;
    private final int maxSpeedMultiplier = 8;
    
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
        
        heroArm = new HeroArm();
        
        hero = new Hero(heroArm);
        addObject(hero, 400, 300);
        
        addObject(heroArm, 0, 0);
        
        healthBar = new Label(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp", 40);
        addObject(healthBar, 730, 20);
        
        waveLabel = new Label("Wave 0", 50);
        addObject(waveLabel, 80, 20);
        
        Enemy.enemies = new ArrayList<Enemy>();
        
        wave = 0;
        startWave();
    }
    
    public void act() {
        if (enemiesToSpawn > 0) {
            if (spawnTimer.millisElapsed() > spawnInterval) {
                // randomly generate buffs based on wave number
                bonusSpeed = ((double) Greenfoot.getRandomNumber(Math.min(wave, maxSpeedMultiplier))) / 10.0;
                bonusHp = Greenfoot.getRandomNumber(wave);
                bonusAttack = Greenfoot.getRandomNumber((int)(wave / 2.5) + 1);
                bonusAttackSpeed = Greenfoot.getRandomNumber(Math.min(wave, maxSpeedMultiplier)) * 10;
                
                // spawn enemy with buffs
                Enemy enemy = new Enemy(wave + bonusHp, bonusSpeed, bonusAttack, bonusAttackSpeed);
                addObject(enemy, Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
                
                enemiesToSpawn--;
                spawnTimer.mark();
            }
        } 
        else if (Enemy.enemies.size() == 0 && upgradeManager == null) {
            if (!Hero.hero.isDead) {
                if (wave % 1 == 0) {
                    upgradeManager = new UpgradeManager(2, this, true);
                } else {
                    // spawns 3 upgrades
                    upgradeManager = new UpgradeManager(easyReward + waveDifficulty, this, false);
                }
                
                addObject(upgradeManager, 0, 0);
            }
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
