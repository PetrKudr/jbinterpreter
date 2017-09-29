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
public final class ConstFloatEvaluatorImpl implements FloatEvaluator {
    
    private final double value;

    public ConstFloatEvaluatorImpl(double value) {
        this.value = value;
    }

    @Override
    public FloatEvaluator binded(SymTab st) {
        return this;
    }

    @Override
    public double value(SymTab symTab) {
        return value;
    }

    @Override
    public IntEvaluator asInt() {
        return new ConstIntEvaluatorImpl(Double.valueOf(value).intValue());
    }

    @Override
    public String asString(SymTab st) {
        return String.valueOf(value);
    }
}
