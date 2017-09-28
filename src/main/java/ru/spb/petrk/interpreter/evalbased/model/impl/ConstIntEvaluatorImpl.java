/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntEvaluator;

/**
 *
 * @author petrk
 */
public final class ConstIntEvaluatorImpl implements IntEvaluator {
    
    private final int value;

    public ConstIntEvaluatorImpl(int value) {
        this.value = value;
    }

    @Override
    public int value(SymTab symTab) {
        return value;
    }

    @Override
    public FloatEvaluator asFloat() {
        return new ConstFloatEvaluatorImpl(value);
    }

    @Override
    public String asString(SymTab st) {
        return String.valueOf(value);
    }
}
