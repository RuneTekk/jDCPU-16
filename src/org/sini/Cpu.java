package org.sini;

import static org.sini.Ops.*;

/**
 * Cpu.java
 * @version 1.0.0
 */
public final class Cpu {
    
    /**
     * As of DCPU-16 Version 1.1 the amount of memory is 128 kilobytes. 
     */
    private static final int AMOUNT_MEMORY = 0x10000;
    
    /**
     * As of DCPU-16 Version 1.1 the amount of registers is 8; they are
     * (A, B, C, X, Y, Z, I, and J in that order)
     */
    private static final int AMOUNT_REGISTERS = 8;
    
    /**
     * The program counter index in the registers array.
     */
    private static final int PC = 8;
    
    /**
     * The stack pointer index in the registers array.
     */
    private static final int SP = 9;
    
    /**
     * The overflow index in the registers array.
     */
    private static final int O = 10;
    
    /**
     * The local memory of this {@link Cpu}.
     */
    Cell[] m;
    
    /**
     * The registers for the {@link Cpu}.
     */
    Cell[] r;
    
    /**
     * Initializes this {@link Cpu}.
     */
    public void initialize() {
        m = new Cell[AMOUNT_MEMORY];
        r = new Cell[AMOUNT_REGISTERS + 3];    
        r[SP].v = 0xFFFF;
    }
    
    /**
     * Executes a program.
     * @param memory The short array that represents the memory of the program.
     */
    public void execute(int[] memory) {
        mount(memory);
        execute();
    }
    
    /**
     * Executes the currently mounted program.
     */
    public void execute() {
        int op = m[r[PC].v++].v;
        switch(op & 0xF) {
            
            case 0x0:
                op >>= 4;               
                switch(op & 0x3F) {
                    
                    case 0:
                        return;
                }
                break;
            
            case OP_SET:
                
                break;
                
            case OP_ADD:
                
                break;
        }
    }
    
    /**
     * Gets the v from a v opcode.
     * @param op The opcode.
     * @return The v from the opcode.
     */
    public Object getValue(int op) {
        switch(op) {
            
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
                return r[op];
                
            case 0x08:
            case 0x09:
            case 0x0A:
            case 0x0B:
            case 0x0C:
            case 0x0D:
            case 0x0E:
            case 0x0F:
                return m[r[op - 0x08].v];
            
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            case 0x17:
                return m[r[op - 0x10].v + m[r[PC].v++].v];
                
            case 0x18:
                return m[r[SP].v++];
                
            case 0x19:
                return m[r[SP].v];      
                
            case 0x1A:
                return m[--r[SP].v];
            
            case 0x1B:
                return r[SP]; 
            
            case 0x1C:
                return r[PC];
                
            case 0x1D:
                return r[O];
                
            case 0x1E:
                return m[m[r[PC].v++].v];
                
            case 0x1F:
                return m[r[PC].v++];
                
            case 0x20:
            case 0x21:
            case 0x22:
            case 0x23:
            case 0x24:
            case 0x25:
            case 0x26:
            case 0x27:
            case 0x28:
            case 0x29:
            case 0x2A:
            case 0x2B:
            case 0x2C:
            case 0x2D:
            case 0x2E:
            case 0x2F:
            case 0x30:
            case 0x31:
            case 0x32:
            case 0x33:
            case 0x34:
            case 0x35:
            case 0x36:
            case 0x37:
            case 0x38:
            case 0x39:
            case 0x3A:
            case 0x3B:
            case 0x3C:
            case 0x3D:
            case 0x3E:
            case 0x3F:
                return op - 0x20;
                
            default:
                throw new RuntimeException("Unknown op in fetch: " + op);
                
        }
    }
    
    /**
     * Mount the memory of a program onto the memory of the Cpu.
     * @param memory The short array that represents the memory of the program.
     */
    public void mount(int[] memory) {
        initialize();
        for(int i = 0; i < memory.length; i++) 
            m[i].v = memory[i];
    }
}
