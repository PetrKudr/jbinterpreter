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
     * Interprets input
     * 
     * @param input - code
     * @param out - out print stream
     * @param err - err print stream
     * @return true if no errors occured, false otherwise
     */
    boolean interpret(String input, PrintStream out, PrintStream err);
}