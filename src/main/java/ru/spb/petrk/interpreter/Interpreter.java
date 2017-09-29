/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter;

import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * @param out print stream
     * @param err print stream
     * @param canceller can be used to interrupt interpretation
     * @return true if no errors occurred, false otherwise
     */
    boolean interpret(String input, PrintStream out, PrintStream err, AtomicBoolean canceller);
    
    /**
     * Interprets input.
     * 
     * Treats input as a complete program. Results of previous 
     * interpretation does not interfere with the current one (variables, 
     * declared in previous interpretation are not visible).
     * 
     * @param input - code of a program
     * @param listener listens output and error events
     * @param canceller can be used to interrupt interpretation
     * @return true if no errors occurred, false otherwise
     */
    boolean interpret(String input, InterpreterListener listener, AtomicBoolean canceller);
}