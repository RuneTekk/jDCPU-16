package org.sini;

/**
 * Ops.java
 * @version 1.0.0
 */
public final class Ops {
    
    /**
     * Sets value a to value b.
     */
    public final static int OP_SET = 0x1;
    
    /**
     * Sets a to a plus b.
     */
    public final static int OP_ADD = 0x2;
    
    /**
     * Sets a to a subtracted from b.
     */
    public final static int OP_SUB = 0x3;
    
    /**
     * Sets a to a multiplied by b.
     */
    public final static int OP_MUL = 0x4;
    
    /**
     * Sets a to a divided by b, if b is 0 then a will be equal to 0.
     */
    public final static int OP_DIV = 0x5;   
    
    /**
     * Sets a to a modulus b, if b is 0 then a will be equal to 0.
     */
    public final static int OP_MOD = 0x6;
    
    /**
     * Sets a to a bit shifted left b.
     */
    public final static int OP_SHL = 0x7;
    
    /**
     * Sets a to a bit shifted right b.
     */
    public final static int OP_SHR = 0x8;
    
    
    /**
     * Sets a to a bitwise and b.
     */
    public final static int OP_AND = 0x9;
    
    /**
     * Sets a to a bitwise or b.
     */
    public final static int OP_BOR = 0xA;
    
    /**
     * Sets a to a bitwise xor b.
     */
    public final static int OP_XOR = 0xB;
       
    /**
     * Preforms next instruction if a equals b.
     */
    public final static int OP_IFE = 0xC;
    
    /**
     * SPreforms next instruction if a does not equal b.
     */
    public final static int OP_IFN = 0xD;
    
    /**
     * Preforms next instruction if a is greater than b.
     */
    public final static int OP_IFG = 0xE;
    
    /**
     * Preforms next instruction if a bitwise and b is not equal to 0.
     */
    public final static int OP_IFB = 0xF;
    
    /**
     * Preforms a jump to a sub routine.
     */
    public final static int OP_JSR = 0x01;
    
    /**
     * The listing of names for opcodes.
     */
    public final static String[] OP_NAMES;
    
    /**
     * The listing of names for values.
     */
    public final static String[] V_NAMES;
    
    static {
        OP_NAMES = new String[0x11];
        OP_NAMES[OP_SET] = "SET";
        OP_NAMES[OP_ADD] = "ADD";
        OP_NAMES[OP_SUB] = "SUB";
        OP_NAMES[OP_MUL] = "MUL";
        OP_NAMES[OP_DIV] = "DIV";
        OP_NAMES[OP_MOD] = "MOD";
        OP_NAMES[OP_SHL] = "SHL";
        OP_NAMES[OP_SHR] = "SHR";
        OP_NAMES[OP_AND] = "AND";
        OP_NAMES[OP_BOR] = "BOR";
        OP_NAMES[OP_XOR] = "XOR";
        OP_NAMES[OP_IFE] = "IFE";
        OP_NAMES[OP_IFN] = "IFN";
        OP_NAMES[OP_IFG] = "IFG";
        OP_NAMES[OP_IFB] = "IFB";
        OP_NAMES[OP_JSR << 4] = "JSR";
        V_NAMES = new String[0x40];
        V_NAMES[0x00] = "A";
        V_NAMES[0x01] = "B";
        V_NAMES[0x02] = "C";
        V_NAMES[0x03] = "X";
        V_NAMES[0x04] = "Y";
        V_NAMES[0x05] = "Z";
        V_NAMES[0x06] = "I";
        V_NAMES[0x07] = "J";
        V_NAMES[0x08] = "[A]";
        V_NAMES[0x09] = "[B]";
        V_NAMES[0x0A] = "[C]";
        V_NAMES[0x0B] = "[X]";
        V_NAMES[0x0C] = "[Y]";
        V_NAMES[0x0D] = "[Z]";
        V_NAMES[0x0E] = "[I]";
        V_NAMES[0x0F] = "[J]";
        V_NAMES[0x10] = "[%nw%+A]";
        V_NAMES[0x11] = "[%nw%+B]";
        V_NAMES[0x12] = "[%nw%+C]";
        V_NAMES[0x13] = "[%nw%+X]";
        V_NAMES[0x14] = "[%nw%+Y]";
        V_NAMES[0x15] = "[%nw%+Z]";
        V_NAMES[0x16] = "[%nw%+I]";
        V_NAMES[0x17] = "[%nw%+J]";
        V_NAMES[0x18] = "POP";
        V_NAMES[0x19] = "PEEK";
        V_NAMES[0x1A] = "PUSH";
        V_NAMES[0x1B] = "SP";
        V_NAMES[0x1C] = "PC";
        V_NAMES[0x1D] = "O";
        V_NAMES[0x1E] = "[%nw%]";
        V_NAMES[0x1F] = "%nw%";
        V_NAMES[0x20] = "0x00";
        V_NAMES[0x21] = "0x01";
        V_NAMES[0x22] = "0x02";
        V_NAMES[0x23] = "0x03";
        V_NAMES[0x24] = "0x04";
        V_NAMES[0x25] = "0x05";
        V_NAMES[0x26] = "0x06";
        V_NAMES[0x27] = "0x07";
        V_NAMES[0x28] = "0x08";
        V_NAMES[0x29] = "0x09";
        V_NAMES[0x2A] = "0x0A";
        V_NAMES[0x2B] = "0x0B";
        V_NAMES[0x2C] = "0x0C";
        V_NAMES[0x2D] = "0x0D";
        V_NAMES[0x2E] = "0x0E";
        V_NAMES[0x2F] = "0x0F";
        V_NAMES[0x30] = "0x10";
        V_NAMES[0x31] = "0x11";
        V_NAMES[0x32] = "0x12";
        V_NAMES[0x33] = "0x13";
        V_NAMES[0x34] = "0x14";
        V_NAMES[0x35] = "0x15";
        V_NAMES[0x36] = "0x16";
        V_NAMES[0x37] = "0x17";
        V_NAMES[0x38] = "0x18";
        V_NAMES[0x39] = "0x19";
        V_NAMES[0x3A] = "0x1A";
        V_NAMES[0x3B] = "0x1B";
        V_NAMES[0x3C] = "0x1C";
        V_NAMES[0x3D] = "0x1D";
        V_NAMES[0x3E] = "0x1E";
        V_NAMES[0x3F] = "0x1F";
    }
}
