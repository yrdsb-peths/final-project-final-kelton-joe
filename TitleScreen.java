import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Default world before starting the game
 * 
 * @author Joe and Kelton
 * @version May 2025
 */
public class TitleScreen extends World
{

    /**
     * TitleScreen Constructor
     */
    public TitleScreen()
    {   
        // 800x600 world
        super(800, 600, 1);
        
        // sets background
        setBackground(new GreenfootImage("titleScreen.jpg"));
        
        // name label
        Label gameName = new Label("Project Nameless", 70);
        addObject(gameName, 400, 100);
        
        // start button
        Button start = new Button("Start");
        addObject(start, 325, 475);
        
        // settings button
        Button setting = new Button("Setting");
        addObject(setting, 440, 475);
    }
}
