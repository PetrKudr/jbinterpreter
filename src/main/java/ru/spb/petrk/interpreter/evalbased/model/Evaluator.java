/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import ru.spb.petrk.interpreter.evalbased.SymTab;

/**
 * Represents entity that can compute result from state.
 * 
 * Currently, symtab serves as a state. 
 *
 * @author petrk
 */
public interface Evaluator {
    
    /**
     * Creates evaluator which is bound to the given symtab.
     * 
     * That makes returned evaluator effectively constant.
     * 
     * Tip: Implementations should evaluate value immediately, because 
     * there is no point to defer evaluation further. Also, in case of deferred
     * evaluation there could be stack overflow issues if calculations are
     * chained.
     * 
     * @param st
     * @return binded evaluator
     */
    Evaluator bind(SymTab st);
    
    /**
     * Returns string representation of the evaluator with the given symtab.
     * 
     * @param st
     * @return 
     */
    String asString(SymTab st);
}
