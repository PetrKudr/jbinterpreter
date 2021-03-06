/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.IntStream;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;

/**
 * Int sequence evaluator that will be taken out of symtab in the future.
 *
 * @author petrk
 */
public final class SymTabIntSequenceEvaluatorImpl implements IntSequenceEvaluator {
    
    private final AtomicBoolean canceller;
    
    private final Function<SymTab, IntSequenceEvaluator> supplier;
    
    public SymTabIntSequenceEvaluatorImpl(AtomicBoolean canceller, Function<SymTab, IntSequenceEvaluator> supplier) {
        this.canceller = canceller;
        this.supplier = supplier;
    }

    @Override
    public IntSequenceEvaluator bind(SymTab symTab) {
        return new SymTabIntSequenceEvaluatorImpl(canceller, (any) -> supplier.apply(symTab).bind(symTab));
    }

    @Override
    public IntStream stream(SymTab st) {
        if (canceller.get()) {
            throw new EvalInterruptedInterpreterException();
        }
        return supplier.apply(st).stream(st);
    }
    
    @Override
    public FloatSequenceEvaluator asFloatSequence() {
        return new MappedFloatSequenceEvaluatorImpl<>(canceller, this, (baseEval, st) -> 
                baseEval.stream(st).mapToDouble(intVal -> intVal)
        );
    }
}
