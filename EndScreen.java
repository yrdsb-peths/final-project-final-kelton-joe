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
        
        setBackground(new GreenfootImage("gameOver.jpg"));
        
        Label gameOverText = new Label("Game Over", 80);
        addObject(gameOverText, 400, 100);
        
        Label resetText = new Label("Press space to restart", 35);
        addObject(resetText, 400, 500);
    }
    
    public void act() {
        if (Greenfoot.isKeyDown("space")) {
            GameWorld gameWorld = new GameWorld();
            Greenfoot.setWorld(gameWorld);
        }
    }
}
