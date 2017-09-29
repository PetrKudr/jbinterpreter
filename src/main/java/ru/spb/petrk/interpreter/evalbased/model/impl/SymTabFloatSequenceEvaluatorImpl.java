/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;

/**
 * Float sequence evaluator that will be taken out of symtab in the future.
 *
 * @author petrk
 */
public class SymTabFloatSequenceEvaluatorImpl implements FloatSequenceEvaluator {
    
    private final AtomicBoolean canceller;
    
    private final Function<SymTab, FloatSequenceEvaluator> supplier;

    public SymTabFloatSequenceEvaluatorImpl(AtomicBoolean canceller, Function<SymTab, FloatSequenceEvaluator> supplier) {
        this.canceller = canceller;
        this.supplier = supplier;
    }

    @Override
    public SymTabFloatSequenceEvaluatorImpl bind(SymTab st) {
        return new SymTabFloatSequenceEvaluatorImpl(canceller, (any) -> supplier.apply(st).bind(st));
    }

    @Override
    public DoubleStream stream(SymTab symTab) {
        if (canceller.get()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(symTab).stream(symTab);
    }

    @Override
    public IntSequenceEvaluator asIntSequence() {
        return new MappedIntSequenceEvaluatorImpl<>(canceller, this, (baseEval, st) -> 
                baseEval.stream(st).mapToInt(dblVal -> Double.valueOf(dblVal).intValue())
        );
    }
}
