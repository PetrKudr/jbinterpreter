/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased;

/**
 *
 * @author petrk
 */
@FunctionalInterface
public interface EvalInterpreterListener {
    
    /**
     * Called when interpreter prints string or result of expression.
     * 
     * @param msg 
     */
    void onOut(String msg);
}
