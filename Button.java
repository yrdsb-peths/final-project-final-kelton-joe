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
    
    // for no hovering
    private boolean disableHover;
    
    // image for the button
    public GreenfootImage image;
    
    // things for setting screen
    public Label displayText;
    private boolean waitingForInput;
    private String key;
    public String control;
    
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
        /*
        else if (type.equals("Menu")) {
            image = new GreenfootImage("menu.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }*/
        else if (type.equals("Rerolls")) {
            image = new GreenfootImage("reset.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            isRemovable = true;
        }
        else if (type.equals("Reset")) {
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
        }/*
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
        }*/
        else if (type.equals("Continue")) {
            image = new GreenfootImage("start.png");
            scale = 0.47;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Setting")) {
            image = new GreenfootImage("setting.png");
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Settings")) {
            image = new GreenfootImage("setting.png");
            scale = 1.25;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            disableHover = true;
        }
        else if (type.equals("Big Frame")) {
            image = new GreenfootImage("bigFrame.png");
            scale = 0.5;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale * 0.75));
            disableHover = true;
        }
        else if (type.equals("Big Frame 2")) {
            image = new GreenfootImage("bigFrame.png");
            scale = 0.65;
            image.scale((int) (image.getWidth() * scale * 1.5), (int) (image.getHeight() * scale * 0.75));
            disableHover = true;
        }
        else if (type.equals("Small Frame")) {
            image = new GreenfootImage("smallFrame.png");
            scale = 0.35;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else if (type.equals("Small Frame 2")) {
            image = new GreenfootImage("smallFrame.png");
            scale = 0.3;
            image.scale((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
            disableHover = true;
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
        if ((isMouseOver() && !isHovered && !disableHover) || waitingForInput) {
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
        
        if (Greenfoot.mouseClicked(this) && !waitingForInput) {
            // confirm upgrades
            if (type.equals("Confirm")) UpgradeManager.isConfirmed = true;
            
            // reroll upgrades
            if (type.equals("Rerolls")) GameWorld.gameWorld.upgradeManager.rerollUpgrades();
            
            // home
            if (type.equals("Home")) {
                TitleScreen titleScreen = new TitleScreen();
                GameWorld.gameWorld = null;
                Greenfoot.setWorld(titleScreen);
            }
            // start button
            if (type.equals("Start")) {
                GameWorld gameWorld = new GameWorld();
                Greenfoot.setWorld(gameWorld);
            }
            // return to home screen
            if (type.equals("Home") || type.equals("Title")) {
                TitleScreen titleScreen = new TitleScreen();
                GameWorld.gameWorld = null;
                Greenfoot.setWorld(titleScreen);
            }
            // restart game
            if (type.equals("Restart")) {
                GameWorld gameWorld = new GameWorld();
                Greenfoot.setWorld(gameWorld);
            }
            // visit settings screen
            if (type.equals("Setting")) {
                Settings settings = new Settings();
                Greenfoot.setWorld(settings);
            }
            // reset settings
            if (type.equals("Reset")) {
                // resets hero default keybinds
                Hero.forward = "w";
                Hero.backward = "s";
                Hero.left = "a";
                Hero.right = "d";
                Hero.dash = "e";
                Hero.skill = "space";
                
                // changes display texts
                Settings.changeForwards.displayText.setValue("w");
                Settings.changeBackwards.displayText.setValue("s");
                Settings.changeLeft.displayText.setValue("a");
                Settings.changeRight.displayText.setValue("d");
                Settings.changeDash.displayText.setValue("e");
                Settings.changeAttack.displayText.setValue("space");
                Settings.changeForwards.setLocation(Settings.changeForwards.getX(), Settings.changeForwards.getY());
                Settings.changeBackwards.setLocation(Settings.changeBackwards.getX(), Settings.changeBackwards.getY());
                Settings.changeLeft.setLocation(Settings.changeLeft.getX(), Settings.changeLeft.getY());
                Settings.changeRight.setLocation(Settings.changeRight.getX(), Settings.changeRight.getY());
                Settings.changeDash.setLocation(Settings.changeDash.getX(), Settings.changeDash.getY());
                Settings.changeAttack.setLocation(Settings.changeAttack.getX(), Settings.changeAttack.getY());
            }
        }
        if (Greenfoot.mouseClicked(displayText) || Greenfoot.mouseClicked(this)) {
            // setting change key
            if (type.equals("Small Frame")) {
                key = null;
                displayText.setValue(" ");
                displayText.setLocation(getX(), getY());
                waitingForInput = true;
            }
        }
        // change keybinds
        if (waitingForInput) {
            key = Greenfoot.getKey();
            if (key != null) {
                key = key.toLowerCase();
                displayText.setValue(key);
                displayText.setLocation(getX(), getY());
                switch (control) {
                    case "forwards":
                        Hero.forward = key;
                        break;
                    case "backwards":
                        Hero.backward = key;
                        break;
                    case "left":
                        Hero.left = key;
                        break;
                    case "right":
                        Hero.right = key;
                        break;
                    case "attack":
                        Hero.skill = key;
                        break;
                    case "dash":
                        Hero.dash = key;
                        break;
                }
                waitingForInput = false;
            }
        }
    }
    
    /**
     * Method for checking if mouse is hovering over the button
     * 
     * @return whether it is hovering or not
     */
    public Boolean isMouseOver() {
        mouseX = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getX() : -1;
        mouseY = Greenfoot.getMouseInfo() != null ? Greenfoot.getMouseInfo().getY() : -1;
        
        return mouseX >= getX() - getImage().getWidth() / 2
                      && mouseX <= getX() + getImage().getWidth() / 2
                      && mouseY >= getY() - getImage().getHeight() / 2
                      && mouseY <= getY() + getImage().getHeight() / 2;
    }
}
