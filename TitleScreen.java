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
        
        Label gameName = new Label("Placeholder Game", 80);
        addObject(gameName, 400, 100);
        
        Label start = new Label("Press space to start the game", 40);
        addObject(start, 400, 200);
    }
    
    public void act() {
        if (Greenfoot.isKeyDown("space")) {
            MyWorld gameWorld = new MyWorld();
            Greenfoot.setWorld(gameWorld);
        }
    }
}
