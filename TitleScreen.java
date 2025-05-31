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
        addObject(start, 300, 500);
        
        Button menu = new Button("Menu");
        addObject(menu, 400, 500);
        
        Button unique = new Button("Unique");
        addObject(unique, 500, 500);
    }
}
