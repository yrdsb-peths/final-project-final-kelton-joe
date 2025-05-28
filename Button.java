import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Font;

/**
 * Write a description of class ConfirmButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Button extends Actor
{
    public String type;
    public GreenfootImage image;
    
    public Button(String text) {
        type = text;
        
        image = new GreenfootImage(150, 50);
        image.setColor(Color.GRAY);
        image.fill();
        
        image.setColor(Color.BLACK);
        image.drawString(text, 50, 30);
        
        setImage(image);
    }
    
    public void act()
    {
        if (GameWorld.gameWorld.upgradeManager == null) GameWorld.gameWorld.removeObject(this);
        
        if (Greenfoot.mouseClicked(this) && type.equals("Confirm")) {
            UpgradeManager upgradeManager = (UpgradeManager) getWorld().getObjects(UpgradeManager.class).get(0);
            upgradeManager.isConfirmed = true;  
            
            GameWorld.gameWorld.removeObject(this);
        }
        else if (Greenfoot.mouseClicked(this) && type.equals("Rerolls")) {
            GameWorld.gameWorld.upgradeManager.rerollUpgrades();
            
            image.clear();
            image.setColor(Color.GRAY);
            image.fill();
            image.setColor(Color.BLACK);
            image.drawString(type + ": " + UpgradeManager.numRerolls, 50, 30);
            setImage(image);
            
            GameWorld.gameWorld.removeObject(this);
        }
    }
}
