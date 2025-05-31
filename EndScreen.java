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
        
        Button restart = new Button("Restart");
        addObject(restart, 360, 500);
        
        Button home = new Button("Home");
        addObject(home, 440, 500);
    }
}
