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
     * The ratio of pixels for the width to the amount of pixels on the display.
     */
    private final static int WIDTH_RATIO = 14;
    
    /**
     * The ratio of pixels for the height to the amount of pixels on the display.
     */
    private final static int HEIGHT_RATIO = 25;   
    
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
        Font consoleFont = new Font("MonteCarlo", Font.PLAIN, 18);
        graphics.translate(3, 24);
        for(int x = 0; x < DISPLAY_WIDTH; x++) {
            for(int y = 0; y < DISPLAY_HEIGHT; y++) {
                int value = cpu.m[Cpu.VIDEO_RAM + x + (y * DISPLAY_HEIGHT)].v;
                int bc = value >> 12 & 0xF;
                graphics.setColor(new Color((bc & 0x4) != 0 ? 255 : 0, 
                                            (bc & 0x2) != 0 ? 255 : 0, 
                                            (bc & 0x1) != 0 ? 255 : 0));
                graphics.fillRect(x * WIDTH_RATIO, y * HEIGHT_RATIO, WIDTH_RATIO, HEIGHT_RATIO);
                int fc = value >>> 8 & 0xF;
                String c = "" + (char) (value & 0xFF);
                graphics.setFont(consoleFont);
                graphics.setColor(new Color((fc & 0x4) != 0 ? 255 : 0, 
                                            (fc & 0x2) != 0 ? 255 : 0, 
                                            (fc & 0x1) != 0 ? 255 : 0));
                graphics.drawString(c, x * WIDTH_RATIO + (WIDTH_RATIO/2 - graphics.getFontMetrics().stringWidth(c)/2), y * HEIGHT_RATIO + HEIGHT_RATIO - 5);                 
            }
        }
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, displayHeight - 60, displayWidth, 1);
        graphics.setFont(titleFont);
        graphics.drawString(DISPLAY_TITLE, displayWidth/2 - graphics.getFontMetrics().stringWidth(DISPLAY_TITLE)/2, displayHeight - 60 + graphics.getFontMetrics().getHeight());   
    }
    
    /**
     * Constructs a new {@link Display};
     * @param cpu The {@link Cpu} to create the display for.
     */
    public Display(Cpu cpu) {
        super("jDCPU Display");
        displayWidth = DISPLAY_WIDTH * WIDTH_RATIO;
        displayHeight = DISPLAY_HEIGHT * HEIGHT_RATIO + 60;
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
