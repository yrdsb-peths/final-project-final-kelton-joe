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
        super(800, 600, 1);
        
        setBackground(new GreenfootImage("titleScreen.jpg"));
        
        Label gameName = new Label("Project Nameless", 70);
        addObject(gameName, 400, 100);
        
        Button start = new Button("Start");
        addObject(start, 400, 475);
        
        Button menu = new Button("Menu");
        addObject(menu, 325, 475);
        
        Button unique = new Button("Unique");
        addObject(unique, 475, 475);
        
        Button stat = new Button("Stat");
        addObject(stat, 325, 540);
        
        Button setting = new Button("Setting");
        addObject(setting, 440, 540);
    }
}
