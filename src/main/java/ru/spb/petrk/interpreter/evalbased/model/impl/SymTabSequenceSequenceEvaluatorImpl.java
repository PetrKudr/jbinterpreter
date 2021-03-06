/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceSequenceEvaluator;

/**
 * Sequence sequence evaluator that will be taken out of symtab in the future.
 *
 * @author petrk
 */
public final class SymTabSequenceSequenceEvaluatorImpl implements SequenceSequenceEvaluator {
    
    private final AtomicBoolean canceller;
    
    private final Function<SymTab, SequenceSequenceEvaluator> supplier;

    public SymTabSequenceSequenceEvaluatorImpl(AtomicBoolean canceller, Function<SymTab, SequenceSequenceEvaluator> supplier) {
        this.canceller = canceller;
        this.supplier = supplier;
    }

    @Override
    public SymTabSequenceSequenceEvaluatorImpl bind(SymTab st) {
        return new SymTabSequenceSequenceEvaluatorImpl(canceller, (any) -> supplier.apply(st).bind(st));
    }

    @Override
    public Stream<SequenceEvaluator> stream(SymTab symTab) {
        if (canceller.get()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(symTab).stream(symTab);
    }
}
