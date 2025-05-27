import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Font;

/**
 * Write a description of class ConfirmButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ConfirmButton extends Actor
{
    public ConfirmButton() {
        GreenfootImage image = new GreenfootImage(150, 50);
        image.setColor(Color.GRAY);
        image.fill();
        
        image.setColor(Color.BLACK);
        image.drawString("Confirm", 50, 30);
        
        setImage(image);
    }
    
    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            UpgradeManager upgradeManager = (UpgradeManager) getWorld().getObjects(UpgradeManager.class).get(0);
            upgradeManager.isConfirmed = true;  
            GameWorld.gameWorld.removeObject(this);
        }
    }
}
