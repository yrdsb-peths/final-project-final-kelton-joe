import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Settings class for the user to change controls
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class Settings extends World
{
    // buttons for changing keybinds
    public static Button changeForwards;
    public static Button changeBackwards;
    public static Button changeLeft;
    public static Button changeRight;
    public static Button changeAttack;
    public static Button changeDash;
    
    /**
     * Constructor for objects of class Settings.
     */
    public Settings()
    {    
        // 800x600 world
        super(800, 600, 1); 
        
        // sets background
        setBackground("background/background6.png");
        
        // return to title screen
        Button home = new Button("Title");
        addObject(home, 460, 500);
        
        // adds reset button
        Button reset = new Button("Reset");
        addObject(reset, 335, 500);
        
        // does not function
        Button settings = new Button("Settings");
        addObject(settings, 400, 75);
        
        //move forwards
        Button forwards = new Button("Big Frame");
        addObject(forwards, 175, 175);
        Label forwardText = new Label("Forwards", 30);
        addObject(forwardText, 175, 175);
        
        // change forwards
        changeForwards = new Button("Small Frame");
        changeForwards.displayText = new Label("w", 30);
        changeForwards.control = "forwards";
        addObject(changeForwards, 325, 175);
        addObject(changeForwards.displayText, 325, 175);
        
        //move backwards
        Button backwards = new Button("Big Frame");
        addObject(backwards, 175, 250);
        Label backwardText = new Label("Backwards", 30);
        addObject(backwardText, 175, 250);
        
        // change backwards
        changeBackwards = new Button("Small Frame");
        changeBackwards.displayText = new Label("s", 30);
        changeBackwards.control = "backwards";
        addObject(changeBackwards, 325, 250);
        addObject(changeBackwards.displayText, 325, 250);
        
        //move left
        Button left = new Button("Big Frame");
        addObject(left, 175, 325);
        Label leftText = new Label("Left", 30);
        addObject(leftText, 175, 325);
        
        // change left
        changeLeft = new Button("Small Frame");
        changeLeft.displayText = new Label("a", 30);
        changeLeft.control = "left";
        addObject(changeLeft, 325, 325);
        addObject(changeLeft.displayText, 325, 325);
        
        //move right
        Button right = new Button("Big Frame");
        addObject(right, 175, 400);
        Label rightText = new Label("Right", 30);
        addObject(rightText, 175, 400);
        
        // change right
        changeRight = new Button("Small Frame");
        changeRight.displayText = new Label("d", 30);
        changeRight.control = "right";
        addObject(changeRight, 325, 400);
        addObject(changeRight.displayText, 325, 400);
        
        //dash
        Button dash = new Button("Big Frame");
        addObject(dash, 500, 175);
        Label dashText = new Label("Dash", 30);
        addObject(dashText, 500, 175);
        
        // change dash
        changeDash = new Button("Small Frame");
        changeDash.displayText = new Label("e", 30);
        changeDash.control = "dash";
        addObject(changeDash, 650, 175);
        addObject(changeDash.displayText, 650, 175);
        
        //attack
        Button attack = new Button("Big Frame");
        addObject(attack, 500, 250);
        Label attackText = new Label("Attack", 30);
        addObject(attackText, 500, 250);
        
        // change attack
        changeAttack = new Button("Small Frame");
        changeAttack.displayText = new Label("space", 30);
        changeAttack.control = "attack";
        addObject(changeAttack, 650, 250);
        addObject(changeAttack.displayText, 650, 250);
        
        //other
        //Button other1 = new Button("Big Frame");
        //addObject(other1, 500, 325);
        
        // change other
        //Button changeOther1 = new Button("Small Frame");
        //addObject(changeOther1, 650, 325);
        
        // other
        //Button other2 = new Button("Big Frame");
        //addObject(other2, 500, 400);
        
        // change other 
        //Button changeOther2 = new Button("Small Frame");
        //addObject(changeOther2, 650, 400);
    }
}
