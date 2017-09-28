/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.astbased;

/**
 *
 * @author petrk
 */
@FunctionalInterface
public interface ASTInterpreterListener {
    
    /**
     * Called when interpreter prints string or result of expression.
     * 
     * @param msg 
     */
    void onOut(String msg);
}
