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
    private double scale;
    
    private int mouseX, mouseY;
    private boolean isHovered;
    
    // image for the button
    public GreenfootImage image;
    
    /**
     * Constructor for a button
     * 
     * @param text: text to be displayed on the button
     */
    public Button(String text) {
        type = text;
        if (type.equals("Start")) {
            image = new GreenfootImage("start.png");
            scale = 0.47;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            setImage(image);
        }
        else if (type.equals("Menu")) {
            image = new GreenfootImage("menu.png");
            scale = 0.75;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            setImage(image);
        }
        else if (type.equals("Rerolls")) {
            image = new GreenfootImage("reset.png");
            scale = 0.75;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            setImage(image);
        }
        else if (type.equals("Confirm")) {
            image = new GreenfootImage("confirm.png");
            scale = 0.75;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            setImage(image);
        }
        else if (type.equals("Home")) {
            image = new GreenfootImage("home.png");
            scale = 0.75;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            setImage(image);
        }
        else if (type.equals("Restart")) {
            image = new GreenfootImage("restart.png");
            scale = 0.75;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            setImage(image);
        }
        else if (type.equals("Unique")) {
            image = new GreenfootImage("uniques.png");
            scale = 0.75;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            setImage(image);
        }
    }
    
    /**
     * Act method for the Button class 
     * Confirms if pressed and button type is confirm
     * Rerolls upgrades if pressed and type is reroll
     */
    public void act()
    {
        // checks mouse hovering for lighter background
        if (isMouseOver() && !isHovered) {
            getImage().setTransparency(200);
            isHovered = true;
        }
        // otherwise don't make lighter background
        else if (!isMouseOver() && isHovered) {
            getImage().setTransparency(255);
            isHovered = false;
        }
        
        // confirm or reroll
        if (type.equals("Confirm") || type.equals("Rerolls")) {
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
        // start button
        else if (type.equals("Start") && Greenfoot.mouseClicked(this)) {
            GameWorld gameWorld = new GameWorld();
            Greenfoot.setWorld(gameWorld);
        }
        else if (type.equals("Home") && Greenfoot.mouseClicked(this)) {
            TitleScreen titleScreen = new TitleScreen();
            GameWorld.gameWorld = null;
            Greenfoot.setWorld(titleScreen);
        }
        else if (type.equals("Restart") && Greenfoot.mouseClicked(this)) {
            GameWorld gameWorld = new GameWorld();
            Greenfoot.setWorld(gameWorld);
        }
    }
    
    public Boolean isMouseOver() {
        mouseX = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getX() : -1;
        mouseY = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getY() : -1;
        
        return mouseX >= getX() - getImage().getWidth() / 2
                      && mouseX <= getX() + getImage().getWidth() / 2
                      && mouseY >= getY() - getImage().getHeight() / 2
                      && mouseY <= getY() + getImage().getHeight() / 2;
    }
}
