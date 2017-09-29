/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

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
    
    private final IntEvaluator left;
    
    private final IntEvaluator right;

    public IntSequenceEvaluatorImpl(IntEvaluator left, IntEvaluator right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public IntSequenceEvaluator binded(SymTab st) {
        return new IntSequenceEvaluatorImpl(left.binded(st), right.binded(st));
    }

    @Override
    public IntStream stream(SymTab symTab) {
        if (Thread.interrupted()) {
            throw new EvalInterruptedInterpreterException();
        }
        return IntStream.rangeClosed(left.value(symTab), right.value(symTab));
    }
    
    @Override
    public FloatSequenceEvaluator asFloatSequence() {
        return new MappedFloatSequenceEvaluatorImpl<>(this, (base, st) -> 
                base.stream(st).mapToDouble(intVal -> intVal)
        );
    }
}
