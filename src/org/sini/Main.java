package org.sini;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Main.java
 * @version 1.0.0
 * @author RuneTekk Development (SiniSoul)
 */
public final class Main {
    
    /**
     * Prints the application tag.
     */
    private static void printTag() {
        System.out.println(""
        + "                     _____               _______   _    _                           "
        + "\n                    |  __ \\             |__   __| | |  | |                       "
        + "\n                    | |__) |   _ _ __   ___| | ___| | _| | __                     "
        + "\n                    |  _  / | | | '_ \\ / _ \\ |/ _ \\ |/ / |/ /                  "
        + "\n                    | | \\ \\ |_| | | | |  __/ |  __/   <|   <                    "
        + "\n                    |_|  \\_\\__,_|_| |_|\\___|_|\\___|_|\\_\\_|\\_\\             "
        + "\n----------------------------------------------------------------------------------"
        + "\n                                    jDCPU-16                                      "
        + "\n                               Created by SiniSoul                                "
        + "\n----------------------------------------------------------------------------------");
    }
    
    /**
     * The main entry point for the program.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        printTag();
        for(int i = 0; i < args.length;) {
            String argument = args[i++];
            if(!argument.startsWith("-"))
                throw new RuntimeException("Unmarked command argument: " + argument);
            argument = argument.substring(1);
            if(argument.equals("c") || argument.equals("compile")) {
                if(args.length - i < 2)
                    throw new RuntimeException("Usage: -c <source file> <destination file>...");
                InputStream is = null;               
                try {
                    is = new FileInputStream(args[i++]);
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while opening source stream: \n\t" + ex);
                }
                Asm asm = new Asm();
                int[] insns = null;
                try {
                    insns = asm.assemble(is);
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while compiling: \n\t" + ex);
                }
                OutputStream os = null;
                try {
                    os = new FileOutputStream(args[i++]);
                    for(int insn : insns) {                     
                        os.write(insn >> 8);
                        os.write(insn);
                    }
                    os.flush();
                    os.close();
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while writing the output file: \n\t" + ex);
                }
                System.out.println("Compiled " + insns.length + " instructions to " + args[i - 1] + "...");
            } else if(argument.equals("d") || argument.equals("disasm")) {
                if(args.length - i < 2)
                    throw new RuntimeException("Usage: -d <source file> <destination file>...");
                InputStream is = null;               
                try {
                    is = new FileInputStream(args[i++]);
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while opening source stream: \n\t" + ex);
                }
                Dasm asm = new Dasm();
                String insns = null;
                try {
                    insns = asm.disassemble(is);
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while disassembling: \n\t" + ex);
                }
                OutputStream os = null;
                try {
                    os = new FileOutputStream(args[i++]);
                    os.write(insns.getBytes());
                    os.flush();
                    os.close();
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while writing the output file: \n\t" + ex);
                }
                System.out.println("Disassembled instructions to " + args[i - 1] + "...");
            } else if(argument.equals("e") || argument.equals("execute")) {
                if(args.length - i < 1)
                    throw new RuntimeException("Usage: -e <source file>...");
                InputStream is = null;               
                try {
                    is = new FileInputStream(args[i++]);
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while opening source stream: \n\t" + ex);
                }
                boolean debug = false;
                if(args[i].startsWith("--"))
                    if(args[i++].equals("--d") || args[i - 1].equals("--debug"))
                        debug = true;
                    else 
                        throw new RuntimeException("Expected --d or --debug flag...");                 
                Cpu cpu = new Cpu();
                cpu.debug = debug;
                try {
                    cpu.execute(is);
                } catch(Exception ex) {
                    throw new RuntimeException("Exception thrown while executing: \n\t" + ex);                    
                }
                System.out.println("The program took a total of " + cpu.r[Cpu.C].v + " cycles...");
            } else
                throw new RuntimeException("Unknown command argument: " + argument);
        }
    }  
}
