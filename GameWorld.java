import greenfoot.*;
import java.util.ArrayList;

public class GameWorld extends World {
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
    private final int maxSpeedMultiplier = 8;
    
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
    Label boss1 = new Label("The Wyrmroot", 50);
    Label bossBarText = new Label("Zarock: the All-Devouring", 30);
    SimpleTimer labelTimer = new SimpleTimer();
    
    /**
     * Constructor for the world
     */
    public GameWorld() {
        // size of the world is 800 by 600 pixels
        super(800, 600, 1);
        
        // random background
        //setBackground("background/background" + Greenfoot.getRandomNumber(5) + ".png");
        //setBackground("background.png");
        
        // init
        gameWorld = this;
        gameOver = false;
        heroArm = new HeroArm();
        
        // spawn actors
        hero = new Hero(heroArm);
        addObject(hero, 400, 300);
        addObject(heroArm, 0, 0);
        healthBar = new Label(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp", 40);
        addObject(healthBar, 730, 20);
        waveLabel = new Label("Wave 0", 50);
        addObject(waveLabel, 80, 20);
        
        // list of enemies
        Enemy.enemies = new ArrayList<Enemy>();
        
        // start original wave
        wave = 0;
        startWave();
        
        // give total rerolls
        UpgradeManager.numRerolls = 5;
    }
    
    public void act() {
        if (labelTimer.millisElapsed() > 1500) {
            removeObject(bossText);
            removeObject(boss1);
        }
        // spawn interval
        if (wave % 10 == 0) spawnInterval = 3000;
        else spawnInterval = 1000 / waveMultiplier;
                    
        if (enemiesToSpawn > 0) {
            if (spawnTimer.millisElapsed() > spawnInterval) {
                if (wave % 10 == 0) {
                    // spawns the boss
                    spawnBoss();
                    addObject(bossBarText, 400, 20);
                } else {
                    // randomly generate buffs based on wave number
                    bonusSpeed = ((double) Greenfoot.getRandomNumber(Math.min(wave, maxSpeedMultiplier))) / 10.0;
                    bonusHp = Greenfoot.getRandomNumber(wave);
                    bonusAttack = Greenfoot.getRandomNumber((int)(wave / 2.5) + 1);
                    bonusAttackSpeed = Greenfoot.getRandomNumber(Math.min(wave, maxSpeedMultiplier)) * 10;
                    
                    // spawn enemy with buffs
                    Enemy enemy = new Enemy((wave + bonusHp) * waveMultiplier, bonusSpeed, bonusAttack * waveMultiplier, bonusAttackSpeed);
                    addObject(enemy, Greenfoot.getRandomNumber(800), Greenfoot.getRandomNumber(600));
                    
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
                
                addObject(upgradeManager, 0, 0);
            }
        }
    }
    
    public void startWave() {
        // increase wave number every time it is called
        wave++;
        
        if (wave % 10 == 0) {
            labelTimer.mark();
            addObject(bossText, 400, 300);
            addObject(boss1, 400, 375);
        }
        
        waveMultiplier = wave/10 + 1;
        
        Hero.hero.currentHp = Hero.hero.maxHp;
        GameWorld.healthBar.setValue(Hero.hero.currentHp + "/" + Hero.hero.maxHp + " hp");
        
        waveLabel.setValue("Wave " + wave);
        
        if (wave % 10 == 0) {
            enemiesToSpawn = 1;
            spawnTimer.mark();
            waveDifficulty = 2;
            return;
        }
        
        waveDifficulty = Greenfoot.getRandomNumber(3);
        
        enemiesToSpawn = (wave + Greenfoot.getRandomNumber(2)) * waveMultiplier;
        
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
    
    private void spawnBoss() {
        Wyrmroot wyrmroot = new Wyrmroot(300 * waveMultiplier, 4 * waveMultiplier);
        addObject(wyrmroot, 400, 300);
        
        enemiesToSpawn--;
        spawnTimer.mark();
    }
    
    public void removeUpgrades() {
        removeObject(upgradeManager);
        upgradeManager = null;
    }
}
