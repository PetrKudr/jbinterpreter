/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.interpreter.evalbased.SymTab;

/**
 * Represents evaluator for a string.
 *
 * @author petrk
 */
public interface StringEvaluator extends Evaluator {

    /**
     * Computes value using state.
     * 
     * @param symTab - state
     * @return string
     */
    String value(SymTab symTab);
}
