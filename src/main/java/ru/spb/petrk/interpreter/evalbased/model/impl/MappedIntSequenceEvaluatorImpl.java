/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;

/**
 *
 * @author petrk
 */
public final class MappedIntSequenceEvaluatorImpl<InSeq extends SequenceEvaluator> implements IntSequenceEvaluator {
    
    private final AtomicBoolean canceller;
    
    private final InSeq base;
    
    private final BiFunction<InSeq, SymTab, IntStream> supplier;

    public MappedIntSequenceEvaluatorImpl(AtomicBoolean canceller, InSeq base, BiFunction<InSeq, SymTab, IntStream> supplier) {
        this.base = base;
        this.supplier = supplier;
        this.canceller = canceller;
    }

    @Override
    public MappedIntSequenceEvaluatorImpl<InSeq> bind(SymTab st) {
        return new MappedIntSequenceEvaluatorImpl<>(canceller, (InSeq) base.bind(st), supplier);
    }

    @Override
    public IntStream stream(SymTab symTab) {
        if (canceller.get()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(base, symTab);
    }
    
    @Override
    public FloatSequenceEvaluator asFloatSequence() {
        return new MappedFloatSequenceEvaluatorImpl<>(canceller, this, (baseEval, st) -> 
                baseEval.stream(st).mapToDouble(intVal -> intVal)
        );
    }
}
