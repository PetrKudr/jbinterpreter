/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.Evaluator;
import ru.spb.petrk.interpreter.evalbased.model.FloatSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.IntSequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;

/**
 *
 * @author petrk
 */
public final class IntSequenceEvaluatorImpl implements IntSequenceEvaluator {
    
    private final Function<SymTab, IntStream> supplier;

    public IntSequenceEvaluatorImpl(Function<SymTab, IntStream> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IntStream stream(SymTab symTab) {
        return supplier.apply(symTab);
    }
    
    @Override
    public FloatSequenceEvaluator asFloatSequence() {
        return new FloatSequenceEvaluatorImpl((st) -> 
                stream(st).mapToDouble(intVal -> intVal)
        );
    }
}
