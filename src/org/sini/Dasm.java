package org.sini;

/**
 * Dasm.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Dasm {
    
    /**
     * Disassembles a programs instructions into an ASM code string.
     * @param memory The program memory.
     * @return The ASM code string.
     */
    public String disassemble(int[] memory) {
        String code = "";
        int position = 0;
        boolean tabulate = false;
        while(position < memory.length) {
            int opcodeValue = memory[position++];
            int insnOpcode = (opcodeValue & 0xF) != 0 ? opcodeValue & 0xF : opcodeValue & 0x3F0;
            int arguments = (insnOpcode & 0xF) == 0 ? 1 : 2;
            boolean doTabulate = insnOpcode >= Ops.OP_IFE && insnOpcode <= Ops.OP_IFB ? true : false;
            code += (tabulate ? "    " : "") + Ops.OP_NAMES[insnOpcode] + " ";
            tabulate = false;
            for(int argument = 0; argument < arguments; argument++) {
                code += argument > 0 ? " " : "";
                int valueOpcode = (opcodeValue & 0xF) == 0 ? opcodeValue >> 10 : argument == 0 ? opcodeValue >> 4 & 0x3F : opcodeValue >> 10;
                String valueName = Ops.V_NAMES[valueOpcode];
                while(valueName.indexOf("%nw%") != -1) {
                    valueName = valueName.replaceFirst("%nw%", "0x" + Integer.toHexString(memory[position++]));
                }
                code += valueName;
            }
            tabulate = doTabulate;
            code += position < memory.length ? "\n" : "";
        }
        return code;
    }   
}
