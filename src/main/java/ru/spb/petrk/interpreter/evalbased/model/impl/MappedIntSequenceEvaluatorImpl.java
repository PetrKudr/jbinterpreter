/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

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
    
    private final InSeq base;
    
    private final BiFunction<InSeq, SymTab, IntStream> supplier;

    public MappedIntSequenceEvaluatorImpl(InSeq base, BiFunction<InSeq, SymTab, IntStream> supplier) {
        this.base = base;
        this.supplier = supplier;
    }

    @Override
    public MappedIntSequenceEvaluatorImpl<InSeq> binded(SymTab st) {
        return new MappedIntSequenceEvaluatorImpl<>((InSeq) base.binded(st), supplier);
    }

    @Override
    public IntStream stream(SymTab symTab) {
        if (Thread.interrupted()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(base, symTab);
    }
    
    @Override
    public FloatSequenceEvaluator asFloatSequence() {
        return new MappedFloatSequenceEvaluatorImpl<>(this, (baseEval, st) -> 
                baseEval.stream(st).mapToDouble(intVal -> intVal)
        );
    }
}
