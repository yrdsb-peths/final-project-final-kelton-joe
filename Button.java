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
    
    // mouse location
    private int mouseX, mouseY;
    private boolean isHovered;
    
    // buttons to be removed after upgrades
    private boolean isRemovable;
    
    // image for the button
    public GreenfootImage image;
    
    /**
     * Constructor for a button
     * 
     * @param text: text to be displayed on the button
     */
    public Button(String text) {
        // change type of button
        type = text;
        
        // default scale (size) of button
        scale = 0.75;
        
        if (type.equals("Start")) {
            image = new GreenfootImage("start.png");
            scale = 0.47;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Menu")) {
            image = new GreenfootImage("menu.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Rerolls")) {
            image = new GreenfootImage("reset.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            isRemovable = true;
        }
        else if (type.equals("Confirm")) {
            image = new GreenfootImage("confirm.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            isRemovable = true;
        }
        else if (type.equals("Home")) {
            image = new GreenfootImage("home.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            isRemovable = true;
        }
        else if (type.equals("Title")) {
            image = new GreenfootImage("home.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Restart")) {
            image = new GreenfootImage("restart.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Unique")) {
            image = new GreenfootImage("uniques.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Pause")) {
            image = new GreenfootImage("pause.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Stat")) {
            image = new GreenfootImage("stat.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Continue")) {
            image = new GreenfootImage("start.png");
            scale = 0.47;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Setting")) {
            image = new GreenfootImage("setting.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        
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
        
        // removes buttons if there is no upgrade manager (not in upgrade phase anymore)
        if (GameWorld.gameWorld != null && GameWorld.gameWorld.upgradeManager == null && isRemovable) GameWorld.gameWorld.removeObject(this);
        
        if (Greenfoot.mouseClicked(this)) {
            // confirm upgrades
            if (type.equals("Confirm")) UpgradeManager.isConfirmed = true;
            
            // reroll upgrades
            else if (type.equals("Rerolls")) GameWorld.gameWorld.upgradeManager.rerollUpgrades();
            
            // home
            else if (type.equals("Home")) {
                TitleScreen titleScreen = new TitleScreen();
                GameWorld.gameWorld = null;
                Greenfoot.setWorld(titleScreen);
            }
            // start button
            else if (type.equals("Start")) {
                GameWorld gameWorld = new GameWorld();
                Greenfoot.setWorld(gameWorld);
            }
            // return to home screen
            else if (type.equals("Home") || type.equals("Title")) {
                TitleScreen titleScreen = new TitleScreen();
                GameWorld.gameWorld = null;
                Greenfoot.setWorld(titleScreen);
            }
            // restart game
            else if (type.equals("Restart")) {
                GameWorld gameWorld = new GameWorld();
                Greenfoot.setWorld(gameWorld);
            }
        }
    }
    
    // checks if mouse is hovering
    public Boolean isMouseOver() {
        mouseX = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getX() : -1;
        mouseY = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getY() : -1;
        
        return mouseX >= getX() - getImage().getWidth() / 2
                      && mouseX <= getX() + getImage().getWidth() / 2
                      && mouseY >= getY() - getImage().getHeight() / 2
                      && mouseY <= getY() + getImage().getHeight() / 2;
    }
}
