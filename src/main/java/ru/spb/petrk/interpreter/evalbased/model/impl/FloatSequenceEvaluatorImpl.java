/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;

/**
 *
 * @author petrk
 */
public final class FloatSequenceEvaluatorImpl implements FloatSequenceEvaluator {
    
    private final Function<SymTab, DoubleStream> supplier;

    public FloatSequenceEvaluatorImpl(Function<SymTab, DoubleStream> supplier) {
        this.supplier = supplier;
    }

    @Override
    public DoubleStream stream(SymTab symTab) {
        return supplier.apply(symTab);
    }

    @Override
    public IntSequenceEvaluator asIntSequence() {
        return new IntSequenceEvaluatorImpl((st) -> 
                stream(st).mapToInt(dblVal -> Double.valueOf(dblVal).intValue())
        );
    }
}
