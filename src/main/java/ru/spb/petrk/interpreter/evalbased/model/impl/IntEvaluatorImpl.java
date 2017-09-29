/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.function.ToIntFunction;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntEvaluator;

/**
 *
 * @author petrk
 */
public final class IntEvaluatorImpl implements IntEvaluator {
    
    private final ToIntFunction<SymTab> supplier;

    public IntEvaluatorImpl(ToIntFunction<SymTab> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IntEvaluator bind(final SymTab st) {
        return new ConstIntEvaluatorImpl(value(st));
    }

    @Override
    public int value(SymTab symTab) {
        if (Thread.interrupted()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.applyAsInt(symTab);
    }
    
    @Override
    public FloatEvaluator asFloat() {
        return new FloatEvaluatorImpl((SymTab symTab) -> this.value(symTab));
    }
    
    @Override
    public String asString(SymTab st) {
        return String.valueOf(value(st));
    }
}
