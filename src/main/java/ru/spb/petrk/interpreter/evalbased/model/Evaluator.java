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
    
    String asString(SymTab st);
}
