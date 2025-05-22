import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * End screen after player loses
 * 
 * @author Kelton and Joe
 * @version May 2025
 */
public class EndScreen extends World
{
    /**
     * Endscreen contructor
     */
    public EndScreen()
    {    
        super(800, 600, 1);
        
        Label gameOverText = new Label("Game Over", 80);
        addObject(gameOverText, 400, 300);
        
        Label resetText = new Label("press space to restart", 40);
        addObject(resetText, 400, 350);
    }
    
    public void act() {
        if (Greenfoot.isKeyDown("space")) {
            GameWorld gameWorld = new GameWorld();
            Greenfoot.setWorld(gameWorld);
        }
    }
}
