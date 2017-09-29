/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.function.ToDoubleFunction;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntEvaluator;

/**
 *
 * @author petrk
 */
public final class FloatEvaluatorImpl implements FloatEvaluator {
    
    private final ToDoubleFunction<SymTab> supplier;

    public FloatEvaluatorImpl(ToDoubleFunction<SymTab> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public FloatEvaluator binded(final SymTab st) {
        return new ConstFloatEvaluatorImpl(value(st));
    }

    @Override
    public double value(SymTab symTab) {
        if (Thread.interrupted()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.applyAsDouble(symTab);
    }
    
    @Override
    public IntEvaluator asInt() {
        return new IntEvaluatorImpl((SymTab symTab) -> Double.valueOf(this.value(symTab)).intValue());
    }

    @Override
    public String asString(SymTab st) {
        return String.valueOf(value(st));
    }
}
