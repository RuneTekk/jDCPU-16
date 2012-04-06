package org.sini;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Asm.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Asm {
    
    /**
     * Set a limit to the size of the program.
     */
    private static final int PROGRAM_SIZE = 0x1000;
    
    /**
     * Set a limit to the amount of labels.
     */
    private static final int LABELS_SIZE = 0x00FF;
    
    /**
     * Characters to remove from the code.
     */
    private static final String REMOVE_CHARS = "[^\nA-Za-z0-9\\[\\];:+ ]*";
    
    /**
     * Pattern to remove comments from the code.
     */
    private static final String REMOVE_COMMENT = ";[^\n]*";
    
    /**
     * Pattern to find a labels in the context in the code.
     */
    private static final Pattern FIND_LABEL = Pattern.compile(":([^ \n]+)");  
    
    /**
     * Pattern to find the address references in the code.
     */
    private static final Pattern FIND_ADDR = Pattern.compile("\\[([^\\]]+)");
    
    /**
     * Marks the origin of a label.
     */
    private final static char LABEL_ORIGIN = ':';
    
    /**
     * Marks a label reference.
     */
    private final static char LABEL_REF = '|';
    
    /**
     * Assembles the code into an instruction list.
     * @param code The code to assemble into instructions.
     */
    public int[] assemble(String code) {
        code = code.replaceAll(REMOVE_COMMENT + "|" + REMOVE_CHARS, "").toLowerCase();
        Matcher labelMatcher = FIND_LABEL.matcher(code);
        int labelOffset = 0;
        while(labelMatcher.find()) {
            if(labelOffset > LABELS_SIZE)
                throw new RuntimeException("Label overflow");
            String labelName = labelMatcher.group(1);
            code = code.replaceFirst(":" + labelName, "" + LABEL_ORIGIN + labelOffset);
            if(code.indexOf(":" + labelName) >= 0)
                throw new RuntimeException("Duplicate label: " + labelName);
            code = code.replaceAll(labelName, "" + LABEL_REF + labelOffset++);
        }
        Matcher addrMatcher = FIND_ADDR.matcher(code);
        while(addrMatcher.find()) {
            String address = addrMatcher.group(1);
            code = code.replace(address, address.replaceAll("[ ]", "_"));
        }
        int[] memory = new int[PROGRAM_SIZE];
        int[] labels = new int[LABELS_SIZE];
        int pc = 0;       
        String[] lines = code.split("[\n]");
        code = code.replaceAll("[\n]", "");
        for(int line = 0; line < lines.length; line++) {
            if(lines[line].equals(""))
                continue;
            String[] words = lines[line].split("[ ]");
            for(int word = 0; word < words.length; word++) {
                if(words[word].equals(""))
                    continue;
                if(words[word].charAt(0) == LABEL_ORIGIN) {
                    int offset = Integer.parseInt(words[word].substring(1, words[word].length()));
                    labels[offset] = pc;
                    continue;
                }
                int amountArguments = getAmountArguments(words[word]);
                if(amountArguments < 0)
                    throw new RuntimeException("Line " + (line + 1) + ", unknown op: " + words[word]);
                if(word + amountArguments >= words.length)
                    throw new RuntimeException("Line " + (line + 1) + ", missing arg: " + (word + amountArguments + 1 - words.length) + ", op: " + words[word]);               
                while(amountArguments-- > 0) { 
                    String wordValue = words[++word].replaceAll("_", " ");
                    int counterCost = getCounterCost(wordValue);
                    if(counterCost == -1)
                        throw new RuntimeException("Line " + (line + 1) + ", unknown value: " + wordValue);
                }
            }
        }
        return memory;
    }
    
    /**
     * Gets the amount of arguments for an operation.
     * @param op The operation to get the operations for.
     * @return The amount of arguments, -1 if the op is unregistered.
     */
    public int getAmountArguments(String op) {
        return op.equals("jsr") ? 1 : op.equals("set") || op.equals("add") || 
                                      op.equals("sub") || op.equals("mul") ||
                                      op.equals("div") || op.equals("mod") ||
                                      op.equals("shl") || op.equals("shr") ||
                                      op.equals("and") || op.equals("bor") ||
                                      op.equals("xor") || op.equals("ife") ||
                                      op.equals("ifn") || op.equals("ifg") ||
                                      op.equals("ifb") ? 2 : -1;
    }
    
    /**
     * Gets the counter cost for a value operation.
     * @param value The value operation to get the cost for.
     * @return The counter cost, -1 if the value is unrecognized.
     */
    public int getCounterCost(String value) {
        return value.equals("a") || value.equals("b") || value.equals("c") || 
               value.equals("x") || value.equals("y") || value.equals("z") || 
               value.equals("i") || value.equals("j") || value.equals("o") || 
               value.equals("cp") || value.equals("sp") || value.equals("pop") || 
               value.equals("peek") || value.equals("push") || 
               value.matches("\\[[a-cx-zij]\\]") ? 0 : 
               value.matches("0x[01]?[0-9a-f]+") || 
               value.matches("\\[0x[0-9a-f]{1,4}\\]") ||
               value.matches("\\[{2}0x[0-9a-f]{1,4}\\]{2}") ? 1 : -1;
    } 
}
