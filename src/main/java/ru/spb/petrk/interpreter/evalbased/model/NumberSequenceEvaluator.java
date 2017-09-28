/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.spb.petrk.interpreter.evalbased.model;

import java.util.stream.BaseStream;

/**
 * Represents evaluator for a sequence of numbers.
 *
 * @author petrk
 */
public interface NumberSequenceEvaluator<S extends BaseStream> extends SequenceEvaluator<S> {
    
    IntSequenceEvaluator asIntSequence();
    
    FloatSequenceEvaluator asFloatSequence();
}
