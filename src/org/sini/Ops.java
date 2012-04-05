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
    public final static int OP_OR = 0xA;
    
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
    
}
