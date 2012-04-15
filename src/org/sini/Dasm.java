package org.sini;

import java.io.IOException;
import java.io.InputStream;

/**
 * Dasm.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Dasm {
    
    /**
     * Mount the memory of a program onto the memory of the {@link Cpu}.
     * @param is The {@link InputStream} to get the code from to disassemble.
     */
    public String disassemble(InputStream is) throws IOException {
        int available = is.available();
        if(available % 2 != 0)
            throw new IOException();
        int[] memory = new int[available/2];
        int offset = 0;
        while((available -= 2) >= 0) {
            memory[offset++] = is.read() << 8 | is.read();
        }
        return disassemble(memory);
    }
    
    /**
     * Disassembles a programs instructions into an ASM code string.
     * @param memory The program memory.
     * @return The ASM code string.
     */
    public String disassemble(int[] memory) {
        String code = "";
        int position = 0;
        int tabulate = 0;
        while(position < memory.length) {
            int oldPosition = position;
            int opcodeValue = memory[position++];
            int insnOpcode = (opcodeValue & 0xF) != 0 ? opcodeValue & 0xF : opcodeValue & 0x3F0;
            int arguments = (insnOpcode & 0xF) == 0 ? 1 : 2;
            boolean doTabulate = insnOpcode >= Ops.OP_IFE && insnOpcode <= Ops.OP_IFB ? true : false;
            for(int tab = 0; tab < tabulate; tab++)
                code += "    ";
            code += Ops.OP_NAMES[insnOpcode] + " ";
            tabulate = tabulate < 1 ? 0 : --tabulate;
            for(int argument = 0; argument < arguments; argument++) {
                code += argument > 0 ? " " : "";
                int valueOpcode = (opcodeValue & 0xF) == 0 ? opcodeValue >> 10 : argument == 0 ? opcodeValue >> 4 & 0x3F : opcodeValue >> 10;
                String valueName = Ops.V_NAMES[valueOpcode];
                while(valueName.indexOf("%nw%") != -1) {
                    valueName = valueName.replaceFirst("%nw%", "0x" + Integer.toHexString(memory[position++]));
                }
                code += valueName;
            }
            tabulate = doTabulate ? tabulate + 1 : tabulate;
            code += " 0x" + Integer.toHexString(oldPosition) + (position < memory.length ? "\n" : "");
        }
        return code;
    }   
}
