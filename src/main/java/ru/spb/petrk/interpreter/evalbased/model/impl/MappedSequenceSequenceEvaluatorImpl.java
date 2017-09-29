/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceSequenceEvaluator;

/**
 *
 * @author petrk
 */
public final class MappedSequenceSequenceEvaluatorImpl<InSeq extends SequenceEvaluator> implements SequenceSequenceEvaluator {
    
    private final AtomicBoolean canceller;
    
    private final InSeq base;
    
    private final BiFunction<InSeq, SymTab, Stream<SequenceEvaluator>> supplier;

    public MappedSequenceSequenceEvaluatorImpl(AtomicBoolean canceller, InSeq base, BiFunction<InSeq, SymTab, Stream<SequenceEvaluator>> supplier) {
        this.canceller = canceller;
        this.base = base;
        this.supplier = supplier;
    }

    @Override
    public MappedSequenceSequenceEvaluatorImpl<InSeq> bind(SymTab st) {
        return new MappedSequenceSequenceEvaluatorImpl<>(canceller, (InSeq) base.bind(st), supplier);
    }

    @Override
    public Stream<SequenceEvaluator> stream(SymTab symTab) {
        if (canceller.get()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(base, symTab);
    }
}
