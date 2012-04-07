package org.sini;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;

/**
 * Display.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Display extends Frame {
    
    /**
     * The title of the display.
     */
    private static final String DISPLAY_TITLE = "jDCPU-16";
    
    /**
     * The amount of pixels in the application per 
     */
    private final static int PIXEL_RATIO = 25;
    
    /**
     * The amount of pixels in the displays width.
     */
    private final static int DISPLAY_WIDTH = 32;
    
    /**
     * The amount of pixels in the displays height.
     */
    private final static int DISPLAY_HEIGHT = 16;
    
    /**
     * The current {@link Cpu} that this {@link Display} is servicing.
     */
    private Cpu cpu;
    
    /** 
     * The width of the display.
     */
    private int displayWidth;
    
    /** 
     * The height of the display.
     */
    private int displayHeight;
    
    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    @Override
    public void paint(Graphics graphics) {
        Font titleFont = new Font("Ariel", Font.BOLD, 14);
        graphics.translate(3, 24);
        for(int x = 0; x < DISPLAY_WIDTH; x++) {
            for(int y = 0; y < DISPLAY_HEIGHT; y++) {
                int m = cpu.m[Cpu.VIDEO_RAM + x + (y * DISPLAY_HEIGHT)].v;
                graphics.setColor(new Color(m, false));
                graphics.fillRect(x * PIXEL_RATIO, y * PIXEL_RATIO, PIXEL_RATIO, PIXEL_RATIO);
            }
        }
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, displayHeight - 60, displayWidth, 1);
        graphics.setFont(titleFont);
        graphics.drawString(DISPLAY_TITLE, displayWidth/2 - graphics.getFontMetrics().stringWidth(DISPLAY_TITLE), displayHeight - 60 + graphics.getFontMetrics().getHeight());   
    }
    
    /**
     * Constructs a new {@link Display};
     * @param cpu The {@link Cpu} to create the display for.
     */
    public Display(Cpu cpu) {
        super("jDCPU Display");
        displayWidth = DISPLAY_WIDTH * PIXEL_RATIO;
        displayHeight = DISPLAY_HEIGHT * PIXEL_RATIO + 60;
        setSize(displayWidth, displayHeight);
        setBackground(Color.BLACK);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - displayWidth/2, 
                    screenSize.height/2 - displayHeight/2);
        setResizable(false);
        this.cpu = cpu;
        setVisible(true);
    }  
}
