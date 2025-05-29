import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Visual Indicator for when Spectral Veil effect is active
 * 
 * @author Joe and Kelton
 * @version June 2025
 */
public class SpectralVeilActive extends Actor
{
    /**
     * Constructor for the indicator: ghost image
     */
    public SpectralVeilActive() {
        GreenfootImage image = new GreenfootImage("spectralVeilActive.png");
        image.scale(50, 50);
        setImage(image);
    }
}
