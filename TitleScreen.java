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
        
        // start
        Button start = new Button("Start");
        addObject(start, 325, 475);
        
        // menu
        //Button menu = new Button("Menu");
        //addObject(menu, 325, 475);
        
        // unique explanations
        //Button unique = new Button("Unique");
        //addObject(unique, 475, 475);
        
        // stat list
        //Button stat = new Button("Stat");
        //addObject(stat, 325, 540);
        
        Button setting = new Button("Setting");
        addObject(setting, 440, 475);
    }
}
