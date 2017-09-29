/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import ru.spb.petrk.interpreter.evalbased.EvalInterruptedInterpreterException;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;

/**
 *
 * @author petrk
 */
public final class IntSequenceEvaluatorImpl implements IntSequenceEvaluator {
    
    private final AtomicBoolean canceller;
    
    private final IntEvaluator left;
    
    private final IntEvaluator right;

    public IntSequenceEvaluatorImpl(AtomicBoolean canceller, IntEvaluator left, IntEvaluator right) {
        this.canceller = canceller;
        this.left = left;
        this.right = right;
    }

    @Override
    public IntSequenceEvaluator bind(SymTab st) {
        return new IntSequenceEvaluatorImpl(canceller, left.bind(st), right.bind(st));
    }

    @Override
    public IntStream stream(SymTab symTab) {
        if (canceller.get()) {
            throw new EvalInterruptedInterpreterException();
        }
        return IntStream.rangeClosed(left.value(symTab), right.value(symTab));
    }
    
    @Override
    public FloatSequenceEvaluator asFloatSequence() {
        return new MappedFloatSequenceEvaluatorImpl<>(canceller, this, (base, st) -> 
                base.stream(st).mapToDouble(intVal -> intVal)
        );
    }
}
