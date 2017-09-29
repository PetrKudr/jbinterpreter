/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;
import ru.spb.petrk.interpreter.evalbased.model.VoidEvaluator;

/**
 *
 * @author petrk
 */
public final class VoidEvaluatorImpl implements VoidEvaluator {
    
    public static final VoidEvaluator INSTANCE = new VoidEvaluatorImpl();

    private VoidEvaluatorImpl() {}

    @Override
    public Evaluator binded(SymTab st) {
        return this;
    }

    @Override
    public String toString() {
        return "void";
    }
    
    @Override
    public String asString(SymTab st) {
        return "void";
    }
}
