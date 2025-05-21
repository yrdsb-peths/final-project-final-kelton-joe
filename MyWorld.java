import greenfoot.*;

public class MyWorld extends World {
    
    /**
     * Constructor for the world
     */
    public MyWorld() {
        // size of the world is 800 by 600 pixels
        super(800, 600, 1);
        
        Hero hero = new Hero();
        addObject(hero, 400, 300);
        
        Enemy enemy = new Enemy(hero);
        addObject(enemy, 100, 100);
    }
}
