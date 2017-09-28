/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model.impl;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import ru.spb.petrk.interpreter.evalbased.SymTab;
import ru.spb.petrk.interpreter.evalbased.model.SequenceEvaluator;
import ru.spb.petrk.interpreter.evalbased.model.SequenceSequenceEvaluator;

/**
 *
 * @author petrk
 */
public final class SequenceSequenceEvaluatorImpl implements SequenceSequenceEvaluator {
    
    private final Function<SymTab, Stream<SequenceEvaluator>> supplier;

    public SequenceSequenceEvaluatorImpl(Function<SymTab, Stream<SequenceEvaluator>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Stream<SequenceEvaluator> stream(SymTab symTab) {
        return supplier.apply(symTab);
    }
}
