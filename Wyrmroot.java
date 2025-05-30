import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;

/**
 * Write a description of class Boss here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Wyrmroot extends Enemy
{
    private double attackSpeed = 2000;
    
    public Wyrmroot(int hitpoints, int attack) {
        super(hitpoints, 0, attack, 2000);
        attackCooldown.mark();
    }
    
    protected void addedToWorld(World world) {
        double scale = 0.1;
        
        redBar = new RedBar(scale);
        world.addObject(super.redBar, getX(), getY());
        
        greenBar = new GreenBar(scale);
        world.addObject(greenBar, getX(), getY());
    }
    
    @Override
    public void jester() {
        if (Greenfoot.getRandomNumber(2) > 0 && Hero.hero.jesterLvl > 1) {
            isStunned = true;
            removeHp(Greenfoot.getRandomNumber(((int) (Hero.hero.attack / 3)) + 1), false, Color.MAGENTA, 30);
            stunTimer.mark();
        }
    }
    
    private void blueBite() {
        
    }
    
    private void purpleBite() {
        
    }
}
