/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.function.BiFunction;
import java.util.stream.DoubleStream;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;

/**
 *
 * @author petrk
 */
public final class MappedFloatSequenceEvaluatorImpl<InSeq extends SequenceEvaluator> implements FloatSequenceEvaluator {
    
    private final InSeq base;
    
    private final BiFunction<InSeq, SymTab, DoubleStream> supplier;

    public MappedFloatSequenceEvaluatorImpl(InSeq base, BiFunction<InSeq, SymTab, DoubleStream> supplier) {
        this.base = base;
        this.supplier = supplier;
    }

    @Override
    public MappedFloatSequenceEvaluatorImpl<InSeq> bind(SymTab st) {
        return new MappedFloatSequenceEvaluatorImpl<>((InSeq) base.bind(st), supplier);
    }

    @Override
    public DoubleStream stream(SymTab symTab) {
        if (Thread.interrupted()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(base, symTab);
    }

    @Override
    public IntSequenceEvaluator asIntSequence() {
        return new MappedIntSequenceEvaluatorImpl<>(this, (baseEval, st) -> 
                baseEval.stream(st).mapToInt(dblVal -> Double.valueOf(dblVal).intValue())
        );
    }
}
