/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.interpreter.evalbased.SymTab;


/**
 * Represents evaluator for a float.
 *
 * @author petrk
 */
public interface FloatEvaluator extends NumberEvaluator {
    
    /**
     * Computes value using state.
     * 
     * @param symTab - state
     * @return double result
     */
    double value(SymTab symTab);
    
    @Override
    FloatEvaluator binded(SymTab st);

    @Override
    public default FloatEvaluator asFloat() {
        return this;
    }
}
