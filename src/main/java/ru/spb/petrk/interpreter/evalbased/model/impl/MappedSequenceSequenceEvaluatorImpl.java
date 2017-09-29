/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

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
    
    private final InSeq base;
    
    private final BiFunction<InSeq, SymTab, Stream<SequenceEvaluator>> supplier;

    public MappedSequenceSequenceEvaluatorImpl(InSeq base, BiFunction<InSeq, SymTab, Stream<SequenceEvaluator>> supplier) {
        this.base = base;
        this.supplier = supplier;
    }

    @Override
    public MappedSequenceSequenceEvaluatorImpl<InSeq> binded(SymTab st) {
        return new MappedSequenceSequenceEvaluatorImpl<>((InSeq) base.binded(st), supplier);
    }

    @Override
    public Stream<SequenceEvaluator> stream(SymTab symTab) {
        if (Thread.interrupted()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(base, symTab);
    }
}
