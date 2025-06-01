import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * End screen after player dies
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
        // world size
        super(800, 600, 1);
        
        // sets background
        setBackground(new GreenfootImage("gameOver.jpg"));
        
        // game over label
        Label gameOverText = new Label("Game Over", 80);
        addObject(gameOverText, 400, 100);
        
        // restart button
        Button restart = new Button("Restart");
        addObject(restart, 360, 500);
        
        // home button
        Button home = new Button("Title");
        addObject(home, 440, 500);
    }
}
