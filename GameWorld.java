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
            upgradeManager = new UpgradeManager(3, this);
            addObject(upgradeManager, 0, 0);
        }
    }
    
    public void startWave() {
        // full heal hero at the start of every wave
        Hero.hero.currentHp = Hero.hero.maxHp;
        
        // increase wave number every time it is called
        wave++;
        
        waveLabel.setValue("Wave " + wave);
        
        enemiesToSpawn = wave + Greenfoot.getRandomNumber(2);
        
        spawnTimer.mark();
    }
    
    public void removeUpgrades() {
        upgradeManager = null;
    }
}
