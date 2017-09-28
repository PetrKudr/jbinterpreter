/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.interpreter.evalbased.SymTab;

/**
 * Represents evaluator for an integer.
 *
 * @author petrk
 */
public interface IntEvaluator extends NumberEvaluator {

    /**
     * Computes value using state.
     * 
     * @param symTab - state
     * @return int result
     */
    int value(SymTab symTab);

    @Override
    public default IntEvaluator asInt() {
        return this;
    }
}
