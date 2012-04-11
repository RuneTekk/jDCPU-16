package org.sini;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import static org.sini.Ops.*;

/**
 * Cpu.java
 * @version 1.0.1
 */
public final class Cpu {
    
    /**
     * As of DCPU-16 Version 1.1 the amount of memory is 128 kilobytes. 
     */
    private static final int AMOUNT_MEMORY = 0x10000;
    
    /**
     * The starting index in the memory array of video ram.
     */
    public static final int VIDEO_RAM = 0x8000;
    
    /**
     * The amount of registers in the DCPU.
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
     * The cycle index in the registers array.
     */
    private static final int C = 11;
    
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
        for(int i = 0; i < m.length; i++)
            m[i] = new Cell(i);
        r = new Cell[AMOUNT_REGISTERS + 4];   
        for(int i = 0; i < r.length; i++)
            r[i] = new Cell(i);
        r[SP].v = 0xFFFF;
    }
    
    /**
     * Executes a program.
     * @param is The {@link InputStream} to mount the memory from.
     */
    public void execute(InputStream is) throws IOException {
        mount(is);
        execute();
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
        r[PC].v = 0;
        while(true) {
            int op = m[r[PC].v++].v;
            if((op & 0xF) != 0) {

                Object a = getValue(op >>> 4 & 0x3F, true);                                
                Object b = getValue(op >>> 10 & 0x3F, true);
                if(!(a instanceof Cell)) {
                    Object temp = a;
                    a = b;
                    b = temp;
                }   
                boolean bothLiteral = !(a instanceof Cell) && !(b instanceof Cell);
                int aValue = a instanceof Cell ? ((Cell) a).v : (Integer) a;
                int bValue = b instanceof Cell ? ((Cell) b).v : (Integer) b;
                switch(op & 0xF) {

                    case OP_SET:  
                        if(!bothLiteral)
                            ((Cell) a).v = bValue;
                        r[C].v++;
                        break;

                    case OP_ADD:  
                        if(!bothLiteral) {
                            int value = ((Cell) a).v + bValue;
                            if(value > 0xFFFF) {
                                r[O].v = 0x0001;
                                value &= 0xFFFF;
                            }
                            ((Cell) a).v = value;
                        }
                        r[C].v += 2;
                        break;

                    case OP_SUB:
                        if(!bothLiteral) {
                            int value = ((Cell) a).v - bValue;
                            if(value < 0) {
                                r[O].v = 0xFFFF;
                                value &= 0xFFFF;
                            }
                            ((Cell) a).v = value;
                        }
                        r[C].v += 2;
                        break;

                    case OP_MUL:
                        if(!bothLiteral) {
                            int value = ((Cell) a).v * bValue;
                            ((Cell) a).v = value & 0xFFFF;
                            r[O].v = value >>> 16;
                        }
                        r[C].v += 2;
                        break;

                    case OP_DIV:
                        if(!bothLiteral) {
                            if(bValue == 0) {
                                ((Cell) a).v = 0;
                                r[O].v = 0;
                            } else {
                                ((Cell) a).v = ((Cell) a).v/bValue & 0xFFFF;
                                r[O].v = (((Cell) a).v << 16)/bValue & 0xFFFF;
                            }
                        }
                        r[C].v += 3;
                        break;

                    case OP_MOD:
                        if(!bothLiteral) {
                            if(bValue == 0) {
                                ((Cell) a).v = 0;
                            } else
                                ((Cell) a).v = ((Cell) a).v % bValue;
                        }
                        r[C].v += 3;
                        break;

                    case OP_SHL:
                        if(!bothLiteral) {
                            int value = ((Cell) a).v >> bValue;
                            r[O].v = value >>> 16;
                            ((Cell) a).v = value & 0xFFFF;
                        }
                        r[C].v += 2;
                        break;

                    case OP_SHR:
                        if(!bothLiteral) {
                            ((Cell) a).v = ((Cell) a).v << bValue & 0xFFFF;
                            r[O].v = ((Cell) a).v << 16 >>> bValue & 0xFFFF;
                        }
                        r[C].v += 2;
                        break;

                    case OP_AND:
                        if(!bothLiteral)
                            ((Cell) a).v &= bValue;
                        r[C].v++;
                        break;

                    case OP_BOR:
                        if(!bothLiteral)
                            ((Cell) a).v |= bValue;
                        r[C].v++;
                        break;

                    case OP_XOR:
                        if(!bothLiteral)
                            ((Cell) a).v ^= bValue;
                        r[C].v++;
                        break;

                    case OP_IFE:
                        if(aValue != bValue) {
                            op = m[r[PC].v++].v;
                            getValue(op >>> 4 & 0x3F, false);
                            getValue(op >>> 10 & 0x3F, false);
                            r[C].v++;
                        }
                        r[C].v += 2;
                        break;

                    case OP_IFN:
                        if(aValue == bValue) {
                            op = m[r[PC].v++].v;
                            getValue(op >>> 4 & 0x3F, false);
                            getValue(op >>> 10 & 0x3F, false);
                            r[C].v++;
                        }
                        r[C].v += 2;
                        break;

                    case OP_IFG:
                        if(aValue <= bValue) {
                            op = m[r[PC].v++].v;
                            getValue(op >>> 4 & 0x3F, false);
                            getValue(op >>> 10 & 0x3F, false);
                            r[C].v++;
                        }
                        r[C].v += 2;
                        break;

                     case OP_IFB:
                        if((aValue & bValue) == 0) {
                            op = m[r[PC].v++].v;
                            getValue(op >>> 4 & 0x3F, false);
                            getValue(op >>> 10 & 0x3F, false);
                            r[C].v++;
                        }
                        r[C].v += 2;
                        break;
                }
            } else {
                op >>>= 4;
                Object a = getValue(op >>> 6, true);
                int aValue = a instanceof Cell ? ((Cell) a).v : (Integer) a;
                switch(op & 0x3F) {

                    case 0:
                        return;
                        
                    case OP_JSR:
                        Cell cell = m[--r[SP].v];
                        cell.v = r[PC].v;
                        r[PC].v = aValue;
                        r[C].v += 2;
                        break;
                }
            }
        }
    }
    
    /**
     * Gets the v from a v opcode.
     * @param op The opcode.
     * @param modify Modify the stack pointer.
     * @return The v from the opcode.
     */
    public Object getValue(int op, boolean modify) {
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
                r[C].v ++;
                return m[r[op - 0x10].v + m[r[PC].v++].v];
                
            case 0x18:
                return m[modify ? r[SP].v++ : r[SP].v];
                
            case 0x19:
                return m[r[SP].v];      
                
            case 0x1A:
                return m[modify ? --r[SP].v : r[SP].v];
            
            case 0x1B:
                return r[SP]; 
            
            case 0x1C:
                return r[PC];
                
            case 0x1D:
                return r[O];
                
            case 0x1E:
                r[C].v++;
                return m[m[r[PC].v++].v];
                
            case 0x1F:
                r[C].v++;
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
     * Mount the memory of a program onto the memory of the {@link Cpu}.
     * @param is The {@link InputStream} to mount the memory from.
     */
    public void mount(InputStream is) throws IOException {
        initialize();
        int available = is.available();
        if(available % 2 != 0)
            throw new IOException();
        int offset = 0;
        while((available -= 2) > 0) {
            m[offset++].v = is.read() << 8 | is.read();
        }
    }
    
    /**
     * Mount the memory of a program onto the memory of the {@link Cpu}.
     * @param memory The short array that represents the memory of the program.
     */
    public void mount(int[] memory) {
        initialize();
        for(int i = 0; i < memory.length; i++) 
            m[i].v = memory[i];
    }
    
    /**
     * The main entry point for this program.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            Cpu cpu = new Cpu();
            Asm asm = new Asm();
            cpu.execute(asm.assemble(new FileInputStream("./asm/sys.asm")));
        } catch(Exception ex) {
            System.err.println(ex);
        }
    }
}
