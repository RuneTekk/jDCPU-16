package org.sini;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Asm.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Asm {
    
    /**
     * The maximum amount of instructions allowed to compile.
     */
    private static final int MAXIMUM_INSTRUCTIONS = 0x1000;
    
    /**
     * The maximum amount of labels allowed to compile.
     */
    private static final int MAXIMUM_LABELS = 0xFFFF;
    
    /**
     * The pattern to find hexadecimal numbers in the code with.
     */
    private final static Pattern HEXADECIMAL_PATTERN = Pattern.compile("0x([^\\]\n+ ]*)");
    
    /**
     * The pattern to find decimal numbers in the code with.
     */
    private final static Pattern DECIMAL_PATTERN = Pattern.compile("\\d+");
    
    /**
     * The pattern to find instructions in the code with.
     */
    private static Pattern INSTRUCTION_PATTERN = Pattern.compile("[^\n ]+");
    
    /**
     * The pattern to find labels in the code with.
     */
    private static Pattern LABEL_PATTERN = Pattern.compile(":([^\n ]*)");
    
    /**
     * The pattern to find marked labels in the code with.
     */
    private static Pattern MARKER_PATTERN = Pattern.compile("#(\\d*)#");
    
    /**
     * Gets the line number from the index of a character.
     * @param str The string to get the line numbers from.
     * @param maxIndex The maximum index in the string.
     * @return The line number.
     */
    private static int getLineNumber(String str, int maxIndex) {
        int lineNumber = 1;
        int curIndex = -1;
        while((curIndex = str.indexOf('\n', curIndex + 1)) < maxIndex && curIndex != -1) {
            lineNumber++;
        }
        return lineNumber;
    }
    
    /**
     * Looks up an operator in the operators array.
     * @param str The string to lookup.
     * @return The opcode of the operator.
     */
    private static int lookupInstruction(String str) {
        for(int i = 0; i < Ops.OP_NAMES.length; i++)
            if(Ops.OP_NAMES[i] != null && Ops.OP_NAMES[i].toLowerCase().equals(str))
                return i;
        return -1;
    }
    
    /**
     * Looks up an operator in the operators array.
     * @param str The string to lookup.
     * @param The value is represented as a label.
     * @return The opcode of the operator.
     */
    private static int lookupValue(String str, boolean isLabel) {
        Matcher matcher = DECIMAL_PATTERN.matcher(str);
        if(matcher.find()) {
            int value = Integer.parseInt(matcher.group());
            if(str.matches("^" + DECIMAL_PATTERN.pattern() + "$") && value >= 0x0 && value <= 0x1F && !isLabel)
                return value + 0x20;
            else
                str = matcher.replaceAll("%nw%");
        }       
        for(int i = 0; i < Ops.V_NAMES.length; i++)
            if(Ops.V_NAMES[i] != null && Ops.V_NAMES[i].toLowerCase().equals(str))
                return i;
        return -1;
    }
    
    /**
     * Assembles the code into an instruction list.
     * @param is The {@link InputStream} to read from.
     * @return The assembled instructions.
     */
    public int[] assemble(InputStream is) throws IOException {
        String str = "";
        int read;
        while((read = is.read()) != -1) {
            str += (char) read;
        }
        return assemble(str);
    }
    
    /**
     * Assembles the code into an instruction list.
     * @param code The code to assemble into instructions.
     * @return The assembled instructions.
     */
    public int[] assemble(String code) {      
        code = code.toLowerCase().replaceAll("[^\\w\\[\\]+\n;: ]", "").replaceAll(";[^\n]*", "");
        Matcher matcher = LABEL_PATTERN.matcher(code);
        int labelOffset = 0;
        while(matcher.find()) {
            if(labelOffset > MAXIMUM_LABELS)
                throw new RuntimeException("Label overflow");
            String name = matcher.group(1);
            if(name.length() < 4)
                throw new RuntimeException("Illegal label, " + '\'' + name + '\'' + ", on line " + getLineNumber(code, matcher.start()));
            code = code.replaceFirst(matcher.group(), ":" + labelOffset);
            if(code.matches(matcher.group() + " "))
                throw new RuntimeException("Duplicate label, " + '\'' + name + '\'' + ", on line " + getLineNumber(code, code.indexOf(matcher.group())));
            Matcher referenceMatcher = Pattern.compile(name + "[\n ]").matcher(code);
            while(referenceMatcher.find()) {
                code = code.substring(0, referenceMatcher.start()) + "#" + labelOffset++ + "#" + code.substring(referenceMatcher.end() - 1);
            }
        }       
        while((matcher = HEXADECIMAL_PATTERN.matcher(code)).find()) {          
            String value = matcher.group(1);
            try {      
                code = code.substring(0, matcher.start()) + Integer.valueOf(value, 16) + code.substring(matcher.end());              
            } catch(Exception ex) {
                throw new RuntimeException("Invalid hexidecimal literal, " + '\'' + matcher.group() + '\'' + ", on line " + getLineNumber(code, matcher.start(1)));
            }
        }
        int[] labels = new int[MAXIMUM_LABELS];
        int counter = 0;
        matcher = INSTRUCTION_PATTERN.matcher(code);
        while(matcher.find()) {
            if(matcher.group().startsWith(":")) {
                int labelId = Integer.parseInt(matcher.group().substring(1));
                labels[labelId] = counter;
                continue;
            }
            int insnOpcode = lookupInstruction(matcher.group());
            if(insnOpcode < 0)
                throw new RuntimeException("Unknown instruction, " + '\'' + matcher.group() + '\'' + ", on line " + getLineNumber(code, matcher.start(0)));
            counter++;
            if(counter >= MAXIMUM_INSTRUCTIONS)
                throw new RuntimeException("Instruction overflow");
            int arguments = (insnOpcode & 0xF) == 0 ? 1 : 2;
            for(int argument = 0; argument < arguments; argument++) {
                if(!matcher.find())
                    throw new RuntimeException("Expected argument " + (argument + 1) + " after " + Ops.OP_NAMES[insnOpcode] + " on line " + getLineNumber(code, code.length()));
                String matcherValue = matcher.group();
                Matcher markerMatcher = MARKER_PATTERN.matcher(matcherValue);
                boolean isLabel = false;
                if((isLabel = markerMatcher.find())) {
                    counter++;
                    continue;
                }
                int valueOpcode = lookupValue(matcherValue, isLabel);
                if(valueOpcode < 0)
                    throw new RuntimeException("Unknown value, " + '\'' + matcher.group() + '\'' + ", on line " + getLineNumber(code, matcher.start(0)));                
                if(Ops.V_NAMES[valueOpcode].indexOf("%nw%") > -1) {
                    Matcher decimalMatcher = DECIMAL_PATTERN.matcher(matcher.group());
                    if(!decimalMatcher.find())
                        throw new RuntimeException("PANIC, Logic Error!");
                    int decimalValue = Integer.parseInt(decimalMatcher.group());
                    if(decimalValue > 65535 || decimalValue < 0)
                        throw new RuntimeException("Undetermined overflow error of uncertainty, " + '\'' + "0x" + Integer.toHexString(decimalValue) + '\'' + ", on line " + getLineNumber(code, decimalMatcher.start(0)));
                    counter++;
                }
            }
        }
        code = code.replaceAll(":[^\n ]+", "");
        int[] instructions = new int[MAXIMUM_INSTRUCTIONS];
        counter = 0;
        matcher = INSTRUCTION_PATTERN.matcher(code);
        while(matcher.find()) {
            int insnOpcode = lookupInstruction(matcher.group());          
            int currentCounter = counter++;
            int arguments = (insnOpcode & 0xF) == 0 ? 1 : 2;
            for(int argument = 0; argument < arguments; argument++) { 
                matcher.find();
                String matcherValue = matcher.group();
                Matcher markerMatcher = MARKER_PATTERN.matcher(matcherValue);
                boolean isLabel = false;
                if((isLabel = markerMatcher.find()))
                    matcherValue = markerMatcher.replaceAll("" + labels[Integer.parseInt(markerMatcher.group(1))]);
                int valueOpcode = lookupValue(matcherValue, isLabel);               
                if(Ops.V_NAMES[valueOpcode].indexOf("%nw%") > -1) {
                    Matcher decimalMatcher = DECIMAL_PATTERN.matcher(matcherValue);
                    decimalMatcher.find();
                    int decimalValue = Integer.parseInt(decimalMatcher.group());                  
                    instructions[counter++] = decimalValue;
                }
                insnOpcode |= (insnOpcode & 0xF) == 0 ? valueOpcode << 10 : valueOpcode << (argument == 0 ? 4 : 10);
            }
            instructions[currentCounter] = insnOpcode;
        }
        int[] programInstructions = new int[counter];
        System.arraycopy(instructions, 0, programInstructions, 0, programInstructions.length);
        return programInstructions;
    }
}