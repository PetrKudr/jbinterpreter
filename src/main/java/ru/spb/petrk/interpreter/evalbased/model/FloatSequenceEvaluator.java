/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import java.util.stream.DoubleStream;
import ru.spb.petrk.interpreter.evalbased.SymTab;

/**
 * Represents evaluator for a sequence of floats.
 *
 * @author petrk
 */
public interface FloatSequenceEvaluator extends NumberSequenceEvaluator<DoubleStream> {

    @Override
    FloatSequenceEvaluator bind(SymTab st);
    
    @Override
    default FloatSequenceEvaluator asFloatSequence() {
        return this;
    }
}
