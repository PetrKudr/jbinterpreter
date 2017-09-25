/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import java.io.PrintStream;

/**
 *
 * @author petrk
 */
public interface Interpreter {
    
    /**
     * Interprets input.
     * 
     * Treats input as a complete program. Results of previous 
     * interpretation does not interfere with the current one (variables, 
     * declared in previous interpretation are not visible).
     * 
     * @param input - code of a program
     * @param out - out print stream
     * @param err - err print stream
     * @return true if no errors occured, false otherwise
     */
    boolean interpret(String input, PrintStream out, PrintStream err);
    
    /**
     * Interprets input.
     * 
     * Treats input as a complete program. Results of previous 
     * interpretation does not interfere with the current one (variables, 
     * declared in previous interpretation are not visible).
     * 
     * @param input - code of a program
     * @param listener
     * @return true if no errors occured, false otherwise
     */
    boolean interpret(String input, InterpreterListener listener);
}