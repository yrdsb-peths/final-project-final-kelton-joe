import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Class for the shark boss
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class Shark extends Enemy
{
    // scale size for the boss
    private final int scale = 90;
    private final int mouthOffset = 50;
    
    // boss phase
    private int phase;
    
    // images used for animations
    GreenfootImage[] attackLeft = new GreenfootImage[6];
    GreenfootImage[] attackRight = new GreenfootImage[6];
    GreenfootImage[] death = new GreenfootImage[5];
    GreenfootImage[] enragedLeft = new GreenfootImage[6];
    GreenfootImage[] enragedRight = new GreenfootImage[6];
    GreenfootImage[] swimLeft = new GreenfootImage[6];
    GreenfootImage[] swimRight = new GreenfootImage[6];        
    GreenfootImage[] phaseChange = new GreenfootImage[2];
    
    // animation indexes
    private int attackIndex;
    private int deathIndex;
    private int swimIndex;
    private int phaseIndex;
    
    // animation timers
    private SimpleTimer attackTimer = new SimpleTimer();
    private SimpleTimer deathTimer = new SimpleTimer();
    private SimpleTimer swimTimer = new SimpleTimer();
    private SimpleTimer phaseTimer = new SimpleTimer();
    
    // facing direction
    private boolean facingRight;
    
    // boss states
    private boolean isDead;
    private boolean isEnraged;
    private boolean isAttacking;
    private double resMult;
    
    // location
    private double magnitude;
    private double nx, ny;
    private int heading;
    
    // attack cooldown
    private SimpleTimer attackCooldown = new SimpleTimer();
    private final double attackSpeed = 1000;
    
    // self destruct timer
    private SimpleTimer enrageTimer = new SimpleTimer();
    
    // boss hp bar
    Label healthBar;
    Actor bossBarFrame = new Actor() {
        {
            GreenfootImage image = new GreenfootImage("bossbarframe.png");
            image.scale(500, 80);
            setImage(image);
        }
    };
    
    /**
     * Constructor for the Shark boss
     * 
     * @param hp: hp for the boss
     * @param attack: damage dealt by the boss
     */
    public Shark(int hp, int attack) {
        // creates new enemy (this)
        super(hp, -0.15, attack, 0);
        
        // sets images
        for (int i = 0; i < 6; i++) {
            // right attack animation (p1)
            attackRight[i] = new GreenfootImage("shark/attack/attack" + i + ".png");
            attackRight[i].scale(scale + 20, scale);
            
            // left attack animation (p1)
            attackLeft[i] = new GreenfootImage("shark/attack/attack" + i + ".png");
            attackLeft[i].mirrorHorizontally();
            attackLeft[i].scale(scale + 20, scale);
            
            // right attack animation (p2)
            enragedRight[i] = new GreenfootImage("shark/enraged/attack" + i + ".png");
            enragedRight[i].scale(scale + 30, scale);
            
            // left attack animation (p2)
            enragedLeft[i] = new GreenfootImage("shark/enraged/attack" + i + ".png");
            enragedLeft[i].mirrorHorizontally();
            enragedLeft[i].scale(scale + 30, scale);
            
            // swim right animation
            swimRight[i] = new GreenfootImage("shark/swim/swim" + i + ".png");
            swimRight[i].scale(scale + 30, scale + 10);
            
            // swim left animation
            swimLeft[i] = new GreenfootImage("shark/swim/swim" + i + ".png");
            swimLeft[i].mirrorHorizontally();
            swimLeft[i].scale(scale + 30, scale + 10);
        }
        
        for (int i = 0; i < 5; i++) {
            // death animation
            death[i] = new GreenfootImage("shark/death/death" + i + ".png");
            death[i].scale(scale, scale);
        }
        
        for (int i = 0; i < 2; i++) {
            // phase change animation
            phaseChange[i] = new GreenfootImage("shark/phaseChange/phase" + i + ".png");
            phaseChange[i].scale(scale, scale);
        }
        
        // sets default image
        setImage(swimRight[0]);
        facingRight = true;
        
        // sets instance variables
        phase = 0;
        resMult = 0.7;
    }
    
    /**
     * Main behavior loop for the shark boss.
     * 
     * Handles movement, attacking, animations, status effects,
     * phase transitions, and death.
     */
    public void act()
    {
        if (!isDead) {
            // reduced resistance while attacking
            if (isAttacking && phase == 0) resMult = 2.0;
            
            // phase increasing
            if (hitpoints <= 0 && phase < 2) animatePhase();
            
            // tornado targeting
            if (!isSlowed) target = "hero";
            else if (Hero.hero.vortexLvl > 0) target = "vortex";
            
            // hero targeting
            if (!target.equals("vortex")) {
                // calculate distance from hero
                dx = Hero.hero.getExactX() - getExactX();
                dy = Hero.hero.getExactY() - getExactY();
                
                // facing right if the hero is more to the right
                facingRight = dx >= 0;
                
                // animate swim
                if (!isAttacking) animateSwim();
            }
            
            // scorch
            if (scorchTimer.millisElapsed() >= 1000) {
                if (burnTicks > 0) {
                    // burn sound effects
                    burnSounds[burnSoundIndex].play();
                    burnSoundIndex = (burnSoundIndex + 1) % burnSounds.length;
                    
                    // deal burn damage and reduce timer
                    removeHp((int)burnDamage, false, Color.RED, 25);
                    burnTicks--;
                    scorchTimer.mark();
                    
                    if (hitpoints <= 0) return;
                }
            }
            
            // remove status effects when timer ends
            if (frostbiteTimer.millisElapsed() >= slowDuration) isSlowed = false;
            if (frostbiteFreezeTimer.millisElapsed() >= 750) isFrozen = false;
            if (stunTimer.millisElapsed() >= stunDuration) isStunned = false;
            if (weakenTimer.millisElapsed() > weakenDuration) isWeakened = false;
            
            // movement calculation
            magnitude = Math.sqrt(dx * dx + dy * dy);
            nx = dx / magnitude;
            ny = dy / magnitude;
            
            // rotation
            heading = (int) Math.toDegrees(Math.atan2(dy, dx));
            
            // fix when mirrored
            if (dx < 0) heading -= 180;
            
            // set rotation
            this.setRotation(heading);
            
            // status effect movement calculation
            if (isFrozen || isStunned) setLocation(getExactX(), getExactY());
            else if (isSlowed) setLocation(getExactX() + (nx * speed/3), getExactY() + (ny * speed/3));
            else if (isEnraged) setLocation(getExactX() + (nx * 1.5), getExactY() + (ny * 1.5));
            else setLocation(getExactX() + (nx * speed), getExactY() + (ny * speed));
            
            // attack
            if (attackCooldown.millisElapsed() >= attackSpeed && (magnitude < 90 || isAttacking)) animateAttack(this.phase);
            
            // enraged self damage
            if (isEnraged && enrageTimer.millisElapsed() > 200 && this.hitpoints > (this.maxHitpoints * 0.3)) {
                this.hitpoints = Math.max((int) (this.hitpoints - (this.maxHitpoints * 0.0003)), (int) (this.maxHitpoints * 0.3));
                enrageTimer.mark();
            }
            
            // update health bar
            changeBar();
            
            // tells itself that it died
            if (hitpoints <= 0 && phase == 1) {
                // reset rotation on death
                setRotation(0);
                
                // remove health bars when dead
                GameWorld.gameWorld.removeObject(redBar);
                GameWorld.gameWorld.removeObject(greenBar);
                redBar = null;
                greenBar = null;
                
                // removes boss bar text
                GameWorld.gameWorld.removeObject(bossBarFrame);
                GameWorld.gameWorld.removeObject(GameWorld.gameWorld.bossBarText);
                
                GameWorld.gameWorld.removeObject(healthBar);
                
                // boss is now dead
                isDead = true;
            }
        }
        if (isDead) animateDeath();
    }
    
    /**
     * Applies frostbite effects to the shark if the hero has the frostbite ability.
     * Slows or freezes the shark based on the hero's frostbite level.
     */
    @Override
    public void frostbite() {
        if (Hero.hero.frostbiteLvl > 0) {
            isSlowed = true;
            slowDuration = 800;
            frostbiteTimer.mark();
        }   
        if (Hero.hero.frostbiteLvl > 1) {
            isFrozen = true;
            frostbiteFreezeTimer.mark();
            
            freezeSounds[freezeSoundIndex].play();
            freezeSoundIndex = (freezeSoundIndex + 1) % freezeSounds.length;
        }
    }
    
    /**
     * Updates the shark's health bar value on the screen.
     */
    @Override
    public void changeBar() {
        if (redBar != null && greenBar != null) {
            redBar.setPos(400, 40);
            greenBar.setPos(400, 40, (double) hitpoints / (double) maxHitpoints);
            healthBar.setValue(Math.max(this.hitpoints, 0) + "/" + this.maxHitpoints);
        }
    }
    
    /**
     * Initializes and adds the shark's health bar and boss bar UI elements to the world.
     *
     * @param world: The world the shark was added to.
     */
    @Override
    protected void addedToWorld(World world) {
        world.addObject(bossBarFrame, 400, 60);
        
        redBar = new RedBar(0.0, 0.0, true);
        world.addObject(redBar, 400, 40);
        
        greenBar = new GreenBar(0.0, 0.0, true);
        world.addObject(greenBar, 400, 40);
        
        healthBar = new Label(this.hitpoints + "/" + this.maxHitpoints + " hp", 30);
        world.addObject(healthBar, 400, 60);
    }
    
    /**
     * Applies a stun effect and damage to the shark based on the hero's jester level.
     * Teleport is disabled
     */
    @Override
    public void jester() {
        if (Greenfoot.getRandomNumber(2) > 0 && Hero.hero.jesterLvl > 1) {
            stun(600);
            removeHp((int) (Hero.hero.attack * 1.5), false, Color.MAGENTA, 30);
        }
    }
    
    /** 
     * Removes health from the shark and shows a damage indicator
     * 
     * @param damage: damage taken
     * @param isCrit: whether the hit is a crit
     * @param color: color of damage indicator
     * @param size: size of damage indicator
     */
    @Override
    public void removeHp(int damage, boolean isCrit, Color color, int size) {
        damage = (int) Math.max(damage * resMult, 0);
        hitpoints -= damage;
        
        if (isCrit) {
            color = Color.ORANGE;
            size = 35;
        }
        
        DamageIndicator dmgIndicator = new DamageIndicator((int) damage, size, color);
        GameWorld.gameWorld.addObject(dmgIndicator, (int) getExactX(), (int) getExactY());
    }
    
    /**
     * Animates and executes boss attacks
     * 
     * @param phase: phase of the boss
     */
    private void animateAttack(int phase) {
        // breaks out of the loop if the previous animation is not finished
        if (attackTimer.millisElapsed() < 300 && phase == 0) return;
        else if (attackTimer.millisElapsed() < 150 && phase == 1) return;
        
        // marks animation timer
        attackTimer.mark();
        
        // continues the attack animation if it is not finished
        if (attackIndex < attackRight.length) {
            isAttacking = true;
            
            switch (phase) {
                // phase 0 bite attack
                case 0:
                    if (facingRight) setImage(attackRight[attackIndex]);
                    else setImage(attackLeft[attackIndex]);
                    
                    if (attackIndex == 4 && magnitude <= 90) attack();
                    break;
                
                // phase 1 explosion attack
                case 1:
                    if (facingRight) setImage(enragedRight[attackIndex]);
                    else setImage(enragedLeft[attackIndex]);
                    
                    if (attackIndex == 5) spawnImplosion();
                    break;
            }
            
            // increases the animation index
            attackIndex++;
        }
        // reset the index when finished animation
        else {
            this.resMult = phase == 0 ? 0.7 : 1.4;
            attackIndex = 0;
            isAttacking = false;
            attackCooldown.mark();
        }
    }
    
    /**
     * Method for animating the death of the boss
     */
    private void animateDeath() {
        // breaks out of the loop if the previous animation is not finished
        if (deathTimer.millisElapsed() < 250) return;
        
        // marks animation timer
        deathTimer.mark();
        
        // continues animation if not finished
        if (deathIndex < death.length) {
            setImage(death[deathIndex]);
            deathIndex++;
        }
        
        // removes itself from the world once animation is finished
        else {
            GameWorld.gameWorld.removeObject(this);
            enemies.remove(this);
        }
    }
    
    /** 
     * Method for animating the shark's swimming
     */
    private void animateSwim() {
        // breaks out of the loop if the previous animation is not finished
        if (swimTimer.millisElapsed() < 150) return;
        
        // marks animation timer
        swimTimer.mark();
        
        // continues animation if not finished
        if (swimIndex < swimLeft.length) {
            // checks facing direction and adjusts accordingly
            if (facingRight) setImage(swimRight[swimIndex]);
            else setImage(swimLeft[swimIndex]);
            
            // increases animation index
            swimIndex++;
        }
        // resets animation index once it is finished
        else {
            swimIndex = 0;
        }
    }
    
    /**
     * Animates phase switching
     */
    private void animatePhase() {
        // breaks out of the loop if the previous animation is not finished
        if (phaseTimer.millisElapsed() < 200) return;
        
        // marks animation timer
        phaseTimer.mark();
        
        // animates phase change
        if (phaseIndex < phaseChange.length) {
            setImage(phaseChange[phaseIndex]);
            phaseIndex++;
        }
        
        // applies phase changes
        else {
            // increases phase and changes stats
            phaseIndex++;
            phase++;
            isEnraged = true;
            enrageTimer.mark();
            this.resMult = 1.4;
            
            // resets health
            hitpoints = maxHitpoints;
        }
    }
    
    /**
     * Spawns an implosion from the second phase of the shark
     */
    private void spawnImplosion() {
        // calculates location
        double angleRad = Math.toRadians(getRotation());
        if (!facingRight) angleRad += Math.PI;
        int spawnX = (int) (getExactX() + Math.cos(angleRad) * mouthOffset);
        int spawnY = (int) (getExactY() + Math.sin(angleRad) * mouthOffset);
        
        // adds to world
        Implosion implosion = new Implosion((int) (this.attack * 0.3), isDodged);
        GameWorld.gameWorld.addObject(implosion, spawnX, spawnY);
        
        // deals self damage
        this.hitpoints = Math.max((int) (hitpoints - (maxHitpoints * 0.002 * (Greenfoot.getRandomNumber(3) + 1))), 0);
    }
}
