import greenfoot.*;
import java.util.ArrayList;

public class GameWorld extends World {
    static GameWorld gameWorld;
    
    SimpleTimer spawnTimer = new SimpleTimer();
    
    private int enemiesToSpawn;
    private int bonusHp;
    
    private final int spawnInterval = 500;
    
    private Hero hero;
    
    public int wave;
    
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
        addObject(healthBar, 750, 20);
        
        waveLabel = new Label("Wave 0", 50);
        addObject(waveLabel, 70, 20);
        
        Enemy.enemies = new ArrayList<Enemy>();
        
        wave = 0;
        startWave();
    }
    
    public void act() {
        if (enemiesToSpawn > 0) {
            if (spawnTimer.millisElapsed() > spawnInterval) {
                Enemy enemy = new Enemy(wave + bonusHp);
                addObject(enemy, Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
                
                enemiesToSpawn--;
                spawnTimer.mark();
            }
        } 
        else if (Enemy.enemies.size() == 0 && upgradeManager == null) {
            upgradeManager = new UpgradeManager(5, this);
            addObject(upgradeManager, 0, 0);
        }
    }
    
    public void startWave() {
        wave++;
        
        waveLabel.setValue("Wave " + wave);
        
        enemiesToSpawn = wave;
        bonusHp = 0;
        
        switch (Greenfoot.getRandomNumber(3)) {
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
