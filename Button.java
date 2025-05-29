import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Button class
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class Button extends Actor
{
    // type of button (confirm/reroll)
    public String type;
    
    // image for the button
    public GreenfootImage image;
    
    /**
     * Constructor for a button
     * 
     * @param text: text to be displayed on the button
     */
    public Button(String text) {
        type = text;
        
        // gray background of 150 x 50
        image = new GreenfootImage(150, 50);
        image.setColor(Color.GRAY);
        image.fill();
        
        // black text starting at 50, 30
        image.setColor(Color.BLACK);
        image.drawString(text, 50, 30);
        
        // sets the image
        setImage(image);
    }
    
    /**
     * Act method for the Button class 
     * Confirms if pressed and button type is confirm
     * Rerolls upgrades if pressed and type is reroll
     */
    public void act()
    {
        // removes any button if there is no upgrade manager
        if (GameWorld.gameWorld.upgradeManager == null) GameWorld.gameWorld.removeObject(this);
        
        if (Greenfoot.mouseClicked(this) && type.equals("Confirm")) {
            // confirms upgrades
            UpgradeManager.isConfirmed = true;  
        }
        
        else if (Greenfoot.mouseClicked(this) && type.equals("Rerolls")) {
            // rerolls upgrades
            GameWorld.gameWorld.upgradeManager.rerollUpgrades();
        }
    }
}
